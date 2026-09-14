// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/ThemeService.java
// # 📌 Amac: Tema secimi, uygulama ve kalici tercih is kurallarini yonetir.
// # 📌 Service - Java
// # Version: 1.2.0
// # Aciklama: Kayitli temayi yukler ve Ayarlar ekranindan secilen acik/koyu temayi uygular; hizli toggle mantigi icermez.
// # Bagimli Oldugu Katman: Service | Repository | Tool | Model
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.models.ThemeMode;
import com.turkuazlabs.telefonrehberi.repositories.UserPreferencesRepository;
import com.turkuazlabs.telefonrehberi.tools.FlatLafThemeTool;

public final class ThemeService {
    private final UserPreferencesRepository preferencesRepository;
    private final FlatLafThemeTool themeTool;
    private ThemeMode currentMode;

    public ThemeService(UserPreferencesRepository preferencesRepository, FlatLafThemeTool themeTool) {
        this.preferencesRepository = preferencesRepository;
        this.themeTool = themeTool;
        this.currentMode = ThemeMode.LIGHT;
    }

    public ThemeMode applySavedTheme() {
        currentMode = preferencesRepository.loadTheme();
        themeTool.apply(currentMode);
        return currentMode;
    }

    public ThemeMode currentMode() {
        return currentMode;
    }

    public ThemeMode setTheme(ThemeMode mode) {
        ThemeMode safeMode = mode == null ? ThemeMode.LIGHT : mode;
        themeTool.apply(safeMode);
        preferencesRepository.saveTheme(safeMode);
        currentMode = safeMode;
        return currentMode;
    }
}
