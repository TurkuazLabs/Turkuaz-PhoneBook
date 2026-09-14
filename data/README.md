# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/data/README.md
# 📌 Amac: v2.32.0 ve daha eski portable veri yerlesimi icin migration kaynak klasorunu belgeler.
# 📌 Tool - Markdown
# Version: 2.0.0
# Aciklama: Legacy portable DB ve teknik veri migration kaynagidir; guncel Windows DB Contacts/Veri, Linux DB XDG data alanindadir.
# Bagimli Oldugu Katman: Service | Repository | Config

Bu klasor yeni portable dagitimda calisan kullanici veri alani degildir.

Guncel ana DB konumu:

- Windows: `%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri\telefon-rehberi.db`
- Linux: `$XDG_DATA_HOME/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db` veya `~/.local/share/turkuazlabs/telefon-rehberi/data/telefon-rehberi.db`
- macOS: `~/Library/Application Support/TurkuazLabs/TelefonRehberi/data/telefon-rehberi.db`

Eski `data/telefon-rehberi.db`, SQLite WAL/SHM, `contacts.tsv`, `sync-token.txt` ve `saved-views` bulunursa v2.32.1 ilk acilis migration servisi bunlari hedefteki mevcut veriyi ezmeden yeni konuma kopyalar.
