// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/ReminderNotificationRepository.java
// # 📌 Amac: Masaustu hatirlatma bildiriminin son basarili gosterim tarihini kullanici config alaninda saklar.
// # 📌 Repository - Java
// Version: 1.1.0
// Aciklama: Gunluk dedup state'ini YAML olarak yonetir ve uygulama instance'lari arasinda atomik bildirim kilidi saglar.
// Bagimli Oldugu Katman: Repository | Config | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ReminderNotificationRepository {
    private static final String LAST_NOTIFIED_DATE_KEY = "last_notified_date";
    private static final String LOCK_FILE_SUFFIX = ".lock";

    private final SimpleYamlRepository yamlRepository;
    private final Path stateFile;
    private final Path lockFile;

    public ReminderNotificationRepository() {
        this(new SimpleYamlRepository(), AppConfig.REMINDER_NOTIFICATION_STATE_FILE);
    }

    public ReminderNotificationRepository(SimpleYamlRepository yamlRepository, Path stateFile) {
        this.yamlRepository = Objects.requireNonNull(yamlRepository);
        this.stateFile = Objects.requireNonNull(stateFile).toAbsolutePath().normalize();
        this.lockFile = this.stateFile.resolveSibling(this.stateFile.getFileName() + LOCK_FILE_SUFFIX);
    }

    public NotificationLock acquireNotificationLock() {
        ensureParentDirectory(lockFile);
        try {
            FileChannel channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            try {
                return new NotificationLock(channel, channel.lock());
            } catch (IOException | RuntimeException exception) {
                channel.close();
                throw exception;
            }
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SETTINGS_WRITE, exception);
        }
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

        ensureParentDirectory(stateFile);
        try {
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

    private void ensureParentDirectory(Path path) {
        try {
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SETTINGS_WRITE, exception);
        }
    }

    public static final class NotificationLock implements AutoCloseable {
        private final FileChannel channel;
        private final FileLock lock;

        private NotificationLock(FileChannel channel, FileLock lock) {
            this.channel = channel;
            this.lock = lock;
        }

        @Override
        public void close() {
            try {
                lock.release();
            } catch (IOException exception) {
                throw new IllegalStateException(Messages.ERROR_SETTINGS_WRITE, exception);
            } finally {
                try {
                    channel.close();
                } catch (IOException exception) {
                    throw new IllegalStateException(Messages.ERROR_SETTINGS_WRITE, exception);
                }
            }
        }
    }
}
