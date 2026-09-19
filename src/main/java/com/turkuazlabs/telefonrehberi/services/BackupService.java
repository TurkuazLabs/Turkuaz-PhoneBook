// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/BackupService.java
// # 📌 Amac: Manuel ve haftalik otomatik SQLite yedekleme is kurallarini yonetir.
// # 📌 Service - Java
// # Version: 1.1.0
// # Aciklama: 7 gunluk otomatik yedek araligi, en fazla 5 kopya ve manuel/es zamanli retention temizligini uygular.
// # Bagimli Oldugu Katman: Service | Repository | Model
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.models.AppSettings;
import com.turkuazlabs.telefonrehberi.repositories.BackupRepository;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public final class BackupService {
    private static final Duration AUTO_BACKUP_INTERVAL = Duration.ofDays(7);
    private static final int MAX_BACKUP_RETENTION = 5;

    private final BackupRepository repository;

    public BackupService(BackupRepository repository) {
        this.repository = repository;
    }

    public Path backupNow(int retention) {
        Path backup = repository.createBackup();
        repository.prune(normalizeRetention(retention));
        return backup;
    }

    public Optional<Path> autoBackupIfNeeded(AppSettings settings) {
        int retention = normalizeRetention(settings.backupRetention());
        repository.prune(retention);
        if (!settings.autoBackup() || repository.hasBackupWithin(AUTO_BACKUP_INTERVAL)) {
            return Optional.empty();
        }
        return Optional.of(backupNow(retention));
    }

    public int cleanupExcessBackups(int retention) {
        return repository.prune(normalizeRetention(retention));
    }

    private int normalizeRetention(int retention) {
        return Math.max(1, Math.min(MAX_BACKUP_RETENTION, retention));
    }

    public List<Path> listBackups() {
        return repository.listBackups();
    }
}
