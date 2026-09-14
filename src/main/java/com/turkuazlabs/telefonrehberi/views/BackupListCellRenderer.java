// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/BackupListCellRenderer.java
// # 📌 Amac: Yedekleme gecmisindeki dosyalari modern kart satirlari olarak gosterir.
// # 📌 View - Java
// # Version: 1.0.0
// # Aciklama: Yedek dosya adini, yerel yedek aciklamasini ve secim vurgusunu tek renderer icinde sunar.
// # Bagimli Oldugu Katman: View | Config | Language
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.language.Messages;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class BackupListCellRenderer extends JPanel implements ListCellRenderer<String> {
    private final JLabel titleLabel = new JLabel();
    private final JLabel subtitleLabel = new JLabel(Messages.BACKUP_LIST_ITEM_SUBTITLE);
    private boolean selected;

    public BackupListCellRenderer() {
        setOpaque(false);
        setLayout(new BorderLayout(12, 0));
        setBorder(new EmptyBorder(7, 10, 7, 10));
        setPreferredSize(new Dimension(0, 62));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new javax.swing.BoxLayout(text, javax.swing.BoxLayout.Y_AXIS));

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13.5f));
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 11.5f));
        text.add(titleLabel);
        text.add(javax.swing.Box.createVerticalStrut(4));
        text.add(subtitleLabel);
        add(text, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends String> list,
            String value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        selected = isSelected;
        titleLabel.setText(value == null || value.isBlank() ? Messages.EMPTY_VALUE : value);
        titleLabel.setForeground(ModernThemePalette.textPrimary());
        subtitleLabel.setForeground(ModernThemePalette.textSecondary());
        setToolTipText(value);
        return this;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = selected ? ModernThemePalette.accentSoft() : ModernThemePalette.surfaceElevated();
            g.setColor(fill);
            g.fillRoundRect(3, 3, Math.max(1, getWidth() - 6), Math.max(1, getHeight() - 6), 14, 14);
            g.setColor(selected ? ModernThemePalette.accent() : ModernThemePalette.border());
            g.drawRoundRect(3, 3, Math.max(1, getWidth() - 6), Math.max(1, getHeight() - 6), 14, 14);
            if (selected) {
                g.fillRoundRect(4, 13, 3, Math.max(18, getHeight() - 26), 3, 3);
            }
        } finally {
            g.dispose();
        }
        super.paintComponent(graphics);
    }
}
