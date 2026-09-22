// # Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ModernButtons.java
// # Amac: Uygulama genelindeki aksiyon butonlarini tek tip, tema-duyarli ve klavye erisilebilir sekilde uretir.
// # View - Java
// # Version: 3.0.0
// # Aciklama: Tema v3 buton sistemi; primary, secondary, danger, text, quick ve chip varyantlarinda hover, pressed, disabled ve focus durumlarini cizer.
// # Bagimli Oldugu Katman: View | Config
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.UiConfig;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class ModernButtons {
    public static JButton primary(String text) {
        return new StyledButton(text, Variant.PRIMARY, UiConfig.BUTTON_MIN_WIDTH, UiConfig.PRIMARY_BUTTON_HEIGHT, UiConfig.BUTTON_ARC, true);
    }

    public static JButton secondary(String text) {
        return new StyledButton(text, Variant.SECONDARY, UiConfig.BUTTON_MIN_WIDTH, UiConfig.PRIMARY_BUTTON_HEIGHT, UiConfig.BUTTON_ARC, false);
    }

    public static JButton danger(String text) {
        return new StyledButton(text, Variant.DANGER, UiConfig.BUTTON_MIN_WIDTH, UiConfig.PRIMARY_BUTTON_HEIGHT, UiConfig.BUTTON_ARC, true);
    }

    public static JButton text(String text) {
        return new StyledButton(text, Variant.TEXT, 0, UiConfig.TEXT_BUTTON_HEIGHT, UiConfig.BUTTON_ARC, false);
    }

    public static JButton quick(String text, Icon icon) {
        StyledButton button = new StyledButton(
                text,
                Variant.SECONDARY,
                100,
                UiConfig.QUICK_ACTION_HEIGHT,
                UiConfig.BUTTON_ARC,
                false
        );
        button.setIcon(icon);
        button.setIconTextGap(8);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        return button;
    }

    public static JButton chip(String text) {
        StyledButton button = new StyledButton(
                text,
                Variant.CHIP,
                0,
                UiConfig.FILTER_CHIP_HEIGHT,
                UiConfig.CHIP_ARC,
                false
        );
        button.setBorder(new EmptyBorder(4, 10, 4, 10));
        button.setFont(button.getFont().deriveFont(Font.PLAIN, UiConfig.CHIP_FONT_SIZE));
        return button;
    }

    private enum Variant {
        PRIMARY,
        SECONDARY,
        DANGER,
        TEXT,
        CHIP
    }

    private static final class StyledButton extends JButton {
        private final Variant variant;
        private final int minimumWidth;
        private final int fixedHeight;
        private final int arc;

        private StyledButton(
                String text,
                Variant variant,
                int minimumWidth,
                int fixedHeight,
                int arc,
                boolean emphasized
        ) {
            super(text);
            this.variant = variant;
            this.minimumWidth = minimumWidth;
            this.fixedHeight = fixedHeight;
            this.arc = arc;

            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 15, 7, 15));
            setFont(getFont().deriveFont(
                    emphasized ? Font.BOLD : Font.PLAIN,
                    variant == Variant.TEXT ? UiConfig.TEXT_BUTTON_FONT_SIZE : UiConfig.BUTTON_FONT_SIZE
            ));
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            setCursor(Cursor.getPredefinedCursor(enabled ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension preferred = super.getPreferredSize();
            return new Dimension(Math.max(minimumWidth, preferred.width), fixedHeight);
        }

        @Override
        public Dimension getMinimumSize() {
            Dimension preferred = getPreferredSize();
            return new Dimension(minimumWidth, preferred.height);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            boolean focused = isEnabled() && isFocusOwner();
            boolean pressed = isEnabled() && getModel().isPressed();
            boolean hovered = isEnabled() && getModel().isRollover();

            setForeground(resolveForeground());
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (focused) {
                    g.setColor(ModernThemePalette.focusRing());
                    g.setStroke(new BasicStroke(UiConfig.BUTTON_FOCUS_RING_WIDTH));
                    g.drawRoundRect(
                            1,
                            1,
                            Math.max(1, getWidth() - 3),
                            Math.max(1, getHeight() - 3),
                            arc + 2,
                            arc + 2
                    );
                }

                int inset = focused ? UiConfig.BUTTON_FOCUS_RING_WIDTH + 1 : 1;
                int width = Math.max(1, getWidth() - (inset * 2) - 1);
                int height = Math.max(1, getHeight() - (inset * 2) - 1);
                Color fill = resolveFill(pressed, hovered);
                Color border = resolveBorder(hovered);

                if (fill != null) {
                    g.setColor(fill);
                    g.fillRoundRect(inset, inset, width, height, arc, arc);
                }
                if (border != null) {
                    g.setColor(border);
                    g.setStroke(new BasicStroke(1f));
                    g.drawRoundRect(inset, inset, width, height, arc, arc);
                }
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }

        private Color resolveFill(boolean pressed, boolean hovered) {
            if (!isEnabled()) {
                return variant == Variant.TEXT ? null : ModernThemePalette.disabledSurface();
            }
            return switch (variant) {
                case PRIMARY -> pressed
                        ? ModernThemePalette.actionPressed()
                        : hovered ? ModernThemePalette.actionHover() : ModernThemePalette.actionFill();
                case SECONDARY -> pressed
                        ? ModernThemePalette.controlPressed()
                        : hovered ? ModernThemePalette.controlHover() : ModernThemePalette.surfaceElevated();
                case DANGER -> pressed
                        ? ModernThemePalette.dangerPressed()
                        : hovered ? ModernThemePalette.dangerHover() : ModernThemePalette.surfaceElevated();
                case TEXT -> pressed
                        ? ModernThemePalette.selectionBackground()
                        : hovered ? ModernThemePalette.accentSoft() : null;
                case CHIP -> pressed
                        ? ModernThemePalette.selectionBackground()
                        : hovered ? ModernThemePalette.controlHover() : ModernThemePalette.surfaceMuted();
            };
        }

        private Color resolveBorder(boolean hovered) {
            if (!isEnabled()) {
                return variant == Variant.TEXT ? null : ModernThemePalette.border();
            }
            return switch (variant) {
                case PRIMARY, TEXT -> null;
                case SECONDARY, CHIP -> hovered ? ModernThemePalette.borderStrong() : ModernThemePalette.border();
                case DANGER -> ModernThemePalette.dangerBorder();
            };
        }

        private Color resolveForeground() {
            if (!isEnabled()) {
                return ModernThemePalette.disabledText();
            }
            return switch (variant) {
                case PRIMARY -> ModernThemePalette.actionForeground();
                case SECONDARY, CHIP -> ModernThemePalette.textPrimary();
                case DANGER -> ModernThemePalette.danger();
                case TEXT -> ModernThemePalette.accentStrong();
            };
        }
    }

    private ModernButtons() {
    }
}
