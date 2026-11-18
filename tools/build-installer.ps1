# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/build-installer.ps1
# 📌 Amac: TurkuazInstaller icin Windows payload ve native Setup EXE uretir.
# 📌 Tool - PowerShell
# Version: 1.7.0
# Aciklama: SHA-256 dogrulamali kalici dependency cache, Windows ikonlari, JRE/JDBC/SLF4J/FlatLaf payloadi ve Rust TurkuazInstaller EXE uretir.
# Bagimli Oldugu Katman: Tool | Config

param(
    [string]$ExpectedTag = '',
    [string]$DistPath = 'dist',
    [string]$StagePath = 'dist-installer',
    [string]$CachePath = 'cache\installer'
)

$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$dist = Join-Path $root $DistPath
$stage = Join-Path $root $StagePath
$payload = Join-Path $stage 'payload'
$launcherConfig = Join-Path $root 'config\launcher.yml'
$versionConfig = Join-Path $root 'config\version.yml'
$installerCommit = '10101dee8b6efe4775f5cf72aa05f3a3da934bfa'
. (Join-Path $PSScriptRoot 'windows-branding.ps1')

function Read-SimpleYaml([string]$Path) {
    $result = @{}
    foreach ($line in Get-Content -LiteralPath $Path) {
        if ($line -match '^\s*([A-Za-z0-9_]+)\s*:\s*["'']?(.*?)["'']?\s*$') {
            $result[$matches[1]] = $matches[2].Trim().Trim('"').Trim("'")
        }
    }
    return $result
}

function Assert-Sha256([string]$Path, [string]$Expected) {
    $actual = (Get-FileHash -LiteralPath $Path -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actual -ne $Expected.ToLowerInvariant()) {
        throw "SHA-256 uyusmuyor: $Path"
    }
}

function Ensure-Directory([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path)) {
        New-Item -ItemType Directory -Path $Path -Force | Out-Null
    }
}

function Download-File([string]$Url, [string]$Destination) {
    Ensure-Directory (Split-Path -Parent $Destination)
    Invoke-WebRequest -Uri $Url -OutFile $Destination -UseBasicParsing
}

function Get-VerifiedCachedFile([string]$Url, [string]$Destination, [string]$ExpectedSha256) {
    Ensure-Directory (Split-Path -Parent $Destination)

    if (Test-Path -LiteralPath $Destination) {
        $actual = (Get-FileHash -LiteralPath $Destination -Algorithm SHA256).Hash.ToLowerInvariant()
        if ($actual -eq $ExpectedSha256.ToLowerInvariant()) {
            Write-Host "[CACHE] Kullaniliyor: $Destination"
            return
        }

        Write-Host "[CACHE] SHA uyusmadi, yeniden indiriliyor: $Destination"
        Remove-Item -LiteralPath $Destination -Force
    }

    Download-File $Url $Destination
    Assert-Sha256 $Destination $ExpectedSha256
    Write-Host "[CACHE] Dogrulandi: $Destination"
}


$version = Read-SimpleYaml $versionConfig
$appVersion = $version['app_version']
if (-not $appVersion) { throw 'app_version bulunamadi.' }
if ($ExpectedTag -and $ExpectedTag -ne "v$appVersion") {
    throw "Tag/surum uyusmuyor: $ExpectedTag != v$appVersion"
}

$exe = Join-Path $dist 'TelefonRehberi.exe'
$jar = Join-Path $dist 'TelefonRehberi.jar'
if (-not (Test-Path -LiteralPath $exe) -or -not (Test-Path -LiteralPath $jar)) {
    & (Join-Path $PSScriptRoot 'build-release.ps1') -ExpectedTag $ExpectedTag
}

Remove-Item -LiteralPath $stage -Recurse -Force -ErrorAction SilentlyContinue
Ensure-Directory $payload
foreach ($dir in @('app','assets\branding','config','lib','runtime')) { Ensure-Directory (Join-Path $payload $dir) }

