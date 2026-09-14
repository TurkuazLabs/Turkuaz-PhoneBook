// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/LabelColorPalette.java
// # 📌 Amac: Grup ve etiket renk anahtarlarini merkezi UI renklerine donusturur.
// # 📌 Config - Java
// # Version: 1.0.0
// # Aciklama: Yonetim listelerinde kullanilan renk anahtarlarini tek noktada tanimlar ve tema uyumlu fallback saglar.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.config;

import java.awt.Color;
import java.util.Map;

public final class LabelColorPalette {
    private static final Map<String, Color> COLORS = Map.of(
            "turkuaz", new Color(18, 140, 126),
            "mavi", new Color(66, 133, 244),
            "yesil", new Color(52, 168, 83),
            "sari", new Color(244, 180, 0),
            "turuncu", new Color(242, 133, 0),
            "kirmizi", new Color(229, 72, 77),
            "mor", new Color(126, 87, 194),
            "gri", new Color(117, 117, 117)
    );

    public static Color resolve(String key) {
        if (key == null) {
            return ModernThemePalette.accent();
        }
        return COLORS.getOrDefault(key.trim().toLowerCase(), ModernThemePalette.accent());
    }

    private LabelColorPalette() {
    }
}
