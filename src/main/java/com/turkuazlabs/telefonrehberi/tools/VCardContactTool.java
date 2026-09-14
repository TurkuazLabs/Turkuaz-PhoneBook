// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/VCardContactTool.java
// # 📌 Amac: vCard 3.0/4.0 kisi dosyalarini okur ve vCard 4.0 dosyasi uretir.
// # 📌 Tool - Java
// # Version: 2.33.0
// # Aciklama: Iletisim satirlari, profil fotografi, onemli tarihler ve Turkuaz hatirlatma alanlarini vCard 4.0 ile tasir.
// # Bagimli Oldugu Katman: Tool | Model | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.models.ImportantDate;
import com.turkuazlabs.telefonrehberi.models.ImportantDateType;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

public final class VCardContactTool {
    private static final String BEGIN = "BEGIN:VCARD";
    private static final String END = "END:VCARD";
    private static final String VERSION = "VERSION:4.0";
    private static final String PROP_BDAY_REMINDER = "X-TURKUAZ-BDAY-REMINDER";
    private static final String PROP_IMPORTANT_DATE = "X-TURKUAZ-IMPORTANT-DATE";
    private static final String PROP_KEEP_IN_TOUCH = "X-TURKUAZ-KEEP-IN-TOUCH";
    private static final String PROP_LAST_CONTACTED = "X-TURKUAZ-LAST-CONTACTED";

    public void write(Path path, List<Contact> contacts) {
        List<String> output = new ArrayList<>();
        for (Contact c : contacts) {
            output.add(BEGIN);
            output.add(VERSION);
            output.add("FN:" + escape(c.name()));
            for (ContactMethod method : c.phones()) {
                output.add("TEL;TYPE=" + typeToken(method.label(), "voice") + ";X-TURKUAZ-LABEL=" + escapeParam(method.label()) + ":" + escape(method.value()));
            }
            for (ContactMethod method : c.emails()) {
                output.add("EMAIL;TYPE=" + typeToken(method.label(), "internet") + ";X-TURKUAZ-LABEL=" + escapeParam(method.label()) + ":" + escape(method.value()));
            }
            add(output, "ORG:", c.company());
            add(output, "TITLE:", c.jobTitle());
            add(output, "BDAY:", c.birthday());
            output.add(PROP_BDAY_REMINDER + ":" + c.birthdayReminderLeadTime().daysBefore());
            for (ImportantDate value : c.importantDates()) {
                output.add(PROP_IMPORTANT_DATE
                        + ";TYPE=" + value.type().name()
                        + ";X-TURKUAZ-LABEL=" + escapeParam(value.label())
                        + ";X-TURKUAZ-REMIND=" + value.reminderLeadTime().daysBefore()
                        + ":" + escape(value.dateValue()));
            }
            output.add(PROP_KEEP_IN_TOUCH + ":" + c.keepInTouchInterval().days());
            add(output, PROP_LAST_CONTACTED + ":", c.lastContactedDate());
            add(output, "URL:", c.website());
            add(output, "CATEGORIES:", c.category());
            if (!allBlank(c.address(), c.district(), c.city(), c.postalCode(), c.country())) {
                output.add("ADR;TYPE=home:;;" + escape(c.address()) + ";" + escape(c.city()) + ";" + escape(c.district()) + ";" + escape(c.postalCode()) + ";" + escape(c.country()));
            }
            add(output, "NOTE:", c.notes());
            if (c.photo() != null && c.photo().length > 0) {
                output.add("PHOTO:data:image/png;base64," + Base64.getEncoder().encodeToString(c.photo()));
            }
            output.add(END);
        }
        try { Files.write(path, output, StandardCharsets.UTF_8); }
        catch (IOException exception) { throw new IllegalStateException(Messages.ERROR_EXPORT_FILE, exception); }
    }

    public List<ContactDraft> read(Path path) {
        try {
            List<String> lines = unfold(Files.readAllLines(path, StandardCharsets.UTF_8));
            List<ContactDraft> result = new ArrayList<>();
            CardBuilder card = null;
            for (String raw : lines) {
                String line = raw.trim();
                if (BEGIN.equalsIgnoreCase(line)) { card = new CardBuilder(); continue; }
                if (END.equalsIgnoreCase(line)) {
                    if (card != null) result.add(card.build());
                    card = null;
                    continue;
                }
                if (card != null) card.accept(line);
            }
            return result;
        } catch (IOException exception) { throw new IllegalStateException(Messages.ERROR_IMPORT_FILE, exception); }
    }

