// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/ContactRepository.java
// # 📌 Amac: Contact verisine SQLite uzerinden erisim, schema migration ve mobil kimlik esleme islemlerini yonetir.
// # 📌 Repository - Java
// # Version: 3.2.0
// # Aciklama: CRUD, batch child loading, sync UUID/alias, legacy migration ve unique natural identity fallback ile idempotent mobil upsert saglar.
// # Bagimli Oldugu Katman: Repository | Model | Tool | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.models.ImportantDate;
import com.turkuazlabs.telefonrehberi.models.ImportantDateType;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;
import com.turkuazlabs.telefonrehberi.tools.SQLiteConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ContactRepository {
    private static final int SCHEMA_VERSION = 4;

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS contacts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT NOT NULL DEFAULT '',
                phone_secondary TEXT NOT NULL DEFAULT '',
                phone_work TEXT NOT NULL DEFAULT '',
                email TEXT NOT NULL DEFAULT '',
                email_secondary TEXT NOT NULL DEFAULT '',
                company TEXT NOT NULL DEFAULT '',
                job_title TEXT NOT NULL DEFAULT '',
                birthday TEXT NOT NULL DEFAULT '',
                website TEXT NOT NULL DEFAULT '',
                category TEXT NOT NULL DEFAULT '',
                address TEXT NOT NULL DEFAULT '',
                city TEXT NOT NULL DEFAULT '',
                district TEXT NOT NULL DEFAULT '',
                postal_code TEXT NOT NULL DEFAULT '',
                country TEXT NOT NULL DEFAULT '',
                notes TEXT NOT NULL DEFAULT '',
                favorite INTEGER NOT NULL DEFAULT 0,
                photo_blob BLOB,
                birthday_remind_days_before INTEGER NOT NULL DEFAULT -1,
                keep_in_touch_days INTEGER NOT NULL DEFAULT 0,
                last_contacted_date TEXT NOT NULL DEFAULT '',
                sync_uuid TEXT NOT NULL,
                source_device_id TEXT,
                external_id TEXT,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                deleted_at TEXT
            )
            """;

    private static final String CREATE_METHODS_SQL = """
            CREATE TABLE IF NOT EXISTS contact_methods (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                contact_id INTEGER NOT NULL,
                kind TEXT NOT NULL,
                label TEXT NOT NULL DEFAULT '',
                value TEXT NOT NULL,
                is_primary INTEGER NOT NULL DEFAULT 0,
                position INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(contact_id) REFERENCES contacts(id) ON DELETE CASCADE
            )
            """;

    private static final String CREATE_IMPORTANT_DATES_SQL = """
            CREATE TABLE IF NOT EXISTS contact_important_dates (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                contact_id INTEGER NOT NULL,
                date_type TEXT NOT NULL,
                label TEXT NOT NULL DEFAULT '',
                date_value TEXT NOT NULL,
                remind_days_before INTEGER NOT NULL DEFAULT -1,
                position INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(contact_id) REFERENCES contacts(id) ON DELETE CASCADE
            )
            """;

    private static final String CREATE_SCHEMA_META_SQL = """
            CREATE TABLE IF NOT EXISTS schema_meta (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
            """;

    private static final String CREATE_SYNC_ALIASES_SQL = """
            CREATE TABLE IF NOT EXISTS contact_sync_aliases (
                alias_uuid TEXT PRIMARY KEY,
                contact_id INTEGER NOT NULL,
                FOREIGN KEY(contact_id) REFERENCES contacts(id) ON DELETE CASCADE
            )
            """;

    private static final String CREATE_EXTERNAL_ALIASES_SQL = """
            CREATE TABLE IF NOT EXISTS contact_external_aliases (
                device_id TEXT NOT NULL,
                external_id TEXT NOT NULL,
                contact_id INTEGER NOT NULL,
                PRIMARY KEY(device_id, external_id),
                FOREIGN KEY(contact_id) REFERENCES contacts(id) ON DELETE CASCADE
            )
            """;

    private static final String CONTACT_COLUMNS = """
            id, name, phone, phone_secondary, phone_work, email, email_secondary,
            company, job_title, birthday, website, category, address, city, district,
            postal_code, country, notes, favorite, photo_blob, birthday_remind_days_before,
            keep_in_touch_days, last_contacted_date, sync_uuid
            """;

    private static final String SELECT_ALL_SQL = "SELECT " + CONTACT_COLUMNS
            + " FROM contacts WHERE deleted_at IS NULL ORDER BY favorite DESC, name COLLATE NOCASE, id";
    private static final String SELECT_TRASH_SQL = "SELECT " + CONTACT_COLUMNS
            + " FROM contacts WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC, name COLLATE NOCASE";
    private static final String SELECT_BY_ID_SQL = "SELECT " + CONTACT_COLUMNS + " FROM contacts WHERE id = ?";
    private static final String SELECT_BY_EXTERNAL_SQL = "SELECT " + CONTACT_COLUMNS
            + " FROM contacts WHERE source_device_id = ? AND external_id = ? LIMIT 1";
    private static final String SELECT_BY_SYNC_UUID_SQL = "SELECT " + CONTACT_COLUMNS
            + " FROM contacts WHERE sync_uuid = ? LIMIT 1";

    private static final String INSERT_SQL = """
            INSERT INTO contacts(
                name, phone, phone_secondary, phone_work, email, email_secondary,
                company, job_title, birthday, website, category, address, city, district,
                postal_code, country, notes, favorite, photo_blob, birthday_remind_days_before,
                keep_in_touch_days, last_contacted_date, sync_uuid
            ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE contacts SET
                name = ?, phone = ?, phone_secondary = ?, phone_work = ?, email = ?, email_secondary = ?,
                company = ?, job_title = ?, birthday = ?, website = ?, category = ?, address = ?, city = ?,
                district = ?, postal_code = ?, country = ?, notes = ?, favorite = ?, photo_blob = ?,
                birthday_remind_days_before = ?, keep_in_touch_days = ?, last_contacted_date = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

    private static final String UPDATE_EXTERNAL_BY_ID_SQL = """
            UPDATE contacts SET
                name = ?, phone = ?, phone_secondary = ?, phone_work = ?, email = ?, email_secondary = ?,
                company = ?, job_title = ?, birthday = ?, website = ?,
                category = CASE WHEN ? = '' THEN category ELSE ? END,
                address = ?, city = ?, district = ?, postal_code = ?, country = ?,
                notes = CASE WHEN ? = '' THEN notes ELSE ? END,
                favorite = CASE WHEN ? = 1 THEN 1 ELSE favorite END,
                photo_blob = CASE WHEN ? IS NULL THEN photo_blob ELSE ? END,
                source_device_id = ?, external_id = ?, deleted_at = NULL, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

    private static final String INSERT_EXTERNAL_SQL = """
            INSERT INTO contacts(
                name, phone, phone_secondary, phone_work, email, email_secondary,
                company, job_title, birthday, website, category, address, city, district,
                postal_code, country, notes, favorite, photo_blob, sync_uuid, source_device_id, external_id, deleted_at
            ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)
            """;

    private static final String SOFT_DELETE_SQL = "UPDATE contacts SET deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";
    private static final String RESTORE_SQL = "UPDATE contacts SET deleted_at = NULL, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NOT NULL";
    private static final String HARD_DELETE_SQL = "DELETE FROM contacts WHERE id = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM contacts WHERE deleted_at IS NULL";
    private static final String SELECT_UPDATED_AT_SQL = "SELECT id, updated_at FROM contacts WHERE deleted_at IS NULL";
    private static final String INSERT_LEGACY_SQL = "INSERT OR IGNORE INTO contacts(id, name, phone, email, sync_uuid) VALUES(?, ?, ?, ?, ?)";

    private static final Map<String, String> REQUIRED_COLUMNS = buildRequiredColumns();
    private final SQLiteConnectionProvider connectionProvider;

    public ContactRepository(SQLiteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        initializeSchema();
    }

    public List<Contact> findAll() { return queryContacts(SELECT_ALL_SQL); }
    public List<Contact> findTrash() { return queryContacts(SELECT_TRASH_SQL); }

    public Optional<Contact> findById(long id) {
        try (Connection connection = connectionProvider.open()) {
            return findById(connection, id);
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    public Optional<Contact> findByExternal(String sourceDeviceId, String externalId) {
        if (sourceDeviceId == null || sourceDeviceId.isBlank() || externalId == null || externalId.isBlank()) return Optional.empty();
        try (Connection connection = connectionProvider.open()) {
            Optional<Long> id = findIdByExternal(connection, sourceDeviceId.trim(), externalId.trim());
            return id.isPresent() ? findById(connection, id.get()) : Optional.empty();
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    public Optional<Contact> findBySyncUuid(String syncUuid) {
        String normalized = normalizeSyncUuid(syncUuid);
        if (normalized.isEmpty()) return Optional.empty();
        try (Connection connection = connectionProvider.open()) {
            Optional<Long> id = findIdBySyncUuid(connection, normalized);
            return id.isPresent() ? findById(connection, id.get()) : Optional.empty();
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    public synchronized Contact insert(ContactDraft draft) {
        String syncUuid = UUID.randomUUID().toString();
        try (Connection connection = connectionProvider.open()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                int nextIndex = bindDraft(statement, draft, 1);
                statement.setString(nextIndex, syncUuid);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) throw new IllegalStateException(Messages.ERROR_DATABASE_WRITE);
                    long id = keys.getLong(1);
                    replaceMethods(connection, id, draft);
                    replaceImportantDates(connection, id, draft);
                    connection.commit();
                    return fromDraft(id, draft, syncUuid);
                }
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    public synchronized Contact update(long id, ContactDraft draft) {
        try (Connection connection = connectionProvider.open()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
                int nextIndex = bindDraft(statement, draft, 1);
                statement.setLong(nextIndex, id);
                if (statement.executeUpdate() != 1) throw new IllegalArgumentException(Messages.ERROR_CONTACT_NOT_FOUND);
                replaceMethods(connection, id, draft);
                replaceImportantDates(connection, id, draft);
                connection.commit();
                return findById(connection, id).orElseThrow(() -> new IllegalStateException(Messages.ERROR_DATABASE_WRITE));
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    public synchronized boolean softDelete(long id) { return executeIdUpdate(SOFT_DELETE_SQL, id); }
    public synchronized boolean restore(long id) { return executeIdUpdate(RESTORE_SQL, id); }
    public synchronized boolean hardDelete(long id) { return executeIdUpdate(HARD_DELETE_SQL, id); }

    public long count() {
        try (Connection connection = connectionProvider.open();
             PreparedStatement statement = connection.prepareStatement(COUNT_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getLong(1) : 0L;
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    public Map<Long, String> findUpdatedAtByContact() {
        try (Connection connection = connectionProvider.open();
             PreparedStatement statement = connection.prepareStatement(SELECT_UPDATED_AT_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            Map<Long, String> values = new LinkedHashMap<>();
            while (resultSet.next()) values.put(resultSet.getLong("id"), resultSet.getString("updated_at"));
            return Map.copyOf(values);
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    public synchronized void importLegacy(List<Contact> contacts) {
        if (contacts.isEmpty()) return;
        try (Connection connection = connectionProvider.open();
             PreparedStatement statement = connection.prepareStatement(INSERT_LEGACY_SQL)) {
            connection.setAutoCommit(false);
            for (Contact contact : contacts) {
                statement.setLong(1, contact.id());
                statement.setString(2, contact.name());
                statement.setString(3, contact.phone());
                statement.setString(4, contact.email());
                statement.setString(5, UUID.randomUUID().toString());
                statement.addBatch();
            }
            statement.executeBatch();
            migrateLegacyMethods(connection);
            connection.commit();
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    public synchronized Optional<Contact> findExternalTarget(
            ContactDraft draft,
            String sourceDeviceId,
            String externalId,
            String requestedSyncUuid
    ) {
        String normalizedSyncUuid = normalizeSyncUuid(requestedSyncUuid);
        try (Connection connection = connectionProvider.open()) {
            Optional<Long> contactId = resolveExternalTargetId(
                    connection, draft, sourceDeviceId, externalId, normalizedSyncUuid
            );
            return contactId.isPresent() ? findById(connection, contactId.get()) : Optional.empty();
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    public synchronized Contact upsertExternal(
            ContactDraft draft,
            String sourceDeviceId,
            String externalId,
            String requestedSyncUuid
    ) {
        String normalizedSyncUuid = normalizeSyncUuid(requestedSyncUuid);
        try (Connection connection = connectionProvider.open()) {
            connection.setAutoCommit(false);
            try {
                Long contactId = resolveExternalTargetId(
                        connection, draft, sourceDeviceId, externalId, normalizedSyncUuid
                ).orElse(null);

                if (contactId != null) {
                    clearExternalIdentityConflict(connection, sourceDeviceId, externalId, contactId);
                    registerExternalAlias(connection, sourceDeviceId, externalId, contactId);
                    registerSyncAlias(connection, normalizedSyncUuid, contactId);
                    updateExternalById(connection, contactId, draft, sourceDeviceId, externalId);
                    replaceMethods(connection, contactId, draft);
                    connection.commit();
                    return findById(connection, contactId).orElseThrow(() -> new IllegalStateException(Messages.ERROR_DATABASE_WRITE));
                }

                String syncUuid = normalizedSyncUuid.isEmpty() ? UUID.randomUUID().toString() : normalizedSyncUuid;
                long id = insertExternal(connection, draft, sourceDeviceId, externalId, syncUuid);
                registerExternalAlias(connection, sourceDeviceId, externalId, id);
                replaceMethods(connection, id, draft);
                connection.commit();
                return findById(connection, id).orElseThrow(() -> new IllegalStateException(Messages.ERROR_DATABASE_WRITE));
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    private Optional<Long> resolveExternalTargetId(
            Connection connection, ContactDraft draft, String sourceDeviceId, String externalId, String normalizedSyncUuid
    ) throws SQLException {
        if (!normalizedSyncUuid.isEmpty()) {
            Optional<Long> bySync = findIdBySyncUuid(connection, normalizedSyncUuid);
            if (bySync.isPresent()) return bySync;
        }
        Optional<Long> byExternal = findIdByExternal(connection, sourceDeviceId, externalId);
        if (byExternal.isPresent()) return byExternal;
        return findUniqueNaturalIdentity(connection, draft);
    }

    private long insertExternal(Connection connection, ContactDraft draft, String deviceId, String externalId, String syncUuid) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_EXTERNAL_SQL, Statement.RETURN_GENERATED_KEYS)) {
            int index = bindExternalDraft(statement, draft, 1);
            statement.setString(index++, syncUuid);
            statement.setString(index++, deviceId);
            statement.setString(index, externalId);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new IllegalStateException(Messages.ERROR_DATABASE_WRITE);
                return keys.getLong(1);
            }
        }
    }

    private void updateExternalById(Connection connection, long id, ContactDraft draft, String deviceId, String externalId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_EXTERNAL_BY_ID_SQL)) {
            int index = 1;
            statement.setString(index++, draft.name());
            statement.setString(index++, draft.phone());
            statement.setString(index++, draft.phoneSecondary());
            statement.setString(index++, draft.phoneWork());
            statement.setString(index++, draft.email());
            statement.setString(index++, draft.emailSecondary());
            statement.setString(index++, draft.company());
            statement.setString(index++, draft.jobTitle());
            statement.setString(index++, draft.birthday());
            statement.setString(index++, draft.website());
            statement.setString(index++, draft.category());
            statement.setString(index++, draft.category());
            statement.setString(index++, draft.address());
            statement.setString(index++, draft.city());
            statement.setString(index++, draft.district());
            statement.setString(index++, draft.postalCode());
            statement.setString(index++, draft.country());
            statement.setString(index++, draft.notes());
            statement.setString(index++, draft.notes());
            statement.setInt(index++, draft.favorite() ? 1 : 0);
            statement.setBytes(index++, draft.photo());
            statement.setBytes(index++, draft.photo());
            statement.setString(index++, deviceId);
            statement.setString(index++, externalId);
            statement.setLong(index, id);
            if (statement.executeUpdate() != 1) throw new IllegalStateException(Messages.ERROR_DATABASE_WRITE);
        }
    }

    private void clearExternalIdentityConflict(Connection connection, String deviceId, String externalId, long keepId) throws SQLException {
        String sql = "UPDATE contacts SET source_device_id = NULL, external_id = NULL WHERE source_device_id = ? AND external_id = ? AND id <> ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, deviceId);
            statement.setString(2, externalId);
            statement.setLong(3, keepId);
            statement.executeUpdate();
        }
    }

    private Optional<Long> findIdBySyncUuid(Connection connection, String syncUuid) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM contacts WHERE sync_uuid = ? LIMIT 1")) {
            statement.setString(1, syncUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) return Optional.of(resultSet.getLong(1));
            }
        }
        try (PreparedStatement statement = connection.prepareStatement("SELECT contact_id FROM contact_sync_aliases WHERE alias_uuid = ? LIMIT 1")) {
            statement.setString(1, syncUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(resultSet.getLong(1)) : Optional.empty();
            }
        }
    }

    private Optional<Long> findIdByExternal(Connection connection, String deviceId, String externalId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM contacts WHERE source_device_id = ? AND external_id = ? LIMIT 1")) {
            statement.setString(1, deviceId);
            statement.setString(2, externalId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) return Optional.of(resultSet.getLong(1));
            }
        }
        try (PreparedStatement statement = connection.prepareStatement("SELECT contact_id FROM contact_external_aliases WHERE device_id = ? AND external_id = ? LIMIT 1")) {
            statement.setString(1, deviceId);
            statement.setString(2, externalId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(resultSet.getLong(1)) : Optional.empty();
            }
        }
    }

    private Optional<Long> findUniqueNaturalIdentity(Connection connection, ContactDraft draft) throws SQLException {
        java.util.LinkedHashSet<Long> candidates = new java.util.LinkedHashSet<>();
        java.util.LinkedHashSet<String> phones = new java.util.LinkedHashSet<>();
        for (ContactMethod method : draft.phones()) {
            String value = normalizePhoneIdentity(method.value());
            if (!value.isEmpty()) phones.add(value);
        }
        for (String phone : phones) collectNaturalCandidates(connection, ContactMethod.PHONE, phone, candidates);

        java.util.LinkedHashSet<String> emails = new java.util.LinkedHashSet<>();
        for (ContactMethod method : draft.emails()) {
            String value = normalizeEmailIdentity(method.value());
            if (!value.isEmpty()) emails.add(value);
        }
        for (String email : emails) collectNaturalCandidates(connection, ContactMethod.EMAIL, email, candidates);
        return candidates.size() == 1 ? Optional.of(candidates.iterator().next()) : Optional.empty();
    }

    private void collectNaturalCandidates(
            Connection connection, String kind, String normalizedValue, java.util.Set<Long> candidates
    ) throws SQLException {
        String comparator = ContactMethod.PHONE.equals(kind) ? "cm.value = ?" : "LOWER(TRIM(cm.value)) = ?";
        String sql = "SELECT DISTINCT cm.contact_id FROM contact_methods cm "
                + "JOIN contacts c ON c.id = cm.contact_id "
                + "WHERE cm.kind = ? AND " + comparator + " AND c.deleted_at IS NULL LIMIT 2";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, kind);
            statement.setString(2, normalizedValue);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    candidates.add(resultSet.getLong("contact_id"));
                    if (candidates.size() > 1) return;
                }
            }
        }
    }

    private void registerSyncAlias(Connection connection, String aliasUuid, long contactId) throws SQLException {
        if (aliasUuid == null || aliasUuid.isBlank()) return;
        String canonical = "";
        try (PreparedStatement statement = connection.prepareStatement("SELECT sync_uuid FROM contacts WHERE id = ?")) {
            statement.setLong(1, contactId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) canonical = resultSet.getString(1);
            }
        }
        if (aliasUuid.equals(canonical)) return;
        String sql = "INSERT INTO contact_sync_aliases(alias_uuid, contact_id) VALUES(?, ?) "
                + "ON CONFLICT(alias_uuid) DO UPDATE SET contact_id=excluded.contact_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, aliasUuid);
            statement.setLong(2, contactId);
            statement.executeUpdate();
        }
    }

    private String normalizePhoneIdentity(String value) {
        return value == null ? "" : value.trim().replaceAll("[^0-9+]", "");
    }

    private String normalizeEmailIdentity(String value) {
        return value == null ? "" : value.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private void registerExternalAlias(Connection connection, String deviceId, String externalId, long contactId) throws SQLException {
        if (deviceId == null || deviceId.isBlank() || externalId == null || externalId.isBlank()) return;
        String sql = "INSERT INTO contact_external_aliases(device_id, external_id, contact_id) VALUES(?, ?, ?) "
                + "ON CONFLICT(device_id, external_id) DO UPDATE SET contact_id=excluded.contact_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, deviceId.trim());
            statement.setString(2, externalId.trim());
            statement.setLong(3, contactId);
            statement.executeUpdate();
        }
    }

    public synchronized void transferSyncIdentities(long duplicateId, long primaryId) {
        if (duplicateId == primaryId) return;
        try (Connection connection = connectionProvider.open()) {
            connection.setAutoCommit(false);
            try {
                String duplicateUuid = "";
                String deviceId = "";
                String externalId = "";
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT sync_uuid, COALESCE(source_device_id,''), COALESCE(external_id,'') FROM contacts WHERE id = ?")) {
                    statement.setLong(1, duplicateId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            duplicateUuid = resultSet.getString(1);
                            deviceId = resultSet.getString(2);
                            externalId = resultSet.getString(3);
                        }
                    }
                }
                if (duplicateUuid != null && !duplicateUuid.isBlank()) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO contact_sync_aliases(alias_uuid, contact_id) VALUES(?, ?) "
                                    + "ON CONFLICT(alias_uuid) DO UPDATE SET contact_id=excluded.contact_id")) {
                        statement.setString(1, duplicateUuid);
                        statement.setLong(2, primaryId);
                        statement.executeUpdate();
                    }
                    try (PreparedStatement statement = connection.prepareStatement("UPDATE contacts SET sync_uuid = ? WHERE id = ?")) {
                        statement.setString(1, UUID.randomUUID().toString());
                        statement.setLong(2, duplicateId);
                        statement.executeUpdate();
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement("UPDATE contact_sync_aliases SET contact_id = ? WHERE contact_id = ?")) {
                    statement.setLong(1, primaryId);
                    statement.setLong(2, duplicateId);
                    statement.executeUpdate();
                }
                registerExternalAlias(connection, deviceId, externalId, primaryId);
                try (PreparedStatement statement = connection.prepareStatement("UPDATE contact_external_aliases SET contact_id = ? WHERE contact_id = ?")) {
                    statement.setLong(1, primaryId);
                    statement.setLong(2, duplicateId);
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE contacts SET source_device_id = NULL, external_id = NULL WHERE id = ?")) {
                    statement.setLong(1, duplicateId);
                    statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    private List<Contact> queryContacts(String sql) {
        try (Connection connection = connectionProvider.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<ContactScalars> rows = new ArrayList<>();
            while (resultSet.next()) rows.add(mapScalars(resultSet));
            if (rows.isEmpty()) return List.of();

            Map<Long, List<ContactMethod>> phones = new HashMap<>();
            Map<Long, List<ContactMethod>> emails = new HashMap<>();
            loadAllMethods(connection, phones, emails);
            Map<Long, List<ImportantDate>> dates = loadAllImportantDates(connection);

            List<Contact> contacts = new ArrayList<>(rows.size());
            for (ContactScalars row : rows) {
                contacts.add(toContact(
                        row,
                        phones.getOrDefault(row.id(), List.of()),
                        emails.getOrDefault(row.id(), List.of()),
                        dates.getOrDefault(row.id(), List.of())
                ));
            }
            return List.copyOf(contacts);
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    private boolean executeIdUpdate(String sql, long id) {
        try (Connection connection = connectionProvider.open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    private void initializeSchema() {
        try (Connection connection = connectionProvider.open(); Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE_SQL);
            migrateColumns(connection);
            migrateSyncUuids(connection);
            statement.execute(CREATE_METHODS_SQL);
            statement.execute(CREATE_IMPORTANT_DATES_SQL);
            statement.execute(CREATE_SCHEMA_META_SQL);
            statement.execute(CREATE_SYNC_ALIASES_SQL);
            statement.execute(CREATE_EXTERNAL_ALIASES_SQL);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_contact_methods_contact ON contact_methods(contact_id, kind, position)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_contact_important_dates_contact ON contact_important_dates(contact_id, position)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_contact_important_dates_date ON contact_important_dates(date_value)");
            statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_contacts_sync_uuid ON contacts(sync_uuid) WHERE sync_uuid IS NOT NULL AND sync_uuid <> ''");
            statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_contacts_external ON contacts(source_device_id, external_id) WHERE source_device_id IS NOT NULL AND external_id IS NOT NULL");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_sync_alias_contact ON contact_sync_aliases(contact_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_contact_methods_kind_value ON contact_methods(kind, value, contact_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_external_alias_contact ON contact_external_aliases(contact_id)");
            migrateLegacyMethods(connection);
            writeSchemaVersion(connection);
        } catch (SQLException exception) {
            throw databaseFailure(exception);
        }
    }

    private void migrateColumns(Connection connection) throws SQLException {
        Map<String, Boolean> existing = new LinkedHashMap<>();
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("PRAGMA table_info(contacts)")) {
            while (resultSet.next()) existing.put(resultSet.getString("name"), Boolean.TRUE);
        }
        try (Statement statement = connection.createStatement()) {
            for (Map.Entry<String, String> column : REQUIRED_COLUMNS.entrySet()) {
                if (!existing.containsKey(column.getKey())) statement.execute("ALTER TABLE contacts ADD COLUMN " + column.getKey() + " " + column.getValue());
            }
        }
    }

    private void migrateSyncUuids(Connection connection) throws SQLException {
        List<Long> missing = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM contacts WHERE sync_uuid IS NULL OR trim(sync_uuid) = ''");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) missing.add(resultSet.getLong(1));
        }
        try (PreparedStatement update = connection.prepareStatement("UPDATE contacts SET sync_uuid = ? WHERE id = ?")) {
            for (long id : missing) {
                update.setString(1, UUID.randomUUID().toString());
                update.setLong(2, id);
                update.addBatch();
            }
            update.executeBatch();
        }
    }

    private void writeSchemaVersion(Connection connection) throws SQLException {
        String sql = "INSERT INTO schema_meta(key,value) VALUES('schema_version',?) ON CONFLICT(key) DO UPDATE SET value=excluded.value";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Integer.toString(SCHEMA_VERSION));
            statement.executeUpdate();
        }
    }

    private void migrateLegacyMethods(Connection connection) throws SQLException {
        String[] sql = {
                legacyMethodMigrationSql(ContactMethod.PHONE, ContactMethod.LABEL_MOBILE, "phone", 1, 0, true),
                legacyMethodMigrationSql(ContactMethod.PHONE, ContactMethod.LABEL_OTHER, "phone_secondary", 0, 1, false),
                legacyMethodMigrationSql(ContactMethod.PHONE, ContactMethod.LABEL_WORK, "phone_work", 0, 2, false),
                legacyMethodMigrationSql(ContactMethod.EMAIL, ContactMethod.LABEL_EMAIL, "email", 1, 0, true),
                legacyMethodMigrationSql(ContactMethod.EMAIL, ContactMethod.LABEL_WORK, "email_secondary", 0, 1, false)
        };
        try (Statement statement = connection.createStatement()) {
            for (String value : sql) statement.executeUpdate(value);
        }
    }

    private String legacyMethodMigrationSql(String kind, String label, String legacyColumn, int primary, int position, boolean firstOfKind) {
        String duplicateCondition = firstOfKind
                ? "m.contact_id=c.id AND m.kind='%s'".formatted(kind)
                : "m.contact_id=c.id AND m.kind='%s' AND m.value=c.%s".formatted(kind, legacyColumn);
        return ("INSERT INTO contact_methods(contact_id,kind,label,value,is_primary,position) "
                + "SELECT id,'%s','%s',%s,%d,%d FROM contacts c WHERE %s<>'' "
                + "AND NOT EXISTS(SELECT 1 FROM contact_methods m WHERE %s)")
                .formatted(kind, label.replace("'", "''"), legacyColumn, primary, position, legacyColumn, duplicateCondition);
    }

    private int bindDraft(PreparedStatement statement, ContactDraft draft, int startIndex) throws SQLException {
        int index = startIndex;
        statement.setString(index++, draft.name());
        statement.setString(index++, draft.phone());
        statement.setString(index++, draft.phoneSecondary());
        statement.setString(index++, draft.phoneWork());
        statement.setString(index++, draft.email());
        statement.setString(index++, draft.emailSecondary());
        statement.setString(index++, draft.company());
        statement.setString(index++, draft.jobTitle());
        statement.setString(index++, draft.birthday());
        statement.setString(index++, draft.website());
        statement.setString(index++, draft.category());
        statement.setString(index++, draft.address());
        statement.setString(index++, draft.city());
        statement.setString(index++, draft.district());
        statement.setString(index++, draft.postalCode());
        statement.setString(index++, draft.country());
        statement.setString(index++, draft.notes());
        statement.setInt(index++, draft.favorite() ? 1 : 0);
        statement.setBytes(index++, draft.photo());
        statement.setInt(index++, draft.birthdayReminderLeadTime().daysBefore());
        statement.setInt(index++, draft.keepInTouchInterval().days());
        statement.setString(index++, draft.lastContactedDate());
        return index;
    }

    private int bindExternalDraft(PreparedStatement statement, ContactDraft draft, int startIndex) throws SQLException {
        int index = startIndex;
        statement.setString(index++, draft.name());
        statement.setString(index++, draft.phone());
        statement.setString(index++, draft.phoneSecondary());
        statement.setString(index++, draft.phoneWork());
        statement.setString(index++, draft.email());
        statement.setString(index++, draft.emailSecondary());
        statement.setString(index++, draft.company());
        statement.setString(index++, draft.jobTitle());
        statement.setString(index++, draft.birthday());
        statement.setString(index++, draft.website());
        statement.setString(index++, draft.category());
        statement.setString(index++, draft.address());
        statement.setString(index++, draft.city());
        statement.setString(index++, draft.district());
        statement.setString(index++, draft.postalCode());
        statement.setString(index++, draft.country());
        statement.setString(index++, draft.notes());
        statement.setInt(index++, draft.favorite() ? 1 : 0);
        statement.setBytes(index++, draft.photo());
        return index;
    }

    private void replaceMethods(Connection connection, long contactId, ContactDraft draft) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM contact_methods WHERE contact_id = ?")) {
            delete.setLong(1, contactId);
            delete.executeUpdate();
        }
        String sql = "INSERT INTO contact_methods(contact_id,kind,label,value,is_primary,position) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement insert = connection.prepareStatement(sql)) {
            for (ContactMethod method : concat(draft.phones(), draft.emails())) {
                if (method.value().isBlank()) continue;
                insert.setLong(1, contactId);
                insert.setString(2, method.kind());
                insert.setString(3, method.label());
                insert.setString(4, method.value());
                insert.setInt(5, method.primary() ? 1 : 0);
                insert.setInt(6, method.position());
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private void replaceImportantDates(Connection connection, long contactId, ContactDraft draft) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM contact_important_dates WHERE contact_id = ?")) {
            delete.setLong(1, contactId);
            delete.executeUpdate();
        }
        String sql = "INSERT INTO contact_important_dates(contact_id,date_type,label,date_value,remind_days_before,position) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement insert = connection.prepareStatement(sql)) {
            for (ImportantDate value : draft.importantDates()) {
                if (value == null || value.dateValue().isBlank()) continue;
                insert.setLong(1, contactId);
                insert.setString(2, value.type().name());
                insert.setString(3, value.label());
                insert.setString(4, value.dateValue());
                insert.setInt(5, value.reminderLeadTime().daysBefore());
                insert.setInt(6, value.position());
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private Optional<Contact> findById(Connection connection, long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapContact(connection, resultSet)) : Optional.empty();
            }
        }
    }

    private Contact mapContact(Connection connection, ResultSet resultSet) throws SQLException {
        ContactScalars row = mapScalars(resultSet);
        return toContact(row, loadMethods(connection, row.id(), ContactMethod.PHONE),
                loadMethods(connection, row.id(), ContactMethod.EMAIL), loadImportantDates(connection, row.id()));
    }

    private ContactScalars mapScalars(ResultSet resultSet) throws SQLException {
        return new ContactScalars(
                resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("phone"),
                resultSet.getString("phone_secondary"), resultSet.getString("phone_work"), resultSet.getString("email"),
                resultSet.getString("email_secondary"), resultSet.getString("company"), resultSet.getString("job_title"),
                resultSet.getString("birthday"), resultSet.getString("website"), resultSet.getString("category"),
                resultSet.getString("address"), resultSet.getString("city"), resultSet.getString("district"),
                resultSet.getString("postal_code"), resultSet.getString("country"), resultSet.getString("notes"),
                resultSet.getInt("favorite") == 1, resultSet.getBytes("photo_blob"),
                ReminderLeadTime.fromDays(resultSet.getInt("birthday_remind_days_before")),
                KeepInTouchInterval.fromDays(resultSet.getInt("keep_in_touch_days")),
                resultSet.getString("last_contacted_date"), resultSet.getString("sync_uuid")
        );
    }

    private Contact toContact(ContactScalars row, List<ContactMethod> phones, List<ContactMethod> emails, List<ImportantDate> dates) {
        return new Contact(
                row.id(), row.name(), row.phone(), row.phoneSecondary(), row.phoneWork(), row.email(), row.emailSecondary(),
                row.company(), row.jobTitle(), row.birthday(), row.website(), row.category(), row.address(), row.city(),
                row.district(), row.postalCode(), row.country(), row.notes(), row.favorite(), phones, emails, row.photo(),
                row.birthdayReminder(), dates, row.keepInTouch(), row.lastContactedDate(), row.syncUuid()
        );
    }

    private Contact fromDraft(long id, ContactDraft draft, String syncUuid) {
        return new Contact(
                id, draft.name(), draft.phone(), draft.phoneSecondary(), draft.phoneWork(), draft.email(), draft.emailSecondary(),
                draft.company(), draft.jobTitle(), draft.birthday(), draft.website(), draft.category(), draft.address(), draft.city(),
                draft.district(), draft.postalCode(), draft.country(), draft.notes(), draft.favorite(), draft.phones(), draft.emails(),
                draft.photo(), draft.birthdayReminderLeadTime(), draft.importantDates(), draft.keepInTouchInterval(),
                draft.lastContactedDate(), syncUuid
        );
    }

    private List<ContactMethod> loadMethods(Connection connection, long contactId, String kind) throws SQLException {
        String sql = "SELECT kind,label,value,is_primary,position FROM contact_methods WHERE contact_id=? AND kind=? ORDER BY is_primary DESC, position, id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contactId);
            statement.setString(2, kind);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ContactMethod> out = new ArrayList<>();
                while (resultSet.next()) out.add(mapMethod(resultSet));
                return List.copyOf(out);
            }
        }
    }

    private void loadAllMethods(Connection connection, Map<Long, List<ContactMethod>> phones, Map<Long, List<ContactMethod>> emails) throws SQLException {
        String sql = "SELECT contact_id,kind,label,value,is_primary,position FROM contact_methods ORDER BY contact_id,kind,is_primary DESC,position,id";
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                long contactId = resultSet.getLong("contact_id");
                ContactMethod method = mapMethod(resultSet);
                Map<Long, List<ContactMethod>> target = ContactMethod.PHONE.equals(method.kind()) ? phones : emails;
                target.computeIfAbsent(contactId, ignored -> new ArrayList<>()).add(method);
            }
        }
        freezeValues(phones);
        freezeValues(emails);
    }

    private ContactMethod mapMethod(ResultSet resultSet) throws SQLException {
        return new ContactMethod(
                resultSet.getString("kind"), resultSet.getString("label"), resultSet.getString("value"),
                resultSet.getInt("is_primary") == 1, resultSet.getInt("position")
        );
    }

    private List<ImportantDate> loadImportantDates(Connection connection, long contactId) throws SQLException {
        String sql = "SELECT date_type,label,date_value,remind_days_before,position FROM contact_important_dates WHERE contact_id=? ORDER BY position,id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contactId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ImportantDate> values = new ArrayList<>();
                while (resultSet.next()) values.add(mapImportantDate(resultSet));
                return List.copyOf(values);
            }
        }
    }

    private Map<Long, List<ImportantDate>> loadAllImportantDates(Connection connection) throws SQLException {
        Map<Long, List<ImportantDate>> values = new HashMap<>();
        String sql = "SELECT contact_id,date_type,label,date_value,remind_days_before,position FROM contact_important_dates ORDER BY contact_id,position,id";
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                values.computeIfAbsent(resultSet.getLong("contact_id"), ignored -> new ArrayList<>()).add(mapImportantDate(resultSet));
            }
        }
        freezeValues(values);
        return values;
    }

    private ImportantDate mapImportantDate(ResultSet resultSet) throws SQLException {
        ImportantDateType type;
        try { type = ImportantDateType.valueOf(resultSet.getString("date_type")); }
        catch (IllegalArgumentException exception) { type = ImportantDateType.CUSTOM; }
        return new ImportantDate(
                type, resultSet.getString("label"), resultSet.getString("date_value"),
                ReminderLeadTime.fromDays(resultSet.getInt("remind_days_before")), resultSet.getInt("position")
        );
    }

    private <T> void freezeValues(Map<Long, List<T>> map) {
        for (Map.Entry<Long, List<T>> entry : new ArrayList<>(map.entrySet())) {
            map.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
    }

    private List<ContactMethod> concat(List<ContactMethod> first, List<ContactMethod> second) {
        List<ContactMethod> out = new ArrayList<>(first.size() + second.size());
        out.addAll(first);
        out.addAll(second);
        return out;
    }

    private String normalizeSyncUuid(String value) {
        if (value == null || value.isBlank()) return "";
        try { return UUID.fromString(value.trim()).toString(); }
        catch (IllegalArgumentException exception) { return ""; }
    }

    private IllegalStateException databaseFailure(SQLException exception) {
        return new IllegalStateException(Messages.ERROR_DATABASE_OPERATION, exception);
    }

    private static Map<String, String> buildRequiredColumns() {
        Map<String, String> columns = new LinkedHashMap<>();
        columns.put("phone_secondary", "TEXT NOT NULL DEFAULT ''");
        columns.put("phone_work", "TEXT NOT NULL DEFAULT ''");
        columns.put("email_secondary", "TEXT NOT NULL DEFAULT ''");
        columns.put("company", "TEXT NOT NULL DEFAULT ''");
        columns.put("job_title", "TEXT NOT NULL DEFAULT ''");
        columns.put("birthday", "TEXT NOT NULL DEFAULT ''");
        columns.put("website", "TEXT NOT NULL DEFAULT ''");
        columns.put("category", "TEXT NOT NULL DEFAULT ''");
        columns.put("address", "TEXT NOT NULL DEFAULT ''");
        columns.put("city", "TEXT NOT NULL DEFAULT ''");
        columns.put("district", "TEXT NOT NULL DEFAULT ''");
        columns.put("postal_code", "TEXT NOT NULL DEFAULT ''");
        columns.put("country", "TEXT NOT NULL DEFAULT ''");
        columns.put("notes", "TEXT NOT NULL DEFAULT ''");
        columns.put("favorite", "INTEGER NOT NULL DEFAULT 0");
        columns.put("photo_blob", "BLOB");
        columns.put("birthday_remind_days_before", "INTEGER NOT NULL DEFAULT -1");
        columns.put("keep_in_touch_days", "INTEGER NOT NULL DEFAULT 0");
        columns.put("last_contacted_date", "TEXT NOT NULL DEFAULT ''");
        columns.put("sync_uuid", "TEXT");
        columns.put("source_device_id", "TEXT");
        columns.put("external_id", "TEXT");
        columns.put("deleted_at", "TEXT");
        return columns;
    }

    private record ContactScalars(
            long id, String name, String phone, String phoneSecondary, String phoneWork,
            String email, String emailSecondary, String company, String jobTitle, String birthday,
            String website, String category, String address, String city, String district, String postalCode,
            String country, String notes, boolean favorite, byte[] photo, ReminderLeadTime birthdayReminder,
            KeepInTouchInterval keepInTouch, String lastContactedDate, String syncUuid
    ) { }
}
