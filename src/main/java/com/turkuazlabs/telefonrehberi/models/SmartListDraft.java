// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/SmartListDraft.java
// # 📌 Amac: Yeni akilli liste olusturma form verisini tasir.
// # 📌 Model - Java
// # Version: 2.4.0
// # Aciklama: Kimliksiz akilli liste kuralini Controller -> Service akisinda tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record SmartListDraft(String name, String field, String operator, String value) {
}
