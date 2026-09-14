// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/ContactQuickActionService.java
// # 📌 Amac: Secili kisi icin Ara, WhatsApp, E-posta ve Kopyala hizli aksiyon kurallarini yonetir.
// # 📌 Service - Java
// # Version: 2.12.3
// # Aciklama: Birincil iletisim alanini secer, masaustu Tool katmanina uygun URI/pano islemini yaptirir ve kullanici hatalarini yonetir.
// # Bagimli Oldugu Katman: Service | Tool | Model | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.tools.DesktopActionTool;

import java.util.List;

public final class ContactQuickActionService {
    private final DesktopActionTool desktopActionTool;

    public ContactQuickActionService(DesktopActionTool desktopActionTool) {
        this.desktopActionTool = desktopActionTool;
    }

    public void call(Contact contact) {
        String phone = primaryValue(contact.phones(), contact.phone(), Messages.QUICK_ACTION_PHONE_REQUIRED);
        if (!desktopActionTool.open(desktopActionTool.phoneUri(phone))) {
            throw new IllegalStateException(Messages.QUICK_ACTION_OPEN_FAILED);
        }
    }

    public void openWhatsApp(Contact contact) {
        String phone = primaryValue(contact.phones(), contact.phone(), Messages.QUICK_ACTION_PHONE_REQUIRED);
        if (!desktopActionTool.open(desktopActionTool.whatsappUri(phone))) {
            throw new IllegalStateException(Messages.QUICK_ACTION_OPEN_FAILED);
        }
    }

    public void email(Contact contact) {
        String email = primaryValue(contact.emails(), contact.email(), Messages.QUICK_ACTION_EMAIL_REQUIRED);
        if (!desktopActionTool.open(desktopActionTool.emailUri(email))) {
            throw new IllegalStateException(Messages.QUICK_ACTION_OPEN_FAILED);
        }
    }

    public String copyPreferred(Contact contact) {
        String value = preferredCopyValue(contact);
        if (!desktopActionTool.copyText(value)) {
            throw new IllegalStateException(Messages.QUICK_ACTION_COPY_FAILED);
        }
        return value;
    }

    private String preferredCopyValue(Contact contact) {
        String phone = primaryValueOrEmpty(contact.phones(), contact.phone());
        if (!phone.isBlank()) {
            return phone;
        }
        String email = primaryValueOrEmpty(contact.emails(), contact.email());
        if (!email.isBlank()) {
            return email;
        }
        throw new IllegalArgumentException(Messages.QUICK_ACTION_COPY_REQUIRED);
    }

    private String primaryValue(List<ContactMethod> methods, String fallback, String missingMessage) {
        String value = primaryValueOrEmpty(methods, fallback);
        if (value.isBlank()) {
            throw new IllegalArgumentException(missingMessage);
        }
        return value;
    }

    private String primaryValueOrEmpty(List<ContactMethod> methods, String fallback) {
        if (methods != null && !methods.isEmpty()) {
            return methods.stream()
                    .filter(ContactMethod::primary)
                    .findFirst()
                    .orElse(methods.get(0))
                    .value();
        }
        return fallback == null ? "" : fallback.trim();
    }
}
