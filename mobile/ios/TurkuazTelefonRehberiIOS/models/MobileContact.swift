// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/models/MobileContact.swift
// # 📌 Amac: iOS ve masaustu arasinda tasinan genisletilmis kisi kaydini temsil eder.
// # 📌 Model - Swift
// # Version: 2.37.0
// # Aciklama: Native contact identifier ile platformlar arasi kalici sync UUID kimligini Codable olarak tasir.
// # Bagimli Oldugu Katman: Model
import Foundation

struct MobileContactMethod: Codable, Hashable {
    static let phoneKind = "PHONE"
    static let emailKind = "EMAIL"
    static let mobileLabel = "Cep"
    static let otherLabel = "Diger"
    static let workLabel = "Is"
    static let homeLabel = "Ev"
    static let emailLabel = "E-posta"
    static let phoneLabel = "Telefon"

    let kind: String
    let label: String
    let value: String
    let primary: Bool
    let position: Int
}

struct MobileContact: Identifiable, Codable {
    let id: String
    let syncUuid: String
    let name: String
    let phone: String
    let phoneSecondary: String
    let phoneWork: String
    let email: String
    let emailSecondary: String
    let company: String
    let jobTitle: String
    let birthday: String
    let website: String
    let category: String
    let address: String
    let city: String
    let district: String
    let postalCode: String
    let country: String
    let notes: String
    let favorite: Bool
    let phones: [MobileContactMethod]
    let emails: [MobileContactMethod]
    let photoBase64: String

    enum CodingKeys: String, CodingKey {
        case id, name, phone, email, company, birthday, website, category, address, city, district, country, notes, favorite, phones, emails
        case syncUuid = "sync_uuid"
        case phoneSecondary = "phone_secondary"
        case phoneWork = "phone_work"
        case emailSecondary = "email_secondary"
        case jobTitle = "job_title"
        case postalCode = "postal_code"
        case photoBase64 = "photo_base64"
    }

    init(
        id: String,
        syncUuid: String = "",
        name: String,
        phone: String = "",
        phoneSecondary: String = "",
        phoneWork: String = "",
        email: String = "",
        emailSecondary: String = "",
        company: String = "",
        jobTitle: String = "",
        birthday: String = "",
        website: String = "",
        category: String = "",
        address: String = "",
        city: String = "",
        district: String = "",
        postalCode: String = "",
        country: String = "",
        notes: String = "",
        favorite: Bool = false,
        phones: [MobileContactMethod] = [],
        emails: [MobileContactMethod] = [],
        photoBase64: String = ""
    ) {
        self.id = id
        self.syncUuid = syncUuid
        self.name = name
        self.phone = phone
        self.phoneSecondary = phoneSecondary
        self.phoneWork = phoneWork
        self.email = email
        self.emailSecondary = emailSecondary
        self.company = company
        self.jobTitle = jobTitle
        self.birthday = birthday
        self.website = website
        self.category = category
        self.address = address
        self.city = city
        self.district = district
        self.postalCode = postalCode
        self.country = country
        self.notes = notes
        self.favorite = favorite
        self.phones = phones.isEmpty ? Self.legacyPhones(phone: phone, secondary: phoneSecondary, work: phoneWork) : phones
        self.emails = emails.isEmpty ? Self.legacyEmails(email: email, secondary: emailSecondary) : emails
        self.photoBase64 = photoBase64
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        if let numericId = try? container.decode(Int64.self, forKey: .id) { id = String(numericId) }
        else { id = try container.decode(String.self, forKey: .id) }
        syncUuid = try container.decodeIfPresent(String.self, forKey: .syncUuid) ?? ""
        name = try container.decodeIfPresent(String.self, forKey: .name) ?? ""
        phone = try container.decodeIfPresent(String.self, forKey: .phone) ?? ""
        phoneSecondary = try container.decodeIfPresent(String.self, forKey: .phoneSecondary) ?? ""
        phoneWork = try container.decodeIfPresent(String.self, forKey: .phoneWork) ?? ""
        email = try container.decodeIfPresent(String.self, forKey: .email) ?? ""
        emailSecondary = try container.decodeIfPresent(String.self, forKey: .emailSecondary) ?? ""
        company = try container.decodeIfPresent(String.self, forKey: .company) ?? ""
        jobTitle = try container.decodeIfPresent(String.self, forKey: .jobTitle) ?? ""
        birthday = try container.decodeIfPresent(String.self, forKey: .birthday) ?? ""
        website = try container.decodeIfPresent(String.self, forKey: .website) ?? ""
        category = try container.decodeIfPresent(String.self, forKey: .category) ?? ""
        address = try container.decodeIfPresent(String.self, forKey: .address) ?? ""
        city = try container.decodeIfPresent(String.self, forKey: .city) ?? ""
        district = try container.decodeIfPresent(String.self, forKey: .district) ?? ""
        postalCode = try container.decodeIfPresent(String.self, forKey: .postalCode) ?? ""
        country = try container.decodeIfPresent(String.self, forKey: .country) ?? ""
        notes = try container.decodeIfPresent(String.self, forKey: .notes) ?? ""
        favorite = try container.decodeIfPresent(Bool.self, forKey: .favorite) ?? false
        let decodedPhones = try container.decodeIfPresent([MobileContactMethod].self, forKey: .phones) ?? []
        let decodedEmails = try container.decodeIfPresent([MobileContactMethod].self, forKey: .emails) ?? []
        phones = decodedPhones.isEmpty ? Self.legacyPhones(phone: phone, secondary: phoneSecondary, work: phoneWork) : decodedPhones
        emails = decodedEmails.isEmpty ? Self.legacyEmails(email: email, secondary: emailSecondary) : decodedEmails
        photoBase64 = try container.decodeIfPresent(String.self, forKey: .photoBase64) ?? ""
    }

    private static func legacyPhones(phone: String, secondary: String, work: String) -> [MobileContactMethod] {
        var out: [MobileContactMethod] = []
        if !phone.isEmpty { out.append(.init(kind: MobileContactMethod.phoneKind, label: MobileContactMethod.mobileLabel, value: phone, primary: true, position: out.count)) }
        if !secondary.isEmpty { out.append(.init(kind: MobileContactMethod.phoneKind, label: MobileContactMethod.otherLabel, value: secondary, primary: out.isEmpty, position: out.count)) }
        if !work.isEmpty { out.append(.init(kind: MobileContactMethod.phoneKind, label: MobileContactMethod.workLabel, value: work, primary: out.isEmpty, position: out.count)) }
        return out
    }

    private static func legacyEmails(email: String, secondary: String) -> [MobileContactMethod] {
        var out: [MobileContactMethod] = []
        if !email.isEmpty { out.append(.init(kind: MobileContactMethod.emailKind, label: MobileContactMethod.emailLabel, value: email, primary: true, position: out.count)) }
        if !secondary.isEmpty { out.append(.init(kind: MobileContactMethod.emailKind, label: MobileContactMethod.workLabel, value: secondary, primary: out.isEmpty, position: out.count)) }
        return out
    }
}

struct ContactEnvelope: Codable { let contacts: [MobileContact] }
struct PullResult { let added: Int; let skipped: Int }
struct EnsureContactResult { let contactId: String; let created: Bool }
