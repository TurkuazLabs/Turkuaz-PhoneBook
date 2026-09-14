// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/BulkUndoService.java
// # 📌 Amac: Toplu kisi islemlerinin sinirli cok adimli undo/redo gecmisini guvenli sekilde yonetir.
// # 📌 Service - Java
// # Version: 2.0.0
// # Aciklama: Firma/kategori/favori, grup/etiket ve Cop Kutusu islemlerini stack tabanli undo/redo gecmisiyle yonetir.
// # Bagimli Oldugu Katman: Service | Model | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.BulkUndoState;
import com.turkuazlabs.telefonrehberi.models.BulkUndoType;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.GroupRecord;
import com.turkuazlabs.telefonrehberi.models.TagRecord;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BulkUndoService {
    private final ContactService contactService;
    private final GroupService groupService;
    private final TagService tagService;
    private final int historyLimit;
    private final Deque<BulkUndoState> undoStack = new ArrayDeque<>();
    private final Deque<BulkUndoState> redoStack = new ArrayDeque<>();

    public BulkUndoService(ContactService contactService, GroupService groupService, TagService tagService, int historyLimit) {
        if (historyLimit < 1) {
            throw new IllegalArgumentException(Messages.ERROR_BULK_HISTORY_LIMIT);
        }
        this.contactService = contactService;
        this.groupService = groupService;
        this.tagService = tagService;
        this.historyLimit = historyLimit;
    }

    public int setCompanies(List<Long> contactIds, String company) {
        String target = normalize(company);
        List<Contact> before = contactService.requireContacts(contactIds).stream()
                .filter(contact -> !normalize(contact.company()).equals(target))
                .toList();
        int changed = contactService.setCompanies(contactIds, company);
        storeContactSnapshot(Messages.UNDO_BULK_COMPANY, before, changed);
        return changed;
    }

    public int setCategories(List<Long> contactIds, String category) {
        String target = normalize(category);
        List<Contact> before = contactService.requireContacts(contactIds).stream()
                .filter(contact -> !normalize(contact.category()).equals(target))
                .toList();
        int changed = contactService.setCategories(contactIds, category);
        storeContactSnapshot(Messages.UNDO_BULK_CATEGORY, before, changed);
        return changed;
    }

    public int setFavorites(List<Long> contactIds, boolean favorite) {
        List<Contact> before = contactService.requireContacts(contactIds).stream()
                .filter(contact -> contact.favorite() != favorite)
                .toList();
        int changed = contactService.setFavorites(contactIds, favorite);
        storeContactSnapshot(favorite ? Messages.UNDO_BULK_FAVORITE : Messages.UNDO_BULK_UNFAVORITE, before, changed);
        return changed;
    }

    public int addToGroup(long groupId, List<Long> contactIds) {
        List<Long> affected = contactsWithoutGroup(groupId, contactIds, groupService.membershipsByContact());
        int changed = groupService.addContacts(groupId, contactIds);
        storeMembership(BulkUndoType.GROUP_ADD, Messages.UNDO_BULK_GROUP_ADD, groupId, affected, changed);
        return changed;
    }

    public int removeFromGroup(long groupId, List<Long> contactIds) {
        List<Long> affected = contactsWithGroup(groupId, contactIds, groupService.membershipsByContact());
        int changed = groupService.removeContacts(groupId, contactIds);
        storeMembership(BulkUndoType.GROUP_REMOVE, Messages.UNDO_BULK_GROUP_REMOVE, groupId, affected, changed);
        return changed;
    }

    public int addToTag(long tagId, List<Long> contactIds) {
        List<Long> affected = contactsWithoutTag(tagId, contactIds, tagService.membershipsByContact());
        int changed = tagService.addContacts(tagId, contactIds);
        storeMembership(BulkUndoType.TAG_ADD, Messages.UNDO_BULK_TAG_ADD, tagId, affected, changed);
        return changed;
    }

    public int removeFromTag(long tagId, List<Long> contactIds) {
        List<Long> affected = contactsWithTag(tagId, contactIds, tagService.membershipsByContact());
        int changed = tagService.removeContacts(tagId, contactIds);
        storeMembership(BulkUndoType.TAG_REMOVE, Messages.UNDO_BULK_TAG_REMOVE, tagId, affected, changed);
        return changed;
    }

    public int moveToTrash(List<Long> contactIds) {
        List<Long> ids = contactService.requireContacts(contactIds).stream().map(Contact::id).toList();
        int changed = contactService.deleteContacts(contactIds);
        if (changed > 0) {
            pushNewUndo(new BulkUndoState(BulkUndoType.TRASH, Messages.UNDO_BULK_TRASH, 0L, ids, List.of()));
        }
        return changed;
    }

    public Optional<String> currentLabel() {
        BulkUndoState state = undoStack.peekFirst();
        return state == null ? Optional.empty() : Optional.of(state.label());
    }

    public Optional<String> redoLabel() {
        BulkUndoState state = redoStack.peekFirst();
        return state == null ? Optional.empty() : Optional.of(state.label());
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public String undo() {
        BulkUndoState state = undoStack.pollFirst();
        if (state == null) {
            throw new IllegalStateException(Messages.UNDO_NOT_AVAILABLE);
        }
        try {
            BulkUndoState redoState = counterState(state);
            applyUndo(state);
            push(redoStack, redoState);
            return state.label();
        } catch (RuntimeException exception) {
            undoStack.addFirst(state);
            throw exception;
        }
    }

    public String redo() {
        BulkUndoState state = redoStack.pollFirst();
        if (state == null) {
            throw new IllegalStateException(Messages.REDO_NOT_AVAILABLE);
        }
        try {
            BulkUndoState undoState = counterState(state);
            applyRedo(state);
            push(undoStack, undoState);
            return state.label();
        } catch (RuntimeException exception) {
            redoStack.addFirst(state);
            throw exception;
        }
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    private void applyUndo(BulkUndoState state) {
        switch (state.type()) {
            case CONTACT_SNAPSHOT -> contactService.restoreBulkSnapshots(state.contacts());
            case GROUP_ADD -> groupService.removeContacts(state.relatedId(), state.contactIds());
            case GROUP_REMOVE -> groupService.addContacts(state.relatedId(), state.contactIds());
            case TAG_ADD -> tagService.removeContacts(state.relatedId(), state.contactIds());
            case TAG_REMOVE -> tagService.addContacts(state.relatedId(), state.contactIds());
            case TRASH -> contactService.restoreTrashContacts(state.contactIds());
        }
    }

    private void applyRedo(BulkUndoState state) {
        switch (state.type()) {
            case CONTACT_SNAPSHOT -> contactService.restoreBulkSnapshots(state.contacts());
            case GROUP_ADD -> groupService.addContacts(state.relatedId(), state.contactIds());
            case GROUP_REMOVE -> groupService.removeContacts(state.relatedId(), state.contactIds());
            case TAG_ADD -> tagService.addContacts(state.relatedId(), state.contactIds());
            case TAG_REMOVE -> tagService.removeContacts(state.relatedId(), state.contactIds());
            case TRASH -> contactService.deleteContacts(state.contactIds());
        }
    }

    private BulkUndoState counterState(BulkUndoState state) {
        if (state.type() != BulkUndoType.CONTACT_SNAPSHOT) {
            return state;
        }
        List<Contact> current = contactService.requireContacts(state.contactIds());
        return new BulkUndoState(state.type(), state.label(), state.relatedId(), state.contactIds(), current);
    }

    private void storeContactSnapshot(String label, List<Contact> before, int changed) {
        if (changed > 0 && !before.isEmpty()) {
            pushNewUndo(new BulkUndoState(BulkUndoType.CONTACT_SNAPSHOT, label, 0L,
                    before.stream().map(Contact::id).toList(), before));
        }
    }

    private void storeMembership(BulkUndoType type, String label, long relatedId, List<Long> affected, int changed) {
        if (changed > 0 && !affected.isEmpty()) {
            pushNewUndo(new BulkUndoState(type, label, relatedId, affected, List.of()));
        }
    }

    private void pushNewUndo(BulkUndoState state) {
        push(undoStack, state);
        redoStack.clear();
    }

    private void push(Deque<BulkUndoState> stack, BulkUndoState state) {
        stack.addFirst(state);
        while (stack.size() > historyLimit) {
            stack.removeLast();
        }
    }

    private List<Long> contactsWithoutGroup(long groupId, List<Long> contactIds, Map<Long, List<GroupRecord>> memberships) {
        return normalizedIds(contactIds).stream()
                .filter(contactId -> memberships.getOrDefault(contactId, List.of()).stream().noneMatch(group -> group.id() == groupId))
                .toList();
    }

    private List<Long> contactsWithGroup(long groupId, List<Long> contactIds, Map<Long, List<GroupRecord>> memberships) {
        return normalizedIds(contactIds).stream()
                .filter(contactId -> memberships.getOrDefault(contactId, List.of()).stream().anyMatch(group -> group.id() == groupId))
                .toList();
    }

    private List<Long> contactsWithoutTag(long tagId, List<Long> contactIds, Map<Long, List<TagRecord>> memberships) {
        return normalizedIds(contactIds).stream()
                .filter(contactId -> memberships.getOrDefault(contactId, List.of()).stream().noneMatch(tag -> tag.id() == tagId))
                .toList();
    }

    private List<Long> contactsWithTag(long tagId, List<Long> contactIds, Map<Long, List<TagRecord>> memberships) {
        return normalizedIds(contactIds).stream()
                .filter(contactId -> memberships.getOrDefault(contactId, List.of()).stream().anyMatch(tag -> tag.id() == tagId))
                .toList();
    }

    private List<Long> normalizedIds(List<Long> contactIds) {
        if (contactIds == null) return List.of();
        return contactIds.stream().filter(id -> id != null && id > 0L).distinct().toList();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
