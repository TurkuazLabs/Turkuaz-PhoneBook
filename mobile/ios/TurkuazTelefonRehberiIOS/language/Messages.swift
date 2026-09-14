// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/language/Messages.swift
// # 📌 Amac: iOS mobil istemcide gorunen tum mesajlari merkezi olarak tutar.
// # 📌 Language - Swift
// # Version: 1.3.0
// # Aciklama: Ekran, baglanti, izin ve iki yonlu senkron durum metinlerini tanimlar.
// # Bagimli Oldugu Katman: Language
import Foundation

enum Messages {
    static let title = "Turkuaz Telefon Rehberi"
    static let connectionSection = "PC Baglantisi"
    static let syncSection = "Senkron"
    static let statusSection = "Durum"
    static let serverUrlHint = "http://192.168.1.10:8787"
    static let tokenHint = "Senkron tokeni"
    static let testConnectionButton = "Baglantiyi Test Et"
    static let pullButton = "PC Rehberini iPhone'a Al"
    static let pushButton = "iPhone Rehberini PC'ye Gonder"
    static let ready = "Hazir"
    static let working = "Islem yapiliyor..."
    static let connectionOk = "PC baglantisi basarili."
    static let serverRequired = "PC adresi bos olamaz."
    static let tokenRequired = "Senkron tokeni bos olamaz."
    static let contactsPermissionRequired = "Rehber izni verilmeden senkron yapilamaz."
    static let trustedLanHint = "PC ve iPhone ayni guvenilir Wi-Fi/LAN aginda olmali."
    static let invalidPcAddress = "Gecersiz PC adresi."
    static let invalidPcResponse = "PC gecersiz cevap dondurdu."
    static let createdContactNotFound = "Olusturulan iOS kisi kaydi bulunamadi."

    static func pullResult(added: Int, skipped: Int) -> String {
        "\(added) PC kaydi iPhone'a eklendi, \(skipped) mevcut kayit atlandi."
    }

    static func pushResult(count: Int) -> String {
        "\(count) iPhone kaydi PC'ye gonderildi."
    }

    static func httpError(status: Int, body: String) -> String {
        "HTTP \(status): \(body)"
    }

    static func error(_ detail: String) -> String {
        "Hata: \(detail)"
    }
}
