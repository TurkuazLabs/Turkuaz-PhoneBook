// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactReminderSummary.java
// # 📌 Amac: Kisi profilinde gosterilecek onemli tarih ve iletisimde kal ozetini tasir.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Bir sonraki iletisim tarihi, gecikme durumu ve bugun aktif olan tarih hatirlatma sayisini tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record ContactReminderSummary(
        String nextContactDate,
        boolean keepInTouchDue,
        int activeImportantDateReminders
) {
    public static ContactReminderSummary empty() {
        return new ContactReminderSummary("", false, 0);
    }
}
