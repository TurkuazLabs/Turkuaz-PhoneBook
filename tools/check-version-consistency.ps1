# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/check-version-consistency.ps1
# 📌 Amac: Merkezi release surumlerinin masaustu, launcher, Android, iOS ve Windows installer ile birebir uyumunu dogrular.
# 📌 Tool - PowerShell
# Version: 1.1.0
# Aciklama: config/version.yml kaynak degerlerini dagitim katmanlari ile ana Turkce README ve Ingilizce README surum badge'leriyle karsilastirir.
# Bagimli Oldugu Katman: Tool | Config | Language

[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$versionFile = Join-Path $root 'config\version.yml'

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

function Assert-Value {
    param([string]$Actual, [string]$Expected, [string]$Description)
    if ($Actual -ne $Expected) {
        throw "$Description uyusmuyor. Beklenen=$Expected Gercek=$Actual"
    }
}

function Assert-ContainsLiteral {
    param([string]$Path, [string]$Expected, [string]$Description)
    if (-not (Test-Path -LiteralPath $Path)) { throw "$Description dosyasi bulunamadi: $Path" }
    $content = Get-Content -LiteralPath $Path -Raw -Encoding UTF8
    if (-not $content.Contains($Expected)) {
        throw "$Description surum degeri bulunamadi: $Expected"
    }
}

$version = Read-SimpleYaml $versionFile
$appVersion = [string]$version['app_version']
$launcherVersion = [string]$version['launcher_version']
$mobileBuild = [string]$version['mobile_build']

if ([string]::IsNullOrWhiteSpace($appVersion)) { throw 'app_version bos.' }
if ([string]::IsNullOrWhiteSpace($launcherVersion)) { throw 'launcher_version bos.' }
if ([string]::IsNullOrWhiteSpace($mobileBuild)) { throw 'mobile_build bos.' }
if ($mobileBuild -notmatch '^\d+$') { throw "mobile_build sayisal degil: $mobileBuild" }

$state = Read-SimpleYaml (Join-Path $root 'config\state.yml')
Assert-Value ([string]$state['app_version']) $appVersion 'config/state.yml app_version'
Assert-Value ([string]$state['launcher_version']) $launcherVersion 'config/state.yml launcher_version'

Assert-ContainsLiteral (Join-Path $root 'src\main\java\com\turkuazlabs\telefonrehberi\config\AppConfig.java') `
    ('public static final String APP_VERSION = "{0}";' -f $appVersion) 'AppConfig APP_VERSION'

Assert-ContainsLiteral (Join-Path $root 'mobile\android\app\build.gradle') `
    ("versionName '$appVersion'") 'Android versionName'
Assert-ContainsLiteral (Join-Path $root 'mobile\android\app\build.gradle') `
    ("versionCode $mobileBuild") 'Android versionCode'

Assert-ContainsLiteral (Join-Path $root 'mobile\ios\TurkuazTelefonRehberiIOS\project.yml') `
    ('MARKETING_VERSION: "{0}"' -f $appVersion) 'iOS MARKETING_VERSION'
Assert-ContainsLiteral (Join-Path $root 'mobile\ios\TurkuazTelefonRehberiIOS\project.yml') `
    ('CURRENT_PROJECT_VERSION: "{0}"' -f $mobileBuild) 'iOS CURRENT_PROJECT_VERSION'

Assert-ContainsLiteral (Join-Path $root 'packaging\windows\TelefonRehberi.iss') `
    ('#define AppVersion "{0}"' -f $appVersion) 'Inno Setup fallback AppVersion'

Assert-ContainsLiteral (Join-Path $root 'launcher\native\services\launcher_service.go') `
    ("TurkuazPhoneBookLauncher/$launcherVersion") 'Native launcher User-Agent'

Assert-ContainsLiteral (Join-Path $root 'README.md') `
    ("version-$appVersion-") 'README version badge'
Assert-ContainsLiteral (Join-Path $root 'README.en.md') `
    ("version-$appVersion-") 'README.en version badge'

Write-Host "[OK] Version consistency: App=$appVersion Launcher=$launcherVersion MobileBuild=$mobileBuild"
