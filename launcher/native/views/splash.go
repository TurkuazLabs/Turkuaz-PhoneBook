// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/views/splash.go
// 📌 Amac: Launcher splash view sozlesmesini ve sessiz helper view'ini tanimlar.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: Platform splash uygulamalarinin ortak arayuzu ve self-update no-op view katmanidir.
// Bagimli Oldugu Katman: View

package views

type Splash interface {
	Update(status string, percent int)
	Close()
}

type silentSplash struct{}

func NewSilentSplash() Splash           { return silentSplash{} }
func (silentSplash) Update(string, int) {}
func (silentSplash) Close()             {}
