# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/CHANGELOG.md
# 📌 Amac: Turkuaz Telefon Rehberi surum degisikliklerini kaydeder.
# 📌 Modul - Markdown
# Version: 2.37.0
# Aciklama: v2.37.0 veri guvenligi, sync kimligi, performans ve quality gate surumudur.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language


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
- Kullanici ayarlari Program Files configinden ayrilarak OS kullanici preferences alanina tasindi.
- Launcher user preference update override'i ve installed update token davranisi duzeltildi.
- `schema_meta` / schema version altyapisi ve Java release quality gate eklendi.
- GitHub Build/Release workflow Windows installer smoke ve Java quality gate ile sertlestirildi.
- SQLite JDBC runtime icin SLF4J API bagimliligi portable launcher, installer ve quality gate classpathine acikca eklendi.
- Mobil HTTP Bearer token karsilastirmasi sabit zamanli yapildi; HTTP executor shutdown lifecycle uygulama kapanisina baglandi.
- Backup once gecici `.partial-*` snapshot uretir, integrity kontrolunden sonra atomik olarak final dosyaya tasir; ayni saniyedeki yedek adlari milisaniye ile ayrilir.
- Natural identity ile eslesen mobil update artik MOBILE_SYNC history snapshotini da kaybetmez.
- Android/iOS kalan contact/token hata metinleri Language katmaninda merkezilestirildi.
- Android/iOS natural identity fallback birden fazla adayda otomatik mapping yapmayacak sekilde ambiguity-safe hale getirildi.
- Launcher 2.4.0, Android versionCode ve iOS build 570 olarak hizalandi.

## v2.36.0

- Program Files kurulumlu Windows surumune GitHub Releases tabanli Inno Setup auto-update eklendi.
- Launcher yeni surumde `update-manifest.yml` icindeki Setup asset ve SHA-256 degerini kullanir.
- Setup EXE `%LOCALAPPDATA%` launcher cache alanina indirilir, SHA-256 dogrulanir ve Windows UAC `runas` ile baslatilir.
- Auto-update Inno Setup `/AUTOUPDATE` modunda sessiz kurulum yapar ve tamamlandiginda Telefon Rehberi'ni normal kullanici olarak yeniden acar.
- Kurulumlu surumde `update_enabled` artik kapatilmaz; Program Files dosyalari launcher tarafindan dogrudan degistirilmez.
- GitHub Release workflow Setup EXE SHA-256 degerini update manifestine otomatik ekler.
- Portable Windows ve Linux native updater davranisi korunur.
- Launcher surumu 2.3.0, Android versionCode ve iOS build numarasi 560 olarak hizalandi.


## v2.35.0

- Windows Inno Setup kurulum modulu eklendi.
- Varsayilan kurulum yolu `C:\Program Files\TurkuazLabs\TelefonRehberi` olarak belirlendi.
- Setup payload native EXE, JAR, Temurin JRE 17, SQLite JDBC, FlatLaf ve branding assetlerini birlikte paketler.
- Program Files kurulum modu `config/installed.mode` ile portable moddan ayrilir.
- Kurulumlu launcher yazilabilir cache/log/state verisini `%LOCALAPPDATA%\TurkuazLabs\TelefonRehberi\Launcher` altinda tutar.
- Kurulumlu surumde launcher in-place Program Files update yapmaz; yeni surum Setup EXE ile kurulur.
- Portable launcher kendi bootstrap/update davranisini korur.
- Uninstall uygulama dosyalarini kaldirir; `Contacts\Turkuaz Telefon Rehberi` altindaki DB, export ve yedekleri korur.
- GitHub Actions Windows release akisina Inno Setup derleme ve `TelefonRehberi-Setup-v2.35.0.exe` asseti eklendi.
- Android versionCode ve iOS build numarasi 550 olarak hizalandi.

## v2.34.1

- Windows SQLite ana veritabani `%APPDATA%` altindan `Contacts\Turkuaz Telefon Rehberi\Veri` klasorune tasindi.
- `telefon-rehberi.db`, `-wal` ve `-shm` ayni gorunur `Veri` klasorunde tutulur.
- Eski APPDATA DB bundle'i hedef bos ise otomatik kopyalanir; mevcut hedef DB asla ezilmez.

## v2.34.0

- Windows kullaniciya gorunen export ve backup dosyalari `Contacts\Turkuaz Telefon Rehberi` altina ayrildi.
- Linux kullanici dosyalari varsa `~/Contacts`, yoksa XDG Documents altina yerlestirilir.
- Linux native launcher ve masaustu kurulum dosyalari eklendi.

## v2.33.3

