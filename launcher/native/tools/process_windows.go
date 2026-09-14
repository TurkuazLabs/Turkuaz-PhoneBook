// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/process_windows.go
// 📌 Amac: Windows uygulama ve helper processlerini konsolsuz baslatir.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: javaw ve native updater icin Windows process adaptorudur.
// Bagimli Oldugu Katman: Tool

//go:build windows

package tools

import (
	"os/exec"
	"syscall"
)

func StartDetached(executable string, args []string, workingDirectory string) error {
	cmd := exec.Command(executable, args...)
	cmd.Dir = workingDirectory
	cmd.SysProcAttr = &syscall.SysProcAttr{HideWindow: true, CreationFlags: 0x08000000}
	return cmd.Start()
}
