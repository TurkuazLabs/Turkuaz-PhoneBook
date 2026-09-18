// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/LanguagePreferenceTool.java
// # 📌 Amac: Uygulama bootstrap asamasinda AppConfig yuklenmeden once kullanici dil tercihini dosyadan okur.
// # 📌 Tool - Java
// Version: 1.0.0
// Aciklama: Yeni preferences.yml yoksa legacy preference dosyasina bakar; hata veya eksik degerde sistem dili fallbackini kullanir.
// Bagimli Oldugu Katman: Tool | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.language.LocaleText;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LanguagePreferenceTool {
    public String readLanguageCode(Path preferencesFile, Path legacyPreferencesFile) {
        if (preferencesFile != null && Files.exists(preferencesFile)) {
            return readFrom(preferencesFile);
        }
        return readFrom(legacyPreferencesFile);
    }

    private String readFrom(Path path) {
        if (path == null || Files.notExists(path)) return LocaleText.LANGUAGE_SYSTEM_CODE;
        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isBlank() || trimmed.startsWith("#")) continue;
                int separator = trimmed.indexOf(':');
                if (separator < 1) continue;
                String key = trimmed.substring(0, separator).trim();
                if (!LocaleText.LANGUAGE_PREFERENCE_KEY.equals(key)) continue;
                return LocaleText.normalizeLanguageCode(clean(trimmed.substring(separator + 1)));
            }
        } catch (IOException ignored) {
            return LocaleText.LANGUAGE_SYSTEM_CODE;
        }
        return LocaleText.LANGUAGE_SYSTEM_CODE;
    }

    private String clean(String value) {
        String cleaned = value == null ? "" : value.trim();
        if (cleaned.length() >= 2 && ((cleaned.startsWith("\"") && cleaned.endsWith("\""))
                || (cleaned.startsWith("'") && cleaned.endsWith("'")))) {
            return cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned;
    }
}
