// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ResponsiveInfoPanel.java
// # 📌 Amac: Salt-okunur profil alanlarini genis ekranda iki, dar ekranda tek kolon halinde duzenler.
// # 📌 View - Java
// # Version: 1.0.0
// # Aciklama: Profil gorunumundeki etiket/deger ciftlerini editor ile ayni responsive tasarim dilinde sunar.
// # Bagimli Oldugu Katman: View | Config
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.UiConfig;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public final class ResponsiveInfoPanel extends JPanel {
    private final List<Entry> entries = new ArrayList<>();
    private int lastColumnCount = -1;

    public ResponsiveInfoPanel() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                refreshLayoutIfNeeded();
            }
        });
    }

    public void addField(String label, JComponent component) {
        addEntry(infoCell(label, component), false);
    }

    public void addFullField(String label, JComponent component) {
        addEntry(infoCell(label, component), true);
    }

    private void addEntry(JComponent component, boolean fullWidth) {
        entries.add(new Entry(component, fullWidth));
        rebuildLayout(columnCount());
    }

    private JPanel infoCell(String label, JComponent component) {
        JPanel cell = new JPanel(new BorderLayout(0, UiConfig.PROFILE_FIELD_LABEL_GAP));
        cell.setOpaque(false);

        JLabel labelView = new JLabel(label);
        labelView.setForeground(ModernThemePalette.textSecondary());
        labelView.setFont(labelView.getFont().deriveFont(Font.BOLD, UiConfig.PROFILE_FIELD_LABEL_SIZE));
        cell.add(labelView, BorderLayout.NORTH);

        component.setForeground(ModernThemePalette.textPrimary());
        if (component instanceof JLabel valueLabel) {
            valueLabel.setFont(valueLabel.getFont().deriveFont(Font.PLAIN, UiConfig.PROFILE_FIELD_VALUE_SIZE));
        }
        cell.add(component, BorderLayout.CENTER);
        return cell;
    }

    private void refreshLayoutIfNeeded() {
        int columns = columnCount();
        if (columns != lastColumnCount) rebuildLayout(columns);
    }

    private int columnCount() {
        return getWidth() > 0 && getWidth() < UiConfig.PROFILE_INFO_COMPACT_BREAKPOINT
                ? UiConfig.PROFILE_INFO_COMPACT_COLUMNS
                : UiConfig.PROFILE_INFO_WIDE_COLUMNS;
    }

    private void rebuildLayout(int columns) {
        removeAll();
        lastColumnCount = columns;
        int row = 0;
        int column = 0;
        for (Entry entry : entries) {
            if (entry.fullWidth()) {
                if (column != 0) { row++; column = 0; }
                add(entry.component(), constraints(0, row, columns));
                row++;
                continue;
            }
            add(entry.component(), constraints(column, row, 1));
            column++;
            if (column >= columns) { row++; column = 0; }
        }
        if (column != 0) row++;
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = row;
        filler.gridwidth = columns;
        filler.weightx = 1.0;
        filler.weighty = 1.0;
        filler.fill = GridBagConstraints.BOTH;
        add(Box.createGlue(), filler);
        revalidate();
        repaint();
    }

    private GridBagConstraints constraints(int column, int row, int span) {
        GridBagConstraints value = new GridBagConstraints();
        value.gridx = column;
        value.gridy = row;
        value.gridwidth = span;
        value.weightx = 1.0;
        value.fill = GridBagConstraints.HORIZONTAL;
        value.anchor = GridBagConstraints.NORTHWEST;
        value.insets = new Insets(
                UiConfig.PROFILE_INFO_GAP / 2,
                UiConfig.PROFILE_INFO_GAP / 2,
                UiConfig.PROFILE_INFO_GAP / 2,
                UiConfig.PROFILE_INFO_GAP / 2
        );
        return value;
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension preferred = getPreferredSize();
        return new Dimension(0, Math.min(preferred.height, UiConfig.PROFILE_INFO_MIN_HEIGHT));
    }

    private record Entry(JComponent component, boolean fullWidth) {
    }
}
