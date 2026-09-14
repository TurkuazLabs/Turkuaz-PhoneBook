// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/repositories/yaml_repository_test.go
// 📌 Amac: Native launcher basit YAML repository okuma ve state yazimini dogrular.
// 📌 Modul - GoTest
// Version: 2.0.0
// Aciklama: Config quote temizleme ve state round-trip testidir.
// Bagimli Oldugu Katman: Repository

package repositories

import (
	"os"
	"path/filepath"
	"testing"
)

func TestSimpleYAMLRoundTrip(t *testing.T) {
	dir := t.TempDir()
	path := filepath.Join(dir, "state.yml")
	repo := SimpleYAMLRepository{}
	if err := repo.WriteState(path, "2.33.3", "2.0.0"); err != nil {
		t.Fatal(err)
	}
	values, err := repo.Read(path)
	if err != nil {
		t.Fatal(err)
	}
	if values["app_version"] != "2.33.3" || values["launcher_version"] != "2.0.0" {
		t.Fatalf("beklenmeyen state: %#v", values)
	}
	if _, err := os.Stat(path); err != nil {
		t.Fatal(err)
	}
}
