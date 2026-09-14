// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/CollapsibleNavigationSection.java
// # 📌 Amac: Sol navigasyondaki ikincil menuleri kompakt acilir/kapanir bolumler halinde sunar.
// # 📌 View - Java
// # Version: 1.0.0
// # Aciklama: Varsayilan kapali bolumler, aktif alt sayfa vurgusu ve tek tikla acma/kapatma davranisi saglar.
// # Bagimli Oldugu Katman: View | Config
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.UiConfig;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class CollapsibleNavigationSection extends JPanel {
    private final SectionHeaderButton headerButton;
    private final JPanel contentPanel = new JPanel();
    private boolean expanded;
    private boolean activeChild;
    private Runnable toggleAction = () -> setExpanded(!expanded);

    public CollapsibleNavigationSection(String title, boolean expanded) {
        this.expanded = expanded;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        headerButton = new SectionHeaderButton(title);
        headerButton.addActionListener(event -> toggleAction.run());
        add(headerButton);

        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.setVisible(expanded);
        add(contentPanel);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    public void setToggleAction(Runnable action) {
        toggleAction = action == null ? () -> setExpanded(!expanded) : action;
    }

    public void addNavigationButton(JButton button) {
        if (contentPanel.getComponentCount() > 0) {
            contentPanel.add(Box.createVerticalStrut(UiConfig.NAV_CHILD_GAP));
        }
        contentPanel.add(button);
    }

    public boolean containsNavigationButton(JButton button) {
        if (button == null) {
            return false;
        }
        for (Component component : contentPanel.getComponents()) {
            if (component == button) {
                return true;
            }
        }
        return false;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean value) {
        if (expanded == value) {
            return;
        }
        expanded = value;
        contentPanel.setVisible(value);
        headerButton.repaint();
        revalidate();
        repaint();
    }

    public void setActiveChild(boolean value) {
        activeChild = value;
        headerButton.setForeground(value
                ? ModernThemePalette.sidebarActiveText()
                : ModernThemePalette.sidebarMutedText());
        headerButton.repaint();
    }

    private final class SectionHeaderButton extends JButton {
        private SectionHeaderButton(String title) {
            super(title);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(getFont().deriveFont(Font.BOLD, UiConfig.NAV_SECTION_FONT_SIZE));
            setForeground(ModernThemePalette.sidebarMutedText());
            setBorder(new EmptyBorder(7, 10, 7, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, UiConfig.NAV_SECTION_HEIGHT));
            setPreferredSize(new Dimension(UiConfig.SIDEBAR_WIDTH - 36, UiConfig.NAV_SECTION_HEIGHT));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setRolloverEnabled(true);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (activeChild || getModel().isRollover()) {
                    g.setColor(activeChild ? ModernThemePalette.sidebarActive() : ModernThemePalette.sidebarHover());
                    g.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                }
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
            paintArrow(graphics);
        }

        private void paintArrow(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(getForeground());
                g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int centerX = getWidth() - 17;
                int centerY = getHeight() / 2;
                if (expanded) {
                    g.drawLine(centerX - 4, centerY - 2, centerX, centerY + 2);
                    g.drawLine(centerX, centerY + 2, centerX + 4, centerY - 2);
                } else {
                    g.drawLine(centerX - 2, centerY - 4, centerX + 2, centerY);
                    g.drawLine(centerX + 2, centerY, centerX - 2, centerY + 4);
                }
            } finally {
                g.dispose();
            }
        }
    }
}
