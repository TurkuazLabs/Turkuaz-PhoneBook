// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/user_preferences_tool_test.go
// 📌 Amac: Native launcher kullanici preferences.yml yolunun platform config kokuyla uyumunu test eder.
// 📌 Tool - Go Test
// Version: 1.1.0
// Aciklama: Aktif platformda cozumlenen dosya yolunun TurkuazLabs TelefonRehberi preferences.yml sonunu dogrular.
// Bagimli Oldugu Katman: Tool | Config

package tools

import (
	"os"
	"path/filepath"
	"strings"
	"testing"
)

func TestUserPreferencesPathHasExpectedFile(t *testing.T) {
	path := filepath.Clean(UserPreferencesPath())
	if filepath.Base(path) != "preferences.yml" {
		t.Fatalf("beklenmeyen preferences dosyasi: %s", path)
	}
	normalized := strings.ToLower(filepath.ToSlash(path))
	if !strings.Contains(normalized, "telefonrehberi") && !strings.Contains(normalized, "telefon-rehberi") {
		t.Fatalf("uygulama config dizini eksik: %s", path)
	}
}


func TestReadThemePreference(t *testing.T) {
	temp := t.TempDir()
	path := filepath.Join(temp, "preferences.yml")
	if err := os.WriteFile(path, []byte("theme: \"dark\"\n"), 0o600); err != nil {
		t.Fatal(err)
	}
	if got := ReadThemePreference(path); got != "dark" {
		t.Fatalf("dark tema okunamadi: %s", got)
	}
	if err := os.WriteFile(path, []byte("theme: \"invalid\"\n"), 0o600); err != nil {
		t.Fatal(err)
	}
	if got := ReadThemePreference(path); got != "light" {
		t.Fatalf("gecersiz tema light fallback olmali: %s", got)
	}
	if got := ReadThemePreference(filepath.Join(temp, "missing.yml")); got != "light" {
		t.Fatalf("eksik preferences light fallback olmali: %s", got)
	}
}
