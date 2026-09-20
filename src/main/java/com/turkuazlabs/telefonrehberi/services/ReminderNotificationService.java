// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/ReminderNotificationService.java
// # 📌 Amac: Aktif kisi hatirlatmalarini gizlilik odakli tek masaustu bildiriminde ozetler.
// # 📌 Service - Java
// Version: 1.0.1
// Aciklama: Kullanici tercihini, gunluk bildirim dedup state'ini, aktif hatirlatma sayisini ve DesktopNotificationTool cagrisi is kuralini yonetir.
// Bagimli Oldugu Katman: Service | Repository | Tool | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.repositories.ReminderNotificationRepository;

import java.time.LocalDate;
import com.turkuazlabs.telefonrehberi.tools.DesktopNotificationTool;

public final class ReminderNotificationService {
    private final ContactService contactService;
    private final ReminderNotificationRepository notificationRepository;
    private final DesktopNotificationTool notificationTool;

    public ReminderNotificationService(
            ContactService contactService,
            ReminderNotificationRepository notificationRepository,
            DesktopNotificationTool notificationTool
    ) {
        this.contactService = contactService;
        this.notificationRepository = notificationRepository;
        this.notificationTool = notificationTool;
    }

    public boolean notifyDueReminders(boolean enabled) {
        if (!enabled) return false;

        LocalDate today = LocalDate.now();
        if (notificationRepository.wasNotifiedOn(today) || !notificationTool.isSupported()) return false;

        int dueCount = contactService.listDueReminderContacts().size();
        if (dueCount <= 0) return false;

        boolean shown = notificationTool.show(
                Messages.REMINDER_NOTIFICATION_TITLE,
                String.format(Messages.REMINDER_NOTIFICATION_SUMMARY_FORMAT, dueCount)
        );
        if (!shown) return false;

        notificationRepository.markNotified(today);
        return true;
    }
}
