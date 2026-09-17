# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/CHANGELOG.md
# 📌 Amac: Turkuaz PhoneBook guncel surum degisikliklerini kaydeder ve eski tam surum gecmisine baglanti verir.
# 📌 Modul - Markdown
# Version: 2.38.0
# Aciklama: v2.38.0 marka, Turkce/Ingilizce yerellestirme, mobil/desktop guvenlik ve release hizalama surumudur.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

## v2.38.0

- Repository ve global urun kimligi `TurkuazLabs/Turkuaz-PhoneBook` / **Turkuaz PhoneBook** olarak yenilendi; `TelefonRehberi.exe`, Java package adlari, Inno Setup `AppId` ve mevcut kullanici veri yollari geriye donuk uyumluluk icin korundu.
- Dil bagimsiz ana marka **Turkuaz** olarak sabitlendi; uygulama logosundaki `Telefon Rehberi` gibi dile bagli metinler kaldirildi.
- Ana GitHub vitrini Ingilizce `README.md`, Turkce dokumantasyon ise `README.tr.md` olarak ayrildi.
- Masaustu Swing mesaj katalogu sistem/JVM locale degerine gore Turkce veya Ingilizce fallback kullanacak sekilde yerellestirildi.
- Masaustu gorunen urun adi Turkce sistemlerde **Turkuaz Telefon Rehberi**, diger dillerde **Turkuaz PhoneBook** olarak merkezi Language katmanindan secilir.
- Contact method etiketleri kalici veriyi degistirmeden locale'e gore gosterilir; telefon ulke adlari ve varsayilan telefon ulkesi sistem locale degerine gore yerellestirilir ve guvenli fallback uygular.
- Native Go launcher modul yolu `github.com/turkuazlabs/turkuaz-phonebook/launcher/native` olarak yenilendi; Windows UI dili ve Linux locale degerlerine gore splash, durum ve hata metinleri yerellestirildi.
- Windows Inno Setup Turkce kurulumda **Turkuaz Telefon Rehberi**, Ingilizce kurulumda **Turkuaz PhoneBook** gorunen adini kullanir.
- Linux `.desktop`, `Kurulum.sh` ve `Kaldir.sh` Ingilizce fallback + Turkce locale destegi kazandi.
- Android varsayilan Ingilizce resource ve Turkce `values-tr` kaynaklarina ayrildi; manifest uygulama adi `@string/app_name` uzerinden yerellestirildi ve API/transport hata metinleri Language katmanina tasindi.
- Android sync token girisi maskelendi; sync kimligi/credential tercihleri Auto Backup'tan haric tutuldu; cleartext LAN senkronu yerel ag endpointleriyle sinirlandi ve CI kontrolu eklendi.
- iOS varsayilan Ingilizce ad/izin metinleri ile Turkce `InfoPlist.strings` kaynaklarini kullanir; senkron ekran metinleri yerellestirildi ve Keychain token yazma hatalari sessizce yutulmaz.
- iOS senkron endpoint dogrulamasi Android ile hizalandi; cleartext HTTP yalniz loopback/private/link-local/yerel LAN hedeflerinde kabul edilir, public hedeflerde HTTPS zorunludur ve bu kural macOS Swift CI quality gate ile test edilir.
- Masaustu sync token dosyasi POSIX sistemlerde owner-only izinlerle korunur; token tamamlanmis gecici dosyadan ayni dizinde yayinlanarak kismi icerik gorunurlugu engellenir ve eszamanli ilk-olusturma yarisi guvenli yonetilir.
- Release surumleri **app 2.38.0**, **launcher 2.5.0**, **Android versionCode 580** ve **iOS build 580** olarak hizalandi.
- Windows/Linux build scriptleri ve generated release metadata v2.38.0 cizgisine tasindi; checked-in `updates/update-manifest.yml` gercek yeni binary hashleri uretilene kadar son yayinlanmis v2.37 referansi olarak acikca isaretlendi.

## v2.37.0

- WAL-guvenli SQLite backup `VACUUM INTO` + `PRAGMA integrity_check` ile yenilendi.
- Kalici `sync_uuid`, external/sync alias tablolari ve idempotent mobile upsert eklendi.
- Android native ID <-> sync UUID mappingi, Keystore AES/GCM token saklama ve e-posta duplicate fallback eklendi.
- iOS native ID <-> sync UUID mappingi ve Keychain token saklama eklendi.
- LAN mobil senkron varsayilan kapali; request body varsayilan 2 MiB ile sinirli.
- MobileSyncController payload mapping kurallari Tool/Service katmanina tasindi.
- History retention varsayilan 5000 kayit olarak DB seviyesinde uygulanir.
- ContactRepository toplu child loading ile N+1 sorgulari kaldirdi.
- Duplicate tarama indeksli aday uretimine, merge ise kayipsiz coklu iletisim + sync identity aktarimina gecti.
- Backup/import/export/duplicate scan Swing EDT disinda calisir.
- `schema_meta` / schema version altyapisi ve Java release quality gate eklendi.
- GitHub Build/Release workflow Windows installer smoke ve Java quality gate ile sertlestirildi.
- Mobil HTTP Bearer token karsilastirmasi sabit zamanli yapildi ve HTTP executor lifecycle uygulama kapanisina baglandi.
- Launcher 2.4.0, Android versionCode ve iOS build 570 olarak hizalandi.

## Eski Surum Gecmisi

v2.36.0 ve daha eski tum ayrintili degisiklik gecmisi, v2.38.0 duzenlemesi oncesindeki tam CHANGELOG ile birlikte [`docs/CHANGELOG_LEGACY.md`](./docs/CHANGELOG_LEGACY.md) dosyasinda korunur.
