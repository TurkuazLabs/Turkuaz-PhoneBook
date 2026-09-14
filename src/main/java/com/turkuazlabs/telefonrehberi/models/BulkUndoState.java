// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/BulkUndoState.java
// # 📌 Amac: Undo/redo gecmisindeki bir toplu islemin geri yuklenebilir durumunu immutable modelde tasir.
// # 📌 Model - Java
// # Version: 1.1.0
// # Aciklama: Islem turu, kullanici etiketi, ilgili grup/etiket kimligi, kisi kimlikleri ve tam kisi snapshotlarini gecmis icin saklar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

import java.util.List;

public record BulkUndoState(
        BulkUndoType type,
        String label,
        long relatedId,
        List<Long> contactIds,
        List<Contact> contacts
) {
    public BulkUndoState {
        contactIds = contactIds == null ? List.of() : List.copyOf(contactIds);
        contacts = contacts == null ? List.of() : List.copyOf(contacts);
        label = label == null ? "" : label;
    }
}
