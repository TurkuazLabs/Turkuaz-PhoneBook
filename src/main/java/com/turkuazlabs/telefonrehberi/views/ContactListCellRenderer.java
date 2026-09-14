// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ContactListCellRenderer.java
// # 📌 Amac: Kisi listesini modern kart satirlari, avatar, oncelikli iletisim ve meta bilgilerle cizer.
// # 📌 View - Java
// # Version: 2.33.3
// # Aciklama: Duz ve ferah liste satirlariyla secili/hover durumlarini ayirir; kisi bilgisini daha az gorsel gurultu ile sunar.
// # Bagimli Oldugu Katman: View | Model | Config | Language | Tool
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.UiConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.tools.ContactPhotoTool;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

public final class ContactListCellRenderer extends JPanel implements ListCellRenderer<Contact> {
    private final JLabel avatarLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel detailLabel = new JLabel();
    private final JLabel metaLabel = new JLabel();
    private final JLabel favoriteLabel = new JLabel(Messages.CONTACT_LIST_FAVORITE_SYMBOL, SwingConstants.CENTER);
    private final Component metaGap = Box.createVerticalStrut(UiConfig.CONTACT_LIST_TEXT_GAP);
    private final ContactPhotoTool photoTool = new ContactPhotoTool();
    private boolean selected;
    private boolean hovered;
    private int hoveredIndex = -1;

