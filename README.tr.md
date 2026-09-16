# 📄 Dosya Yolu: /README.tr.md
# 📌 Amac: Turkuaz PhoneBook projesinin Turkce GitHub vitrini ve teknik ozetidir
# 📌 Modul - Markdown
# Version: 2.37.4
# Aciklama: Global marka kimligi, Turkce/Ingilizce yerellestirme, platform durumu, hizli baslangic, veri guvenligi, mimari ve release akislarini Turkce olarak toplar
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

<div align="center">
  <img src="assets/branding/app-icon-128.png" alt="Turkuaz" width="120">

  <h1>Turkuaz Telefon Rehberi</h1>

  <p><strong>TurkuazLabs tarafindan gelistirilen local-first kisi yonetimi ve mobil senkron uygulamasi.</strong></p>
  <p>Global urun adi: <strong>Turkuaz PhoneBook</strong> · Ana marka: <strong>Turkuaz</strong></p>

  <p><a href="README.md">English README</a></p>

  <p>
    <a href="https://github.com/TurkuazLabs/Turkuaz-PhoneBook/actions/workflows/build.yml"><img src="https://github.com/TurkuazLabs/Turkuaz-PhoneBook/actions/workflows/build.yml/badge.svg" alt="Build"></a>
    <img src="https://img.shields.io/badge/version-2.37.0-0aa6a6" alt="Version 2.37.0">
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

- Windows Inno Setup secilen kurulum diline gore `Turkuaz Telefon Rehberi` veya `Turkuaz PhoneBook` adini gosterir.
- Native Go launcher Windows'ta OS UI dilini, Linux'ta `LC_ALL`, `LC_MESSAGES` ve `LANG` degerlerini kullanir.
- Android varsayilan Ingilizce resource ve Turkce `values-tr` kaynaklari kullanir.
- iOS varsayilan Ingilizce ad/izin metinleri ile Turkce `InfoPlist.strings` kaynaklarini kullanir; senkron ekran mesajlari da sistem diline gore secilir.
- Linux `.desktop` girdisi Ingilizce varsayilan ad ile Turkce `Name[tr]` / `Comment[tr]` degerlerini tasir.
- `Kurulum.sh` ve `Kaldir.sh` terminal mesajlari sistem locale degerine gore Turkce veya Ingilizce gosterilir.
- Ana logo ve splash marka adi yalnizca **Turkuaz** olarak kalir.

Masaustu Swing arayuzunun genis metin katalogu Language katmaninda merkezidir; tam runtime dil secimi sonraki yerellestirme genisletmesidir.

## Hizli Baslangic

### Windows - Kurulumlu

Release paketindeki `TelefonRehberi-Setup-vX.Y.Z.exe` dosyasini calistirin. Varsayilan kurulum dizini:

`C:\Program Files\TurkuazLabs\TelefonRehberi`

Kullanici verisi Program Files altinda tutulmaz.

### Windows - Portable

Portable arsivi cikartip `TelefonRehberi.exe` dosyasini calistirin. Native launcher gerekli runtime ve bagimliliklari paket yapisindan kullanir.

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
- Android tokeni Android Keystore + AES/GCM ile korunur.
- iOS tokeni Keychain'de saklanir.
- HTTP request body varsayilan limiti 2 MiB'dir.
- Bearer token karsilastirmasi sabit zamanli yapilir.

## Yedekleme

SQLite yedekleri ham DB dosyasi kopyalamak yerine `VACUUM INTO` ile transaction-consistent snapshot olarak uretilir ve `PRAGMA integrity_check` ile dogrulanir.

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
- WAL-safe backup, sync UUID, history retention ve request limit testleri
- Go launcher native build
- Windows Inno Setup gercek silent install/uninstall smoke testi
- Linux native release build
- Android API 36 debug APK build
- iOS simulator build

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
