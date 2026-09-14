// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/SyncServerInfo.java
// # 📌 Amac: Mobil senkron sunucusunun kullaniciya gosterilecek baglanti bilgisini tasir.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Sunucu durumu, LAN adresi, port ve erisim anahtarini temsil eder.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record SyncServerInfo(
        boolean running,
        String address,
        int port,
        String token
) {
}
