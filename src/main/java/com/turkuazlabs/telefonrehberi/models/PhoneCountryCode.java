// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/PhoneCountryCode.java
// # 📌 Amac: Telefon numarasi girisinde secilebilen ulke ve uluslararasi arama kodunu temsil eder.
// # 📌 Model - Java
// # Version: 2.9.0
// # Aciklama: ISO kodu, gorunen ulke adi ve + ile baslayan ulke kodunu immutable olarak tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record PhoneCountryCode(
        String isoCode,
        String countryName,
        String dialCode
) {
    public static final String CUSTOM_ISO = "CUSTOM";

    public PhoneCountryCode {
        isoCode = isoCode == null ? "" : isoCode.trim().toUpperCase();
        countryName = countryName == null ? "" : countryName.trim();
        dialCode = normalizeDialCode(dialCode);
    }

    public boolean custom() {
        return CUSTOM_ISO.equals(isoCode);
    }

    @Override
    public String toString() {
        if (custom()) return countryName;
        return countryName + " (" + dialCode + ")";
    }

    private static String normalizeDialCode(String value) {
        if (value == null || value.isBlank()) return "";
        String digits = value.replaceAll("[^0-9]", "");
        return digits.isBlank() ? "" : "+" + digits;
    }
}
