// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ResponsiveCardGridPanel.java
// # 📌 Amac: Dashboard kartlarini kullanilabilir genislige gore cok kolonlu responsive grid olarak duzenler.
// # 📌 View - Java
// # Version: 1.1.0
// # Aciklama: Genis, orta ve dar breakpointlerde kolon sayisini degistirir; tek kolon modunda kartlarin kendi yuksekligini korur.
// # Bagimli Oldugu Katman: View | Config
package com.turkuazlabs.telefonrehberi.views;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public final class ResponsiveCardGridPanel extends JPanel {
    private final List<JComponent> cards = new ArrayList<>();
    private final int wideColumns;
    private final int mediumColumns;
    private final int compactColumns;
    private final int mediumBreakpoint;
    private final int compactBreakpoint;
    private final int horizontalGap;
    private final int verticalGap;
    private int activeColumns = -1;

    public ResponsiveCardGridPanel(
            int wideColumns,
            int mediumColumns,
            int compactColumns,
            int mediumBreakpoint,
            int compactBreakpoint,
            int horizontalGap,
            int verticalGap
    ) {
        if (wideColumns < 1 || mediumColumns < 1 || compactColumns < 1
                || mediumBreakpoint < 1 || compactBreakpoint < 1) {
            throw new IllegalArgumentException();
        }
        this.wideColumns = wideColumns;
        this.mediumColumns = mediumColumns;
        this.compactColumns = compactColumns;
        this.mediumBreakpoint = mediumBreakpoint;
        this.compactBreakpoint = compactBreakpoint;
        this.horizontalGap = Math.max(0, horizontalGap);
        this.verticalGap = Math.max(0, verticalGap);
        setOpaque(false);
        setLayout(new GridBagLayout());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                applyColumns(resolveColumns(getWidth()));
            }

            @Override
            public void componentShown(ComponentEvent event) {
                applyColumns(resolveColumns(getWidth()));
            }
        });
    }

    public void addCard(JComponent card) {
        if (card == null) {
            return;
        }
        cards.add(card);
        rebuild(resolveColumns(getWidth()));
    }

    private int resolveColumns(int width) {
        if (width > 0 && width < compactBreakpoint) {
            return compactColumns;
        }
        if (width > 0 && width < mediumBreakpoint) {
            return mediumColumns;
        }
        return wideColumns;
    }

    private void applyColumns(int columns) {
        if (activeColumns == columns) {
            return;
        }
        rebuild(columns);
    }

    private void rebuild(int columns) {
        activeColumns = columns;
        removeAll();

        int row = 0;
        int column = 0;
        for (JComponent card : cards) {
            add(card, cardConstraints(column, row));
            column++;
            if (column >= columns) {
                row++;
                column = 0;
            }
        }

        if (column > 0) {
            while (column < columns) {
                add(Box.createHorizontalGlue(), fillerConstraints(column, row));
                column++;
            }
        }

        revalidate();
        repaint();
    }

    private GridBagConstraints cardConstraints(int column, int row) {
        GridBagConstraints value = baseConstraints(column, row);
        value.fill = GridBagConstraints.BOTH;
        return value;
    }

    private GridBagConstraints fillerConstraints(int column, int row) {
        GridBagConstraints value = baseConstraints(column, row);
        value.fill = GridBagConstraints.HORIZONTAL;
        return value;
    }

    private GridBagConstraints baseConstraints(int column, int row) {
        GridBagConstraints value = new GridBagConstraints();
        value.gridx = column;
        value.gridy = row;
        value.weightx = 1.0;
        value.weighty = 0.0;
        value.anchor = GridBagConstraints.NORTHWEST;
        value.insets = new Insets(
                row == 0 ? 0 : verticalGap,
                column == 0 ? 0 : horizontalGap / 2,
                0,
                column == activeColumns - 1 ? 0 : horizontalGap / 2
        );
        return value;
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension preferred = getPreferredSize();
        return new Dimension(Integer.MAX_VALUE, preferred.height);
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension preferred = getPreferredSize();
        return new Dimension(0, preferred.height);
    }
}
