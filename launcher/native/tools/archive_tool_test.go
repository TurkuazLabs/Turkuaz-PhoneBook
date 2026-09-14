// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/archive_tool_test.go
// 📌 Amac: ZIP/TAR.GZ archive extraction guvenligini ve Linux JRE arsiv uyumlulugunu dogrular.
// 📌 Modul - GoTest
// Version: 1.0.0
// Aciklama: TAR.GZ regular file extraction ve path traversal reddini test eder.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"archive/tar"
	"compress/gzip"
	"os"
	"path/filepath"
	"testing"
)

func TestExtractTarGz(t *testing.T) {
	temp := t.TempDir()
	archive := filepath.Join(temp, "runtime.tar.gz")
	writeTarGzFixture(t, archive, "jdk/bin/java", []byte("java"))

	destination := filepath.Join(temp, "out")
	if err := ExtractTarGz(archive, destination); err != nil {
		t.Fatalf("ExtractTarGz hata: %v", err)
	}
	data, err := os.ReadFile(filepath.Join(destination, "jdk", "bin", "java"))
	if err != nil {
		t.Fatalf("Cikan dosya okunamadi: %v", err)
	}
	if string(data) != "java" {
		t.Fatalf("Beklenmeyen icerik: %q", string(data))
	}
}

func TestExtractTarGzRejectsTraversal(t *testing.T) {
	temp := t.TempDir()
	archive := filepath.Join(temp, "unsafe.tar.gz")
	writeTarGzFixture(t, archive, "../escape", []byte("bad"))

	if err := ExtractTarGz(archive, filepath.Join(temp, "out")); err == nil {
		t.Fatal("Path traversal arsivi reddedilmeliydi")
	}
}

func writeTarGzFixture(t *testing.T, path, name string, data []byte) {
	t.Helper()
	file, err := os.Create(path)
	if err != nil {
		t.Fatal(err)
	}
	gzipWriter := gzip.NewWriter(file)
	tarWriter := tar.NewWriter(gzipWriter)
	header := &tar.Header{Name: name, Mode: 0o755, Size: int64(len(data)), Typeflag: tar.TypeReg}
	if err := tarWriter.WriteHeader(header); err != nil {
		t.Fatal(err)
	}
	if _, err := tarWriter.Write(data); err != nil {
		t.Fatal(err)
	}
	if err := tarWriter.Close(); err != nil {
		t.Fatal(err)
	}
	if err := gzipWriter.Close(); err != nil {
		t.Fatal(err)
	}
	if err := file.Close(); err != nil {
		t.Fatal(err)
	}
}
