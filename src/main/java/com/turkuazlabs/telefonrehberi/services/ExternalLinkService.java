// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/ExternalLinkService.java
// # 📌 Amac: Uygulamanin resmi web ve destek baglantilarini merkezi kurallarla acar.
// # 📌 Service - Java
// # Version: 1.0.0
// # Aciklama: Hakkinda sayfasindaki harici baglantilari AppConfig uzerinden DesktopActionTool katmanina yonlendirir.
// # Bagimli Oldugu Katman: Service | Tool | Config | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.tools.DesktopActionTool;

public final class ExternalLinkService {
    private final DesktopActionTool desktopActionTool;

    public ExternalLinkService(DesktopActionTool desktopActionTool) {
        this.desktopActionTool = desktopActionTool;
    }

    public void openWebsite() {
        openRequired(AppConfig.PUBLIC_WEBSITE_URL);
    }

    public void openSupport() {
        openRequired(AppConfig.SUPPORT_URL);
    }

    private void openRequired(String url) {
        if (!desktopActionTool.open(desktopActionTool.webUri(url))) {
            throw new IllegalStateException(Messages.EXTERNAL_LINK_OPEN_FAILED);
        }
    }
}
