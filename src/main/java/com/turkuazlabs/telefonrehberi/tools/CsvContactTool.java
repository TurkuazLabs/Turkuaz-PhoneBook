// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/CsvContactTool.java
// # 📌 Amac: Kisi verisini UTF-8 CSV dosyasina cevirir ve CSV dosyasindan ContactDraft listesi okur.
// # 📌 Tool - Java
// # Version: 2.33.0
// # Aciklama: RFC 4180 ile iletisim satirlari, onemli tarihler ve hatirlatma ayarlarini kayipsiz tasir.
// # Bagimli Oldugu Katman: Tool | Model | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CsvContactTool {
    private static final List<String> HEADERS = List.of(
            "Name", "Phone", "Phone 2", "Work Phone", "Email", "Email 2", "Phones", "Emails",
            "Company", "Job Title", "Birthday", "Website", "Category", "Address", "District", "City",
            "Postal Code", "Country", "Notes", "Favorite", "Birthday Reminder Days", "Important Dates",
            "Keep In Touch Days", "Last Contacted Date"
    );
    private final ContactMethodCodec methodCodec = new ContactMethodCodec();
    private final ImportantDateCodec importantDateCodec = new ImportantDateCodec();

    public void write(Path path, List<Contact> contacts) {
        try {
            List<String> lines = new ArrayList<>();
            lines.add(join(HEADERS));
            for (Contact c : contacts) {
                lines.add(join(List.of(
                        safe(c.name()), safe(c.phone()), safe(c.phoneSecondary()), safe(c.phoneWork()), safe(c.email()),
                        safe(c.emailSecondary()), methodCodec.encode(c.phones()), methodCodec.encode(c.emails()), safe(c.company()),
                        safe(c.jobTitle()), safe(c.birthday()), safe(c.website()), safe(c.category()), safe(c.address()),
                        safe(c.district()), safe(c.city()), safe(c.postalCode()), safe(c.country()), safe(c.notes()),
                        Boolean.toString(c.favorite()), Integer.toString(c.birthdayReminderLeadTime().daysBefore()),
                        importantDateCodec.encode(c.importantDates()), Integer.toString(c.keepInTouchInterval().days()),
                        safe(c.lastContactedDate())
                )));
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) { throw new IllegalStateException(Messages.ERROR_EXPORT_FILE, exception); }
    }

    public List<ContactDraft> read(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            List<List<String>> rows = parseRows(reader);
            if (rows.isEmpty()) return List.of();
            Map<String, Integer> header = headerIndex(rows.get(0));
            List<ContactDraft> result = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                List<String> row = rows.get(i);
                if (row.stream().allMatch(String::isBlank)) continue;
                result.add(toDraft(row, header));
            }
            return result;
        } catch (IOException exception) { throw new IllegalStateException(Messages.ERROR_IMPORT_FILE, exception); }
    }

    private ContactDraft toDraft(List<String> row, Map<String, Integer> h) {
        String phone = get(row, h, "phone");
        String phone2 = get(row, h, "phone 2");
        String workPhone = get(row, h, "work phone");
        String email = get(row, h, "email");
        String email2 = get(row, h, "email 2");
        List<ContactMethod> phones = methodCodec.decode(get(row, h, "phones"));
        List<ContactMethod> emails = methodCodec.decode(get(row, h, "emails"));
        ContactDraft legacy = new ContactDraft(
                get(row, h, "name"), phone, phone2, workPhone, email, email2,
                get(row, h, "company"), get(row, h, "job title"), get(row, h, "birthday"), get(row, h, "website"),
                get(row, h, "category"), get(row, h, "address"), get(row, h, "city"), get(row, h, "district"),
                get(row, h, "postal code"), get(row, h, "country"), get(row, h, "notes"),
                Boolean.parseBoolean(get(row, h, "favorite"))
        );
        return new ContactDraft(
                legacy.name(), legacy.phone(), legacy.phoneSecondary(), legacy.phoneWork(), legacy.email(), legacy.emailSecondary(),
                legacy.company(), legacy.jobTitle(), legacy.birthday(), legacy.website(), legacy.category(), legacy.address(),
                legacy.city(), legacy.district(), legacy.postalCode(), legacy.country(), legacy.notes(), legacy.favorite(),
                phones.isEmpty() ? legacy.phones() : phones, emails.isEmpty() ? legacy.emails() : emails, null,
                ReminderLeadTime.fromDays(intValue(get(row, h, "birthday reminder days"), ReminderLeadTime.DISABLED.daysBefore())),
                importantDateCodec.decode(get(row, h, "important dates")),
                KeepInTouchInterval.fromDays(intValue(get(row, h, "keep in touch days"), KeepInTouchInterval.DISABLED.days())),
                get(row, h, "last contacted date")
        );
    }

    private Map<String, Integer> headerIndex(List<String> headers) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) result.put(headers.get(i).trim().toLowerCase(Locale.ROOT), i);
        return result;
    }

    private String get(List<String> row, Map<String, Integer> header, String key) {
        Integer index = header.get(key);
        return index == null || index < 0 || index >= row.size() ? "" : row.get(index).trim();
    }

    private int intValue(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (NumberFormatException exception) { return fallback; }
    }

    private String join(List<String> values) { return values.stream().map(this::quote).reduce((a, b) -> a + "," + b).orElse(""); }
    private String quote(String value) {
        String safe = safe(value);
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r")) {
            return "\"" + safe.replace("\"", "\"\"") + "\"";
        }
        return safe;
    }

    private List<List<String>> parseRows(BufferedReader reader) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        int current;
        while ((current = reader.read()) != -1) {
            char ch = (char) current;
            if (quoted) {
                if (ch == '"') {
                    reader.mark(1);
                    int next = reader.read();
                    if (next == '"') cell.append('"');
                    else { quoted = false; if (next != -1) reader.reset(); }
                } else cell.append(ch);
            } else if (ch == '"') quoted = true;
            else if (ch == ',') { row.add(cell.toString()); cell.setLength(0); }
            else if (ch == '\n') { row.add(trimCr(cell.toString())); cell.setLength(0); rows.add(row); row = new ArrayList<>(); }
            else cell.append(ch);
        }
        if (cell.length() > 0 || !row.isEmpty()) { row.add(trimCr(cell.toString())); rows.add(row); }
        return rows;
    }

    private String trimCr(String value) { return value.endsWith("\r") ? value.substring(0, value.length() - 1) : value; }
    private String safe(String value) { return value == null ? "" : value; }
}
