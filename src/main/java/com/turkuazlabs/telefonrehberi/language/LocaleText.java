// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/LocaleText.java
// # 📌 Amac: Masaustu Language katmaninda sistem dili, kullanici dil tercihi ve Turkce/Ingilizce metin secimini merkezi yonetir.
// # 📌 Language - Java
// Version: 1.1.0
// Aciklama: system/tr/en dil kodlarini normalize eder; kullanici tercihini sistem locale fallbackinden once uygular.
// Bagimli Oldugu Katman: Language
package com.turkuazlabs.telefonrehberi.language;

import java.util.Locale;

public final class LocaleText {
    public static final String LANGUAGE_PREFERENCE_KEY = "language";
    public static final String LANGUAGE_SYSTEM_CODE = "system";
    public static final String LANGUAGE_TURKISH_CODE = "tr";
    public static final String LANGUAGE_ENGLISH_CODE = "en";

    private static volatile String configuredLanguageCode = LANGUAGE_SYSTEM_CODE;

    public static void applyLanguage(String languageCode) {
        configuredLanguageCode = normalizeLanguageCode(languageCode);
    }

    public static String configuredLanguageCode() {
        return configuredLanguageCode;
    }

    public static String normalizeLanguageCode(String languageCode) {
        String value = languageCode == null ? "" : languageCode.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case LANGUAGE_TURKISH_CODE -> LANGUAGE_TURKISH_CODE;
            case LANGUAGE_ENGLISH_CODE -> LANGUAGE_ENGLISH_CODE;
            default -> LANGUAGE_SYSTEM_CODE;
        };
    }

    public static boolean isSupportedLanguageCode(String languageCode) {
        String value = languageCode == null ? "" : languageCode.trim().toLowerCase(Locale.ROOT);
        return LANGUAGE_SYSTEM_CODE.equals(value)
                || LANGUAGE_TURKISH_CODE.equals(value)
                || LANGUAGE_ENGLISH_CODE.equals(value);
    }

    public static boolean isTurkish() {
        if (LANGUAGE_TURKISH_CODE.equals(configuredLanguageCode)) return true;
        if (LANGUAGE_ENGLISH_CODE.equals(configuredLanguageCode)) return false;
        return LANGUAGE_TURKISH_CODE.equalsIgnoreCase(Locale.getDefault().getLanguage());
    }

    public static String text(String turkish, String english) {
        return isTurkish() ? turkish : english;
    }

    private LocaleText() {
    }
}
