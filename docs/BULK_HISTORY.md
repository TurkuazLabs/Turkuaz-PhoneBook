# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/BULK_HISTORY.md
# 📌 Amac: Toplu kisi islemlerindeki cok adimli Undo/Redo gecmisinin davranisini ve katman sorumluluklarini tanimlar.
# 📌 Modul - Markdown
# Version: 1.0.0
# Aciklama: Undo/redo stack kurallari, kapasite, kisayollar ve desteklenen toplu islem turlerini belgeler.
# Bagimli Oldugu Katman: Controller | Service | Model | View | Config | Language

# Toplu Islem Undo/Redo Gecmisi

## Kapsam

Toplu firma, kategori, favori, grup, etiket ve Cop Kutusu islemleri `BulkUndoService` tarafindan geri alinabilir ve yeniden uygulanabilir.

## Gecmis kurali

- Undo ve redo icin ayri stack kullanilir.
- Stack kapasitesi `config/app.yml` icindeki `bulk_history_limit` degeridir.
- Varsayilan kapasite 20 islemdir.
- Basarili yeni toplu islem undo stack'ine eklenir ve redo stack'ini temizler.
- Gercek degisiklik uretmeyen toplu islem gecmisi degistirmez.
- Bildirim timeout olmasi yalnizca View'i gizler; Service gecmisi korunur.

## Kisayollar

- `Ctrl+Z`: Son toplu islemi geri alir.
- `Ctrl+Y`: Son geri alinan toplu islemi yeniden uygular.
- macOS ortaminda Swing menu shortcut mask kullanildigi icin platformun standart Command kisayolu uygulanir.

## Katman sorumlulugu

Controller yalnizca View istegini alir ve `BulkUndoService` metodunu cagirir. Islem yonu, stack yonetimi, snapshot alma ve ters islem secimi Service katmanindadir. Repository erisimi mevcut Contact/Group/Tag servisleri uzerinden gerceklesir. View yalnizca bildirim ve kisayol eventlerini sunar.
