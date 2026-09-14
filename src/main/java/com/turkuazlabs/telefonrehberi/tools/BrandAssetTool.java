// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/BrandAssetTool.java
// # 📌 Amac: Uygulama marka ikonunu classpath veya portable assets klasorunden yukler.
// # 📌 Tool - Java
// # Version: 1.0.0
// # Aciklama: JAR icine gomulu PNG birincil, portable assets dosyasi ikincil kaynak olarak kullanilir.
// # Bagimli Oldugu Katman: Tool | Config
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class BrandAssetTool {
    private static final String CLASSPATH_ICON = "/assets/branding/app-icon-128.png";
    private static final Path FILE_ICON = AppConfig.APP_ROOT.resolve("assets/branding/app-icon-128.png");

    public Optional<Image> loadAppIcon() {
        try (InputStream stream = BrandAssetTool.class.getResourceAsStream(CLASSPATH_ICON)) {
            if (stream != null) {
                return Optional.ofNullable(ImageIO.read(stream));
            }
        } catch (IOException ignored) {
            // Portable dosya fallback'i denenir.
        }
        if (Files.exists(FILE_ICON)) {
            try {
                return Optional.ofNullable(ImageIO.read(FILE_ICON.toFile()));
            } catch (IOException ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
