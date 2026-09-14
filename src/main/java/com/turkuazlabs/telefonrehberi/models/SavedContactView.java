// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/SavedContactView.java
// # 📌 Amac: Kaydedilmis kisi gorunumu adi, kimligi, varsayilan durumu ve filtre/siralama durumunu immutable olarak tasir.
// # 📌 Model - Java
// # Version: 1.1.0
// # Aciklama: Kullanici tarafindan saklanan ContactFilter kombinasyonunu ve acilista uygulanacak varsayilan gorunum bilgisini temsil eder.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record SavedContactView(String id, String name, ContactFilter filter, boolean defaultView) {
    public SavedContactView {
        id = safe(id);
        name = safe(name);
        filter = filter == null
                ? new ContactFilter("", false, "", "", "", null, null, ContactSort.defaultSort())
                : filter;
    }

    public SavedContactView withName(String newName) {
        return new SavedContactView(id, newName, filter, defaultView);
    }

    public SavedContactView withDefaultView(boolean value) {
        return new SavedContactView(id, name, filter, value);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
