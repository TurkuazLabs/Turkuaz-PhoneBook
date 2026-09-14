# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/README.md
# 📌 Amac: Turkuaz Telefon Rehberi v2.37.0 kalite, veri guvenligi, sync kimligi ve dagitim mimarisini aciklar.
# 📌 Modul - Markdown
# Version: 2.37.0
# Aciklama: Gelistirme, portable kullanim, activity timeline, cok adimli islem gecmisi, kisi yonetimi ve release mimarisinin ana dokumanidir.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

# Turkuaz Telefon Rehberi v2.37.0

Java 17 + Swing + FlatLaf tabanli masaustu contact manager. SQLite ile kalici veri, Android/iOS iki yonlu LAN senkronu, native Windows/Linux launcher, portable Temurin JRE ve GitHub Releases update sistemi kullanir.

## v2.37.0 yenilikleri

- SQLite yedekleri WAL-guvenli `VACUUM INTO` snapshot ve `PRAGMA integrity_check` dogrulamasi kullanir.
- Her kisi kalici `sync_uuid` kimligi tasir; Android/iOS native contact ID ile UUID mappingini saklar.
- Mobil sync tokeni Android Keystore AES/GCM ve iOS Keychain ile korunur; LAN sync varsayilan kapali gelir.
- Mobil import request body limiti varsayilan 2 MiB'dir ve HTTP 413 ile reddedilir.
- History snapshot tablosu varsayilan 5000 kayit retention uygular.
- Kisi listeleme child verileri batch yukler; N+1 sorgu kaldirildi.
- Duplicate tarama telefon/e-posta/ad+firma indeksleriyle aday uretir; merge tum iletisim satirlarini ve sync kimliklerini korur.
- Backup/import/export/duplicate scan gibi agir masaustu islemleri Swing EDT disinda calisir.
- Kullanici ayarlari Program Files configine yazilmaz; yazilabilir OS kullanici config alaninda tutulur.
- Java release quality gate, Windows installer smoke testi ve release workflow kalite kapilari eklendi.
- Android versionCode ve iOS build numarasi 570; native launcher 2.4.0 olarak hizalandi.

## v2.36.0 yenilikleri

- Windows kurulumlu dagitim Inno Setup ile Program Files altina kurulur ve GitHub Releases uzerinden kendi kendini gunceller.
- Kurulumlu surum varsayilan olarak `C:\Program Files\TurkuazLabs\TelefonRehberi` altina kurulur.
- Inno Setup paketi native launcher, uygulama JAR'i, Temurin JRE 17, SQLite JDBC, FlatLaf ve marka assetlerini birlikte tasir; ilk acilista bagimlilik indirmesi gerekmez.
- Kurulumlu surum `config/installed.mode` ile portable surumden ayrilir.
- Program Files kurulumunda launcher cache, log ve state dosyalari `%LOCALAPPDATA%\TurkuazLabs\TelefonRehberi\Launcher` altinda tutulur.
- Kurulumlu launcher yeni surum bulunca Setup EXE dosyasini `%LOCALAPPDATA%` cache alanina indirir, SHA-256 dogrular, UAC ile calistirir ve Inno Setup uzerinden yerinde gunceller.
- Auto-update tamamlandiginda Inno Setup `/AUTOUPDATE` modu uygulamayi normal kullanici olarak yeniden baslatir.
- Portable surum mevcut native launcher bootstrap ve GitHub update davranisini korur.
- Windows SQLite DB `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri` altinda kalir; uninstall bu kullanici verisini silmez.
- GitHub Actions tag release'i Windows Portable ZIP'e ek olarak `TelefonRehberi-Setup-v2.36.0.exe` uretir.
- Android versionCode ve iOS build numarasi 560 olarak v2.36.0 ile hizalandi.

## v2.34.1 yenilikleri

- Windows SQLite ana veritabani `%APPDATA%` altindan `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri` klasorune tasindi.
- SQLite `-wal` ve `-shm` sidecar dosyalari ayni `Veri` klasorunde tutulur.
- Eski APPDATA DB bundle'i hedef bos ise ilk acilista yeni konuma kayipsiz kopyalanir; hedefte DB varsa uzerine yazilmaz.
- Linux SQLite yerlesimi XDG data alaninda kalmaya devam eder.

