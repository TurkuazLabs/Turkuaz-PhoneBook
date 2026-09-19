// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/FlatLafThemeTool.java
// # 📌 Amac: FlatLaf tema motorunu Swing arayuzune uygulayan adaptor gorevi gorur.
// # 📌 Tool - Java
// # Version: 1.5.0
// # Aciklama: FlatLaf siniflarini yukler; Turkuaz light/dark paletini input, liste, sekme, focus ve scrollbar varsayilanlarina uygular.
// # Bagimli Oldugu Katman: Tool | Model | Config
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.UiConfig;
import com.turkuazlabs.telefonrehberi.models.ThemeMode;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.lang.reflect.Method;

public final class FlatLafThemeTool {
    public boolean apply(ThemeMode mode) {
        String className = mode == ThemeMode.DARK
                ? UiConfig.FLATLAF_DARK_CLASS
                : UiConfig.FLATLAF_LIGHT_CLASS;
        try {
            Class<?> lookAndFeelClass = Class.forName(className);
            Method setupMethod = lookAndFeelClass.getMethod("setup");
            Object result = setupMethod.invoke(null);
            boolean applied = !(result instanceof Boolean booleanResult) || booleanResult;
            if (applied) {
                applyGlobalDefaults();
                refreshOpenWindows();
                return true;
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // Launcher FlatLaf'i hazirlayamadiysa uygulama sistem temasi ile acilmaya devam eder.
        }
        applySystemFallback();
        return false;
    }

    private void applyGlobalDefaults() {
        UIManager.put("defaultFont", new Font("Segoe UI Variable", Font.PLAIN, 14));
        UIManager.put("Component.arc", 12);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Button.arc", 12);
        UIManager.put("Button.margin", new Insets(8, 14, 8, 14));
        UIManager.put("Button.minimumWidth", 92);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("TextField.margin", new Insets(8, 11, 8, 11));
        UIManager.put("PasswordField.margin", new Insets(8, 11, 8, 11));
        UIManager.put("ComboBox.padding", new Insets(7, 10, 7, 10));
        UIManager.put("MenuItem.selectionType", "underline");
        UIManager.put("ProgressBar.arc", 16);
        UIManager.put("ScrollBar.width", 11);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("TabbedPane.tabHeight", 40);
        UIManager.put("TabbedPane.tabInsets", new Insets(7, 14, 7, 14));
        UIManager.put("TabbedPane.showTabSeparators", false);
        UIManager.put("ToolTip.arc", 8);
        UIManager.put("TitlePane.unifiedBackground", true);

        UIManager.put("Panel.background", ModernThemePalette.background());
        UIManager.put("RootPane.background", ModernThemePalette.background());
        UIManager.put("Label.foreground", ModernThemePalette.textPrimary());
        UIManager.put("TextField.background", ModernThemePalette.surface());
        UIManager.put("TextField.foreground", ModernThemePalette.textPrimary());
        UIManager.put("TextField.inactiveBackground", ModernThemePalette.surfaceMuted());
        UIManager.put("TextArea.background", ModernThemePalette.surface());
        UIManager.put("TextArea.foreground", ModernThemePalette.textPrimary());
        UIManager.put("ComboBox.background", ModernThemePalette.surface());
        UIManager.put("ComboBox.foreground", ModernThemePalette.textPrimary());
        UIManager.put("List.background", ModernThemePalette.surface());
        UIManager.put("List.foreground", ModernThemePalette.textPrimary());
        UIManager.put("List.selectionBackground", ModernThemePalette.accentSoft());
        UIManager.put("List.selectionForeground", ModernThemePalette.textPrimary());
        UIManager.put("Table.background", ModernThemePalette.surface());
        UIManager.put("Table.foreground", ModernThemePalette.textPrimary());
        UIManager.put("Table.selectionBackground", ModernThemePalette.accentSoft());
        UIManager.put("Table.selectionForeground", ModernThemePalette.textPrimary());
        UIManager.put("TabbedPane.background", ModernThemePalette.surface());
        UIManager.put("TabbedPane.selectedBackground", ModernThemePalette.accentSoft());
        UIManager.put("Separator.foreground", ModernThemePalette.border());
        UIManager.put("Component.borderColor", ModernThemePalette.border());
        UIManager.put("Component.focusColor", ModernThemePalette.accent());
        UIManager.put("Component.focusedBorderColor", ModernThemePalette.accent());
        UIManager.put("ScrollBar.thumb", ModernThemePalette.border());
        UIManager.put("ScrollBar.hoverThumbColor", ModernThemePalette.accent());
    }

    private void applySystemFallback() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
            refreshOpenWindows();
        } catch (Exception ignored) {
            // Java varsayilan LookAndFeel son fallback olarak kullanilir.
        }
    }

    private void refreshOpenWindows() {
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            window.invalidate();
            window.validate();
            window.repaint();
        }
    }
}
