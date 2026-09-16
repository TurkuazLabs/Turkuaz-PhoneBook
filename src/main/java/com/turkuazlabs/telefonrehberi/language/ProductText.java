// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/ProductText.java
// # 📌 Amac: Masaustu uygulamasinin dil bagimsiz marka ve yerellestirilmis urun adini merkezi sunar.
// # 📌 Language - Java
// Version: 1.0.0
// Aciklama: Sistem dili Turkce ise Turkuaz Telefon Rehberi, diger dillerde Turkuaz PhoneBook gorunen adini kullanir.
// Bagimli Oldugu Katman: Language
package com.turkuazlabs.telefonrehberi.language;

import java.util.Locale;

public final class ProductText {
    private static final String TURKISH_LANGUAGE_CODE = "tr";

    public static final String BRAND_NAME = "Turkuaz";
    public static final String APP_NAME_TR = "Turkuaz Telefon Rehberi";
    public static final String APP_NAME_EN = "Turkuaz PhoneBook";
    public static final String APP_NAME = isTurkishLocale() ? APP_NAME_TR : APP_NAME_EN;

    public static boolean isTurkishLocale() {
        return TURKISH_LANGUAGE_CODE.equalsIgnoreCase(Locale.getDefault().getLanguage());
    }

    private ProductText() {
    }
}