## v2.34.0 yenilikleri

- Windows kullaniciya gorunen dosya koku `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi` olarak ayrildi.
- Linux'ta varsa `~/Contacts/Turkuaz Telefon Rehberi`, yoksa `XDG_DOCUMENTS_DIR/Turkuaz Telefon Rehberi` kullanilir.
- VCF/CSV export `Disari Aktarilanlar`, SQLite yedekleri `Yedekler` altinda tutulur.
- Linux preferences `XDG_CONFIG_HOME`, cache `XDG_CACHE_HOME` altina ayrildi.
- Linux native launcher, `Kurulum.sh`, `Kaldir.sh` ve `Terminal=false` desktop entry eklendi.

## v2.33.3 yenilikleri

- Windows ana giris noktasi `TelefonRehberi.exe` native launcher oldu; kullanici uygulamayi PowerShell ile acmaz.
- Konsol penceresi olmadan calisan native Win32 splash eklendi.
- Splash Java hazirligi, bagimlilik kontrolu, GitHub update ve uygulama acilis asamalarini gosterir.
- Native launcher Go 1.23 ile katmanli olarak `Controller -> Service -> Repository/Tool -> View/Language` mimarisinde yazildi.
- GitHub updater native launcher EXE'yi staged helper ile sonraki acilista guvenli olarak gunceller.
- Kurulum kisayollari dogrudan `TelefonRehberi.exe` hedefler.
- GitHub Actions release akisi JAR ile birlikte `TelefonRehberi.exe` assetini uretir.
- Android versionCode ve iOS build numarasi 533 olarak v2.33.3 ile hizalandi.

## v2.33.2 yenilikleri

- Kisiler ekraninin sol tarayici alani sade ve tek hiyerarside yeniden duzenlendi.
- `Yeni Kisi` ana aksiyonu panel basligina tasindi; alt sabit Yeni Kisi/Yenile buton satiri kaldirildi.
- Arama altindaki kontroller tek satirda `Favoriler / Gorunum / Filtreler / ...` olarak toplandi.
- Kaydedilmis gorunum secimi, gorunum kaydetme ve yonetme islemleri acilir `Gorunum` menusune tasindi.
- Liste yenileme ve tum filtreleri temizleme ikincil `...` menusune tasindi.
- Aktif yapisal filtre sayisi `Filtreler (N)` olarak gorunur; filtre drawer acikken buton `Kapat` durumuna gecer.
- Kisi liste satirlari 80 px, avatarlar 42 px olacak sekilde sikilastirildi; secili/hover olmayan satirlardaki gereksiz kart cerceveleri kaldirildi.
- Android versionCode ve iOS build numarasi 532 olarak v2.33.2 ile hizalandi.

## v2.33.1 yenilikleri

- Sol navigasyon kalabaligi azaltildi; Ana Sayfa, Kisiler ve Favoriler ana erisim olarak surekli gorunur.
- Gruplar, Etiketler ve Akilli Listeler `Duzenle` acilir grubunda toplandi.
- Ice / Disa Aktar, Mobil Senkron, Yedekleme ve Duzelt ve Yonet `Veri ve Araclar` acilir grubunda toplandi.
- Gecmis ve Cop Kutusu `Arsiv` acilir grubunda toplandi.
- Gruplar accordion mantiginda calisir; ayni anda yalniz bir grup acik kalir ve aktif alt sayfa grubu otomatik acilir.
- Ikincil menu satirlari girintili ve daha kompakt hale getirildi; Ayarlar ve Hakkinda alt bolumde ayrildi.
- Android versionCode ve iOS build numarasi 531 olarak v2.33.1 ile hizalandi.

## v2.33.0 yenilikleri

