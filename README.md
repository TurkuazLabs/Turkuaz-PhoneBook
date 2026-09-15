# 📄 Dosya Yolu: /README.md
# 📌 Amac: Turkuaz PhoneBook projesinin ana GitHub vitrini ve teknik ozetidir
# 📌 Modul - Markdown
# Version: 2.37.3
# Aciklama: Global marka kimligi, yerellestirilmis urun adi, platform durumu, hizli baslangic, veri guvenligi, mimari ve release akislarini tek sayfada toplar
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

<div align="center">
  <img src="assets/branding/app-icon-128.png" alt="Turkuaz" width="120">

  <h1>Turkuaz PhoneBook</h1>

  <p><strong>Local-first contact management and mobile sync by TurkuazLabs.</strong></p>
  <p>Turkce arayuz adi: <strong>Turkuaz Telefon Rehberi</strong> · Ana marka: <strong>Turkuaz</strong></p>

  <p>
    <a href="https://github.com/TurkuazLabs/Turkuaz-PhoneBook/actions/workflows/build.yml"><img src="https://github.com/TurkuazLabs/Turkuaz-PhoneBook/actions/workflows/build.yml/badge.svg" alt="Build"></a>
    <img src="https://img.shields.io/badge/version-2.37.0-0aa6a6" alt="Version 2.37.0">
    <img src="https://img.shields.io/badge/Java-17-orange" alt="Java 17">
    <img src="https://img.shields.io/badge/SQLite-local-blue" alt="SQLite">
    <img src="https://img.shields.io/badge/Windows-supported-0078D4" alt="Windows">
    <img src="https://img.shields.io/badge/Linux-supported-FCC624" alt="Linux">
  </p>

  <p>
    <a href="#marka-ve-urun-adi">Marka</a> •
    <a href="#neden-turkuaz-phonebook">Neden?</a> •
    <a href="#temel-ozellikler">Ozellikler</a> •
    <a href="#platform-durumu">Platformlar</a> •
    <a href="#dil-ve-yerellestirme">Dil</a> •
    <a href="#hizli-baslangic">Hizli Baslangic</a> •
    <a href="#veri-ve-gizlilik">Veri ve Gizlilik</a> •
    <a href="#mimari">Mimari</a>
  </p>
</div>

---

## Marka ve Urun Adi

Repository ve global urun kimligi **Turkuaz PhoneBook** olarak kullanilir. Dil bagimsiz ana marka ise yalnizca **Turkuaz**'dir.

Gorunen urun adi kullanici diline gore yerellestirilir:

- Turkce: **Turkuaz Telefon Rehberi**
- Ingilizce: **Turkuaz PhoneBook**

Logo ve ikon kimligi dile bagli degildir. Teknik geriye donuk uyumluluk icin `TelefonRehberi.exe`, Java package adlari, Inno Setup `AppId` ve mevcut kullanici veri klasorleri yeniden adlandirilmaz.

Canonical repository:

`https://github.com/TurkuazLabs/Turkuaz-PhoneBook`

## Neden Turkuaz PhoneBook?

Turkuaz PhoneBook, kisi verisini bir bulut servisine zorunlu olarak tasimadan yonetmek isteyen kullanicilar icin gelistirilen masaustu odakli bir rehber uygulamasidir.

Ana veri SQLite veritabaninda kullanicinin kendi cihazinda tutulur. Windows ve Linux masaustu istemcileri ana calisma ortamini olusturur; Android ve iOS istemcileri ise ayni guvenilir yerel ag uzerinden masaustu uygulamasi ile senkronize olabilir.

Projenin temel hedefleri:

- Kisi verisini kullanicinin kontrolunde tutmak
- Basit bir telefon rehberinin otesine gecerek grup, etiket, gecmis, hatirlatma ve duplicate yonetimi sunmak
- Windows ve Linux'ta native launcher ile kolay baslatma saglamak
- Mobil cihazlarla bulut zorunlulugu olmadan yerel ag senkronu kurmak
- Yedekleme, veri tasima ve guncelleme akislarini guvenli hale getirmek

## Temel Ozellikler

### Kisi Yonetimi

- Kisi, telefon, e-posta, adres, firma ve not yonetimi
- Bir kisi icin sinirsiz telefon ve e-posta satiri
- Profil fotografi
- Favoriler
- Gruplar ve renkli etiketler
- Akilli listeler
- Cop Kutusu ve geri yukleme

### Gecmis ve Toplu Islemler

- Kisi degisiklik zaman cizelgesi
- Toplu islemler icin cok adimli Undo / Redo
- History retention kontrolu
- Duplicate tarama
- Kayipsiz kisi birlestirme

### Import / Export

- VCF 3.0 / 4.0 import
- vCard 4.0 export
- UTF-8 CSV import / export
- Eski portable TSV verilerinden otomatik gecis

### Hatirlatmalar

