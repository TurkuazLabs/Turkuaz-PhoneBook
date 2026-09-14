// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/ContactMethodCodec.java
// # 📌 Amac: Sinirsiz telefon/e-posta satirlarini metin tabanli tasima formatina cevirir ve geri okur.
// # 📌 Tool - Java
// # Version: 2.8.0
// # Aciklama: URL-safe alan kodlamasi ile CSV, history ve mobil form payloadlari icin kayipsiz codec saglar.
// # Bagimli Oldugu Katman: Tool | Model
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.models.ContactMethod;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ContactMethodCodec {
    private static final String ROW_SEPARATOR = "~";
    private static final String FIELD_SEPARATOR = "|";

    public String encode(List<ContactMethod> methods) {
        StringBuilder out = new StringBuilder();
        if (methods == null) return "";
        for (ContactMethod method : methods) {
            if (method == null || method.value().isBlank()) continue;
            if (out.length() > 0) out.append(ROW_SEPARATOR);
            out.append(enc(method.kind())).append(FIELD_SEPARATOR)
                    .append(enc(method.label())).append(FIELD_SEPARATOR)
                    .append(enc(method.value())).append(FIELD_SEPARATOR)
                    .append(method.primary() ? "1" : "0").append(FIELD_SEPARATOR)
                    .append(method.position());
        }
        return out.toString();
    }

    public List<ContactMethod> decode(String value) {
        if (value == null || value.isBlank()) return List.of();
        List<ContactMethod> out = new ArrayList<>();
        for (String row : value.split(ROW_SEPARATOR, -1)) {
            String[] fields = row.split("\\|", -1);
            if (fields.length < 4) continue;
            String kind = dec(fields[0]);
            String label = dec(fields[1]);
            String methodValue = dec(fields[2]);
            boolean primary = "1".equals(fields[3]) || Boolean.parseBoolean(fields[3]);
            int position = out.size();
            if (fields.length > 4) {
                try { position = Integer.parseInt(fields[4]); } catch (NumberFormatException ignored) { }
            }
            if (!methodValue.isBlank()) out.add(new ContactMethod(kind, label, methodValue, primary, position));
        }
        return List.copyOf(out);
    }

    private String enc(String value) { return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8); }
    private String dec(String value) { return URLDecoder.decode(value == null ? "" : value, StandardCharsets.UTF_8); }
}
