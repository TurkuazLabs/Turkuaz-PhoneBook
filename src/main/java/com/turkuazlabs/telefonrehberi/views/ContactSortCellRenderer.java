// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ContactSortCellRenderer.java
// # 📌 Amac: Kisi siralama seceneklerini JComboBox icinde kullanici dostu metinlerle cizer.
// # 📌 View - Java
// # Version: 1.0.0
// # Aciklama: ContactSort enum degerlerini Language katmanindaki okunur etiketlerle goruntuler.
// # Bagimli Oldugu Katman: View | Language | Model
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.language.ContactSortText;
import com.turkuazlabs.telefonrehberi.models.ContactSort;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

public final class ContactSortCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof ContactSort sort) {
            setText(ContactSortText.display(sort));
        }
        return this;
    }
}
