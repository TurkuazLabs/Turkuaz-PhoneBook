// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/ModernThemePalette.java
// # 📌 Amac: Turkuaz marka kimligini acik/koyu temalarda tutarli ve erisilebilir renk sistemiyle merkezi olarak sunar.
// # 📌 Config - Java
// # Version: 2.14.0
// # Aciklama: Acik temayi daha ferah yapar; koyu temayi logo turkuazinin tonlariyla yeniden kurar ve aksiyon/selection renklerini ayirir.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.config;

import javax.swing.UIManager;
import java.awt.Color;

public final class ModernThemePalette {
    private static final Color BRAND_TURQUOISE = new Color(18, 140, 126);
    private static final Color BRAND_TURQUOISE_DARK = new Color(17, 117, 104);
    private static final Color BRAND_TURQUOISE_DARKER = new Color(14, 102, 91);
    private static final Color BRAND_TURQUOISE_BRIGHT = new Color(46, 196, 182);
    private static final Color BRAND_TURQUOISE_BRIGHTER = new Color(106, 215, 203);

    private static final Color LIGHT_BACKGROUND = new Color(246, 248, 249);
    private static final Color LIGHT_BACKGROUND_ALT = new Color(252, 253, 253);
    private static final Color LIGHT_SURFACE = new Color(255, 255, 255);
    private static final Color LIGHT_SURFACE_ELEVATED = new Color(255, 255, 255);
    private static final Color LIGHT_SURFACE_MUTED = new Color(250, 252, 252);
    private static final Color LIGHT_BORDER = new Color(226, 233, 232);
    private static final Color LIGHT_TEXT = new Color(17, 27, 33);
    private static final Color LIGHT_TEXT_SECONDARY = new Color(99, 113, 121);
    private static final Color LIGHT_SIDEBAR = new Color(252, 253, 253);
    private static final Color LIGHT_SIDEBAR_TEXT = new Color(32, 44, 51);
    private static final Color LIGHT_SIDEBAR_MUTED = new Color(104, 117, 126);

    private static final Color DARK_BACKGROUND = new Color(16, 25, 29);
    private static final Color DARK_BACKGROUND_ALT = new Color(20, 33, 38);
    private static final Color DARK_SURFACE = new Color(26, 43, 48);
    private static final Color DARK_SURFACE_ELEVATED = new Color(33, 55, 61);
    private static final Color DARK_SURFACE_MUTED = new Color(23, 38, 43);
    private static final Color DARK_BORDER = new Color(43, 69, 75);
    private static final Color DARK_TEXT = new Color(231, 242, 243);
    private static final Color DARK_TEXT_SECONDARY = new Color(152, 172, 177);
    private static final Color DARK_SIDEBAR = new Color(19, 33, 38);
    private static final Color DARK_SIDEBAR_TEXT = new Color(231, 242, 243);
    private static final Color DARK_SIDEBAR_MUTED = new Color(152, 172, 177);

    private static final Color[] AVATAR_COLORS = {
            new Color(37, 211, 102),
            BRAND_TURQUOISE,
            new Color(83, 189, 235),
            new Color(125, 91, 166),
            new Color(239, 105, 80),
            new Color(244, 180, 0)
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
        return isDark() ? BRAND_TURQUOISE_BRIGHT : BRAND_TURQUOISE;
    }

    public static Color accentStrong() {
        return isDark() ? BRAND_TURQUOISE_BRIGHTER : BRAND_TURQUOISE_DARK;
    }

    public static Color accentSoft() {
        return isDark() ? new Color(23, 58, 56) : new Color(234, 249, 246);
    }

    public static Color actionFill() {
        return BRAND_TURQUOISE_DARK;
    }

    public static Color actionHover() {
        return isDark() ? new Color(20, 130, 116) : BRAND_TURQUOISE_DARKER;
    }

    public static Color actionPressed() {
        return BRAND_TURQUOISE_DARKER;
    }

    public static Color brandAccent() {
        return BRAND_TURQUOISE;
    }

    public static Color brandAccent(boolean dark) {
        return BRAND_TURQUOISE;
    }

    public static Color brandGlyph(boolean dark) {
        return dark ? new Color(236, 253, 250) : Color.WHITE;
    }

    public static Color success() {
        return new Color(37, 211, 102);
    }

    public static Color warning() {
        return new Color(244, 180, 0);
    }

    public static Color danger() {
        return isDark() ? new Color(255, 112, 117) : new Color(210, 55, 55);
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
        return isDark() ? new Color(46, 196, 182, 18) : new Color(18, 140, 126, 9);
    }

    public static Color sidebarActive() {
        return isDark() ? new Color(23, 58, 56) : new Color(234, 249, 246);
    }

    public static Color sidebarActiveText() {
        return isDark() ? BRAND_TURQUOISE_BRIGHTER : BRAND_TURQUOISE_DARK;
    }

    public static Color shadow() {
        return isDark() ? new Color(0, 0, 0, 46) : new Color(17, 27, 33, 9);
    }

    private ModernThemePalette() {
    }
}
