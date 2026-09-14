// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/views/ContentView.swift
// # 📌 Amac: iOS mobil senkron ekranini SwiftUI ile goruntuler.
// # 📌 View - Swift
// # Version: 1.1.0
// # Aciklama: PC adresi, token, baglanti testi, iki yonlu senkron butonlari ve durum bilgisini sunar.
// # Bagimli Oldugu Katman: View | Controller | Language
import SwiftUI

struct ContentView: View {
    @ObservedObject var controller: MainController

    var body: some View {
        NavigationStack {
            Form {
                Section(Messages.connectionSection) {
                    TextField(Messages.serverUrlHint, text: $controller.serverUrl)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                    SecureField(Messages.tokenHint, text: $controller.token)
                    Button(Messages.testConnectionButton) {
                        controller.testConnection()
                    }
                    .disabled(controller.working)
                }

                Section(Messages.syncSection) {
                    Button(Messages.pullButton) {
                        controller.pullFromDesktop()
                    }
                    .disabled(controller.working)

                    Button(Messages.pushButton) {
                        controller.pushToDesktop()
                    }
                    .disabled(controller.working)
                }

                Section(Messages.statusSection) {
                    Text(controller.status)
                    Text(Messages.trustedLanHint)
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }
            }
            .navigationTitle(Messages.title)
        }
    }
}
