// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/GroupService.java
// # 📌 Amac: Manuel kisi gruplari icin is kurallarini uygular.
// # 📌 Service - Java
// # Version: 2.29.0
// # Aciklama: Grup adini dogrular, grup CRUD ile tekli/toplu kisi uyelik islemlerini Repository katmanina yonlendirir.
// # Bagimli Oldugu Katman: Service | Repository | Model | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.GroupRecord;
import com.turkuazlabs.telefonrehberi.repositories.GroupRepository;

import java.util.List;
import java.util.Map;

public final class GroupService {
    private final GroupRepository repository;

    public GroupService(GroupRepository repository) { this.repository = repository; }
    public List<GroupRecord> listGroups() { return repository.findAll(); }
    public Map<Long, List<GroupRecord>> membershipsByContact() { return repository.findMembershipsByContact(); }
    public void createGroup(String name, String color) {
        repository.insert(required(name, Messages.ERROR_GROUP_NAME_REQUIRED), validColor(color));
    }
    public void deleteGroup(long id) { if (!repository.delete(id)) throw new IllegalArgumentException(Messages.ERROR_GROUP_NOT_FOUND); }
    public void addContact(long groupId, long contactId) { repository.addContact(groupId, contactId); }
    public int addContacts(long groupId, List<Long> contactIds) {
        return repository.addContacts(groupId, requiredIds(contactIds));
    }
    public void removeContact(long groupId, long contactId) { repository.removeContact(groupId, contactId); }
    public int removeContacts(long groupId, List<Long> contactIds) {
        return repository.removeContacts(groupId, requiredIds(contactIds));
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
    private String required(String value, String message) { if (value == null || value.isBlank()) throw new IllegalArgumentException(message); return value.trim(); }
}
