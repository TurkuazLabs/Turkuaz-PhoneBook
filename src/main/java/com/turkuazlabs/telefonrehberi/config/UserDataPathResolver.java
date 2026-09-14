// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/UserDataPathResolver.java
// # 📌 Amac: Windows, Linux ve macOS icin teknik kullanici veri, config ve cache koklerini merkezi olarak belirler.
// # 📌 Config - Java
// # Version: 1.1.0
// # Aciklama: APPDATA, LOCALAPPDATA ve XDG data/config/cache standartlarini uygulama klasorunden bagimsiz cozer.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public final class UserDataPathResolver {
    private static final String WINDOWS_VENDOR_DIRECTORY = "TurkuazLabs";
    private static final String WINDOWS_APP_DIRECTORY = "TelefonRehberi";
    private static final String WINDOWS_CACHE_DIRECTORY = "TelefonRehberiCache";
    private static final String UNIX_VENDOR_DIRECTORY = "turkuazlabs";
    private static final String UNIX_APP_DIRECTORY = "telefon-rehberi";

    private UserDataPathResolver() {
    }

    public static Path resolve() {
        return resolveDataRoot();
    }

    public static Path resolveDataRoot() {
        String osName = osName();
        Path userHome = userHome();

        if (osName.contains("win")) {
            Path base = environmentPath("APPDATA", userHome.resolve("AppData").resolve("Roaming"));
            return windowsAppRoot(base);
        }
        if (osName.contains("mac")) {
            return userHome.resolve("Library")
                    .resolve("Application Support")
                    .resolve(WINDOWS_VENDOR_DIRECTORY)
                    .resolve(WINDOWS_APP_DIRECTORY)
                    .toAbsolutePath()
                    .normalize();
        }

        Path base = environmentPath("XDG_DATA_HOME", userHome.resolve(".local").resolve("share"));
        return unixAppRoot(base);
    }

    public static Path resolveConfigRoot() {
        String osName = osName();
        Path userHome = userHome();

        if (osName.contains("win")) {
            Path base = environmentPath("APPDATA", userHome.resolve("AppData").resolve("Roaming"));
            return windowsAppRoot(base).resolve("config").normalize();
        }
        if (osName.contains("mac")) {
            return resolveDataRoot().resolve("config").normalize();
        }

        Path base = environmentPath("XDG_CONFIG_HOME", userHome.resolve(".config"));
        return unixAppRoot(base);
    }

    public static Path resolveCacheRoot() {
        String osName = osName();
        Path userHome = userHome();

        if (osName.contains("win")) {
            Path base = environmentPath("LOCALAPPDATA", userHome.resolve("AppData").resolve("Local"));
            return base.resolve(WINDOWS_VENDOR_DIRECTORY).resolve(WINDOWS_CACHE_DIRECTORY).toAbsolutePath().normalize();
        }
        if (osName.contains("mac")) {
            return userHome.resolve("Library")
                    .resolve("Caches")
                    .resolve(WINDOWS_VENDOR_DIRECTORY)
                    .resolve(WINDOWS_APP_DIRECTORY)
                    .toAbsolutePath()
                    .normalize();
        }

        Path base = environmentPath("XDG_CACHE_HOME", userHome.resolve(".cache"));
        return unixAppRoot(base);
    }

    private static Path windowsAppRoot(Path base) {
        return base.resolve(WINDOWS_VENDOR_DIRECTORY).resolve(WINDOWS_APP_DIRECTORY).toAbsolutePath().normalize();
    }

    private static Path unixAppRoot(Path base) {
        return base.resolve(UNIX_VENDOR_DIRECTORY).resolve(UNIX_APP_DIRECTORY).toAbsolutePath().normalize();
    }

    private static Path environmentPath(String key, Path fallback) {
        String value = environmentValue(key);
        return value == null ? fallback : Paths.get(value);
    }

    private static String environmentValue(String key) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String osName() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    }

    private static Path userHome() {
        return Paths.get(System.getProperty("user.home", ".")).toAbsolutePath().normalize();
    }
}
