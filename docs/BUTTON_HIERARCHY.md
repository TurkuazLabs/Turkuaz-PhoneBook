# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/BUTTON_HIERARCHY.md
# 📌 Amac: Uygulama genelinde buton sayisini ve gorsel hiyerarsiyi standartlastirir.
# 📌 View - Markdown
# Version: 2.13.1
# Aciklama: Primary, secondary, danger, text, hizli aksiyon ve overflow kurallarini; ortak ModernButtons bileseni kullanimini tanimlar.
# Bagimli Oldugu Katman: View | Language | Config

# Button Hiyerarsisi v2.13.1

## Ortak bilesen
- Swing aksiyon butonlari dogrudan `new JButton` ile uretilmez.
- Primary, secondary, danger, text ve hizli aksiyon butonlari `views/ModernButtons.java` uzerinden uretilir.
- Satir bazli `+ Ekle` ve `...` aksiyonlari secondary stili kullanir.

## Kurallar
- Bir ekranda ayni is icin tekrar eden buton bulunmaz.
- Primary aksiyon ayni anda en fazla bir tane gorunur.
- Yeni kayitta primary metin `Kaydet`, secili kayitta `Guncelle` olur.
- `Sil` yalnizca mevcut kayit seciliyken gorunur ve danger stili kullanir.
- Ikincil islemler secondary stilde tutulur.
- Satir bazli detay islemler `...` overflow menusune tasinir.

## Kisi editoru
- Yeni Kisi / Yenile: sol kisi tarayici altinda.
- Iptal / Sil / Kaydet-Guncelle: editor altinda tek aksiyon sirasi.
- Profil ozeti tekrar CRUD butonu tasimaz.

## Telefon ve e-posta
- `+ Ekle`: yeni iletisim alani ekler.
- `...`: Duzenle, Birincil Yap ve Sil islemlerini acar.

## Profil fotografi
- `Fotograf...`: Fotograf Sec ve Fotografi Kaldir islemlerini tek menude toplar.

## Dashboard
- Dashboard ayni islemi sol navigasyonla tekrar eden buyuk butonlar tasimaz.
- Hizli Baslangic alaninda yalnizca `Yeni Kisi` primary aksiyondur.
- `Kisileri Ac` buton cercevesi olmayan text-action olarak gosterilir.
- `Yedekleme` ve `Mobil Senkron` dashboard butonu degildir; sol navigasyondaki kendi sayfalarindan yonetilir.
- Rehber Calisma Alani bilgi karti aksiyon butonu tasimaz.

## Profil hizli aksiyonlari
- Profil CRUD butonu tasimaz.
- Secili kisi varsa tek satirda `Ara`, `WhatsApp`, `E-posta`, `Kopyala` gorunur.
- Dort aksiyon ayni boyut, ayni border ve ayni ikon diliyle gosterilir.
- Yeni kisi modunda hizli aksiyon satiri gizlenir.