- Dogum gunu
- Yildonumu
- Ozel tarihler
- "Iletisimde Kal" takip plani

### Senkronizasyon

- Android / iOS ile iki yonlu LAN senkronu
- Kalici `sync_uuid` tabanli kisi kimligi
- Mobil native contact ID <-> sync UUID eslestirmesi
- Token korumali REST API
- Ambiguity-safe telefon / e-posta identity fallback

## Platform Durumu

| Platform | Durum | Dagitim / Build | Not |
| --- | --- | --- | --- |
| Windows 10/11 x64 | ✅ Destekleniyor | Inno Setup + Portable | Ana masaustu platformu |
| Linux x64 | ✅ Destekleniyor | Native ELF + TAR.GZ | Kullanici seviyesinde kurulum desteklenir |
| Android API 36 | 🧪 Test asamasi | Debug APK | LAN senkron istemcisi |
| iOS | 🧪 Test asamasi | Simulator build | Gercek cihaz dagitimi henuz release akisinda degil |

GitHub Actions `main` branch uzerinde Windows, Linux, Android API 36 ve iOS simulator buildlerini dogrular.

## Dil ve Yerellestirme

Dagitim katmanlarinda Turkce ve Ingilizce urun adi ayrimi uygulanir:

- Windows Inno Setup secilen kurulum diline gore `Turkuaz Telefon Rehberi` veya `Turkuaz PhoneBook` adini gosterir.
- Android varsayilan olarak Ingilizce resource kullanir; sistem dili Turkce ise `values-tr` kaynaklari devreye girer.
- iOS gorunen ad, izin metinleri ve senkron ekran mesajlarini sistem diline gore Turkce veya Ingilizce gosterir.
- Linux `.desktop` girdisi varsayilan Ingilizce ad/aciklama ile birlikte Turkce `Name[tr]` ve `Comment[tr]` degerlerini tasir.
- Ana logo ve splash marka adi yalnizca **Turkuaz** olarak dil bagimsiz tutulur.

Masaustu Swing arayuzunun genis metin katalogu Language katmaninda merkezidir; tam runtime dil secimi mevcut teknik kimlik ve veri yollarini degistirmeden genisletilebilir.

## Hizli Baslangic

### Windows - Kurulumlu

Release paketindeki Inno Setup dosyasini calistirin:

`TelefonRehberi-Setup-vX.Y.Z.exe`

Varsayilan kurulum dizini:

`C:\Program Files\TurkuazLabs\TelefonRehberi`

Kullanici verisi Program Files altinda tutulmaz.

### Windows - Portable

Portable arsivi cikartin ve:

`TelefonRehberi.exe`

ile uygulamayi baslatin. Native launcher gerekli runtime ve bagimliliklari paket yapisindan kullanir.

### Linux

Linux full paketini cikarttiktan sonra `Kurulum.sh` ile kullanici hesabina kurulum yapilabilir. Paket native ELF launcher ve masaustu `.desktop` girisi icerir.

### Kaynaktan Gelistirme

Temel masaustu gereksinimleri:

- Java 17
- Go 1.23.x
- SQLite JDBC
- FlatLaf

Release ve kalite scriptleri `tools/` klasorundedir. GitHub Actions ayni kalite kapilarini otomatik olarak calistirir.

## Veri ve Gizlilik

Turkuaz PhoneBook'un ana kisi veritabani yereldir. Uygulamanin calismasi icin zorunlu bir TurkuazLabs bulut hesabi veya merkezi kisi verisi servisi gerekmez.

### Windows Ana Veri

`%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri\telefon-rehberi.db`

SQLite WAL ve SHM dosyalari ayni `Veri` klasorunde bulunur.

### Linux Ana Veri

XDG kullaniliyorsa:

`$XDG_DATA_HOME/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`

Fallback:

`~/.local/share/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`

### Kullanici Ayarlari

Windows:

`%APPDATA%\TurkuazLabs\TelefonRehberi\config\preferences.yml`

Linux:

`$XDG_CONFIG_HOME/turkuazlabs/telefon-rehberi/preferences.yml`

veya:

`~/.config/turkuazlabs/telefon-rehberi/preferences.yml`

### Mobil Senkron Guvenligi

- LAN senkronu varsayilan olarak kapali gelir.
- Yalniz guvenilir yerel ag icin tasarlanmistir.
- Android tokeni Android Keystore + AES/GCM ile korunur.
- iOS tokeni Keychain'de saklanir.
- HTTP request body varsayilan limiti 2 MiB'dir.
- Bearer token karsilastirmasi sabit zamanli yapilir.

Mobil senkron tasarimi icin [docs/MOBILE_SYNC_API.md](./docs/MOBILE_SYNC_API.md) dosyasina bakin.

## Yedekleme

SQLite yedekleri ham DB dosyasi kopyalamak yerine transaction-consistent snapshot mantigiyla uretilir:

