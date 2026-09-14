// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/UserFilesPathResolver.java
// # 📌 Amac: Kullaniciya gorunen export ve yedek dosyalari icin platforma uygun rehber klasorunu belirler.
// # 📌 Config - Java
// # Version: 1.0.0
// # Aciklama: Windows Contacts, Linux Contacts/XDG Documents ve macOS Documents altinda Turkuaz Telefon Rehberi kokunu cozer.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public final class UserFilesPathResolver {
    private static final String APP_DIRECTORY = "Turkuaz Telefon Rehberi";
    private static final String WINDOWS_CONTACTS_DIRECTORY = "Contacts";
    private static final String LINUX_CONTACTS_DIRECTORY = "Contacts";
    private static final String DEFAULT_DOCUMENTS_DIRECTORY = "Documents";
    private static final String XDG_DOCUMENTS_KEY = "XDG_DOCUMENTS_DIR";
    private static final String USER_DIRS_FILE = "user-dirs.dirs";

    private UserFilesPathResolver() {
    }

    public static Path resolve() {
        String override = environmentValue("TURKUAZ_CONTACTS_HOME");
        if (override != null) {
            return Paths.get(expandHome(override)).toAbsolutePath().normalize().resolve(APP_DIRECTORY);
        }

        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        Path userHome = Paths.get(System.getProperty("user.home", ".")).toAbsolutePath().normalize();

        if (osName.contains("win")) {
            String userProfile = environmentValue("USERPROFILE");
            Path profile = userProfile == null ? userHome : Paths.get(userProfile).toAbsolutePath().normalize();
            return profile.resolve(WINDOWS_CONTACTS_DIRECTORY).resolve(APP_DIRECTORY).normalize();
        }

        if (osName.contains("mac")) {
            return userHome.resolve(DEFAULT_DOCUMENTS_DIRECTORY).resolve(APP_DIRECTORY).normalize();
        }

        Path contacts = userHome.resolve(LINUX_CONTACTS_DIRECTORY);
        if (Files.isDirectory(contacts)) {
            return contacts.resolve(APP_DIRECTORY).normalize();
        }
        return resolveLinuxDocuments(userHome).resolve(APP_DIRECTORY).normalize();
    }

    private static Path resolveLinuxDocuments(Path userHome) {
        Path configHome = environmentPath("XDG_CONFIG_HOME", userHome.resolve(".config"));
        Path userDirs = configHome.resolve(USER_DIRS_FILE);
        if (Files.isRegularFile(userDirs)) {
            try {
                List<String> lines = Files.readAllLines(userDirs);
                for (String line : lines) {
                    Path resolved = parseDocumentsLine(line, userHome);
                    if (resolved != null) {
                        return resolved;
                    }
                }
            } catch (IOException ignored) {
                // Fallback asagida uygulanir.
            }
        }
        return userHome.resolve(DEFAULT_DOCUMENTS_DIRECTORY).normalize();
    }

    private static Path parseDocumentsLine(String line, Path userHome) {
        String trimmed = line == null ? "" : line.trim();
        if (!trimmed.startsWith(XDG_DOCUMENTS_KEY + "=")) {
            return null;
        }
        String value = trimmed.substring(trimmed.indexOf('=') + 1).trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        value = value.replace("${HOME}", userHome.toString()).replace("$HOME", userHome.toString());
        if (value.isBlank()) {
            return null;
        }
        Path path = Paths.get(value);
        return path.isAbsolute() ? path.normalize() : userHome.resolve(path).normalize();
    }

    private static Path environmentPath(String key, Path fallback) {
        String value = environmentValue(key);
        return value == null ? fallback : Paths.get(expandHome(value)).toAbsolutePath().normalize();
    }

    private static String expandHome(String value) {
        if (value == null) {
            return null;
        }
        String home = System.getProperty("user.home", ".");
        if (value.equals("~")) {
            return home;
        }
        if (value.startsWith("~/") || value.startsWith("~\\")) {
            return home + value.substring(1);
        }
        return value;
    }

    private static String environmentValue(String key) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? null : value.trim();
    }
}
