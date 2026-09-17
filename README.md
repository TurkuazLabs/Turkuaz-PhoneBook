# 📄 Dosya Yolu: /README.md
# 📌 Amac: Turkuaz PhoneBook projesinin uluslararasi GitHub vitrini ve teknik ozetidir
# 📌 Modul - Markdown
# Version: 2.38.2
# Aciklama: v2.38.0 global marka kimligi, platform destegi, local-first veri modeli, guvenlik, Turkce/Ingilizce yerellestirme, build ve release akislarini Ingilizce ana README olarak sunar
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

<div align="center">
  <img src="assets/branding/app-icon-128.png" alt="Turkuaz" width="120">

  <h1>Turkuaz PhoneBook</h1>

  <p><strong>Local-first contact management and mobile sync by TurkuazLabs.</strong></p>
  <p>Turkish product name: <strong>Turkuaz Telefon Rehberi</strong> · Brand: <strong>Turkuaz</strong></p>

  <p><a href="README.tr.md">Turkce README</a></p>

  <p>
    <a href="https://github.com/TurkuazLabs/Turkuaz-PhoneBook/actions/workflows/build.yml"><img src="https://github.com/TurkuazLabs/Turkuaz-PhoneBook/actions/workflows/build.yml/badge.svg" alt="Build"></a>
    <img src="https://img.shields.io/badge/version-2.38.0-0aa6a6" alt="Version 2.38.0">
    <img src="https://img.shields.io/badge/Java-17-orange" alt="Java 17">
    <img src="https://img.shields.io/badge/SQLite-local-blue" alt="SQLite">
    <img src="https://img.shields.io/badge/Windows-supported-0078D4" alt="Windows">
    <img src="https://img.shields.io/badge/Linux-supported-FCC624" alt="Linux">
  </p>
</div>

---

## Overview

**Turkuaz PhoneBook** is a desktop-first contact manager designed around local ownership of personal data. The primary contact database stays on the user's device in SQLite; no TurkuazLabs cloud account or central contact-storage service is required for normal use.

Windows and Linux are the primary desktop platforms. Android and iOS companion clients can synchronize contacts with the desktop application over the same trusted local network.

The project focuses on:

- local ownership of contact data
- practical contact organization beyond a basic address book
- native desktop launchers on Windows and Linux
- optional two-way LAN synchronization with mobile devices
- safe backup, migration, import/export and update workflows

## Branding and Product Names

The canonical repository and global product identity are **Turkuaz PhoneBook**. The language-neutral brand is simply **Turkuaz**.

Visible product names are localized:

- Turkish: **Turkuaz Telefon Rehberi**
- English and fallback: **Turkuaz PhoneBook**

The logo and icon identity are language-neutral. Compatibility-sensitive technical identifiers are intentionally preserved, including `TelefonRehberi.exe`, Java package names, the Inno Setup `AppId`, and existing user-data paths.

Canonical repository:

`https://github.com/TurkuazLabs/Turkuaz-PhoneBook`

## Features

### Contact Management

- contacts, phone numbers, email addresses, postal addresses, companies and notes
- multiple phone numbers and email addresses per contact
- profile photos
- favorites
- groups and colored tags
- smart lists
- Trash and restore

### History and Bulk Operations

- per-contact activity/history timeline
- multi-step Undo / Redo for bulk operations
- history retention controls
- duplicate candidate scanning
- lossless contact merging

### Import and Export

- VCF 3.0 / 4.0 import
- vCard 4.0 export
- UTF-8 CSV import/export
- migration from legacy portable TSV data

### Reminders

- birthdays
- anniversaries
- custom important dates
- keep-in-touch schedules

### Mobile Synchronization

- two-way Android/iOS LAN synchronization
- persistent `sync_uuid` identity
- mobile native-contact-ID to sync-UUID mapping
- bearer-token protected REST API
- ambiguity-safe phone/email identity fallback

