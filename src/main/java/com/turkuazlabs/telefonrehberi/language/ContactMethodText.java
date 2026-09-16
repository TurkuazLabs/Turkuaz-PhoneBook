// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/ContactMethodText.java
// # 📌 Amac: Kalici contact method etiketlerini veri degerini degistirmeden kullanici dilinde gosterir.
// # 📌 Language - Java
// Version: 1.0.0
// Aciklama: Eski Turkce canonical etiketleri ve yaygin Ingilizce karsiliklarini Turkce/Ingilizce display metnine cevirir; duzenleme sirasinda degismeyen etiketi ayni storage degeriyle korur.
// Bagimli Oldugu Katman: Language | Model
package com.turkuazlabs.telefonrehberi.language;

import com.turkuazlabs.telefonrehberi.models.ContactMethod;

public final class ContactMethodText {
    public static String displayLabel(String storedLabel) {
        String label = storedLabel == null ? "" : storedLabel.trim();
        return switch (label.toLowerCase(java.util.Locale.ROOT)) {
            case "cep", "mobile" -> LocaleText.text("Cep", "Mobile");
            case "diger", "other" -> LocaleText.text("Diger", "Other");
            case "is", "work" -> LocaleText.text("Is", "Work");
            case "ev", "home" -> LocaleText.text("Ev", "Home");
            case "e-posta", "email" -> LocaleText.text("E-posta", "Email");
            case "telefon", "phone" -> LocaleText.text("Telefon", "Phone");
            default -> label;
        };
    }

    public static String storageLabel(String originalStoredLabel, String editedDisplayLabel, String fallbackStoredLabel) {
        String original = originalStoredLabel == null ? "" : originalStoredLabel.trim();
        String edited = editedDisplayLabel == null ? "" : editedDisplayLabel.trim();
        String fallback = fallbackStoredLabel == null ? "" : fallbackStoredLabel.trim();
        if (edited.isBlank()) {
            return fallback;
        }
        if (!original.isBlank() && edited.equals(displayLabel(original))) {
            return original;
        }
        return edited;
    }

    public static String defaultLabel(String kind) {
        return ContactMethod.EMAIL.equals(kind)
                ? displayLabel(ContactMethod.LABEL_EMAIL)
                : displayLabel(ContactMethod.LABEL_MOBILE);
    }

    private ContactMethodText() {
    }
}
