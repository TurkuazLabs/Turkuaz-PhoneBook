# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/PHONE_INPUT.md
# 📌 Amac: Ulke kodlu telefon girisi, otomatik gorunur bicimlendirme ve validasyon davranisini belgeler.
# 📌 Modul - Markdown
# Version: 2.11.0
# Aciklama: Telefonun ekranda okunabilir, veri katmaninda ise temiz E.164 benzeri uluslararasi bicimde tutulmasini aciklar.
# Bagimli Oldugu Katman: Config | Model | Tool | View | Language

# Telefon Girisi v2.11.0

Telefon eklerken kullanici once ulkeyi secer, sonra yalnizca ulusal numarayi yazar.

Turkiye ornegi:

- Ulke: `Turkiye (+90)`
- Giris: `5551234567`
- Ekran: `555 123 45 67`
- Saklanan: `+905551234567`

Turkiye icin `05551234567` girilirse baslangictaki trunk `0` temizlenir.

## Canli kontrol

Telefon editoru secilen ulkeye gore:

- ornek ulusal numara gosterir,
- yazarken rakamlari okunabilir gruplara ayirir,
- ulusal rakam sayisinin beklenen aralikta olup olmadigini gosterir,
- E.164 toplam 15 rakam sinirini kontrol eder.

Bu kontrol bir operator numara tahsis sorgusu degildir. Beklenen uzunlukla uyusmayan numara tamamen reddedilmez; kaydetmeden once kullaniciya uyari ve devam secenegi sunulur.

## Katmanlar

- `PhoneCountryCodeCatalog`: Secilebilir ulke kodlari.
- `PhoneNumberRuleCatalog`: Ornek, uzunluk ve gorunur gruplama kurallari.
- `PhoneNumberTool`: Normalize, compose, format ve validasyon.
- `ContactMethodsPanel`: Kullanici girisi ve canli durum gosterimi.