- Kisi editorune dogum gunu hatirlatma secenegi eklendi: kapali, ayni gun, 2 gun, 7 gun veya 14 gun once.
- Kisi basina sinirsiz yildonumu ve ozel tarih kaydi eklendi; her tarih kendi etiketini ve hatirlatma suresini tasir.
- `contact_important_dates` tablosu ile onemli tarihler JSON yerine sorgulanabilir SQLite satirlari olarak saklanir.
- Iletisimde Kal plani 30, 90, 180 veya 365 gun dongusu ve son iletisim tarihiyle yonetilir.
- Bir sonraki iletisim tarihi ve gecikme durumu Service katmaninda hesaplanir; profil gorunumu yalniz sonucu gosterir.
- Ana Sayfa `Takip Gereken` karti bugun aktif tarih hatirlatmasi veya gecikmis iletisim plani olan kisi sayisini gosterir.
- History snapshot/restore yeni hatirlatma alanlarini, onemli tarih listesini ve iletisim plani bilgisini kayipsiz tasir.
- CSV ve vCard aktarimlarina Turkuaz onemli tarih/hatirlatma alanlari eklendi; eski dosyalarla geriye uyumluluk korunur.
- Mobil senkron mevcut masaustu hatirlatma alanlarini ezmez; mobilde yeni gelen kisiler varsayilan olarak hatirlatmasiz olusur.
- Android versionCode ve iOS build numarasi 530 olarak v2.33.0 ile hizalandi.

## v2.32.1 yenilikleri

- Rehber veritabani artik uygulama/portable klasoru icinde tutulmaz.
- Windows kullanici veri koku `%APPDATA%\TurkuazLabs\TelefonRehberi` olarak kullanilir.
- Linux'ta `$XDG_DATA_HOME/turkuazlabs/telefon-rehberi`; XDG_DATA_HOME yoksa `~/.local/share/turkuazlabs/telefon-rehberi` kullanilir.
- macOS icin `~/Library/Application Support/TurkuazLabs/TelefonRehberi` destegi bulunur.
- SQLite DB, DB yedekleri, mobil sync token, kaydedilmis gorunumler ve kullanici tercihleri kalici kullanici veri dizinine ayrildi.
- v2.32.0 ve daha eski portable `data`, `backup` ve `config/preferences.yml` verileri ilk acilista hedefteki mevcut veriyi ezmeden otomatik kopyalanir.
- SQLite WAL/SHM sidecar dosyalari DB ile birlikte migration edilir; eski kaynak dosyalar guvenlik icin otomatik silinmez.
- Uygulama update JAR yedekleri rehber backup klasorunden ayrilarak `updates/app-backups` altina tasindi.
- Ayarlar ekranindaki Veritabani ve Yedek Klasoru yollari yeni kalici konumu gosterir.
- Windows kaldirici normal kaldirmada kullanici verisini korur; yalniz `-RemoveData` ile acikca istenirse APPDATA verisi de silinir.
- Android versionCode ve iOS build numarasi 521 olarak v2.32.1 ile hizalandi.

## v2.32.0 yenilikleri

- Secili kisi profilinin icine filtrelenebilir `Aktivite` zaman cizelgesi eklendi.
- Timeline verisi mevcut `contact_history` tablosundan kisi kimligine gore Repository -> Service akisi ile okunur.
- `Tumu`, `Degisiklik`, `Aktarim` ve `Geri Yukleme` filtreleri gercek history aksiyonlarina gore Service katmaninda uygulanir.
- Her filtre chip'i kendi toplam kayit sayisini gosterir; profil icinde config ile belirlenen son 12 uygun hareket listelenir.
- Olusturma, guncelleme, silme, restore, mobil senkron, import, merge ve gecmisten donus icin aciklayici timeline metinleri eklendi.
- Profil hero alani daha kompakt hale getirildi; avatar ve baslik olculeri azaltildi, bilgi kartlarina daha fazla dikey alan ayrildi.
- Genel history ve profil activity limitleri `config/app.yml` icindeki `history_limit` ve `contact_activity_limit` ayarlarindan gelir.
- Eski v2.30/v2.31 `app.yml` dosyalarinda yeni limitler yoksa merkezi Config fallback degerleri kullanilir; JAR-only updater geriye uyumlu kalir.
- Android versionCode ve iOS build numarasi 520 olarak v2.32.0 ile hizalandi.

## v2.31.0 yenilikleri

