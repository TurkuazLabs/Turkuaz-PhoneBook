// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ImportResult.java
// # 📌 Amac: VCF/CSV ice aktarma sonucunu tasir.
// # 📌 Model - Java
// # Version: 2.4.0
// # Aciklama: Basarili ve atlanan kayit sayilarini tek sonuc nesnesinde tutar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record ImportResult(int imported, int skipped) {
}
