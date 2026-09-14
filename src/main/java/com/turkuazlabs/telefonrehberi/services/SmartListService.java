// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/SmartListService.java
// # 📌 Amac: Kural tabanli akilli listeleri olusturur ve kisiler uzerinde calistirir.
// # 📌 Service - Java
// # Version: 2.4.0
// # Aciklama: Desteklenen alan/operator kurallarini dogrular ve dinamik kisi filtrelemesi yapar.
// # Bagimli Oldugu Katman: Service | Repository | Model | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.SmartList;
import com.turkuazlabs.telefonrehberi.models.SmartListDraft;
import com.turkuazlabs.telefonrehberi.repositories.SmartListRepository;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class SmartListService {
    public static final String OP_CONTAINS = "contains";
    public static final String OP_EQUALS = "equals";
    public static final String OP_IS_EMPTY = "is_empty";
    public static final String OP_NOT_EMPTY = "not_empty";
    public static final String OP_TRUE = "true";
    private static final Set<String> FIELDS = Set.of("name", "phone", "email", "company", "job_title", "city", "country", "category", "notes", "birthday", "favorite");
    private static final Set<String> OPERATORS = Set.of(OP_CONTAINS, OP_EQUALS, OP_IS_EMPTY, OP_NOT_EMPTY, OP_TRUE);

    private final SmartListRepository repository;
    private final ContactService contactService;

    public SmartListService(SmartListRepository repository, ContactService contactService) {
        this.repository = repository;
        this.contactService = contactService;
    }

    public List<SmartList> listSmartLists() { return repository.findAll(); }
    public void create(SmartListDraft input) {
        if (input == null || blank(input.name())) throw new IllegalArgumentException(Messages.ERROR_SMART_LIST_NAME_REQUIRED);
        String field = safe(input.field()).toLowerCase(Locale.ROOT);
        String operator = safe(input.operator()).toLowerCase(Locale.ROOT);
        if (!FIELDS.contains(field)) throw new IllegalArgumentException(Messages.ERROR_SMART_LIST_FIELD);
        if (!OPERATORS.contains(operator)) throw new IllegalArgumentException(Messages.ERROR_SMART_LIST_OPERATOR);
        repository.insert(new SmartListDraft(input.name().trim(), field, operator, safe(input.value()).trim()));
    }
    public void delete(long id) { if (!repository.delete(id)) throw new IllegalArgumentException(Messages.ERROR_SMART_LIST_NOT_FOUND); }
    public List<Contact> evaluate(SmartList rule) {
        if (rule == null) return List.of();
        return contactService.listContacts().stream().filter(contact -> matches(contact, rule)).toList();
    }

    private boolean matches(Contact c, SmartList r) {
        if ("favorite".equals(r.field())) return OP_TRUE.equals(r.operator()) && c.favorite();
        String actual = fieldValue(c, r.field());
        String expected = safe(r.value());
        return switch (r.operator()) {
            case OP_EQUALS -> actual.equalsIgnoreCase(expected);
            case OP_IS_EMPTY -> actual.isBlank();
            case OP_NOT_EMPTY -> !actual.isBlank();
            case OP_CONTAINS -> actual.toLowerCase(Locale.ROOT).contains(expected.toLowerCase(Locale.ROOT));
            default -> false;
        };
    }
    private String fieldValue(Contact c, String field) {
        return safe(switch (field) {
            case "name" -> c.name();
            case "phone" -> c.phone();
            case "email" -> c.email();
            case "company" -> c.company();
            case "job_title" -> c.jobTitle();
            case "city" -> c.city();
            case "country" -> c.country();
            case "category" -> c.category();
            case "notes" -> c.notes();
            case "birthday" -> c.birthday();
            default -> "";
        });
    }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private String safe(String value) { return value == null ? "" : value; }
}
