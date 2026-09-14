// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactFilterOption.java
// # 📌 Amac: ID tabanli grup ve etiket filtre seceneklerini tip guvenli olarak temsil eder.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Tumu secenegi icin null ID, gercek grup/etiket secenekleri icin kimlik ve gorunen ad tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record ContactFilterOption(Long id, String label) {
    public ContactFilterOption {
        label = label == null ? "" : label.trim();
    }

    public boolean all() {
        return id == null;
    }

    @Override
    public String toString() {
        return label;
    }
}
