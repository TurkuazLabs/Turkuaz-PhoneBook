// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ImportantDate.java
// # 📌 Amac: Kisiye ait yildonumu veya ozel tarih kaydini temsil eder.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Tarih turu, gorunen etiket, tarih, hatirlatma suresi ve sirayi immutable kayitta tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record ImportantDate(
        ImportantDateType type,
        String label,
        String dateValue,
        ReminderLeadTime reminderLeadTime,
        int position
) {
    public ImportantDate {
        type = type == null ? ImportantDateType.CUSTOM : type;
        label = label == null ? "" : label.trim();
        dateValue = dateValue == null ? "" : dateValue.trim();
        reminderLeadTime = reminderLeadTime == null ? ReminderLeadTime.DISABLED : reminderLeadTime;
        position = Math.max(0, position);
    }
}
