# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/QUICK_ACTIONS.md
# 📌 Amac: Secili kisi profilindeki hizli aksiyonlarin davranisini ve katmanlarini tanimlar.
# 📌 View - Markdown
# Version: 2.12.3
# Aciklama: Ara, WhatsApp, E-posta ve Kopyala aksiyonlarinin UI ve masaustu entegrasyon kurallarini belgeler.
# Bagimli Oldugu Katman: Controller | Service | Tool | View | Language

# Profil Hizli Aksiyonlari v2.12.3

- Ara: birincil telefon icin `tel:` URI'sini varsayilan masaustu uygulamasina iletir.
- WhatsApp: birincil telefonun rakamlarini `https://wa.me/` adresiyle acar.
- E-posta: birincil e-posta icin `mailto:` URI'sini acar.
- Kopyala: once birincil telefonu, telefon yoksa birincil e-postayi panoya kopyalar.
- Secili kisi yoksa aksiyon satiri gorunmez.
- CRUD islemleri hizli aksiyon satirinda tekrar edilmez.
