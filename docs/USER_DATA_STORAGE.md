# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/USER_DATA_STORAGE.md
# 📌 Amac: v2.37.0 platform veri, config, cache, export ve backup dizinlerini belgeler.
# 📌 Modul - Markdown
# Version: 2.0.0
# Aciklama: Program dosyalari ile kullanici verisini ayirir; legacy migration ve uninstall guvencelerini tanimlar.
# Bagimli Oldugu Katman: Config | Service | Repository | Tool

## Windows

Program Files:

`C:\Program Files\TurkuazLabs\TelefonRehberi`

Ana SQLite:

`%USERPROFILE%\Contacts\Turkuaz Telefon Rehberi\Veri\telefon-rehberi.db`

Kullanici dosyalari:

- `Contacts\Turkuaz Telefon Rehberi\Disari Aktarilanlar`
- `Contacts\Turkuaz Telefon Rehberi\Yedekler`

Preferences ve teknik metadata:

`%APPDATA%\TurkuazLabs\TelefonRehberi`

Launcher cache/log/state:

`%LOCALAPPDATA%\TurkuazLabs\TelefonRehberi\Launcher`

## Linux

- DB/teknik data: `$XDG_DATA_HOME/turkuazlabs/telefon-rehberi` veya `~/.local/share/turkuazlabs/telefon-rehberi`
- Preferences: `$XDG_CONFIG_HOME/turkuazlabs/telefon-rehberi` veya `~/.config/turkuazlabs/telefon-rehberi`
- Cache: `$XDG_CACHE_HOME/turkuazlabs/telefon-rehberi` veya `~/.cache/turkuazlabs/telefon-rehberi`
- Export/backup: `~/Contacts/Turkuaz Telefon Rehberi` veya XDG Documents fallback

## Backup guvencesi

Yedekler ham DB dosya kopyasi degildir. Uygulama WAL acikken `VACUUM INTO` ile transaction-consistent snapshot uretir ve `PRAGMA integrity_check` sonucu `ok` olmadan yedegi basarili saymaz.

## Migration

Eski portable ve APPDATA DB bundle'lari hedef bos ise yeni konuma kopyalanir. Hedefte mevcut DB veya kullanici dosyasi varsa uzerine yazilmaz. Legacy kaynak dosyalar migration sonrasinda otomatik silinmez.

## Uninstall

Inno Setup uninstall yalniz Program Files altindaki uygulama dosyalarini kaldirir. `Contacts\Turkuaz Telefon Rehberi` altindaki DB, export ve yedekler korunur.