- Windows kullanici giris noktasi `TelefonRehberi.exe` native launcher olarak degistirildi.
- Kullanici acilisinda PowerShell ve konsol penceresi kaldirildi.
- Native Win32 splash; Java, bagimlilik, update ve uygulama acilis durumunu gosterir.
- Launcher mimarisi Go ile Controller -> Service -> Repository/Tool -> View/Language katmanlarina ayrildi.
- Native launcher GitHub Releases uzerinden kendi guncellemesini staged helper ile guvenli uygular.
- Portable ZIP ve Windows kisayollari dogrudan EXE baslatir.
- GitHub Actions masaustu buildine Go 1.23 ve native EXE release asseti eklendi.

## 2.33.2

- Kisiler tarayicisinin ust kontrol alani tek bir kompakt aksiyon satirinda toplandi.
- Yeni Kisi ana aksiyonu basliga tasindi; alttaki sabit Yeni Kisi/Yenile satiri kaldirildi.
- Kaydedilmis gorunumler `Gorunum` acilir menusunden secilir, kaydedilir ve yonetilir.
- Yenile ve tum filtreleri temizle islemleri `...` menusune alindi.
- Filtre butonu aktif yapisal filtre sayisini gosterir.
- Kisi liste satirlari daha kompakt ve duz hale getirildi; normal satirlardaki gereksiz kart cercevesi kaldirildi.
- Android versionCode ve iOS build numarasi 532 olarak v2.33.2 ile hizalandi.

## 2.33.1

- Sol menu kalabaligini azaltmak icin ikincil sayfalar acilir/kapanir gruplara ayrildi.
- `Rehber` bolumunde yalniz Ana Sayfa, Kisiler ve Favoriler surekli gorunur tutuldu.
- `Duzenle` grubu Gruplar, Etiketler ve Akilli Listeler sayfalarini toplar.
- `Veri ve Araclar` grubu Ice / Disa Aktar, Mobil Senkron, Yedekleme ve Duzelt ve Yonet sayfalarini toplar.
- `Arsiv` grubu Gecmis ve Cop Kutusu sayfalarini toplar.
- Accordion davranisiyla ayni anda yalniz bir ikincil grup acik kalir; aktif alt sayfaya gecildiginde ilgili grup otomatik acilir.
- Alt menu satirlari daha kucuk ve girintili, marka alani ve navigasyon yukseklikleri daha kompakt hale getirildi.
- Ayarlar ve Hakkinda ana navigasyondan ayri alt bolumde tutuldu.
- Android versionCode ve iOS build numarasi 531 olarak v2.33.1 ile hizalandi.

## 2.33.0

- Onemli tarihler ve tarih bazli hatirlatma kurallari eklendi.
- Iletisimde Kal 30/90/180/365 gun dongusu eklendi.
- Takip Gereken dashboard sayaci eklendi.
- History, CSV ve vCard yeni alanlari kayipsiz tasiyacak sekilde genisletildi.
- SQLite schema `contact_important_dates` ve acik reminder kolonlariyla migrate edilir.

## v2.32.1

- SQLite rehber veritabani uygulama klasorunden ayrilarak standart OS kullanici veri dizinine tasindi.
- Windows icin `%APPDATA%\TurkuazLabs\TelefonRehberi`, Linux icin XDG_DATA_HOME veya `~/.local/share/turkuazlabs/telefon-rehberi` kullanilir.
- DB yedekleri, sync token, saved views ve kullanici tercihleri ayni kalici kullanici veri kokune alindi.
- UserDataMigrationService ve UserDataMigrationRepository eski portable veriyi hedefteki mevcut dosyalari ezmeden bir kez migrate eder.
- SQLite `-wal` ve `-shm` sidecar dosyalari ana DB ile birlikte korunur; ana DB en son finalize edilerek yarim migration riski azaltildi.
- Eski kaynak portable veri migration sonrasinda silinmez; uygulama yalniz yeni kalici konumdan calisir.
- Launcher JAR update backup dizini `updates/app-backups` olarak rehber DB backup alanindan ayrildi.
- Windows Kurulum/Kaldir scriptleri yeni veri ayrimina uyarlandi; normal kaldirma kalici kullanici verisini korur.
- Ayarlar ve Yedekleme ekranlarindaki path etiketleri yeni kullanici veri dizinini gosterir.
- Android versionCode ve iOS build numarasi 521 olarak v2.32.1 ile hizalandi.

## v2.32.0

