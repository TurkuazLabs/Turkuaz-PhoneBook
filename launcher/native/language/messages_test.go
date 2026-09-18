// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/language/messages_test.go
// 📌 Amac: Native launcher hata yerellestirmesinin Turkce kaynak hatalari Ingilizce fallback'e eksiksiz cevirdigini dogrular.
// 📌 Tool - Go Test
// Version: 1.0.0
// Aciklama: Arsiv, SHA-256, elevated process ve Java runtime hata metinlerinde Ingilizce fallback regresyonlarini engeller.
// Bagimli Oldugu Katman: Language

package language

import "testing"

func TestLocalizeErrorEnglishFallback(t *testing.T) {
	tests := map[string]string{
		"guvensiz ZIP girdisi: ../evil": "unsafe ZIP entry: ../evil",
		"guvensiz TAR girdisi: ../evil": "unsafe TAR entry: ../evil",
		"guvensiz TAR symlink girdisi: link": "unsafe TAR symlink entry: link",
		"SHA-256 dogrulama hatasi: package.bin": "SHA-256 verification error: package.bin",
		"gecersiz SHA-256 checksum": "invalid SHA-256 checksum",
		"yonetici yetkili setup guncellemesi bu platformda desteklenmiyor": "elevated setup update is not supported on this platform",
		"yonetici yetkili process baslatilamadi: ShellExecute kodu 5": "elevated process could not be started: ShellExecute code 5",
		"indirilen Java paketinde javaw.exe bulunamadi": "downloaded Java package does not contain javaw.exe",
		"indirilen Java paketinde java bulunamadi": "downloaded Java package does not contain java",
	}

	for input, expected := range tests {
		if actual := localizeError(input, false); actual != expected {
			t.Fatalf("localizeError(%q) = %q, expected %q", input, actual, expected)
		}
	}
}

func TestLocalizeErrorKeepsTurkishSource(t *testing.T) {
	input := "gecersiz SHA-256 checksum"
	if actual := localizeError(input, true); actual != input {
		t.Fatalf("Turkce locale kaynak metni degisti: %q", actual)
	}
}

func TestLocalizeErrorKeepsEmptyMessage(t *testing.T) {
	if actual := localizeError("   ", false); actual != "   " {
		t.Fatalf("Bos hata metni degisti: %q", actual)
	}
}
