// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/MigrationService.java
// # 📌 Amac: Eski TSV rehber verisini yeni SQLite depolamaya bir kez aktarir.
// # 📌 Service - Java
// # Version: 1.0.0
// # Aciklama: SQLite bos ise legacy reader ve repository arasinda migration is akisini yonetir.
// # Bagimli Oldugu Katman: Service | Repository | Tool
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.repositories.ContactRepository;
import com.turkuazlabs.telefonrehberi.tools.LegacyTsvReader;

import java.util.List;

public final class MigrationService {
    private final ContactRepository repository;
    private final LegacyTsvReader legacyReader;

    public MigrationService(ContactRepository repository, LegacyTsvReader legacyReader) {
        this.repository = repository;
        this.legacyReader = legacyReader;
    }

    public int migrateLegacyContactsIfNeeded() {
        if (repository.count() > 0L) {
            return 0;
        }
        List<Contact> legacyContacts = legacyReader.readIfExists();
        repository.importLegacy(legacyContacts);
        return legacyContacts.size();
    }
}
