// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/cmd/telefonrehberi/main.go
// 📌 Amac: TelefonRehberi native launcher executable giris noktasidir.
// 📌 Modul - Go
// Version: 2.0.1
// Aciklama: Turkuaz-PhoneBook modul kokundeki controller katmanina request devreden ince bootstrap dosyasidir.
// Bagimli Oldugu Katman: Controller

package main

import (
	"os"

	"github.com/turkuazlabs/turkuaz-phonebook/launcher/native/controllers"
)

func main() {
	os.Exit(controllers.LauncherController{}.Run(os.Args))
}
