// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ThemeMode.java
// # 📌 Amac: Masaustu arayuzunun destekledigi tema modlarini tipli olarak tanimlar.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: FlatLaf acik ve koyu tema modlari icin kalici deger modelidir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

import java.util.Locale;

public enum ThemeMode {
    LIGHT("light"),
    DARK("dark");

    private final String persistedValue;

    ThemeMode(String persistedValue) {
        this.persistedValue = persistedValue;
    }

    public String persistedValue() {
        return persistedValue;
    }

    public static ThemeMode fromPersistedValue(String value) {
        if (value == null) {
            return LIGHT;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (ThemeMode mode : values()) {
            if (mode.persistedValue.equals(normalized)) {
                return mode;
            }
        }
        return LIGHT;
    }
}
