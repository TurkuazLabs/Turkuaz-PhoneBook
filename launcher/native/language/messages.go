// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/language/messages.go
// 📌 Amac: Native launcher kullanici mesajlarini merkezi ve yerellestirilebilir olarak tutar.
// 📌 Language - Go
// Version: 2.3.1
// Aciklama: Turkce sistemlerde Turkce, diger dillerde Ingilizce marka, durum ve launcher/tool hata metinlerini kullanir.
// Bagimli Oldugu Katman: Language

package language

import "strings"

var (
	AppName                = localized("Turkuaz Telefon Rehberi", "Turkuaz PhoneBook")
	SplashSubtitle         = localized("Kisileriniz guvende, her zaman yaninizda", "Your contacts, safe and close at hand")
	StatusStarting         = localized("Baslatiliyor...", "Starting...")
	StatusPreparing        = localized("Bilesenler kontrol ediliyor", "Checking components")
	StatusJava             = localized("Java ortami hazirlaniyor", "Preparing Java runtime")
	StatusDependencies     = localized("Uygulama bilesenleri hazirlaniyor", "Preparing application components")
	StatusUpdate           = localized("Guncellemeler kontrol ediliyor", "Checking for updates")
	StatusOpening          = localized("Telefon Rehberi aciliyor", "Opening PhoneBook")
	StatusLauncherUpdate   = localized("Launcher guncellemesi uygulaniyor", "Applying launcher update")
	StatusInstallingUpdate = localized("Yeni surum kuruluyor", "Installing new version")
	ErrorTitle             = localized("Turkuaz Telefon Rehberi - Hata", "Turkuaz PhoneBook - Error")
	LauncherErrorFormat    = localized("Launcher hatasi: %v", "Launcher error: %v")
	LauncherUpdateError    = localized("Launcher update baslatilamadi: %v", "Launcher update could not be started: %v")
)

var englishErrorReplacer = strings.NewReplacer(
	"launcher config okunamadi", "launcher config could not be read",
	"launcher state okunamadi", "launcher state could not be read",
	"kurulum Java runtime dosyasi eksik; Telefon Rehberi Setup ile Onar/Kur islemi yapin", "the installed Java runtime is missing; repair or reinstall Turkuaz PhoneBook",
	"Java metadata indirilemedi", "Java metadata could not be downloaded",
	"Adoptium uygun Java paketi dondurmedi", "Adoptium did not return a compatible Java package",
	"Adoptium paket URL veya checksum eksik", "Adoptium package URL or checksum is missing",
	"Java indirilemedi", "Java could not be downloaded",
	"Java ZIP arsivi acilamadi", "Java ZIP archive could not be extracted",
	"Java TAR.GZ arsivi acilamadi", "Java TAR.GZ archive could not be extracted",
	"Java runtime kopyalanamadi", "Java runtime could not be copied",
	"portable Java kurulumu tamamlanamadi", "portable Java installation could not be completed",
	"kurulum SQLite JDBC dosyasi eksik veya bozuk; Telefon Rehberi Setup ile Onar/Kur islemi yapin", "the installed SQLite JDBC file is missing or damaged; repair or reinstall Turkuaz PhoneBook",
	"SQLite JDBC indirilemedi", "SQLite JDBC could not be downloaded",
	"kurulum SLF4J API dosyasi eksik veya bozuk; Telefon Rehberi Setup ile Onar/Kur islemi yapin", "the installed SLF4J API file is missing or damaged; repair or reinstall Turkuaz PhoneBook",
	"SLF4J API indirilemedi", "SLF4J API could not be downloaded",
	"kurulum FlatLaf dosyasi eksik veya bozuk; Telefon Rehberi Setup ile Onar/Kur islemi yapin", "the installed FlatLaf file is missing or damaged; repair or reinstall Turkuaz PhoneBook",
	"FlatLaf checksum indirilemedi", "FlatLaf checksum could not be downloaded",
	"FlatLaf indirilemedi", "FlatLaf could not be downloaded",
	"uygulama JAR bulunamadi", "application JAR was not found",
	"bekleyen launcher bulunamadi", "pending launcher was not found",
	"launcher dosyasi guncelleme icin serbest birakilmadi", "launcher file was not released for update",
	"guvensiz ZIP girdisi", "unsafe ZIP entry",
	"guvensiz TAR symlink girdisi", "unsafe TAR symlink entry",
	"guvensiz TAR girdisi", "unsafe TAR entry",
	"SHA-256 dogrulama hatasi", "SHA-256 verification error",
	"gecersiz SHA-256 checksum", "invalid SHA-256 checksum",
	"yonetici yetkili setup guncellemesi bu platformda desteklenmiyor", "elevated setup update is not supported on this platform",
	"yonetici yetkili process baslatilamadi", "elevated process could not be started",
	"ShellExecute kodu", "ShellExecute code",
	"indirilen Java paketinde javaw.exe bulunamadi", "downloaded Java package does not contain javaw.exe",
	"indirilen Java paketinde java bulunamadi", "downloaded Java package does not contain java",
	"eksik ayar", "missing setting",
	"dosya bulunamadi", "file was not found",
	"dosya eksik", "file is missing",
	"dogrulamasi basarisiz", "verification failed",
	"checksum uyusmuyor", "checksum does not match",
	"arsiv acilamadi", "archive could not be extracted",
	"baslatilamadi", "could not be started",
	"kopyalanamadi", "could not be copied",
	"yazilamadi", "could not be written",
	"okunamadi", "could not be read",
)

func LocalizeError(message string) string {
	return localizeError(message, isTurkishLocale())
}

func localizeError(message string, turkish bool) string {
	if turkish || strings.TrimSpace(message) == "" {
		return message
	}
	return englishErrorReplacer.Replace(message)
}
