# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/build-release.ps1
# 📌 Amac: Java 17 JAR, native Windows EXE launcher ve GitHub release paketini uretir.
# 📌 Modul - PowerShell
# Version: 2.4.0
# Aciklama: v2.37.0 quality gate uyumlu JAR, EXE, platform update manifesti ve portable ZIP uretir.
# Bagimli Oldugu Katman: Tool | Config

[CmdletBinding()]
param(
    [string]$ExpectedTag = ''
)

$ErrorActionPreference = 'Stop'
$rootPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$buildPath = Join-Path $rootPath 'build'
$distPath = Join-Path $rootPath 'dist'
$classesPath = Join-Path $buildPath 'classes'
$packageRoot = Join-Path $buildPath 'package'
$versionPath = Join-Path $rootPath 'config\version.yml'
$sourcePath = Join-Path $rootPath 'src\main\java'
$nativeLauncherPath = Join-Path $rootPath 'launcher\native'
$jarPath = Join-Path $distPath 'TelefonRehberi.jar'
$launcherDist = Join-Path $distPath 'TelefonRehberi.exe'
$manifestDist = Join-Path $distPath 'update-manifest.yml'

function Read-SimpleYaml {
    param([string]$Path)
    $result = @{}
    foreach ($line in Get-Content -LiteralPath $Path -Encoding UTF8) {
        $trimmed = $line.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmed) -or $trimmed.StartsWith('#')) { continue }
        $index = $trimmed.IndexOf(':')
        if ($index -lt 1) { continue }
        $key = $trimmed.Substring(0, $index).Trim()
        $value = $trimmed.Substring($index + 1).Trim().Trim('"').Trim("'")
        $result[$key] = $value
    }
    return $result
}

function Ensure-Directory {
    param([string]$Path)
    if (-not (Test-Path -LiteralPath $Path)) {
        New-Item -ItemType Directory -Path $Path -Force | Out-Null
    }
}

function Copy-DirectoryContent {
    param([string]$Source, [string]$Destination)
    Ensure-Directory $Destination
    if (Test-Path -LiteralPath $Source) {
        Copy-Item -Path (Join-Path $Source '*') -Destination $Destination -Recurse -Force
    }
}

function Write-StateFile {
    param([string]$Path, [string]$AppVersion, [string]$LauncherVersion)
    $content = @(
        '# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/config/state.yml',
        '# 📌 Amac: Kurulu uygulama ve native launcher surum durumunu tutar.',
        '# 📌 Modul - YAML',
        '# Version: 2.3.0',
        '# Aciklama: GitHub updater tarafindan yonetilen yerel surum bilgisidir.',
        '# Bagimli Oldugu Katman: Repository',
        '',
        ('app_version: "{0}"' -f $AppVersion),
        ('launcher_version: "{0}"' -f $LauncherVersion)
    )
    Set-Content -LiteralPath $Path -Value $content -Encoding UTF8
}

if (-not (Test-Path -LiteralPath $versionPath)) { throw "Surum dosyasi bulunamadi: $versionPath" }
$version = Read-SimpleYaml $versionPath
$appVersion = [string]$version['app_version']
$launcherVersion = [string]$version['launcher_version']
if ([string]::IsNullOrWhiteSpace($appVersion) -or [string]::IsNullOrWhiteSpace($launcherVersion)) {
    throw 'app_version veya launcher_version bos.'
}

if (-not [string]::IsNullOrWhiteSpace($ExpectedTag)) {
    $tagVersion = $ExpectedTag.Trim()
    if ($tagVersion.StartsWith('v')) { $tagVersion = $tagVersion.Substring(1) }
    if ($tagVersion -ne $appVersion) { throw "Git tag ile app_version uyusmuyor. Tag=$tagVersion App=$appVersion" }
}

Remove-Item -LiteralPath $buildPath -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -LiteralPath $distPath -Recurse -Force -ErrorAction SilentlyContinue
Ensure-Directory $classesPath
Ensure-Directory $distPath
Ensure-Directory $packageRoot

