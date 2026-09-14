// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/process_other.go
// 📌 Amac: Windows disi test ortaminda child process baslatma uyumlulugu saglar.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: Native launcher core testleri icin genel process adaptorudur.
// Bagimli Oldugu Katman: Tool

//go:build !windows

package tools

import "os/exec"

func StartDetached(executable string, args []string, workingDirectory string) error {
	cmd := exec.Command(executable, args...)
	cmd.Dir = workingDirectory
	return cmd.Start()
}