- Kisi profiline mevcut history altyapisini kullanan `Aktivite` zaman cizelgesi eklendi.
- ContactHistoryRepository kisi kimligine gore kronolojik history sorgusu kazandi.
- ContactService timeline kayitlarini `Tumu`, `Degisiklik`, `Aktarim` ve `Geri Yukleme` kategorilerine ayirir.
- ContactActivityFeed filtre sayaclarini ve config limiti uygulanmis gorunur kayitlari tek modelde tasir.
- Profil secimi veya activity filtresi degistiginde Controller yalnizca Service cagrisini tetikler; filtre kurali Controller/View katmanina tasinmadi.
- HistoryActionText her history aksiyonu icin kullanici dostu timeline aciklamasi sunar.
- Timeline satirlari yeni ContactActivityTimelineRow View bileseninde dikey rail/nokta diliyle cizilir.
- Profil avatar ve baslik boyutlari daha kompakt hale getirildi.
- `history_limit` ve `contact_activity_limit` portable YAML config ayarlari eklendi.
- Eski v2.30/v2.31 `app.yml` dosyalarinda yeni limitler yoksa merkezi Config fallback degerleri kullanilir; JAR-only updater geriye uyumlu kalir.
- Android versionCode ve iOS build numarasi 520 olarak v2.32.0 ile hizalandi.

## v2.31.0

- Toplu firma, kategori, favori, grup, etiket ve Cop Kutusu islemleri cok adimli Undo/Redo gecmisine tasindi.
- Gecmis Service katmaninda iki sinirli stack ile tutulur; kapasite `bulk_history_limit` config degerinden gelir.
- `Ctrl+Z` Undo ve `Ctrl+Y` Redo masaustu kisayollari eklendi.
- 12 saniyelik durum bildirimi artik yalnizca View gorunurlugunu kapatir; Service gecmisini temizlemez.
- Snapshot tabanli alan degisikliklerinde undo/redo karsi snapshoti islem aninda guvenli sekilde uretilir.
- Grup/etiket ve Cop Kutusu islemleri tiplerine gore ters/yineleme islemlerini Service katmaninda uygular.
- Basarili yeni bir toplu islem redo dalini temizler; no-op islemler mevcut gecmisi korur.
- Android versionCode ve iOS build numarasi 510 olarak v2.31.0 ile hizalandi.

## v2.30.0

- Toplu firma, kategori, favori, grup, etiket ve Cop Kutusu islemleri tek adimlik Geri Al destegi kazandi.
- Son geri alinabilir islem Service katmaninda immutable `BulkUndoState` olarak tutulur.
- Kisi alan degisikliklerinde tam Contact snapshot, grup/etiket islemlerinde sadece gercekten degisen uyelikler saklanir.
- Cop Kutusuna toplu tasima, soft-delete kimliklerini restore ederek geri alinabilir.
- Durum cubuguna 12 saniyelik kompakt Geri Al bildirimi eklendi.
- Geri alma islemi history ve filtre/yonetim ekranlarini yeniden senkronlar.
- Android versionCode ve iOS build numarasi 500 olarak v2.30.0 ile hizalandi.

## v2.29.0

- Toplu firma ve kategori guncelleme eklendi.
- Toplu gruptan cikarma ve etiketten cikarma eklendi.
- Secili kisiler icin VCF/CSV disa aktarma eklendi.
- Toplu islem paneli uc islevsel karta ayrildi.
- Firma/kategori toplu degisiklikleri history snapshot kaydi olusturur.
- Grup ve etiket Repository katmanina batch remove islemleri eklendi.
- Android versionCode ve iOS build numarasi 490 olarak v2.29.0 ile hizalandi.

## v2.28.0

- Kisi listesi `MULTIPLE_INTERVAL_SELECTION` ile Ctrl/Shift coklu secim destegi kazandi.
- Birden fazla kisi secildiginde profil/editor yerine responsive Toplu Islemler paneli gosterilir.
- Toplu gruba ekleme ve etikete ekleme Service -> Repository batch akisiyla eklendi.
- Toplu favori yapma/favoriden cikarma ContactService uzerinden history snapshot koruyarak calisir.
- Toplu Cop Kutusuna Tasima ayri onay adimiyla eklendi.
- Liste yenilemelerinde secili kisi kimlikleri korunarak coklu secim yeniden uygulanir.
- Android versionCode ve iOS build numarasi 480 olarak v2.28.0 ile hizalandi.

## v2.27.0

- Kaydedilmis gorunumlar icin Yeniden Adlandir ve Kopyala aksiyonlari eklendi.
- Ayni isimli rename/copy cakismalari Service katmaninda engellendi.
- Bir kayit Varsayilan Gorunum olarak isaretlenebilir ve bu secim kaldirilabilir.
- Tek varsayilan gorunum kurali Service katmaninda uygulanir.
- Varsayilan gorunum uygulama acilisinda filtre + siralama durumuyla otomatik uygulanir.
- Varsayilan durum UUID tabanli gorunum YAML dosyasinda `default_view` alaniyla saklanir.
- Kaydedilmis gorunum secicisinde varsayilan kayit `[Varsayilan]` etiketiyle gosterilir.
- Dar kisi panelini korumak icin Kaydet disindaki gorunum islemleri kompakt `...` menusunde toplandi.
- Android versionCode ve iOS build numarasi 470 olarak v2.27.0 ile hizalandi.

