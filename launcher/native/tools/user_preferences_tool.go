// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/user_preferences_tool.go
// 📌 Amac: Native launcher icin Java uygulamasiyla ortak kullanici preferences.yml yolunu cozer.
// 📌 Modul - Go
// Version: 1.0.0
// Aciklama: Windows APPDATA, Linux XDG_CONFIG_HOME ve macOS Application Support config yollarini ayni kuralla belirler.
// Bagimli Oldugu Katman: Tool | Config

package tools

import (
	"os"
	"path/filepath"
	"runtime"
)

func UserPreferencesPath() string {
	home, _ := os.UserHomeDir()
	switch runtime.GOOS {
	case "windows":
		base := os.Getenv("APPDATA")
		if base == "" {
			base = filepath.Join(home, "AppData", "Roaming")
		}
		return filepath.Join(base, "TurkuazLabs", "TelefonRehberi", "config", "preferences.yml")
	case "darwin":
		return filepath.Join(home, "Library", "Application Support", "TurkuazLabs", "TelefonRehberi", "config", "preferences.yml")
	default:
		base := os.Getenv("XDG_CONFIG_HOME")
		if base == "" {
			base = filepath.Join(home, ".config")
		}
		return filepath.Join(base, "turkuazlabs", "telefon-rehberi", "preferences.yml")
	}
}
