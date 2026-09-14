// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/tools/KeychainTokenTool.swift
// # 📌 Amac: iOS mobil senkron tokenini Keychain icinde guvenli saklar.
// # 📌 Tool - Swift
// # Version: 1.0.0
// # Aciklama: Token okuma/yazma/silme islemlerini Security framework uzerinden yapar.
// # Bagimli Oldugu Katman: Tool | Config
import Foundation
import Security

final class KeychainTokenTool {
    func save(_ value: String) {
        let data = Data(value.utf8)
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: MobileConfig.keychainService,
            kSecAttrAccount as String: MobileConfig.keychainTokenAccount
        ]
        SecItemDelete(query as CFDictionary)
        if value.isEmpty { return }
        var add = query
        add[kSecValueData as String] = data
        add[kSecAttrAccessible as String] = kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        let status = SecItemAdd(add as CFDictionary, nil)
        if status != errSecSuccess { NSLog("Turkuaz Keychain token save error: %d", status) }
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