- Toplu islemler icin varsayilan 20 kayitlik cok adimli Undo/Redo gecmisi eklendi.
- `Ctrl+Z` son toplu islemi geri alir; `Ctrl+Y` geri alinan islemi yeniden uygular.
- Undo bildiriminin 12 saniyelik gorunurluk suresi artik gecmis kaydini silmez.
- Yeni basarili toplu islem redo dalini temizler; degisiklik yapmayan islemler mevcut gecmisi bozmaz.
- Kisi snapshot, grup/etiket uyeligi ve Cop Kutusu islemleri ayni tip guvenli gecmis servisi uzerinden calisir.
- Gecmis kapasitesi `config/app.yml` icindeki `bulk_history_limit` ayarindan gelir.
- Android versionCode ve iOS build numarasi 510 olarak hizalandi.

## v2.30.0 yenilikleri

- Toplu firma, kategori, favori, grup, etiket ve Cop Kutusu islemlerinden sonra tek adimlik Geri Al bildirimi eklendi.
- Geri alma bildirimi alt durum cubugunda 12 saniye gorunur ve tek tikla son toplu islemi tersine cevirir.
- Kisi alan degisikliklerinde tam Contact snapshot saklanir; grup/etiket islemlerinde yalniz gercekten degisen uyelikler kaydedilir.
- Cop Kutusuna toplu tasima soft-delete kimliklerini restore ederek geri alinabilir.
- Yeni geri alinabilir toplu islem onceki undo kaydinin yerini alir.
- Android versionCode ve iOS build numarasi 500 olarak hizalandi.

## v2.23.0 yenilikleri

- Mobil Senkron sayfasi servis durumu, baglanti adresi ve token icin responsive durum kartlarina tasindi.
- Yedekleme sayfasi ozet, konum, yeni yedek aksiyonu ve modern yedek gecmisi olarak yeniden duzenlendi.
- Ayarlar sekmeleri ortak modern kart yapisina tasindi ve dar pencerede kaydirilabilir hale getirildi.
- Genel ayarlarda acilis/pencere davranisi ile veri konumlari ayri kartlarda toplandi.
- Senkron ayarlarina yeniden baslatma gereksinimini aciklayan bilgi karti eklendi.
- Yedek listesi icin BackupListCellRenderer eklendi.
- Android versionCode ve iOS build numarasi 430 olarak hizalandi.

## v2.19.0 yenilikleri

- Kisi listesi yuvarlatilmis modern kart satirlarina tasindi.
- Secili kisi turkuaz vurgu ve sol secim cizgisiyle daha belirgin hale getirildi.
- Mouse hover durumu ayri yuzey vurgusuyla eklendi.
- Ad, telefon/e-posta, firma ve kategori bilgileri daha okunabilir katmanlara ayrildi.
- Profil fotografi listede dairesel maske ile gosterilir.
- Uzun ad ve meta bilgiler kontrollu olarak kisaltilir; tam bilgi tooltip icinde korunur.
- Kompakt liste modunda ucuncu meta satiri gizlenerek yukseklik korunur.
- Android versionCode ve iOS build numarasi 390 olarak hizalandi.

## v2.18.0 yenilikleri

- Kisi profil gorunumu editor ile ayni responsive alan karti tasarimina tasindi.
- Profil alanlari genis alanda iki kolon, dar alanda tek kolon yerlesir.
- Temel, iletisim, is, adres ve ek bilgi kartlari ayri okunabilir bolumlere donusturuldu.
- Firma, unvan, web, adres, il, ilce, posta kodu ve ulke degerleri ayri profil alanlari olarak gosterilir.
- Notlar ile grup/etiket chipleri Ek Bilgiler kartinda birlestirildi.
- ResponsiveInfoPanel ortak View bileseni eklendi.
- Android versionCode ve iOS build numarasi 380 olarak hizalandi.

## v2.17.0 yenilikleri

- Kisi editoru alan etiketleri inputlarin ustune tasindi.
- Genis editor alaninda iki kolon, dar alanda tek kolon responsive form duzeni eklendi.
- Temel, Iletisim, Is, Adres ve Diger sekmeleri modern bolum kartlariyla yeniden duzenlendi.
- Adres ve not alanlari tam genislik kullanacak sekilde ayarlandi.
- ResponsiveFormPanel ortak View bileseni eklendi.
- Android versionCode ve iOS build numarasi 370 olarak hizalandi.

