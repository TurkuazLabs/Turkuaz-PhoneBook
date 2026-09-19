<div align="center">
  <img src="assets/branding/app-icon-128.png" alt="Turkuaz" width="120">

  <h1>Turkuaz Telefon Rehberi</h1>

  <p><strong>TurkuazLabs tarafından geliştirilen, veriyi öncelikle yerelde tutan kişi yönetimi ve mobil senkron uygulaması.</strong></p>
  <p>Global urun adi: <strong>Turkuaz PhoneBook</strong> · Ana marka: <strong>Turkuaz</strong></p>

  <p><a href="README.en.md">English README</a></p>

  <p>
    <a href="https://github.com/TurkuazLabs/Turkuaz-PhoneBook/actions/workflows/build.yml"><img src="https://github.com/TurkuazLabs/Turkuaz-PhoneBook/actions/workflows/build.yml/badge.svg" alt="Build"></a>
    <img src="https://img.shields.io/badge/version-2.38.3-0aa6a6" alt="Version 2.38.3">
    <img src="https://img.shields.io/badge/Java-17-orange" alt="Java 17">
    <img src="https://img.shields.io/badge/SQLite-local-blue" alt="SQLite">
  </p>
</div>

---

## Marka ve Urun Adi

Repository ve global urun kimligi **Turkuaz PhoneBook** olarak kullanilir. Dil bagimsiz ana marka yalnizca **Turkuaz**'dir.

Gorunen urun adi kullanici diline gore yerellestirilir:

- Turkce: **Turkuaz Telefon Rehberi**
- Ingilizce ve diger diller icin fallback: **Turkuaz PhoneBook**

Logo ve ikon kimligi dile bagli degildir. Teknik geriye donuk uyumluluk icin `TelefonRehberi.exe`, Java package adlari, Inno Setup `AppId` ve mevcut kullanici veri klasorleri yeniden adlandirilmaz.

Canonical repository: `https://github.com/TurkuazLabs/Turkuaz-PhoneBook`

## Neden Turkuaz PhoneBook?

Turkuaz PhoneBook, kisi verisini zorunlu bir bulut servisine tasimadan yonetmek isteyen kullanicilar icin gelistirilen masaustu odakli bir rehber uygulamasidir. Ana veri SQLite veritabaninda kullanicinin kendi cihazinda tutulur. Windows ve Linux ana masaustu platformlaridir; Android ve iOS istemcileri ayni guvenilir yerel ag uzerinden masaustu uygulamasi ile senkronize olabilir.

Temel hedefler:

- Kisi verisini kullanicinin kontrolunde tutmak
- Grup, etiket, gecmis, hatirlatma ve duplicate yonetimi sunmak
- Windows ve Linux'ta native launcher ile kolay baslatma saglamak
- Mobil cihazlarla bulut zorunlulugu olmadan LAN senkronu kurmak
- Yedekleme, veri tasima ve guncelleme akislarini guvenli hale getirmek

## Temel Ozellikler

- Kisi, telefon, e-posta, adres, firma, not ve profil fotografi yonetimi
- Sinirsiz telefon ve e-posta satiri
- Favoriler, gruplar, renkli etiketler ve akilli listeler
- Cop Kutusu ve geri yukleme
- Kisi aktivite/gecmis zaman cizelgesi
- Toplu islemlerde cok adimli Undo / Redo
- Duplicate tarama ve kayipsiz birlestirme
- VCF 3.0/4.0 ve UTF-8 CSV import/export
- Dogum gunu, yildonumu, ozel tarih ve Iletisimde Kal hatirlatmalari
- Android/iOS ile iki yonlu LAN senkronu
- Kalici `sync_uuid` kimligi ve ambiguity-safe identity fallback

## Platform Durumu

| Platform | Durum | Dagitim / Build | Not |
| --- | --- | --- | --- |
| Windows 10/11 x64 | ✅ Destekleniyor | Inno Setup + Portable | Ana masaustu platformu |
| Linux x64 | ✅ Destekleniyor | Native ELF + TAR.GZ | Kullanici seviyesinde kurulum |
| Android API 36 | 🧪 Test asamasi | Debug APK | LAN senkron istemcisi |
| iOS | 🧪 Test asamasi | Simulator build | Gercek cihaz release dagitimi henuz yok |

## Dil ve Yerellestirme

