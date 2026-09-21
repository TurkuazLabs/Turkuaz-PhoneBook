# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/RELEASE_CHECKLIST.md
# 📌 Amac: Turkuaz PhoneBook release oncesi ve sonrasi dogrulama adimlarini tek yerde tutar.
# 📌 Modul - Markdown
# Version: 1.5.0
# Aciklama: config/version.yml merkezli preflight, dinamik tag/release, checksum, update-manifest ve dagitim smoke kontrolleridir.
# Bagimli Oldugu Katman: Tool | Config | Language

# Turkuaz PhoneBook Release Checklist

Release surumleri icin tek kaynak `config/version.yml` dosyasidir:

- `APP_VERSION = app_version`
- `LAUNCHER_VERSION = launcher_version`
- `MOBILE_BUILD = mobile_build`

## Release oncesi

- `main` son commitinin GitHub Actions `Build` kosusu tamamen yesil olmali.
- Windows job: Java quality gate, desktop build, Inno Setup build ve silent install/uninstall smoke testi gecmeli.
- Linux job: Java quality gate ve native full TAR.GZ build gecmeli.
- Android API 36 debug APK ve iOS simulator build gecmeli.
- Main Build artefactlari `windows-main-build`, `linux-main-build`, `android-main-debug` olarak olusmali.
- Windows portable ZIP ve Linux TAR.GZ archive integrity kontrolunden gecmeli.
- Portable paketlerin bootstrap dagitimi oldugu ve ilk acilista internet uzerinden runtime/bagimlilik indirdigi dokumantasyonda acikca belirtilmeli; Windows Setup ise offline runtime/bagimlilik payloadini tasimali.
- Paketlerde kullaniciya ait `telefon-rehberi.db`, `preferences.yml`, sync token, WAL veya SHM dosyasi bulunmamali.
- `tools/check-version-consistency.ps1` app, launcher ve mobil build degerlerini tum dagitim katmanlarinda dogrulamali.
- `updates/update-manifest.yml` main branchte son yayinlanmis release referansini tasimali. Yeni release manifesti tag-triggered Release workflow tarafindan gercek SHA-256 degerleriyle uretilir.

## Yayin

1. `config/version.yml` icindeki `APP_VERSION` icin release metadata dosyalarini hazirla.
2. Son yesil `main` commit mesajini tam olarak `publish v<APP_VERSION> release` yap.
3. Build workflow, tum platform joblari yesil olduktan sonra `v<APP_VERSION>` annotated tagini otomatik olusturur.
4. Build workflow, tag icin `Release` workflow'unu otomatik dispatch eder.
5. GitHub Release basliginin `Turkuaz PhoneBook v<APP_VERSION>` oldugunu dogrula.

## Release assetleri

Asagidaki assetler release'te bulunmali:

- `TelefonRehberi.jar`
- `TelefonRehberi.exe`
- `TelefonRehberi-linux-amd64`
- `update-manifest.yml`
- `CHECKSUMS.txt`
- `TelefonRehberi-Portable-v<APP_VERSION>-FULL.zip`
- `TelefonRehberi-Setup-v<APP_VERSION>.exe`
- `TelefonRehberi-Linux-v<APP_VERSION>-FULL.tar.gz`
- `Turkuaz-PhoneBook-Android-v<APP_VERSION>-debug.apk`

## Release sonrasi

- `CHECKSUMS.txt` ile release assetlerinin SHA-256 degerlerini dogrula.
- Release `update-manifest.yml` icinde `app_version: "<APP_VERSION>"` ve dogru `launcher_version` oldugunu dogrula.
- Windows Setup SHA-256 degerinin manifestteki `windows_setup_sha256` ile ayni oldugunu dogrula.
- Temiz Windows sisteminde Setup kurulumunu ve uygulama acilisini kontrol et.
- Portable Windows paketinin mevcut kullanici verisini paket icine yazmadan acildigini ve temiz sistemde ilk acilista runtime/bagimlilik bootstrap akisini basariyla tamamladigini kontrol et.
- Linux full pakette `Kurulum.sh`, native launcher ve masaustu girdisini kontrol et.
- Turkce ve English masaustu dil tercihlerinin kaydedildigi anda mevcut pencerede uygulanip kaydedilmemis kisi draftini korudugunu kontrol et.
- Launcher latest release kontrolunun yeni `v<APP_VERSION>` manifestini gordugunu dogrula.
- Release tamamlandiktan sonra main `updates/update-manifest.yml` dosyasini yayinlanmis assetlerin gercek SHA-256 degerleriyle guncelle.
