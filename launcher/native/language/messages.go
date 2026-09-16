// 📄 Dosya Yolu: C:/Projects/TelefonRehberi/launcher/native/language/messages.go
// 📌 Amac: Native launcher kullanici mesajlarini merkezi ve yerellestirilebilir olarak tutar.
// 📌 Language - Go
// Version: 2.2.0
// Aciklama: Turkce sistemlerde Turkuaz Telefon Rehberi, diger dillerde Turkuaz PhoneBook marka/metinlerini kullanir.
// Bagimli Oldugu Katman: Language

package language

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