## Platform Status

| Platform | Status | Distribution / Build | Notes |
| --- | --- | --- | --- |
| Windows 10/11 x64 | ✅ Supported | Inno Setup + Portable | Primary desktop platform |
| Linux x64 | ✅ Supported | Native ELF + TAR.GZ | User-level installation |
| Android API 36 | 🧪 Testing | Debug APK | LAN sync companion |
| iOS | 🧪 Testing | Simulator build | Device release distribution is not enabled yet |

GitHub Actions validates Windows, Linux, Android API 36 and iOS simulator builds on `main`.

## Localization

The application and distribution layers support Turkish and English/fallback product identity:

- The Swing desktop UI reads the JVM/system locale: Turkish (`tr`) uses the Turkish catalog; every other locale currently uses the English fallback catalog.
- The desktop product title is **Turkuaz Telefon Rehberi** on Turkish systems and **Turkuaz PhoneBook** otherwise.
- Windows Inno Setup uses **Turkuaz Telefon Rehberi** or **Turkuaz PhoneBook** according to the selected installer language.
- The native Go launcher reads the Windows UI language or Linux `LC_ALL` / `LC_MESSAGES` / `LANG` locale and localizes splash, status and user-visible error text.
- Android uses English default resources and Turkish `values-tr` resources.
- iOS uses English defaults plus Turkish `InfoPlist.strings`; sync-screen messages also follow the preferred UI language.
- The Linux `.desktop` file includes English defaults and Turkish `Name[tr]` / `Comment[tr]` entries.
- `Kurulum.sh` and `Kaldir.sh` localize terminal output for Turkish vs. English/fallback locales.
- The splash brand itself remains simply **Turkuaz**.

The desktop localization is intentionally system-locale based for now; changing language interactively while the application is running is not yet exposed as a user setting. Technical identifiers and existing user-data paths remain unchanged.

## Quick Start

### Windows — Installed

Run the Inno Setup package from a release:

`TelefonRehberi-Setup-vX.Y.Z.exe`

Default installation directory:

`C:\Program Files\TurkuazLabs\TelefonRehberi`

User data is not stored under Program Files.

### Windows — Portable

Extract the portable archive and launch:

`TelefonRehberi.exe`

The native launcher uses the packaged runtime and dependencies.

### Linux

Extract the full Linux package and run `Kurulum.sh` for a user-level installation. The package includes the native ELF launcher and a desktop entry.

### Development Requirements

Primary desktop requirements:

- Java 17
- Go 1.23.x
- SQLite JDBC
- FlatLaf

Build, test and release helpers live under `tools/`.

## Data and Privacy

Turkuaz PhoneBook stores the primary contact database locally.

### Windows Database

`%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri\telefon-rehberi.db`

SQLite WAL and SHM files live next to the database.

### Linux Database

With XDG data configured:

`$XDG_DATA_HOME/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`

Fallback:

`~/.local/share/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`

### Preferences

Windows:

`%APPDATA%\TurkuazLabs\TelefonRehberi\config\preferences.yml`

Linux:

`$XDG_CONFIG_HOME/turkuazlabs/telefon-rehberi/preferences.yml`

Fallback:

`~/.config/turkuazlabs/telefon-rehberi/preferences.yml`

## Mobile Sync Security

- LAN sync is disabled by default.
- It is intended for trusted local networks only.
- Android stores the sync token using Android Keystore + AES/GCM.
- iOS stores the sync token in Keychain.
- Android cleartext sync traffic is restricted to local-network endpoints.
- Android backup rules exclude sync identity and credential preferences.
- Desktop sync-token creation uses owner-only permissions on POSIX systems and publishes completed token files without a partial-content exposure window.
- The default HTTP request-body limit is 2 MiB.
- Bearer-token comparison is constant-time.

See [Mobile Sync API](./docs/MOBILE_SYNC_API.md) for protocol details.

## Backup Safety

