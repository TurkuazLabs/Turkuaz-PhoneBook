// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/KeepInTouchInterval.java
// # 📌 Amac: Bir kisiyle tekrar iletisime gecme dongusunu tip guvenli olarak temsil eder.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Kapali, aylik, 3 aylik, 6 aylik ve yillik iletisim dongulerini gun cinsinden tanimlar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public enum KeepInTouchInterval {
    DISABLED(0),
    MONTHLY(30),
    QUARTERLY(90),
    HALF_YEAR(180),
    YEARLY(365);

    private final int days;

    KeepInTouchInterval(int days) {
        this.days = days;
    }

    public int days() {
        return days;
    }

    public boolean enabled() {
        return days > 0;
    }

    public static KeepInTouchInterval fromDays(int value) {
        for (KeepInTouchInterval item : values()) {
            if (item.days == value) return item;
        }
        return DISABLED;
    }
}
