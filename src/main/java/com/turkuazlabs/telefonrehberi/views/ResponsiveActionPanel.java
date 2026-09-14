// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ResponsiveActionPanel.java
// # 📌 Amac: Aksiyon butonlarini mevcut genislige gore tek veya cok satirli grid olarak yerlestirir.
// # 📌 View - Java
// # Version: 2.21.0
// # Aciklama: Yalnizca gorunen butonlari hesaba katar; BoxLayout icinde gereksiz dikey buyumeden breakpoint tabanli aksiyon gridini korur.
// # Bagimli Oldugu Katman: View
package com.turkuazlabs.telefonrehberi.views;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public final class ResponsiveActionPanel extends JPanel {
    private final int wideColumns;
    private final int compactColumns;
    private final int compactBreakpoint;
    private final int horizontalGap;
    private final int verticalGap;
    private int activeColumns;

    public ResponsiveActionPanel(
            int wideColumns,
            int compactColumns,
            int compactBreakpoint,
            int horizontalGap,
            int verticalGap
    ) {
        if (wideColumns < 1 || compactColumns < 1 || compactBreakpoint < 1) {
            throw new IllegalArgumentException();
        }
        this.wideColumns = wideColumns;
        this.compactColumns = compactColumns;
        this.compactBreakpoint = compactBreakpoint;
        this.horizontalGap = Math.max(0, horizontalGap);
        this.verticalGap = Math.max(0, verticalGap);
        setOpaque(false);
        applyColumns(wideColumns);
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

    @Override
    public Dimension getMaximumSize() {
        Dimension preferred = getPreferredSize();
        return new Dimension(Integer.MAX_VALUE, preferred.height);
    }

    private int resolveColumns(int width) {
        return width > 0 && width < compactBreakpoint ? compactColumns : wideColumns;
    }

    private void applyColumns(int columns) {
        if (activeColumns == columns) {
            return;
        }
        activeColumns = columns;
        setLayout(new VisibleGridLayout(activeColumns, horizontalGap, verticalGap));
        revalidate();
    }

    private static final class VisibleGridLayout implements LayoutManager {
        private final int maxColumns;
        private final int horizontalGap;
        private final int verticalGap;

        private VisibleGridLayout(int maxColumns, int horizontalGap, int verticalGap) {
            this.maxColumns = maxColumns;
            this.horizontalGap = horizontalGap;
            this.verticalGap = verticalGap;
        }

        @Override
        public void addLayoutComponent(String name, Component component) {
        }

        @Override
        public void removeLayoutComponent(Component component) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent, false);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent, true);
        }

        @Override
        public void layoutContainer(Container parent) {
            List<Component> visible = visibleComponents(parent);
            if (visible.isEmpty()) {
                return;
            }
            Insets insets = parent.getInsets();
            int columns = Math.min(maxColumns, visible.size());
            int rows = rowCount(visible.size(), columns);
            int availableWidth = Math.max(0, parent.getWidth() - insets.left - insets.right);
            int availableHeight = Math.max(0, parent.getHeight() - insets.top - insets.bottom);
            int rowHeight = Math.max(0, (availableHeight - Math.max(0, rows - 1) * verticalGap) / rows);
            int componentIndex = 0;
            int y = insets.top;

            for (int row = 0; row < rows; row++) {
                int remaining = visible.size() - componentIndex;
                int rowColumns = Math.min(columns, remaining);
                int cellWidth = Math.max(0, (availableWidth - Math.max(0, rowColumns - 1) * horizontalGap) / rowColumns);
                int x = insets.left;
                for (int column = 0; column < rowColumns; column++) {
                    Component component = visible.get(componentIndex++);
                    component.setBounds(x, y, cellWidth, rowHeight);
                    x += cellWidth + horizontalGap;
                }
                y += rowHeight + verticalGap;
            }
        }

        private Dimension layoutSize(Container parent, boolean minimum) {
            List<Component> visible = visibleComponents(parent);
            Insets insets = parent.getInsets();
            if (visible.isEmpty()) {
                return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
            }
            int columns = Math.min(maxColumns, visible.size());
            int rows = rowCount(visible.size(), columns);
            int maxWidth = 0;
            int maxHeight = 0;
            for (Component component : visible) {
                Dimension size = minimum ? component.getMinimumSize() : component.getPreferredSize();
                maxWidth = Math.max(maxWidth, size.width);
                maxHeight = Math.max(maxHeight, size.height);
            }
            int width = insets.left + insets.right + (maxWidth * columns) + Math.max(0, columns - 1) * horizontalGap;
            int height = insets.top + insets.bottom + (maxHeight * rows) + Math.max(0, rows - 1) * verticalGap;
            return new Dimension(width, height);
        }

        private int rowCount(int size, int columns) {
            return (size + columns - 1) / columns;
        }

        private List<Component> visibleComponents(Container parent) {
            List<Component> result = new ArrayList<>();
            for (Component component : parent.getComponents()) {
                if (component.isVisible()) {
                    result.add(component);
                }
            }
            return result;
        }
    }
}
