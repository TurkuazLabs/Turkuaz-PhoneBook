// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/repositories/SettingsRepository.swift
// # 📌 Amac: iOS mobil senkron baglanti ayarlarini, Keychain tokenini ve sync mappinglerini kalici saklar.
// # 📌 Repository - Swift
// # Version: 2.37.2
// # Aciklama: URL/cihaz/mapping verisini UserDefaults'ta, tokeni Keychain'de tutar; Swift 6 actor izolasyonu uygular.
// # Bagimli Oldugu Katman: Repository | Config | Tool
import Foundation

@MainActor
final class SettingsRepository {
    private let defaults = UserDefaults.standard
    private let keychain = KeychainTokenTool()

    func saveConnection(serverUrl: String, token: String) {
        defaults.set(normalizeUrl(serverUrl), forKey: MobileConfig.preferencesServerUrl)
        keychain.save(token.trimmingCharacters(in: .whitespacesAndNewlines))
        defaults.removeObject(forKey: MobileConfig.preferencesTokenLegacy)
    }

    func serverUrl() -> String { defaults.string(forKey: MobileConfig.preferencesServerUrl) ?? "" }

    func token() -> String {
        let secure = keychain.load()
        if !secure.isEmpty { return secure }
        let legacy = defaults.string(forKey: MobileConfig.preferencesTokenLegacy) ?? ""
        if !legacy.isEmpty {
            keychain.save(legacy)
            defaults.removeObject(forKey: MobileConfig.preferencesTokenLegacy)
        }
        return legacy
    }

    func deviceId() -> String {
        if let existing = defaults.string(forKey: MobileConfig.preferencesDeviceId), !existing.isEmpty { return existing }
        let created = UUID().uuidString.lowercased()
        defaults.set(created, forKey: MobileConfig.preferencesDeviceId)
        return created
    }

    func saveSyncMapping(nativeContactId: String, syncUuid: String) {
        let nativeId = nativeContactId.trimmingCharacters(in: .whitespacesAndNewlines)
        let uuid = syncUuid.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !nativeId.isEmpty, !uuid.isEmpty else { return }
        let nativeKey = MobileConfig.preferencesSyncMapNativePrefix + nativeId
        let uuidKey = MobileConfig.preferencesSyncMapUuidPrefix + uuid
        let previousUuid = defaults.string(forKey: nativeKey) ?? ""
        let previousNativeId = defaults.string(forKey: uuidKey) ?? ""
        if !previousUuid.isEmpty && previousUuid != uuid {
            defaults.removeObject(forKey: MobileConfig.preferencesSyncMapUuidPrefix + previousUuid)
        }
        if !previousNativeId.isEmpty && previousNativeId != nativeId {
            defaults.removeObject(forKey: MobileConfig.preferencesSyncMapNativePrefix + previousNativeId)
        }
        defaults.set(uuid, forKey: nativeKey)
        defaults.set(nativeId, forKey: uuidKey)
    }

    func syncUuid(forNativeId nativeContactId: String) -> String {
        defaults.string(forKey: MobileConfig.preferencesSyncMapNativePrefix + nativeContactId) ?? ""
    }

    func nativeId(forSyncUuid syncUuid: String) -> String {
        defaults.string(forKey: MobileConfig.preferencesSyncMapUuidPrefix + syncUuid) ?? ""
    }

    private func normalizeUrl(_ value: String) -> String {
        var result = value.trimmingCharacters(in: .whitespacesAndNewlines)
        while result.hasSuffix("/") { result.removeLast() }
        return result
    }
}
