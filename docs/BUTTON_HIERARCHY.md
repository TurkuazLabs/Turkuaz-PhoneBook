# Dosya Yolu: C:/Projects/TelefonRehberi/docs/BUTTON_HIERARCHY.md
# Amac: Uygulama genelindeki aksiyon hiyerarsisini ve Tema v3 buton davranislarini standartlastirir.
# View - Markdown
# Version: 3.0.0
# Aciklama: Primary, secondary, danger, text, quick ve chip varyantlarinin kullanim, durum ve erisilebilirlik kurallarini tanimlar.
# Bagimli Oldugu Katman: View | Language | Config

# Button Hiyerarsisi v3

## Ortak bilesen

- Swing aksiyon butonlari dogrudan `new JButton` ile uretilmez.
- Uygulama aksiyonlari `views/ModernButtons.java` uzerinden uretilir.
- Buton renkleri `ModernThemePalette`, geometri ve olculer `UiConfig` uzerinden gelir.
- Metin degisince preferred width yeniden hesaplanir; runtime dil degisimi sabit eski genislikte kalmaz.
- Klavye focus halkasi kapatilmaz; custom 2px focus ring kullanilir.

## Varyantlar

- `primary`: ekrandaki ana ilerletici/kaydedici aksiyon. Ayni aksiyon grubunda en fazla bir tane.
- `secondary`: iptal, yenile, ac, sec gibi geri donulebilir ikincil aksiyon.
- `danger`: silme veya geri donulemez islem.
- `text`: dusuk agirlikli navigation/yardim aksiyonu; normal durumda cercevesiz.
- `quick`: profil hizli aksiyonlari; secondary gorsel dili + ikon.
- `chip`: filtre ve kompakt secim/yonetim aksiyonlari; pill geometri.

## Durumlar

Her buton su durumlari ayri gostermelidir:

- Default
- Hover
- Pressed
- Disabled
- Keyboard focus

Primary hover/pressed koyulasir. Secondary hover neutral kalir. Danger hover kirmizi soft yuzeye gecer. Text hover soft Turkuaz yuzey alir. Disabled butonlar accent rengi kullanmaz.

## Boyutlar

- Primary / Secondary / Danger: 40px yukseklik.
- Quick: 40px yukseklik.
- Text: 36px yukseklik.
- Chip: 30px yukseklik.
- Standart buton radius: 10px.
- Chip radius: pill.
- Standart minimum genislik: 88px; chip ve text içerige gore genisler.

## Kisi editoru

- Yeni Kisi / Yenile: sol kisi tarayici altinda.
- Iptal / Sil / Kaydet-Guncelle: editor altinda tek aksiyon sirasi.
- Profil ozeti tekrar CRUD butonu tasimaz.
- Yeni kayitta primary metin `Kaydet`, secili kayitta `Guncelle` olur.
- `Sil` yalnizca mevcut kayit seciliyken gorunur ve danger stili kullanir.

## Telefon ve e-posta

- `+ Ekle`: yeni iletisim alani ekler.
- `...`: Duzenle, Birincil Yap ve Sil islemlerini acar.
- Satir bazli islemler primary stil kullanmaz.

## Profil fotografi

- `Fotograf...`: Fotograf Sec ve Fotografi Kaldir islemlerini tek menude toplar.

## Dashboard

- Dashboard ayni islemi sol navigasyonla tekrar eden buyuk butonlar tasimaz.
- Hizli Baslangic alaninda yalnizca `Yeni Kisi` primary aksiyondur.
- `Kisileri Ac` text-action olarak gosterilir.
- `Yedekleme` ve `Mobil Senkron` kendi sayfalarindan yonetilir.
- Bilgi kartlari gereksiz aksiyon butonu tasimaz.

## Profil hizli aksiyonlari

- Profil CRUD butonu tasimaz.
- Secili kisi varsa `Ara`, `WhatsApp`, `E-posta`, `Kopyala` quick varyantinda gorunur.
- Dort aksiyon ayni yukseklik, border ve ikon diliyle gosterilir.
- Yeni kisi modunda hizli aksiyon satiri gizlenir.
