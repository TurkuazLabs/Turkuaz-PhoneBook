// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/SettingsService.java
// # 📌 Amac: Kullanici ayarlari is kurallarini ve runtime degisiklik kontrolunu yonetir.
// # 📌 Service - Java
// # Version: 2.4.0
// # Aciklama: Hatirlatma dahil tercihleri saklar; tema ve dili runtime uygular, yalniz mobil senkron servis degisikliklerini restart olarak isaretler.
// # Bagimli Oldugu Katman: Service | Repository | Model | Config | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.config.UiConfig;
import com.turkuazlabs.telefonrehberi.language.LocaleText;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.AppSettings;
import com.turkuazlabs.telefonrehberi.models.SettingsSaveResult;
import com.turkuazlabs.telefonrehberi.repositories.UserPreferencesRepository;

public final class SettingsService {
    private final UserPreferencesRepository preferencesRepository;
    private final ThemeService themeService;

    public SettingsService(UserPreferencesRepository preferencesRepository, ThemeService themeService) {
        this.preferencesRepository = preferencesRepository;
        this.themeService = themeService;
    }

    public AppSettings loadSettings() {
        AppSettings preferences = preferencesRepository.load();
        return new AppSettings(
                preferences.theme(),
                LocaleText.normalizeLanguageCode(preferences.languageCode()),
                UiConfig.isKnownPage(preferences.startupPage()) ? preferences.startupPage() : UiConfig.PAGE_DASHBOARD,
                preferences.rememberWindow(),
                Math.max(UiConfig.WINDOW_MIN_WIDTH, preferences.windowWidth()),
                Math.max(UiConfig.WINDOW_MIN_HEIGHT, preferences.windowHeight()),
                preferences.compactMode(),
                preferences.confirmDelete(),
                preferences.autoBackup(),
                Math.max(1, Math.min(UiConfig.MAX_BACKUP_RETENTION, preferences.backupRetention())),
                preferences.mobileSyncEnabled(),
                preferences.mobileSyncPort(),
                preferences.updateEnabled(),
                preferences.reminderNotificationsEnabled()
        );
    }

    public SettingsSaveResult saveSettings(AppSettings input) {
        validate(input);
        AppSettings before = loadSettings();
        String normalizedLanguage = LocaleText.normalizeLanguageCode(input.languageCode());
        boolean languageChanged = !before.languageCode().equals(normalizedLanguage);
        preferencesRepository.save(input);
        themeService.setTheme(input.theme());
        if (languageChanged) {
            LocaleText.applyLanguage(normalizedLanguage);
            Messages.reload();
        }
        boolean restartRequired = before.mobileSyncEnabled() != input.mobileSyncEnabled()
                || before.mobileSyncPort() != input.mobileSyncPort();
        return new SettingsSaveResult(languageChanged, restartRequired);
    }

    private void validate(AppSettings settings) {
        if (settings == null) throw new IllegalArgumentException(Messages.ERROR_SETTINGS_REQUIRED);
        if (!LocaleText.isSupportedLanguageCode(settings.languageCode())) {
            throw new IllegalArgumentException(Messages.ERROR_LANGUAGE_CODE);
        }
        if (!UiConfig.isKnownPage(settings.startupPage())) throw new IllegalArgumentException(Messages.ERROR_STARTUP_PAGE);
        if (settings.mobileSyncPort() < 1024 || settings.mobileSyncPort() > 65535) {
            throw new IllegalArgumentException(Messages.ERROR_SYNC_PORT_RANGE);
        }
        if (settings.backupRetention() < 1 || settings.backupRetention() > UiConfig.MAX_BACKUP_RETENTION) {
            throw new IllegalArgumentException(Messages.ERROR_BACKUP_RETENTION_RANGE);
        }
    }
}
