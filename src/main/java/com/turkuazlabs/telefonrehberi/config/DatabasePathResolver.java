// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/DatabasePathResolver.java
// # 📌 Amac: SQLite ana veritabani dosyasini platforma uygun kalici klasore yerlestirir.
// # 📌 Config - Java
// # Version: 1.0.0
// # Aciklama: Windows'ta DB'yi kullanicinin Contacts/Turkuaz Telefon Rehberi/Veri klasorune, diger platformlarda teknik veri kokune yerlestirir.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public final class DatabasePathResolver {
    private static final String USER_DATABASE_DIRECTORY = "Veri";

    private DatabasePathResolver() {
    }

    public static Path resolve(Path userFilesRoot, Path technicalDataRoot, String configuredPath) {
        Path path = Paths.get(configuredPath);
        if (path.isAbsolute()) {
            return path.normalize();
        }

        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            Path fileName = path.getFileName();
            if (fileName == null) {
                throw new IllegalArgumentException("Database dosya adi bos olamaz.");
            }
            return userFilesRoot.resolve(USER_DATABASE_DIRECTORY).resolve(fileName).normalize();
        }

        return technicalDataRoot.resolve(path).normalize();
    }
}
