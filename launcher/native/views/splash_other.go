// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/views/splash_other.go
// 📌 Amac: Windows disi platformlarda sessiz launcher splash ve masaustu hata bildirimi saglar.
// 📌 Modul - Go
// Version: 2.2.0
// Aciklama: Linux masaustu acilisinda terminal gerektirmez; hata icin zenity veya kdialog fallback kullanir.
// Bagimli Oldugu Katman: View

//go:build !windows

package views

import "os/exec"

type noopSplash struct{}

func NewSplash(string) Splash         { return noopSplash{} }
func (noopSplash) Update(string, int) {}
func (noopSplash) Close()             {}

func ShowError(title, message string) {
	if path, err := exec.LookPath("zenity"); err == nil {
		_ = exec.Command(path, "--error", "--title="+title, "--text="+message).Run()
		return
	}
	if path, err := exec.LookPath("kdialog"); err == nil {
		_ = exec.Command(path, "--title", title, "--error", message).Run()
	}
}
