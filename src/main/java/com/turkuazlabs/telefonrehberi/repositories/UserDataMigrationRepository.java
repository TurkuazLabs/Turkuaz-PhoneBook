// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/UserDataMigrationRepository.java
// # 📌 Amac: Eski portable kullanici verilerini yeni OS kullanici veri dizinine kayipsiz kopyalayan storage islemlerini saglar.
// # 📌 Repository - Java
// # Version: 1.0.1
// # Aciklama: SQLite ana dosyasi ve WAL sidecar dosyalari dahil olmak uzere hedefte bulunmayan verileri atomik gecis ile kopyalar.
// # Bagimli Oldugu Katman: Repository | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.language.Messages;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class UserDataMigrationRepository {
    private static final String SQLITE_WAL_SUFFIX = "-wal";
    private static final String SQLITE_SHM_SUFFIX = "-shm";
    private static final String TEMP_SUFFIX = ".migrating";
    private static final String DATABASE_BACKUP_PREFIX = "telefon-rehberi-";
    private static final String DATABASE_BACKUP_EXTENSION = ".db";

    public void prepareDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException exception) {
            throw migrationFailure(exception);
        }
    }

    public boolean copyDatabaseBundleIfMissing(Path sourceDatabase, Path targetDatabase) {
        if (Files.exists(targetDatabase) || Files.notExists(sourceDatabase)) {
            return false;
        }

        prepareDirectory(targetDatabase.getParent());
        synchronizeSidecar(sourceDatabase, targetDatabase, SQLITE_WAL_SUFFIX);
        synchronizeSidecar(sourceDatabase, targetDatabase, SQLITE_SHM_SUFFIX);
        copyFileAtomically(sourceDatabase, targetDatabase);
        return true;
    }

    public boolean copyFileIfMissing(Path source, Path target) {
        if (Files.exists(target) || Files.notExists(source) || !Files.isRegularFile(source)) {
            return false;
        }
        prepareDirectory(target.getParent());
        copyFileAtomically(source, target);
        return true;
    }


    public int copyDatabaseBackupsIfMissing(Path sourceDirectory, Path targetDirectory) {
        if (Files.notExists(sourceDirectory) || !Files.isDirectory(sourceDirectory)) {
            return 0;
        }
        prepareDirectory(targetDirectory);
        try (Stream<Path> stream = Files.list(sourceDirectory)) {
            List<Path> backups = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(DATABASE_BACKUP_PREFIX))
                    .filter(path -> path.getFileName().toString().endsWith(DATABASE_BACKUP_EXTENSION))
                    .sorted(Comparator.comparing(Path::getFileName))
                    .toList();
            int copied = 0;
            for (Path source : backups) {
                Path target = targetDirectory.resolve(source.getFileName());
                if (Files.notExists(target)) {
                    copyFileAtomically(source, target);
                    copied++;
                }
            }
            return copied;
        } catch (IOException exception) {
            throw migrationFailure(exception);
        }
    }

    public int copyDirectoryContentsIfMissing(Path sourceDirectory, Path targetDirectory) {
        if (Files.notExists(sourceDirectory) || !Files.isDirectory(sourceDirectory)) {
            return 0;
        }
        prepareDirectory(targetDirectory);
        try (Stream<Path> stream = Files.walk(sourceDirectory)) {
            List<Path> entries = stream.sorted(Comparator.naturalOrder()).toList();
            int copied = 0;
            for (Path source : entries) {
                Path relative = sourceDirectory.relativize(source);
                Path target = targetDirectory.resolve(relative);
                if (Files.isDirectory(source)) {
                    Files.createDirectories(target);
                } else if (Files.isRegularFile(source) && Files.notExists(target)) {
                    copyFileAtomically(source, target);
                    copied++;
                }
            }
            return copied;
        } catch (IOException exception) {
            throw migrationFailure(exception);
        }
    }

    public boolean markerExists(Path markerFile) {
        return Files.exists(markerFile);
    }

    public void writeMarker(Path markerFile, String content) {
        try {
            Files.createDirectories(markerFile.getParent());
            Path temporary = markerFile.resolveSibling(markerFile.getFileName() + TEMP_SUFFIX);
            Files.writeString(temporary, content);
            moveTemporary(temporary, markerFile);
        } catch (IOException exception) {
            throw migrationFailure(exception);
        }
    }

    private void synchronizeSidecar(Path sourceDatabase, Path targetDatabase, String suffix) {
        Path source = Path.of(sourceDatabase.toString() + suffix);
        Path target = Path.of(targetDatabase.toString() + suffix);
        try {
            Files.deleteIfExists(target);
        } catch (IOException exception) {
            throw migrationFailure(exception);
        }
        if (Files.exists(source)) {
            copyFileAtomically(source, target);
        }
    }

    private void copyFileAtomically(Path source, Path target) {
        Path temporary = target.resolveSibling(target.getFileName() + TEMP_SUFFIX);
        try {
            Files.deleteIfExists(temporary);
            Files.copy(source, temporary, StandardCopyOption.COPY_ATTRIBUTES);
            moveTemporary(temporary, target);
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException ignored) {
                // Ana migration hatasi korunur.
            }
            throw migrationFailure(exception);
        }
    }

    private void moveTemporary(Path temporary, Path target) throws IOException {
        try {
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporary, target);
        }
    }

    private IllegalStateException migrationFailure(IOException exception) {
        return new IllegalStateException(Messages.ERROR_USER_DATA_MIGRATION, exception);
    }
}
