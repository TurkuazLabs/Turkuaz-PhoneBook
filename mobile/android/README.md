# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/README.md
# 📌 Amac: Android native Java companion client build, permission and synchronization usage documentation.
# 📌 Documentation - Markdown
# Version: 2.38.0
# Aciklama: Android API 36 companion client development, device testing, localization and trusted-LAN synchronization steps in English.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

# Turkuaz PhoneBook — Android Companion

[Turkce README](./README.tr.md)

## Requirements

- JDK 17
- Android SDK API 36
- Gradle 8.13
- Android Gradle Plugin 8.13.2

## Build

GitHub Actions builds the Android client automatically. With Gradle available locally, run from `mobile/android`:

`gradle :app:assembleDebug`

Debug APK:

`app/build/outputs/apk/debug/app-debug.apk`

## First Connection

1. Open the Turkuaz PhoneBook desktop application.
2. Open **Mobile Sync**.
3. Enter the desktop PC address in the Android client.
4. Enter the sync token.
5. Select **Test Connection**.
6. Grant Contacts permission when Android asks for it.

## Choose a Direction

**Get PC Contacts** adds desktop SQLite contacts to the Android system contacts provider.

**Send Phone Contacts to PC** reads Android contacts and sends them to the desktop SQLite database.

## Permissions

- `android.permission.INTERNET`
- `android.permission.READ_CONTACTS`
- `android.permission.WRITE_CONTACTS`

Contacts permissions are requested at runtime.

## Localization

English is the default/fallback resource language. Turkish devices use `values-tr`. The visible product name is **Turkuaz PhoneBook** in English/fallback locales and **Turkuaz Telefon Rehberi** in Turkish.

## Security

v2.37.0 uses local HTTP for LAN synchronization. Use sync only while the PC and Android device are on the same trusted LAN/Wi-Fi network. Do not expose the desktop sync port `8787` to the Internet.
