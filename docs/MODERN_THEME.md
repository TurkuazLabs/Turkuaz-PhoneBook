# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/docs/MODERN_THEME.md
# 📌 Amac: Turkuaz Telefon Rehberi Turkuaz Clean tema sistemini belgeler.
# 📌 View - Markdown
# Version: 2.0.0
# Aciklama: Acik-oncelikli renk paleti, tipografi, kart, navigasyon ve tema secim kurallarini tanimlar.
# Bagimli Oldugu Katman: View | Config | Tool

# Turkuaz Clean Theme

## Tasarim ilkeleri

- Acik tema varsayilan ve birincil gorunumdur.
- Koyu tema ikinci secenektir ve yalnizca `Ayarlar > Gorunum` altindan secilir.
- Ana kisiler ekraninda hizli tema degistirme butonu bulunmaz.
- Turkuaz yalnizca aktif durum, primary aksiyon ve secili ogelerde vurgu rengi olarak kullanilir.
- Gradient arka plan, gradient sidebar ve agir golgeler kullanilmaz.
- Icerik hiyerarsisi `background -> surface -> surfaceMuted` seklindedir.
- Kartlar 16px radius, ince border ve cok hafif golge kullanir.
- Sidebar acik temada acik yuzey, koyu temada sade koyu yuzey kullanir.
- Navigasyon aktif durumda yumusak Turkuaz arka plan ve ince vurgu cizgisi kullanir.
- Primary butonlar tek renk Turkuaz dolgu kullanir.
- Tipografi Windows'ta Segoe UI Variable, fallback olarak Segoe UI kullanir.

## Merkezi kaynaklar

- `config/ModernThemePalette.java`: semantik acik/koyu renkler.
- `config/UiConfig.java`: boyut, radius, bosluk ve marka sabitleri.
- `tools/FlatLafThemeTool.java`: FlatLaf global UI varsayilanlari.
- `views/PhoneBookFrame.java`: kart, sidebar, toolbar ve custom renderer'lar.

## Tema degistirme

Tema secimi `Ayarlar > Gorunum` ekranindaki tema secicisinden yapilir. Kaydetme islemi `SettingsService -> ThemeService -> FlatLafThemeTool` akisiyla ilerler. Yeni kurulumlarda `light` varsayilan degerdir.
