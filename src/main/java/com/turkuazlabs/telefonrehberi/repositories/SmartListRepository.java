// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/SmartListRepository.java
// # 📌 Amac: Akilli liste kural tanimlarini SQLite icinde saklar.
// # 📌 Repository - Java
// # Version: 2.4.0
// # Aciklama: Dinamik liste kurallarinin ekleme, silme ve listeleme islemlerini yapar.
// # Bagimli Oldugu Katman: Repository | Model | Tool | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.SmartList;
import com.turkuazlabs.telefonrehberi.models.SmartListDraft;
import com.turkuazlabs.telefonrehberi.tools.SQLiteConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class SmartListRepository {
    private final SQLiteConnectionProvider connectionProvider;

    public SmartListRepository(SQLiteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        try (Connection c = connectionProvider.open(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS smart_lists(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE COLLATE NOCASE, field TEXT NOT NULL, operator TEXT NOT NULL, value TEXT NOT NULL DEFAULT '', created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException e) { throw failure(e); }
    }

    public List<SmartList> findAll() {
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement("SELECT id, name, field, operator, value FROM smart_lists ORDER BY name COLLATE NOCASE"); ResultSet r = s.executeQuery()) {
            List<SmartList> out = new ArrayList<>();
            while (r.next()) out.add(new SmartList(r.getLong("id"), r.getString("name"), r.getString("field"), r.getString("operator"), r.getString("value")));
            return out;
        } catch (SQLException e) { throw failure(e); }
    }

    public long insert(SmartListDraft draft) {
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement("INSERT INTO smart_lists(name, field, operator, value) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, draft.name()); s.setString(2, draft.field()); s.setString(3, draft.operator()); s.setString(4, draft.value()); s.executeUpdate();
            try (ResultSet k = s.getGeneratedKeys()) { return k.next() ? k.getLong(1) : 0L; }
        } catch (SQLException e) { throw failure(e); }
    }

    public boolean delete(long id) {
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement("DELETE FROM smart_lists WHERE id = ?")) { s.setLong(1, id); return s.executeUpdate() > 0; }
        catch (SQLException e) { throw failure(e); }
    }

    private IllegalStateException failure(SQLException e) { return new IllegalStateException(Messages.ERROR_SMART_LIST_OPERATION, e); }
}
