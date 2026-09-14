// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/SQLiteBackupTool.java
// # 📌 Amac: Calisan WAL SQLite veritabanindan tutarli ve dogrulanmis snapshot yedegi uretir.
// # 📌 Tool - Java
// # Version: 1.1.0
// # Aciklama: VACUUM INTO ile gecici snapshot uretir, integrity_check sonrasi atomik olarak final yedek dosyasina tasir.
// # Bagimli Oldugu Katman: Tool | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;

import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public final class SQLiteBackupTool {
    private final SQLiteConnectionProvider connectionProvider;

    public SQLiteBackupTool(SQLiteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Path sourceDatabaseFile() {
        return connectionProvider.databaseFile();
    }

    public void createVerifiedSnapshot(Path target) {
        Path normalized = target.toAbsolutePath().normalize();
        Path temporary = normalized.resolveSibling(normalized.getFileName() + ".partial-" + UUID.randomUUID());
        try {
            Path parent = normalized.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.deleteIfExists(temporary);
            try (Connection connection = connectionProvider.open(); Statement statement = connection.createStatement()) {
                statement.execute("VACUUM INTO '" + escapeSqlLiteral(temporary.toString()) + "'");
            }
            verifyIntegrity(temporary);
            moveVerifiedSnapshot(temporary, normalized);
        } catch (Exception exception) {
            try { Files.deleteIfExists(temporary); } catch (Exception ignored) { }
            if (exception instanceof IllegalStateException state) throw state;
            throw new IllegalStateException(Messages.ERROR_BACKUP_CREATE, exception);
        }
    }

    public void verifyIntegrity(Path databaseFile) {
        Path normalized = databaseFile.toAbsolutePath().normalize();
        try {
            if (!Files.isRegularFile(normalized) || Files.size(normalized) == 0L) {
                throw new IllegalStateException(Messages.ERROR_BACKUP_INTEGRITY);
            }
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(Messages.ERROR_BACKUP_INTEGRITY, exception);
        }
        String jdbcUrl = AppConfig.SQLITE_JDBC_PREFIX + normalized;
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA integrity_check")) {
            if (!resultSet.next() || !"ok".equalsIgnoreCase(resultSet.getString(1))) {
                throw new IllegalStateException(Messages.ERROR_BACKUP_INTEGRITY);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(Messages.ERROR_BACKUP_INTEGRITY, exception);
        }
    }

    private void moveVerifiedSnapshot(Path source, Path target) throws java.io.IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target);
        }
    }

    private String escapeSqlLiteral(String value) {
        return value.replace("'", "''");
    }
}
