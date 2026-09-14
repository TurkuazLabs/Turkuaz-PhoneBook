// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/path_tool.go
// 📌 Amac: Launcher kok ve relative dosya yollarini guvenli cozer.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: Executable dizinini APP_ROOT kabul eden path adaptorudur.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"os"
	"path/filepath"
)

func ExecutableRoot() (string, error) {
	exe, err := os.Executable()
	if err != nil {
		return "", err
	}
	exe, err = filepath.EvalSymlinks(exe)
	if err != nil {
		return "", err
	}
	return filepath.Dir(exe), nil
}

func AppPath(root, relative string) string {
	return filepath.Clean(filepath.Join(root, filepath.FromSlash(relative)))
}
