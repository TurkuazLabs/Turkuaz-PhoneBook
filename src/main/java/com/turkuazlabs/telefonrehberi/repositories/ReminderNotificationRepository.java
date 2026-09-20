// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/ReminderNotificationRepository.java
// # 📌 Amac: Masaustu hatirlatma bildiriminin son basarili gosterim tarihini kullanici config alaninda saklar.
// # 📌 Repository - Java
// Version: 1.0.0
// Aciklama: Ayni takvim gununde tekrarlanan OS bildirimlerini engellemek icin gunluk dedup state'ini YAML olarak yonetir.
// Bagimli Oldugu Katman: Repository | Config | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ReminderNotificationRepository {
    private static final String LAST_NOTIFIED_DATE_KEY = "last_notified_date";

    private final SimpleYamlRepository yamlRepository;
    private final Path stateFile;

    public ReminderNotificationRepository() {
        this(new SimpleYamlRepository(), AppConfig.REMINDER_NOTIFICATION_STATE_FILE);
    }

    public ReminderNotificationRepository(SimpleYamlRepository yamlRepository, Path stateFile) {
        this.yamlRepository = Objects.requireNonNull(yamlRepository);
        this.stateFile = Objects.requireNonNull(stateFile).toAbsolutePath().normalize();
    }

    public boolean wasNotifiedOn(LocalDate date) {
        return date != null && lastNotifiedDate().map(date::equals).orElse(false);
    }

    public Optional<LocalDate> lastNotifiedDate() {
        String value = yamlRepository.read(stateFile).get(LAST_NOTIFIED_DATE_KEY);
        if (value == null || value.isBlank()) return Optional.empty();

        try {
            return Optional.of(LocalDate.parse(value));
        } catch (DateTimeParseException ignored) {
            return Optional.empty();
        }
    }

    public void markNotified(LocalDate date) {
        Objects.requireNonNull(date);
        ensureStateFile();
        yamlRepository.update(stateFile, Map.of(LAST_NOTIFIED_DATE_KEY, date.toString()));
    }

    private void ensureStateFile() {
        if (Files.exists(stateFile)) return;

        try {
            Path parent = stateFile.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(stateFile, List.of(
                    "# 📄 Dosya Yolu: C:/Users/<kullanici>/AppData/Roaming/TurkuazLabs/TelefonRehberi/config/reminder-notification-state.yml",
                    "# 📌 Amac: Masaustu hatirlatma bildiriminin son basarili gosterim tarihini saklar.",
                    "# 📌 Repository - YAML",
                    "# Version: 1.0.0",
                    "# Aciklama: Ayni gun tekrarlanan hatirlatma bildirimlerini engelleyen yerel runtime state dosyasidir.",
                    "# Bagimli Oldugu Katman: Repository | Service",
                    ""
            ), AppConfig.DATA_CHARSET);
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SETTINGS_WRITE, exception);
        }
    }
}