## v2.13.1 yenilikleri

- Primary, secondary, danger, text ve hizli aksiyon butonlari `ModernButtons` ortak View bileseninde merkezilestirildi.
- Telefon/e-posta `+ Ekle` ve `...` aksiyonlari da ayni modern secondary buton stiline alindi.
- VCF/CSV export secimi `ContactFileFormat` modeliyle tip guvenli hale getirildi; controller magic stringleri kaldirildi.
- Export uzanti dogrulamasi Service katmaninda formatla birlikte uygulanir.
- Android versionCode ve iOS build numarasi 331 olarak hizalandi.

## v2.13.0 yenilikleri

- Kisi secildiginde sekmeli form yerine once modern bilgi kartlari gorunur.
- `Duzenle` ile editor acilir; `Iptal` ile kaydedilmemis degisiklikler atilip profil gorunumune donulur.
- Telefonlar, e-postalar, is/profil, adres, notlar ve grup/etiketler ayri bloklarda okunur.
- Hizli Ara / WhatsApp / E-posta / Kopyala aksiyonlari profil goruntuleme modunda kullanilir.
- `Yeni Kisi` editoru dogrudan bos form ile acar.
- Iki kolonlu responsive yapi ve WhatsApp esintili kisi listesi korunur.
- Android versionCode ve iOS build numarasi 330 olarak hizalandi.

## v2.12.4 yenilikleri

- Sol kisi tarayici WhatsApp benzeri duz liste diline gecirildi.
- Kisi satirlarinda yuvarlak avatar, ad, firma/telefon onizlemesi ve sag meta alani daha belirgin.
- Secili satir yumusak yesil vurgu kullanir; diger satirlarda agir kart kutulari yoktur.
- Favoriler filtresi checkbox yerine kompakt filtre chip'i olarak gosterilir.
- Acik tema varsayilan; koyu tema Ayarlar > Gorunum altinda ikinci secenektir.
- Android versionCode ve iOS build numarasi 324 olarak v2.12.4 ile hizalandi.

## v2.12.3 yenilikleri

- Secili kisi profilinde `Ara`, `WhatsApp`, `E-posta` ve `Kopyala` hizli aksiyonlari eklendi.
- Hizli aksiyonlar yeni kisi modunda gizlenir.
- Telefon, WhatsApp ve e-posta URI acma islemleri servis/tool katmanlarina ayrildi.
- Android versionCode ve iOS build numarasi 323 olarak v2.12.3 ile hizalandi.

## v2.12.2 yenilikleri

- Ana Sayfa buton hiyerarsisi sadelestirildi.
- Hizli Baslangic kartinda yalnizca `Yeni Kisi` primary aksiyon olarak birakildi.
- `Kisileri Ac` cercevesiz metin aksiyonuna donusturuldu.
- Dashboard uzerindeki tekrar eden `Simdi Yedek Al` ve `Mobil Senkron` butonlari kaldirildi; bu islemler sol navigasyondaki kendi modullerinden yonetilir.
- Rehber Calisma Alani kartindaki tekrar eden `Kisileri Ac` butonu kaldirildi.
- Android versionCode ve iOS build numarasi 322 olarak v2.12.2 ile hizalandi.

## v2.12.1 yenilikleri

- Kisiler ekrani uc kolondan iki kolona indirildi.
- Sol kolon avatarli kisi listesi ve arama/filtreleme icin ayrildi.
- Sag kolon profil ozeti ile sekmeli editoru tek responsive calisma alaninda birlestirir.
- JSplitPane tabanli oranli yeniden boyutlandirma eklendi; pencere daralirken editor artik ezilmez.
- Minimum pencere genisligi 1040px seviyesine indirildi ve sidebar daha kompakt hale getirildi.
- Profil ozeti yatay hero + iletisim/kategori/konum chip alanina donusturuldu.
- Android versionCode ve iOS build numarasi 321 olarak v2.12.1 ile hizalandi.

## v2.11.0 yenilikleri

