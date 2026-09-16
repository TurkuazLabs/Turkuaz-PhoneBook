// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/language/locale_windows.go
// 📌 Amac: Windows native launcher icin kullanici arayuz dilini isletim sisteminden tespit eder.
// 📌 Language - Go
// Version: 1.0.0
// Aciklama: Windows UI LANGID uzerinden Turkce tespiti yapar; API sonucu yoksa ortam degiskenlerine geri doner.
// Bagimli Oldugu Katman: Language | Tool

//go:build windows

package language

import "syscall"

var procGetUserDefaultUILanguage = syscall.NewLazyDLL("kernel32.dll").NewProc("GetUserDefaultUILanguage")

func isTurkishLocale() bool {
	langID, _, _ := procGetUserDefaultUILanguage.Call()
	if langID == 0 {
		return localeFromEnvironment()
	}
	return uint16(langID)&0x03ff == 0x001f
}
