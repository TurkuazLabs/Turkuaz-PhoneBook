// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/file_tool.go
// 📌 Amac: Launcher dosya sistemi islemlerini merkezi yapar.
// 📌 Modul - Go
// Version: 2.1.0
// Aciklama: Dizin, izin koruyan kopyalama, tasima ve backup rotasyonu aracidir.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"io"
	"os"
	"path/filepath"
	"sort"
)

func EnsureDirs(root string, relatives ...string) error {
	for _, relative := range relatives {
		if err := os.MkdirAll(AppPath(root, relative), 0o755); err != nil {
			return err
		}
	}
	return nil
}

func CopyFile(source, destination string) error {
	in, err := os.Open(source)
	if err != nil {
		return err
	}
	defer in.Close()
	info, err := in.Stat()
	if err != nil {
		return err
	}
	if err := os.MkdirAll(filepath.Dir(destination), 0o755); err != nil {
		return err
	}
	out, err := os.OpenFile(destination, os.O_CREATE|os.O_TRUNC|os.O_WRONLY, info.Mode().Perm())
	if err != nil {
		return err
	}
	if _, err = io.Copy(out, in); err != nil {
		out.Close()
		return err
	}
	if err := out.Close(); err != nil {
		return err
	}
	return os.Chmod(destination, info.Mode().Perm())
}

func ReplaceFile(source, destination string) error {
	if err := os.MkdirAll(filepath.Dir(destination), 0o755); err != nil {
		return err
	}
	_ = os.Remove(destination)
	if err := os.Rename(source, destination); err == nil {
		return nil
	}
	if err := CopyFile(source, destination); err != nil {
		return err
	}
	return os.Remove(source)
}

func RotateFiles(dir, pattern string, keep int) error {
	if keep < 1 {
		return nil
	}
	matches, err := filepath.Glob(filepath.Join(dir, pattern))
	if err != nil {
		return err
	}
	sort.Slice(matches, func(i, j int) bool {
		a, _ := os.Stat(matches[i])
		b, _ := os.Stat(matches[j])
		if a == nil || b == nil {
			return matches[i] > matches[j]
		}
		return a.ModTime().After(b.ModTime())
	})
	for _, path := range matches[keep:] {
		_ = os.Remove(path)
	}
	return nil
}