- Telefon numarasi yazarken secilen ulkeye gore otomatik bosluklandirma uygulanir.
- Turkiye icin `5551234567` girisi ekranda `555 123 45 67` olur; SQLite/VCF/mobil veri yine temiz `+905551234567` saklar.
- Telefon penceresinde secilen ulkeye gore ornek numara gosterilir.
- Canli kontrol satiri numaranin beklenen ulusal uzunlukla uyumunu gosterir.
- Yaygin ulkeler icin ulusal numara uzunluk araligi ve gorunur grup kurallari merkezi katalogdan gelir.
- Katalogda ozel kural bulunmayan ulkelerde genel E.164 uzunluk kontrolu kullanilir.
- Beklenen uzunlukla uyusmayan numara kaydedilirken kullaniciya uyari ve devam onayi sunulur; veri zorla reddedilmez.
- Telefon bicim mantigi `PhoneNumberRuleCatalog` ve `PhoneNumberTool` katmanlarinda tutulur; GUI icine magic string/kural gomulmez.
- Android versionCode ve iOS build numarasi 300 olarak v2.11.0 ile hizalandi.

## v2.9.0 yenilikleri

- Telefon ekleme/duzenleme penceresine ulke kodu secimi eklendi.
- Turkiye varsayilan ulkedir ve +90 otomatik secilir.
- Kullanici sadece kalan telefon numarasini yazar; Turkiye icin baslangictaki 0 otomatik temizlenir.
- Telefonlar +ulkeKoduUlusalNumara biciminde saklanir; VCF, CSV, SQLite ve mobil senkronla uyumlu kalir.
- Mevcut + ile baslayan telefonlar duzenlenirken ulke kodu otomatik algilanir.
- Yaygin Avrupa, Orta Dogu, Asya, Amerika ve Afrika ulkeleri secilebilir.
- Listede olmayan kodlar icin Diger / Ozel Kod secenegi bulunur.
- Varsayilan telefon ulkesi `config/app.yml` icindeki `default_phone_country_iso` ayarindan gelir.

## v2.8.0 yenilikleri

- Kisi basina sinirsiz telefon ve e-posta satiri eklendi.
- Her iletisim satiri kendi etiketini, birincil durumunu ve sirasini tasir.
- Gercek profil fotografi PNG/JPEG olarak secilebilir; normalize edilip SQLite icinde saklanir.
- Fotograf yoksa harf avatari otomatik kullanilir.
- Profil kartinda grup ve etiket uyelikleri chip olarak gosterilir.
- SQLite `contact_methods` tablosu eski sabit telefon/e-posta kolonlarindan otomatik migrate edilir.
- Legacy kolonlar geriye uyumluluk icin birincil ve ilk ek alanlarla senkron tutulur.
- VCF 4.0 import/export tum telefonlari, e-postalari ve profil fotografini tasir.
- CSV import/export sinirsiz iletisim alanlarini kayipsiz compact codec ile tasir.
- Kisi gecmisi snapshotlari sinirsiz iletisim alanlari ve fotografi korur.
- Mobil REST API `phones[]`, `emails[]` ve `photo_base64` alanlarini destekler.
- Android ve iOS kaynaklari sistem rehberindeki birden fazla telefon/e-posta ve fotografi masaustu ile esler.
- Acik tema varsayilan, koyu tema Ayarlar > Gorunum altinda ikinci secenek olarak korunur.

## v2.7.0 yenilikleri

- Kisiler ekrani klasik tablo gorunumunden modern uc kolonlu calisma alanina tasindi.
- Sol kolonda avatarli kisi listesi, anlik arama, favori filtresi ve kisi sayaci bulunur.
- Kisi listesi ad, firma/telefon, kategori ve favori durumunu tek satir kartinda gosterir.
- Orta kolonda buyuk profil avatari, firma/unvan, telefon, e-posta, kategori, konum ve dogum tarihi ozetlenir.
- Sag kolonda Temel / Is / Adres / Diger sekmeli editor korunur.
- Kompakt gorunum ayari yeni avatarli liste satir yuksekligine uygulanir.
- Secili kisi arama/yenileme sonrasinda mumkun oldugunca korunur.
- Eski JTable tabanli ContactTableModel kaldirildi; gereksiz dead code temizlendi.
- Acik tema ana tema, koyu tema Ayarlar > Gorunum altinda ikinci secenek olarak korunur.

