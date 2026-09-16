# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/assets/branding/README.md
# 📌 Amac: Turkuaz marka ikon ve logo kurallarini uluslararasi ana dokuman olarak tanimlar.
# 📌 View - Markdown
# Version: 1.2.0
# Aciklama: Dil bagimsiz Turkuaz marka kimligi, logo kullanimi ve yerellestirilmis urun adi kurallarini Ingilizce olarak belgeler.
# Bagimli Oldugu Katman: View | Tool | Language

# Turkuaz Branding Rules

The primary brand name is **Turkuaz**.

`app-logo.png` must not contain a language-specific product descriptor such as `Telefon Rehberi`, `PhoneBook`, `Contacts`, or an equivalent phrase in another language. Product naming belongs to the application and installer localization layers, not to the bitmap brand asset.

Visible product names are localized as follows:

- Turkish: `Turkuaz Telefon Rehberi`
- English and fallback: `Turkuaz PhoneBook`

The current `app-logo.png` intentionally uses the same language-neutral artwork as the canonical `app-icon.png`. A dedicated Turkuaz wordmark may replace it later, but it must remain language-neutral and must not embed the localized product descriptor.

Compatibility-sensitive technical identifiers are intentionally preserved. `TelefonRehberi.exe`, Java package names, the Inno Setup `AppId`, existing user-data directories, and the SQLite database path are not renamed as part of branding changes.

PNG and ICO application icons are shared by the Swing window, portable packages, Windows shortcuts, Linux desktop integration, and installer assets.

Turkce surum: [README.tr.md](./README.tr.md)
