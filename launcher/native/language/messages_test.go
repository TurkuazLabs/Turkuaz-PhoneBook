// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/language/messages_test.go
// 📌 Amac: Native launcher hata ceviri tablosunun dinamik detaylari bozmadan Ingilizce fallback urettigini dogrular.
// 📌 Language - Go Test
// Version: 1.0.0
// Aciklama: Kritik Java, JDBC, JAR ve config hata metinlerinin Ingilizce karsiliklarini regresyona karsi test eder.
// Bagimli Oldugu Katman: Language

package language

import "testing"

func TestEnglishErrorReplacerPreservesDetails(t *testing.T) {
	tests := []struct {
		name string
		in   string
		want string
	}{
		{
			name: "launcher config",
			in:   "launcher config okunamadi: access denied",
			want: "launcher config could not be read: access denied",
		},
		{
			name: "installed Java runtime",
			in:   "kurulum Java runtime dosyasi eksik; Telefon Rehberi Setup ile Onar/Kur islemi yapin",
			want: "the installed Java runtime is missing; repair or reinstall Turkuaz PhoneBook",
		},
		{
			name: "sqlite download",
			in:   "SQLite JDBC indirilemedi: timeout",
			want: "SQLite JDBC could not be downloaded: timeout",
		},
		{
			name: "application jar",
			in:   "uygulama JAR bulunamadi: C:/Program Files/TurkuazLabs/TelefonRehberi/app/TelefonRehberi.jar",
			want: "application JAR was not found: C:/Program Files/TurkuazLabs/TelefonRehberi/app/TelefonRehberi.jar",
		},
		{
			name: "missing setting",
			in:   "eksik ayar: github_repo",
			want: "missing setting: github_repo",
		},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			if got := englishErrorReplacer.Replace(test.in); got != test.want {
				t.Fatalf("translation mismatch\nwant: %q\n got: %q", test.want, got)
			}
		})
	}
}