- Masaustu Swing arayuzu varsayilan olarak JVM/sistem locale degerini kullanir; Ayarlar > Genel bolumunden `Sistem Dilini Kullan`, `Turkce` veya `English` tercihi kalici olarak secilebilir.
- Masaustu urun adi Turkce sistemlerde **Turkuaz Telefon Rehberi**, diger sistemlerde **Turkuaz PhoneBook** olarak gorunur.
- Windows Inno Setup secilen kurulum diline gore `Turkuaz Telefon Rehberi` veya `Turkuaz PhoneBook` adini gosterir.
- Native Go launcher Windows'ta OS UI dilini, Linux'ta `LC_ALL`, `LC_MESSAGES` ve `LANG` degerlerini kullanarak splash, durum ve kullaniciya gosterilen hata metinlerini yerellestirir.
- Android varsayilan Ingilizce resource ve Turkce `values-tr` kaynaklari kullanir.
- iOS varsayilan Ingilizce ad/izin metinleri ile Turkce `InfoPlist.strings` kaynaklarini kullanir; senkron ekran mesajlari da tercih edilen UI diline gore secilir.
- Linux `.desktop` girdisi Ingilizce varsayilan ad ile Turkce `Name[tr]` / `Comment[tr]` degerlerini tasir.
- `Kurulum.sh` ve `Kaldir.sh` terminal mesajlari sistem locale degerine gore Turkce veya Ingilizce gosterilir.
- Ana logo ve splash marka adi yalnizca **Turkuaz** olarak kalir.

Masaustu dil tercihi `preferences.yml` icinde kalici tutulur. Language katalogu uygulama acilisinda olusturuldugu icin dil degisikligi uygulama yeniden baslatildiginda tum arayuze uygulanir. Teknik kimlikler ve mevcut kullanici veri yollari degismez.

## Hizli Baslangic

### Windows - Kurulumlu

Release paketindeki `TelefonRehberi-Setup-vX.Y.Z.exe` dosyasini calistirin. Varsayilan kurulum dizini:

`C:\Program Files\TurkuazLabs\TelefonRehberi`

Kullanici verisi Program Files altinda tutulmaz.

### Windows - Portable

Portable arsivi cikartip `TelefonRehberi.exe` dosyasini calistirin. Portable paket bootstrap dagitimidir: ilk acilista native launcher Temurin JRE 17, SQLite JDBC, SLF4J API ve FlatLaf bagimliliklarini indirir, mevcut SHA-256 dogrulamalarini yapar ve uygulamayi baslatir. Bu nedenle ilk portable acilisinda internet gerekir. Windows Setup paketi ise runtime ve bagimliliklari kendi icinde tasir.

### Linux

Linux full paketini cikarttiktan sonra `Kurulum.sh` ile kullanici hesabina kurulum yapabilirsiniz. Paket native ELF launcher ve `.desktop` girdisini icerir.

### Kaynaktan Gelistirme

Temel masaustu gereksinimleri: Java 17, Go 1.23.x, SQLite JDBC ve FlatLaf. Build/release/quality scriptleri `tools/` klasorundedir.

## Veri ve Gizlilik

Ana kisi veritabani yereldir; uygulamanin calismasi icin zorunlu TurkuazLabs bulut hesabi veya merkezi kisi verisi servisi gerekmez.

Windows SQLite:

`%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri\telefon-rehberi.db`

Linux XDG veri alani:

`$XDG_DATA_HOME/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`

Fallback:

`~/.local/share/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`

Windows preferences:

`%APPDATA%\TurkuazLabs\TelefonRehberi\config\preferences.yml`

### Mobil Senkron Guvenligi

- LAN senkronu varsayilan kapali gelir.
- Yalniz guvenilir yerel ag icin tasarlanmistir.
- Android tokeni Android Keystore + AES/GCM ile korunur ve giris alaninda maskelenir.
- Android cleartext senkron trafigi yerel ag hedefleriyle sinirlanir; sync kimligi/credential tercihleri Auto Backup'a dahil edilmez.
- iOS tokeni Keychain'de saklanir; Keychain yazma hatalari sessizce yutulmaz.
- Masaustu sync token dosyasi POSIX sistemlerde owner-only izinlerle korunur ve tamamlanmamis icerik gorunurlugu olmadan yayinlanir.
- HTTP request body varsayilan limiti 2 MiB'dir.
- Bearer token karsilastirmasi sabit zamanli yapilir.

## Yedekleme

SQLite yedekleri ham DB dosyasi kopyalamak yerine `VACUUM INTO` ile transaction-consistent snapshot olarak uretilir ve `PRAGMA integrity_check` ile dogrulanir. Otomatik yedekleme 7 gunluk aralikla calisir ve en yeni 5 kopya korunur; daha eski uygulama yedekleri retention temizligiyle silinir.

