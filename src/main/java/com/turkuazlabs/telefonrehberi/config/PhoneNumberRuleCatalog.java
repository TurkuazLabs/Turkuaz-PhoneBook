// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/PhoneNumberRuleCatalog.java
// # 📌 Amac: Telefon girisi icin ulkeye gore ornek, uzunluk ve gorunur gruplama kurallarini merkezi tutar.
// # 📌 Config - Java
// # Version: 2.10.0
// # Aciklama: Yaygin ulkeler icin pratik ulusal numara kurallari saglar; diger ulkelerde E.164 uyumlu genel kurala duser.
// # Bagimli Oldugu Katman: Config | Model
package com.turkuazlabs.telefonrehberi.config;

import com.turkuazlabs.telefonrehberi.models.PhoneNumberRule;

import java.util.List;
import java.util.Map;

public final class PhoneNumberRuleCatalog {
    private static final PhoneNumberRule FALLBACK = new PhoneNumberRule("", "555 123 4567", 5, 14, List.of(3, 3, 4));

    private static final Map<String, PhoneNumberRule> RULES = Map.ofEntries(
            rule("TR", "555 123 45 67", 10, 10, 3, 3, 2, 2),
            rule("US", "555 123 4567", 10, 10, 3, 3, 4),
            rule("CA", "555 123 4567", 10, 10, 3, 3, 4),
            rule("GB", "7400 123 456", 9, 10, 4, 3, 3),
            rule("DE", "151 234 5678", 7, 11, 3, 3, 4),
            rule("FR", "6 12 34 56 78", 9, 9, 1, 2, 2, 2, 2),
            rule("IT", "312 345 6789", 9, 11, 3, 3, 4),
            rule("ES", "612 345 678", 9, 9, 3, 3, 3),
            rule("PT", "912 345 678", 9, 9, 3, 3, 3),
            rule("NL", "6 12345678", 9, 9, 1, 8),
            rule("BE", "470 12 34 56", 8, 9, 3, 2, 2, 2),
            rule("CH", "79 123 45 67", 9, 9, 2, 3, 2, 2),
            rule("AT", "664 123 4567", 7, 13, 3, 3, 4),
            rule("GR", "691 234 5678", 10, 10, 3, 3, 4),
            rule("BG", "88 123 4567", 8, 9, 2, 3, 4),
            rule("RO", "712 345 678", 9, 9, 3, 3, 3),
            rule("PL", "512 345 678", 9, 9, 3, 3, 3),
            rule("CZ", "601 123 456", 9, 9, 3, 3, 3),
            rule("SK", "912 123 456", 9, 9, 3, 3, 3),
            rule("HU", "20 123 4567", 8, 9, 2, 3, 4),
            rule("SE", "70 123 45 67", 7, 10, 2, 3, 2, 2),
            rule("NO", "412 34 567", 8, 8, 3, 2, 3),
            rule("DK", "20 12 34 56", 8, 8, 2, 2, 2, 2),
            rule("FI", "40 123 4567", 7, 10, 2, 3, 4),
            rule("IE", "85 123 4567", 7, 9, 2, 3, 4),
            rule("AZ", "50 123 45 67", 9, 9, 2, 3, 2, 2),
            rule("AE", "50 123 4567", 9, 9, 2, 3, 4),
            rule("SA", "50 123 4567", 9, 9, 2, 3, 4),
            rule("QA", "3312 3456", 8, 8, 4, 4),
            rule("KW", "500 12345", 8, 8, 3, 5),
            rule("BH", "3600 1234", 8, 8, 4, 4),
            rule("OM", "9212 3456", 8, 8, 4, 4),
            rule("IQ", "770 123 4567", 10, 10, 3, 3, 4),
            rule("IR", "912 123 4567", 10, 10, 3, 3, 4),
            rule("IL", "50 123 4567", 8, 9, 2, 3, 4),
            rule("EG", "100 123 4567", 10, 10, 3, 3, 4),
            rule("RU", "912 345 67 89", 10, 10, 3, 3, 2, 2),
            rule("KZ", "701 123 4567", 10, 10, 3, 3, 4),
            rule("UZ", "90 123 45 67", 9, 9, 2, 3, 2, 2),
            rule("IN", "98765 43210", 10, 10, 5, 5),
            rule("PK", "300 1234567", 10, 10, 3, 7),
            rule("CN", "138 0013 8000", 11, 11, 3, 4, 4),
            rule("JP", "90 1234 5678", 9, 10, 2, 4, 4),
            rule("KR", "10 1234 5678", 9, 10, 2, 4, 4),
            rule("AU", "412 345 678", 9, 9, 3, 3, 3),
            rule("NZ", "21 123 4567", 8, 10, 2, 3, 4),
            rule("BR", "11 91234 5678", 10, 11, 2, 5, 4),
            rule("MX", "55 1234 5678", 10, 10, 2, 4, 4),
            rule("AR", "11 2345 6789", 10, 10, 2, 4, 4),
            rule("ZA", "82 123 4567", 9, 9, 2, 3, 4)
    );

    private PhoneNumberRuleCatalog() {
    }

    public static PhoneNumberRule byIso(String isoCode) {
        if (isoCode == null || isoCode.isBlank()) return FALLBACK;
        return RULES.getOrDefault(isoCode.trim().toUpperCase(java.util.Locale.ROOT), FALLBACK);
    }

    private static Map.Entry<String, PhoneNumberRule> rule(
            String isoCode,
            String example,
            int minDigits,
            int maxDigits,
            Integer... groups
    ) {
        PhoneNumberRule value = new PhoneNumberRule(isoCode, example, minDigits, maxDigits, List.of(groups));
        return Map.entry(isoCode, value);
    }
}
