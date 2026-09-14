# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/IMPORTANT_DATES_AND_KEEP_IN_TOUCH.md
# 📌 Amac: v2.33.0 onemli tarihler ve Iletisimde Kal veri akisini dokumante eder.
# 📌 Docs - Markdown
# Version: 1.0.0
# Aciklama: SQLite saklama, Service dogrulamasi, profil ozeti, dashboard ve aktarim davranisini tanimlar.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

## Veri modeli

Dogum tarihi mevcut `contacts.birthday` alaninda kalir. Dogum gunu hatirlatma suresi `birthday_remind_days_before`, Iletisimde Kal dongusu `keep_in_touch_days`, son iletisim tarihi `last_contacted_date` kolonlarinda saklanir.

Ek yildonumu ve ozel tarihler `contact_important_dates` tablosunda kisi kimligi, tur, etiket, tarih, hatirlatma suresi ve sira bilgisiyle satir bazli saklanir.

## Is kurali

Tarih dogrulamasi ve bir sonraki iletisim tarihi hesaplamasi `ContactService` icindedir. View tarih aritmetigi yapmaz.

Iletisimde Kal aktifse son iletisim tarihi zorunludur. Bir sonraki tarih son iletisim tarihine secili dongu gun sayisi eklenerek hesaplanir.

## Profil ve dashboard

Profil `Hatirlatmalar` kartinda dogum gunu kurali, ek onemli tarihler, sonraki iletisim tarihi ve bugun aktif tarih hatirlatma sayisi gorunur.

Ana Sayfa `Takip Gereken` karti bugun aktif tarih hatirlatmasi veya gecikmis Iletisimde Kal plani olan kisi sayisini gosterir.

## Gecmis ve aktarim

History snapshot yeni alanlari ve ek tarih listesini saklar. CSV alanlari ve Turkuaz vCard X-properties roundtrip aktarimi destekler. Eski snapshot, CSV ve vCard dosyalari varsayilan kapali reminder degerleriyle okunmaya devam eder.
