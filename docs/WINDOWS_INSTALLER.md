# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/WINDOWS_INSTALLER.md
# 📌 Amac: v2.37.0 Inno Setup Program Files kurulumu ve auto-update davranisini belgeler.
# 📌 Modul - Markdown
# Version: 2.0.0
# Aciklama: Installed/portable ayrimi, writable launcher alani, uninstall ve Setup auto-update akisidir.
# Bagimli Oldugu Katman: Tool | Config

## Kurulum

Varsayilan dizin:

`C:\Program Files\TurkuazLabs\TelefonRehberi`

Setup native launcher, uygulama JAR'i, Temurin JRE 17, SQLite JDBC, FlatLaf ve branding assetlerini birlikte kurar. `config/installed.mode` installed dagitimi portable'dan ayirir.

## Yazilabilir launcher alani

`%LOCALAPPDATA%\TurkuazLabs\TelefonRehberi\Launcher`

Cache, log, state ve indirilen update Setup EXE burada tutulur. Program Files calisma verisi icin kullanilmaz.

## Auto-update

Launcher GitHub latest release manifestini kontrol eder. Daha yeni surumde Setup EXE cache'e indirilir, SHA-256 dogrulanir ve normal kullanici tokeniyla baslatilir. Inno Setup `PrivilegesRequired=admin` nedeniyle UAC ister. `/AUTOUPDATE` sonrasinda uygulama `runasoriginaluser` ile normal kullanici olarak yeniden acilir.

## Uninstall

Uninstall Program Files uygulama payloadini kaldirir; `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi` altindaki rehber DB, export ve backup dosyalarini silmez.
