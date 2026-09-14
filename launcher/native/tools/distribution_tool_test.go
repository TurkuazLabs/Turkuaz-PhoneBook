// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/distribution_tool_test.go
// 📌 Amac: Installed launcher yazilabilir kok ve portable fallback davranisini dogrular.
// 📌 Tool - GoTest
// Version: 1.0.0
// Aciklama: LOCALAPPDATA tabanli launcher kokunu ve portable root korunmasini test eder.
// Bagimli Oldugu Katman: Tool

package tools

import (
	"path/filepath"
	"testing"
)

func TestLauncherMutableRootPortable(t *testing.T) {
	root := filepath.Join("tmp", "portable")
	if actual := LauncherMutableRoot(root, false); actual != root {
		t.Fatalf("portable root degisti: %s", actual)
	}
}

func TestLauncherMutableRootInstalled(t *testing.T) {
	t.Setenv("LOCALAPPDATA", filepath.Join("tmp", "localappdata"))
	expected := filepath.Join("tmp", "localappdata", "TurkuazLabs", "TelefonRehberi", "Launcher")
	if actual := LauncherMutableRoot("ignored", true); actual != expected {
		t.Fatalf("installed mutable root beklenenden farkli: %s", actual)
	}
}
