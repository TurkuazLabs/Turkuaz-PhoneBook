// # Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/ModernThemePalette.java
// # Amac: Turkuaz marka kimligini acik ve koyu temalarda tutarli, sakin ve erisilebilir semantik renk tokenlariyla sunar.
// # Config - Java
// # Version: 3.0.0
// # Aciklama: Tema v3 paleti; arka plan, yuzey, border, metin, secim, focus, kontrol, durum ve aksiyon renklerini tek kaynaktan yonetir.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.config;

import javax.swing.UIManager;
import java.awt.Color;

public final class ModernThemePalette {
    private static final Color BRAND_300 = new Color(99, 218, 205);
    private static final Color BRAND_400 = new Color(48, 196, 180);
    private static final Color BRAND_600 = new Color(11, 123, 111);
    private static final Color BRAND_700 = new Color(8, 105, 96);
    private static final Color BRAND_800 = new Color(7, 88, 81);

    private static final Color LIGHT_BACKGROUND = new Color(244, 247, 249);
    private static final Color LIGHT_BACKGROUND_ALT = new Color(238, 243, 245);
    private static final Color LIGHT_SURFACE = new Color(255, 255, 255);
    private static final Color LIGHT_SURFACE_ELEVATED = new Color(255, 255, 255);
    private static final Color LIGHT_SURFACE_MUTED = new Color(247, 250, 251);
    private static final Color LIGHT_BORDER = new Color(216, 226, 230);
    private static final Color LIGHT_BORDER_STRONG = new Color(197, 209, 214);
    private static final Color LIGHT_TEXT = new Color(23, 35, 41);
    private static final Color LIGHT_TEXT_SECONDARY = new Color(95, 114, 123);
    private static final Color LIGHT_TEXT_MUTED = new Color(132, 149, 157);
    private static final Color LIGHT_SIDEBAR = new Color(249, 251, 252);
    private static final Color LIGHT_SIDEBAR_TEXT = new Color(32, 46, 53);
    private static final Color LIGHT_SIDEBAR_MUTED = new Color(104, 121, 130);
    private static final Color LIGHT_ACCENT_SOFT = new Color(230, 245, 242);
    private static final Color LIGHT_SELECTION = new Color(221, 242, 238);
    private static final Color LIGHT_CONTROL_HOVER = new Color(240, 246, 247);
    private static final Color LIGHT_CONTROL_PRESSED = new Color(232, 240, 242);
    private static final Color LIGHT_DISABLED_SURFACE = new Color(241, 245, 246);

    private static final Color DARK_BACKGROUND = new Color(16, 23, 27);
    private static final Color DARK_BACKGROUND_ALT = new Color(20, 31, 36);
    private static final Color DARK_SURFACE = new Color(24, 37, 43);
    private static final Color DARK_SURFACE_ELEVATED = new Color(29, 44, 51);
    private static final Color DARK_SURFACE_MUTED = new Color(20, 32, 38);
    private static final Color DARK_BORDER = new Color(45, 65, 74);
    private static final Color DARK_BORDER_STRONG = new Color(59, 86, 96);
    private static final Color DARK_TEXT = new Color(241, 247, 247);
    private static final Color DARK_TEXT_SECONDARY = new Color(167, 187, 193);
    private static final Color DARK_TEXT_MUTED = new Color(120, 144, 153);
    private static final Color DARK_SIDEBAR = new Color(18, 28, 33);
    private static final Color DARK_SIDEBAR_TEXT = new Color(235, 244, 245);
    private static final Color DARK_SIDEBAR_MUTED = new Color(151, 174, 181);
    private static final Color DARK_ACCENT_SOFT = new Color(23, 61, 58);
    private static final Color DARK_SELECTION = new Color(29, 73, 68);
    private static final Color DARK_CONTROL_HOVER = new Color(32, 48, 57);
    private static final Color DARK_CONTROL_PRESSED = new Color(37, 56, 65);
    private static final Color DARK_DISABLED_SURFACE = new Color(25, 37, 43);

    private static final Color ACTION_FILL = BRAND_600;
    private static final Color ACTION_HOVER = BRAND_700;
    private static final Color ACTION_PRESSED = BRAND_800;
    private static final Color ACTION_FOREGROUND = Color.WHITE;

    private static final Color LIGHT_DANGER = new Color(190, 45, 54);
    private static final Color LIGHT_DANGER_BORDER = new Color(229, 169, 174);
    private static final Color LIGHT_DANGER_SOFT = new Color(253, 236, 238);
    private static final Color LIGHT_DANGER_HOVER = new Color(250, 221, 224);
    private static final Color LIGHT_DANGER_PRESSED = new Color(247, 207, 211);

    private static final Color DARK_DANGER = new Color(255, 132, 139);
    private static final Color DARK_DANGER_BORDER = new Color(122, 64, 71);
    private static final Color DARK_DANGER_SOFT = new Color(60, 32, 37);
    private static final Color DARK_DANGER_HOVER = new Color(74, 37, 43);
    private static final Color DARK_DANGER_PRESSED = new Color(86, 42, 49);

