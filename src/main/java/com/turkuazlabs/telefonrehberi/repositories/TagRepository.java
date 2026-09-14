// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/TagRepository.java
// # 📌 Amac: Renkli etiketleri ve kisi-etiket baglantilarini SQLite uzerinden yonetir.
// # 📌 Repository - Java
// # Version: 2.29.0
// # Aciklama: Etiket CRUD, tekli/toplu kisi baglama-cikarma ve merge transfer islemlerini gerceklestirir.
// # Bagimli Oldugu Katman: Repository | Model | Tool | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.TagRecord;
import com.turkuazlabs.telefonrehberi.tools.SQLiteConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public final class TagRepository {
    private final SQLiteConnectionProvider connectionProvider;

    public TagRepository(SQLiteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        initialize();
    }

    public List<TagRecord> findAll() {
        String sql = """
                SELECT t.id, t.name, t.color, COUNT(ct.contact_id) AS contact_count
                FROM contact_tags_def t LEFT JOIN contact_tags ct ON ct.tag_id = t.id
                GROUP BY t.id, t.name, t.color ORDER BY t.name COLLATE NOCASE
                """;
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement(sql); ResultSet r = s.executeQuery()) {
            List<TagRecord> out = new ArrayList<>();
            while (r.next()) out.add(new TagRecord(r.getLong("id"), r.getString("name"), r.getString("color"), r.getInt("contact_count")));
            return out;
        } catch (SQLException e) { throw failure(e); }
    }


    public Map<Long, List<TagRecord>> findMembershipsByContact() {
        String sql = "SELECT d.id, d.name, d.color, l.contact_id FROM contact_tags l JOIN contact_tags_def d ON d.id = l.tag_id ORDER BY d.name COLLATE NOCASE";
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement(sql); ResultSet r = s.executeQuery()) {
            Map<Long, List<TagRecord>> out = new LinkedHashMap<>();
            while (r.next()) {
                long contactId = r.getLong("contact_id");
                out.computeIfAbsent(contactId, ignored -> new ArrayList<>()).add(new TagRecord(
                        r.getLong("id"), r.getString("name"), r.getString("color"), 0
                ));
            }
            out.replaceAll((id, values) -> List.copyOf(values));
            return Map.copyOf(out);
        } catch (SQLException e) { throw failure(e); }
    }

    public long insert(String name, String color) {
        String sql = "INSERT INTO contact_tags_def(name, color) VALUES(?, ?)";
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, name); s.setString(2, color); s.executeUpdate();
            try (ResultSet k = s.getGeneratedKeys()) { return k.next() ? k.getLong(1) : 0L; }
        } catch (SQLException e) { throw failure(e); }
    }

    public boolean delete(long id) {
        try (Connection c = connectionProvider.open()) {
            c.setAutoCommit(false);
            try (PreparedStatement links = c.prepareStatement("DELETE FROM contact_tags WHERE tag_id = ?");
                 PreparedStatement tag = c.prepareStatement("DELETE FROM contact_tags_def WHERE id = ?")) {
                links.setLong(1, id); links.executeUpdate();
                tag.setLong(1, id); boolean deleted = tag.executeUpdate() > 0;
                c.commit(); return deleted;
            } catch (SQLException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw failure(e); }
    }
    public void addContact(long tagId, long contactId) { link("INSERT OR IGNORE INTO contact_tags(tag_id, contact_id) VALUES(?, ?)", tagId, contactId); }
    public int addContacts(long tagId, List<Long> contactIds) {
        String sql = "INSERT OR IGNORE INTO contact_tags(tag_id, contact_id) VALUES(?, ?)";
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement(sql)) {
            c.setAutoCommit(false);
            int added = 0;
            for (Long contactId : contactIds) {
                if (contactId == null || contactId <= 0L) continue;
                s.setLong(1, tagId);
                s.setLong(2, contactId);
                added += s.executeUpdate();
            }
            c.commit();
            return added;
        } catch (SQLException e) { throw failure(e); }
    }
    public void removeContact(long tagId, long contactId) { link("DELETE FROM contact_tags WHERE tag_id = ? AND contact_id = ?", tagId, contactId); }
    public int removeContacts(long tagId, List<Long> contactIds) {
        String sql = "DELETE FROM contact_tags WHERE tag_id = ? AND contact_id = ?";
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement(sql)) {
            c.setAutoCommit(false);
            int removed = 0;
            for (Long contactId : contactIds) {
                if (contactId == null || contactId <= 0L) continue;
                s.setLong(1, tagId);
                s.setLong(2, contactId);
                removed += s.executeUpdate();
            }
            c.commit();
            return removed;
        } catch (SQLException e) { throw failure(e); }
    }
    public void removeAllMemberships(long contactId) { update("DELETE FROM contact_tags WHERE contact_id = ?", contactId); }
    public void transferMemberships(long sourceContactId, long targetContactId) {
        String sql = "INSERT OR IGNORE INTO contact_tags(tag_id, contact_id) SELECT tag_id, ? FROM contact_tags WHERE contact_id = ?";
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, targetContactId); s.setLong(2, sourceContactId); s.executeUpdate();
        } catch (SQLException e) { throw failure(e); }
    }

    private void initialize() {
        try (Connection c = connectionProvider.open(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS contact_tags_def(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE COLLATE NOCASE, color TEXT NOT NULL DEFAULT 'turkuaz', created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            s.execute("CREATE TABLE IF NOT EXISTS contact_tags(tag_id INTEGER NOT NULL, contact_id INTEGER NOT NULL, PRIMARY KEY(tag_id, contact_id))");
            s.execute("CREATE INDEX IF NOT EXISTS idx_contact_tags_contact ON contact_tags(contact_id)");
        } catch (SQLException e) { throw failure(e); }
    }

    private boolean update(String sql, long id) {
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement(sql)) { s.setLong(1, id); return s.executeUpdate() > 0; }
        catch (SQLException e) { throw failure(e); }
    }
    private void link(String sql, long tagId, long contactId) {
        try (Connection c = connectionProvider.open(); PreparedStatement s = c.prepareStatement(sql)) { s.setLong(1, tagId); s.setLong(2, contactId); s.executeUpdate(); }
        catch (SQLException e) { throw failure(e); }
    }
    private IllegalStateException failure(SQLException e) { return new IllegalStateException(Messages.ERROR_TAG_OPERATION, e); }
}