## v2.26.0

- Filtre + siralama kombinasyonlari isim verilerek kaydedilebilir hale getirildi.
- Kaydedilmis gorunum secicisi kisi tarayici kartina eklendi.
- Kaydetme ayni isimde mevcut gorunumu gunceller; gereksiz kopya olusmaz.
- Kaydedilmis gorunumlar `data/saved-views` altinda UUID tabanli ayri YAML dosyalarinda saklanir.
- Gorunum metinleri storage katmaninda guvenli Base64 URL kodlamasi ile saklanir.
- Kaydedilmis gorunum tek tikla uygulanabilir ve secili gorunum silinebilir.
- Kullanici filtre, siralama, favori veya arama durumunu degistirdiginde secili gorunum otomatik olarak serbest moda doner.
- Android versionCode ve iOS build numarasi 460 olarak v2.26.0 ile hizalandi.

## v2.25.0

- Kisi listesine 6 tip guvenli siralama secenegi eklendi: Favoriler Once, Ad A-Z, Ad Z-A, Firma A-Z, Sehir A-Z ve Son Degistirilen.
- ContactSort enum modeli ve kullanici dostu ContactSortText dil eslemesi eklendi.
- Siralama secici mevcut filtre cekmecesine 6. responsive kontrol olarak eklendi.
- Varsayilan disindaki siralama aktif chip olarak gosterilir ve tek tikla varsayilana doner.
- Filtre temizleme aksiyonu siralama tercihini degistirmeden yalnizca yapisal filtreleri temizler.
- ContactFilter modeli siralama bilgisini tasiyacak sekilde genisletildi.
- ContactFilterService tum filtrelerden sonra merkezi siralama uygular.
- Son Degistirilen icin ContactRepository updated_at haritasini saglayan read metodu kazandi.
- Android versionCode ve iOS build numarasi 450 olarak v2.25.0 ile hizalandi.

## v2.24.0

- Kisi tarayicisina firma, sehir, kategori, grup ve etiket filtreleri eklendi.
- Favoriler dahil tum filtreler ContactFilter modeliyle tek sorgu sozlesmesinde toplandi.
- ContactFilterService arama ve coklu filtreleme kurallarini merkezi Service katmanina tasidi.
- Dinamik filtre secenekleri ContactFilterOptions modeliyle repository verilerinden uretilir.
- Aktif filtreler tek tek kaldirilabilen responsive chipler olarak gosterilir.
- Filtre cekmecesi acikken chip ozeti gizlenerek minimum pencerede kisi listesinin gorunur kalmasi saglandi.
- Kisi tarayici minimum genisligi yeni filtre kontrollerine uygun olarak 300px yapildi.
- Android versionCode ve iOS build numarasi 440 olarak v2.24.0 ile hizalandi.

## v2.23.0

- Mobil Senkron sayfasi servis durumu, baglanti adresi ve guvenlik tokeni icin responsive kartlara tasindi.
- Senkron baglanti bilgisi aksiyonu yeni bilgi kartina alindi.
- Yedekleme sayfasi yedek durumu, yedek konumu, yeni yedek aksiyonu ve yedek gecmisi olarak ayrildi.
- Yedek gecmisi icin BackupListCellRenderer eklendi.
- Ayarlar sayfasi modern kart tabanli ve dikey kaydirilabilir sekme iceriklerine donusturuldu.
- Genel, Gorunum, Yedekleme, Senkron ve Guncelleme ayarlari aciklayici kartlara tasindi.
- Senkron ayarlarinda yeniden baslatma gereksinimi arayuzde acik hale getirildi.
- Android versionCode ve iOS build numarasi 430 olarak v2.23.0 ile hizalandi.

## v2.22.0

- Kisi Gecmisi modern kart listesi ve secili snapshot detay paneliyle yenilendi.
- Gecmis islem kodlari HistoryActionText ile kullanici dostu Turkce etiketlere cevrildi.
- Duzelt ve Yonet ekrani mukerrer aday liste + karsilastirma detay workspace yapisina tasindi.
- Cop Kutusu secili kisi ayrintilari ve geri yukle/kalici sil aksiyonlarini ayri detay kartinda toplar.
- Ice / Disa Aktar ekrani responsive import, VCF export ve CSV export kartlariyla yeniden tasarlandi.
- OperationsListCellRenderer ortak View bileseni eklendi.
- Arac ekranlari genis pencerede iki kolon, dar pencerede tek kolon responsive davranir.
- Android versionCode ve iOS build numarasi 420 olarak v2.22.0 ile hizalandi.

## v2.21.0

