// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/HistoryEntry.java
// # 📌 Amac: Kisi degisiklik gecmisi kaydini tasir.
// # 📌 Model - Java
// # Version: 2.4.0
// # Aciklama: Snapshot, islem tipi ve zaman bilgisini geri yukleme icin tutar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record HistoryEntry(long id, long contactId, String action, String contactName, String snapshot, String createdAt) {
}
