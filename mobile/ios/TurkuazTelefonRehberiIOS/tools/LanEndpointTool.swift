// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/tools/LanEndpointTool.swift
// # 📌 Amac: iOS mobil senkron sunucu adresini guvenli HTTP/HTTPS ve LAN kurallarina gore dogrular.
// # 📌 Tool - Swift
// Version: 1.0.0
// # 📌 Aciklama: Cleartext HTTP'yi yalniz loopback, private/link-local IP ve yerel hostname hedeflerine sinirlar; uzak hedeflerde HTTPS gerektirir.
// # 📌 Bagimli Oldugu Katman: Tool
import Foundation

struct LanEndpointTool {
    func normalizeBaseUrl(_ value: String) throws -> String {
        var normalized = value.trimmingCharacters(in: .whitespacesAndNewlines)
        while normalized.hasSuffix("/") {
            normalized.removeLast()
        }
        guard !normalized.isEmpty else { throw LanEndpointError.required }
        guard let components = URLComponents(string: normalized), components.url != nil else {
            throw LanEndpointError.invalidAddress
        }

        let scheme = (components.scheme ?? "").lowercased()
        guard scheme == "http" || scheme == "https" else { throw LanEndpointError.invalidAddress }
        guard let rawHost = components.host, !rawHost.isEmpty else { throw LanEndpointError.invalidAddress }
        guard components.user == nil,
              components.password == nil,
              components.query == nil,
              components.fragment == nil,
              components.path.isEmpty || components.path == "/" else {
            throw LanEndpointError.invalidAddress
        }

        if scheme == "http" && !isLocalOrPrivateHost(rawHost) {
            throw LanEndpointError.cleartextLocalOnly
        }
        return normalized
    }

    func isLocalOrPrivateHost(_ value: String) -> Bool {
        var host = value.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        if host.hasPrefix("[") && host.hasSuffix("]") {
            host.removeFirst()
            host.removeLast()
        }
        if let zoneIndex = host.firstIndex(of: "%") {
            host = String(host[..<zoneIndex])
        }

        if host == "localhost" || host == "localhost.localdomain" { return true }
        if host.hasSuffix(".local") || host.hasSuffix(".lan") || host.hasSuffix(".home.arpa") { return true }
        if !host.contains(".") && !host.contains(":") { return true }
        if isPrivateIpv4(host) { return true }
        if host.hasPrefix("::ffff:") {
            return isPrivateIpv4(String(host.dropFirst("::ffff:".count)))
        }
        return isPrivateIpv6(host)
    }

    private func isPrivateIpv4(_ host: String) -> Bool {
        let parts = host.split(separator: ".", omittingEmptySubsequences: false)
        guard parts.count == 4 else { return false }
        var octets: [Int] = []
        for part in parts {
            guard !part.isEmpty, part.count <= 3, let value = Int(part), (0...255).contains(value) else { return false }
            octets.append(value)
        }
        return octets[0] == 10
            || octets[0] == 127
            || (octets[0] == 169 && octets[1] == 254)
            || (octets[0] == 172 && (16...31).contains(octets[1]))
            || (octets[0] == 192 && octets[1] == 168)
            || (octets[0] == 100 && (64...127).contains(octets[1]))
    }

    private func isPrivateIpv6(_ host: String) -> Bool {
        if host == "::1" { return true }
        if host.hasPrefix("fc") || host.hasPrefix("fd") { return true }
        guard host.count >= 3, host.hasPrefix("fe") else { return false }
        let third = host[host.index(host.startIndex, offsetBy: 2)]
        return third == "8" || third == "9" || third == "a" || third == "b"
    }
}

enum LanEndpointError: Error, Equatable {
    case required
    case invalidAddress
    case cleartextLocalOnly
}