$sourceFiles = Get-ChildItem -LiteralPath $sourcePath -Filter '*.java' -File -Recurse | Sort-Object FullName
if ($sourceFiles.Count -lt 1) { throw 'Java kaynak dosyasi bulunamadi.' }
$sourceArgs = @($sourceFiles | ForEach-Object { $_.FullName })
& javac '--release' '17' '--add-modules' 'jdk.httpserver' '-encoding' 'UTF-8' '-d' $classesPath @sourceArgs
if ($LASTEXITCODE -ne 0) { throw "javac basarisiz. ExitCode=$LASTEXITCODE" }

$assetsSource = Join-Path $rootPath 'assets'
if (Test-Path -LiteralPath $assetsSource) { Copy-DirectoryContent $assetsSource (Join-Path $classesPath 'assets') }
$jarManifest = Join-Path $buildPath 'MANIFEST.MF'
Set-Content -LiteralPath $jarManifest -Value @('Manifest-Version: 1.0','Main-Class: com.turkuazlabs.telefonrehberi.Main','') -Encoding ASCII
& jar '--create' '--file' $jarPath '--manifest' $jarManifest '-C' $classesPath '.'
if ($LASTEXITCODE -ne 0) { throw "jar paketleme basarisiz. ExitCode=$LASTEXITCODE" }

if (-not (Get-Command go -ErrorAction SilentlyContinue)) { throw 'Go compiler bulunamadi. Native launcher icin Go 1.23+ gerekli.' }
Push-Location $nativeLauncherPath
try {
    & go test './...'
    if ($LASTEXITCODE -ne 0) { throw "Native launcher test basarisiz. ExitCode=$LASTEXITCODE" }
    $oldGoos = $env:GOOS
    $oldGoarch = $env:GOARCH
    $oldCgo = $env:CGO_ENABLED
    try {
        $env:GOOS = 'windows'
        $env:GOARCH = 'amd64'
        $env:CGO_ENABLED = '0'
        & go build '-trimpath' '-ldflags=-H=windowsgui -s -w' '-o' $launcherDist './cmd/telefonrehberi'
        if ($LASTEXITCODE -ne 0) { throw "Native launcher build basarisiz. ExitCode=$LASTEXITCODE" }
    }
    finally {
        $env:GOOS = $oldGoos
        $env:GOARCH = $oldGoarch
        $env:CGO_ENABLED = $oldCgo
    }
}
finally { Pop-Location }

$appSha = (Get-FileHash -LiteralPath $jarPath -Algorithm SHA256).Hash.ToLowerInvariant()
$launcherSha = (Get-FileHash -LiteralPath $launcherDist -Algorithm SHA256).Hash.ToLowerInvariant()

$updateManifest = @(
    '# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/updates/update-manifest.yml',
    '# 📌 Amac: GitHub Release asset update metadata ve SHA-256 degerlerini tanimlar.',
    '# 📌 Modul - YAML',
    '# Version: 2.3.0',
    '# Aciklama: v2.37.0 Windows/Linux platform launcher ve Setup auto-update release metadata dosyasidir.',
    '# Bagimli Oldugu Katman: Tool',
    '',
    ('app_version: "{0}"' -f $appVersion),
    'app_asset: "TelefonRehberi.jar"',
    ('app_sha256: "{0}"' -f $appSha),
    ('launcher_version: "{0}"' -f $launcherVersion),
    'launcher_windows_asset: "TelefonRehberi.exe"',
    ('launcher_windows_sha256: "{0}"' -f $launcherSha),
    'launcher_asset: "TelefonRehberi.exe"',
    ('launcher_sha256: "{0}"' -f $launcherSha)
)
Set-Content -LiteralPath $manifestDist -Value $updateManifest -Encoding UTF8

$portableName = "TelefonRehberi-Portable-v$appVersion"
$portablePath = Join-Path $packageRoot $portableName
foreach ($dir in @('app','assets','cache','config','lib','logs','runtime','updates','updates/app-backups')) {
    Ensure-Directory (Join-Path $portablePath $dir)
}

