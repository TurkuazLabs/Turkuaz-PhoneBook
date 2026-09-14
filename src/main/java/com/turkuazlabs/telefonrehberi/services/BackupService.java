// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/BackupService.java
// # 📌 Amac: Manuel ve gunluk otomatik SQLite yedekleme is kurallarini yonetir.
// # 📌 Service - Java
// # Version: 1.0.0
// # Aciklama: Yedek olusturma, gunluk tekrar engelleme ve retention temizligini uygular.
// # Bagimli Oldugu Katman: Service | Repository | Model
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.models.AppSettings;
import com.turkuazlabs.telefonrehberi.repositories.BackupRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class BackupService {
    private final BackupRepository repository;

    public BackupService(BackupRepository repository) {
        this.repository = repository;
    }

    public Path backupNow(int retention) {
        Path backup = repository.createBackup();
        repository.prune(Math.max(1, retention));
        return backup;
    }

    public Optional<Path> autoBackupIfNeeded(AppSettings settings) {
        if (!settings.autoBackup() || repository.hasBackupToday()) {
            return Optional.empty();
        }
        return Optional.of(backupNow(settings.backupRetention()));
    }

    public List<Path> listBackups() {
        return repository.listBackups();
    }
}
