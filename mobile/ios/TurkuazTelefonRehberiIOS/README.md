# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/README.md
# 📌 Amac: iOS SwiftUI istemcisinin XcodeGen, signing, izin ve senkron kullanimini aciklar.
# 📌 Documentation - Markdown
# Version: 2.37.0
# Aciklama: iOS Contacts framework istemcisini simulator veya gercek cihaz icin hazirlama adimlarini tanimlar.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

# iOS Mobil Istemci

## Teknoloji

- Swift 6
- SwiftUI
- Contacts framework
- iOS 18+
- XcodeGen

## Xcode projesini uret

macOS'ta XcodeGen kurulu iken bu klasorde:

`xcodegen generate`

Sonra `TurkuazTelefonRehberiIOS.xcodeproj` Xcode ile acilir.

## Gercek iPhone build

Gercek cihaza yuklemek icin Xcode Signing & Capabilities bolumunde kendi Apple Development Team secilmelidir. Bundle ID gerekiyorsa `project.yml` icinden degistirilir ve proje yeniden uretilir.

## Ilk baglanti

1. Windows masaustu uygulamasinda `Mobil Senkron` penceresini ac.
2. PC adresini ve tokeni iPhone uygulamasina gir.
3. `Baglantiyi Test Et` sec.
4. Contacts izni istendiginde izin ver.

## Yonu sec

`PC Rehberini iPhone'a Al`: PC SQLite rehberini iPhone sistem rehberine ekler.

`iPhone Rehberini PC'ye Gonder`: iPhone rehberini okuyup PC SQLite veritabanina aktarir.

## Guvenlik

Local Network ve Contacts izin aciklamalari `Info.plist` icindedir. v2.37.0 local HTTP kullandigi icin yalnizca guvenilir LAN/Wi-Fi aginda kullan.
