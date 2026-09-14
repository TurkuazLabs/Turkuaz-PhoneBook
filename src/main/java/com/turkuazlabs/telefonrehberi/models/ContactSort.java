// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactSort.java
// # 📌 Amac: Kisi listesinin destekledigi siralama seceneklerini tip guvenli olarak tanimlar.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Ad, firma, sehir, son degistirilme ve favori oncelikli siralama turlerini temsil eder.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public enum ContactSort {
    FAVORITES_FIRST,
    NAME_ASC,
    NAME_DESC,
    COMPANY,
    CITY,
    UPDATED_DESC;

    public static ContactSort defaultSort() {
        return FAVORITES_FIRST;
    }
}
