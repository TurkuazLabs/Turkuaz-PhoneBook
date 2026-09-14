// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactFileFormat.java
// # 📌 Amac: Rehber ice ve disa aktarma dosya formatlarini tip guvenli olarak tanimlar.
// # 📌 Model - Java
// # Version: 2.13.1
// # Aciklama: Controller, View ve Service arasinda VCF/CSV uzantilarini magic string kullanmadan tek kaynaktan tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum ContactFileFormat {
    VCARD("vcf", "VCF"),
    CSV("csv", "CSV");

    private final String extension;
    private final String displayName;

    ContactFileFormat(String extension, String displayName) {
        this.extension = extension;
        this.displayName = displayName;
    }

    public String extension() {
        return extension;
    }

    public String dottedExtension() {
        return "." + extension;
    }

    public String displayName() {
        return displayName;
    }

    public boolean matchesFileName(String fileName) {
        if (fileName == null) return false;
        return fileName.toLowerCase(Locale.ROOT).endsWith(dottedExtension());
    }

    public static Optional<ContactFileFormat> fromFileName(String fileName) {
        return Arrays.stream(values())
                .filter(format -> format.matchesFileName(fileName))
                .findFirst();
    }

    public static String[] extensions() {
        return Arrays.stream(values())
                .map(ContactFileFormat::extension)
                .toArray(String[]::new);
    }
}
