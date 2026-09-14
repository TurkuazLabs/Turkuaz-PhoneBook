# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/CONTACT_BROWSER_COMPACT.md
# 📌 Amac: v2.33.2 sade Kisiler tarayicisi hiyerarsisini ve gorunum kararlarini dokumante eder.
# 📌 Modul - Markdown
# Version: 1.0.0
# Aciklama: Kisi listesi basligi, arama, kompakt aksiyon satiri, filtre drawer ve liste satiri davranisini tanimlar.
# Bagimli Oldugu Katman: View | Config | Language

# Kisi Tarayicisi v2.33.2

Kisiler paneli surekli gorunen kontrol sayisini azaltacak sekilde uc seviyeye ayrilir:

1. Baslik: `Kisiler`, guncel kisi sayisi ve ana `Yeni Kisi` aksiyonu.
2. Bulma: Tek arama alani.
3. Kompakt arac satiri: `Favoriler`, `Gorunum`, `Filtreler`, `...`.

`Gorunum` acilir menusu kaydedilmis gorunumleri secme, mevcut durumu kaydetme ve secili gorunumu yonetme islemlerini toplar.

`...` acilir menusu seyrek kullanilan `Tum Filtreleri Temizle` ve `Listeyi Yenile` islemlerini toplar.

Aktif filtreler drawer kapaliyken chip olarak gorunur. Filtre butonu aktif yapisal filtre sayisini `Filtreler (N)` biciminde bildirir.

Kisi liste satirlari normal modda 80 px, kompakt modda 64 px yuksekliktedir. Normal satirlarda kalici kart cercevesi cizilmez; yalniz secili ve hover durumlari yuzey vurgusu kullanir.
