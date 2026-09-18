# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/test-java.ps1
# 📌 Amac: Windows Java 17 release quality gate testlerini SQLite JDBC ile derleyip calistirir.
# 📌 Tool - PowerShell
# Version: 1.3.0
# Aciklama: Genel Java, sync-token ve cross-platform mobil API endpoint sozlesmesi quality gate testlerini sabitlenmis SQLite JDBC ve SLF4J bagimliliklariyla calistirir.
# Bagimli Oldugu Katman: Tool | Config | Repository | Service | Language

$ErrorActionPreference = 'Stop'
$Root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$Config = Join-Path $Root 'config\launcher.yml'
$Build = Join-Path $Root 'build-tests'
$Classes = Join-Path $Build 'classes'
$Lib = Join-Path $Build 'lib'

function Read-SimpleYamlValue([string]$Path, [string]$Key) {
    foreach ($line in Get-Content -LiteralPath $Path -Encoding UTF8) {
        $trimmed = $line.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmed) -or $trimmed.StartsWith('#')) { continue }
        $index = $trimmed.IndexOf(':')
        if ($index -lt 1) { continue }
        if ($trimmed.Substring(0, $index).Trim() -ne $Key) { continue }
        return $trimmed.Substring($index + 1).Trim().Trim('"').Trim("'")
    }
    throw "YAML anahtari bulunamadi: $Key"
}

$Version = Read-SimpleYamlValue $Config 'sqlite_jdbc_version'
$UrlTemplate = Read-SimpleYamlValue $Config 'sqlite_jdbc_url'
$ExpectedSha = (Read-SimpleYamlValue $Config 'sqlite_jdbc_sha256').ToLowerInvariant()
$Url = $UrlTemplate.Replace('{version}', $Version)
$Jar = Join-Path $Lib "sqlite-jdbc-$Version.jar"
$Slf4jVersion = Read-SimpleYamlValue $Config 'slf4j_version'
$Slf4jUrlTemplate = Read-SimpleYamlValue $Config 'slf4j_api_url'
$Slf4jExpectedSha = (Read-SimpleYamlValue $Config 'slf4j_api_sha256').ToLowerInvariant()
$Slf4jUrl = $Slf4jUrlTemplate.Replace('{version}', $Slf4jVersion)
$Slf4jJar = Join-Path $Lib "slf4j-api-$Slf4jVersion.jar"

Remove-Item -LiteralPath $Build -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $Classes, $Lib -Force | Out-Null
Invoke-WebRequest -UseBasicParsing -Uri $Url -OutFile $Jar
$ActualSha = (Get-FileHash -LiteralPath $Jar -Algorithm SHA256).Hash.ToLowerInvariant()
if ($ActualSha -ne $ExpectedSha) { throw 'SQLite JDBC SHA-256 uyusmuyor.' }
Invoke-WebRequest -UseBasicParsing -Uri $Slf4jUrl -OutFile $Slf4jJar
$Slf4jActualSha = (Get-FileHash -LiteralPath $Slf4jJar -Algorithm SHA256).Hash.ToLowerInvariant()
if ($Slf4jActualSha -ne $Slf4jExpectedSha) { throw 'SLF4J API SHA-256 uyusmuyor.' }

$Sources = @(Get-ChildItem -LiteralPath (Join-Path $Root 'src\main\java'), (Join-Path $Root 'src\test\java') -Filter '*.java' -File -Recurse | Sort-Object FullName | ForEach-Object FullName)
& javac '--release' '17' '--add-modules' 'jdk.httpserver' '-encoding' 'UTF-8' '-d' $Classes @Sources
if ($LASTEXITCODE -ne 0) { throw "Java test compile basarisiz. ExitCode=$LASTEXITCODE" }

$ClassPath = "$Classes;$Jar;$Slf4jJar"
& java '--add-modules' 'jdk.httpserver' '-cp' $ClassPath 'com.turkuazlabs.telefonrehberi.QualityGateTest'
if ($LASTEXITCODE -ne 0) { throw "Java quality gate basarisiz. ExitCode=$LASTEXITCODE" }

& java '--add-modules' 'jdk.httpserver' '-cp' $ClassPath 'com.turkuazlabs.telefonrehberi.SyncTokenStoreQualityGateTest'
if ($LASTEXITCODE -ne 0) { throw "Sync token quality gate basarisiz. ExitCode=$LASTEXITCODE" }

& java '--add-modules' 'jdk.httpserver' '-cp' $ClassPath 'com.turkuazlabs.telefonrehberi.ApiContractQualityGateTest' $Root
if ($LASTEXITCODE -ne 0) { throw "API contract quality gate basarisiz. ExitCode=$LASTEXITCODE" }