Copy-Item -LiteralPath $exe -Destination (Join-Path $payload 'TelefonRehberi.exe') -Force
Copy-Item -LiteralPath $jar -Destination (Join-Path $payload 'app\TelefonRehberi.jar') -Force
foreach ($docName in @('README.md','LICENSE','SECURITY.md','THIRD_PARTY_NOTICES.md')) {
    $docSource = Join-Path $root $docName
    if (Test-Path -LiteralPath $docSource) { Copy-Item -LiteralPath $docSource -Destination (Join-Path $payload $docName) -Force }
}
Copy-Item -Path (Join-Path $root 'assets\branding\*') -Destination (Join-Path $payload 'assets\branding') -Recurse -Force
$installedBranding = Join-Path $payload 'assets\branding'
Write-TurkuazWindowsIconSet -SourcePng (Join-Path $installedBranding 'app-icon-512.png') -DestinationDirectory $installedBranding
$versionedShellIcon = Join-Path $installedBranding ("app-icon-v{0}.ico" -f $appVersion)
Copy-Item -LiteralPath (Join-Path $installedBranding 'app-icon.ico') -Destination $versionedShellIcon -Force
foreach ($name in @('app.yml','launcher.yml','state.yml','version.yml')) {
    Copy-Item -LiteralPath (Join-Path $root "config\$name") -Destination (Join-Path $payload "config\$name") -Force
}
Copy-Item -LiteralPath (Join-Path $root 'packaging\windows\installed.mode') -Destination (Join-Path $payload 'config\installed.mode') -Force

# Installed surum GitHub manifestinden Setup EXE indirerek guncellenir; update_enabled acik kalir.
$installedLauncherConfig = Join-Path $payload 'config\launcher.yml'
(Get-Content -LiteralPath $installedLauncherConfig) -replace '^update_enabled:\s*.*$', 'update_enabled: "true"' |
    Set-Content -LiteralPath $installedLauncherConfig -Encoding UTF8

$config = Read-SimpleYaml $launcherConfig
$cache = if ([IO.Path]::IsPathRooted($CachePath)) {
    [IO.Path]::GetFullPath($CachePath)
} else {
    [IO.Path]::GetFullPath((Join-Path $root $CachePath))
}
Ensure-Directory $cache

