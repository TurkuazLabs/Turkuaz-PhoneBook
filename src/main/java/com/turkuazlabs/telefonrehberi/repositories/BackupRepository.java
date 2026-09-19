// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/BackupRepository.java
// # 📌 Amac: SQLite rehber yedeklerini olusturur, listeler ve retention uygular.
// # 📌 Repository - Java
// # Version: 2.1.0
// # Aciklama: WAL-guvenli snapshotlari saklar, son yedek yasini kontrol eder ve en yeni N kopyayi koruyarak eski yedekleri temizler.
// # Bagimli Oldugu Katman: Repository | Tool | Config | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.tools.SQLiteBackupTool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class BackupRepository {
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
    private static final String PREFIX = "telefon-rehberi-";
    private static final String EXTENSION = ".db";
    private final Path backupDirectory;
    private final SQLiteBackupTool backupTool;

    public BackupRepository(SQLiteBackupTool backupTool) {
        this(AppConfig.BACKUP_PATH, backupTool);
    }

    public BackupRepository(Path backupDirectory, SQLiteBackupTool backupTool) {
        this.backupDirectory = backupDirectory.toAbsolutePath().normalize();
        this.backupTool = backupTool;
    }

    public Path createBackup() {
        if (Files.notExists(backupTool.sourceDatabaseFile())) {
            throw new IllegalStateException(Messages.ERROR_BACKUP_DATABASE_MISSING);
        }
        try {
            Files.createDirectories(backupDirectory);
            Path target = backupDirectory.resolve(PREFIX + LocalDateTime.now().format(FILE_TIME) + EXTENSION);
            backupTool.createVerifiedSnapshot(target);
            return target;
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_BACKUP_CREATE, exception);
        }
    }

    public boolean hasBackupWithin(Duration interval) {
        if (interval == null || interval.isNegative() || interval.isZero()) {
            throw new IllegalArgumentException("Backup interval must be positive.");
        }
        List<Path> backups = listBackups();
        if (backups.isEmpty()) return false;
        try {
            Instant newestBackup = Files.getLastModifiedTime(backups.get(0)).toInstant();
            return newestBackup.isAfter(Instant.now().minus(interval));
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_BACKUP_LIST, exception);
        }
    }

    public List<Path> listBackups() {
        if (Files.notExists(backupDirectory)) return List.of();
        try (Stream<Path> stream = Files.list(backupDirectory)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(PREFIX))
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .sorted(Comparator.comparing(Path::getFileName).reversed())
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_BACKUP_LIST, exception);
        }
    }

    public int prune(int keepCount) {
        int safeKeepCount = Math.max(1, keepCount);
        List<Path> backups = listBackups();
        int deleted = 0;
        for (int index = safeKeepCount; index < backups.size(); index++) {
            try {
                if (Files.deleteIfExists(backups.get(index))) deleted++;
            } catch (IOException exception) {
                throw new IllegalStateException(Messages.ERROR_BACKUP_PRUNE, exception);
            }
        }
        return deleted;
    }
}
