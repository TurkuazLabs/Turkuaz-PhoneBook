// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/HistoryAction.java
// # 📌 Amac: Kisi gecmisinde kullanilan islem tiplerini merkezi olarak tanimlar.
// # 📌 Model - Java
// # Version: 2.4.0
// # Aciklama: Magic string kullanmadan create/update/delete/restore/sync/merge/import aksiyonlarini tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public enum HistoryAction {
    CREATE,
    UPDATE,
    DELETE,
    RESTORE,
    MOBILE_SYNC,
    MERGE,
    IMPORT,
    HISTORY_RESTORE
}
