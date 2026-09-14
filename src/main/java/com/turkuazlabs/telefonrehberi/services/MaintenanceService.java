// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/MaintenanceService.java
// # 📌 Amac: Mukerrer kisi tespiti ve guvenli birlestirme is kurallarini yonetir.
// # 📌 Service - Java
// # Version: 2.37.1
// # Aciklama: Duplicate adaylarini bulur; kisi alanlariyla birlikte onemli tarih ve iletisimde kal verilerini kayipsiz birlestirir.
// # Bagimli Oldugu Katman: Service | Repository | Model | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.models.DuplicateCandidate;
import com.turkuazlabs.telefonrehberi.models.ImportantDate;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MaintenanceService {
    private final ContactService contactService;
    private final GroupService groupService;
    private final TagService tagService;

    public MaintenanceService(ContactService contactService, GroupService groupService, TagService tagService) {
        this.contactService = contactService;
        this.groupService = groupService;
        this.tagService = tagService;
    }

    public List<DuplicateCandidate> findDuplicates() {
        List<Contact> contacts = contactService.listContacts();
        java.util.Map<Long, Contact> byId = contacts.stream().collect(java.util.stream.Collectors.toMap(Contact::id, value -> value));
        java.util.LinkedHashMap<PairKey, String> matches = new java.util.LinkedHashMap<>();

        indexDuplicateValues(contacts, matches, true);
        indexDuplicateValues(contacts, matches, false);
        indexNameCompany(contacts, matches);

        return matches.entrySet().stream()
                .map(entry -> new DuplicateCandidate(
                        byId.get(entry.getKey().firstId()), byId.get(entry.getKey().secondId()), entry.getValue()
                ))
                .filter(candidate -> candidate.primary() != null && candidate.duplicate() != null)
                .toList();
    }

    private void indexDuplicateValues(List<Contact> contacts, java.util.Map<PairKey, String> matches, boolean phone) {
        java.util.Map<String, List<Long>> index = new java.util.HashMap<>();
        for (Contact contact : contacts) {
            java.util.LinkedHashSet<String> values = new java.util.LinkedHashSet<>();
            if (phone) {
                contact.phones().forEach(method -> {
                    String normalized = normalizePhone(method.value());
                    if (!normalized.isEmpty()) values.add(normalized);
                });
            } else {
                contact.emails().forEach(method -> {
                    String normalized = normalizeText(method.value());
                    if (!normalized.isEmpty()) values.add(normalized);
                });
            }
            for (String value : values) {
                List<Long> previous = index.computeIfAbsent(value, ignored -> new ArrayList<>());
                for (long previousId : previous) {
                    matches.putIfAbsent(PairKey.of(previousId, contact.id()),
                            phone ? Messages.DUPLICATE_REASON_PHONE : Messages.DUPLICATE_REASON_EMAIL);
                }
                previous.add(contact.id());
            }
        }
    }

    private void indexNameCompany(List<Contact> contacts, java.util.Map<PairKey, String> matches) {
        java.util.Map<String, List<Long>> index = new java.util.HashMap<>();
        for (Contact contact : contacts) {
            String name = normalizeText(contact.name());
            String company = normalizeText(contact.company());
            if (name.isEmpty() || company.isEmpty()) continue;
            String key = name + "\u0000" + company;
            List<Long> previous = index.computeIfAbsent(key, ignored -> new ArrayList<>());
            for (long previousId : previous) {
                matches.putIfAbsent(PairKey.of(previousId, contact.id()), Messages.DUPLICATE_REASON_NAME_COMPANY);
            }
            previous.add(contact.id());
        }
    }

    public void permanentlyDelete(long contactId) {
        groupService.removeAllMemberships(contactId);
        tagService.removeAllMemberships(contactId);
        contactService.permanentlyDeleteContact(contactId);
    }

    public Contact merge(long primaryId, long duplicateId) {
        if (primaryId == duplicateId) {
            throw new IllegalArgumentException(Messages.ERROR_DUPLICATE_SAME_CONTACT);
        }
        Contact primary = contactService.findById(primaryId)
                .orElseThrow(() -> new IllegalArgumentException(Messages.ERROR_CONTACT_NOT_FOUND));
        Contact duplicate = contactService.findById(duplicateId)
                .orElseThrow(() -> new IllegalArgumentException(Messages.ERROR_CONTACT_NOT_FOUND));
        Contact merged = contactService.replaceForMerge(primaryId, mergeDraft(primary, duplicate));
        contactService.transferSyncIdentitiesForMerge(duplicateId, primaryId);
        groupService.transferMemberships(duplicateId, primaryId);
        tagService.transferMemberships(duplicateId, primaryId);
        contactService.softDeleteForMerge(duplicateId);
        return merged;
    }

    private ContactDraft mergeDraft(Contact a, Contact b) {
        ContactDraft legacy = new ContactDraft(
                choose(a.name(), b.name()),
                choose(a.phone(), b.phone()),
                choose(a.phoneSecondary(), secondaryPhone(a, b)),
                choose(a.phoneWork(), b.phoneWork()),
                choose(a.email(), b.email()),
                choose(a.emailSecondary(), secondaryEmail(a, b)),
                choose(a.company(), b.company()),
                choose(a.jobTitle(), b.jobTitle()),
                choose(a.birthday(), b.birthday()),
                choose(a.website(), b.website()),
                choose(a.category(), b.category()),
                choose(a.address(), b.address()),
                choose(a.city(), b.city()),
                choose(a.district(), b.district()),
                choose(a.postalCode(), b.postalCode()),
                choose(a.country(), b.country()),
                mergeNotes(a.notes(), b.notes()),
                a.favorite() || b.favorite()
        );
        ReminderLeadTime birthdayReminder = a.birthdayReminderLeadTime() != ReminderLeadTime.DISABLED
                ? a.birthdayReminderLeadTime() : b.birthdayReminderLeadTime();
        KeepInTouchInterval keepInTouch = a.keepInTouchInterval().enabled()
                ? a.keepInTouchInterval() : b.keepInTouchInterval();
        String lastContacted = laterDate(a.lastContactedDate(), b.lastContactedDate());
        List<ContactMethod> phones = mergeMethods(a.phones(), b.phones(), ContactMethod.PHONE);
        List<ContactMethod> emails = mergeMethods(a.emails(), b.emails(), ContactMethod.EMAIL);
        return new ContactDraft(
                legacy.name(), valueAt(phones, 0), valueAt(phones, 1), firstMethodByLabel(phones, ContactMethod.LABEL_WORK),
                valueAt(emails, 0), valueAt(emails, 1),
                legacy.company(), legacy.jobTitle(), legacy.birthday(), legacy.website(), legacy.category(), legacy.address(),
                legacy.city(), legacy.district(), legacy.postalCode(), legacy.country(), legacy.notes(), legacy.favorite(),
                phones, emails, a.photo() != null ? a.photo() : b.photo(),
                birthdayReminder, mergeImportantDates(a.importantDates(), b.importantDates()), keepInTouch, lastContacted
        );
    }

    private List<ContactMethod> mergeMethods(List<ContactMethod> first, List<ContactMethod> second, String kind) {
        List<ContactMethod> combined = new ArrayList<>();
        java.util.LinkedHashSet<String> seen = new java.util.LinkedHashSet<>();
        boolean primaryAssigned = false;
        for (ContactMethod method : java.util.stream.Stream.concat(first.stream(), second.stream()).toList()) {
            if (method == null || !kind.equals(method.kind()) || blank(method.value())) continue;
            String key = ContactMethod.PHONE.equals(kind) ? normalizePhone(method.value()) : normalizeText(method.value());
            if (key.isEmpty() || !seen.add(key)) continue;
            boolean primary = method.primary() && !primaryAssigned;
            if (primary) primaryAssigned = true;
            combined.add(new ContactMethod(kind, method.label(), method.value().trim(), primary, combined.size()));
        }
        if (!combined.isEmpty() && !primaryAssigned) {
            ContactMethod firstMethod = combined.get(0);
            combined.set(0, new ContactMethod(
                    firstMethod.kind(), firstMethod.label(), firstMethod.value(), true, firstMethod.position()
            ));
        }
        return List.copyOf(combined);
    }

    private String valueAt(List<ContactMethod> methods, int index) {
        return index >= 0 && index < methods.size() ? methods.get(index).value() : "";
    }

    private String firstMethodByLabel(List<ContactMethod> methods, String label) {
        return methods.stream()
                .filter(method -> label.equalsIgnoreCase(method.label()))
                .map(ContactMethod::value)
                .findFirst()
                .orElse("");
    }

    private List<ImportantDate> mergeImportantDates(List<ImportantDate> first, List<ImportantDate> second) {
        List<ImportantDate> result = new ArrayList<>();
        for (ImportantDate value : first) addImportantDateIfMissing(result, value);
        for (ImportantDate value : second) addImportantDateIfMissing(result, value);
        List<ImportantDate> normalized = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            ImportantDate value = result.get(i);
            normalized.add(new ImportantDate(value.type(), value.label(), value.dateValue(), value.reminderLeadTime(), i));
        }
        return List.copyOf(normalized);
    }

    private void addImportantDateIfMissing(List<ImportantDate> result, ImportantDate candidate) {
        if (candidate == null || candidate.dateValue().isBlank()) return;
        boolean exists = result.stream().anyMatch(value ->
                value.type() == candidate.type()
                        && sameText(value.label(), candidate.label())
                        && value.dateValue().equals(candidate.dateValue())
        );
        if (!exists) result.add(candidate);
    }

    private String laterDate(String first, String second) {
        if (blank(first)) return safe(second);
        if (blank(second)) return first.trim();
        return first.trim().compareTo(second.trim()) >= 0 ? first.trim() : second.trim();
    }

    private String secondaryPhone(Contact a, Contact b) {
        if (!sameNormalized(a.phone(), b.phone())) return b.phone();
        return b.phoneSecondary();
    }

    private String secondaryEmail(Contact a, Contact b) {
        if (!sameText(a.email(), b.email())) return b.email();
        return b.emailSecondary();
    }

    private String mergeNotes(String first, String second) {
        if (blank(first)) return safe(second);
        if (blank(second) || first.trim().equals(second.trim())) return first.trim();
        return first.trim() + System.lineSeparator() + "---" + System.lineSeparator() + second.trim();
    }

    private String duplicateReason(Contact a, Contact b) {
        if (sameNormalized(a.phone(), b.phone()) && !blank(a.phone())) return Messages.DUPLICATE_REASON_PHONE;
        if (sameText(a.email(), b.email()) && !blank(a.email())) return Messages.DUPLICATE_REASON_EMAIL;
        if (sameText(a.name(), b.name()) && !blank(a.name()) && sameText(a.company(), b.company()) && !blank(a.company())) {
            return Messages.DUPLICATE_REASON_NAME_COMPANY;
        }
        return "";
    }

    private boolean sameNormalized(String a, String b) {
        return normalizePhone(a).equals(normalizePhone(b));
    }
    private boolean sameText(String a, String b) {
        return safe(a).trim().toLowerCase(Locale.ROOT).equals(safe(b).trim().toLowerCase(Locale.ROOT));
    }
    private String normalizePhone(String value) { return safe(value).replaceAll("[^0-9+]", ""); }
    private String normalizeText(String value) { return safe(value).trim().toLowerCase(Locale.ROOT); }
    private String choose(String first, String second) { return blank(first) ? safe(second) : first.trim(); }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private String safe(String value) { return value == null ? "" : value; }

    private record PairKey(long firstId, long secondId) {
        static PairKey of(long a, long b) { return a <= b ? new PairKey(a, b) : new PairKey(b, a); }
    }
}
