// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/SmartList.java
// # 📌 Amac: Kural tabanli dinamik kisi listesi tanimini tasir.
// # 📌 Model - Java
// # Version: 2.4.0
// # Aciklama: Akilli liste adi, alan, operator ve deger bilgisini kalici model olarak tutar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record SmartList(long id, String name, String field, String operator, String value) {
}
