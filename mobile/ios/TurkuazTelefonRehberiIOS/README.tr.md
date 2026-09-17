# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/README.tr.md
# 📌 Amac: iOS SwiftUI istemcisinin XcodeGen, signing, izin ve senkron kullanimini Turkce aciklar.
# 📌 Documentation - Markdown
# Version: 2.38.1
# Aciklama: iOS 18+ Contacts framework istemcisini simulator veya gercek cihaz icin hazirlama, yerellestirme, endpoint dogrulama ve guvenilir LAN senkron adimlarini Turkce tanimlar.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

# Turkuaz Telefon Rehberi — iOS Mobil Istemci

[English README](./README.md)

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

Gercek cihaza yuklemek icin Xcode **Signing & Capabilities** bolumunde kendi Apple Development Team secilmelidir. Bundle ID gerekiyorsa `project.yml` icinden degistirilir ve proje yeniden uretilir.

## Ilk baglanti

1. Masaustu uygulamasinda `Mobil Senkron` ekranini ac.
2. PC adresini ve tokeni iPhone uygulamasina gir.
3. `Baglantiyi Test Et` sec.
4. Contacts izni istendiginde izin ver.

## Yonu sec

`PC Rehberini iPhone'a Al`: PC SQLite rehberini iPhone sistem rehberine ekler.

`iPhone Rehberini PC'ye Gonder`: iPhone rehberini okuyup PC SQLite veritabanina aktarir.

## Yerellestirme

Varsayilan/fallback urun ve izin dili Ingilizcedir. Turkce urun/izin metinleri `resources/tr.lproj/InfoPlist.strings` ile saglanir. Senkron ekran mesajlari tercih edilen UI dilini takip eder.

## Guvenlik

Local Network ve Contacts izin aciklamalari `Info.plist` icindedir. HTTP senkron adresleri yalniz loopback, private/link-local IP araliklari ve `.local`, `.lan`, `.home.arpa` gibi yerel LAN hostname hedefleri icin kabul edilir; public/uzak hedeflerde HTTPS zorunludur. Ayni endpoint politikasi Build ve Release CI icinde `tools/test-ios-endpoint.sh` ile regression gate olarak test edilir.

LAN senkronunu yalniz bilgisayar ve iPhone'un ayni guvenilir Wi-Fi/LAN aginda oldugu durumda kullanin ve masaustu senkron portunu dogrudan Internet'e acmayin.
