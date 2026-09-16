# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/SECURITY.md
# 📌 Amac: Defines the Turkuaz PhoneBook security support and vulnerability-reporting policy.
# 📌 Modul - Markdown
# Version: 1.2.0
# Aciklama: English primary policy covering supported releases, sensitive data, LAN sync boundaries and responsible security reporting.
# Bagimli Oldugu Katman: Tool | Config

# Security Policy

This policy applies to **Turkuaz PhoneBook** (Turkish product name: **Turkuaz Telefon Rehberi**).

[Turkce guvenlik politikasi](./SECURITY.tr.md)

## Supported Version

Security fixes are applied to the latest actively supported GitHub Release. Users of older versions should update to the latest release before reporting a problem that may already have been fixed.

## Sensitive Data

Do not upload real contact databases, phone numbers, email addresses, synchronization tokens, backup databases, or personal screenshots to public issues or discussions. Reproduce problems with empty or test data whenever possible.

## Mobile LAN Synchronization

Mobile synchronization is disabled by default and is intended only for trusted local networks. Do not expose port `8787` to the Internet and do not share synchronization tokens with untrusted devices or people.

## Reporting a Vulnerability

If GitHub Security Advisories / private vulnerability reporting is enabled for this repository, use that private channel. Do not publish exploit details, tokens, private user data, or other sensitive material in a public issue.
