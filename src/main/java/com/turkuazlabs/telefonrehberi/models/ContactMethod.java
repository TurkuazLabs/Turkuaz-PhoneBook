// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactMethod.java
// # 📌 Amac: Bir kisiye ait sinirsiz telefon veya e-posta iletisim satirini temsil eder.
// # 📌 Model - Java
// # Version: 2.8.0
// # Aciklama: Tur, etiket, deger, birincil durumu ve sira bilgisini immutable olarak tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record ContactMethod(
        String kind,
        String label,
        String value,
        boolean primary,
        int position
) {
    public static final String PHONE = "PHONE";
    public static final String EMAIL = "EMAIL";
    public static final String LABEL_MOBILE = "Cep";
    public static final String LABEL_OTHER = "Diger";
    public static final String LABEL_WORK = "Is";
    public static final String LABEL_HOME = "Ev";
    public static final String LABEL_EMAIL = "E-posta";
    public static final String LABEL_PHONE = "Telefon";

    public ContactMethod {
        kind = kind == null ? "" : kind.trim().toUpperCase();
        label = label == null ? "" : label.trim();
        value = value == null ? "" : value.trim();
        position = Math.max(0, position);
    }

    public boolean isPhone() { return PHONE.equals(kind); }
    public boolean isEmail() { return EMAIL.equals(kind); }
}
