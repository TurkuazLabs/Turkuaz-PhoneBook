// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/ImportantDateCodec.java
// # 📌 Amac: Kisiye ait onemli tarih listelerini history snapshot metnine kayipsiz cevirir ve geri okur.
// # 📌 Tool - Java
// # Version: 1.0.0
// # Aciklama: Tarih turu, etiket, tarih degeri, hatirlatma suresi ve sira bilgisini URL-safe satir formatinda tasir.
// # Bagimli Oldugu Katman: Tool | Model
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.models.ImportantDate;
import com.turkuazlabs.telefonrehberi.models.ImportantDateType;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ImportantDateCodec {
    private static final String ROW_SEPARATOR = "~";
    private static final String FIELD_SEPARATOR = "|";

    public String encode(List<ImportantDate> values) {
        if (values == null || values.isEmpty()) return "";
        StringBuilder out = new StringBuilder();
        for (ImportantDate value : values) {
            if (value == null || value.dateValue().isBlank()) continue;
            if (out.length() > 0) out.append(ROW_SEPARATOR);
            out.append(enc(value.type().name())).append(FIELD_SEPARATOR)
                    .append(enc(value.label())).append(FIELD_SEPARATOR)
                    .append(enc(value.dateValue())).append(FIELD_SEPARATOR)
                    .append(value.reminderLeadTime().daysBefore()).append(FIELD_SEPARATOR)
                    .append(value.position());
        }
        return out.toString();
    }

    public List<ImportantDate> decode(String value) {
        if (value == null || value.isBlank()) return List.of();
        List<ImportantDate> out = new ArrayList<>();
        for (String row : value.split(ROW_SEPARATOR, -1)) {
            String[] fields = row.split("\\|", -1);
            if (fields.length < 4) continue;
            ImportantDateType type;
            try { type = ImportantDateType.valueOf(dec(fields[0])); }
            catch (IllegalArgumentException exception) { type = ImportantDateType.CUSTOM; }
            int reminderDays;
            try { reminderDays = Integer.parseInt(fields[3]); }
            catch (NumberFormatException exception) { reminderDays = ReminderLeadTime.DISABLED.daysBefore(); }
            int position = out.size();
            if (fields.length > 4) {
                try { position = Integer.parseInt(fields[4]); } catch (NumberFormatException ignored) { }
            }
            String dateValue = dec(fields[2]);
            if (!dateValue.isBlank()) {
                out.add(new ImportantDate(type, dec(fields[1]), dateValue, ReminderLeadTime.fromDays(reminderDays), position));
            }
        }
        return List.copyOf(out);
    }

    private String enc(String value) { return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8); }
    private String dec(String value) { return URLDecoder.decode(value == null ? "" : value, StandardCharsets.UTF_8); }
}