$sqliteVersion = $config['sqlite_jdbc_version']
$sqliteUrl = $config['sqlite_jdbc_url'].Replace('{version}', $sqliteVersion)
$sqliteTarget = Join-Path $payload ($config['sqlite_jdbc_path'].Replace('/', '\'))
$sqliteDownload = Join-Path $cache 'sqlite-jdbc.jar'
Get-VerifiedCachedFile $sqliteUrl $sqliteDownload $config['sqlite_jdbc_sha256']
Ensure-Directory (Split-Path -Parent $sqliteTarget)
Copy-Item -LiteralPath $sqliteDownload -Destination $sqliteTarget -Force

$slf4jVersion = $config['slf4j_version']
$slf4jUrl = $config['slf4j_api_url'].Replace('{version}', $slf4jVersion)
$slf4jRelative = $config['slf4j_api_path'].Replace('{version}', $slf4jVersion).Replace('/', '\')
$slf4jTarget = Join-Path $payload $slf4jRelative
$slf4jDownload = Join-Path $cache 'slf4j-api.jar'
Get-VerifiedCachedFile $slf4jUrl $slf4jDownload $config['slf4j_api_sha256']
Ensure-Directory (Split-Path -Parent $slf4jTarget)
Copy-Item -LiteralPath $slf4jDownload -Destination $slf4jTarget -Force

$flatVersion = $config['flatlaf_version']
$flatUrl = $config['flatlaf_url'].Replace('{version}', $flatVersion)
$flatShaUrl = $config['flatlaf_sha256_url'].Replace('{version}', $flatVersion)
$flatRelative = $config['flatlaf_path'].Replace('{version}', $flatVersion).Replace('/', '\')
$flatTarget = Join-Path $payload $flatRelative
$flatDownload = Join-Path $cache 'flatlaf.jar'
$flatShaRaw = (Invoke-WebRequest -Uri $flatShaUrl -UseBasicParsing).Content
$flatSha = (($flatShaRaw -split '\s+')[0]).Trim().ToLowerInvariant()
Get-VerifiedCachedFile $flatUrl $flatDownload $flatSha
Ensure-Directory (Split-Path -Parent $flatTarget)
Copy-Item -LiteralPath $flatDownload -Destination $flatTarget -Force
Set-Content -LiteralPath ($flatTarget + '.sha256') -Value $flatSha -Encoding ASCII

$major = $config['java_major']
$arch = if ($config['java_architecture'] -eq 'auto') { 'x64' } else { $config['java_architecture'] }
$image = $config['java_image_type']
$vendor = $config['java_vendor']
$api = $config['adoptium_assets_url'].Replace('{major}', $major).Replace('{arch}', $arch).Replace('{image}', $image).Replace('{vendor}', $vendor).Replace('{os}', 'windows')
$assets = Invoke-RestMethod -Uri $api -UseBasicParsing
if (-not $assets -or -not $assets[0].binary.package.link -or -not $assets[0].binary.package.checksum) {
    throw 'Adoptium Windows JRE paketi bulunamadi.'
}
$jreArchive = Join-Path $cache 'temurin-jre.zip'
Get-VerifiedCachedFile $assets[0].binary.package.link $jreArchive $assets[0].binary.package.checksum
$jreExtract = Join-Path $stage 'jre-extract'
Expand-Archive -LiteralPath $jreArchive -DestinationPath $jreExtract -Force
$javaw = Get-ChildItem -Path $jreExtract -Filter 'javaw.exe' -Recurse -File | Select-Object -First 1
if (-not $javaw) { throw 'Temurin paketinde javaw.exe bulunamadi.' }
$jreHome = Split-Path -Parent (Split-Path -Parent $javaw.FullName)
$jreTarget = Join-Path $payload ($config['java_runtime_dir'].Replace('/', '\'))
Ensure-Directory (Split-Path -Parent $jreTarget)
Copy-Item -LiteralPath $jreHome -Destination $jreTarget -Recurse -Force

# @dev-id TR-TI-SETUP-001
# Pinned, independently verified TurkuazInstaller source; no Inno/NSIS.
$engineSource = Join-Path $stage 'turkuaz-installer-source'
& git clone --no-checkout 'https://github.com/TurkuazLabs/TurkuazInstaller.git' $engineSource
if ($LASTEXITCODE -ne 0) { throw 'TurkuazInstaller clone basarisiz.' }
& git -C $engineSource checkout --detach $installerCommit
if ($LASTEXITCODE -ne 0) { throw 'TurkuazInstaller pinned checkout basarisiz.' }
$actualCommit = (& git -C $engineSource rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0 -or $actualCommit -cne $installerCommit) {
    throw 'TurkuazInstaller kaynak commit kimligi eslesmiyor.'
}
$engineManifest = Join-Path $engineSource 'rust/Cargo.toml'
& cargo build --locked --manifest-path $engineManifest -p turkuaz-installer-cli --release --bin turkuaz-installer --features setup-authoring-gui
if ($LASTEXITCODE -ne 0) { throw 'TurkuazInstaller engine build basarisiz.' }
$cli = Join-Path $engineSource 'rust/target/release/turkuaz-installer.exe'
if (-not (Test-Path -LiteralPath $cli -PathType Leaf)) { throw 'TurkuazInstaller engine bulunamadi.' }
Ensure-Directory $dist
$package = Join-Path $dist "TelefonRehberi-v$appVersion.tzpkg"
$setup = Join-Path $dist "TelefonRehberi-Setup-v$appVersion.exe"
& $cli build-package --package-id com.turkuazlabs.telefonrehberi --version $appVersion --source $payload --output $package --executable TelefonRehberi.exe
if ($LASTEXITCODE -ne 0) { throw 'TelefonRehberi TurkuazInstaller paket build basarisiz.' }
& $cli build-native-setup --stub $cli --package $package --output $setup
if ($LASTEXITCODE -ne 0) { throw 'TurkuazInstaller native setup build basarisiz.' }
if (-not (Test-Path -LiteralPath $setup -PathType Leaf)) { throw "Setup EXE uretilmedi: $setup" }

$setupSha = (Get-FileHash -LiteralPath $setup -Algorithm SHA256).Hash.ToLowerInvariant()
$manifestPath = Join-Path $dist 'update-manifest.yml'
if (Test-Path -LiteralPath $manifestPath) {
    $manifestLines = @(Get-Content -LiteralPath $manifestPath | Where-Object { $_ -notmatch '^windows_setup_(asset|sha256):' })
    $manifestLines += ('windows_setup_asset: "TelefonRehberi-Setup-v{0}.exe"' -f $appVersion)
    $manifestLines += ('windows_setup_sha256: "{0}"' -f $setupSha)
    Set-Content -LiteralPath $manifestPath -Value $manifestLines -Encoding UTF8
}

$checksumsPath = Join-Path $dist 'CHECKSUMS.txt'
$checksumTargets = @(
    'TelefonRehberi.jar',
    'TelefonRehberi.exe',
    "TelefonRehberi-Portable-v$appVersion-FULL.zip",
    "TelefonRehberi-Setup-v$appVersion.exe",
    "TelefonRehberi-v$appVersion.tzpkg",
    'update-manifest.yml'
)
$checksumLines = foreach ($targetName in $checksumTargets) {
    $targetPath = Join-Path $dist $targetName
    if (Test-Path -LiteralPath $targetPath) {
        '{0}  {1}' -f ((Get-FileHash -LiteralPath $targetPath -Algorithm SHA256).Hash.ToLowerInvariant()), $targetName
    }
}
Set-Content -LiteralPath $checksumsPath -Value $checksumLines -Encoding ASCII

Write-Host "[OK] Setup hazir: $setup"
Write-Host "[OK] Setup SHA-256: $setupSha"