    private List<String> unfold(List<String> input) {
        List<String> output = new ArrayList<>();
        for (String line : input) {
            if (!output.isEmpty() && (line.startsWith(" ") || line.startsWith("\t"))) {
                int last = output.size() - 1;
                output.set(last, output.get(last) + line.substring(1));
            } else output.add(line);
        }
        return output;
    }

    private void add(List<String> output, String prefix, String value) { if (!blank(value)) output.add(prefix + escape(value)); }
    private boolean allBlank(String... values) { for (String value : values) if (!blank(value)) return false; return true; }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private String escape(String value) { return safe(value).replace("\\", "\\\\").replace("\n", "\\n").replace(",", "\\,").replace(";", "\\;"); }
    private String escapeParam(String value) { return safe(value).replace(";", "").replace(":", "").replace(",", " "); }
    private static String unescape(String value) { return safe(value).replace("\\n", "\n").replace("\\,", ",").replace("\\;", ";").replace("\\\\", "\\"); }
    private static String safe(String value) { return value == null ? "" : value; }

    private String typeToken(String label, String fallback) {
        String value = safe(label).toLowerCase(Locale.ROOT);
        if (value.contains("cep") || value.contains("mobil")) return "cell";
        if (value.equals("is") || value.contains("work")) return "work";
        if (value.contains("ev") || value.contains("home")) return "home";
        return fallback;
    }

    private static final class CardBuilder {
        private String name = "";
        private final List<ContactMethod> phones = new ArrayList<>();
        private final List<ContactMethod> emails = new ArrayList<>();
        private String company = "";
        private String jobTitle = "";
        private String birthday = "";
        private ReminderLeadTime birthdayReminder = ReminderLeadTime.DISABLED;
        private final List<ImportantDate> importantDates = new ArrayList<>();
        private KeepInTouchInterval keepInTouch = KeepInTouchInterval.DISABLED;
        private String lastContactedDate = "";
        private String website = "";
        private String category = "";
        private String address = "";
        private String city = "";
        private String district = "";
        private String postalCode = "";
        private String country = "";
        private String notes = "";
        private byte[] photo;

        private void accept(String line) {
            int colon = line.indexOf(':');
            if (colon < 1) return;
            String key = line.substring(0, colon);
            String upperKey = key.toUpperCase(Locale.ROOT);
            String rawValue = line.substring(colon + 1);
            if (upperKey.startsWith("ADR")) { parseAddress(rawValue); return; }
            if (upperKey.startsWith("PHOTO")) { photo = parsePhoto(rawValue); return; }
            if (upperKey.equals(PROP_BDAY_REMINDER)) { birthdayReminder = ReminderLeadTime.fromDays(parseInt(rawValue, ReminderLeadTime.DISABLED.daysBefore())); return; }
            if (upperKey.startsWith(PROP_IMPORTANT_DATE)) { parseImportantDate(key, rawValue); return; }
            if (upperKey.equals(PROP_KEEP_IN_TOUCH)) { keepInTouch = KeepInTouchInterval.fromDays(parseInt(rawValue, KeepInTouchInterval.DISABLED.days())); return; }
            if (upperKey.equals(PROP_LAST_CONTACTED)) { lastContactedDate = unescape(rawValue); return; }
            String value = unescape(rawValue);
            if (upperKey.equals("FN") || upperKey.startsWith("FN;")) name = value;
            else if (upperKey.startsWith("TEL")) addMethod(phones, ContactMethod.PHONE, labelFromKey(key, ContactMethod.LABEL_PHONE), value);
            else if (upperKey.startsWith("EMAIL")) addMethod(emails, ContactMethod.EMAIL, labelFromKey(key, ContactMethod.LABEL_EMAIL), value);
            else if (upperKey.equals("ORG") || upperKey.startsWith("ORG;")) company = value;
            else if (upperKey.equals("TITLE") || upperKey.startsWith("TITLE;")) jobTitle = value;
            else if (upperKey.equals("BDAY") || upperKey.startsWith("BDAY;")) birthday = value;
            else if (upperKey.equals("URL") || upperKey.startsWith("URL;")) website = value;
            else if (upperKey.equals("CATEGORIES") || upperKey.startsWith("CATEGORIES;")) category = value;
            else if (upperKey.equals("NOTE") || upperKey.startsWith("NOTE;")) notes = value;
        }

