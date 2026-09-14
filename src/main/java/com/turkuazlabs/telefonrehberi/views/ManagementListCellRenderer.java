// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ManagementListCellRenderer.java
// # 📌 Amac: Grup, etiket ve akilli liste kayitlarini modern kart satirlari olarak cizer.
// # 📌 View - Java
// # Version: 1.0.0
// # Aciklama: Secim vurgusu, renk noktasi, ikincil aciklama ve sag bilgi rozeti ile yonetim listelerini ortak gorunumde sunar.
// # Bagimli Oldugu Katman: View | Model | Config | Language
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.LabelColorPalette;
import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.UiConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.GroupRecord;
import com.turkuazlabs.telefonrehberi.models.SmartList;
import com.turkuazlabs.telefonrehberi.models.TagRecord;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class ManagementListCellRenderer extends JPanel implements ListCellRenderer<Object> {
    private final JPanel colorDot = new ColorDotPanel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel subtitleLabel = new JLabel();
    private final JLabel badgeLabel = new JLabel();
    private boolean selected;
    private Color accent = ModernThemePalette.accent();

    public ManagementListCellRenderer() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 0));
        setBorder(BorderFactory.createEmptyBorder(
                UiConfig.MANAGEMENT_LIST_CELL_PADDING_Y,
                UiConfig.MANAGEMENT_LIST_CELL_PADDING_X,
                UiConfig.MANAGEMENT_LIST_CELL_PADDING_Y,
                UiConfig.MANAGEMENT_LIST_CELL_PADDING_X
        ));

        colorDot.setOpaque(false);
        colorDot.setPreferredSize(new Dimension(UiConfig.MANAGEMENT_COLOR_DOT_SIZE, UiConfig.MANAGEMENT_COLOR_DOT_SIZE));
        add(colorDot, BorderLayout.WEST);

        JPanel text = new JPanel(new BorderLayout(0, 4));
        text.setOpaque(false);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, UiConfig.MANAGEMENT_ITEM_TITLE_SIZE));
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, UiConfig.MANAGEMENT_ITEM_SUBTITLE_SIZE));
        text.add(titleLabel, BorderLayout.NORTH);
        text.add(subtitleLabel, BorderLayout.CENTER);
        add(text, BorderLayout.CENTER);

        badgeLabel.setHorizontalAlignment(JLabel.RIGHT);
        badgeLabel.setFont(badgeLabel.getFont().deriveFont(Font.BOLD, UiConfig.MANAGEMENT_ITEM_BADGE_SIZE));
        add(badgeLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        selected = isSelected;
        if (value instanceof GroupRecord group) {
            accent = LabelColorPalette.resolve(group.color());
            titleLabel.setText(group.name());
            subtitleLabel.setText(String.format(Messages.MANAGEMENT_COLOR_FORMAT, group.color()));
            badgeLabel.setText(String.format(Messages.MANAGEMENT_CONTACT_COUNT_FORMAT, group.contactCount()));
        } else if (value instanceof TagRecord tag) {
            accent = LabelColorPalette.resolve(tag.color());
            titleLabel.setText(tag.name());
            subtitleLabel.setText(String.format(Messages.MANAGEMENT_COLOR_FORMAT, tag.color()));
            badgeLabel.setText(String.format(Messages.MANAGEMENT_CONTACT_COUNT_FORMAT, tag.contactCount()));
        } else if (value instanceof SmartList rule) {
            accent = ModernThemePalette.accent();
            titleLabel.setText(rule.name());
            subtitleLabel.setText(Messages.smartRuleSummary(rule.field(), rule.operator(), rule.value()));
            badgeLabel.setText(Messages.MANAGEMENT_RULE_BADGE);
        } else {
            accent = ModernThemePalette.accent();
            titleLabel.setText(value == null ? Messages.EMPTY_VALUE : value.toString());
            subtitleLabel.setText("");
            badgeLabel.setText("");
        }

        titleLabel.setForeground(selected ? ModernThemePalette.accentStrong() : ModernThemePalette.textPrimary());
        subtitleLabel.setForeground(ModernThemePalette.textSecondary());
        badgeLabel.setForeground(selected ? ModernThemePalette.accentStrong() : ModernThemePalette.textSecondary());
        setToolTipText(titleLabel.getText());
        return this;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int margin = UiConfig.MANAGEMENT_LIST_CELL_MARGIN;
            int width = Math.max(1, getWidth() - (margin * 2));
            int height = Math.max(1, getHeight() - (margin * 2));
            g.setColor(selected ? ModernThemePalette.accentSoft() : ModernThemePalette.surfaceMuted());
            g.fillRoundRect(margin, margin, width, height,
                    UiConfig.MANAGEMENT_LIST_CELL_ARC, UiConfig.MANAGEMENT_LIST_CELL_ARC);
            g.setColor(selected ? ModernThemePalette.accent() : ModernThemePalette.border());
            g.setStroke(new BasicStroke(1f));
            g.drawRoundRect(margin, margin, width, height,
                    UiConfig.MANAGEMENT_LIST_CELL_ARC, UiConfig.MANAGEMENT_LIST_CELL_ARC);
            if (selected) {
                int y = UiConfig.MANAGEMENT_SELECTED_BAR_INSET;
                int h = Math.max(16, getHeight() - (y * 2));
                g.setColor(ModernThemePalette.accent());
                g.fillRoundRect(margin + 1, y, UiConfig.MANAGEMENT_SELECTED_BAR_WIDTH, h, 3, 3);
            }
        } finally {
            g.dispose();
        }
        super.paintComponent(graphics);
    }

    private final class ColorDotPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(UiConfig.MANAGEMENT_COLOR_DOT_SIZE, Math.min(getWidth(), getHeight()));
                int x = Math.max(0, (getWidth() - size) / 2);
                int y = Math.max(0, (getHeight() - size) / 2);
                g.setColor(accent);
                g.fillOval(x, y, size, size);
            } finally {
                g.dispose();
            }
        }
    }
}
