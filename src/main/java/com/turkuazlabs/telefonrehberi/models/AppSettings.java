// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/AppSettings.java
// # 📌 Amac: Kullaniciya acik masaustu, hatirlatma, yedekleme, senkron ve update ayarlarini tek modelde tasir.
// # 📌 Model - Java
// # Version: 1.2.0
// # Aciklama: Ayarlar ekrani ile repository/service katmanlari arasinda masaustu hatirlatma bildirimi dahil immutable tercihleri tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record AppSettings(
        ThemeMode theme,
        String languageCode,
        String startupPage,
        boolean rememberWindow,
        int windowWidth,
        int windowHeight,
        boolean compactMode,
        boolean confirmDelete,
        boolean autoBackup,
        int backupRetention,
        boolean mobileSyncEnabled,
        int mobileSyncPort,
        boolean updateEnabled,
        boolean reminderNotificationsEnabled
) {
}
