// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/LinkLabel.java
// # 📌 Amac: Masaustu arayuzunde tiklanabilir marka ve destek baglantilarini ortak gorunumle sunar.
// # 📌 View - Java
// # Version: 1.0.0
// # Aciklama: Link gorunumunu, el imlecini ve tiklama eventini tek View bileseninde kapsuller.
// # Bagimli Oldugu Katman: View | Config
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;

import javax.swing.JLabel;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public final class LinkLabel extends JLabel {
    public LinkLabel(String text, Runnable action) {
        super(text);
        setForeground(ModernThemePalette.accentStrong());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(underlined(getFont()));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (isEnabled() && action != null) {
                    action.run();
                }
            }
        });
    }

    private Font underlined(Font source) {
        Map<TextAttribute, Object> attributes = new HashMap<>(source.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        return source.deriveFont(attributes);
    }
}