SQLite backups are created as transaction-consistent snapshots instead of raw database-file copies:

- `VACUUM INTO`
- `PRAGMA integrity_check`
- temporary `.partial-*` snapshot followed by safe final-file replacement

Backups preserve contacts as well as groups, tags, history, smart lists and Trash data.

Windows backup directory:

`%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Yedekler\`

## Portable Runtime

The native launcher can manage the application runtime without modifying the system `JAVA_HOME` or `PATH`.

Packaged dependencies include:

- Eclipse Temurin JRE 17
- Xerial SQLite JDBC
- SLF4J API
- FlatLaf

## Architecture

The project follows a layered architecture:

`Controller -> Service -> Repository/Model -> Tool -> View -> Language`

| Layer | Responsibility |
| --- | --- |
| Controller | Receives UI/request events and delegates to services |
| Service | Business rules and application workflows |
| Repository / Model | SQLite and persistent data operations |
| Tool | VCF/CSV, runtime, assets, HTTP and external-system adapters |
| View | Swing / FlatLaf user interface |
| Language | Central user-visible text catalog |
| Config | Technical defaults and shared configuration |

## Repository Layout

| Path | Purpose |
| --- | --- |
| `src/main/java/` | Java desktop application |
| `src/test/java/` | Java quality-gate tests |
| `launcher/native/` | Go-based Windows/Linux native launcher |
| `mobile/android/` | Android companion client |
| `mobile/ios/` | iOS companion client |
| `packaging/windows/` | Inno Setup packaging |
| `packaging/linux/` | Linux installer and desktop entry |
| `config/` | Central application/launcher configuration |
| `tools/` | Build, test and release scripts |
| `docs/` | Technical documentation |
| `updates/` | Update-manifest template |
| `.github/workflows/` | CI and release automation |

## Build and Quality Gates

CI validates:

- Java 17 compilation and SQLite runtime quality gate
- Turkish/English desktop product-name and core message-catalog localization guards
- WAL-safe backup behavior
- `sync_uuid` and mobile identity idempotency
- history retention
- request-body limits
- user-preference round trips
- Go launcher/native builds
- real Windows Inno Setup silent install/uninstall smoke tests
- Linux native release build
- Android API 36 debug APK build
- iOS simulator build

## v2.38.0 Highlights

v2.38.0 expands Turkuaz PhoneBook from the v2.37 data-safety foundation into a bilingual, internationally presented release line:

- repository/global product identity moved to **Turkuaz PhoneBook** while preserving technical compatibility identifiers
- language-neutral **Turkuaz** branding and text-free application logo
- Turkish/English system-locale desktop message catalog
- localized contact-method and phone-country display without changing stored contact data
- localized Windows installer, native launcher, Linux desktop/install scripts, Android and iOS companion UI
- automatic phone-country default derived safely from the system locale
- Android LAN endpoint restrictions, credential masking and backup exclusions
- iOS Keychain write failures surfaced instead of being silently ignored
- desktop sync-token file permission and publication hardening
- launcher release line advanced to **2.5.0**

See [CHANGELOG.md](./CHANGELOG.md) for the full release history.

## Documentation

- [Turkce README](./README.tr.md)
- [CHANGELOG](./CHANGELOG.md)
- [Windows Installer](./docs/WINDOWS_INSTALLER.md)
- [User Data Storage](./docs/USER_DATA_STORAGE.md)
- [Mobile Sync API](./docs/MOBILE_SYNC_API.md)
- [Security](./SECURITY.md)
- [Third Party Notices](./THIRD_PARTY_NOTICES.md)
- [GitHub / Release Setup](./GITHUB_SETUP.md)

## License

This repository is public, but public visibility does **not** automatically make the code open source. Usage, copying, modification and redistribution are governed by [LICENSE](./LICENSE).

## TurkuazLabs

Developed by [TurkuazLabs](https://github.com/TurkuazLabs).
