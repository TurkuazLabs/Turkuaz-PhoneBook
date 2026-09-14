// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/tools/version_tool_test.go
// 📌 Amac: Native launcher semver karsilastirma aracini dogrular.
// 📌 Modul - GoTest
// Version: 2.0.0
// Aciklama: Update kararinda patch, minor ve major karsilastirmalarini test eder.
// Bagimli Oldugu Katman: Tool

package tools

import "testing"

func TestVersionGreater(t *testing.T) {
	cases := []struct {
		candidate string
		installed string
		expected  bool
	}{
		{"2.33.3", "2.33.2", true},
		{"2.34.1", "2.33.9", true},
		{"3.0.0", "2.99.99", true},
		{"2.33.3", "2.33.3", false},
		{"2.33.2", "2.33.3", false},
	}
	for _, item := range cases {
		if actual := VersionGreater(item.candidate, item.installed); actual != item.expected {
			t.Fatalf("VersionGreater(%q,%q)=%v expected=%v", item.candidate, item.installed, actual, item.expected)
		}
	}
}
