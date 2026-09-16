// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/PhoneCountryCodeText.java
// # 📌 Amac: Telefon ulke kodu seceneklerini ISO kodundan kullanici dilinde gosterir.
// # 📌 Language - Java
// Version: 1.0.0
// Aciklama: Config/Model katmanindaki mevcut ulke verisini degistirmeden Turkce veya Ingilizce ulke adini ve arama kodunu display metnine cevirir.
// # Bagimli Oldugu Katman: Language | Model
package com.turkuazlabs.telefonrehberi.language;

import com.turkuazlabs.telefonrehberi.models.PhoneCountryCode;

import java.util.IllformedLocaleException;
import java.util.Locale;

public final class PhoneCountryCodeText {
    public static String display(PhoneCountryCode value) {
        if (value == null) {
            return Messages.EMPTY_VALUE;
        }
        if (value.custom()) {
            return LocaleText.text("Diger / Ozel Kod", "Other / Custom Code");
        }
        String countryName = countryName(value);
        return value.dialCode().isBlank() ? countryName : countryName + " (" + value.dialCode() + ")";
    }

    public static String countryName(PhoneCountryCode value) {
        if (value == null || value.isoCode().isBlank()) {
            return Messages.EMPTY_VALUE;
        }
        Locale displayLocale = LocaleText.isTurkish()
                ? Locale.forLanguageTag("tr-TR")
                : Locale.ENGLISH;
        try {
            Locale countryLocale = new Locale.Builder().setRegion(value.isoCode()).build();
            String localized = countryLocale.getDisplayCountry(displayLocale);
            if (localized != null && !localized.isBlank() && !localized.equalsIgnoreCase(value.isoCode())) {
                return localized;
            }
        } catch (IllformedLocaleException ignored) {
            // XK gibi platforma gore taninmayan kodlarda katalog adina geri donulur.
        }
        return value.countryName().isBlank() ? value.isoCode() : value.countryName();
    }

    private PhoneCountryCodeText() {
    }
}
