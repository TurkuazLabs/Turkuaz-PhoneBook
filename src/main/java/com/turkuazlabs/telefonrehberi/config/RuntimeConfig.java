// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/RuntimeConfig.java
// # 📌 Amac: config/app.yml dosyasini okuyup tipli masaustu calisma ayarlarina donusturur.
// # 📌 Config - Java
// # Version: 1.4.0
// # Aciklama: Harici YAML ayarlarini parse eder; yeni history/timeline limitlerinde eski portable config icin merkezi fallback uygular.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public record RuntimeConfig(
        String databaseFile,
        String legacyDataFile,
        String syncTokenFile,
        int sqliteBusyTimeoutMs,
        boolean mobileSyncEnabled,
        String mobileSyncBindAddress,
        int mobileSyncPort,
        int mobileSyncBacklog,
        int mobileSyncThreads,
        int mobileSyncMaxRequestBytes,
        int syncTokenBytes,
        String defaultPhoneCountryIso,
        int bulkHistoryLimit,
        int historyLimit,
        int contactActivityLimit,
        int historyRetentionLimit
) {
    public static RuntimeConfig load(Path path) {
        Map<String, String> values = readSimpleYaml(path);
        return new RuntimeConfig(
                required(values, "database_file"),
                required(values, "legacy_data_file"),
                required(values, "sync_token_file"),
                positiveInt(values, "sqlite_busy_timeout_ms"),
                booleanValue(values, "mobile_sync_enabled"),
                required(values, "mobile_sync_bind_address"),
                positiveInt(values, "mobile_sync_port"),
                positiveInt(values, "mobile_sync_backlog"),
                positiveInt(values, "mobile_sync_threads"),
                positiveIntOrDefault(values, "mobile_sync_max_request_bytes", RuntimeConfigDefaults.MOBILE_SYNC_MAX_REQUEST_BYTES),
                positiveInt(values, "sync_token_bytes"),
                required(values, "default_phone_country_iso").toUpperCase(java.util.Locale.ROOT),
                positiveIntOrDefault(values, "bulk_history_limit", RuntimeConfigDefaults.BULK_HISTORY_LIMIT),
                positiveIntOrDefault(values, "history_limit", RuntimeConfigDefaults.HISTORY_LIMIT),
                positiveIntOrDefault(values, "contact_activity_limit", RuntimeConfigDefaults.CONTACT_ACTIVITY_LIMIT),
                positiveIntOrDefault(values, "history_retention_limit", RuntimeConfigDefaults.HISTORY_RETENTION_LIMIT)
        );
    }

    private static Map<String, String> readSimpleYaml(Path path) {
        if (Files.notExists(path)) {
            throw new IllegalStateException("Config bulunamadi: " + path.toAbsolutePath());
        }
        try {
            Map<String, String> result = new HashMap<>();
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isBlank() || trimmed.startsWith("#")) {
                    continue;
                }
                int separator = trimmed.indexOf(':');
                if (separator < 1) {
                    continue;
                }
                String key = trimmed.substring(0, separator).trim();
                String value = trimmed.substring(separator + 1).trim();
                if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }
                result.put(key, value);
            }
            return result;
        } catch (IOException exception) {
            throw new IllegalStateException("Config okunamadi: " + path.toAbsolutePath(), exception);
        }
    }

    private static String required(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Eksik config: " + key);
        }
        return value;
    }

    private static int positiveInt(Map<String, String> values, String key) {
        try {
            int value = Integer.parseInt(required(values, key));
            if (value < 1) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Gecersiz pozitif config: " + key, exception);
        }
    }

    private static int positiveIntOrDefault(Map<String, String> values, String key, int defaultValue) {
        String raw = values.get(key);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(raw);
            if (value < 1) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Gecersiz pozitif config: " + key, exception);
        }
    }

    private static boolean booleanValue(Map<String, String> values, String key) {
        String value = required(values, key).toLowerCase(java.util.Locale.ROOT);
        return switch (value) {
            case "true", "1", "yes", "on" -> true;
            case "false", "0", "no", "off" -> false;
            default -> throw new IllegalStateException("Gecersiz boolean config: " + key);
        };
    }
}
