# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/DESKTOP_REMINDERS.md
# 📌 Amac: Masaustu hatirlatma bildirimlerinin davranisini, gizlilik kurallarini ve katman sorumluluklarini tanimlar.
# 📌 Modul - Markdown
# Version: 1.0.0
# Aciklama: v2.39.0 masaustu reminder notification modulunun kullanici tercihi, tetikleme ve gizlilik sozlesmesidir.
# Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View | Language

# Desktop Reminders

## Kapsam

Turkuaz PhoneBook v2.39.0, mevcut dogum gunu, onemli tarih ve Iletisimde Kal kurallarini masaustu bildirimiyle ozetleyebilir.

Bildirim tercihi **Ayarlar > Genel** bolumundedir ve varsayilan olarak kapali gelir.

## Davranis

- Uygulama acilisinda aktif hatirlatmalar Service katmaninda hesaplanir.
- Aktif hatirlatma yoksa bildirim uretilmez.
- Aktif hatirlatma varsa acilis basina en fazla bir ozet bildirim uretilir.
- Bildirim tercihi kapaliysa SystemTray entegrasyonu cagrilmaz.
- SystemTray desteklenmeyen ortamlarda uygulama normal calismaya devam eder.

## Gizlilik

Masaustu bildirimi kisi adi, telefon, e-posta, firma veya tarih ayrintisi tasimaz.

Bildirim yalnizca aktif hatirlatmaya sahip kisi sayisini gosterir. Ayrintilar uygulama icinden goruntulenir.

## Mimari

`Controller -> Service -> Repository/Model -> Tool -> View -> Language`

- `PhoneBookController`: acilista Service cagrisi yapar.
- `ReminderNotificationService`: preference, aktif hatirlatma sayisi ve bildirim is kuralini yonetir.
- `ContactService`: mevcut hatirlatma kurallariyla aktif kisileri secer.
- `UserPreferencesRepository`: `reminder_notifications_enabled` degerini saklar.
- `DesktopNotificationTool`: Java `SystemTray` / `TrayIcon` adaptorudur.
- `PhoneBookFrame`: kullanici tercih kontrolunu sunar.
- `Messages`: Turkce/Ingilizce bildirim ve ayar metinlerini merkezi tutar.

## Veri Modeli

Yeni SQLite tablosu veya schema migration yoktur. Modul mevcut kisi hatirlatma alanlarini kullanir.

Kullanici tercihi:

`reminder_notifications_enabled: "true|false"`

Eski preferences dosyalarinda alan bulunmuyorsa guvenli fallback **false** degeridir.
