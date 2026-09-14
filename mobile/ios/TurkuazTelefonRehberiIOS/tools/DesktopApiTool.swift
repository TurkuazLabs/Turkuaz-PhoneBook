// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/tools/DesktopApiTool.swift
// # 📌 Amac: iOS istemcinin masaustu LAN senkron API'siyle HTTP iletisimi yapan adaptorudur.
// # 📌 Tool - Swift
// # Version: 2.37.1
// # Aciklama: Sinirsiz telefon/e-posta ve profil fotografi dahil status, listeleme ve import isteklerini Swift 6 actor izolasyonuyla yapar.
// # Bagimli Oldugu Katman: Tool | Config | Model | Language
import Foundation

@MainActor
final class DesktopApiTool {
    func test(baseUrl: String, token: String) async throws {
        _ = try await request(url: baseUrl + MobileConfig.statusPath, token: token, method: MobileConfig.httpGet, body: nil)
    }

    func fetchContacts(baseUrl: String, token: String) async throws -> [MobileContact] {
        let data = try await request(url: baseUrl + MobileConfig.contactsPath, token: token, method: MobileConfig.httpGet, body: nil)
        return try JSONDecoder().decode(ContactEnvelope.self, from: data).contacts
    }

    func pushContact(baseUrl: String, token: String, deviceId: String, syncUuid: String, contact: MobileContact) async throws -> String {
        var components = URLComponents()
        components.queryItems = [
            URLQueryItem(name: MobileConfig.formName, value: contact.name),
            URLQueryItem(name: MobileConfig.formPhone, value: contact.phone),
            URLQueryItem(name: MobileConfig.formPhoneSecondary, value: contact.phoneSecondary),
            URLQueryItem(name: MobileConfig.formPhoneWork, value: contact.phoneWork),
            URLQueryItem(name: MobileConfig.formEmail, value: contact.email),
            URLQueryItem(name: MobileConfig.formEmailSecondary, value: contact.emailSecondary),
            URLQueryItem(name: MobileConfig.formPhones, value: encodeMethods(contact.phones)),
            URLQueryItem(name: MobileConfig.formEmails, value: encodeMethods(contact.emails)),
            URLQueryItem(name: MobileConfig.formPhotoBase64, value: contact.photoBase64),
            URLQueryItem(name: MobileConfig.formCompany, value: contact.company),
            URLQueryItem(name: MobileConfig.formJobTitle, value: contact.jobTitle),
            URLQueryItem(name: MobileConfig.formBirthday, value: contact.birthday),
            URLQueryItem(name: MobileConfig.formWebsite, value: contact.website),
            URLQueryItem(name: MobileConfig.formCategory, value: contact.category),
            URLQueryItem(name: MobileConfig.formAddress, value: contact.address),
            URLQueryItem(name: MobileConfig.formCity, value: contact.city),
            URLQueryItem(name: MobileConfig.formDistrict, value: contact.district),
            URLQueryItem(name: MobileConfig.formPostalCode, value: contact.postalCode),
            URLQueryItem(name: MobileConfig.formCountry, value: contact.country),
            URLQueryItem(name: MobileConfig.formNotes, value: contact.notes),
            URLQueryItem(name: MobileConfig.formFavorite, value: String(contact.favorite)),
            URLQueryItem(name: MobileConfig.formDeviceId, value: deviceId),
            URLQueryItem(name: MobileConfig.formExternalId, value: contact.id),
            URLQueryItem(name: MobileConfig.formSyncUuid, value: syncUuid)
        ]
        let body = components.percentEncodedQuery?.data(using: .utf8) ?? Data()
        let data = try await request(url: baseUrl + MobileConfig.importPath, token: token, method: MobileConfig.httpPost, body: body)
        let object = try JSONSerialization.jsonObject(with: data) as? [String: Any]
        return object?[MobileConfig.formSyncUuid] as? String ?? ""
    }

    private func encodeMethods(_ methods: [MobileContactMethod]) -> String {
        methods
            .filter { !$0.value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty }
            .map { method in
                [
                    encodeComponent(method.kind),
                    encodeComponent(method.label),
                    encodeComponent(method.value),
                    method.primary ? MobileConfig.methodPrimaryTrue : MobileConfig.methodPrimaryFalse,
                    String(method.position)
                ].joined(separator: MobileConfig.methodFieldSeparator)
            }
            .joined(separator: MobileConfig.methodRowSeparator)
    }

    private func encodeComponent(_ value: String) -> String {
        value.addingPercentEncoding(withAllowedCharacters: MobileConfig.methodComponentAllowedCharacters) ?? ""
    }

    private func request(url: String, token: String, method: String, body: Data?) async throws -> Data {
        guard let endpoint = URL(string: url) else { throw ApiError.invalidUrl }
        var request = URLRequest(url: endpoint, timeoutInterval: MobileConfig.requestTimeout)
        request.httpMethod = method
        request.setValue(MobileConfig.bearerPrefix + token, forHTTPHeaderField: MobileConfig.authorizationHeader)
        request.setValue(MobileConfig.contentTypeJson, forHTTPHeaderField: MobileConfig.acceptHeader)
        if let body {
            request.httpBody = body
            request.setValue(MobileConfig.contentTypeForm, forHTTPHeaderField: MobileConfig.contentTypeHeader)
        }
        let (data, response) = try await URLSession.shared.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse else { throw ApiError.invalidResponse }
        guard MobileConfig.httpSuccessRange.contains(httpResponse.statusCode) else {
            let text = String(data: data, encoding: .utf8) ?? ""
            throw ApiError.http(httpResponse.statusCode, text)
        }
        return data
    }
}

enum ApiError: LocalizedError {
    case invalidUrl
    case invalidResponse
    case http(Int, String)

    var errorDescription: String? {
        switch self {
        case .invalidUrl:
            return Messages.invalidPcAddress
        case .invalidResponse:
            return Messages.invalidPcResponse
        case let .http(status, body):
            return Messages.httpError(status: status, body: body)
        }
    }
}
