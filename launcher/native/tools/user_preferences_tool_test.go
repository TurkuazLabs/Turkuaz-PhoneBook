// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/user_preferences_tool_test.go
// 📌 Amac: Native launcher kullanici preferences.yml yolunun platform config kokuyla uyumunu test eder.
// 📌 Tool - Go Test
// Version: 1.0.0
// Aciklama: Aktif platformda cozumlenen dosya yolunun TurkuazLabs TelefonRehberi preferences.yml sonunu dogrular.
// Bagimli Oldugu Katman: Tool | Config

package tools

import (
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
