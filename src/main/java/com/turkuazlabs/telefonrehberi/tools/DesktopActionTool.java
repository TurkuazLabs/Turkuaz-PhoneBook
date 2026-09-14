// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/DesktopActionTool.java
// # 📌 Amac: Telefon, e-posta, WhatsApp ve pano hizli aksiyonlarini masaustu isletim sistemine iletir.
// # 📌 Tool - Java
// # Version: 2.16.0
// # Aciklama: Telefon, e-posta, web URI uretimi, varsayilan masaustu uygulamasini acma ve panoya metin kopyalama islemlerini kapsuller.
// # Bagimli Oldugu Katman: Tool
package com.turkuazlabs.telefonrehberi.tools;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.net.URISyntaxException;

public final class DesktopActionTool {
    public URI phoneUri(String phone) {
        return URI.create("tel:" + normalizedDialPhone(phone));
    }

    public URI whatsappUri(String phone) {
        String digits = digitsOnly(phone);
        return URI.create("https://wa.me/" + digits);
    }

    public URI emailUri(String email) {
        try {
            return new URI("mailto", email == null ? "" : email.trim(), null);
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    public URI webUri(String url) {
        return URI.create(url == null ? "" : url.trim());
    }

    public boolean open(URI uri) {
        if (uri == null || !Desktop.isDesktopSupported()) {
            return false;
        }
        try {
            Desktop desktop = Desktop.getDesktop();
            if ("mailto".equalsIgnoreCase(uri.getScheme()) && desktop.isSupported(Desktop.Action.MAIL)) {
                desktop.mail(uri);
                return true;
            }
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
                return true;
            }
            return false;
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean copyText(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), null);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private String normalizedDialPhone(String phone) {
        String value = phone == null ? "" : phone.trim();
        String digits = digitsOnly(value);
        return value.startsWith("+") ? "+" + digits : digits;
    }

    private String digitsOnly(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }
}
