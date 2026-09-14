// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/LegacyTsvReader.java
// # 📌 Amac: v1.3.0 ve onceki TSV rehber kayitlarini migration icin okur.
// # 📌 Tool - Java
// # Version: 1.1.0
// # Aciklama: Base64 kodlu eski TSV satirlarini genisletilmis Contact modeline varsayilan bos alanlarla donusturur.
// # Bagimli Oldugu Katman: Tool | Config | Model | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class LegacyTsvReader {
    private static final int FIELD_COUNT = 4;
    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int PHONE_INDEX = 2;
    private static final int EMAIL_INDEX = 3;

    public List<Contact> readIfExists() {
        if (java.nio.file.Files.notExists(AppConfig.LEGACY_DATA_FILE)) {
            return List.of();
        }

        try {
            List<Contact> contacts = new ArrayList<>();
            for (String line : java.nio.file.Files.readAllLines(AppConfig.LEGACY_DATA_FILE, AppConfig.DATA_CHARSET)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(AppConfig.LEGACY_FIELD_SEPARATOR, -1);
                if (fields.length != FIELD_COUNT) {
                    continue;
                }
                contacts.add(new Contact(
                        Long.parseLong(fields[ID_INDEX]),
                        decode(fields[NAME_INDEX]),
                        decode(fields[PHONE_INDEX]),
                        "",
                        "",
                        decode(fields[EMAIL_INDEX]),
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        false
                ));
            }
            return contacts;
        } catch (IOException | IllegalArgumentException exception) {
            throw new IllegalStateException(Messages.ERROR_LEGACY_READ, exception);
        }
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), AppConfig.DATA_CHARSET);
    }
}
