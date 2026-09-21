# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/WINDOWS_INSTALLER.md
# 📌 Amac: v2.38.0 Inno Setup Program Files kurulumu, yerellestirilmis urun adi ve auto-update davranisini belgeler.
# 📌 Modul - Markdown
# Version: 2.5.0
# Aciklama: Installed/portable ayrimi, Setup auto-update, Windows ikon zinciri ve SHA-dogrulamali CI installer dependency cache akisidir.
# Bagimli Oldugu Katman: Tool | Config

## Kurulum

Varsayilan dizin:

`C:\Program Files\TurkuazLabs\TelefonRehberi`

Setup native launcher, uygulama JAR'i, Temurin JRE 17, SQLite JDBC, FlatLaf ve branding assetlerini birlikte kurar. Paketleme sirasinda Windows kisayol ICO'su kaynak PNG'deki fazla seffaf alani kirparak coklu cozunurlukte yeniden uretilir; bu nedenle masaustu ve Baslat menusu ikonu onceki pakete gore daha buyuk ve okunakli gorunur. Kurulum dili Turkce ise gorunen urun adi **Turkuaz Telefon Rehberi**, Ingilizce ise **Turkuaz PhoneBook** olur. Teknik kurulum dizini ve `TelefonRehberi.exe` geriye donuk uyumluluk icin degismez. `config/installed.mode` installed dagitimi portable'dan ayirir.

## Yazilabilir launcher alani

`%LOCALAPPDATA%\TurkuazLabs\TelefonRehberi\Launcher`

Cache, log, state ve indirilen update Setup EXE burada tutulur. Program Files calisma verisi icin kullanilmaz.

## Auto-update

Launcher GitHub latest release manifestini kontrol eder. Daha yeni surumde Setup EXE cache'e indirilir, SHA-256 dogrulanir ve normal kullanici tokeniyla baslatilir. Inno Setup `PrivilegesRequired=admin` nedeniyle UAC ister. `/AUTOUPDATE` sonrasinda uygulama `runasoriginaluser` ile normal kullanici olarak yeniden acilir.

## Uninstall

Uninstall Program Files uygulama payloadini kaldirir; `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi` altindaki rehber DB, export ve backup dosyalarini silmez.


## Light / Dark splash

Native Windows launcher, splash acilmadan once kullanicinin `preferences.yml` dosyasindaki `theme` degerini okur. `light` ve `dark` icin arka plan, yazi, progress bar ve Turkuaz marka ikonu ayri palet kullanir. Tema degeri eksik veya gecersizse guvenli fallback `light` olur.


## Windows ikon zinciri

Windows kurulumunda ikonlar tek bir dusuk cozunurluklu kaynaga birakilmaz:

- Masaustu ve Baslat menusu kisayollari surume ozel `app-icon-vX.Y.Z.ico` yolunu kullanir; bu Windows icon cache'in eski ikonu gostermesini engeller.
- Uygulamalar ve Ozellikler / uninstall kaydi ayni surume ozel ICO dosyasini kullanir.
- Swing pencere basligi ve taskbar icin 512 px kaynaktan 16/20/24/32/40/48/64/128/256 px tam boyutlu ikon listesi uretilir.
- Light/Dark tema degistiginde Swing pencere ikon listesi de tema paletine gore yenilenir.


## CI installer dependency cache

Windows Build job'u Setup payloadi icin gereken buyuk bagimliliklari `cache/installer` altinda saklar. GitHub Actions cache anahtari `config/launcher.yml` dosyasinin hash degerini kullanir; dependency surumu veya URL/config degistiginde yeni cache anahtari uretilir.

Cache kapsami:

- SQLite JDBC
- SLF4J API
- FlatLaf
- Temurin JRE 17 Windows arsivi

Cache guvenilir kaynak yerine gecmez. `build-installer.ps1`, cache'den okunan her dosyanin SHA-256 degerini beklenen checksum ile tekrar karsilastirir. Uyusmazlikta cache dosyasi silinir ve yeniden indirilir. Cache klasoru staging payloadinin disindadir ve Setup paketine dahil edilmez.
