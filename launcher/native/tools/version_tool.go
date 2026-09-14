// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/version_tool.go
// 📌 Amac: Launcher ve uygulama semver degerlerini karsilastirir.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: Major-minor-patch surum karsilastirma aracidir.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"strconv"
	"strings"
)

func VersionGreater(candidate, installed string) bool {
	a := parseVersion(candidate)
	b := parseVersion(installed)
	for i := 0; i < 3; i++ {
		if a[i] != b[i] {
			return a[i] > b[i]
		}
	}
	return false
}

func parseVersion(value string) [3]int {
	value = strings.TrimPrefix(strings.TrimSpace(value), "v")
	value = strings.SplitN(value, "-", 2)[0]
	parts := strings.Split(value, ".")
	var result [3]int
	for i := 0; i < len(parts) && i < 3; i++ {
		result[i], _ = strconv.Atoi(parts[i])
	}
	return result
}