## v2.6.0 yenilikleri

- Turkuaz Clean tema sistemi: daha sade, daha az renkli ve daha kurumsal yuzeyler.
- Acik tema artik varsayilan ve birincil gorunumdur.
- Koyu tema ikinci secenek olarak sadece `Ayarlar > Gorunum` altinda bulunur.
- Kisiler ekranindaki hizli tema degistirme butonu kaldirildi.
- Gradient arka plan/sidebar/status tasarimi kaldirildi.
- Kartlar daha ince border, daha dusuk golge ve 16px radius kullanir.
- Sidebar acik temada acik yuzey, koyu temada sade koyu yuzey kullanir.
- Primary butonlar gradient yerine tek renk Turkuaz vurgu kullanir.
- Tipografi ve bosluklar daha kompakt hale getirildi.
- Tema secimi mevcut kullanici tercihini korur; yeni temiz kurulum `light` ile baslar.

## v2.4.0 yenilikleri

- Gruplar
  - manuel grup olusturma
  - renk anahtari
  - secili kisiyi gruba ekleme/cikarma
- Renkli Etiketler
  - bir kisi birden fazla etikete baglanabilir
  - turkuaz, mavi, yesil, sari, turuncu, kirmizi, mor ve gri renk anahtarlari
- Akilli Listeler
  - ad, telefon, e-posta, firma, unvan, sehir, ulke, kategori, not, dogum tarihi ve favori alanlari
  - contains / equals / is_empty / not_empty / true operatorleri
  - kurala gore dinamik kisi listesi
- Duzelt ve Yonet
  - ayni telefon
  - ayni e-posta
  - ayni ad + firma
  - muhtemel mukerrer kayit tespiti
  - alan, grup ve etiket bilgilerini koruyarak birlestirme
- Cop Kutusu
  - normal silme artik soft-delete
  - silinen kisiyi geri yukleme
  - ayri onayla kalici silme
- Kisi Gecmisi
  - create/update/delete/restore/mobile sync/import/merge snapshotlari
  - secili snapshot surumune geri donme
- VCF/vCard
  - vCard 3.0/4.0 import
  - vCard 4.0 export
  - temel RFC 6350 alanlari
- UTF-8 CSV
  - quote/newline destekli import/export
  - yaygin kisi alanlari
- Dogum tarihi
  - `YYYY-AA-GG`
  - yil bilinmiyorsa `--AA-GG`
- Kisi kaydi icin ana telefon zorunlulugu kaldirildi; en az bir telefon veya e-posta yeterli.
- Sidebar yeni contact-manager modulleri icin kaydirilabilir hale getirildi.

## Ana veri

Ana veritabani uygulama klasorunden bagimsiz kullanici veri dizinindedir:

- Windows: `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri\telefon-rehberi.db`
- Linux (XDG): `$XDG_DATA_HOME/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`
- Linux (fallback): `~/.local/share/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`
- macOS: `~/Library/Application Support/TurkuazLabs/TelefonRehberi/data/telefon-rehberi.db`

Eski portable `data/telefon-rehberi.db` ve `data/contacts.tsv` dosyalari ilk uygun acilista yeni kullanici veri dizinine kayipsiz migrate edilir.

v2.11.0 schema migration eski SQLite kayitlarini silmez. `contact_methods`, `deleted_at`, history, group, tag ve smart-list tablolari eksikse otomatik olusturulur; eski sabit telefon/e-posta kolonlari yeni iletisim tablosuna aktarilir.

## Sidebar

Surekli gorunen ana rehber navigasyonu:

- Ana Sayfa
- Kisiler
- Favoriler

Accordion gruplari:

- Duzenle: Gruplar, Etiketler, Akilli Listeler
- Veri ve Araclar: Ice / Disa Aktar, Mobil Senkron, Yedekleme, Duzelt ve Yonet
- Arsiv: Gecmis, Cop Kutusu

