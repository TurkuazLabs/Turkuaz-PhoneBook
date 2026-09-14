// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ReminderLeadTime.java
// # 📌 Amac: Onemli tarih hatirlatmalarinin kac gun once baslayacagini tip guvenli olarak temsil eder.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Kapali, ayni gun, 2 gun, 7 gun ve 14 gun once seceneklerini merkezi olarak tanimlar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public enum ReminderLeadTime {
    DISABLED(-1),
    SAME_DAY(0),
    TWO_DAYS(2),
    ONE_WEEK(7),
    TWO_WEEKS(14);

    private final int daysBefore;

    ReminderLeadTime(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    public int daysBefore() {
        return daysBefore;
    }

    public static ReminderLeadTime fromDays(int value) {
        for (ReminderLeadTime item : values()) {
            if (item.daysBefore == value) return item;
        }
        return DISABLED;
    }
}
