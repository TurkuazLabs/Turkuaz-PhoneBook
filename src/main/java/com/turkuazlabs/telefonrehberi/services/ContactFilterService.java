// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/ContactFilterService.java
// # 📌 Amac: Kisi arama, coklu filtreleme ve siralama is kurallarini merkezi olarak uygular.
// # 📌 Service - Java
// # Version: 1.1.0
// # Aciklama: Metin ve yapisal filtreleri birlestirir; ad, firma, sehir, guncellenme ve favori oncelikli siralamayi merkezi uygular.
// # Bagimli Oldugu Katman: Service | Repository | Model
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactFilter;
import com.turkuazlabs.telefonrehberi.models.ContactFilterOption;
import com.turkuazlabs.telefonrehberi.models.ContactFilterOptions;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.models.ContactSort;
import com.turkuazlabs.telefonrehberi.models.GroupRecord;
import com.turkuazlabs.telefonrehberi.models.TagRecord;
import com.turkuazlabs.telefonrehberi.repositories.ContactRepository;
import com.turkuazlabs.telefonrehberi.repositories.GroupRepository;
import com.turkuazlabs.telefonrehberi.repositories.TagRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

public final class ContactFilterService {
    private final ContactRepository contactRepository;
    private final GroupRepository groupRepository;
    private final TagRepository tagRepository;

    public ContactFilterService(
            ContactRepository contactRepository,
            GroupRepository groupRepository,
            TagRepository tagRepository
    ) {
        this.contactRepository = contactRepository;
        this.groupRepository = groupRepository;
        this.tagRepository = tagRepository;
    }

    public List<Contact> filter(ContactFilter filter) {
        ContactFilter safeFilter = filter == null
                ? new ContactFilter("", false, "", "", "", null, null, ContactSort.defaultSort())
                : filter;
        String query = normalize(safeFilter.query());
        Map<Long, List<GroupRecord>> groupMemberships = safeFilter.groupId() == null
                ? Map.of()
                : groupRepository.findMembershipsByContact();
        Map<Long, List<TagRecord>> tagMemberships = safeFilter.tagId() == null
                ? Map.of()
                : tagRepository.findMembershipsByContact();

        Map<Long, String> updatedAt = safeFilter.sort() == ContactSort.UPDATED_DESC
                ? contactRepository.findUpdatedAtByContact()
                : Map.of();

        return contactRepository.findAll().stream()
                .filter(contact -> !safeFilter.favoritesOnly() || contact.favorite())
                .filter(contact -> query.isEmpty() || matchesQuery(contact, query))
                .filter(contact -> matchesExact(contact.company(), safeFilter.company()))
                .filter(contact -> matchesExact(contact.city(), safeFilter.city()))
                .filter(contact -> matchesExact(contact.category(), safeFilter.category()))
                .filter(contact -> matchesGroup(contact.id(), safeFilter.groupId(), groupMemberships))
                .filter(contact -> matchesTag(contact.id(), safeFilter.tagId(), tagMemberships))
                .sorted(sortComparator(safeFilter.sort(), updatedAt))
                .toList();
    }

    public ContactFilterOptions options() {
        List<Contact> contacts = contactRepository.findAll();
        return new ContactFilterOptions(
                distinctValues(contacts.stream().map(Contact::company).toList()),
                distinctValues(contacts.stream().map(Contact::city).toList()),
                distinctValues(contacts.stream().map(Contact::category).toList()),
                groupRepository.findAll().stream()
                        .map(group -> new ContactFilterOption(group.id(), group.name()))
                        .toList(),
                tagRepository.findAll().stream()
                        .map(tag -> new ContactFilterOption(tag.id(), tag.name()))
                        .toList()
        );
    }

    private Comparator<Contact> sortComparator(ContactSort sort, Map<Long, String> updatedAt) {
        ContactSort safeSort = sort == null ? ContactSort.defaultSort() : sort;
        Comparator<Contact> byNameAsc = Comparator
                .comparing((Contact contact) -> safeText(contact.name()), String.CASE_INSENSITIVE_ORDER)
                .thenComparingLong(Contact::id);

        return switch (safeSort) {
            case FAVORITES_FIRST -> Comparator
                    .comparing(Contact::favorite)
                    .reversed()
                    .thenComparing(byNameAsc);
            case NAME_ASC -> byNameAsc;
            case NAME_DESC -> Comparator
                    .comparing((Contact contact) -> safeText(contact.name()), String.CASE_INSENSITIVE_ORDER.reversed())
                    .thenComparingLong(Contact::id);
            case COMPANY -> Comparator
                    .comparing((Contact contact) -> isBlank(contact.company()))
                    .thenComparing(contact -> safeText(contact.company()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(byNameAsc);
            case CITY -> Comparator
                    .comparing((Contact contact) -> isBlank(contact.city()))
                    .thenComparing(contact -> safeText(contact.city()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(byNameAsc);
            case UPDATED_DESC -> Comparator
                    .comparing((Contact contact) -> safeText(updatedAt.get(contact.id())), Comparator.reverseOrder())
                    .thenComparing(byNameAsc);
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private List<String> distinctValues(List<String> values) {
        TreeSet<String> sorted = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                sorted.add(value.trim());
            }
        }
        return List.copyOf(sorted);
    }

    private boolean matchesQuery(Contact contact, String query) {
        return contains(contact.name(), query)
                || contact.phones().stream().anyMatch(method -> matchesMethod(method, query))
                || contact.emails().stream().anyMatch(method -> matchesMethod(method, query))
                || contains(contact.company(), query)
                || contains(contact.jobTitle(), query)
                || contains(contact.category(), query)
                || contains(contact.address(), query)
                || contains(contact.city(), query)
                || contains(contact.district(), query)
                || contains(contact.country(), query)
                || contains(contact.notes(), query);
    }

    private boolean matchesMethod(ContactMethod method, String query) {
        return contains(method.value(), query) || contains(method.label(), query);
    }

    private boolean matchesExact(String value, String filterValue) {
        return filterValue == null
                || filterValue.isBlank()
                || (value != null && value.trim().equalsIgnoreCase(filterValue.trim()));
    }

    private boolean matchesGroup(long contactId, Long groupId, Map<Long, List<GroupRecord>> memberships) {
        if (groupId == null) return true;
        return memberships.getOrDefault(contactId, List.of()).stream().anyMatch(group -> group.id() == groupId);
    }

    private boolean matchesTag(long contactId, Long tagId, Map<Long, List<TagRecord>> memberships) {
        if (tagId == null) return true;
        return memberships.getOrDefault(contactId, List.of()).stream().anyMatch(tag -> tag.id() == tagId);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