- `VACUUM INTO`
- `PRAGMA integrity_check`
- Gecici `.partial-*` dosyasindan final dosyaya guvenli tasima

Yedekler kisi verisinin yaninda grup, etiket, gecmis, akilli liste ve Cop Kutusu verilerini de korur.

Windows yedek dizini:

`%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Yedekler\`

Linux:

`~/Contacts/Turkuaz Telefon Rehberi/Yedekler/`

veya XDG Documents fallback alani.

## Portable Runtime

Native launcher, sistem Java kurulumuna bagimli kalmadan uygulama runtime'ini yonetebilir.

Paketlenen temel bagimliliklar:

- Eclipse Temurin JRE 17
- Xerial SQLite JDBC
- SLF4J API
- FlatLaf

Sistem `JAVA_HOME` veya `PATH` ayarlari degistirilmez.

## Mimari

Proje katmanli mimari kullanir:

`Controller -> Service -> Repository/Model -> Tool -> View -> Language`

| Katman | Sorumluluk |
| --- | --- |
| Controller | GUI event/request alir, Service katmanini cagirir |
| Service | Is kurallarini ve uygulama akislarini yonetir |
| Repository / Model | SQLite ve kalici veri islemlerini yonetir |
| Tool | VCF/CSV, runtime, asset, HTTP ve dis dunya adaptorlerini kapsar |
| View | Swing / FlatLaf kullanici arayuzudur |
| Language | Kullaniciya gorunen metinleri merkezilestirir |
| Config | Teknik varsayilanlar ve merkezi sabitlerdir |

## Repository Yapisi

| Yol | Aciklama |
| --- | --- |
| `src/main/java/` | Java masaustu uygulamasi |
| `src/test/java/` | Java quality gate testleri |
| `launcher/native/` | Go tabanli Windows / Linux native launcher |
| `mobile/android/` | Android istemci |
| `mobile/ios/` | iOS istemci |
| `packaging/windows/` | Inno Setup Windows paketleme |
| `packaging/linux/` | Linux kurulum ve desktop entry dosyalari |
| `config/` | Merkezi uygulama / launcher konfigurasyonu |
| `tools/` | Build, test ve release scriptleri |
| `docs/` | Teknik ve urun dokumantasyonu |
| `updates/` | Update manifest sablonu |
| `.github/workflows/` | CI ve release otomasyonu |

## Build ve Kalite

CI su temel kontrolleri gerceklestirir:

- Java 17 compile ve SQLite runtime quality gate
- WAL-safe backup testi
- `sync_uuid` ve mobile identity idempotency testleri
- History retention testi
- Request body limit testi
- Kullanici preference round-trip testi
- Go launcher testleri ve native build
- Windows Inno Setup gercek silent install / uninstall smoke testi
- Linux native release build
- Android API 36 debug APK build
- iOS simulator build

Build durumu GitHub Actions rozetinden takip edilebilir.

## v2.37.0 Ozet

v2.37.0 veri guvenligi, senkron kimligi, performans ve release kalite altyapisina odaklanan surumdur.

One cikan degisiklikler:

- WAL-guvenli SQLite backup
- Kalici `sync_uuid`
- Android Keystore ve iOS Keychain token korumasi
- Duplicate scan performans iyilestirmesi
- Repository child data batch loading
- EDT disi agir islemler
- Schema version altyapisi
- Java quality gate
- Windows installer smoke testi
- Cross-platform GitHub Actions dogrulamasi

Tum surum gecmisi icin [CHANGELOG.md](./CHANGELOG.md) dosyasina bakin.

## Dokumantasyon

- [CHANGELOG](./CHANGELOG.md) - Surum gecmisi
- [Windows Installer](./docs/WINDOWS_INSTALLER.md) - Kurulum ve auto-update
- [User Data Storage](./docs/USER_DATA_STORAGE.md) - Veri konumlari
- [Mobile Sync API](./docs/MOBILE_SYNC_API.md) - Mobil LAN senkron protokolu
- [Security](./SECURITY.md) - Guvenlik bildirimleri
- [Third Party Notices](./THIRD_PARTY_NOTICES.md) - Ucuncu taraf bagimliliklar
- [GitHub / Release Setup](./GITHUB_SETUP.md) - Build ve release akisi

## Guvenlik

Bir guvenlik acigi bildirmek icin repository'deki [SECURITY.md](./SECURITY.md) talimatlarini kullanin. Hassas guvenlik konularini herkese acik issue olarak paylasmayin.

## Lisans

Bu repository public olsa da kod otomatik olarak acik kaynak lisansli degildir. Kullanim, kopyalama, degistirme ve dagitim kosullari repository kokundeki [LICENSE](./LICENSE) dosyasinda tanimlanir.

## TurkuazLabs

Bu proje [TurkuazLabs](https://github.com/TurkuazLabs) tarafindan gelistirilmektedir.