- Gruplar, Etiketler ve Akilli Listeler iki panelli modern yonetim workspace yapisina tasindi.
- Grup ve etiket listelerinde renk noktasi, ad ve kisi sayisi ayri gorunur hale getirildi.
- Secili grup/etiket detay kartinda renk, uye sayisi ve mevcut secili kisi bilgisi gosterilir.
- Akilli Liste kural alanlari ham teknik degerler yerine kullanici dostu Turkce etiketlerle sunulur.
- Yonetim alanlari genis pencerede iki kolon, minimum pencerede tek kolon responsive davranir.
- ManagementListCellRenderer ortak View bileseni eklendi.
- LabelColorPalette ile yonetim renk anahtarlari merkezi Config katmanina alindi.
- Android versionCode ve iOS build numarasi 410 olarak v2.21.0 ile hizalandi.

## v2.20.0

- Ana Sayfa istatistikleri breakpoint tabanli ResponsiveCardGridPanel ile yeniden duzenlendi.
- Toplam kisi, favori, bugun dogum gunu, firma ve sehir kartlari ikon ve aciklama satirlariyla yenilendi.
- Hizli Baslangic ile Rehber Durumu kartlari dar pencerede otomatik tek kolona gecer.
- Dashboard senkron ve yedek durumlari icin ayri JLabel nesneleri kullanilarak coklu-parent Swing hatasi giderildi.
- Yerel veri durumu teknik dosya adi yerine kullanici odakli Hazir bilgisiyle gosterilir.
- Hizli Bul, Duzenli Tut ve Verinizi Koru ozellik alanlari eklendi.
- Ana Sayfa VerticalScrollablePanel ile minimum pencere boyutunda tasmasiz hale getirildi.
- Android versionCode ve iOS build numarasi 400 olarak v2.20.0 ile hizalandi.

## v2.19.0

- Sol kisi listesi modern kart satiri gorunumune tasindi.
- Secili ve hover satir durumlari ayri yuzey vurgulariyla belirginlestirildi.
- Kisi adi, birincil telefon/e-posta ve firma-kategori meta bilgisi ayri satirlarda gosterilir.
- Favori kisiler sag ustte yildiz simgesiyle korunur.
- Profil fotograflari listede dairesel maske ile cizilir; fotograf yoksa harf avatari kullanilir.
- Uzun liste metinleri ellipsis ile kisaltilir ve tam bilgi tooltip icinde sunulur.
- Kompakt mod iki satirli daha sik gorunume gecerek mevcut ayar davranisini korur.
- Android versionCode ve iOS build numarasi 390 olarak v2.19.0 ile hizalandi.

## v2.18.0

- Kisi profil gorunumu editor ile ayni responsive alan karti tasarimina tasindi.
- Profil alanlari genis alanda iki kolon, dar alanda tek kolon yerlesir.
- Temel, iletisim, is, adres ve ek bilgi kartlari ayri okunabilir bolumlere donusturuldu.
- Firma, unvan, web, adres, il, ilce, posta kodu ve ulke degerleri ayri profil alanlari olarak gosterilir.
- Notlar ile grup/etiket chipleri Ek Bilgiler kartinda birlestirildi.
- Eski HTML ozet kartlari ve yinelenen adres/is ozet metotlari kaldirildi.
- ResponsiveInfoPanel ortak View bileseni eklendi.
- Android versionCode ve iOS build numarasi 380 olarak v2.18.0 ile hizalandi.

## v2.17.0

- Yeni `ResponsiveFormPanel` View bileseni eklendi.
- Kisi editoru genis alanda 2 kolon, dar alanda 1 kolon olacak sekilde breakpoint tabanli hale getirildi.
- Form etiketleri alanlarin ustuna tasinarak yatay alan kaybi azaltildi.
- Temel, Profil, Telefonlar, E-postalar, Is, Adres ve Ek Bilgiler modern bolum kartlarina ayrildi.
- Adres ve not alanlari tam genislik ve kontrollu dikey kaydirma davranisi kazandi.
- Firma, unvan, web sitesi, il, ilce, posta kodu ve ulke alanlarina merkezi placeholder metinleri eklendi.
- Editor sekme tipi, sekme yuksekligi ve form breakpoint olculeri `UiConfig` icinde merkezilestirildi.
- Android versionCode ve iOS build numarasi 370 olarak v2.17.0 ile hizalandi.

## v2.16.0

- TurkuazLabs, resmi web ve Buy Me a Coffee destek satirlari tiklanabilir link bilesenine donusturuldu.
- `ExternalLinkService` eklendi; harici web acma kurali Service katmanina alindi.
- `DesktopActionTool` genel web URI destegi kazandi.
- Marka ve destek URL degerleri `AppConfig` icinde merkezi hale getirildi.
- Sayfa basliklarindaki `vX.Y.Z` rozeti kaldirildi.
- Uygulama surumu Windows baslik cubugunda `Turkuaz Telefon Rehberi - vX.Y.Z` biciminde gosterilir.
- Controller initialize icindeki yinelenen Cop Kutusu restore event baglantisi temizlendi.
- Android versionCode ve iOS build numarasi 360 olarak v2.16.0 ile hizalandi.

