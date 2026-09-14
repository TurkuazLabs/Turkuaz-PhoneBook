// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/distribution_tool.go
// 📌 Amac: Native launcher icin portable ve Program Files kurulum modunu ayirt eder.
// 📌 Tool - Go
// Version: 1.0.0
// Aciklama: Installed marker ve yazilabilir launcher calisma kokunu platformdan cozer.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"os"
	"path/filepath"
	"runtime"
)

const installedMarkerRelativePath = "config/installed.mode"

func IsInstalledDistribution(root string) bool {
	if runtime.GOOS != "windows" {
		return false
	}
	info, err := os.Stat(AppPath(root, installedMarkerRelativePath))
	return err == nil && !info.IsDir()
}

func LauncherMutableRoot(root string, installed bool) string {
	if !installed {
		return root
	}
	base := os.Getenv("LOCALAPPDATA")
	if base == "" {
		home, err := os.UserHomeDir()
		if err == nil {
			base = filepath.Join(home, "AppData", "Local")
		}
	}
	if base == "" {
		return root
	}
	return filepath.Join(base, "TurkuazLabs", "TelefonRehberi", "Launcher")
}
