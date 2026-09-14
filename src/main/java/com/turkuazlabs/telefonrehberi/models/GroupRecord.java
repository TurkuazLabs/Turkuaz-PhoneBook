// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/GroupRecord.java
// # 📌 Amac: Manuel kisi gruplarini ve uye sayisini tasir.
// # 📌 Model - Java
// # Version: 2.4.0
// # Aciklama: Grup kimligi, adi, renk anahtari ve bagli kisi sayisini immutable olarak tutar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record GroupRecord(long id, String name, String color, int contactCount) {
}
