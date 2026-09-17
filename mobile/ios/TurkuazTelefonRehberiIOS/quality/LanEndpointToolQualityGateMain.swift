// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/quality/LanEndpointToolQualityGateMain.swift
// # 📌 Amac: iOS LAN endpoint dogrulamasinin izin verilen ve reddedilen ATS uyumlu adres sinirlarini regression gate olarak test eder.
// # 📌 Tool - Swift
// Version: 1.0.2
// # 📌 Aciklama: Private/loopback HTTP, unqualified/.local host ve HTTPS hedeflerini kabul eder; public/custom-domain cleartext HTTP, gecersiz port ve hatali URL bicimlerini reddeder.
// # 📌 Bagimli Oldugu Katman: Tool
import Foundation

@main
enum LanEndpointToolQualityGateMain {
    static func main() throws {
        let tool = LanEndpointTool()

        try expectSuccess(tool, "http://127.0.0.1:8787")
        try expectSuccess(tool, "http://10.0.0.4:8787")
        try expectSuccess(tool, "http://172.16.2.3:8787")
        try expectSuccess(tool, "http://192.168.1.10:8787/")
        try expectSuccess(tool, "http://100.64.1.2:8787")
        try expectSuccess(tool, "http://desktop.local:8787")
        try expectSuccess(tool, "http://phonebook:8787")
        try expectSuccess(tool, "http://[::1]:8787")
        try expectSuccess(tool, "http://[fd00::1]:8787")
        try expectSuccess(tool, "http://192.168.1.10:65535")
        try expectSuccess(tool, "https://example.com")
        try expectSuccess(tool, "https://desktop.lan:8787")
        try expectSuccess(tool, "https://router.home.arpa:8787")

        try expectFailure(tool, "")
        try expectFailure(tool, "ftp://192.168.1.10")
        try expectFailure(tool, "http://8.8.8.8:8787")
        try expectFailure(tool, "http://example.com:8787")
        try expectFailure(tool, "http://desktop.lan:8787")
        try expectFailure(tool, "http://router.home.arpa:8787")
        try expectFailure(tool, "http://localhost.localdomain:8787")
        try expectFailure(tool, "http://user@desktop.local:8787")
        try expectFailure(tool, "http://192.168.1.10:8787/api")
        try expectFailure(tool, "http://192.168.1.10:8787?x=1")
        try expectFailure(tool, "http://192.168.1.10:0")
        try expectFailure(tool, "http://192.168.1.10:65536")

        let normalized = try tool.normalizeBaseUrl("  http://192.168.1.10:8787///  ")
        guard normalized == "http://192.168.1.10:8787" else {
            throw QualityGateError.failed("Trailing slash normalization failed: \(normalized)")
        }

        print("iOS LAN endpoint quality gate: PASS")
    }

    private static func expectSuccess(_ tool: LanEndpointTool, _ value: String) throws {
        do {
            _ = try tool.normalizeBaseUrl(value)
        } catch {
            throw QualityGateError.failed("Expected success for \(value), got \(error)")
        }
    }

    private static func expectFailure(_ tool: LanEndpointTool, _ value: String) throws {
        do {
            _ = try tool.normalizeBaseUrl(value)
            throw QualityGateError.failed("Expected failure for \(value)")
        } catch let error as QualityGateError {
            throw error
        } catch {
            return
        }
    }
}

enum QualityGateError: Error {
    case failed(String)
}
