// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/tools/KeychainTokenTool.swift
// # 📌 Amac: iOS mobil senkron tokenini Keychain icinde guvenli saklar.
// # 📌 Tool - Swift
// # Version: 1.1.0
// # Aciklama: ThisDeviceOnly Keychain token okuma/yazma/silme islemlerini yapar ve yazma/silme hatalarini sessizce yutmak yerine cagiriciya iletir.
// # Bagimli Oldugu Katman: Tool | Config | Language
import Foundation
import Security

final class KeychainTokenTool {
    func save(_ value: String) throws {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: MobileConfig.keychainService,
            kSecAttrAccount as String: MobileConfig.keychainTokenAccount
        ]

        let deleteStatus = SecItemDelete(query as CFDictionary)
        guard deleteStatus == errSecSuccess || deleteStatus == errSecItemNotFound else {
            throw KeychainTokenError.writeFailed(deleteStatus)
        }
        if value.isEmpty { return }

        var add = query
        add[kSecValueData as String] = Data(value.utf8)
        add[kSecAttrAccessible as String] = kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        let status = SecItemAdd(add as CFDictionary, nil)
        guard status == errSecSuccess else {
            throw KeychainTokenError.writeFailed(status)
        }
    }

    func load() -> String {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: MobileConfig.keychainService,
            kSecAttrAccount as String: MobileConfig.keychainTokenAccount,
            kSecReturnData as String: true,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]
        var result: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        guard status == errSecSuccess, let data = result as? Data else { return "" }
        return String(data: data, encoding: .utf8) ?? ""
    }
}

enum KeychainTokenError: LocalizedError {
    case writeFailed(OSStatus)

    var errorDescription: String? {
        Messages.secureTokenStoreFailed
    }
}