Ayarlar ve Hakkinda alt navigasyon bolumunde ayri tutulur. Ayni anda yalniz bir accordion grubu acik kalir.

## Ayarlar

Kullanici tercihleri uygulama config klasorunden ayri tutulur:

- Windows: `%APPDATA%\TurkuazLabs\TelefonRehberi\config\preferences.yml`
- Linux: `$XDG_CONFIG_HOME/turkuazlabs/telefon-rehberi/preferences.yml`; fallback `~/.config/turkuazlabs/telefon-rehberi/preferences.yml`

`config/app.yml` mobil senkron ve SQLite icin salt-okunur teknik varsayilanlari tasir. Kullanici tarafindan degistirilen senkron acik/kapali, port ve update tercihleri yazilabilir `preferences.yml` icinde tutulur.

Launcher ve GitHub update teknik ayarlari:

`config/launcher.yml`

## Yedekleme

Rehber DB yedekleri kullaniciya gorunen rehber dosya alanindadir:

- Windows: `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Yedekler\telefon-rehberi-YYYYMMDD-HHMMSS.db`
- Linux: `~/Contacts/Turkuaz Telefon Rehberi/Yedekler/` veya XDG Documents fallback alani

Uygulama JAR update yedekleri ayri olarak program klasorundeki `updates/app-backups` altinda tutulur ve rehber verisi sayilmaz.

SQLite dosyasinin tamamini yedekledigi icin kisilerle birlikte grup, etiket, gecmis, akilli liste ve cop kutusu verileri de korunur.

## Native Windows launcher

Windows kullanicisi uygulamayi `TelefonRehberi.exe` ile acar. Konsol veya PowerShell penceresi acilmaz. Inno Setup kurulumlu surum varsayilan olarak `C:\Program Files\TurkuazLabs\TelefonRehberi` altina kurulur; launcher cache/log/state dosyalari `%LOCALAPPDATA%\TurkuazLabs\TelefonRehberi\Launcher` altinda tutulur.

## Native Linux launcher

Linux kullanicisi portable paketteki `TelefonRehberi` ELF launcher ile acar. `Kurulum.sh` kullanici hesabina kurar ve `~/.local/share/applications` altina `Terminal=false` desktop entry yazar. Temurin JRE Linux TAR.GZ paketi ilk calistirmada SHA-256 dogrulamasi ile hazirlanir.

## Portable runtime

Native launcher ilk calistirmada eksikse otomatik olarak:

- Eclipse Temurin JRE 17
- Xerial SQLite JDBC 3.53.4.0
- SLF4J API 1.7.36
- FlatLaf 3.7.2

indirir. Sistem `JAVA_HOME` veya `PATH` ayarini degistirmez.

## Mobil senkron

Android ve iOS istemcileri PC ile ayni guvenilir LAN/Wi-Fi aginda token korumali REST API uzerinden haberlesir.

GitHub'a kisi verisi gonderilmez. GitHub sadece kaynak kod ve release/update dosyalari icin kullanilir.

## GitHub

Repository:

`TurkuazLabs/turkuaz-telefon-rehberi`

Dagitim GitHub Releases uzerinden yapilir. `vX.Y.Z` tag push edilince ortak JAR, Windows native EXE + Portable ZIP, Linux amd64 native launcher + Portable TAR.GZ, Android test APK ve iOS simulator build kontrolleri calisir.

## Lisans ve guvenlik

Repository `LICENSE` dosyasindaki source-visible kosullarla yayinlanir. Guvenlik bildirimleri `SECURITY.md`, ucuncu taraf runtime/kutuphane bildirimleri `THIRD_PARTY_NOTICES.md` dosyasindadir. Mobil LAN senkronu varsayilan kapali gelir ve yalniz guvenilir yerel ag icin tasarlanmistir.

## Mimari

Controller -> Service -> Repository/Model -> Tool -> View -> Language

- Controller: GUI eventini alir ve Service cagirir.
- Service: tum is kurallari.
- Repository: SQLite ve kalici storage.
- Tool: VCF/CSV, asset, runtime ve dis dunya adaptorlugu.
- View: Swing/FlatLaf GUI.
- Language: kullanici metinleri.
- Config: teknik ve merkezi sabitler.
