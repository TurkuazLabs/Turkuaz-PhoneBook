// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/ContactSnapshotCodec.java
// # 📌 Amac: Kisi snapshotlarini history tablosu icin guvenli metne cevirir ve geri okur.
// # 📌 Tool - Java
// # Version: 2.33.0
// # Aciklama: Kisi alanlari, iletisim satirlari, profil fotografi, onemli tarihler ve hatirlatma ayarlarini URL encoded snapshot olarak saklar.
// # Bagimli Oldugu Katman: Tool | Model
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ContactSnapshotCodec {
    private final ContactMethodCodec methodCodec = new ContactMethodCodec();
    private final ImportantDateCodec importantDateCodec = new ImportantDateCodec();

    public String encode(Contact contact) { return encode(contact.toDraft()); }

    public String encode(ContactDraft draft) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("name", draft.name());
        values.put("phone", draft.phone());
        values.put("phoneSecondary", draft.phoneSecondary());
        values.put("phoneWork", draft.phoneWork());
        values.put("email", draft.email());
        values.put("emailSecondary", draft.emailSecondary());
        values.put("company", draft.company());
        values.put("jobTitle", draft.jobTitle());
        values.put("birthday", draft.birthday());
        values.put("website", draft.website());
        values.put("category", draft.category());
        values.put("address", draft.address());
        values.put("city", draft.city());
        values.put("district", draft.district());
        values.put("postalCode", draft.postalCode());
        values.put("country", draft.country());
        values.put("notes", draft.notes());
        values.put("favorite", Boolean.toString(draft.favorite()));
        values.put("phones", methodCodec.encode(draft.phones()));
        values.put("emails", methodCodec.encode(draft.emails()));
        values.put("photo", draft.photo() == null ? "" : Base64.getEncoder().encodeToString(draft.photo()));
        values.put("birthdayReminderDays", Integer.toString(draft.birthdayReminderLeadTime().daysBefore()));
        values.put("importantDates", importantDateCodec.encode(draft.importantDates()));
        values.put("keepInTouchDays", Integer.toString(draft.keepInTouchInterval().days()));
        values.put("lastContactedDate", draft.lastContactedDate());
        StringBuilder output = new StringBuilder();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (output.length() > 0) output.append('&');
            output.append(encodeValue(entry.getKey())).append('=').append(encodeValue(safe(entry.getValue())));
        }
        return output.toString();
    }

    public ContactDraft decode(String snapshot) {
        Map<String, String> values = new LinkedHashMap<>();
        if (snapshot != null && !snapshot.isBlank()) {
            for (String pair : snapshot.split("&")) {
                int separator = pair.indexOf('=');
                if (separator > 0) values.put(decodeValue(pair.substring(0, separator)), decodeValue(pair.substring(separator + 1)));
            }
        }
        List<ContactMethod> phones = methodCodec.decode(value(values, "phones"));
        List<ContactMethod> emails = methodCodec.decode(value(values, "emails"));
        byte[] photo = decodePhoto(value(values, "photo"));
        ReminderLeadTime birthdayReminder = ReminderLeadTime.fromDays(intValue(values, "birthdayReminderDays", ReminderLeadTime.DISABLED.daysBefore()));
        KeepInTouchInterval keepInTouch = KeepInTouchInterval.fromDays(intValue(values, "keepInTouchDays", KeepInTouchInterval.DISABLED.days()));
        var importantDates = importantDateCodec.decode(value(values, "importantDates"));
        String lastContactedDate = value(values, "lastContactedDate");
        if (phones.isEmpty() && emails.isEmpty()) {
            return new ContactDraft(
                    value(values, "name"), value(values, "phone"), value(values, "phoneSecondary"),
                    value(values, "phoneWork"), value(values, "email"), value(values, "emailSecondary"),
                    value(values, "company"), value(values, "jobTitle"), value(values, "birthday"),
                    value(values, "website"), value(values, "category"), value(values, "address"),
                    value(values, "city"), value(values, "district"), value(values, "postalCode"),
                    value(values, "country"), value(values, "notes"), Boolean.parseBoolean(value(values, "favorite")),
                    new ContactDraft(
                            value(values, "name"), value(values, "phone"), value(values, "phoneSecondary"), value(values, "phoneWork"),
                            value(values, "email"), value(values, "emailSecondary"), value(values, "company"), value(values, "jobTitle"),
                            value(values, "birthday"), value(values, "website"), value(values, "category"), value(values, "address"),
                            value(values, "city"), value(values, "district"), value(values, "postalCode"), value(values, "country"),
                            value(values, "notes"), Boolean.parseBoolean(value(values, "favorite"))
                    ).phones(),
                    new ContactDraft(
                            value(values, "name"), value(values, "phone"), value(values, "phoneSecondary"), value(values, "phoneWork"),
                            value(values, "email"), value(values, "emailSecondary"), value(values, "company"), value(values, "jobTitle"),
                            value(values, "birthday"), value(values, "website"), value(values, "category"), value(values, "address"),
                            value(values, "city"), value(values, "district"), value(values, "postalCode"), value(values, "country"),
                            value(values, "notes"), Boolean.parseBoolean(value(values, "favorite"))
                    ).emails(),
                    photo, birthdayReminder, importantDates, keepInTouch, lastContactedDate
            );
        }
        return new ContactDraft(
                value(values, "name"), value(values, "phone"), value(values, "phoneSecondary"),
                value(values, "phoneWork"), value(values, "email"), value(values, "emailSecondary"),
                value(values, "company"), value(values, "jobTitle"), value(values, "birthday"),
                value(values, "website"), value(values, "category"), value(values, "address"),
                value(values, "city"), value(values, "district"), value(values, "postalCode"),
                value(values, "country"), value(values, "notes"), Boolean.parseBoolean(value(values, "favorite")),
                phones, emails, photo, birthdayReminder, importantDates, keepInTouch, lastContactedDate
        );
    }

    private byte[] decodePhoto(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Base64.getDecoder().decode(value); } catch (IllegalArgumentException ignored) { return null; }
    }

    private String encodeValue(String value) { return URLEncoder.encode(safe(value), StandardCharsets.UTF_8); }
    private String decodeValue(String value) { return URLDecoder.decode(value, StandardCharsets.UTF_8); }
    private String value(Map<String, String> values, String key) { return values.getOrDefault(key, ""); }
    private int intValue(Map<String, String> values, String key, int fallback) {
        try { return Integer.parseInt(value(values, key)); } catch (NumberFormatException exception) { return fallback; }
    }
    private String safe(String value) { return value == null ? "" : value; }
}
