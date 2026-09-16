// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/SyncTokenStore.java
// # 📌 Amac: Mobil senkron API erisim anahtarini guvenli rastgele olarak olusturur ve yerelde saklar.
// # 📌 Tool - Java
// Version: 1.1.0
// # Aciklama: 256-bit token uretir; POSIX sistemlerde mevcut/yeni token dosyasini yalniz dosya sahibi okuyup yazabilecek sekilde 0600 izinleriyle korur.
// # Bagimli Oldugu Katman: Tool | Config | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.HexFormat;
import java.util.Set;

public final class SyncTokenStore {
    private static final Set<PosixFilePermission> OWNER_ONLY = EnumSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE
    );

    private final SecureRandom secureRandom = new SecureRandom();

    public synchronized String getOrCreate() {
        try {
            Files.createDirectories(AppConfig.DATA_PATH);
            if (Files.exists(AppConfig.SYNC_TOKEN_FILE)) {
                securePermissions(AppConfig.SYNC_TOKEN_FILE);
                String existing = Files.readString(AppConfig.SYNC_TOKEN_FILE, AppConfig.DATA_CHARSET).trim();
                if (!existing.isBlank()) {
                    return existing;
                }
            }

            byte[] bytes = new byte[AppConfig.SYNC_TOKEN_BYTES];
            secureRandom.nextBytes(bytes);
            String token = HexFormat.of().formatHex(bytes);
            Files.writeString(AppConfig.SYNC_TOKEN_FILE, token, AppConfig.DATA_CHARSET);
            securePermissions(AppConfig.SYNC_TOKEN_FILE);
            return token;
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SYNC_TOKEN, exception);
        }
    }

    private void securePermissions(Path path) throws IOException {
        PosixFileAttributeView posix = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        if (posix != null) {
            Files.setPosixFilePermissions(path, OWNER_ONLY);
        }
    }
}
