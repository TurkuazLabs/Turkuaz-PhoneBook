// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/ReminderText.java
// # 📌 Amac: Hatirlatma enum degerlerini kullaniciya gosterilecek merkezi metinlere cevirir.
// # 📌 Language - Java
// # Version: 1.0.0
// # Aciklama: Onemli tarih, hatirlatma suresi ve iletisim dongusu gorunen metinlerini tek yerde tutar.
// # Bagimli Oldugu Katman: Language | Model
package com.turkuazlabs.telefonrehberi.language;

import com.turkuazlabs.telefonrehberi.models.ImportantDateType;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;

public final class ReminderText {
    private ReminderText() {
    }

    public static String importantDateType(ImportantDateType value) {
        return switch (value == null ? ImportantDateType.CUSTOM : value) {
            case ANNIVERSARY -> Messages.IMPORTANT_DATE_TYPE_ANNIVERSARY;
            case CUSTOM -> Messages.IMPORTANT_DATE_TYPE_CUSTOM;
        };
    }

    public static String leadTime(ReminderLeadTime value) {
        return switch (value == null ? ReminderLeadTime.DISABLED : value) {
            case DISABLED -> Messages.REMINDER_DISABLED;
            case SAME_DAY -> Messages.REMINDER_SAME_DAY;
            case TWO_DAYS -> Messages.REMINDER_TWO_DAYS;
            case ONE_WEEK -> Messages.REMINDER_ONE_WEEK;
            case TWO_WEEKS -> Messages.REMINDER_TWO_WEEKS;
        };
    }

    public static String keepInTouch(KeepInTouchInterval value) {
        return switch (value == null ? KeepInTouchInterval.DISABLED : value) {
            case DISABLED -> Messages.KEEP_IN_TOUCH_DISABLED;
            case MONTHLY -> Messages.KEEP_IN_TOUCH_MONTHLY;
            case QUARTERLY -> Messages.KEEP_IN_TOUCH_QUARTERLY;
            case HALF_YEAR -> Messages.KEEP_IN_TOUCH_HALF_YEAR;
            case YEARLY -> Messages.KEEP_IN_TOUCH_YEARLY;
        };
    }
}
