// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/cmd/telefonrehberi/main.go
// 📌 Amac: TelefonRehberi native launcher executable giris noktasidir.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: Request'i controller katmanina devreden ince bootstrap dosyasidir.
// Bagimli Oldugu Katman: Controller

package main

import (
	"os"

	"github.com/turkuazlabs/turkuaz-telefon-rehberi/launcher/native/controllers"
)

func main() {
	os.Exit(controllers.LauncherController{}.Run(os.Args))
}
