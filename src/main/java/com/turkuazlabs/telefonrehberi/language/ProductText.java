// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/ProductText.java
// # 📌 Amac: Masaustu uygulamasinin dil bagimsiz marka ve yerellestirilmis urun adini merkezi sunar.
// # 📌 Language - Java
// Version: 1.1.0
// Aciklama: Ortak LocaleText secicisi ile Turkce sistemlerde Turkuaz Telefon Rehberi, diger dillerde Turkuaz PhoneBook gorunen adini kullanir.
// Bagimli Oldugu Katman: Language
package com.turkuazlabs.telefonrehberi.language;

public final class ProductText {
    public static final String BRAND_NAME = "Turkuaz";
    public static final String APP_NAME_TR = "Turkuaz Telefon Rehberi";
    public static final String APP_NAME_EN = "Turkuaz PhoneBook";
    public static final String APP_NAME = LocaleText.text(APP_NAME_TR, APP_NAME_EN);

    public static boolean isTurkishLocale() {
        return LocaleText.isTurkish();
    }

    private ProductText() {
    }
}
