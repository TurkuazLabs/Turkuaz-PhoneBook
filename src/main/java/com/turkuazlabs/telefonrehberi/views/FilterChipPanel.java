// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/FilterChipPanel.java
// # 📌 Amac: Aktif kisi filtrelerini mevcut genislige gore responsive chip gridinde gosterir.
// # 📌 View - Java
// # Version: 1.1.0
// # Aciklama: Chipleri genis alanda iki, cok dar alanda tek kolonlu yerlestirir ve BoxLayout icin dogru dinamik yukseklik bildirir.
// # Bagimli Oldugu Katman: View | Config
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.UiConfig;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public final class FilterChipPanel extends JPanel {
    private int activeColumns = -1;

    public FilterChipPanel() {
        setOpaque(false);
        applyColumns(UiConfig.FILTER_CHIP_WIDE_COLUMNS);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                applyColumns(resolveColumns(getWidth()));
            }
        });
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension preferred = getPreferredSize();
        return new Dimension(Integer.MAX_VALUE, preferred.height);
    }

    private int resolveColumns(int width) {
        return width > 0 && width < UiConfig.FILTER_CHIP_COMPACT_BREAKPOINT
                ? UiConfig.FILTER_CHIP_COMPACT_COLUMNS
                : UiConfig.FILTER_CHIP_WIDE_COLUMNS;
    }

    private void applyColumns(int columns) {
        if (activeColumns == columns) return;
        activeColumns = columns;
        setLayout(new GridLayout(0, activeColumns, UiConfig.FILTER_CHIP_GAP, UiConfig.FILTER_CHIP_GAP));
        revalidate();
    }
}
