// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/RuntimeConfigDefaults.java
// # 📌 Amac: Eski portable config dosyalari icin geriye uyumlu runtime varsayilanlarini merkezi tutar.
// # 📌 Config - Java
// # Version: 1.1.0
// # Aciklama: Yeni config anahtarlari eski app.yml dosyasinda yoksa guvenli history ve timeline limitlerini saglar.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.config;

public final class RuntimeConfigDefaults {
    public static final int BULK_HISTORY_LIMIT = 20;
    public static final int HISTORY_LIMIT = 250;
    public static final int CONTACT_ACTIVITY_LIMIT = 12;
    public static final int HISTORY_RETENTION_LIMIT = 5000;
    public static final int MOBILE_SYNC_MAX_REQUEST_BYTES = 2 * 1024 * 1024;

    private RuntimeConfigDefaults() {
    }
}
