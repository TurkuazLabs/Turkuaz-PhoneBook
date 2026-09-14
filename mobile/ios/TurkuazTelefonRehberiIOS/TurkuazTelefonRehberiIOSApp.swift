// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/TurkuazTelefonRehberiIOSApp.swift
// # 📌 Amac: iOS SwiftUI uygulamasinin dependency wiring ve giris noktasidir.
// # 📌 Bootstrap - Swift
// # Version: 1.0.1
// # Aciklama: Repository, Tool, Service ve Controller katmanlarini MainActor uzerinde olusturup ContentView'i baslatir.
// # Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View
import SwiftUI

@main
@MainActor
struct TurkuazTelefonRehberiIOSApp: App {
    @StateObject private var controller: MainController

    init() {
        let settingsRepository = SettingsRepository()
        let service = SyncService(
            contactsRepository: DeviceContactRepository(),
            settingsRepository: settingsRepository,
            desktopApiTool: DesktopApiTool()
        )
        _controller = StateObject(wrappedValue: MainController(service: service))
    }

    var body: some Scene {
        WindowGroup {
            ContentView(controller: controller)
        }
    }
}
