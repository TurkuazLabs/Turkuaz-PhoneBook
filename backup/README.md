# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/backup/README.md
# 📌 Amac: v2.32.0 ve daha eski portable rehber DB yedekleri icin migration kaynak klasorunu belgeler.
# 📌 Tool - Markdown
# Version: 2.0.0
# Aciklama: Legacy portable backup migration kaynagidir; guncel rehber yedekleri kullaniciya gorunen Contacts/Documents alaninda tutulur.
# Bagimli Oldugu Katman: Service | Repository | Tool

Guncel rehber DB yedek konumu Windows'ta `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Yedekler`, Linux'ta `~/Contacts/Turkuaz Telefon Rehberi/Yedekler` veya XDG Documents fallback alanidir.

Eski `telefon-rehberi-*.db` dosyalari ilk acilista yeni yedek konumuna, hedefte ayni ad yoksa kopyalanir. Launcher tarafindaki uygulama JAR yedekleri bu migrationa dahil edilmez.
