# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/README.md
# 📌 Amac: Android native Java mobil istemcisinin build, izin ve senkron kullanimini aciklar.
# 📌 Documentation - Markdown
# Version: 2.37.0
# Aciklama: Android API 36 istemcisinin gelistirme ve cihaz test adimlarini tanimlar.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

# Android Mobil Istemci

## Gereksinimler

- JDK 17
- Android SDK API 36
- Gradle 8.13
- Android Gradle Plugin 8.13.2

## Build

GitHub Actions build'i otomatik yapar. Lokal ortamda `mobile/android` klasorunde Gradle mevcutsa:

`gradle :app:assembleDebug`

APK:

`app/build/outputs/apk/debug/app-debug.apk`

## Ilk baglanti

1. Windows masaustu uygulamasini ac.
2. `Mobil Senkron` butonuna bas.
3. PC adresini Android uygulamasindaki PC adresi alanina gir.
4. Tokeni gir.
5. `Baglantiyi Test Et` sec.
6. Android rehber izni istediginde izin ver.

## Yonu sec

`PC Rehberini Telefona Al`: SQLite kayitlarini Android sistem rehberine ekler.

`Telefon Rehberini PC'ye Gonder`: Android sistem rehberini okuyup PC SQLite veritabanina yollar.

## Izinler

- `android.permission.INTERNET`
- `android.permission.READ_CONTACTS`
- `android.permission.WRITE_CONTACTS`

Rehber izinleri runtime'da kullanicidan istenir.

## Guvenlik

v2.37.0 local HTTP kullandigi icin PC ve Android ayni guvenilir LAN/Wi-Fi aginda olmali. Masaustu 8787 portunu Internet'e acma.