## v2.15.0

- Hakkinda sayfasi teknik stack satiri yerine urun odakli kisa tanitim metniyle yenilendi.
- Uygulama surumu, resmi site, web ve destek satirlari modern tek kart icinde toplandi.
- Veri dosyasi ve GitHub satirlari Hakkinda gorunumunden kaldirildi.
- TurkuazLabs marka odakli aciklama paragrafi eklendi.
- Alt bilgi icin vurgulu bilgi kutusu tasarlandi.
- Hakkinda icerigi `VerticalScrollablePanel` ile gelecekteki ek iceriklere hazir hale getirildi.
- Android versionCode ve iOS build numarasi 350 olarak v2.15.0 ile hizalandi.

## v2.14.0

- Kisi workspace minimum genislikleri minimum pencere boyutuyla uyumlu hale getirildi.
- Sol kisi tarayici 260-440px araliginda sinirlanan responsive divider yonetimine alindi.
- Sag profil/editor alaninin minimum 360px korunmasi saglandi.
- Profil avatar ve baslik tipografisi dar workspace icin kompakt breakpoint davranisi kazandi.
- Profil hizli aksiyonlari genis alanda 4 kolon, dar alanda 2 kolon grid olarak duzenlenir.
- Editor alt aksiyonlari genis alanda 3 kolon, dar alanda 2 kolon grid olarak duzenlenir.
- JTabbedPane dar alanda yatay kaydirilabilir sekme duzenine gecirildi.
- Kisi listesi ve profil bilgi alanlarindaki gereksiz yatay scrollbarlar kaldirildi.
- Profil bilgi kartlari viewport genisligini tam takip eden VerticalScrollablePanel ile duzenlendi.
- ResponsiveActionPanel ortak View bileseni eklendi.
- Android versionCode ve iOS build numarasi 340 olarak v2.14.0 ile hizalandi.

## v2.13.1

- Ortak `ModernButtons` View bileseni eklendi.
- Ana ekran ve iletisim satiri butonlarinin primary/secondary/danger/text/hizli aksiyon stilleri tek merkezde toplandi.
- `ContactMethodsPanel` icindeki dogrudan `new JButton` kullanimi kaldirildi.
- VCF/CSV export akisi `ContactFileFormat` enum modeliyle tip guvenli hale getirildi.
- Controller icindeki `"vcf"` ve `"csv"` magic stringleri kaldirildi.
- Export format dogrulamasi Service katmanina tasindi.
- Android versionCode ve iOS build numarasi 331 olarak hizalandi.

## v2.13.0

- Secili kisi icin varsayilan gorunum salt-okunur modern profil bilgi kartlarina donusturuldu.
- Duzenleme formu yalnizca `Duzenle` aksiyonu veya `Yeni Kisi` ile acilir.
- Telefonlar, e-postalar, is/profil, adres, notlar ve grup/etiketler ayri bilgi bloklarinda gosterilir.
- `Iptal`, mevcut kisi duzenlenirken kaydedilmemis degisiklikleri atip profil gorunumune doner.
- Hizli Ara / WhatsApp / E-posta / Kopyala aksiyonlari yalniz profil goruntuleme modunda kalir.
- Android versionCode ve iOS build numarasi 330 olarak hizalandi.

## v2.12.4

- Sol kisi listesi WhatsApp esintili duz liste satirlarina donusturuldu.
- Secili kisi yumusak accent zeminiyle vurgulanir; normal satirlarda kart cercevesi yerine ince ayirici kullanilir.
- Avatar, ad, firma/telefon onizlemesi ve sag meta alani yeniden hizalandi.
- Favori filtresi klasik checkbox yerine kompakt toggle-chip gorunumune alindi.
- Kisi listesi satir yuksekligi ve avatar boyutu daha kompakt hale getirildi.
- Android versionCode ve iOS build numarasi 324 olarak guncellendi.

## v2.12.3

- Secili kisi profilinde `Ara`, `WhatsApp`, `E-posta` ve `Kopyala` hizli aksiyonlari tek tip butonlarla eklendi.
- Hizli aksiyonlar yalnizca mevcut kisi secildiginde gorunur.
- `ContactQuickActionService` ve `DesktopActionTool` katmanlari eklendi.
- `docs/QUICK_ACTIONS.md` ile aksiyon davranislari belgelendi.
- Android versionCode ve iOS build numarasi 323 olarak guncellendi.

## v2.12.2

