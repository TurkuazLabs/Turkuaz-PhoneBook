// # Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/FlatLafThemeTool.java
// # Amac: FlatLaf tema motorunu Swing arayuzune uygular ve tum standart kontrolleri tema tokenlariyla esler.
// # Tool - Java
// # Version: 2.0.0
// # Aciklama: Tema v3; buton, input, liste, tablo, sekme, menu, tooltip, progress, focus ve scrollbar durumlarini tek semantik paletten uygular.
// # Bagimli Oldugu Katman: Tool | Config | Model
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
        ThemeMode safeMode = mode == null ? ThemeMode.LIGHT : mode;
        String className = safeMode == ThemeMode.DARK
                ? UiConfig.FLATLAF_DARK_CLASS
                : UiConfig.FLATLAF_LIGHT_CLASS;
        try {
            Class<?> lookAndFeelClass = Class.forName(className);
            Method setupMethod = lookAndFeelClass.getMethod("setup");
            Object result = setupMethod.invoke(null);
            boolean applied = !(result instanceof Boolean booleanResult) || booleanResult;
            if (applied) {
                UIManager.put("laf.dark", safeMode == ThemeMode.DARK);
                applyGlobalDefaults();
                refreshOpenWindows();
                return true;
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // FlatLaf hazirlanamazsa marka tokenlariyla sistem LookAndFeel fallback'i kullanilir.
        }
        applySystemFallback(safeMode);
        return false;
    }

    private void applyGlobalDefaults() {
        UIManager.put("defaultFont", new Font("Segoe UI Variable", Font.PLAIN, 14));

        UIManager.put("Component.arc", 10);
        UIManager.put("Component.focusWidth", UiConfig.BUTTON_FOCUS_RING_WIDTH);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Component.arrowType", "chevron");
        UIManager.put("Component.borderColor", ModernThemePalette.border());
        UIManager.put("Component.disabledBorderColor", ModernThemePalette.border());
        UIManager.put("Component.focusColor", ModernThemePalette.focusRing());
        UIManager.put("Component.focusedBorderColor", ModernThemePalette.focusRing());

        UIManager.put("Button.arc", UiConfig.BUTTON_ARC);
        UIManager.put("Button.margin", new Insets(7, 14, 7, 14));
        UIManager.put("Button.minimumWidth", UiConfig.BUTTON_MIN_WIDTH);
        UIManager.put("Button.background", ModernThemePalette.surfaceElevated());
        UIManager.put("Button.foreground", ModernThemePalette.textPrimary());
        UIManager.put("Button.hoverBackground", ModernThemePalette.controlHover());
        UIManager.put("Button.pressedBackground", ModernThemePalette.controlPressed());
        UIManager.put("Button.disabledBackground", ModernThemePalette.disabledSurface());
        UIManager.put("Button.disabledText", ModernThemePalette.disabledText());
        UIManager.put("Button.borderColor", ModernThemePalette.border());
        UIManager.put("Button.hoverBorderColor", ModernThemePalette.borderStrong());
        UIManager.put("Button.focusedBorderColor", ModernThemePalette.focusRing());

        UIManager.put("ToggleButton.background", ModernThemePalette.surfaceElevated());
        UIManager.put("ToggleButton.foreground", ModernThemePalette.textPrimary());
        UIManager.put("ToggleButton.selectedBackground", ModernThemePalette.accentSoft());
        UIManager.put("ToggleButton.selectedForeground", ModernThemePalette.accentStrong());
        UIManager.put("ToggleButton.hoverBackground", ModernThemePalette.controlHover());

        UIManager.put("TextComponent.arc", 10);
        UIManager.put("TextField.margin", new Insets(8, 11, 8, 11));
        UIManager.put("PasswordField.margin", new Insets(8, 11, 8, 11));
        UIManager.put("FormattedTextField.margin", new Insets(8, 11, 8, 11));
        UIManager.put("TextField.background", ModernThemePalette.inputBackground());
        UIManager.put("TextField.foreground", ModernThemePalette.textPrimary());
        UIManager.put("TextField.inactiveBackground", ModernThemePalette.disabledSurface());
        UIManager.put("TextField.inactiveForeground", ModernThemePalette.disabledText());
        UIManager.put("TextField.caretForeground", ModernThemePalette.accentStrong());
        UIManager.put("TextArea.background", ModernThemePalette.inputBackground());
        UIManager.put("TextArea.foreground", ModernThemePalette.textPrimary());
        UIManager.put("TextArea.caretForeground", ModernThemePalette.accentStrong());
        UIManager.put("TextPane.background", ModernThemePalette.inputBackground());
        UIManager.put("TextPane.foreground", ModernThemePalette.textPrimary());
        UIManager.put("EditorPane.background", ModernThemePalette.inputBackground());
        UIManager.put("EditorPane.foreground", ModernThemePalette.textPrimary());
        UIManager.put("TextField.selectionBackground", ModernThemePalette.selectionBackground());
        UIManager.put("TextField.selectionForeground", ModernThemePalette.selectionForeground());
        UIManager.put("TextArea.selectionBackground", ModernThemePalette.selectionBackground());
        UIManager.put("TextArea.selectionForeground", ModernThemePalette.selectionForeground());

        UIManager.put("ComboBox.padding", new Insets(7, 10, 7, 10));
        UIManager.put("ComboBox.background", ModernThemePalette.inputBackground());
        UIManager.put("ComboBox.foreground", ModernThemePalette.textPrimary());
        UIManager.put("ComboBox.selectionBackground", ModernThemePalette.selectionBackground());
        UIManager.put("ComboBox.selectionForeground", ModernThemePalette.selectionForeground());
        UIManager.put("Spinner.background", ModernThemePalette.inputBackground());
        UIManager.put("Spinner.foreground", ModernThemePalette.textPrimary());

        UIManager.put("List.background", ModernThemePalette.surface());
        UIManager.put("List.foreground", ModernThemePalette.textPrimary());
        UIManager.put("List.selectionBackground", ModernThemePalette.selectionBackground());
        UIManager.put("List.selectionForeground", ModernThemePalette.selectionForeground());

        UIManager.put("Tree.background", ModernThemePalette.surface());
        UIManager.put("Tree.foreground", ModernThemePalette.textPrimary());
        UIManager.put("Tree.selectionBackground", ModernThemePalette.selectionBackground());
        UIManager.put("Tree.selectionForeground", ModernThemePalette.selectionForeground());

        UIManager.put("Table.background", ModernThemePalette.surface());
        UIManager.put("Table.foreground", ModernThemePalette.textPrimary());
        UIManager.put("Table.selectionBackground", ModernThemePalette.selectionBackground());
        UIManager.put("Table.selectionForeground", ModernThemePalette.selectionForeground());
        UIManager.put("Table.gridColor", ModernThemePalette.border());
        UIManager.put("TableHeader.background", ModernThemePalette.tableHeaderBackground());
        UIManager.put("TableHeader.foreground", ModernThemePalette.textPrimary());
        UIManager.put("TableHeader.separatorColor", ModernThemePalette.border());

        UIManager.put("TabbedPane.tabHeight", 38);
        UIManager.put("TabbedPane.tabInsets", new Insets(7, 14, 7, 14));
        UIManager.put("TabbedPane.showTabSeparators", false);
        UIManager.put("TabbedPane.background", ModernThemePalette.surface());
        UIManager.put("TabbedPane.selectedBackground", ModernThemePalette.surfaceElevated());
        UIManager.put("TabbedPane.foreground", ModernThemePalette.textSecondary());
        UIManager.put("TabbedPane.selectedForeground", ModernThemePalette.textPrimary());
        UIManager.put("TabbedPane.underlineColor", ModernThemePalette.accent());
        UIManager.put("TabbedPane.inactiveUnderlineColor", ModernThemePalette.borderStrong());
        UIManager.put("TabbedPane.focusColor", ModernThemePalette.accentSoft());

        UIManager.put("MenuBar.background", ModernThemePalette.surfaceElevated());
        UIManager.put("MenuBar.foreground", ModernThemePalette.textPrimary());
        UIManager.put("Menu.background", ModernThemePalette.surfaceElevated());
        UIManager.put("Menu.foreground", ModernThemePalette.textPrimary());
        UIManager.put("Menu.selectionBackground", ModernThemePalette.accentSoft());
        UIManager.put("Menu.selectionForeground", ModernThemePalette.textPrimary());
        UIManager.put("MenuItem.background", ModernThemePalette.surfaceElevated());
        UIManager.put("MenuItem.foreground", ModernThemePalette.textPrimary());
        UIManager.put("MenuItem.selectionBackground", ModernThemePalette.accentSoft());
        UIManager.put("MenuItem.selectionForeground", ModernThemePalette.textPrimary());
        UIManager.put("MenuItem.selectionType", "default");
        UIManager.put("PopupMenu.background", ModernThemePalette.surfaceElevated());
        UIManager.put("PopupMenu.borderColor", ModernThemePalette.border());

        UIManager.put("ProgressBar.arc", 999);
        UIManager.put("ProgressBar.background", ModernThemePalette.surfaceMuted());
        UIManager.put("ProgressBar.foreground", ModernThemePalette.accent());

        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("ScrollBar.track", ModernThemePalette.backgroundAlt());
        UIManager.put("ScrollBar.thumb", ModernThemePalette.scrollbarThumb());
        UIManager.put("ScrollBar.hoverThumbColor", ModernThemePalette.scrollbarHoverThumb());
        UIManager.put("ScrollBar.pressedThumbColor", ModernThemePalette.accent());

        UIManager.put("Separator.foreground", ModernThemePalette.border());
        UIManager.put("ToolTip.arc", 8);
        UIManager.put("ToolTip.background", ModernThemePalette.surfaceElevated());
        UIManager.put("ToolTip.foreground", ModernThemePalette.textPrimary());
        UIManager.put("ToolTip.borderColor", ModernThemePalette.border());

        UIManager.put("Panel.background", ModernThemePalette.background());
        UIManager.put("RootPane.background", ModernThemePalette.background());
        UIManager.put("Label.foreground", ModernThemePalette.textPrimary());
        UIManager.put("TitlePane.unifiedBackground", true);
        UIManager.put("TitlePane.background", ModernThemePalette.backgroundAlt());
        UIManager.put("TitlePane.inactiveBackground", ModernThemePalette.background());
        UIManager.put("TitlePane.foreground", ModernThemePalette.textPrimary());
    }

    private void applySystemFallback(ThemeMode mode) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("laf.dark", mode == ThemeMode.DARK);
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
            applyGlobalDefaults();
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
