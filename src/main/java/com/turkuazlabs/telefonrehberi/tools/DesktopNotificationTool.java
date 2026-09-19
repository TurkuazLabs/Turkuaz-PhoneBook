// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/DesktopNotificationTool.java
// # 📌 Amac: Masaustu isletim sistemi bildirim alanina gecici Turkuaz bildirimi gonderir.
// # 📌 Tool - Java
// Version: 1.0.0
// Aciklama: Java SystemTray adaptorudur; desteklenmeyen ortamlarda sessizce no-op olur ve tray ikonunu gecici kullanir.
// Bagimli Oldugu Katman: Tool
package com.turkuazlabs.telefonrehberi.tools;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.Timer;
import java.util.TimerTask;

public final class DesktopNotificationTool {
    private static final long TRAY_ICON_LIFETIME_MS = 20_000L;

    private final Image appIcon;

    public DesktopNotificationTool(Image appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isSupported() {
        return appIcon != null && SystemTray.isSupported();
    }

    public void show(String title, String message) {
        if (!isSupported() || title == null || title.isBlank() || message == null || message.isBlank()) return;

        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(appIcon);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(title);

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
            scheduleRemoval(tray, trayIcon);
        } catch (AWTException | RuntimeException ignored) {
            tray.remove(trayIcon);
        }
    }

    private void scheduleRemoval(SystemTray tray, TrayIcon trayIcon) {
        Timer timer = new Timer("turkuaz-reminder-notification-cleanup", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                tray.remove(trayIcon);
                timer.cancel();
            }
        }, TRAY_ICON_LIFETIME_MS);
    }
}
