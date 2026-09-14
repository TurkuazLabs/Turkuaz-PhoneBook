// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/TagRecord.java
// # 📌 Amac: Renkli kisi etiketlerini ve kullanim sayisini tasir.
// # 📌 Model - Java
// # Version: 2.4.0
// # Aciklama: Etiket kimligi, adi, renk anahtari ve bagli kisi sayisini immutable olarak tutar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record TagRecord(long id, String name, String color, int contactCount) {
}
