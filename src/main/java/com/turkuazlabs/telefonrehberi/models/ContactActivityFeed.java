// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactActivityFeed.java
// # 📌 Amac: Kisi profilindeki filtrelenmis aktivite kayitlarini ve filtre sayaclarini tasir.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Timeline kayitlarini toplam, degisiklik, aktarim ve geri yukleme adetleriyle birlikte View katmanina iletir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

import java.util.List;

public record ContactActivityFeed(
        List<HistoryEntry> entries,
        int totalCount,
        int changeCount,
        int transferCount,
        int restoreCount
) {
    public ContactActivityFeed {
        entries = entries == null ? List.of() : List.copyOf(entries);
    }

    public static ContactActivityFeed empty() {
        return new ContactActivityFeed(List.of(), 0, 0, 0, 0);
    }
}
