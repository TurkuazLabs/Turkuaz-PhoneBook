// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/DuplicateCandidate.java
// # 📌 Amac: Muhtemel mukerrer iki kisi kaydini ve eslesme nedenini tasir.
// # 📌 Model - Java
// # Version: 2.4.0
// # Aciklama: Duzelt ve Yonet ekraninda birlestirme adayi olarak kullanilir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record DuplicateCandidate(Contact primary, Contact duplicate, String reason) {
}
