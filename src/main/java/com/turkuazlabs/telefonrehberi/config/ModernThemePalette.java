// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/ModernThemePalette.java
// # 📌 Amac: WhatsApp esintili modern acik/koyu renk paletini merkezi olarak sunar.
// # 📌 Config - Java
// # Version: 2.11.0
// # Aciklama: Acik temayi varsayilan tutar; WhatsApp benzeri ferah yesil vurgu, yumusak zemin ve okunakli yuzey renkleri uretir.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.config;

import javax.swing.UIManager;
import java.awt.Color;

public final class ModernThemePalette {
    private static final Color LIGHT_BACKGROUND = new Color(240, 242, 245);
    private static final Color LIGHT_BACKGROUND_ALT = new Color(248, 250, 251);
    private static final Color LIGHT_SURFACE = new Color(255, 255, 255);
    private static final Color LIGHT_SURFACE_ELEVATED = new Color(255, 255, 255);
    private static final Color LIGHT_SURFACE_MUTED = new Color(247, 250, 249);
    private static final Color LIGHT_BORDER = new Color(221, 229, 228);
    private static final Color LIGHT_TEXT = new Color(17, 27, 33);
    private static final Color LIGHT_TEXT_SECONDARY = new Color(102, 119, 129);
    private static final Color LIGHT_SIDEBAR = new Color(250, 251, 251);
    private static final Color LIGHT_SIDEBAR_TEXT = new Color(32, 44, 51);
    private static final Color LIGHT_SIDEBAR_MUTED = new Color(104, 117, 126);

    private static final Color DARK_BACKGROUND = new Color(11, 20, 26);
    private static final Color DARK_BACKGROUND_ALT = new Color(17, 27, 33);
    private static final Color DARK_SURFACE = new Color(32, 44, 51);
    private static final Color DARK_SURFACE_ELEVATED = new Color(42, 57, 66);
    private static final Color DARK_SURFACE_MUTED = new Color(24, 34, 41);
    private static final Color DARK_BORDER = new Color(54, 73, 82);
    private static final Color DARK_TEXT = new Color(233, 237, 239);
    private static final Color DARK_TEXT_SECONDARY = new Color(134, 150, 160);
    private static final Color DARK_SIDEBAR = new Color(17, 27, 33);
    private static final Color DARK_SIDEBAR_TEXT = new Color(233, 237, 239);
    private static final Color DARK_SIDEBAR_MUTED = new Color(134, 150, 160);

    private static final Color[] AVATAR_COLORS = {
            new Color(37, 211, 102),
            new Color(18, 140, 126),
            new Color(83, 189, 235),
            new Color(125, 91, 166),
            new Color(239, 105, 80),
            new Color(244, 180, 0)
    };

    private static final Color ACCENT = new Color(18, 140, 126);
    private static final Color ACCENT_STRONG = new Color(17, 117, 104);

    public static boolean isDark() {
        return UIManager.getBoolean("laf.dark");
    }

    public static Color background() {
        return isDark() ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    public static Color backgroundAlt() {
        return isDark() ? DARK_BACKGROUND_ALT : LIGHT_BACKGROUND_ALT;
    }

    public static Color surface() {
        return isDark() ? DARK_SURFACE : LIGHT_SURFACE;
    }

    public static Color surfaceElevated() {
        return isDark() ? DARK_SURFACE_ELEVATED : LIGHT_SURFACE_ELEVATED;
    }

    public static Color surfaceMuted() {
        return isDark() ? DARK_SURFACE_MUTED : LIGHT_SURFACE_MUTED;
    }

    public static Color border() {
        return isDark() ? DARK_BORDER : LIGHT_BORDER;
    }

    public static Color textPrimary() {
        return isDark() ? DARK_TEXT : LIGHT_TEXT;
    }

    public static Color textSecondary() {
        return isDark() ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;
    }

    public static Color avatarColor(String key) {
        int hash = key == null ? 0 : key.hashCode();
        return AVATAR_COLORS[Math.floorMod(hash, AVATAR_COLORS.length)];
    }

    public static Color accent() {
        return ACCENT;
    }

    public static Color accentStrong() {
        return ACCENT_STRONG;
    }

    public static Color accentSoft() {
        return isDark() ? new Color(17, 68, 61) : new Color(223, 247, 238);
    }

    public static Color success() {
        return new Color(37, 211, 102);
    }

    public static Color warning() {
        return new Color(244, 180, 0);
    }

    public static Color danger() {
        return new Color(229, 72, 77);
    }

    public static Color sidebarStart() {
        return isDark() ? DARK_SIDEBAR : LIGHT_SIDEBAR;
    }

    public static Color sidebarEnd() {
        return sidebarStart();
    }

    public static Color sidebarText() {
        return isDark() ? DARK_SIDEBAR_TEXT : LIGHT_SIDEBAR_TEXT;
    }

    public static Color sidebarMutedText() {
        return isDark() ? DARK_SIDEBAR_MUTED : LIGHT_SIDEBAR_MUTED;
    }

    public static Color sidebarHover() {
        return isDark() ? new Color(255, 255, 255, 14) : new Color(18, 140, 126, 10);
    }

    public static Color sidebarActive() {
        return isDark() ? new Color(22, 78, 70) : new Color(223, 247, 238);
    }

    public static Color sidebarActiveText() {
        return isDark() ? new Color(223, 247, 238) : new Color(17, 117, 104);
    }

    public static Color shadow() {
        return isDark() ? new Color(0, 0, 0, 38) : new Color(17, 27, 33, 12);
    }

    private ModernThemePalette() {
    }
}
