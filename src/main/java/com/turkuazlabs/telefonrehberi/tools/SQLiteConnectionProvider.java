// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/SQLiteConnectionProvider.java
// # 📌 Amac: SQLite veritabanina JDBC baglantisi saglayan storage adaptorudur.
// # 📌 Tool - Java
// # Version: 1.1.0
// # Aciklama: Uretim ve test icin enjekte edilebilir DB yolu kullanir; WAL, foreign key ve busy timeout PRAGMA ayarlarini uygular.
// # Bagimli Oldugu Katman: Tool | Config | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class SQLiteConnectionProvider {
    private final Path databaseFile;
    private final int busyTimeoutMilliseconds;

    public SQLiteConnectionProvider() {
        this(AppConfig.DATABASE_FILE, AppConfig.SQLITE_BUSY_TIMEOUT_MILLISECONDS);
    }

    public SQLiteConnectionProvider(Path databaseFile, int busyTimeoutMilliseconds) {
        if (databaseFile == null) throw new IllegalArgumentException("databaseFile bos olamaz.");
        if (busyTimeoutMilliseconds < 1) throw new IllegalArgumentException("busyTimeoutMilliseconds pozitif olmali.");
        this.databaseFile = databaseFile.toAbsolutePath().normalize();
        this.busyTimeoutMilliseconds = busyTimeoutMilliseconds;
        prepareStorage();
        loadDriver();
    }

    public Connection open() {
        try {
            Connection connection = DriverManager.getConnection(AppConfig.SQLITE_JDBC_PREFIX + databaseFile);
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
                statement.execute("PRAGMA journal_mode = WAL");
                statement.execute("PRAGMA busy_timeout = " + busyTimeoutMilliseconds);
            }
            return connection;
        } catch (SQLException exception) {
            throw new IllegalStateException(Messages.ERROR_DATABASE_OPEN, exception);
        }
    }

    public Path databaseFile() {
        return databaseFile;
    }

    private void prepareStorage() {
        try {
            Path parent = databaseFile.getParent();
            if (parent != null) java.nio.file.Files.createDirectories(parent);
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_STORAGE_PREPARE, exception);
        }
    }

    private void loadDriver() {
        try {
            Class.forName(AppConfig.SQLITE_DRIVER_CLASS);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException(Messages.ERROR_SQLITE_DRIVER, exception);
        }
    }
}
