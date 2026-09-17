// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/services/SyncService.swift
// # 📌 Amac: iOS ile masaustu arasindaki iki yonlu rehber senkronizasyonu is kurallarini yonetir.
// # 📌 Service - Swift
// Version: 2.38.1
// # 📌 Aciklama: Kalici sync UUID mappingi, duplicate-safe pull, idempotent push, Keychain hata propagasyonu ve acik hata mesajli guvenli LAN endpoint dogrulamasini uygular.
// # 📌 Bagimli Oldugu Katman: Service | Repository | Tool | Model | Language
import Foundation

@MainActor
final class SyncService {
    private let contactsRepository: DeviceContactRepository
    private let settingsRepository: SettingsRepository
    private let desktopApiTool: DesktopApiTool
    private let lanEndpointTool: LanEndpointTool

    init(
        contactsRepository: DeviceContactRepository,
        settingsRepository: SettingsRepository,
        desktopApiTool: DesktopApiTool,
        lanEndpointTool: LanEndpointTool = LanEndpointTool()
    ) {
        self.contactsRepository = contactsRepository
        self.settingsRepository = settingsRepository
        self.desktopApiTool = desktopApiTool
        self.lanEndpointTool = lanEndpointTool
    }

    func serverUrl() -> String { settingsRepository.serverUrl() }
    func token() -> String { settingsRepository.token() }

    func saveConnection(serverUrl: String, token: String) throws {
        let normalizedUrl: String
        do {
            normalizedUrl = try lanEndpointTool.normalizeBaseUrl(serverUrl)
        } catch LanEndpointError.required {
            throw SyncError.validation(Messages.serverRequired)
        } catch LanEndpointError.cleartextLocalOnly {
            throw SyncError.validation(Messages.serverHttpLocalOnly)
        } catch {
            throw SyncError.validation(Messages.serverUrlInvalid)
        }
        if token.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            throw SyncError.validation(Messages.tokenRequired)
        }
        try settingsRepository.saveConnection(serverUrl: normalizedUrl, token: token)
    }

    func testConnection() async throws {
        try requireSavedConnection()
        try await desktopApiTool.test(baseUrl: settingsRepository.serverUrl(), token: settingsRepository.token())
    }

    func pullDesktopContacts() async throws -> PullResult {
        try requireSavedConnection()
        try await contactsRepository.ensureAccess()
        let desktopContacts = try await desktopApiTool.fetchContacts(baseUrl: settingsRepository.serverUrl(), token: settingsRepository.token())
        var added = 0
        var skipped = 0
        for contact in desktopContacts {
            let mappedNativeId = settingsRepository.nativeId(forSyncUuid: contact.syncUuid)
            let result = try contactsRepository.ensureDesktopContact(contact, preferredContactId: mappedNativeId)
            if !contact.syncUuid.isEmpty { settingsRepository.saveSyncMapping(nativeContactId: result.contactId, syncUuid: contact.syncUuid) }
            if result.created { added += 1 } else { skipped += 1 }
        }
        return PullResult(added: added, skipped: skipped)
    }

    func pushDeviceContacts() async throws -> Int {
        try requireSavedConnection()
        try await contactsRepository.ensureAccess()
        let deviceContacts = try contactsRepository.findAll()
        let deviceId = settingsRepository.deviceId()
        for contact in deviceContacts {
            let currentSyncUuid = settingsRepository.syncUuid(forNativeId: contact.id)
            let canonicalSyncUuid = try await desktopApiTool.pushContact(
                baseUrl: settingsRepository.serverUrl(),
                token: settingsRepository.token(),
                deviceId: deviceId,
                syncUuid: currentSyncUuid,
                contact: contact
            )
            if !canonicalSyncUuid.isEmpty { settingsRepository.saveSyncMapping(nativeContactId: contact.id, syncUuid: canonicalSyncUuid) }
        }
        return deviceContacts.count
    }

    private func requireSavedConnection() throws {
        try saveConnection(serverUrl: settingsRepository.serverUrl(), token: settingsRepository.token())
    }
}

enum SyncError: LocalizedError {
    case validation(String)
    var errorDescription: String? {
        switch self { case let .validation(message): return message }
    }
}
