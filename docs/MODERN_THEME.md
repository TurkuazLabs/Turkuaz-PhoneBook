# Dosya Yolu: C:/Projects/TelefonRehberi/docs/MODERN_THEME.md
# Amac: Turkuaz PhoneBook masaustu tema sisteminin renk, yuzey, durum ve erisilebilirlik kurallarini belgeler.
# View - Markdown
# Version: 3.0.0
# Aciklama: Tema v3 semantik tokenlari, Light/Dark yuzey hiyerarsisi, FlatLaf eslemeleri ve kontrast kurallarini tanimlar.
# Bagimli Oldugu Katman: View | Config | Tool

# Turkuaz Theme v3

## Tasarim ilkeleri

- Acik tema varsayilan ve birincil gorunumdur.
- Koyu tema ayni semantik hiyerarsiyi korur; sadece renk degerleri degisir.
- Turkuaz, marka kimligi ile aksiyon rengi olarak iki farkli tona ayrilir.
- Logo/marka turkuazi sabittir; primary aksiyon dolgusu beyaz metin kontrasti icin daha koyudur.
- Arka plan ve kartlar turkuaza boyanmaz; neutral gri/komur yuzeyler kullanilir.
- Gradient, agir golge, neon kenarlik ve ayni anda birden fazla vurgu rengi kullanilmaz.
- Icerik hiyerarsisi `background -> surface -> surfaceElevated -> surfaceMuted` semantigiyle kurulur.
- Normal ve guclu border tonlari ayridir; hover icin rastgele renk uretilmez.
- Focus halkasi klavye kullanicisi icin gorunur tutulur ve tamamen kapatilmaz.

## Semantik renk tokenlari

Tum yeni UI kodu renkleri `ModernThemePalette` uzerinden alir.

- `background`: ana pencere zemini.
- `backgroundAlt`: title/ikincil zemin.
- `surface`: liste ve temel icerik yuzeyi.
- `surfaceElevated`: kart, popup ve buton yuzeyi.
- `surfaceMuted`: dashboard satiri, chip ve yumusak ikincil yuzey.
- `border`: normal ayirici ve kontrol kenari.
- `borderStrong`: hover veya daha belirgin ayirim.
- `textPrimary`: ana metin.
- `textSecondary`: aciklama/meta metni.
- `textMuted`: disabled veya dusuk oncelikli metin.
- `accent/accentStrong/accentSoft`: secim, focus ve aktif durumlar.
- `selectionBackground`: liste, tablo ve text selection zemini.
- `controlHover/controlPressed`: neutral kontrol durumlari.
- `danger/dangerSoft/dangerHover/dangerPressed`: silme ve geri donulemez aksiyonlar.

View katmaninda yeni `new Color(...)` kullanmak yerine semantik token eklenir.

## Light tema

- Ana zemin cok acik neutral gri kullanir.
- Kartlar beyaz kalir; border ile zeminden ayrilir.
- Turkuaz sadece focus, secim, aktif navigation ve primary aksiyonda belirginlesir.
- Ikincil metin beyaz yuzey uzerinde WCAG AA seviyesini koruyacak koyulukta tutulur.
- Danger hover kirmizi/pembe soft yuzey kullanir; turkuazla karismaz.

## Dark tema

- Ana zemin teal yerine neutral charcoal tabanlidir.
- Kartlar arka plandan bir kademe acik, borderlar ise yuzeyden net sekilde ayridir.
- Parlak turkuaz sadece accent/focus icin kullanilir.
- Danger renkleri Dark tema icin ayri hesaplanir; Light tema hover rengi tekrar kullanilmaz.
- Scrollbar ve disabled durumlari parlak accent yerine neutral tonlarda kalir.

## Standart kontroller

`FlatLafThemeTool` su bilesenleri merkezi tokenlarla esler:

- Button / ToggleButton
- TextField / PasswordField / FormattedTextField
- TextArea / TextPane / EditorPane
- ComboBox / Spinner
- List / Tree / Table / TableHeader
- TabbedPane
- Menu / MenuItem / PopupMenu
- ProgressBar
- ScrollBar
- Tooltip
- TitlePane / Panel / RootPane / Label

Kontrol radius degeri 10px, kart radius degeri 16px'tir. Focus halkasi 2px olarak korunur.

## Buton sistemi

Tum uygulama aksiyonlari `ModernButtons` uzerinden uretilir.

- Primary: kontrastli koyu Turkuaz dolgu + beyaz metin.
- Secondary: neutral elevated yuzey + ince border.
- Danger: normal durumda sakin yuzey, hover/pressed durumunda semantik danger soft yuzey.
- Text: cercevesiz; hover durumunda soft accent zemini.
- Quick: Secondary ile ayni gorsel dil, ikon destekli.
- Chip: daha kucuk, pill geometrili neutral kontrol.

Her varyant `default / hover / pressed / disabled / keyboard focus` durumlarini ayri cizer.

## Erisilebilirlik ve kalite kapisi

- Primary buton foreground/background kontrasti en az WCAG AA 4.5:1 olmalidir.
- Ana metin/background kontrasti Light ve Dark temada en az 7:1 hedefler.
- Ikincil metin/surface kontrasti en az 4.5:1 olmalidir.
- Dark danger metni/surface kontrasti en az 4.5:1 olmalidir.
- Light ve Dark danger hover renkleri ayni olamaz.
- Normal ve guclu border tokenlari ayni olamaz.

Bu kurallar `QualityGateTest` icinde otomatik dogrulanir.

## Merkezi kaynaklar

- `config/ModernThemePalette.java`: tum semantik renk tokenlari.
- `config/UiConfig.java`: radius, boyut, focus kalinligi ve buton geometrisi.
- `tools/FlatLafThemeTool.java`: standart Swing/FlatLaf bilesen eslemeleri.
- `views/ModernButtons.java`: aksiyon butonlari.
- `views/PhoneBookFrame.java`: kart, sidebar ve ozel renderer kompozisyonu.

## Tema degistirme

Tema secimi `Ayarlar > Gorunum` ekranindan yapilir. Akis:

`SettingsService -> ThemeService -> FlatLafThemeTool`

Tema degisince acik pencereler yeniden UI update alir. Yeni kurulumlarda `light` varsayilandir.
