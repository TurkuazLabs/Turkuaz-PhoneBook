// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/config/MobileConfig.swift
// # 📌 Amac: iOS mobil istemcinin API ve kalici ayar sabitlerini merkezi olarak tutar.
// # 📌 Config - Swift
// # Version: 2.37.0
// # Aciklama: Genisletilmis contact form alanlari, endpoint, header, HTTP ve UserDefaults sabitlerini tanimlar.
// # Bagimli Oldugu Katman: Config
import Foundation

enum MobileConfig {
    static let preferencesServerUrl = "server_url"
    static let preferencesTokenLegacy = "sync_token"
    static let preferencesSyncMapNativePrefix = "sync_map_native_"
    static let preferencesSyncMapUuidPrefix = "sync_map_uuid_"
    static let keychainService = "com.turkuazlabs.telefonrehberi.sync"
    static let keychainTokenAccount = "sync_token"
    static let preferencesDeviceId = "device_id"
    static let statusPath = "/api/v1/status"
    static let contactsPath = "/api/v1/contacts"
    static let importPath = "/api/v1/import"
    static let authorizationHeader = "Authorization"
    static let bearerPrefix = "Bearer "
    static let contentTypeHeader = "Content-Type"
    static let contentTypeForm = "application/x-www-form-urlencoded; charset=UTF-8"
    static let acceptHeader = "Accept"
    static let contentTypeJson = "application/json"
    static let formName = "name"
    static let formPhone = "phone"
    static let formPhoneSecondary = "phone_secondary"
    static let formPhoneWork = "phone_work"
    static let formEmail = "email"
    static let formEmailSecondary = "email_secondary"
    static let formCompany = "company"
    static let formJobTitle = "job_title"
    static let formBirthday = "birthday"
    static let formWebsite = "website"
    static let formCategory = "category"
    static let formAddress = "address"
    static let formCity = "city"
    static let formDistrict = "district"
    static let formPostalCode = "postal_code"
    static let formCountry = "country"
    static let formNotes = "notes"
    static let formFavorite = "favorite"
    static let formPhones = "phones"
    static let formEmails = "emails"
    static let formPhotoBase64 = "photo_base64"
    static let formDeviceId = "device_id"
    static let formExternalId = "external_id"
    static let formSyncUuid = "sync_uuid"
    static let httpGet = "GET"
    static let httpPost = "POST"
    static let httpSuccessRange = 200..<300
    static let methodRowSeparator = "~"
    static let methodFieldSeparator = "|"
    static let methodPrimaryTrue = "1"
    static let methodPrimaryFalse = "0"
    static let methodComponentAllowedCharacters = CharacterSet.alphanumerics.union(CharacterSet(charactersIn: "-_."))
    static let requestTimeout: TimeInterval = 20
}
