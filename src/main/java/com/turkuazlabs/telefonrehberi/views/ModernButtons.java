// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ModernButtons.java
// # 📌 Amac: Uygulama genelindeki primary, secondary, danger, text ve hizli aksiyon butonlarini tek tip uretir.
// # 📌 View - Java
// # Version: 2.24.0
// # Aciklama: Ana aksiyon stillerine ek olarak aktif filtre chip butonlarini da merkezi View bileseninden uretir.
// # Bagimli Oldugu Katman: View | Config
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.UiConfig;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class ModernButtons {
    private static final Color PRIMARY_HOVER = new Color(20, 156, 140);
    private static final Color DANGER_HOVER = new Color(255, 239, 239);
    private static final int BUTTON_ARC = 12;
    private static final int QUICK_MIN_WIDTH = 100;
    private static final int QUICK_ICON_GAP = 8;
    private static final String TEXT_ACTION_SUFFIX = "  >";

    public static JButton primary(String text) {
        return new PrimaryButton(text);
    }

    public static JButton secondary(String text) {
        return new SecondaryButton(text);
    }

    public static JButton danger(String text) {
        return new DangerButton(text);
    }

    public static JButton text(String text) {
        return new TextButton(text);
    }

    public static JButton quick(String text, Icon icon) {
        return new QuickActionButton(text, icon);
    }

    public static JButton chip(String text) {
        return new ChipButton(text);
    }

    private static final class PrimaryButton extends JButton {
        private PrimaryButton(String text) {
            super(text);
            setForeground(Color.WHITE);
            configureBaseButton(this, 10, 18);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isPressed()
                        ? ModernThemePalette.accentStrong()
                        : (getModel().isRollover() ? PRIMARY_HOVER : ModernThemePalette.accent());
                g.setColor(fill);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_ARC, BUTTON_ARC);
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static class SecondaryButton extends JButton {
        private SecondaryButton(String text) {
            super(text);
            configureBaseButton(this, 9, 16);
        }

        protected Color fillColor() {
            return getModel().isRollover() ? ModernThemePalette.surfaceMuted() : ModernThemePalette.surface();
        }

        protected Color borderColor() {
            return ModernThemePalette.border();
        }

        protected Color textColor() {
            return ModernThemePalette.textPrimary();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(fillColor());
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BUTTON_ARC, BUTTON_ARC);
                g.setColor(borderColor());
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BUTTON_ARC, BUTTON_ARC);
            } finally {
                g.dispose();
            }
            setForeground(textColor());
            super.paintComponent(graphics);
        }
    }

    private static final class ChipButton extends SecondaryButton {
        private ChipButton(String text) {
            super(text);
            setBorder(new EmptyBorder(4, 9, 4, 9));
            setPreferredSize(new Dimension(getPreferredSize().width, UiConfig.FILTER_CHIP_HEIGHT));
            setFont(getFont().deriveFont(11.5f));
        }
    }

    private static final class QuickActionButton extends SecondaryButton {
        private QuickActionButton(String text, Icon icon) {
            super(text);
            setIcon(icon);
            setIconTextGap(QUICK_ICON_GAP);
            setHorizontalAlignment(SwingConstants.CENTER);
            setPreferredSize(new Dimension(Math.max(QUICK_MIN_WIDTH, getPreferredSize().width), UiConfig.QUICK_ACTION_HEIGHT));
        }
    }

    private static final class TextButton extends JButton {
        private TextButton(String text) {
            super(text + TEXT_ACTION_SUFFIX);
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(ModernThemePalette.accentStrong());
            setBorder(new EmptyBorder(10, 8, 10, 8));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setRolloverEnabled(true);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            setForeground(getModel().isRollover()
                    ? ModernThemePalette.accent()
                    : ModernThemePalette.accentStrong());
            super.paintComponent(graphics);
        }
    }

    private static final class DangerButton extends SecondaryButton {
        private DangerButton(String text) {
            super(text);
        }

        @Override
        protected Color fillColor() {
            return getModel().isRollover() ? DANGER_HOVER : ModernThemePalette.surface();
        }

        @Override
        protected Color borderColor() {
            return ModernThemePalette.danger();
        }

        @Override
        protected Color textColor() {
            return ModernThemePalette.danger();
        }
    }

    private static void configureBaseButton(JButton button, int verticalPadding, int horizontalPadding) {
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, UiConfig.PRIMARY_BUTTON_HEIGHT));
        button.setRolloverEnabled(true);
    }

    private ModernButtons() {
    }
}
