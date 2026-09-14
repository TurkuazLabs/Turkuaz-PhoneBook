// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ContactActivityTimelineRow.java
// # 📌 Amac: Kisi profilindeki tek aktivite kaydini zaman cizelgesi satiri olarak cizer.
// # 📌 View - Java
// # Version: 1.0.0
// # Aciklama: Islem rozeti, aciklama ve tarih bilgisini dikey timeline noktasi ile modern kart icinde sunar.
// # Bagimli Oldugu Katman: View | Model | Config | Language
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.UiConfig;
import com.turkuazlabs.telefonrehberi.models.HistoryEntry;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class ContactActivityTimelineRow extends JPanel {
    private final boolean last;

    public ContactActivityTimelineRow(HistoryEntry entry, boolean last) {
        this.last = last;
        setOpaque(false);
        setLayout(new BorderLayout(UiConfig.ACTIVITY_ROW_GAP, 0));
        setBorder(BorderFactory.createEmptyBorder(
                UiConfig.ACTIVITY_ROW_PADDING_Y,
                0,
                UiConfig.ACTIVITY_ROW_PADDING_Y,
                0
        ));

        JPanel rail = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                try {
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int center = getWidth() / 2;
                    int dotY = UiConfig.ACTIVITY_DOT_TOP;
                    if (!ContactActivityTimelineRow.this.last) {
                        g.setColor(ModernThemePalette.border());
                        g.fillRect(
                                center,
                                dotY + UiConfig.ACTIVITY_DOT_SIZE,
                                UiConfig.ACTIVITY_LINE_WIDTH,
                                Math.max(0, getHeight() - dotY - UiConfig.ACTIVITY_DOT_SIZE)
                        );
                    }
                    g.setColor(ModernThemePalette.accent());
                    g.fillOval(
                            center - (UiConfig.ACTIVITY_DOT_SIZE / 2),
                            dotY,
                            UiConfig.ACTIVITY_DOT_SIZE,
                            UiConfig.ACTIVITY_DOT_SIZE
                    );
                } finally {
                    g.dispose();
                }
            }
        };
        rail.setOpaque(false);
        rail.setPreferredSize(new Dimension(UiConfig.ACTIVITY_RAIL_WIDTH, UiConfig.ACTIVITY_ROW_MIN_HEIGHT));
        add(rail, BorderLayout.WEST);

        JPanel text = new JPanel(new BorderLayout(0, UiConfig.ACTIVITY_TEXT_GAP));
        text.setOpaque(false);

        JLabel action = new JLabel(HistoryActionText.label(entry.action()));
        action.setForeground(ModernThemePalette.textPrimary());
        action.setFont(action.getFont().deriveFont(Font.BOLD, UiConfig.ACTIVITY_ACTION_FONT_SIZE));

        JLabel description = new JLabel(HistoryActionText.description(entry.action()));
        description.setForeground(ModernThemePalette.textSecondary());
        description.setFont(description.getFont().deriveFont(Font.PLAIN, UiConfig.ACTIVITY_DESCRIPTION_FONT_SIZE));

        JLabel date = new JLabel(entry.createdAt());
        date.setForeground(ModernThemePalette.textSecondary());
        date.setFont(date.getFont().deriveFont(Font.PLAIN, UiConfig.ACTIVITY_DATE_FONT_SIZE));

        JPanel center = new JPanel(new BorderLayout(0, UiConfig.ACTIVITY_TEXT_GAP));
        center.setOpaque(false);
        center.add(action, BorderLayout.NORTH);
        center.add(description, BorderLayout.CENTER);

        text.add(center, BorderLayout.CENTER);
        text.add(date, BorderLayout.EAST);
        add(text, BorderLayout.CENTER);
    }
}
