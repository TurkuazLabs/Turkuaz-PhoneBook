# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/CONTACT_WORKSPACE_2COL.md
# 📌 Amac: Iki kolonlu responsive kisi calisma alaninin yerlesim ve resize kurallarini belgeler.
# 📌 View - Markdown
# Version: 2.14.0
# Aciklama: Sol kisi listesi, sag profil/editor workspace ve breakpoint tabanli dar pencere davranisini tanimlar.
# Bagimli Oldugu Katman: View | Config

# Iki Kolonlu Kisi Workspace v2.14.0

## Sol kolon
- Avatarli kisi listesi
- Arama
- Favori filtresi
- Temizle ve yenile aksiyonlari
- Minimum 260px, maksimum 440px genislik

## Sag kolon
- Profil fotografi ve kimlik ozeti
- Profil hizli aksiyonlari
- Telefon, e-posta, is, adres, not ve grup/etiket bilgi kartlari
- Temel, Iletisim, Is, Adres ve Diger editor sekmeleri
- Minimum 360px genislik

## Resize davranisi
- JSplitPane iki kolon arasindaki orani yonetir.
- Sol kolon varsayilan 360px civarinda baslar.
- Divider pencere resize sirasinda min/max kurallariyla otomatik sinirlanir.
- Minimum 1040px pencere genisliginde sol ve sag kolon minimumlari ayni anda korunur.
- Profil workspace 620px altinda avatar ve tipografi kompakt moda gecer.
- Hizli aksiyon alani 640px altinda 4 kolondan 2 kolona gecer.
- Editor aksiyon alani 560px altinda 3 kolondan 2 kolona gecer.
- Editor sekmeleri dar alanda yatay kaydirilabilir sekme duzeni kullanir.
