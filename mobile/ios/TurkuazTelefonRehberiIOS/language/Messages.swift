// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/language/Messages.swift
// # 📌 Amac: iOS mobil istemcide gorunen mesajlari sistem diline gore merkezi olarak tutar.
// # 📌 Language - Swift
// # Version: 1.4.0
// # Aciklama: Turkce sistemlerde Turkce, diger sistemlerde Ingilizce ekran, baglanti, izin ve senkron metinleri kullanir.
// # Bagimli Oldugu Katman: Language
import Foundation

enum Messages {
    private static var isTurkish: Bool {
        Locale.current.language.languageCode?.identifier.lowercased() == "tr"
    }

    private static func text(_ turkish: String, _ english: String) -> String {
        isTurkish ? turkish : english
    }

    static var title: String { text("Turkuaz Telefon Rehberi", "Turkuaz PhoneBook") }
    static var connectionSection: String { text("PC Baglantisi", "PC Connection") }
    static var syncSection: String { text("Senkron", "Sync") }
    static var statusSection: String { text("Durum", "Status") }
    static let serverUrlHint = "http://192.168.1.10:8787"
    static var tokenHint: String { text("Senkron tokeni", "Sync token") }
    static var testConnectionButton: String { text("Baglantiyi Test Et", "Test Connection") }
    static var pullButton: String { text("PC Rehberini iPhone'a Al", "Get PC Contacts on iPhone") }
    static var pushButton: String { text("iPhone Rehberini PC'ye Gonder", "Send iPhone Contacts to PC") }
    static var ready: String { text("Hazir", "Ready") }
    static var working: String { text("Islem yapiliyor...", "Working...") }
    static var connectionOk: String { text("PC baglantisi basarili.", "PC connection successful.") }
    static var serverRequired: String { text("PC adresi bos olamaz.", "PC address cannot be empty.") }
    static var tokenRequired: String { text("Senkron tokeni bos olamaz.", "Sync token cannot be empty.") }
    static var contactsPermissionRequired: String {
        text("Rehber izni verilmeden senkron yapilamaz.", "Contacts permission is required for sync.")
    }
    static var trustedLanHint: String {
        text(
            "PC ve iPhone ayni guvenilir Wi-Fi/LAN aginda olmali.",
            "The PC and iPhone must be on the same trusted Wi-Fi/LAN network."
        )
    }
    static var invalidPcAddress: String { text("Gecersiz PC adresi.", "Invalid PC address.") }
    static var invalidPcResponse: String { text("PC gecersiz cevap dondurdu.", "The PC returned an invalid response.") }
    static var createdContactNotFound: String {
        text("Olusturulan iOS kisi kaydi bulunamadi.", "The created iOS contact could not be found.")
    }

    static func pullResult(added: Int, skipped: Int) -> String {
        if isTurkish {
            return "\(added) PC kaydi iPhone'a eklendi, \(skipped) mevcut kayit atlandi."
        }
        return "\(added) PC contacts added to iPhone, \(skipped) existing contacts skipped."
    }

    static func pushResult(count: Int) -> String {
        if isTurkish {
            return "\(count) iPhone kaydi PC'ye gonderildi."
        }
        return "\(count) iPhone contacts sent to the PC."
    }

    static func httpError(status: Int, body: String) -> String {
        "HTTP \(status): \(body)"
    }

    static func error(_ detail: String) -> String {
        text("Hata: \(detail)", "Error: \(detail)")
    }
}
