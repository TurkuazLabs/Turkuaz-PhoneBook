// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/ImportExportService.java
// # 📌 Amac: VCF ve CSV ice/disa aktarma is akisini yonetir.
// # 📌 Service - Java
// # Version: 2.29.0
// # Aciklama: Import dosya turunu belirler; tum rehber veya secili kisiler icin tip guvenli VCF/CSV export akislarini Tool katmanina yonlendirir.
// # Bagimli Oldugu Katman: Service | Tool | Model | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.ContactFileFormat;
import com.turkuazlabs.telefonrehberi.models.ImportResult;
import com.turkuazlabs.telefonrehberi.tools.CsvContactTool;
import com.turkuazlabs.telefonrehberi.tools.VCardContactTool;

import java.nio.file.Path;
import java.util.List;

public final class ImportExportService {
    private final ContactService contactService;
    private final CsvContactTool csvTool;
    private final VCardContactTool vCardTool;

    public ImportExportService(ContactService contactService, CsvContactTool csvTool, VCardContactTool vCardTool) {
        this.contactService = contactService;
        this.csvTool = csvTool;
        this.vCardTool = vCardTool;
    }

    public ImportResult importFile(Path path) {
        requirePath(path);
        List<ContactDraft> drafts = read(path);
        int imported = 0;
        int skipped = 0;
        for (ContactDraft draft : drafts) {
            try {
                contactService.addImportedContact(draft);
                imported++;
            } catch (IllegalArgumentException exception) {
                skipped++;
            }
        }
        return new ImportResult(imported, skipped);
    }

    public void exportFile(Path path, ContactFileFormat format) {
        write(path, format, contactService.listContacts());
    }

    public int exportContacts(Path path, ContactFileFormat format, List<Long> contactIds) {
        List<Contact> contacts = contactService.requireContacts(contactIds);
        write(path, format, contacts);
        return contacts.size();
    }

    private void write(Path path, ContactFileFormat format, List<Contact> contacts) {
        requirePath(path);
        requireContactFileFormat(format);
        if (!format.matchesFileName(path.getFileName().toString())) {
            throw new IllegalArgumentException(Messages.ERROR_EXPORT_EXTENSION);
        }
        switch (format) {
            case CSV -> csvTool.write(path, contacts);
            case VCARD -> vCardTool.write(path, contacts);
        }
    }

    private List<ContactDraft> read(Path path) {
        ContactFileFormat format = ContactFileFormat.fromFileName(path.getFileName().toString())
                .orElseThrow(() -> new IllegalArgumentException(Messages.ERROR_IMPORT_EXTENSION));
        return switch (format) {
            case CSV -> csvTool.read(path);
            case VCARD -> vCardTool.read(path);
        };
    }

    private void requirePath(Path path) {
        if (path == null) throw new IllegalArgumentException(Messages.ERROR_FILE_REQUIRED);
    }

    private void requireContactFileFormat(ContactFileFormat format) {
        if (format == null) throw new IllegalArgumentException(Messages.ERROR_EXPORT_EXTENSION);
    }
}
