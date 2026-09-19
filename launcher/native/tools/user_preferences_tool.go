// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/user_preferences_tool.go
// 📌 Amac: Native launcher icin Java uygulamasiyla ortak preferences.yml yolunu ve tema tercihini cozer.
// 📌 Modul - Go
// Version: 1.1.0
// Aciklama: Platform config yolunu korur; splash baslamadan light/dark tema tercihini guvenli fallback ile okur.
// Bagimli Oldugu Katman: Tool | Config

package tools

import (
	"bufio"
	"os"
	"path/filepath"
	"runtime"
	"strings"
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

func UserThemePreference() string {
	return ReadThemePreference(UserPreferencesPath())
}

func ReadThemePreference(path string) string {
	file, err := os.Open(path)
	if err != nil {
		return "light"
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := strings.TrimSpace(scanner.Text())
		if line == "" || strings.HasPrefix(line, "#") {
			continue
		}
		parts := strings.SplitN(line, ":", 2)
		if len(parts) != 2 || strings.TrimSpace(parts[0]) != "theme" {
			continue
		}
		value := strings.Trim(strings.TrimSpace(parts[1]), "\"'")
		if strings.EqualFold(value, "dark") {
			return "dark"
		}
		return "light"
	}
	return "light"
}
