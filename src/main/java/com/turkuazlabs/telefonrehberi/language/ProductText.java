// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/ProductText.java
// # 📌 Amac: Masaustu uygulamasinin dil bagimsiz marka ve yerellestirilmis urun adini merkezi sunar.
// # 📌 Language - Java
// Version: 1.3.0
// Aciklama: Ortak LocaleText secicisi ile urun adini runtime dil degisiminde yeniden yuklenebilir olarak sunar.
// Bagimli Oldugu Katman: Language
package com.turkuazlabs.telefonrehberi.language;

public final class ProductText {
    public static final String BRAND_NAME = "Turkuaz";
    public static final String APP_NAME_TR = "Turkuaz Telefon Rehberi";
    public static final String APP_NAME_EN = "Turkuaz PhoneBook";
    public static String APP_NAME = currentAppName();

    public static void reload() {
        APP_NAME = currentAppName();
    }

    public static String currentAppName() {
        return LocaleText.text(APP_NAME_TR, APP_NAME_EN);
    }

    public static boolean isTurkishLocale() {
        return LocaleText.isTurkish();
    }

    private ProductText() {
    }
}
