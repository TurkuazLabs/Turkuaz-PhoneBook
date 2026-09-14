// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/ContactService.java
// # 📌 Amac: Telefon rehberi is kurallarini, dogrulamalari, aramayi, gecmisi ve cop kutusunu yonetir.
// # 📌 Service - Java
// # Version: 2.37.1
// # Aciklama: CRUD, history, profil aktivitesi, onemli tarih dogrulamasi ve iletisimde kal hatirlatma kurallarini uygular.
// # Bagimli Oldugu Katman: Service | Repository | Model | Tool | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactActivityFeed;
import com.turkuazlabs.telefonrehberi.models.ContactActivityFilter;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.models.ContactReminderSummary;
import com.turkuazlabs.telefonrehberi.models.ImportantDate;
import com.turkuazlabs.telefonrehberi.models.ImportantDateType;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;
import com.turkuazlabs.telefonrehberi.models.DashboardSummary;
import com.turkuazlabs.telefonrehberi.models.HistoryAction;
import com.turkuazlabs.telefonrehberi.models.HistoryEntry;
import com.turkuazlabs.telefonrehberi.repositories.ContactHistoryRepository;
import com.turkuazlabs.telefonrehberi.repositories.ContactRepository;
import com.turkuazlabs.telefonrehberi.tools.ContactSnapshotCodec;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ContactService {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MIN_CONTACT_VALUE_LENGTH = 3;
    private static final Pattern DATE_PATTERN = Pattern.compile("^(?:\\d{4}|--)-\\d{2}-\\d{2}$");

    private final ContactRepository repository;
    private final ContactHistoryRepository historyRepository;
    private final ContactSnapshotCodec snapshotCodec;

    public ContactService(
            ContactRepository repository,
            ContactHistoryRepository historyRepository,
            ContactSnapshotCodec snapshotCodec
    ) {
        this.repository = repository;
        this.historyRepository = historyRepository;
        this.snapshotCodec = snapshotCodec;
    }

    public Contact addContact(ContactDraft input) {
        Contact contact = repository.insert(normalizeDraft(input));
        historyRepository.add(contact.id(), HistoryAction.CREATE.name(), contact.name(), snapshotCodec.encode(contact));
        return contact;
    }

    public Contact addImportedContact(ContactDraft input) {
        Contact contact = repository.insert(normalizeDraft(input));
        historyRepository.add(contact.id(), HistoryAction.IMPORT.name(), contact.name(), snapshotCodec.encode(contact));
        return contact;
    }

    public List<Contact> listContacts() {
        return repository.findAll();
    }

    public List<Contact> listTrash() {
        return repository.findTrash();
    }

    public Optional<Contact> findById(long id) {
        return repository.findById(id);
    }

    public DashboardSummary dashboardSummary() {
        List<Contact> contacts = repository.findAll();
        String todaySuffix = "-" + String.format("%02d-%02d", LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        int favorites = (int) contacts.stream().filter(Contact::favorite).count();
        int birthdays = (int) contacts.stream()
                .map(Contact::birthday)
                .filter(value -> value != null && value.endsWith(todaySuffix))
                .count();
        int followUpsDue = (int) contacts.stream().filter(contact -> hasDueReminder(contact, LocalDate.now())).count();
        int companies = (int) contacts.stream()
                .map(Contact::company)
                .filter(value -> value != null && !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .distinct()
                .count();
        int cities = (int) contacts.stream()
                .map(Contact::city)
                .filter(value -> value != null && !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .distinct()
                .count();
        return new DashboardSummary(contacts.size(), favorites, birthdays, followUpsDue, companies, cities);
    }

    public Contact updateContact(long id, ContactDraft input) {
        Contact current = requireContact(id);
        historyRepository.add(current.id(), HistoryAction.UPDATE.name(), current.name(), snapshotCodec.encode(current));
        return repository.update(id, normalizeDraft(input));
    }

    public void deleteContact(long id) {
        Contact current = requireContact(id);
        historyRepository.add(current.id(), HistoryAction.DELETE.name(), current.name(), snapshotCodec.encode(current));
        if (!repository.softDelete(id)) {
            throw new IllegalArgumentException(Messages.ERROR_CONTACT_NOT_FOUND);
        }
    }

    public int setFavorites(List<Long> contactIds, boolean favorite) {
        int changed = 0;
        for (long id : normalizeContactIds(contactIds)) {
            Contact current = requireContact(id);
            if (current.favorite() == favorite) continue;
            historyRepository.add(current.id(), HistoryAction.UPDATE.name(), current.name(), snapshotCodec.encode(current));
            repository.update(id, normalizeDraft(current.toDraft().withFavorite(favorite)));
            changed++;
        }
        return changed;
    }

    public int setCompanies(List<Long> contactIds, String company) {
        String normalized = normalizeOptional(company);
        int changed = 0;
        for (long id : normalizeContactIds(contactIds)) {
            Contact current = requireContact(id);
            if (normalizeOptional(current.company()).equals(normalized)) continue;
            historyRepository.add(current.id(), HistoryAction.UPDATE.name(), current.name(), snapshotCodec.encode(current));
            repository.update(id, normalizeDraft(current.toDraft().withCompany(normalized)));
            changed++;
        }
        return changed;
    }

    public int setCategories(List<Long> contactIds, String category) {
        String normalized = normalizeOptional(category);
        int changed = 0;
        for (long id : normalizeContactIds(contactIds)) {
            Contact current = requireContact(id);
            if (normalizeOptional(current.category()).equals(normalized)) continue;
            historyRepository.add(current.id(), HistoryAction.UPDATE.name(), current.name(), snapshotCodec.encode(current));
            repository.update(id, normalizeDraft(current.toDraft().withCategory(normalized)));
            changed++;
        }
        return changed;
    }

    public List<Contact> requireContacts(List<Long> contactIds) {
        return normalizeContactIds(contactIds).stream().map(this::requireContact).toList();
    }

    public int deleteContacts(List<Long> contactIds) {
        int deleted = 0;
        for (long id : normalizeContactIds(contactIds)) {
            deleteContact(id);
            deleted++;
        }
        return deleted;
    }

    public int restoreBulkSnapshots(List<Contact> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) return 0;
        int restored = 0;
        for (Contact snapshot : snapshots) {
            if (snapshot == null) continue;
            Contact current = requireContact(snapshot.id());
            historyRepository.add(current.id(), HistoryAction.UPDATE.name(), current.name(), snapshotCodec.encode(current));
            repository.update(snapshot.id(), normalizeDraft(snapshot.toDraft()));
            restored++;
        }
        return restored;
    }

    public int restoreTrashContacts(List<Long> contactIds) {
        int restored = 0;
        for (long id : normalizeContactIds(contactIds)) {
            Contact current = requireContact(id);
            historyRepository.add(current.id(), HistoryAction.RESTORE.name(), current.name(), snapshotCodec.encode(current));
            if (repository.restore(id)) restored++;
        }
        return restored;
    }

    public void restoreTrashContact(long id) {
        Contact current = requireContact(id);
        historyRepository.add(current.id(), HistoryAction.RESTORE.name(), current.name(), snapshotCodec.encode(current));
        if (!repository.restore(id)) {
            throw new IllegalArgumentException(Messages.ERROR_CONTACT_NOT_FOUND);
        }
    }

    public void permanentlyDeleteContact(long id) {
        requireContact(id);
        if (!repository.hardDelete(id)) {
            throw new IllegalArgumentException(Messages.ERROR_CONTACT_NOT_FOUND);
        }
    }

    public List<HistoryEntry> listHistory() {
        return historyRepository.findRecent(AppConfig.HISTORY_LIMIT);
    }

    public ContactActivityFeed contactActivity(long contactId, ContactActivityFilter filter) {
        if (contactId <= 0L) {
            return ContactActivityFeed.empty();
        }
        ContactActivityFilter resolvedFilter = filter == null ? ContactActivityFilter.ALL : filter;
        List<HistoryEntry> allEntries = historyRepository.findByContactId(contactId);

        int changeCount = (int) allEntries.stream().filter(entry -> isChangeAction(entry.action())).count();
        int transferCount = (int) allEntries.stream().filter(entry -> isTransferAction(entry.action())).count();
        int restoreCount = (int) allEntries.stream().filter(entry -> isRestoreAction(entry.action())).count();

        List<HistoryEntry> visibleEntries = allEntries.stream()
                .filter(entry -> matchesActivityFilter(entry.action(), resolvedFilter))
                .limit(AppConfig.CONTACT_ACTIVITY_LIMIT)
                .toList();

        return new ContactActivityFeed(
                visibleEntries,
                allEntries.size(),
                changeCount,
                transferCount,
                restoreCount
        );
    }

    public ContactReminderSummary reminderSummary(long contactId) {
        if (contactId <= 0L) return ContactReminderSummary.empty();
        Contact contact = requireContact(contactId);
        LocalDate today = LocalDate.now();
        String nextContactDate = "";
        boolean keepInTouchDue = false;
        if (contact.keepInTouchInterval().enabled() && !contact.lastContactedDate().isBlank()) {
            LocalDate lastContacted = LocalDate.parse(contact.lastContactedDate());
            LocalDate next = lastContacted.plusDays(contact.keepInTouchInterval().days());
            nextContactDate = next.toString();
            keepInTouchDue = !next.isAfter(today);
        }

        int active = 0;
        if (!contact.birthday().isBlank() && contact.birthdayReminderLeadTime() != ReminderLeadTime.DISABLED
                && reminderWindowActive(contact.birthday(), contact.birthdayReminderLeadTime(), today)) {
            active++;
        }
        for (ImportantDate value : contact.importantDates()) {
            if (value.reminderLeadTime() != ReminderLeadTime.DISABLED
                    && reminderWindowActive(value.dateValue(), value.reminderLeadTime(), today)) {
                active++;
            }
        }
        return new ContactReminderSummary(nextContactDate, keepInTouchDue, active);
    }

    public Contact restoreHistory(long historyId) {
        HistoryEntry entry = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException(Messages.ERROR_HISTORY_NOT_FOUND));
        Contact current = repository.findById(entry.contactId()).orElse(null);
        if (current != null) {
            historyRepository.add(current.id(), HistoryAction.HISTORY_RESTORE.name(), current.name(), snapshotCodec.encode(current));
            repository.restore(current.id());
            return repository.update(current.id(), normalizeDraft(snapshotCodec.decode(entry.snapshot())));
        }
        Contact restored = repository.insert(normalizeDraft(snapshotCodec.decode(entry.snapshot())));
        historyRepository.add(restored.id(), HistoryAction.HISTORY_RESTORE.name(), restored.name(), snapshotCodec.encode(restored));
        return restored;
    }

    public synchronized Contact importFromMobile(ContactDraft input, String sourceDeviceId, String externalId, String syncUuid) {
        String safeDeviceId = normalizeRequired(sourceDeviceId, Messages.ERROR_DEVICE_ID_REQUIRED);
        String safeExternalId = normalizeRequired(externalId, Messages.ERROR_EXTERNAL_ID_REQUIRED);
        ContactDraft normalized = normalizeDraft(input);
        repository.findExternalTarget(normalized, safeDeviceId, safeExternalId, syncUuid).ifPresent(contact ->
                historyRepository.add(
                        contact.id(), HistoryAction.MOBILE_SYNC.name(), contact.name(), snapshotCodec.encode(contact)
                )
        );
        return repository.upsertExternal(normalized, safeDeviceId, safeExternalId, syncUuid);
    }

    void transferSyncIdentitiesForMerge(long duplicateId, long primaryId) {
        repository.transferSyncIdentities(duplicateId, primaryId);
    }

    Contact replaceForMerge(long id, ContactDraft draft) {
        Contact current = requireContact(id);
        historyRepository.add(current.id(), HistoryAction.MERGE.name(), current.name(), snapshotCodec.encode(current));
        return repository.update(id, normalizeDraft(draft));
    }

    void softDeleteForMerge(long id) {
        Contact current = requireContact(id);
        historyRepository.add(current.id(), HistoryAction.MERGE.name(), current.name(), snapshotCodec.encode(current));
        if (!repository.softDelete(id)) {
            throw new IllegalArgumentException(Messages.ERROR_CONTACT_NOT_FOUND);
        }
    }

    private boolean matchesActivityFilter(String actionValue, ContactActivityFilter filter) {
        return switch (filter) {
            case ALL -> true;
            case CHANGES -> isChangeAction(actionValue);
            case TRANSFER -> isTransferAction(actionValue);
            case RESTORE -> isRestoreAction(actionValue);
        };
    }

    private boolean isChangeAction(String actionValue) {
        HistoryAction action = parseHistoryAction(actionValue);
        return action == HistoryAction.CREATE
                || action == HistoryAction.UPDATE
                || action == HistoryAction.DELETE
                || action == HistoryAction.MERGE;
    }

    private boolean isTransferAction(String actionValue) {
        HistoryAction action = parseHistoryAction(actionValue);
        return action == HistoryAction.MOBILE_SYNC || action == HistoryAction.IMPORT;
    }

    private boolean isRestoreAction(String actionValue) {
        HistoryAction action = parseHistoryAction(actionValue);
        return action == HistoryAction.RESTORE || action == HistoryAction.HISTORY_RESTORE;
    }

    private HistoryAction parseHistoryAction(String actionValue) {
        if (actionValue == null || actionValue.isBlank()) {
            return null;
        }
        try {
            return HistoryAction.valueOf(actionValue);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private List<Long> normalizeContactIds(List<Long> contactIds) {
        if (contactIds == null || contactIds.isEmpty()) {
            throw new IllegalArgumentException(Messages.SELECT_CONTACT);
        }
        return contactIds.stream()
                .filter(id -> id != null && id > 0L)
                .distinct()
                .toList();
    }

    private Contact requireContact(long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException(Messages.ERROR_CONTACT_NOT_FOUND));
    }

    private ContactDraft normalizeDraft(ContactDraft input) {
        if (input == null) {
            throw new IllegalArgumentException(Messages.ERROR_CONTACT_INPUT_REQUIRED);
        }
        String name = requireName(input.name());
        List<ContactMethod> phones = normalizeMethods(input.phones(), ContactMethod.PHONE, Messages.ERROR_PHONE_MIN_LENGTH);
        List<ContactMethod> emails = normalizeMethods(input.emails(), ContactMethod.EMAIL, Messages.ERROR_EMAIL_MIN_LENGTH);
        if (phones.isEmpty() && emails.isEmpty()) {
            throw new IllegalArgumentException(Messages.ERROR_PHONE_OR_EMAIL_REQUIRED);
        }
        String birthday = normalizeOptional(input.birthday());
        validateAnnualDate(birthday, Messages.ERROR_BIRTHDAY_FORMAT);
        ReminderLeadTime birthdayReminder = input.birthdayReminderLeadTime() == null
                ? ReminderLeadTime.DISABLED : input.birthdayReminderLeadTime();
        if (birthday.isEmpty() && birthdayReminder != ReminderLeadTime.DISABLED) {
            throw new IllegalArgumentException(Messages.ERROR_BIRTHDAY_REMINDER_REQUIRES_DATE);
        }
        List<ImportantDate> importantDates = normalizeImportantDates(input.importantDates());
        KeepInTouchInterval keepInTouch = input.keepInTouchInterval() == null
                ? KeepInTouchInterval.DISABLED : input.keepInTouchInterval();
        String lastContacted = normalizeOptional(input.lastContactedDate());
        if (!lastContacted.isEmpty()) validateFullDate(lastContacted, Messages.ERROR_LAST_CONTACTED_FORMAT);
        if (keepInTouch.enabled() && lastContacted.isEmpty()) {
            throw new IllegalArgumentException(Messages.ERROR_KEEP_IN_TOUCH_REQUIRES_DATE);
        }
        String phone = valueAt(phones, 0);
        String phoneSecondary = valueAt(phones, 1);
        String phoneWork = firstByLabel(phones, ContactMethod.LABEL_WORK, 2);
        String email = valueAt(emails, 0);
        String emailSecondary = valueAt(emails, 1);
        return new ContactDraft(
                name, phone, phoneSecondary, phoneWork, email, emailSecondary,
                normalizeOptional(input.company()), normalizeOptional(input.jobTitle()), birthday,
                normalizeOptional(input.website()), normalizeOptional(input.category()), normalizeOptional(input.address()),
                normalizeOptional(input.city()), normalizeOptional(input.district()), normalizeOptional(input.postalCode()),
                normalizeOptional(input.country()), normalizeOptional(input.notes()), input.favorite(), phones, emails, input.photo(),
                birthdayReminder, importantDates, keepInTouch, lastContacted
        );
    }

    private List<ImportantDate> normalizeImportantDates(List<ImportantDate> input) {
        if (input == null || input.isEmpty()) return List.of();
        List<ImportantDate> result = new ArrayList<>();
        for (ImportantDate value : input) {
            if (value == null) continue;
            String dateValue = normalizeOptional(value.dateValue());
            if (dateValue.isEmpty()) continue;
            validateAnnualDate(dateValue, Messages.ERROR_IMPORTANT_DATE_FORMAT);
            ImportantDateType type = value.type() == null ? ImportantDateType.CUSTOM : value.type();
            String label = normalizeOptional(value.label());
            if (label.isEmpty()) {
                label = type == ImportantDateType.ANNIVERSARY
                        ? Messages.IMPORTANT_DATE_DEFAULT_ANNIVERSARY
                        : Messages.IMPORTANT_DATE_DEFAULT_CUSTOM;
            }
            ReminderLeadTime reminder = value.reminderLeadTime() == null
                    ? ReminderLeadTime.DISABLED : value.reminderLeadTime();
            result.add(new ImportantDate(type, label, dateValue, reminder, result.size()));
        }
        return List.copyOf(result);
    }

    private void validateAnnualDate(String value, String message) {
        if (value == null || value.isEmpty()) return;
        if (!DATE_PATTERN.matcher(value).matches()) throw new IllegalArgumentException(message);
        try {
            if (value.startsWith("--")) MonthDay.parse(value);
            else LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateFullDate(String value, String message) {
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(message);
        }
    }

    private boolean hasDueReminder(Contact contact, LocalDate today) {
        if (contact.keepInTouchInterval().enabled() && !contact.lastContactedDate().isBlank()) {
            LocalDate nextContact = LocalDate.parse(contact.lastContactedDate()).plusDays(contact.keepInTouchInterval().days());
            if (!nextContact.isAfter(today)) return true;
        }
        if (!contact.birthday().isBlank()
                && contact.birthdayReminderLeadTime() != ReminderLeadTime.DISABLED
                && reminderWindowActive(contact.birthday(), contact.birthdayReminderLeadTime(), today)) {
            return true;
        }
        for (ImportantDate value : contact.importantDates()) {
            if (value.reminderLeadTime() != ReminderLeadTime.DISABLED
                    && reminderWindowActive(value.dateValue(), value.reminderLeadTime(), today)) {
                return true;
            }
        }
        return false;
    }

    private boolean reminderWindowActive(String dateValue, ReminderLeadTime leadTime, LocalDate today) {
        LocalDate occurrence = nextAnnualOccurrence(dateValue, today);
        LocalDate windowStart = occurrence.minusDays(Math.max(0, leadTime.daysBefore()));
        return !today.isBefore(windowStart) && !today.isAfter(occurrence);
    }

    private LocalDate nextAnnualOccurrence(String dateValue, LocalDate today) {
        MonthDay monthDay = dateValue.startsWith("--")
                ? MonthDay.parse(dateValue)
                : MonthDay.from(LocalDate.parse(dateValue));
        LocalDate candidate = monthDay.atYear(today.getYear());
        if (candidate.isBefore(today)) candidate = monthDay.atYear(today.getYear() + 1);
        return candidate;
    }

    private List<ContactMethod> normalizeMethods(List<ContactMethod> input, String kind, String lengthMessage) {
        List<ContactMethod> result = new ArrayList<>();
        boolean primaryAssigned = false;
        if (input != null) {
            for (ContactMethod method : input) {
                if (method == null || !kind.equals(method.kind())) continue;
                String value = normalizeOptional(method.value());
                if (value.isEmpty()) continue;
                if (value.length() < MIN_CONTACT_VALUE_LENGTH) throw new IllegalArgumentException(lengthMessage);
                String label = normalizeOptional(method.label());
                if (label.isEmpty()) label = ContactMethod.PHONE.equals(kind) ? ContactMethod.LABEL_OTHER : ContactMethod.LABEL_EMAIL;
                boolean primary = method.primary() && !primaryAssigned;
                if (primary) primaryAssigned = true;
                result.add(new ContactMethod(kind, label, value, primary, result.size()));
            }
        }
        if (!result.isEmpty() && !primaryAssigned) {
            ContactMethod first = result.get(0);
            result.set(0, new ContactMethod(first.kind(), first.label(), first.value(), true, 0));
        }
        return List.copyOf(result);
    }

    private String valueAt(List<ContactMethod> values, int index) {
        return index >= 0 && index < values.size() ? values.get(index).value() : "";
    }

    private String firstByLabel(List<ContactMethod> values, String label, int fallbackIndex) {
        String normalizedLabel = label.toLowerCase(Locale.ROOT);
        for (ContactMethod method : values) {
            if (method.label().toLowerCase(Locale.ROOT).equals(normalizedLabel)) return method.value();
        }
        return valueAt(values, fallbackIndex);
    }

    private String requireName(String value) {
        String normalized = normalizeRequired(value, Messages.ERROR_NAME_REQUIRED);
        if (normalized.length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException(Messages.ERROR_NAME_MIN_LENGTH);
        }
        return normalized;
    }

    private String normalizeRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String normalizeOptional(String value) {
        return value == null ? "" : value.trim();
    }
}
