# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/assets/branding/README.tr.md
# 📌 Amac: Turkuaz marka ikon ve logo kurallarini Turkce olarak tanimlar.
# 📌 View - Markdown
# Version: 1.3.0
# Aciklama: Dil bagimsiz Turkuaz marka kimligi, logo kullanimi ve yerellestirilmis urun adi kurallarini Turkce olarak belgeler.
# Bagimli Oldugu Katman: View | Tool | Language

# Turkuaz Marka Kurallari

Ana marka adi **Turkuaz**'dir.

`app-logo.png` icinde `Telefon Rehberi`, `PhoneBook`, `Contacts` veya baska bir dile ait urun tanimi bulunmamalidir. Urun adi bitmap marka gorselinde degil, uygulama ve kurulumun Language katmaninda yerellestirilir.

Gorunen urun adi:

- Turkce: `Turkuaz Telefon Rehberi`
- Ingilizce ve diger diller icin fallback: `Turkuaz PhoneBook`

Mevcut `app-logo.png`, bilerek ana `app-icon.png` ile ayni dil bagimsiz gorseli kullanir. Ileride ozel bir Turkuaz wordmark tasarlanabilir; ancak bu gorselde de dile bagli urun tanimi bulunmamalidir.

Geriye donuk uyumluluk gerektiren teknik kimlikler marka degisikliginden bagimsiz tutulur. `TelefonRehberi.exe`, Java package adlari, Inno Setup `AppId`, mevcut kullanici veri klasorleri ve SQLite veritabani yolu yeniden adlandirilmaz.

PNG ve ICO uygulama ikonlari Swing pencere, portable paket, Windows kisayollari, Linux masaustu entegrasyonu ve kurulum paketinde ortak kullanilir. Windows Setup paketlenirken `tools/build-installer.ps1`, `app-icon-512.png` kaynagindan staged `app-icon.ico` dosyasini yeniden uretir: dis seffaf padding kirpilir ve sembol yaklasik %4 guvenli kenar payiyla 16/24/32/48/64/128/256 px framelere yerlestirilir. Boylece kurulumdan gelen masaustu ve Baslat menusu kisayol ikonu, ana tasarim degismeden Windows ikon hucresini daha dolu kullanir.

English version: [README.md](./README.md)
