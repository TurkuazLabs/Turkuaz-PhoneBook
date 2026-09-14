// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/TagService.java
// # 📌 Amac: Renkli kisi etiketleri icin is kurallarini uygular.
// # 📌 Service - Java
// # Version: 2.29.0
// # Aciklama: Etiket adini dogrular, etiket CRUD ile tekli/toplu kisi baglama-cikarma islemlerini Repository katmanina yonlendirir.
// # Bagimli Oldugu Katman: Service | Repository | Model | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.TagRecord;
import com.turkuazlabs.telefonrehberi.repositories.TagRepository;

import java.util.List;
import java.util.Map;

public final class TagService {
    private final TagRepository repository;

    public TagService(TagRepository repository) { this.repository = repository; }
    public List<TagRecord> listTags() { return repository.findAll(); }
    public Map<Long, List<TagRecord>> membershipsByContact() { return repository.findMembershipsByContact(); }
    public void createTag(String name, String color) {
        repository.insert(required(name), validColor(color));
    }
    public void deleteTag(long id) { if (!repository.delete(id)) throw new IllegalArgumentException(Messages.ERROR_TAG_NOT_FOUND); }
    public void addContact(long tagId, long contactId) { repository.addContact(tagId, contactId); }
    public int addContacts(long tagId, List<Long> contactIds) {
        return repository.addContacts(tagId, requiredIds(contactIds));
    }
    public void removeContact(long tagId, long contactId) { repository.removeContact(tagId, contactId); }
    public int removeContacts(long tagId, List<Long> contactIds) {
        return repository.removeContacts(tagId, requiredIds(contactIds));
    }
    public void removeAllMemberships(long contactId) { repository.removeAllMemberships(contactId); }
    public void transferMemberships(long sourceContactId, long targetContactId) { repository.transferMemberships(sourceContactId, targetContactId); }
    private List<Long> requiredIds(List<Long> contactIds) {
        if (contactIds == null || contactIds.isEmpty()) throw new IllegalArgumentException(Messages.SELECT_CONTACT);
        return contactIds.stream().filter(id -> id != null && id > 0L).distinct().toList();
    }
    private String validColor(String value) {
        String color = value == null ? "" : value.trim().toLowerCase();
        return AppConfig.LABEL_COLORS.contains(color) ? color : AppConfig.LABEL_COLORS.get(0);
    }
    private String required(String value) { if (value == null || value.isBlank()) throw new IllegalArgumentException(Messages.ERROR_TAG_NAME_REQUIRED); return value.trim(); }
}
