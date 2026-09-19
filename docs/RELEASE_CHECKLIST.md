# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/RELEASE_CHECKLIST.md
# 📌 Amac: Turkuaz PhoneBook v2.38.4 release oncesi ve sonrasi dogrulama adimlarini tek yerde tutar.
# 📌 Modul - Markdown
# Version: 1.3.1
# Aciklama: Main CI artefact preflight, tag, Release workflow, checksum, update-manifest ve dagitim smoke kontrolleridir.
# Bagimli Oldugu Katman: Tool | Config | Language

# Turkuaz PhoneBook v2.38.4 Release Checklist

## Release oncesi

- `main` son commitinin GitHub Actions `Build` kosusu tamamen yesil olmali.
- Windows job: Java quality gate, desktop build, Inno Setup build ve silent install/uninstall smoke testi gecmeli.
- Linux job: Java quality gate ve native full TAR.GZ build gecmeli.
- Android API 36 debug APK ve iOS simulator build gecmeli.
- Main Build artefactlari `windows-main-build`, `linux-main-build`, `android-main-debug` olarak olusmali.
- Windows portable ZIP ve Linux TAR.GZ archive integrity kontrolunden gecmeli.
- Portable paketlerin bootstrap dagitimi oldugu ve ilk acilista internet uzerinden runtime/bagimlilik indirdigi dokumantasyonda acikca belirtilmeli; Windows Setup ise offline runtime/bagimlilik payloadini tasimali.
- Paketlerde kullaniciya ait `telefon-rehberi.db`, `preferences.yml`, sync token, WAL veya SHM dosyasi bulunmamali.
- Teknik surumler `app 2.38.4`, `launcher 2.5.2`, Android/iOS build 584 ile version consistency gate'i gecmeli.
- `updates/update-manifest.yml` main branchte son yayinlanmis release referansi olarak kalabilir; gercek v2.38.4 cross-platform manifesti tag-triggered Release workflow tarafindan release assetlerinin gercek SHA-256 degerleriyle uretilir.

## Yayin

1. Son yesil `main` commitinde `v2.38.4` tag olustur.
2. Tag'i GitHub'a push et.
3. `Release` workflow'un tum joblarinin yesil tamamlanmasini bekle.
4. GitHub Release basliginin `Turkuaz PhoneBook v2.38.4` oldugunu dogrula.

## Release assetleri

Asagidaki assetler release'te bulunmali:

- `TelefonRehberi.jar`
- `TelefonRehberi.exe`
- `TelefonRehberi-linux-amd64`
- `update-manifest.yml`
- `CHECKSUMS.txt`
- `TelefonRehberi-Portable-v2.38.4-FULL.zip`
- `TelefonRehberi-Setup-v2.38.4.exe`
- `TelefonRehberi-Linux-v2.38.4-FULL.tar.gz`
- `Turkuaz-PhoneBook-Android-v2.38.4-debug.apk`

## Release sonrasi

- `CHECKSUMS.txt` ile release assetlerinin SHA-256 degerlerini dogrula.
- Release `update-manifest.yml` icinde `app_version: "2.38.4"` ve `launcher_version: "2.5.2"` oldugunu dogrula.
- Windows Setup SHA-256 degerinin manifestteki `windows_setup_sha256` ile ayni oldugunu dogrula.
- Temiz Windows sisteminde Setup kurulumunu ve uygulama acilisini kontrol et.
- Portable Windows paketinin mevcut kullanici verisini paket icine yazmadan acildigini ve temiz sistemde ilk acilista runtime/bagimlilik bootstrap akisini basariyla tamamladigini kontrol et.
- Linux full pakette `Kurulum.sh`, native launcher ve masaustu girdisini kontrol et.
- Turkce ve English masaustu dil tercihlerinin kaydedilip yeniden baslatma sonrasi uygulandigini kontrol et.
- Launcher latest release kontrolunun v2.38.4 manifestini gordugunu dogrula.