- Ana Sayfa Hizli Baslangic kartindaki dort buton iki aksiyona indirildi.
- `Yeni Kisi` tek primary dashboard aksiyonu olarak birakildi.
- `Kisileri Ac` outline buton yerine cercevesiz text-action haline getirildi.
- `Simdi Yedek Al` ve `Mobil Senkron` dashboard tekrarlarindan kaldirildi.
- Rehber Calisma Alani kartindaki tekrar eden `Kisileri Ac` aksiyonu kaldirildi.
- Button hiyerarsisi dokumani dashboard kurallariyla genisletildi.
- Android versionCode ve iOS build numarasi 322 olarak v2.12.2 ile hizalandi.

## v2.12.1

- Kisiler ekrani 3 kolon yerine 2 kolonlu responsive yapida yeniden duzenlendi.
- Sol kisi tarayici ile sag profil+editor alanini ayiran JSplitPane eklendi.
- Sag profil ve editor tek calisma kartinda birlestirildi.
- Pencere kuculurken kolon oranlari korunur ve editor alaninin ezilmesi azaltildi.
- Minimum pencere ve sidebar olculeri dar ekran kullanimina gore guncellendi.
- Android versionCode ve iOS build numarasi 321 olarak v2.12.1 ile hizalandi.

## v2.11.0

- Telefon girisinde ulkeye gore otomatik bosluklandirma eklendi.
- Secilen ulkeye gore ornek ulusal numara gosterimi eklendi.
- Canli kisa/uygun/uzun numara kontrolu eklendi.
- Turkiye `0555...` girisi otomatik trunk 0 temizligi ile `+90` E.164 saklamaya devam eder.
- Beklenen uzunlukla uyusmayan numaralarda kaydetmeden once uyari/onay akisi eklendi.
- PhoneNumberRule ve PhoneValidationResult modelleri eklendi.
- PhoneNumberRuleCatalog ile yaygin ulkelerin pratik uzunluk/gruplama kurallari merkezilestirildi.
- Android versionCode ve iOS build numarasi 300 olarak v2.11.0 ile hizalandi.

## v2.9.0

- Telefon editorune ulke kodu secim kutusu eklendi.
- Turkiye +90 varsayilan ulke kodu yapildi.
- Turkiye numaralarinda yerel baslangic 0 degeri uluslararasi saklama sirasinda otomatik kaldirilir.
- Mevcut uluslararasi numaralarda ulke kodu otomatik algilanir ve kalan kisim ayri alanda duzenlenir.
- Diger / Ozel Kod secenegi ile katalog disi ulke kodlari girilebilir.
- `default_phone_country_iso` runtime ayari config/app.yml dosyasina eklendi.
- PhoneCountryCode, PhoneCountryCodeCatalog ve PhoneNumberTool katmanlari eklendi.
- Android versionCode ve iOS build numarasi 290 olarak v2.9.0 ile hizalandi.

## v2.8.0

- Kisi basina sinirsiz telefon ve e-posta icin normalize `contact_methods` veri modeli eklendi.
- Eski phone/email kolonlari ilk acilista yeni modele otomatik migrate edilir.
- Profil fotografi PNG/JPEG secme, normalize etme, SQLite BLOB saklama ve avatar olarak gosterme eklendi.
- Telefon/e-posta satirlarina etiket, birincil ve sira bilgisi eklendi.
- Profil kartina grup ve etiket chipleri eklendi.
- CSV ve vCard import/export sinirsiz iletisim satirlariyla guncellendi.
- vCard 4.0 profil fotografi tasima destegi eklendi.
- History snapshot codec telefon/e-posta listeleri ve fotografi koruyacak sekilde genisletildi.
- Mobil API phones, emails ve photo_base64 alanlarini destekler.
- Android Contacts ve iOS Contacts repository kaynaklari coklu iletisim satirlari ve fotograf icin guncellendi.
- Dinamik iletisim editorundeki gorunen metinler Language katmanina tasindi.
- Android versionCode ve iOS build numarasi 280 olarak v2.8.0 ile hizalandi.

## v2.7.0

- Kisiler ekrani tablo tabanli gorunumden uc kolonlu modern workspace yapisina gecirildi.
- Sol kolona avatarli kisi listesi, arama, favori filtresi ve kisi sayaci eklendi.
- Kisi satirlari ad, firma/telefon, kategori ve favori bilgisini kart benzeri renderer ile gosterir.
- Orta kolona buyuk profil karti ve hizli iletisim/konum ozeti eklendi.
- Sag kolonda sekmeli kisi editoru ayri bir calisma paneli olarak konumlandirildi.
- Secim arama ve yenileme sonrasinda kisi kimligine gore korunur.
- Kompakt gorunum yeni kisi listesine uyarlandi.
- ContactTableModel ve JTable tabanli dead code kaldirildi.
- Avatar renkleri merkezi ModernThemePalette uzerinden deterministik hale getirildi.
- Android versionCode ve iOS build numarasi 270 olarak v2.7.0 ile hizalandi.


