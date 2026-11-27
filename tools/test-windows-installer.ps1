# Dosya Yolu: REPO_ROOT/tools/test-windows-installer.ps1
# Amac: Aktif native TurkuazInstaller setup ve payload smoke testini ortak calistirir.
# Modul - Tool PowerShell
# Version: 1.0.0
# Aciklama: Build ve release CI icin izole kurulum/kaldirma testini tek noktada tutar.
# Bagimli Oldugu Katman: Tool | Config

[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$Root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$versionMatches = @(Select-String -LiteralPath (Join-Path $Root 'config/version.yml') -Pattern '^app_version:\s*"(\d+\.\d+\.\d+)"\s*$')
if ($versionMatches.Count -ne 1) { throw 'Tek gecerli app_version bulunamadi.' }
$version = $versionMatches[0].Matches[0].Groups[1].Value
$dist = Join-Path $Root 'dist'
$setup = Join-Path $dist "TelefonRehberi-Setup-v$version.exe"
$package = Join-Path $dist "TelefonRehberi-v$version.tzpkg"
foreach ($file in @($setup, $package)) {
    if (-not (Test-Path -LiteralPath $file -PathType Leaf)) { throw "TurkuazInstaller cikti dosyasi eksik: $file" }
    if ((Get-Item -LiteralPath $file).Length -eq 0) { throw "Bos kurulum dosyasi: $file" }
}
if ([string]::IsNullOrWhiteSpace($env:RUNNER_TEMP)) { throw 'RUNNER_TEMP tanimli degil.' }
$target = Join-Path $env:RUNNER_TEMP ("TurkuazTelefonRehberi-Installer-Smoke-" + [Guid]::NewGuid().ToString('N'))
if (Test-Path -LiteralPath $target) { throw 'Smoke test hedefi zaten mevcut.' }
& $setup install-local-self --target $target
if ($LASTEXITCODE -ne 0) { throw 'TurkuazInstaller kurulum smoke testi basarisiz.' }
foreach ($required in @('TelefonRehberi.exe','app\TelefonRehberi.jar','config\installed.mode','runtime\temurin-17\bin\javaw.exe')) {
    if (-not (Test-Path -LiteralPath (Join-Path $target $required) -PathType Leaf)) {
        throw "Kurulum smoke testi eksik dosya: $required"
    }
}
& $setup uninstall-local --package-id com.turkuazlabs.telefonrehberi --target $target --confirm-uninstall YES
if ($LASTEXITCODE -ne 0) { throw 'TurkuazInstaller kaldirma smoke testi basarisiz.' }
Write-Host 'TurkuazInstaller isolated install/uninstall command smoke: PASS'
