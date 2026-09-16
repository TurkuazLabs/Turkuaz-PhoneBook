// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/LocaleText.java
// # 📌 Amac: Masaustu Language katmaninda sistem dilini ve Turkce/Ingilizce metin secimini merkezi yonetir.
// # 📌 Language - Java
// Version: 1.0.0
// Aciklama: Turkce locale icin Turkce, diger locale degerleri icin Ingilizce fallback metin secicisini sunar.
// Bagimli Oldugu Katman: Language
package com.turkuazlabs.telefonrehberi.language;

import java.util.Locale;

public final class LocaleText {
    private static final String TURKISH_LANGUAGE_CODE = "tr";

    public static boolean isTurkish() {
        return TURKISH_LANGUAGE_CODE.equalsIgnoreCase(Locale.getDefault().getLanguage());
    }

    public static String text(String turkish, String english) {
        return isTurkish() ? turkish : english;
    }

    private LocaleText() {
    }
}