## v2.6.0

- Acik tema artik varsayilan ve birincil gorunumdur.
- Koyu tema ikincil secenek olarak Ayarlar > Gorunum bolumune tasindi.
- Kisiler ekranindaki hizli tema degistirme butonu kaldirildi.
- Aurora gradient tasarimi kaldirildi; sade yuzeyler, ince kenarliklar ve daha dusuk golgeler kullanildi.
- Sidebar acik temada acik yuzey, koyu temada sade koyu yuzey kullanir.
- Primary butonlar gradient yerine tek renk Turkuaz vurguyla calisir.
- Kart radiuslari, tablo yukseklikleri ve tipografi daha dengeli hale getirildi.

## v2.5.0

- ModernThemePalette ile tema yuzeyleri merkezilestirildi.
- Gradient uygulama zemini, sidebar ve durum cubugu eklendi.
- Golgeli modern kartlar ve yumusak border sistemi eklendi.
- Sidebar navigasyonuna pill/hover/active state tasarimi eklendi.
- Primary butonlar gradient modern stile gecirildi.
- Tipografi, form padding, tab, scrollbar ve tablo olcekleri guncellendi.
- Dashboard ve arama toolbar'i daha modern gorsel hiyerarsi ile yenilendi.

## v2.4.0

- Gruplar ve kisi-grup baglantilari eklendi.
- Renkli etiketler ve kisi-etiket baglantilari eklendi.
- Kural tabanli Akilli Listeler eklendi.
- Kisi Gecmisi snapshot ve geri yukleme eklendi.
- Silme soft-delete olarak degistirildi; Cop Kutusu eklendi.
- Kalici silme ayri onayli islem oldu.
- Duplicate tarama ve merge eklendi.
- Merge grup/etiket uyeliklerini ana kisiye tasiyor.
- UTF-8 CSV import/export eklendi.
- vCard 3/4 import ve vCard 4 export eklendi.
- Dogum tarihi `--AA-GG` bicimini destekliyor.
- Telefon yerine e-posta ile de kisi kaydi olusturulabiliyor.
- Sidebar yeni moduller icin scroll destekli hale getirildi.
- Android/iOS release metadata v2.4.0 ile hizalandi.

## v2.3.1

- Ekran goruntusu geri bildirimine gore dashboard ve sidebar yeniden tasarlandi.
- Sidebar navigasyonuna cizim tabanli ikonlar, aktif sayfa vurgusu ve bolum basliklari eklendi.
- FlatLaf global tipografi, corner radius, scrollbar ve tablo olcekleri buyutuldu.
- Dashboard bos beyaz alan yerine Hizli Baslangic, Sistem Durumu ve Rehber Calisma Alani kartlarina ayrildi.
- Kisi tablosu ve detay editoru kart tabanli gorunume alindi.
- Alt durum cubugu marka koyu rengine tasindi.
- Temiz kurulumlarda varsayilan tema koyu yapildi; mevcut kullanici tercihi korunur.

## v2.3.0

- Turkuaz branding ikon seti eklendi.
- Sidebar tabanli ana uygulama kabugu eklendi.
- Dashboard ve rehber ozet kartlari eklendi.
- Kisi detay karti ve otomatik harf avatar eklendi.
- Ayarlar modulu eklendi.
- Tema, acilis sayfasi, pencere, kompakt gorunum ve silme onayi kalici hale getirildi.
- Mobil senkron aktif/pasif ve port ayarlari GUI'den yonetilebilir hale geldi.
- GitHub update aktif/pasif ayari GUI'den yonetilebilir hale geldi.
- Manuel ve gunluk otomatik SQLite yedekleme eklendi.
- Yedek retention temizligi eklendi.
- Installer Windows kisayollarinda Turkuaz ikonunu kullanir.
- Branding PNG kaynaklari uygulama JAR icine gomulur.
- Android/iOS release metadata 2.3.0 ile hizalandi.

## v2.2.0

- Kisi modeli ikinci telefon, is telefonu, ikinci e-posta, firma, unvan, dogum tarihi, web sitesi, kategori, adres, ilce, il, posta kodu, ulke, not ve favori alanlariyla genisletildi.
- SQLite schema migration ve genisletilmis mobil API eklendi.

## v2.1.0

- FlatLaf eklendi.
- Acik/koyu tema ve tema tercihi eklendi.
- GitHub Releases updater ve portable dependency akisi iyilestirildi.

## v2.0.0

- TSV storage yerine SQLite mimarisine gecildi.
- Android/iOS iki yonlu LAN senkron mimarisi eklendi.