    public ContactListCellRenderer() {
        setOpaque(false);
        setLayout(new BorderLayout(UiConfig.CONTACT_LIST_CELL_GAP, 0));
        setBorder(new EmptyBorder(
                UiConfig.CONTACT_LIST_CELL_PADDING_Y,
                UiConfig.CONTACT_LIST_CELL_PADDING_X,
                UiConfig.CONTACT_LIST_CELL_PADDING_Y,
                UiConfig.CONTACT_LIST_CELL_PADDING_X
        ));

        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setVerticalAlignment(SwingConstants.CENTER);
        avatarLabel.setPreferredSize(new Dimension(UiConfig.CONTACT_LIST_AVATAR_SIZE, UiConfig.CONTACT_LIST_AVATAR_SIZE));
        add(avatarLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, UiConfig.CONTACT_LIST_NAME_FONT_SIZE));
        detailLabel.setFont(detailLabel.getFont().deriveFont(Font.PLAIN, UiConfig.CONTACT_LIST_DETAIL_FONT_SIZE));
        metaLabel.setFont(metaLabel.getFont().deriveFont(Font.PLAIN, UiConfig.CONTACT_LIST_META_FONT_SIZE));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(Box.createVerticalGlue());
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(UiConfig.CONTACT_LIST_TEXT_GAP));
        textPanel.add(detailLabel);
        textPanel.add(metaGap);
        textPanel.add(metaLabel);
        textPanel.add(Box.createVerticalGlue());
        add(textPanel, BorderLayout.CENTER);

        favoriteLabel.setForeground(ModernThemePalette.warning());
        favoriteLabel.setFont(favoriteLabel.getFont().deriveFont(Font.PLAIN, UiConfig.CONTACT_LIST_FAVORITE_FONT_SIZE));
        favoriteLabel.setPreferredSize(new Dimension(20, 20));
        favoriteLabel.setVerticalAlignment(SwingConstants.TOP);
        add(favoriteLabel, BorderLayout.EAST);
    }

    public void setHoveredIndex(int index) {
        hoveredIndex = index;
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Contact> list,
            Contact contact,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        selected = isSelected;
        hovered = index == hoveredIndex && !isSelected;
        boolean compact = list.getFixedCellHeight() <= UiConfig.CONTACT_LIST_ROW_HEIGHT_COMPACT;

        nameLabel.setText(ellipsize(valueOrDash(contact.name()), UiConfig.CONTACT_LIST_NAME_MAX_CHARS));
        detailLabel.setText(ellipsize(primaryDetail(contact), UiConfig.CONTACT_LIST_DETAIL_MAX_CHARS));

        String meta = metaDetail(contact);
        boolean showMeta = !compact && !meta.isBlank();
        metaLabel.setText(ellipsize(meta, UiConfig.CONTACT_LIST_META_MAX_CHARS));
        metaLabel.setVisible(showMeta);
        metaGap.setVisible(showMeta);
        favoriteLabel.setVisible(contact.favorite());

        avatarLabel.setIcon(contactIcon(contact));
        nameLabel.setForeground(ModernThemePalette.textPrimary());
        detailLabel.setForeground(selected ? ModernThemePalette.accentStrong() : ModernThemePalette.textSecondary());
        metaLabel.setForeground(ModernThemePalette.textSecondary());
        setToolTipText(buildTooltip(contact));
        return this;
    }

    private Icon contactIcon(Contact contact) {
        if (contact.photo() != null && contact.photo().length > 0) {
            Image image = photoTool.scaledImage(
                    contact.photo(), UiConfig.CONTACT_LIST_AVATAR_SIZE, UiConfig.CONTACT_LIST_AVATAR_SIZE
            );
            if (image != null) {
                return new CircularPhotoIcon(image);
            }
        }
        return new InitialsIcon(contact.name());
    }

    private String primaryDetail(Contact contact) {
        String phone = clean(contact.phone());
        if (!phone.isBlank()) {
            return phone;
        }
        String email = clean(contact.email());
        if (!email.isBlank()) {
            return email;
        }
        String company = clean(contact.company());
        return company.isBlank() ? Messages.EMPTY_VALUE : company;
    }

    private String metaDetail(Contact contact) {
        String company = clean(contact.company());
        String category = clean(contact.category());
        if (!company.isBlank() && !category.isBlank()) {
            return company + Messages.CONTACT_LIST_META_SEPARATOR + category;
        }
        if (!company.isBlank()) {
            return company;
        }
        return category;
    }

    private String buildTooltip(Contact contact) {
        String company = clean(contact.company());
        String phone = clean(contact.phone());
        String email = clean(contact.email());
        String category = clean(contact.category());
        StringBuilder builder = new StringBuilder("<html><b>")
                .append(valueOrDash(contact.name()))
                .append("</b>");
        if (!phone.isBlank()) {
            builder.append("<br>").append(phone);
        }
        if (!email.isBlank()) {
            builder.append("<br>").append(email);
        }
        if (!company.isBlank()) {
            builder.append("<br>").append(company);
        }
        if (!category.isBlank()) {
            builder.append("<br>").append(category);
        }
        return builder.append("</html>").toString();
    }

    private String ellipsize(String value, int maxChars) {
        if (value == null || value.length() <= maxChars) {
            return value;
        }
        return value.substring(0, Math.max(1, maxChars - 1)) + "…";
    }

    private String valueOrDash(String value) {
        String cleaned = clean(value);
        return cleaned.isBlank() ? Messages.EMPTY_VALUE : cleaned;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int margin = UiConfig.CONTACT_LIST_CELL_MARGIN;
            int width = Math.max(1, getWidth() - (margin * 2));
            int height = Math.max(1, getHeight() - (margin * 2));

            if (selected) {
                g.setColor(ModernThemePalette.accentSoft());
                g.fillRoundRect(margin, margin, width, height, UiConfig.CONTACT_LIST_CELL_ARC, UiConfig.CONTACT_LIST_CELL_ARC);
                g.setColor(ModernThemePalette.accent());
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(margin, margin, width, height, UiConfig.CONTACT_LIST_CELL_ARC, UiConfig.CONTACT_LIST_CELL_ARC);
            } else if (hovered) {
                g.setColor(ModernThemePalette.surfaceMuted());
                g.fillRoundRect(margin, margin, width, height, UiConfig.CONTACT_LIST_CELL_ARC, UiConfig.CONTACT_LIST_CELL_ARC);
            }

            if (selected) {
                int barInset = UiConfig.CONTACT_LIST_SELECTED_BAR_INSET;
                g.setColor(ModernThemePalette.accent());
                g.fillRoundRect(
                        margin,
                        margin + barInset,
                        UiConfig.CONTACT_LIST_SELECTED_BAR_WIDTH,
                        Math.max(12, height - (barInset * 2)),
                        UiConfig.CONTACT_LIST_SELECTED_BAR_WIDTH,
                        UiConfig.CONTACT_LIST_SELECTED_BAR_WIDTH
                );
            }
        } finally {
            g.dispose();
        }
        super.paintComponent(graphics);
    }

    private static final class CircularPhotoIcon implements Icon {
        private final Image image;

        private CircularPhotoIcon(Image image) {
            this.image = image;
        }

        @Override
        public int getIconWidth() {
            return UiConfig.CONTACT_LIST_AVATAR_SIZE;
        }

        @Override
        public int getIconHeight() {
            return UiConfig.CONTACT_LIST_AVATAR_SIZE;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Ellipse2D clip = new Ellipse2D.Double(x, y, getIconWidth(), getIconHeight());
                g.setClip(clip);
                g.drawImage(image, x, y, getIconWidth(), getIconHeight(), component);
                g.setClip(null);
                g.setColor(ModernThemePalette.border());
                g.draw(clip);
            } finally {
                g.dispose();
            }
        }
    }

    private static final class InitialsIcon implements Icon {
        private final String initials;
        private final Color background;

        private InitialsIcon(String name) {
            initials = initials(name);
            background = ModernThemePalette.avatarColor(name);
        }

        @Override
        public int getIconWidth() {
            return UiConfig.CONTACT_LIST_AVATAR_SIZE;
        }

        @Override
        public int getIconHeight() {
            return UiConfig.CONTACT_LIST_AVATAR_SIZE;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getIconWidth(), getIconHeight());
                g.setColor(background);
                g.fillOval(x, y, size, size);
                g.setColor(Color.WHITE);
                g.setFont(component.getFont().deriveFont(Font.BOLD, 13.5f));
                int textWidth = g.getFontMetrics().stringWidth(initials);
                int baseline = y + (size + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2;
                g.drawString(initials, x + (size - textWidth) / 2, baseline);
            } finally {
                g.dispose();
            }
        }

        private static String initials(String name) {
            if (name == null || name.isBlank()) {
                return "?";
            }
            String[] parts = name.trim().split("\\s+");
            String first = parts[0].substring(0, 1).toUpperCase();
            String second = parts.length > 1
                    ? parts[parts.length - 1].substring(0, 1).toUpperCase()
                    : "";
            return first + second;
        }
    }
}
