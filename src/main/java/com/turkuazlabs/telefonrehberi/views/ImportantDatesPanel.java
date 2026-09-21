// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ImportantDatesPanel.java
// # 📌 Amac: Kisi editorunde sinirsiz yildonumu ve ozel tarih kaydini yonetir.
// # 📌 View - Java
// # Version: 1.2.0
// # Aciklama: Onemli tarih ekleme, duzenleme, silme ve hatirlatma secimi icin modern Swing paneli sunar.
// # Bagimli Oldugu Katman: View | Model | Language | Config
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.language.ReminderText;
import com.turkuazlabs.telefonrehberi.models.ImportantDate;
import com.turkuazlabs.telefonrehberi.models.ImportantDateType;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public final class ImportantDatesPanel extends JPanel {
    private final DefaultListModel<ImportantDate> model = new DefaultListModel<>();
    private final JList<ImportantDate> list = new JList<>(model);
    private final JButton addButton;
    private final JButton moreButton;

    public ImportantDatesPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 8));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(38);
        list.setCellRenderer((source, value, index, selected, focus) -> {
            String type = ReminderText.importantDateType(value.type());
            String reminder = ReminderText.leadTime(value.reminderLeadTime());
            JLabel label = new JLabel(value.label() + "   " + value.dateValue() + "   -   " + type + " / " + reminder);
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(7, 8, 7, 8));
            label.setBackground(selected ? ModernThemePalette.accentSoft() : ModernThemePalette.surface());
            label.setForeground(ModernThemePalette.textPrimary());
            return label;
        });

        JScrollPane scroll = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createLineBorder(ModernThemePalette.border()));
        scroll.setPreferredSize(new Dimension(0, 122));
        add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout(6, 0));
        actions.setOpaque(false);
        addButton = ModernButtons.secondary(Messages.IMPORTANT_DATE_ADD_BUTTON);
        moreButton = ModernButtons.secondary(Messages.METHOD_MORE_BUTTON);
        moreButton.setToolTipText(Messages.IMPORTANT_DATE_ACTIONS_TOOLTIP);
        addButton.addActionListener(event -> addDate());
        moreButton.addActionListener(event -> showActions(moreButton));
        actions.add(addButton, BorderLayout.WEST);
        actions.add(moreButton, BorderLayout.EAST);
        add(actions, BorderLayout.NORTH);
    }

    public void refreshLanguage() {
        addButton.setText(Messages.IMPORTANT_DATE_ADD_BUTTON);
        moreButton.setText(Messages.METHOD_MORE_BUTTON);
        moreButton.setToolTipText(Messages.IMPORTANT_DATE_ACTIONS_TOOLTIP);
        list.repaint();
    }

    public List<ImportantDate> getDates() {
        List<ImportantDate> out = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            ImportantDate value = model.get(i);
            out.add(new ImportantDate(value.type(), value.label(), value.dateValue(), value.reminderLeadTime(), i));
        }
        return List.copyOf(out);
    }

    public void setDates(List<ImportantDate> values) {
        model.clear();
        if (values == null) return;
        for (ImportantDate value : values) {
            if (value != null && !value.dateValue().isBlank()) model.addElement(value);
        }
    }

    public void clearDates() {
        model.clear();
    }

    private void showActions(JButton anchor) {
        if (list.getSelectedIndex() < 0) return;
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem(Messages.IMPORTANT_DATE_EDIT_BUTTON);
        JMenuItem remove = new JMenuItem(Messages.IMPORTANT_DATE_DELETE_BUTTON);
        edit.addActionListener(event -> editDate());
        remove.addActionListener(event -> removeDate());
        menu.add(edit);
        menu.addSeparator();
        menu.add(remove);
        menu.show(anchor, 0, anchor.getHeight());
    }

    private void addDate() {
        ImportantDate value = prompt(null);
        if (value != null) model.addElement(new ImportantDate(value.type(), value.label(), value.dateValue(), value.reminderLeadTime(), model.size()));
    }

    private void editDate() {
        int index = list.getSelectedIndex();
        if (index < 0) return;
        ImportantDate value = prompt(model.get(index));
        if (value != null) model.set(index, new ImportantDate(value.type(), value.label(), value.dateValue(), value.reminderLeadTime(), index));
    }

    private void removeDate() {
        int index = list.getSelectedIndex();
        if (index >= 0) model.remove(index);
    }

    private ImportantDate prompt(ImportantDate current) {
        JComboBox<ImportantDateType> typeCombo = new JComboBox<>(ImportantDateType.values());
        JComboBox<ReminderLeadTime> reminderCombo = new JComboBox<>(ReminderLeadTime.values());
        configureRenderer(typeCombo, true);
        configureRenderer(reminderCombo, false);
        JTextField labelField = new JTextField(current == null ? "" : current.label());
        JTextField dateField = new JTextField(current == null ? "" : current.dateValue());
        if (current != null) {
            typeCombo.setSelectedItem(current.type());
            reminderCombo.setSelectedItem(current.reminderLeadTime());
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 8);
        panel.add(new JLabel(Messages.IMPORTANT_DATE_TYPE_LABEL), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(typeCombo, c);
        addRow(panel, 1, Messages.IMPORTANT_DATE_NAME_LABEL, labelField);
        addRow(panel, 2, Messages.IMPORTANT_DATE_VALUE_LABEL, dateField);
        addRow(panel, 3, Messages.IMPORTANT_DATE_REMINDER_LABEL, reminderCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, Messages.IMPORTANT_DATE_DIALOG_TITLE,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        String date = dateField.getText().trim();
        if (date.isBlank()) return null;
        ImportantDateType type = (ImportantDateType) typeCombo.getSelectedItem();
        String label = labelField.getText().trim();
        if (label.isBlank()) {
            label = type == ImportantDateType.ANNIVERSARY
                    ? Messages.IMPORTANT_DATE_DEFAULT_ANNIVERSARY
                    : Messages.IMPORTANT_DATE_DEFAULT_CUSTOM;
        }
        ReminderLeadTime reminder = (ReminderLeadTime) reminderCombo.getSelectedItem();
        return new ImportantDate(type, label, date, reminder, current == null ? model.size() : current.position());
    }

    private void addRow(JPanel panel, int row, String label, Component component) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 8);
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, c);
    }

    private <T> void configureRenderer(JComboBox<T> combo, boolean dateType) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof ImportantDateType type) setText(ReminderText.importantDateType(type));
                else if (value instanceof ReminderLeadTime lead) setText(ReminderText.leadTime(lead));
                return this;
            }
        });
    }
}
