// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/OperationsListCellRenderer.java
// # 📌 Amac: Gecmis, mukerrer aday ve Cop Kutusu kayitlarini modern kart satirlari olarak cizer.
// # 📌 View - Java
// # Version: 1.0.0
// # Aciklama: Islem arac ekranlarinda baslik, ikincil bilgi, rozet ve secim vurgusunu ortak renderer ile sunar.
// # Bagimli Oldugu Katman: View | Model | Config | Language
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.UiConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.DuplicateCandidate;
import com.turkuazlabs.telefonrehberi.models.HistoryEntry;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class OperationsListCellRenderer extends JPanel implements ListCellRenderer<Object> {
    private final JLabel iconLabel = new JLabel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel subtitleLabel = new JLabel();
    private final JLabel badgeLabel = new JLabel();
    private boolean selected;

    public OperationsListCellRenderer() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 0));
        setBorder(BorderFactory.createEmptyBorder(
                UiConfig.MANAGEMENT_LIST_CELL_PADDING_Y,
                UiConfig.MANAGEMENT_LIST_CELL_PADDING_X,
                UiConfig.MANAGEMENT_LIST_CELL_PADDING_Y,
                UiConfig.MANAGEMENT_LIST_CELL_PADDING_X
        ));

        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setPreferredSize(new Dimension(24, 24));
        iconLabel.setFont(iconLabel.getFont().deriveFont(Font.BOLD, 15f));
        add(iconLabel, BorderLayout.WEST);

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
        if (value instanceof HistoryEntry entry) {
            iconLabel.setText(Messages.OPERATION_HISTORY_ICON);
            titleLabel.setText(entry.contactName());
            subtitleLabel.setText(entry.createdAt());
            badgeLabel.setText(HistoryActionText.label(entry.action()));
        } else if (value instanceof DuplicateCandidate candidate) {
            iconLabel.setText(Messages.OPERATION_DUPLICATE_ICON);
            titleLabel.setText(candidate.primary().name() + Messages.OPERATION_DUPLICATE_SEPARATOR + candidate.duplicate().name());
            subtitleLabel.setText(candidate.reason());
            badgeLabel.setText(Messages.OPERATION_DUPLICATE_BADGE);
        } else if (value instanceof Contact contact) {
            iconLabel.setText(Messages.OPERATION_TRASH_ICON);
            titleLabel.setText(contact.name());
            subtitleLabel.setText(String.format(Messages.OPERATION_CONTACT_META_FORMAT, valueOrDash(contact.phone()), valueOrDash(contact.email())));
            badgeLabel.setText(Messages.OPERATION_TRASH_BADGE);
        } else {
            iconLabel.setText(Messages.OPERATION_GENERIC_ICON);
            titleLabel.setText(value == null ? Messages.EMPTY_VALUE : value.toString());
            subtitleLabel.setText("");
            badgeLabel.setText("");
        }

        iconLabel.setForeground(selected ? ModernThemePalette.accentStrong() : ModernThemePalette.textSecondary());
        titleLabel.setForeground(selected ? ModernThemePalette.accentStrong() : ModernThemePalette.textPrimary());
        subtitleLabel.setForeground(ModernThemePalette.textSecondary());
        badgeLabel.setForeground(selected ? ModernThemePalette.accentStrong() : ModernThemePalette.textSecondary());
        setToolTipText(titleLabel.getText());
        return this;
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? Messages.EMPTY_VALUE : value;
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
}
