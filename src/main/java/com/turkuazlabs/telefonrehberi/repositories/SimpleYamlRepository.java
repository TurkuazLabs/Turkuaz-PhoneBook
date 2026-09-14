// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/SimpleYamlRepository.java
// # 📌 Amac: Basit key:value YAML config dosyalarinda belirli anahtarlari okumak ve guncellemek.
// # 📌 Repository - Java
// # Version: 1.0.0
// # Aciklama: app.yml ve launcher.yml icin comment/header satirlarini koruyan yerel config storage adaptorudur.
// # Bagimli Oldugu Katman: Repository | Config | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SimpleYamlRepository {
    public Map<String, String> read(Path path) {
        Map<String, String> values = new LinkedHashMap<>();
        if (Files.notExists(path)) {
            return values;
        }
        try {
            for (String line : Files.readAllLines(path, AppConfig.DATA_CHARSET)) {
                String trimmed = line.trim();
                if (trimmed.isBlank() || trimmed.startsWith("#")) {
                    continue;
                }
                int separator = trimmed.indexOf(':');
                if (separator < 1) {
                    continue;
                }
                String key = trimmed.substring(0, separator).trim();
                String value = clean(trimmed.substring(separator + 1));
                values.put(key, value);
            }
            return values;
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SETTINGS_READ, exception);
        }
    }

    public void update(Path path, Map<String, String> updates) {
        try {
            List<String> source = Files.exists(path)
                    ? Files.readAllLines(path, AppConfig.DATA_CHARSET)
                    : new ArrayList<>();
            List<String> output = new ArrayList<>();
            Map<String, Boolean> written = new LinkedHashMap<>();
            for (String line : source) {
                String trimmed = line.trim();
                int separator = trimmed.indexOf(':');
                if (!trimmed.startsWith("#") && separator > 0) {
                    String key = trimmed.substring(0, separator).trim();
                    if (updates.containsKey(key)) {
                        output.add(key + ": \"" + escape(updates.get(key)) + "\"");
                        written.put(key, Boolean.TRUE);
                        continue;
                    }
                }
                output.add(line);
            }
            for (Map.Entry<String, String> entry : updates.entrySet()) {
                if (!written.containsKey(entry.getKey())) {
                    output.add(entry.getKey() + ": \"" + escape(entry.getValue()) + "\"");
                }
            }
            Files.createDirectories(path.getParent());
            Files.write(path, output, AppConfig.DATA_CHARSET);
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SETTINGS_WRITE, exception);
        }
    }

    private String clean(String value) {
        String cleaned = value.trim();
        if (cleaned.length() >= 2 && ((cleaned.startsWith("\"") && cleaned.endsWith("\""))
                || (cleaned.startsWith("'") && cleaned.endsWith("'")))) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned;
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\"", "");
    }
}
