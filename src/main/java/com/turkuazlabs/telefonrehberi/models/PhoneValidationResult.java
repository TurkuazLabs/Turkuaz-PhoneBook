// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/PhoneValidationResult.java
// # 📌 Amac: Telefon numarasi bicim kontrolunun sonucunu View katmanina tipli olarak tasir.
// # 📌 Model - Java
// # Version: 2.10.0
// # Aciklama: Validasyon durumu, mevcut rakam sayisi ve beklenen ulusal numara uzunluk araligini tutar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record PhoneValidationResult(
        Status status,
        int nationalDigitCount,
        int minNationalDigits,
        int maxNationalDigits
) {
    public enum Status {
        EMPTY,
        VALID,
        TOO_SHORT,
        TOO_LONG
    }

    public boolean valid() {
        return status == Status.VALID;
    }
}
