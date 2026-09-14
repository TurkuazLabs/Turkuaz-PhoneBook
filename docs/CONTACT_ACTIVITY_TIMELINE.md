# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/CONTACT_ACTIVITY_TIMELINE.md
# 📌 Amac: Kisi profili activity timeline mimarisini, filtrelerini ve veri akis sozlesmesini aciklar.
# 📌 Modul - Markdown
# Version: 1.0.0
# Aciklama: v2.32.0 profil timeline gorunumu mevcut contact_history verisini yeniden kullanir; yeni paralel history storage olusturmaz.
# Bagimli Oldugu Katman: Controller | Service | Repository | View | Language | Config

# Contact Activity Timeline

## Veri kaynagi

Timeline `contact_history` tablosunu kullanir. `ContactHistoryRepository.findByContactId` secili kisiye ait history kayitlarini en yeni kayittan eskiye dogru okur.

## Katman akisi

1. View kisi secimini veya activity filtre degisikligini Controller'a bildirir.
2. Controller secili kisi kimligi ve filtre enum degerini `ContactService.contactActivity` metoduna iletir.
3. Service history aksiyonlarini filtre kategorilerine ayirir ve `contact_activity_limit` kadar kaydi gorunur feed'e alir.
4. `ContactActivityFeed` gorunur history kayitlari ile tum filtre sayaclarini View'a tasir.
5. View timeline satirlarini `ContactActivityTimelineRow` ile cizer.

## Filtreler

- `ALL`: Tum history aksiyonlari.
- `CHANGES`: CREATE, UPDATE, DELETE ve MERGE.
- `TRANSFER`: MOBILE_SYNC ve IMPORT.
- `RESTORE`: RESTORE ve HISTORY_RESTORE.

Filtreleme Controller veya View icinde yapilmaz.

## Config

`config/app.yml`:

- `history_limit`: Genel Kisi Gecmisi sayfasinin son kayit limiti.
- `contact_activity_limit`: Profil timeline icinde tek filtre icin gosterilecek maksimum satir sayisi.

## UI ilkeleri

- Profil hero alani timeline'a daha fazla gorunur alan birakmak icin kompakt tutulur.
- Filtreler chip benzeri toggle butonlarla ve kategori sayaclariyla gosterilir.
- Timeline satiri islem adi, aciklama ve tarih bilgisini birlikte verir.
- Timeline yalnizca read-only ozet gorunumdur; tam snapshot restore islemi mevcut Kisi Gecmisi sayfasinda kalir.
