// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/language/locale.go
// 📌 Amac: Native launcher icin ortak ortam dili yardimcisini tanimlar.
// 📌 Language - Go
// Version: 1.0.1
// Aciklama: LC_ALL, LC_MESSAGES ve LANG degerlerinden Turkce locale fallback tespiti yapar ve ortak yerellestirme secicisini sunar.
// Bagimli Oldugu Katman: Language

package language

import (
	"os"
	"strings"
)

func localeFromEnvironment() bool {
	for _, key := range []string{"LC_ALL", "LC_MESSAGES", "LANG"} {
		value := strings.ToLower(strings.TrimSpace(os.Getenv(key)))
		if value != "" {
			return strings.HasPrefix(value, "tr")
		}
	}
	return false
}

func localized(turkish, english string) string {
	if isTurkishLocale() {
		return turkish
	}
	return english
}