        private void parseImportantDate(String key, String rawValue) {
            ImportantDateType type = ImportantDateType.CUSTOM;
            String label = "";
            int reminderDays = ReminderLeadTime.DISABLED.daysBefore();
            for (String part : key.split(";")) {
                int eq = part.indexOf('=');
                if (eq < 1) continue;
                String paramName = part.substring(0, eq).trim();
                String paramValue = part.substring(eq + 1).trim();
                if ("TYPE".equalsIgnoreCase(paramName)) {
                    try { type = ImportantDateType.valueOf(paramValue.toUpperCase(Locale.ROOT)); }
                    catch (IllegalArgumentException ignored) { type = ImportantDateType.CUSTOM; }
                } else if ("X-TURKUAZ-LABEL".equalsIgnoreCase(paramName)) {
                    label = paramValue;
                } else if ("X-TURKUAZ-REMIND".equalsIgnoreCase(paramName)) {
                    reminderDays = parseInt(paramValue, ReminderLeadTime.DISABLED.daysBefore());
                }
            }
            String dateValue = unescape(rawValue);
            if (!dateValue.isBlank()) {
                importantDates.add(new ImportantDate(type, label, dateValue, ReminderLeadTime.fromDays(reminderDays), importantDates.size()));
            }
        }

        private int parseInt(String value, int fallback) {
            try { return Integer.parseInt(value.trim()); } catch (NumberFormatException exception) { return fallback; }
        }

        private void addMethod(List<ContactMethod> out, String kind, String label, String value) {
            if (!value.isBlank()) out.add(new ContactMethod(kind, label, value, out.isEmpty(), out.size()));
        }

        private String labelFromKey(String key, String fallback) {
            for (String part : key.split(";")) {
                int eq = part.indexOf('=');
                if (eq < 1) continue;
                String name = part.substring(0, eq).trim();
                String value = part.substring(eq + 1).trim();
                if ("X-TURKUAZ-LABEL".equalsIgnoreCase(name) && !value.isBlank()) return value;
                if ("TYPE".equalsIgnoreCase(name)) {
                    String lower = value.toLowerCase(Locale.ROOT);
                    if (lower.contains("cell")) return ContactMethod.LABEL_MOBILE;
                    if (lower.contains("work")) return ContactMethod.LABEL_WORK;
                    if (lower.contains("home")) return ContactMethod.LABEL_HOME;
                }
            }
            return fallback;
        }

        private byte[] parsePhoto(String value) {
            try {
                String data = value;
                int comma = data.indexOf(',');
                if (data.startsWith("data:") && comma >= 0) data = data.substring(comma + 1);
                return Base64.getDecoder().decode(data);
            } catch (IllegalArgumentException ignored) { return null; }
        }

        private void parseAddress(String value) {
            List<String> parts = splitStructured(value);
            if (parts.size() > 2) address = unescape(parts.get(2));
            if (parts.size() > 3) city = unescape(parts.get(3));
            if (parts.size() > 4) district = unescape(parts.get(4));
            if (parts.size() > 5) postalCode = unescape(parts.get(5));
            if (parts.size() > 6) country = unescape(parts.get(6));
        }

        private List<String> splitStructured(String value) {
            List<String> parts = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            boolean escaped = false;
            for (int i = 0; i < value.length(); i++) {
                char ch = value.charAt(i);
                if (escaped) { current.append('\\').append(ch); escaped = false; }
                else if (ch == '\\') escaped = true;
                else if (ch == ';') { parts.add(current.toString()); current.setLength(0); }
                else current.append(ch);
            }
            if (escaped) current.append('\\');
            parts.add(current.toString());
            return parts;
        }

        private ContactDraft build() {
            ContactDraft legacy = new ContactDraft(
                    name, item(phones, 0), item(phones, 1), itemByLabel(phones, ContactMethod.LABEL_WORK), item(emails, 0), item(emails, 1),
                    company, jobTitle, birthday, website, category, address, city, district, postalCode, country, notes, false
            );
            return new ContactDraft(
                    legacy.name(), legacy.phone(), legacy.phoneSecondary(), legacy.phoneWork(), legacy.email(), legacy.emailSecondary(),
                    legacy.company(), legacy.jobTitle(), legacy.birthday(), legacy.website(), legacy.category(), legacy.address(),
                    legacy.city(), legacy.district(), legacy.postalCode(), legacy.country(), legacy.notes(), false,
                    phones, emails, photo, birthdayReminder, importantDates, keepInTouch, lastContactedDate
            );
        }

        private String item(List<ContactMethod> values, int index) { return index < values.size() ? values.get(index).value() : ""; }
        private String itemByLabel(List<ContactMethod> values, String label) {
            return values.stream().filter(v -> label.equalsIgnoreCase(v.label())).map(ContactMethod::value).findFirst().orElse("");
        }
    }
}
