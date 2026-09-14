// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/elevated_process_other.go
// 📌 Amac: Windows disi platformlarda elevated installer cagrisi icin uyumluluk stub'i saglar.
// 📌 Tool - Go
// Version: 1.0.0
// Aciklama: Inno Setup auto-update yalniz Windows installed dagitiminda desteklenir.
// Bagimli Oldugu Katman: Tool

//go:build !windows

package tools

import "fmt"

func StartElevated(executable string, args []string, workingDirectory string) error {
	return fmt.Errorf("yonetici yetkili setup guncellemesi bu platformda desteklenmiyor")
}
