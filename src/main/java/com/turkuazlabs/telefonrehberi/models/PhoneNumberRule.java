// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/PhoneNumberRule.java
// # 📌 Amac: Bir ulkenin telefon girisi icin ornek, beklenen uzunluk ve gorunur gruplama kuralini tasir.
// # 📌 Model - Java
// # Version: 2.10.0
// # Aciklama: Ulusal numara uzunluk araligi ve otomatik bosluklandirma gruplarini immutable olarak saklar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

import java.util.List;

public record PhoneNumberRule(
        String isoCode,
        String exampleNational,
        int minNationalDigits,
        int maxNationalDigits,
        List<Integer> displayGroups
) {
    public PhoneNumberRule {
        isoCode = isoCode == null ? "" : isoCode.trim().toUpperCase();
        exampleNational = exampleNational == null ? "" : exampleNational.trim();
        if (minNationalDigits < 1 || maxNationalDigits < minNationalDigits) {
            throw new IllegalArgumentException("Gecersiz telefon uzunluk kurali.");
        }
        displayGroups = displayGroups == null ? List.of() : List.copyOf(displayGroups);
    }
}
