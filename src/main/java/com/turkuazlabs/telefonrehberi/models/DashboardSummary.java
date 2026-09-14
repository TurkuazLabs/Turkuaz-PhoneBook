// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/DashboardSummary.java
// # 📌 Amac: Ana sayfa istatistik kartlarinda gosterilecek rehber ozetini tasir.
// # 📌 Model - Java
// # Version: 2.33.0
// # Aciklama: Toplam, favori, bugunku dogum gunu, takip gerektiren kisi, firma ve sehir sayilarini tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record DashboardSummary(
        int totalContacts,
        int favoriteContacts,
        int birthdaysToday,
        int followUpsDue,
        int companies,
        int cities
) {
}
