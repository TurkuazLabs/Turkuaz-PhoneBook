# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/MOBILE_SYNC_API.md
# 📌 Amac: v2.37.0 guvenilir LAN mobil senkron API sozlesmesini belgeler.
# 📌 Modul - Markdown
# Version: 2.0.0
# Aciklama: Token, sync UUID, request limiti, endpointler ve guvenlik sinirlarini tanimlar.
# Bagimli Oldugu Katman: Controller | Service | Tool | Config | Model

## Guvenlik modeli

Mobil senkron varsayilan olarak kapalidir. Kullanici Ayarlar/Mobil Senkron alanindan etkinlestirir. Sunucu yalnizca guvenilir LAN/Wi-Fi icin tasarlanmistir; 8787 portunu Internet'e acma. API local HTTP kullanir ve tum endpointler `Authorization: Bearer <sync-token>` ister.

Android tokeni Android Keystore AES/GCM ile, iOS tokeni Keychain ile saklar. Masaustu tokeni kullanici teknik data alaninda tutulur.

Varsayilan POST body limiti 2 MiB'dir; limit asilirsa HTTP `413` doner.

## Kimlik

Her masaustu kisi kalici `sync_uuid` tasir. Mobil cihaz kendi native contact ID'si ile bu UUID arasinda yerel mapping saklar. Push cevabi canonical `sync_uuid` dondurur. Bu kimlik sonraki push/pull islemlerinde kullanilarak ayni kisinin yeniden olusturulmasi engellenir. Merge edilen eski UUID ve device/external ID degerleri alias tablolariyla canonical kisiye yonlendirilir.

## Endpointler

- `GET /api/status`: API ve uygulama surum durumu.
- `GET /api/contacts`: aktif kisiler; `id`, `sync_uuid`, sinirsiz `phones[]`, `emails[]`, profil fotografi ve temel alanlar.
- `POST /api/import`: `application/x-www-form-urlencoded` kisi push; `device_id`, `external_id`, opsiyonel `sync_uuid` zorunlu kimlik akisini tasir.

Basarili import HTTP `201` ve canonical `sync_uuid` dondurur.

## HTTP durumlari

- `200`: basarili GET
- `201`: basarili import
- `400`: gecersiz istek
- `401`: token hatasi
- `405`: desteklenmeyen method
- `413`: request body limiti asildi
- `500`: sunucu islemi basarisiz
