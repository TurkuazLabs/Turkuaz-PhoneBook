# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/build-installer.ps1
# 📌 Amac: Inno Setup icin tam Windows payload ve Setup EXE uretir.
# 📌 Tool - PowerShell
# Version: 1.3.0
# Aciklama: Auto-update etkin installed payload, buyutulmus cok-cozunurluklu Windows ikonu, JRE/JDBC/SLF4J/FlatLaf ve Inno Setup EXE uretir.
# Bagimli Oldugu Katman: Tool | Config

param(
    [string]$ExpectedTag = '',
    [string]$DistPath = 'dist',
    [string]$StagePath = 'dist-installer'
)

$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$dist = Join-Path $root $DistPath
$stage = Join-Path $root $StagePath
$payload = Join-Path $stage 'payload'
$launcherConfig = Join-Path $root 'config\launcher.yml'
$versionConfig = Join-Path $root 'config\version.yml'
$iss = Join-Path $root 'packaging\windows\TelefonRehberi.iss'

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


function Write-EnlargedWindowsIcon([string]$SourcePng, [string]$DestinationIco) {
    if (-not (Test-Path -LiteralPath $SourcePng)) {
        throw "Windows ikon kaynagi bulunamadi: $SourcePng"
    }

    Add-Type -AssemblyName System.Drawing
    $source = [System.Drawing.Bitmap]::FromFile($SourcePng)
    try {
        $minX = $source.Width
        $minY = $source.Height
        $maxX = -1
        $maxY = -1

        for ($y = 0; $y -lt $source.Height; $y++) {
            for ($x = 0; $x -lt $source.Width; $x++) {
                if ($source.GetPixel($x, $y).A -gt 8) {
                    if ($x -lt $minX) { $minX = $x }
                    if ($y -lt $minY) { $minY = $y }
                    if ($x -gt $maxX) { $maxX = $x }
                    if ($y -gt $maxY) { $maxY = $y }
                }
            }
        }

        if ($maxX -lt $minX -or $maxY -lt $minY) {
            throw "Windows ikon kaynaginda gorunur piksel bulunamadi: $SourcePng"
        }

        $cropWidth = $maxX - $minX + 1
        $cropHeight = $maxY - $minY + 1
        $sourceRect = [System.Drawing.Rectangle]::new($minX, $minY, $cropWidth, $cropHeight)
        $sizes = @(16, 24, 32, 48, 64, 128, 256)
        $frames = @()

        foreach ($size in $sizes) {
            $canvas = [System.Drawing.Bitmap]::new([int]$size,[int]$size,[System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
            try {
                $graphics = [System.Drawing.Graphics]::FromImage($canvas)
                try {
                    $graphics.Clear([System.Drawing.Color]::Transparent)
                    $graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
                    $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
                    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
                    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
                    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

                    $margin = [Math]::Max(1, [int][Math]::Round($size * 0.04))
                    $available = $size - (2 * $margin)
                    $scale = [Math]::Min([double]$available / [double]$cropWidth,[double]$available / [double]$cropHeight)
                    $targetWidth = [Math]::Max(1,[int][Math]::Round($cropWidth * $scale))
                    $targetHeight = [Math]::Max(1,[int][Math]::Round($cropHeight * $scale))
                    $left = [int][Math]::Floor(($size - $targetWidth) / 2)
                    $top = [int][Math]::Floor(($size - $targetHeight) / 2)
                    $targetRect = [System.Drawing.Rectangle]::new($left,$top,$targetWidth,$targetHeight)
                    $graphics.DrawImage($source,$targetRect,$sourceRect,[System.Drawing.GraphicsUnit]::Pixel)
                }
                finally { $graphics.Dispose() }

                $memory = [System.IO.MemoryStream]::new()
                try {
                    $canvas.Save($memory,[System.Drawing.Imaging.ImageFormat]::Png)
                    $frames += ,$memory.ToArray()
                }
                finally { $memory.Dispose() }
            }
            finally { $canvas.Dispose() }
        }

        Ensure-Directory (Split-Path -Parent $DestinationIco)
        $stream = [System.IO.File]::Open($DestinationIco,[System.IO.FileMode]::Create,[System.IO.FileAccess]::Write,[System.IO.FileShare]::None)
        $writer = [System.IO.BinaryWriter]::new($stream)
        try {
            $writer.Write([UInt16]0)
            $writer.Write([UInt16]1)
            $writer.Write([UInt16]$sizes.Count)

            $offset = [UInt32](6 + (16 * $sizes.Count))
            for ($i = 0; $i -lt $sizes.Count; $i++) {
                $size = [int]$sizes[$i]
                $frame = [byte[]]$frames[$i]
                $dimension = if ($size -ge 256) { [byte]0 } else { [byte]$size }
                $writer.Write($dimension)
                $writer.Write($dimension)
                $writer.Write([byte]0)
                $writer.Write([byte]0)
                $writer.Write([UInt16]1)
                $writer.Write([UInt16]32)
                $writer.Write([UInt32]$frame.Length)
                $writer.Write([UInt32]$offset)
                $offset = [UInt32]($offset + $frame.Length)
            }
            foreach ($frame in $frames) { $writer.Write([byte[]]$frame) }
        }
        finally {
            $writer.Dispose()
            $stream.Dispose()
        }

        Write-Host "[OK] Windows kisayol ikonu buyutuldu: crop=${cropWidth}x${cropHeight}, ICO=16/24/32/48/64/128/256"
    }
    finally { $source.Dispose() }
}

function Find-Iscc {
    if ($env:ISCC_PATH -and (Test-Path -LiteralPath $env:ISCC_PATH)) { return $env:ISCC_PATH }
    $candidates = @(
        (Join-Path ${env:ProgramFiles} 'Inno Setup 7\ISCC.exe'),
        (Join-Path ${env:ProgramFiles(x86)} 'Inno Setup 7\ISCC.exe'),
        (Join-Path ${env:ProgramFiles(x86)} 'Inno Setup 6\ISCC.exe'),
        (Join-Path ${env:ProgramFiles} 'Inno Setup 6\ISCC.exe')
    )
    foreach ($candidate in $candidates) {
        if ($candidate -and (Test-Path -LiteralPath $candidate)) { return $candidate }
    }
    throw 'ISCC.exe bulunamadi. Inno Setup 6 veya 7 kurulu olmali.'
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
Write-EnlargedWindowsIcon `
    -SourcePng (Join-Path $installedBranding 'app-icon-512.png') `
    -DestinationIco (Join-Path $installedBranding 'app-icon.ico')
foreach ($name in @('app.yml','launcher.yml','state.yml','version.yml')) {
    Copy-Item -LiteralPath (Join-Path $root "config\$name") -Destination (Join-Path $payload "config\$name") -Force
}
Copy-Item -LiteralPath (Join-Path $root 'packaging\windows\installed.mode') -Destination (Join-Path $payload 'config\installed.mode') -Force

# Installed surum GitHub manifestinden Setup EXE indirerek guncellenir; update_enabled acik kalir.
$installedLauncherConfig = Join-Path $payload 'config\launcher.yml'
(Get-Content -LiteralPath $installedLauncherConfig) -replace '^update_enabled:\s*.*$', 'update_enabled: "true"' |
    Set-Content -LiteralPath $installedLauncherConfig -Encoding UTF8

$config = Read-SimpleYaml $launcherConfig
$cache = Join-Path $stage 'cache'
Ensure-Directory $cache

$sqliteVersion = $config['sqlite_jdbc_version']
$sqliteUrl = $config['sqlite_jdbc_url'].Replace('{version}', $sqliteVersion)
$sqliteTarget = Join-Path $payload ($config['sqlite_jdbc_path'].Replace('/', '\'))
$sqliteDownload = Join-Path $cache 'sqlite-jdbc.jar'
Download-File $sqliteUrl $sqliteDownload
Assert-Sha256 $sqliteDownload $config['sqlite_jdbc_sha256']
Ensure-Directory (Split-Path -Parent $sqliteTarget)
Copy-Item -LiteralPath $sqliteDownload -Destination $sqliteTarget -Force

$slf4jVersion = $config['slf4j_version']
$slf4jUrl = $config['slf4j_api_url'].Replace('{version}', $slf4jVersion)
$slf4jRelative = $config['slf4j_api_path'].Replace('{version}', $slf4jVersion).Replace('/', '\')
$slf4jTarget = Join-Path $payload $slf4jRelative
$slf4jDownload = Join-Path $cache 'slf4j-api.jar'
Download-File $slf4jUrl $slf4jDownload
Assert-Sha256 $slf4jDownload $config['slf4j_api_sha256']
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
Download-File $flatUrl $flatDownload
Assert-Sha256 $flatDownload $flatSha
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
Download-File $assets[0].binary.package.link $jreArchive
Assert-Sha256 $jreArchive $assets[0].binary.package.checksum
$jreExtract = Join-Path $stage 'jre-extract'
Expand-Archive -LiteralPath $jreArchive -DestinationPath $jreExtract -Force
$javaw = Get-ChildItem -Path $jreExtract -Filter 'javaw.exe' -Recurse -File | Select-Object -First 1
if (-not $javaw) { throw 'Temurin paketinde javaw.exe bulunamadi.' }
$jreHome = Split-Path -Parent (Split-Path -Parent $javaw.FullName)
$jreTarget = Join-Path $payload ($config['java_runtime_dir'].Replace('/', '\'))
Ensure-Directory (Split-Path -Parent $jreTarget)
Copy-Item -LiteralPath $jreHome -Destination $jreTarget -Recurse -Force

$iscc = Find-Iscc
Ensure-Directory $dist
& $iscc "/DAppVersion=$appVersion" "/DPayloadDir=$payload" "/DOutputDir=$dist" $iss
if ($LASTEXITCODE -ne 0) { throw "Inno Setup derlemesi basarisiz: $LASTEXITCODE" }

$setup = Join-Path $dist "TelefonRehberi-Setup-v$appVersion.exe"
if (-not (Test-Path -LiteralPath $setup)) { throw "Setup EXE uretilmedi: $setup" }

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
