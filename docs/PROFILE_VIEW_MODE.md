# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/PROFILE_VIEW_MODE.md
# 📌 Amac: Kisi profilinin goruntule ve duzenle modlari arasindaki UI davranisini tanimlar.
# 📌 View - Markdown
# Version: 2.13.0
# Aciklama: Secili kiside bilgi kartlarini varsayilan yapar; editoru yalniz Duzenle veya Yeni Kisi aksiyonuyla acar.
# Bagimli Oldugu Katman: View | Controller

# Profil Goruntule / Duzenle Modu

- Kisi secilince varsayilan mod `contact_view` olur.
- Telefon, e-posta, is/profil, adres, not ve grup/etiket bilgileri salt-okunur kartlarda gosterilir.
- `Duzenle` butonu `contact_edit` modunu acar.
- `Iptal`, mevcut kiside form degisikliklerini atar ve tekrar profil gorunumune doner.
- `Yeni Kisi`, editoru dogrudan bos form ile acar.
- Hizli aksiyonlar sadece goruntule modunda gosterilir.
