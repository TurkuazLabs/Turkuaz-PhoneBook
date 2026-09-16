// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/language/locale_other.go
// 📌 Amac: Windows disi native launcher platformlarinda kullanici dilini ortam locale degerlerinden tespit eder.
// 📌 Language - Go
// Version: 1.0.0
// Aciklama: Linux ve diger desteklenen platformlarda LC_ALL, LC_MESSAGES ve LANG uzerinden Turkce locale secimini yapar.
// Bagimli Oldugu Katman: Language

//go:build !windows

package language

func isTurkishLocale() bool {
	return localeFromEnvironment()
}
