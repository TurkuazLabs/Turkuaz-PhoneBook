// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/controllers/MainController.swift
// # 📌 Amac: SwiftUI olaylarini alir ve SyncService katmanina yonlendirir.
// # 📌 Controller - Swift
// # Version: 1.1.0
// # Aciklama: Kullanici baglanti ayarlarini, test ve iki yonlu senkron aksiyonlarini yonetir; is kurali barindirmaz.
// # Bagimli Oldugu Katman: Controller | Service | Language
import Foundation
import SwiftUI

@MainActor
final class MainController: ObservableObject {
    @Published var serverUrl = ""
    @Published var token = ""
    @Published var status = Messages.ready
    @Published var working = false

    private let service: SyncService

    init(service: SyncService) {
        self.service = service
        serverUrl = service.serverUrl()
        token = service.token()
    }

    func testConnection() {
        run {
            try self.service.saveConnection(serverUrl: self.serverUrl, token: self.token)
            try await self.service.testConnection()
            return Messages.connectionOk
        }
    }

    func pullFromDesktop() {
        run {
            try self.service.saveConnection(serverUrl: self.serverUrl, token: self.token)
            let result = try await self.service.pullDesktopContacts()
            return Messages.pullResult(added: result.added, skipped: result.skipped)
        }
    }

    func pushToDesktop() {
        run {
            try self.service.saveConnection(serverUrl: self.serverUrl, token: self.token)
            let count = try await self.service.pushDeviceContacts()
            return Messages.pushResult(count: count)
        }
    }

    private func run(_ action: @escaping () async throws -> String) {
        working = true
        status = Messages.working
        Task {
            do {
                status = try await action()
            } catch {
                status = Messages.error(error.localizedDescription)
            }
            working = false
        }
    }
}
