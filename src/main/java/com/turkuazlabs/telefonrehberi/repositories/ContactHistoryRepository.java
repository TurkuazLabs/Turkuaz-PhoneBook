// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/ContactHistoryRepository.java
// # 📌 Amac: Kisi degisiklik snapshotlarini SQLite icinde saklar, listeler ve retention uygular.
// # 📌 Repository - Java
// # Version: 2.37.0
// # Aciklama: Gecmis kaydi ekleme, timeline sorgusu ve merkezi retention limitiyle eski snapshotlari prune eder.
// # Bagimli Oldugu Katman: Repository | Model | Tool | Language | Config
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.HistoryEntry;
import com.turkuazlabs.telefonrehberi.tools.SQLiteConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ContactHistoryRepository {
    private static final String CREATE_SQL = """
            CREATE TABLE IF NOT EXISTS contact_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                contact_id INTEGER NOT NULL,
                action TEXT NOT NULL,
                contact_name TEXT NOT NULL,
                snapshot TEXT NOT NULL,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;
    private final SQLiteConnectionProvider connectionProvider;
    private final int retentionLimit;

    public ContactHistoryRepository(SQLiteConnectionProvider connectionProvider) {
        this(connectionProvider, AppConfig.HISTORY_RETENTION_LIMIT);
    }

    public ContactHistoryRepository(SQLiteConnectionProvider connectionProvider, int retentionLimit) {
        this.connectionProvider = connectionProvider;
        this.retentionLimit = Math.max(1, retentionLimit);
        try (Connection connection = connectionProvider.open(); Statement statement = connection.createStatement()) {
            statement.execute(CREATE_SQL);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_contact_history_contact ON contact_history(contact_id, id DESC)");
            prune(connection);
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public synchronized void add(long contactId, String action, String contactName, String snapshot) {
        String sql = "INSERT INTO contact_history(contact_id, action, contact_name, snapshot) VALUES(?, ?, ?, ?)";
        try (Connection connection = connectionProvider.open()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, contactId);
                statement.setString(2, action);
                statement.setString(3, contactName);
                statement.setString(4, snapshot);
                statement.executeUpdate();
                prune(connection);
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public List<HistoryEntry> findRecent(int limit) {
        String sql = "SELECT id, contact_id, action, contact_name, snapshot, created_at FROM contact_history ORDER BY id DESC LIMIT ?";
        try (Connection connection = connectionProvider.open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<HistoryEntry> result = new ArrayList<>();
                while (resultSet.next()) result.add(map(resultSet));
                return result;
            }
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public List<HistoryEntry> findByContactId(long contactId) {
        String sql = "SELECT id, contact_id, action, contact_name, snapshot, created_at FROM contact_history WHERE contact_id = ? ORDER BY id DESC";
        try (Connection connection = connectionProvider.open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contactId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<HistoryEntry> result = new ArrayList<>();
                while (resultSet.next()) result.add(map(resultSet));
                return result;
            }
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public Optional<HistoryEntry> findById(long id) {
        String sql = "SELECT id, contact_id, action, contact_name, snapshot, created_at FROM contact_history WHERE id = ?";
        try (Connection connection = connectionProvider.open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    private void prune(Connection connection) throws SQLException {
        String sql = "DELETE FROM contact_history WHERE id NOT IN (SELECT id FROM contact_history ORDER BY id DESC LIMIT ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, retentionLimit);
            statement.executeUpdate();
        }
    }

    private HistoryEntry map(ResultSet resultSet) throws SQLException {
        return new HistoryEntry(
                resultSet.getLong("id"), resultSet.getLong("contact_id"), resultSet.getString("action"),
                resultSet.getString("contact_name"), resultSet.getString("snapshot"), resultSet.getString("created_at")
        );
    }

    private IllegalStateException failure(SQLException exception) {
        return new IllegalStateException(Messages.ERROR_HISTORY_OPERATION, exception);
    }
}
