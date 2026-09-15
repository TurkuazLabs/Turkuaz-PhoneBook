# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/GITHUB_SETUP.md
# 📌 Amac: TurkuazLabs GitHub repository, quality gate ve cross-platform release akislarini belgeler.
# 📌 Modul - Markdown
# Version: 2.4.1
# Aciklama: Turkuaz-PhoneBook repository adi, v2.37.0 Windows Inno Setup auto-update, Linux native/TAR.GZ ve mobile test build akisidir.
# Bagimli Oldugu Katman: Tool | Config

# Repository

`https://github.com/TurkuazLabs/Turkuaz-PhoneBook`

Kaynak dosyalari repository kokune yerlestir.

## Ilk yayin

1. Kaynaklari `main` branch'e push et.
2. GitHub Actions `Build` sonucunda Java quality gate, Windows installer smoke, Linux, Android ve iOS kontrollerinin gectigini dogrula.
3. `v2.37.0` tag olustur.
4. Tag'i GitHub'a push et.
5. `Release` workflow su assetleri olusturur:
   - `TelefonRehberi.jar`
   - `TelefonRehberi.exe`
   - `TelefonRehberi-linux-amd64`
   - `update-manifest.yml`
   - `CHECKSUMS.txt`
   - `TelefonRehberi-Portable-v2.37.0-FULL.zip`
   - `TelefonRehberi-Setup-v2.37.0.exe`
   - `TelefonRehberi-Linux-v2.37.0-FULL.tar.gz`
   - Android test APK

Windows kurulumlu surum `C:\Program Files\TurkuazLabs\TelefonRehberi` altina kurulur. Portable kullanici `TelefonRehberi.exe`, Linux kullanicisi `TelefonRehberi` native launcher ile baslatir.

## Marka ve urun adi

GitHub repository ve global urun kimligi `Turkuaz PhoneBook` olarak kullanilir. Ana logo yalnizca `Turkuaz` markasini tasir.

Gorunen uygulama adi kurulum/arayuz diline gore yerellestirilebilir:

- Turkce: `Turkuaz Telefon Rehberi`
- Ingilizce: `Turkuaz PhoneBook`

Geriye donuk uyumluluk icin teknik kimlikler (`TelefonRehberi.exe`, Java package adlari, Inno Setup AppId ve mevcut veri klasorleri) yeniden adlandirilmaz.

## Veri konumlari

- Windows SQLite: `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri\telefon-rehberi.db`
- Windows export: `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Disari Aktarilanlar`
- Windows backup: `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Yedekler`
- Windows preferences: `%APPDATA%\TurkuazLabs\TelefonRehberi\config\preferences.yml`
- Windows launcher cache/log/state: `%LOCALAPPDATA%\TurkuazLabs\TelefonRehberi\Launcher`
- Linux teknik veri: XDG data/config/cache alanlari
- Linux kullanici dosyalari: `~/Contacts/Turkuaz Telefon Rehberi` veya XDG Documents fallback

## Otomatik guncelleme

Release workflow `TelefonRehberi-Setup-vX.Y.Z.exe` SHA-256 degerini `update-manifest.yml` icine yazar. Program Files kurulumlu launcher yeni `app_version` gorurse Setup EXE'yi LocalAppData cache'e indirir, SHA-256 dogrular ve Inno Setup'i normal kullanici tokeniyla baslatir. Inno Setup UAC ister ve `/AUTOUPDATE` tamamlaninca uygulamayi normal kullanici olarak yeniden acar. Portable Windows ve Linux dagitimlari JAR/native launcher staged update akisini kullanir.

Updater release assetlerini `https://github.com/TurkuazLabs/Turkuaz-PhoneBook` repository'sinden alir.

## Quality gate

- `tools/test-java.ps1` / `tools/test-java.sh`: WAL backup, sync UUID idempotency, history retention, request limiti ve preferences round-trip testleri.
- `go test ./...` ve `go vet ./...`: native launcher.
- Windows CI: Inno Setup silent install payload smoke testi.
- Android: API 36 debug APK build.
- iOS: simulator build, code signing kapali.
