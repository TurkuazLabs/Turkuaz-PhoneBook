// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/VerticalScrollablePanel.java
// # 📌 Amac: Dikey kaydirilan View iceriginin viewport genisligini tam takip etmesini saglar.
// # 📌 View - Java
// # Version: 2.14.0
// # Aciklama: Profil bilgi kartlarinda yatay tasma ve dar viewport icinde gereksiz yatay bosluk olusmasini engeller.
// # Bagimli Oldugu Katman: View | Config
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.UiConfig;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Rectangle;

public final class VerticalScrollablePanel extends JPanel implements Scrollable {
    public VerticalScrollablePanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    protected void addImpl(java.awt.Component component, Object constraints, int index) {
        if (component instanceof JComponent swingComponent) {
            swingComponent.setAlignmentX(LEFT_ALIGNMENT);
        }
        super.addImpl(component, constraints, index);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return UiConfig.SCROLL_UNIT_INCREMENT;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.VERTICAL) {
            return Math.max(UiConfig.SCROLL_BLOCK_INCREMENT, visibleRect.height - UiConfig.SCROLL_UNIT_INCREMENT);
        }
        return Math.max(UiConfig.SCROLL_BLOCK_INCREMENT, visibleRect.width - UiConfig.SCROLL_UNIT_INCREMENT);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
