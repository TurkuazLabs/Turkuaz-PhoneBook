# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/README.md
# 📌 Amac: iOS SwiftUI companion client XcodeGen, signing, permissions and synchronization usage documentation.
# 📌 Documentation - Markdown
# Version: 2.38.1
# Aciklama: iOS 18+ Contacts framework companion client setup, localization, endpoint validation and trusted-LAN synchronization steps in English.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

# Turkuaz PhoneBook — iOS Companion

[Turkce README](./README.tr.md)

## Technology

- Swift 6
- SwiftUI
- Contacts framework
- iOS 18+
- XcodeGen

## Generate the Xcode Project

With XcodeGen installed on macOS, run in this directory:

`xcodegen generate`

Then open `TurkuazTelefonRehberiIOS.xcodeproj` in Xcode.

## Real iPhone Build

To install on a physical device, select your Apple Development Team under Xcode **Signing & Capabilities**. If a different bundle identifier is required, update `project.yml` and regenerate the Xcode project.

## First Connection

1. Open **Mobile Sync** in the Turkuaz PhoneBook desktop application.
2. Enter the PC address and sync token in the iPhone client.
3. Select **Test Connection**.
4. Grant Contacts access when iOS asks for it.

## Choose a Direction

**Get PC Contacts on iPhone** adds desktop SQLite contacts to the iPhone system address book.

**Send iPhone Contacts to PC** reads iPhone contacts and sends them to the desktop SQLite database.

## Localization

English is the default/fallback product and permission language. Turkish product/permission strings are provided through `resources/tr.lproj/InfoPlist.strings`. Sync-screen messages follow the preferred UI language.

## Security

The application declares Local Network and Contacts permission descriptions in `Info.plist`. HTTP synchronization is accepted only for loopback, private/link-local IP ranges and local LAN hostnames such as `.local`, `.lan` and `.home.arpa`; remote/public endpoints must use HTTPS. The same endpoint policy is covered by `tools/test-ios-endpoint.sh` in Build and Release CI.

Use LAN synchronization only on a trusted Wi-Fi/LAN shared by the desktop and iPhone, and do not expose the desktop synchronization port directly to the Internet.
