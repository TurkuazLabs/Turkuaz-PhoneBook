// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/SyncTokenStore.java
// # 📌 Amac: Mobil senkron API erisim anahtarini guvenli rastgele olarak olusturur ve yerelde saklar.
// # 📌 Tool - Java
// # Version: 1.0.1
// # Aciklama: 256-bit token uretir ve OS kullanici veri dizinindeki data/sync-token.txt dosyasinda kalici tutar.
// # Bagimli Oldugu Katman: Tool | Config | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class SyncTokenStore {
    private final SecureRandom secureRandom = new SecureRandom();

    public synchronized String getOrCreate() {
        try {
            java.nio.file.Files.createDirectories(AppConfig.DATA_PATH);
            if (java.nio.file.Files.exists(AppConfig.SYNC_TOKEN_FILE)) {
                String existing = java.nio.file.Files.readString(AppConfig.SYNC_TOKEN_FILE, AppConfig.DATA_CHARSET).trim();
                if (!existing.isBlank()) {
                    return existing;
                }
            }

            byte[] bytes = new byte[AppConfig.SYNC_TOKEN_BYTES];
            secureRandom.nextBytes(bytes);
            String token = HexFormat.of().formatHex(bytes);
            java.nio.file.Files.writeString(AppConfig.SYNC_TOKEN_FILE, token, AppConfig.DATA_CHARSET);
            return token;
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SYNC_TOKEN, exception);
        }
    }
}
