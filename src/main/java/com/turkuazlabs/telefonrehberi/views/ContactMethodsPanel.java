// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ContactMethodsPanel.java
// # 📌 Amac: Sinirsiz telefon veya e-posta satirlarini modern ve yerellestirilmis liste olarak duzenler.
// # 📌 View - Java
// # Version: 2.15.1
// # Aciklama: Contact method etiketlerini ve telefon ulke kodu adlarini Language katmaninda yerellestirir; eski canonical storage degerlerini korur.
// # Bagimli Oldugu Katman: View | Model | Config | Tool | Language
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.config.PhoneCountryCodeCatalog;
import com.turkuazlabs.telefonrehberi.language.ContactMethodText;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.language.PhoneCountryCodeText;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.models.PhoneCountryCode;
import com.turkuazlabs.telefonrehberi.models.PhoneValidationResult;
import com.turkuazlabs.telefonrehberi.tools.PhoneNumberTool;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public final class ContactMethodsPanel extends JPanel {
    private final String kind;
    private final String defaultLabel;
    private final DefaultListModel<ContactMethod> model = new DefaultListModel<>();
    private final JList<ContactMethod> list = new JList<>(model);
    private final PhoneNumberTool phoneNumberTool = new PhoneNumberTool();

    public ContactMethodsPanel(String kind, String defaultLabel) {
        this.kind = kind;
        this.defaultLabel = defaultLabel;
        setOpaque(false);
        setLayout(new BorderLayout(0, 8));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(34);
        list.setCellRenderer((source, method, index, selected, focus) -> {
            String displayLabel = ContactMethodText.displayLabel(method.label());
            JLabel label = new JLabel((method.primary() ? "*  " : "   ") + displayLabel + "   " + method.value());
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(6, 8, 6, 8));
            label.setBackground(selected ? ModernThemePalette.accentSoft() : ModernThemePalette.surface());
            label.setForeground(ModernThemePalette.textPrimary());
            return label;
        });
        JScrollPane scroll = new JScrollPane(
                list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createLineBorder(ModernThemePalette.border()));
        scroll.setPreferredSize(new Dimension(0, 120));
        add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout(6, 0));
        actions.setOpaque(false);
        JButton add = ModernButtons.secondary("+ " + Messages.METHOD_ADD_BUTTON);
        JButton more = ModernButtons.secondary(Messages.METHOD_MORE_BUTTON);
        more.setToolTipText(Messages.METHOD_ACTIONS_TOOLTIP);
        add.addActionListener(event -> addMethod());
        more.addActionListener(event -> showActions(more));
        actions.add(add, BorderLayout.WEST);
        actions.add(more, BorderLayout.EAST);
        add(actions, BorderLayout.NORTH);
    }

    private void showActions(JButton anchor) {
        int index = list.getSelectedIndex();
        if (index < 0) {
            return;
        }
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem(Messages.METHOD_EDIT_BUTTON);
        JMenuItem primary = new JMenuItem(Messages.METHOD_PRIMARY_BUTTON);
        JMenuItem remove = new JMenuItem(Messages.METHOD_DELETE_BUTTON);
        edit.addActionListener(event -> editMethod());
        primary.addActionListener(event -> makePrimary());
        remove.addActionListener(event -> removeMethod());
        menu.add(edit);
        menu.add(primary);
        menu.addSeparator();
        menu.add(remove);
        menu.show(anchor, 0, anchor.getHeight());
    }

    public List<ContactMethod> getMethods() {
        List<ContactMethod> out = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            ContactMethod value = model.get(i);
            out.add(new ContactMethod(kind, value.label(), value.value(), value.primary(), i));
        }
        return List.copyOf(out);
    }

    public void setMethods(List<ContactMethod> values) {
        model.clear();
        if (values == null) return;
        for (ContactMethod value : values) {
            if (value != null && kind.equals(value.kind()) && !value.value().isBlank()) model.addElement(value);
        }
    }

    public void clearMethods() {
        model.clear();
    }

    private void addMethod() {
        ContactMethod value = prompt(null);
        if (value == null) return;
        if (value.primary()) clearPrimary();
        model.addElement(new ContactMethod(kind, value.label(), value.value(), value.primary() || model.isEmpty(), model.size()));
    }

    private void editMethod() {
        int index = list.getSelectedIndex();
        if (index < 0) return;
        ContactMethod edited = prompt(model.get(index));
        if (edited == null) return;
        if (edited.primary()) clearPrimary();
        model.set(index, new ContactMethod(kind, edited.label(), edited.value(), edited.primary(), index));
        ensurePrimary();
    }

    private void removeMethod() {
        int index = list.getSelectedIndex();
        if (index < 0) return;
        model.remove(index);
        ensurePrimary();
    }

    private void makePrimary() {
        int index = list.getSelectedIndex();
        if (index < 0) return;
        clearPrimary();
        ContactMethod current = model.get(index);
        model.set(index, new ContactMethod(kind, current.label(), current.value(), true, index));
    }

    private void clearPrimary() {
        for (int i = 0; i < model.size(); i++) {
            ContactMethod current = model.get(i);
            if (current.primary()) model.set(i, new ContactMethod(kind, current.label(), current.value(), false, i));
        }
    }

    private void ensurePrimary() {
        if (model.isEmpty()) return;
        for (int i = 0; i < model.size(); i++) if (model.get(i).primary()) return;
        ContactMethod first = model.get(0);
        model.set(0, new ContactMethod(kind, first.label(), first.value(), true, 0));
    }

    private ContactMethod prompt(ContactMethod current) {
        if (ContactMethod.PHONE.equals(kind)) return promptPhone(current);
        return promptEmail(current);
    }

    private ContactMethod promptEmail(ContactMethod current) {
        String originalStoredLabel = current == null ? defaultLabel : current.label();
        JTextField labelField = new JTextField(ContactMethodText.displayLabel(originalStoredLabel));
        JTextField valueField = new JTextField(current == null ? "" : current.value());
        JCheckBox primary = new JCheckBox(Messages.METHOD_PRIMARY_LABEL, current == null || current.primary());
        JPanel fields = formPanel();
        addField(fields, 0, Messages.METHOD_LABEL_LABEL, labelField);
        addField(fields, 1, Messages.METHOD_EMAIL_DIALOG, valueField);
        addWide(fields, 2, primary);
        int result = JOptionPane.showConfirmDialog(this, fields, Messages.METHOD_EMAIL_DIALOG, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        String value = text(valueField);
        if (value.isBlank()) return null;
        String label = ContactMethodText.storageLabel(originalStoredLabel, text(labelField), defaultLabel);
        return new ContactMethod(kind, label, value, primary.isSelected(), current == null ? model.size() : current.position());
    }

    private ContactMethod promptPhone(ContactMethod current) {
        String originalStoredLabel = current == null ? defaultLabel : current.label();
        JTextField labelField = new JTextField(ContactMethodText.displayLabel(originalStoredLabel));
        PhoneCountryCode detected = current == null
                ? PhoneCountryCodeCatalog.byIso(AppConfig.DEFAULT_PHONE_COUNTRY_ISO)
                : phoneNumberTool.detectCountry(current.value(), AppConfig.DEFAULT_PHONE_COUNTRY_ISO);
        JComboBox<PhoneCountryCode> countryCombo = new JComboBox<>(PhoneCountryCodeCatalog.selectableValues().toArray(PhoneCountryCode[]::new));
        countryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> source, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(source, value, index, selected, focus);
                if (value instanceof PhoneCountryCode country) {
                    setText(PhoneCountryCodeText.display(country));
                }
                return this;
            }
        });
        countryCombo.setSelectedItem(detected);
        if (countryCombo.getSelectedIndex() < 0) countryCombo.setSelectedItem(PhoneCountryCodeCatalog.byIso(AppConfig.DEFAULT_PHONE_COUNTRY_ISO));

        JTextField customCodeField = new JTextField(current == null ? "" : phoneNumberTool.customDialCode(current.value(), detected));
        JTextField numberField = new JTextField(current == null ? "" : phoneNumberTool.nationalPart(current.value(), detected, AppConfig.DEFAULT_PHONE_COUNTRY_ISO));
        JCheckBox primary = new JCheckBox(Messages.METHOD_PRIMARY_LABEL, current == null || current.primary());
        JLabel hint = new JLabel(Messages.METHOD_PHONE_COUNTRY_HINT);
        JLabel example = new JLabel();
        JLabel preview = new JLabel();
        JLabel validation = new JLabel();

        JPanel fields = formPanel();
        addField(fields, 0, Messages.METHOD_LABEL_LABEL, labelField);
        addField(fields, 1, Messages.METHOD_COUNTRY_CODE_LABEL, countryCombo);
        addField(fields, 2, Messages.METHOD_CUSTOM_COUNTRY_CODE_LABEL, customCodeField);
        addField(fields, 3, Messages.METHOD_NATIONAL_NUMBER_LABEL, numberField);
        addField(fields, 4, Messages.METHOD_PHONE_EXAMPLE_LABEL, example);
        addField(fields, 5, Messages.METHOD_PHONE_PREVIEW_LABEL, preview);
        addField(fields, 6, Messages.METHOD_PHONE_VALIDATION_LABEL, validation);
        addWide(fields, 7, hint);
        addWide(fields, 8, primary);

        boolean[] formatting = {false};
        Runnable update = () -> {
            PhoneCountryCode selected = (PhoneCountryCode) countryCombo.getSelectedItem();
            boolean custom = selected != null && selected.custom();
            customCodeField.setEnabled(custom);
            example.setText(phoneNumberTool.exampleNational(selected));
            String composed = phoneNumberTool.compose(selected, text(customCodeField), text(numberField));
            preview.setText(composed.isBlank() ? Messages.EMPTY_VALUE : composed);
            PhoneValidationResult check = phoneNumberTool.validate(selected, text(customCodeField), text(numberField));
            validation.setText(validationMessage(check));
            validation.setForeground(check.valid() ? ModernThemePalette.success() : ModernThemePalette.warning());
        };

        Runnable formatAndUpdate = () -> {
            if (formatting[0]) return;
            SwingUtilities.invokeLater(() -> {
                if (formatting[0]) return;
                formatting[0] = true;
                try {
                    PhoneCountryCode selected = (PhoneCountryCode) countryCombo.getSelectedItem();
                    String formatted = phoneNumberTool.formatNational(selected, text(numberField));
                    if (!formatted.equals(numberField.getText())) {
                        numberField.setText(formatted);
                        numberField.setCaretPosition(numberField.getText().length());
                    }
                    update.run();
                } finally {
                    formatting[0] = false;
                }
            });
        };

        countryCombo.addActionListener(event -> formatAndUpdate.run());
        documentListener(customCodeField, update);
        documentListener(numberField, formatAndUpdate);
        formatAndUpdate.run();

        int result = JOptionPane.showConfirmDialog(this, fields, Messages.METHOD_PHONE_DIALOG, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        PhoneCountryCode selected = (PhoneCountryCode) countryCombo.getSelectedItem();
        if (selected != null && selected.custom() && phoneNumberTool.normalizeDialCode(text(customCodeField)).isBlank()) {
            JOptionPane.showMessageDialog(this, Messages.ERROR_COUNTRY_CODE_REQUIRED, Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return null;
        }
        PhoneValidationResult check = phoneNumberTool.validate(selected, text(customCodeField), text(numberField));
        if (!check.valid()) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    Messages.ERROR_PHONE_FORMAT_CONFIRM + "\n" + validationMessage(check),
                    Messages.WARNING_TITLE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) return null;
        }
        String value = phoneNumberTool.compose(selected, text(customCodeField), text(numberField));
        if (value.isBlank()) return null;
        String label = ContactMethodText.storageLabel(originalStoredLabel, text(labelField), defaultLabel);
        return new ContactMethod(kind, label, value, primary.isSelected(), current == null ? model.size() : current.position());
    }

    private String validationMessage(PhoneValidationResult result) {
        return switch (result.status()) {
            case EMPTY -> Messages.PHONE_VALIDATION_EMPTY;
            case VALID -> Messages.PHONE_VALIDATION_VALID;
            case TOO_SHORT -> String.format(
                    Messages.PHONE_VALIDATION_TOO_SHORT_FORMAT,
                    result.nationalDigitCount(),
                    result.minNationalDigits()
            );
            case TOO_LONG -> String.format(
                    Messages.PHONE_VALIDATION_TOO_LONG_FORMAT,
                    result.nationalDigitCount(),
                    result.maxNationalDigits()
            );
        };
    }

    private JPanel formPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        return panel;
    }

    private void addField(JPanel panel, int row, String labelText, java.awt.Component component) {
        GridBagConstraints label = constraints(0, row, 0.0);
        label.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), label);
        GridBagConstraints field = constraints(1, row, 1.0);
        field.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, field);
    }

    private void addWide(JPanel panel, int row, java.awt.Component component) {
        GridBagConstraints field = constraints(0, row, 1.0);
        field.gridwidth = 2;
        field.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, field);
    }

    private GridBagConstraints constraints(int column, int row, double weightX) {
        GridBagConstraints value = new GridBagConstraints();
        value.gridx = column;
        value.gridy = row;
        value.weightx = weightX;
        value.insets = new Insets(4, 4, 4, 4);
        return value;
    }

    private void documentListener(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { action.run(); }
            @Override public void removeUpdate(DocumentEvent event) { action.run(); }
            @Override public void changedUpdate(DocumentEvent event) { action.run(); }
        });
    }

    private String text(JTextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