Copy-Item -LiteralPath $launcherDist -Destination (Join-Path $portablePath 'TelefonRehberi.exe') -Force
Copy-Item -LiteralPath (Join-Path $rootPath 'README.md') -Destination $portablePath -Force
foreach ($docName in @('LICENSE','SECURITY.md','THIRD_PARTY_NOTICES.md')) {
    $docSource = Join-Path $rootPath $docName
    if (Test-Path -LiteralPath $docSource) { Copy-Item -LiteralPath $docSource -Destination $portablePath -Force }
}
Copy-DirectoryContent (Join-Path $rootPath 'assets') (Join-Path $portablePath 'assets')
Copy-Item -LiteralPath $jarPath -Destination (Join-Path $portablePath 'app\TelefonRehberi.jar') -Force
Copy-Item -LiteralPath (Join-Path $rootPath 'config\launcher.yml') -Destination (Join-Path $portablePath 'config\launcher.yml') -Force
Copy-Item -LiteralPath (Join-Path $rootPath 'config\app.yml') -Destination (Join-Path $portablePath 'config\app.yml') -Force
Copy-Item -LiteralPath (Join-Path $rootPath 'config\version.yml') -Destination (Join-Path $portablePath 'config\version.yml') -Force
Write-StateFile -Path (Join-Path $portablePath 'config\state.yml') -AppVersion $appVersion -LauncherVersion $launcherVersion
Copy-Item -LiteralPath $manifestDist -Destination (Join-Path $portablePath 'updates\update-manifest.yml') -Force

foreach ($dir in @('cache','lib','logs','runtime')) {
    $readmeSource = Join-Path $rootPath "$dir\README.md"
    if (Test-Path -LiteralPath $readmeSource) { Copy-Item -LiteralPath $readmeSource -Destination (Join-Path $portablePath "$dir\README.md") -Force }
}

$internalChecksums = @(
    '# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/CHECKSUMS.txt',
    '# 📌 Amac: Portable paketteki kritik release dosyalarinin SHA-256 degerlerini listeler.',
    '# 📌 Modul - Text',
    '# Version: 2.3.0',
    '# Aciklama: Native launcher ve JAR manuel butunluk kontroludur.',
    '# Bagimli Oldugu Katman: Tool',
    '',
    ("{0}  TelefonRehberi.exe" -f $launcherSha),
    ("{0}  app/TelefonRehberi.jar" -f $appSha),
    ("{0}  updates/update-manifest.yml" -f ((Get-FileHash -LiteralPath $manifestDist -Algorithm SHA256).Hash.ToLowerInvariant()))
)
Set-Content -LiteralPath (Join-Path $portablePath 'CHECKSUMS.txt') -Value $internalChecksums -Encoding UTF8

$portableZip = Join-Path $distPath "$portableName-FULL.zip"
Compress-Archive -Path $portablePath -DestinationPath $portableZip -CompressionLevel Optimal -Force
$portableSha = (Get-FileHash -LiteralPath $portableZip -Algorithm SHA256).Hash.ToLowerInvariant()
$manifestSha = (Get-FileHash -LiteralPath $manifestDist -Algorithm SHA256).Hash.ToLowerInvariant()

$distChecksums = @(
    '# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/dist/CHECKSUMS.txt',
    '# 📌 Amac: GitHub Release asset SHA-256 degerlerini listeler.',
    '# 📌 Modul - Text',
    '# Version: 2.3.0',
    '# Aciklama: JAR, native EXE, manifest ve portable ZIP butunluk kontroludur.',
    '# Bagimli Oldugu Katman: Tool',
    '',
    ("{0}  TelefonRehberi.jar" -f $appSha),
    ("{0}  TelefonRehberi.exe" -f $launcherSha),
    ("{0}  update-manifest.yml" -f $manifestSha),
    ("{0}  {1}" -f $portableSha, [IO.Path]::GetFileName($portableZip))
)
Set-Content -LiteralPath (Join-Path $distPath 'CHECKSUMS.txt') -Value $distChecksums -Encoding UTF8

Write-Host "Release build tamamlandi. App=$appVersion Launcher=$launcherVersion"
Get-ChildItem -LiteralPath $distPath -File | Select-Object Name, Length
