# 📄 Dosya Yolu: /README.md
# 📌 Amac: Turkuaz Telefon Rehberi projesinin ana GitHub vitrini ve teknik ozetidir
# 📌 Modul - Markdown
# Version: 2.37.0
# Aciklama: Proje ozeti, platformlar, veri guvenligi, mimari ve gelistirme akislarini tek sayfada toplar

Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

# Turkuaz Telefon Rehberi v2.37.0

Java 17 + Swing + FlatLaf tabanli, SQLite kullanan masaustu kisi yonetimi uygulamasi.

Windows ve Linux masaustu kullanimi, Android/iOS ile yerel ag senkronu, portable calisma ve native launcher yapisi desteklenir.

[CHANGELOG](./CHANGELOG.md) | [SECURITY](./SECURITY.md) | [THIRD PARTY NOTICES](./THIRD_PARTY_NOTICES.md)

## Temel Ozellikler

- Kisi, telefon, e-posta, adres, firma ve not yonetimi
- Sinirsiz telefon ve e-posta satiri
- Profil fotografi
- Gruplar ve renkli etiketler
- Akilli listeler
- Favoriler
- Cop Kutusu ve geri yukleme
- Kisi degisiklik gecmisi
- Toplu islemler icin Undo/Redo
- VCF 3.0/4.0 import ve vCard 4.0 export
- UTF-8 CSV import/export
- Duplicate tarama ve kayipsiz birlestirme
- Dogum gunu, yildonumu ve ozel tarih hatirlatmalari
- Iletisimde Kal takip plani
- Android/iOS iki yonlu yerel ag senkronu
- Windows ve Linux native launcher
- Portable runtime

## v2.37.0 Ozeti

- SQLite yedekleri `VACUUM INTO` ve `PRAGMA integrity_check` ile dogrulanir.
- Her kisi kalici `sync_uuid` kimligi tasir.
- Android sync tokeni Keystore AES/GCM ile korunur.
- iOS sync tokeni Keychain ile korunur.
- Mobil LAN senkronu varsayilan kapali gelir.
- Mobil import request body limiti varsayilan 2 MiB'dir.
- History retention varsayilan 5000 kayittir.
- Kisi listeleme child verileri batch yuklenir; N+1 sorgulari kaldirilmistir.
- Duplicate tarama indeksli aday uretimi kullanir.
- Backup/import/export/duplicate scan gibi agir islemler Swing EDT disinda calisir.
- Java release quality gate ve Windows installer smoke testi bulunur.

Tum surum gecmisi icin [CHANGELOG.md](./CHANGELOG.md) dosyasina bakin.

## Platformlar

### Windows

Kurulumlu ve portable kullanim desteklenir.

Kurulumlu varsayilan uygulama yolu:

`C:\Program Files\TurkuazLabs\TelefonRehberi`

Kullanici verisi Program Files icinde tutulmaz.

### Linux

Native ELF launcher ve portable dagitim yapisi bulunur.

`Kurulum.sh` kullanici hesabina kurulum yapabilir ve `Terminal=false` desktop entry olusturur.

### Android / iOS

Mobil istemciler PC ile ayni guvenilir LAN/Wi-Fi aginda token korumali REST API uzerinden senkronize olur.

Kisi verisi GitHub'a gonderilmez.

## Ana Veri Konumlari

Ana SQLite veritabani uygulama klasorunden bagimsiz tutulur.

- Windows: `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri\telefon-rehberi.db`
- Linux XDG: `$XDG_DATA_HOME/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`
- Linux fallback: `~/.local/share/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`
- macOS hazirligi: `~/Library/Application Support/TurkuazLabs/TelefonRehberi/data/telefon-rehberi.db`

Eski portable `data/telefon-rehberi.db` ve `data/contacts.tsv` verileri uygun ilk acilista yeni veri konumuna migrate edilir.

## Kullanici Ayarlari

- Windows: `%APPDATA%\TurkuazLabs\TelefonRehberi\config\preferences.yml`
- Linux XDG: `$XDG_CONFIG_HOME/turkuazlabs/telefon-rehberi/preferences.yml`
- Linux fallback: `~/.config/turkuazlabs/telefon-rehberi/preferences.yml`

Teknik varsayilanlar `config/app.yml`, launcher ayarlari `config/launcher.yml` dosyalarinda tutulur.

## Yedekleme

Rehber DB yedekleri kullanici dosya alaninda tutulur.

- Windows: `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Yedekler\`
- Linux: `~/Contacts/Turkuaz Telefon Rehberi/Yedekler/` veya XDG Documents fallback alani

SQLite yedegi kisilerle birlikte grup, etiket, gecmis, akilli liste ve Cop Kutusu verilerini de korur.

## Portable Runtime

Native launcher gerekli bagimliliklari proje konfigurasyonuna gore hazirlayabilir:

- Eclipse Temurin JRE 17
- Xerial SQLite JDBC
- SLF4J API
- FlatLaf

Sistem `JAVA_HOME` veya `PATH` ayari degistirilmez.

## Mimari

Proje katmanli mimari kullanir:

`Controller -> Service -> Repository/Model -> Tool -> View -> Language`

- Controller: GUI request/event alir ve Service cagirir.
- Service: tum is kurallarini yonetir.
- Repository/Model: SQLite ve kalici storage islemlerini yapar.
- Tool: VCF/CSV, runtime, asset ve dis dunya adaptorlerini yonetir.
- View: Swing/FlatLaf GUI katmanidir.
- Language: kullanici metinlerini merkezilestirir.
- Config: teknik ve merkezi sabitleri tutar.

## Repository Yapisi

- `app/`: masaustu uygulama kaynaklari
- `android/`: Android istemci
- `ios/`: iOS istemci
- `config/`: teknik konfigurasyon
- `docs/`: proje dokumantasyonu
- `.github/workflows/`: build ve release otomasyonu

## Build ve Kalite

GitHub Actions build akisi masaustu, launcher ve mobil kalite kontrollerini calistirir.

Son guncel `main` buildi basarili durumdadir.

Release kalitesi icin Java quality gate ve Windows installer smoke testi bulunur.

## Guvenlik

Mobil LAN senkronu varsayilan kapali gelir ve yalniz guvenilir yerel ag icin tasarlanmistir.

Guvenlik bildirimleri icin [SECURITY.md](./SECURITY.md) dosyasini kullanin.

## Lisans

Lisans kosullari repository kokundeki [LICENSE](./LICENSE) dosyasinda tanimlidir.

## Turkuaz Labs

https://github.com/TurkuazLabs
