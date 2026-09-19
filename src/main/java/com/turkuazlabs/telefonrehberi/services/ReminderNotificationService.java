// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/ReminderNotificationService.java
// # 📌 Amac: Aktif kisi hatirlatmalarini gizlilik odakli tek masaustu bildiriminde ozetler.
// # 📌 Service - Java
// Version: 1.0.0
// Aciklama: Kullanici tercihini, aktif hatirlatma sayisini ve DesktopNotificationTool cagrisi is kuralini yonetir.
// Bagimli Oldugu Katman: Service | Tool | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.tools.DesktopNotificationTool;

public final class ReminderNotificationService {
    private final ContactService contactService;
    private final DesktopNotificationTool notificationTool;

    public ReminderNotificationService(ContactService contactService, DesktopNotificationTool notificationTool) {
        this.contactService = contactService;
        this.notificationTool = notificationTool;
    }

    public boolean notifyDueReminders(boolean enabled) {
        if (!enabled || !notificationTool.isSupported()) return false;

        int dueCount = contactService.listDueReminderContacts().size();
        if (dueCount <= 0) return false;

        notificationTool.show(
                Messages.REMINDER_NOTIFICATION_TITLE,
                String.format(Messages.REMINDER_NOTIFICATION_SUMMARY_FORMAT, dueCount)
        );
        return true;
    }
}
