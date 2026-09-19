# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/assets/branding/README.md
# 📌 Amac: Turkuaz marka ikon ve logo kurallarini uluslararasi ana dokuman olarak tanimlar.
# 📌 View - Markdown
# Version: 1.3.0
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

PNG and ICO application icons are shared by the Swing window, portable packages, Windows shortcuts, Linux desktop integration, and installer assets. During Windows Setup packaging, `tools/build-installer.ps1` rebuilds the staged `app-icon.ico` from `app-icon-512.png`: transparent outer padding is cropped and the artwork is fitted with about a 4% safety margin into 16/24/32/48/64/128/256 px frames. This keeps the installed desktop and Start-menu shortcut visually full-sized without changing the canonical artwork.

Turkce surum: [README.tr.md](./README.tr.md)
