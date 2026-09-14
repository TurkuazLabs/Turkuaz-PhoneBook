// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/hash_tool.go
// 📌 Amac: Indirilen launcher ve uygulama dosyalarinin SHA-256 kontrolunu yapar.
// 📌 Modul - Go
// Version: 2.0.0
// Aciklama: Release asset butunluk dogrulama aracidir.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"io"
	"os"
	"strings"
)

func SHA256File(path string) (string, error) {
	file, err := os.Open(path)
	if err != nil {
		return "", err
	}
	defer file.Close()
	h := sha256.New()
	if _, err := io.Copy(h, file); err != nil {
		return "", err
	}
	return hex.EncodeToString(h.Sum(nil)), nil
}

func AssertSHA256(path, expected string) error {
	actual, err := SHA256File(path)
	if err != nil {
		return err
	}
	if !strings.EqualFold(strings.TrimSpace(actual), strings.TrimSpace(expected)) {
		return fmt.Errorf("SHA-256 dogrulama hatasi: %s", path)
	}
	return nil
}

func ParseChecksum(raw string) (string, error) {
	parts := strings.Fields(strings.TrimSpace(raw))
	if len(parts) == 0 || len(parts[0]) != 64 {
		return "", fmt.Errorf("gecersiz SHA-256 checksum")
	}
	return strings.ToLower(parts[0]), nil
}
