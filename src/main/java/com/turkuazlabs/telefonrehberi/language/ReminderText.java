// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/ReminderText.java
// # 📌 Amac: Hatirlatma enum degerlerini sistem diline uygun merkezi metinlere cevirir.
// # 📌 Language - Java
// Version: 1.1.0
// Aciklama: Onemli tarih, hatirlatma suresi ve iletisim dongusu metinlerini Turkce locale icin Turkce, diger diller icin Ingilizce fallback olarak sunar.
// Bagimli Oldugu Katman: Language | Model
package com.turkuazlabs.telefonrehberi.language;

import com.turkuazlabs.telefonrehberi.models.ImportantDateType;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;

public final class ReminderText {
    private ReminderText() {
    }

    public static String importantDateType(ImportantDateType value) {
        return switch (value == null ? ImportantDateType.CUSTOM : value) {
            case ANNIVERSARY -> LocaleText.text(Messages.IMPORTANT_DATE_TYPE_ANNIVERSARY, "Anniversary");
            case CUSTOM -> LocaleText.text(Messages.IMPORTANT_DATE_TYPE_CUSTOM, "Custom Date");
        };
    }

    public static String leadTime(ReminderLeadTime value) {
        return switch (value == null ? ReminderLeadTime.DISABLED : value) {
            case DISABLED -> LocaleText.text(Messages.REMINDER_DISABLED, "No reminder");
            case SAME_DAY -> LocaleText.text(Messages.REMINDER_SAME_DAY, "Same day");
            case TWO_DAYS -> LocaleText.text(Messages.REMINDER_TWO_DAYS, "2 days before");
            case ONE_WEEK -> LocaleText.text(Messages.REMINDER_ONE_WEEK, "7 days before");
            case TWO_WEEKS -> LocaleText.text(Messages.REMINDER_TWO_WEEKS, "14 days before");
        };
    }

    public static String keepInTouch(KeepInTouchInterval value) {
        return switch (value == null ? KeepInTouchInterval.DISABLED : value) {
            case DISABLED -> LocaleText.text(Messages.KEEP_IN_TOUCH_DISABLED, "No reminder");
            case MONTHLY -> LocaleText.text(Messages.KEEP_IN_TOUCH_MONTHLY, "Every 30 days");
            case QUARTERLY -> LocaleText.text(Messages.KEEP_IN_TOUCH_QUARTERLY, "Every 90 days");
            case HALF_YEAR -> LocaleText.text(Messages.KEEP_IN_TOUCH_HALF_YEAR, "Every 180 days");
            case YEARLY -> LocaleText.text(Messages.KEEP_IN_TOUCH_YEARLY, "Every 365 days");
        };
    }
}