    private static final Color[] LIGHT_AVATAR_COLORS = {
            new Color(26, 143, 95),
            BRAND_600,
            new Color(45, 126, 184),
            new Color(111, 86, 158),
            new Color(191, 91, 67),
            new Color(173, 112, 0)
    };

    private static final Color[] DARK_AVATAR_COLORS = {
            new Color(64, 190, 132),
            BRAND_400,
            new Color(91, 166, 219),
            new Color(153, 126, 201),
            new Color(225, 126, 103),
            new Color(221, 167, 67)
    };

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

    public static Color borderStrong() {
        return isDark() ? DARK_BORDER_STRONG : LIGHT_BORDER_STRONG;
    }

    public static Color textPrimary() {
        return isDark() ? DARK_TEXT : LIGHT_TEXT;
    }

    public static Color textSecondary() {
        return isDark() ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;
    }

    public static Color textMuted() {
        return isDark() ? DARK_TEXT_MUTED : LIGHT_TEXT_MUTED;
    }

    public static Color avatarColor(String key) {
        Color[] colors = isDark() ? DARK_AVATAR_COLORS : LIGHT_AVATAR_COLORS;
        int hash = key == null ? 0 : key.hashCode();
        return colors[Math.floorMod(hash, colors.length)];
    }

    public static Color accent() {
        return isDark() ? BRAND_400 : BRAND_600;
    }

    public static Color accentStrong() {
        return isDark() ? BRAND_300 : BRAND_700;
    }

    public static Color accentSoft() {
        return isDark() ? DARK_ACCENT_SOFT : LIGHT_ACCENT_SOFT;
    }

    public static Color selectionBackground() {
        return isDark() ? DARK_SELECTION : LIGHT_SELECTION;
    }

    public static Color selectionForeground() {
        return textPrimary();
    }

    public static Color controlHover() {
        return isDark() ? DARK_CONTROL_HOVER : LIGHT_CONTROL_HOVER;
    }

    public static Color controlPressed() {
        return isDark() ? DARK_CONTROL_PRESSED : LIGHT_CONTROL_PRESSED;
    }

    public static Color disabledSurface() {
        return isDark() ? DARK_DISABLED_SURFACE : LIGHT_DISABLED_SURFACE;
    }

    public static Color disabledText() {
        return textMuted();
    }

    public static Color focusRing() {
        return accent();
    }

    public static Color actionFill() {
        return ACTION_FILL;
    }

    public static Color actionHover() {
        return ACTION_HOVER;
    }

    public static Color actionPressed() {
        return ACTION_PRESSED;
    }

    public static Color actionForeground() {
        return ACTION_FOREGROUND;
    }

    public static Color brandAccent() {
        return BRAND_600;
    }

    public static Color brandAccent(boolean dark) {
        return BRAND_600;
    }

    public static Color brandGlyph(boolean dark) {
        return Color.WHITE;
    }

    public static Color success() {
        return isDark() ? new Color(74, 195, 141) : new Color(22, 138, 93);
    }

    public static Color warning() {
        return isDark() ? new Color(231, 185, 90) : new Color(167, 106, 0);
    }

    public static Color danger() {
        return isDark() ? DARK_DANGER : LIGHT_DANGER;
    }

    public static Color dangerBorder() {
        return isDark() ? DARK_DANGER_BORDER : LIGHT_DANGER_BORDER;
    }

    public static Color dangerSoft() {
        return isDark() ? DARK_DANGER_SOFT : LIGHT_DANGER_SOFT;
    }

    public static Color dangerHover() {
        return isDark() ? DARK_DANGER_HOVER : LIGHT_DANGER_HOVER;
    }

    public static Color dangerPressed() {
        return isDark() ? DARK_DANGER_PRESSED : LIGHT_DANGER_PRESSED;
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
        return isDark() ? new Color(48, 196, 180, 18) : new Color(11, 123, 111, 13);
    }

    public static Color sidebarActive() {
        return accentSoft();
    }

    public static Color sidebarActiveText() {
        return accentStrong();
    }

    public static Color shadow() {
        return isDark() ? new Color(0, 0, 0, 64) : new Color(23, 35, 41, 18);
    }

    public static Color inputBackground() {
        return isDark() ? new Color(22, 34, 40) : LIGHT_SURFACE;
    }

    public static Color tableHeaderBackground() {
        return isDark() ? DARK_SURFACE_MUTED : new Color(241, 245, 247);
    }

    public static Color scrollbarThumb() {
        return borderStrong();
    }

    public static Color scrollbarHoverThumb() {
        return isDark() ? new Color(82, 111, 121) : new Color(167, 184, 191);
    }

    private ModernThemePalette() {
    }
}