Windows yedek dizini:

`%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Yedekler\`

## Mimari

`Controller -> Service -> Repository/Model -> Tool -> View -> Language`

| Yol | Aciklama |
| --- | --- |
| `src/main/java/` | Java masaustu uygulamasi |
| `src/test/java/` | Java quality gate testleri |
| `launcher/native/` | Go Windows/Linux native launcher |
| `mobile/android/` | Android istemci |
| `mobile/ios/` | iOS istemci |
| `packaging/windows/` | Inno Setup paketleme |
| `packaging/linux/` | Linux kurulum ve desktop entry |
| `config/` | Merkezi konfigurasyon |
| `tools/` | Build, test ve release scriptleri |
| `docs/` | Teknik dokumantasyon |

## Build ve Kalite

CI su kontrolleri gerceklestirir:

- Java 17 compile ve SQLite quality gate
- Turkce/Ingilizce masaustu urun adi ve temel mesaj katalogu yerellestirme regression kontrolleri
- WAL-safe backup, sync UUID, history retention ve request limit testleri
- Go launcher native build
- Windows Inno Setup gercek silent install/uninstall smoke testi
- Linux native release build
- Android API 36 debug APK build
- iOS simulator build

## v2.38.3 Yama Ozeti

v2.38.3 yedekleme politikasini sadelestirir: otomatik SQLite yedegi artik her gun yerine **7 gunde bir** alinir, uygulama en fazla **5 yedek** saklar ve fazladan eski yedekleri otomatik temizler. Yedekleme ekranina ayrica **Eski Yedekleri Temizle** dugmesi eklendi.

## v2.38.2 Yama Ozeti

v2.38.2 Windows ikon zincirini duzeltir: masaustu ve Baslat menusu kisayollari Windows icon cache'ini kiracak surume ozel ICO yolu kullanir, Uygulamalar ve Ozellikler kaydi gercek Turkuaz ICO'sunu gosterir ve Swing pencere/taskbar ikonlari 512 px kaynaktan 16/20/24/32/40/48/64/128/256 px boyutlarda uretilir.

## v2.38.1 Yama Ozeti

v2.38.1, Turkuaz marka gorunusunu secili masaustu temasiyla hizalar. Uygulama ici marka simgesi Light/Dark palete uyum saglar, Windows native splash Java baslamadan once kayitli temayi okur ve Windows paketleme Light/Dark ICO varyantlari uretir. Buyutulmus dusuk-padding Windows kisayol ikonu da bu yamaya dahildir.

## v2.38.0 Ozeti

v2.38.0, v2.37.0 veri guvenligi temelini uluslararasi marka, Turkce/Ingilizce yerellestirme ve ek mobil/desktop guvenlik sertlestirmeleriyle genisletir:

- global urun/repository kimligi **Turkuaz PhoneBook**, dil bagimsiz ana marka **Turkuaz** oldu
- uygulama logosundaki dile bagli `Telefon Rehberi` yazisi kaldirildi
- masaustu Swing mesaj katalogu Turkce/Ingilizce sistem locale destegi kazandi
- contact method ve telefon ulke adlari, kalici veriyi degistirmeden kullanici diline gore gosteriliyor
- Windows installer, native launcher, Linux kurulum/kaldirma, Android ve iOS gorunen metinleri yerellestirildi
- telefon ulkesi sistem locale degerinden otomatik ve guvenli fallback ile seciliyor
- Android LAN cleartext hedef siniri, token maskeleme ve backup exclusion sertlestirmeleri eklendi
- iOS Keychain yazma hatalari gorunur hale getirildi
- masaustu sync token izinleri ve dosya yayinlama akisi sertlestirildi
- native launcher release cizgisi **2.5.0** oldu

## Dokumantasyon

- [CHANGELOG](./CHANGELOG.md)
- [Windows Installer](./docs/WINDOWS_INSTALLER.md)
- [User Data Storage](./docs/USER_DATA_STORAGE.md)
- [Mobile Sync API](./docs/MOBILE_SYNC_API.md)
- [Security](./SECURITY.md)
- [Third Party Notices](./THIRD_PARTY_NOTICES.md)
- [GitHub / Release Setup](./GITHUB_SETUP.md)

## Lisans

Repository public olsa da kod otomatik olarak acik kaynak lisansli degildir. Kullanim, kopyalama, degistirme ve dagitim kosullari [LICENSE](./LICENSE) dosyasinda tanimlanir.

## TurkuazLabs

Bu proje [TurkuazLabs](https://github.com/TurkuazLabs) tarafindan gelistirilmektedir.
