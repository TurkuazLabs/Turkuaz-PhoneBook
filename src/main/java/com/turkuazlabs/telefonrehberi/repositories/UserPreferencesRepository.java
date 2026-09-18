// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/UserPreferencesRepository.java
// # 📌 Amac: Tum kullaniciya acik ayarlari yazilabilir OS kullanici config alaninda saklar.
// # 📌 Repository - Java
// # Version: 3.1.0
// # Aciklama: Tema, pencere, yedekleme, mobil senkron ve update tercihlerini Program Files configinden ayirir.
// # Bagimli Oldugu Katman: Repository | Model | Config | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.config.UiConfig;
import com.turkuazlabs.telefonrehberi.language.LocaleText;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.AppSettings;
import com.turkuazlabs.telefonrehberi.models.ThemeMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class UserPreferencesRepository {
    private static final String THEME_KEY = "theme";
    private static final String LANGUAGE_KEY = LocaleText.LANGUAGE_PREFERENCE_KEY;
    private static final String STARTUP_PAGE_KEY = "startup_page";
    private static final String REMEMBER_WINDOW_KEY = "remember_window";
    private static final String WINDOW_WIDTH_KEY = "window_width";
    private static final String WINDOW_HEIGHT_KEY = "window_height";
    private static final String COMPACT_MODE_KEY = "compact_mode";
    private static final String CONFIRM_DELETE_KEY = "confirm_delete";
    private static final String AUTO_BACKUP_KEY = "auto_backup";
    private static final String BACKUP_RETENTION_KEY = "backup_retention";
    private static final String MOBILE_SYNC_ENABLED_KEY = "mobile_sync_enabled";
    private static final String MOBILE_SYNC_PORT_KEY = "mobile_sync_port";
    private static final String UPDATE_ENABLED_KEY = "update_enabled";

    private final SimpleYamlRepository yamlRepository;
    private final Path preferencesFile;

    public UserPreferencesRepository() {
        this(new SimpleYamlRepository(), AppConfig.PREFERENCES_FILE);
    }

    public UserPreferencesRepository(SimpleYamlRepository yamlRepository, Path preferencesFile) {
        this.yamlRepository = yamlRepository;
        this.preferencesFile = preferencesFile;
    }

    public ThemeMode loadTheme() {
        return load().theme();
    }

    public AppSettings load() {
        Map<String, String> values = yamlRepository.read(preferencesFile);
        return new AppSettings(
                ThemeMode.fromPersistedValue(values.getOrDefault(THEME_KEY, ThemeMode.LIGHT.persistedValue())),
                LocaleText.normalizeLanguageCode(values.getOrDefault(LANGUAGE_KEY, LocaleText.LANGUAGE_SYSTEM_CODE)),
                values.getOrDefault(STARTUP_PAGE_KEY, UiConfig.PAGE_DASHBOARD),
                bool(values.get(REMEMBER_WINDOW_KEY), true),
                integer(values.get(WINDOW_WIDTH_KEY), UiConfig.WINDOW_WIDTH),
                integer(values.get(WINDOW_HEIGHT_KEY), UiConfig.WINDOW_HEIGHT),
                bool(values.get(COMPACT_MODE_KEY), false),
                bool(values.get(CONFIRM_DELETE_KEY), true),
                bool(values.get(AUTO_BACKUP_KEY), true),
                integer(values.get(BACKUP_RETENTION_KEY), UiConfig.DEFAULT_BACKUP_RETENTION),
                bool(values.get(MOBILE_SYNC_ENABLED_KEY), AppConfig.MOBILE_SYNC_ENABLED),
                integer(values.get(MOBILE_SYNC_PORT_KEY), AppConfig.MOBILE_SYNC_PORT),
                bool(values.get(UPDATE_ENABLED_KEY), true)
        );
    }

    public void saveTheme(ThemeMode mode) {
        AppSettings current = load();
        save(new AppSettings(
                mode, current.languageCode(), current.startupPage(), current.rememberWindow(), current.windowWidth(), current.windowHeight(),
                current.compactMode(), current.confirmDelete(), current.autoBackup(), current.backupRetention(),
                current.mobileSyncEnabled(), current.mobileSyncPort(), current.updateEnabled()
        ));
    }

    public void save(AppSettings settings) {
        try {
            Files.createDirectories(preferencesFile.getParent());
            List<String> lines = List.of(
                    "# 📄 Dosya Yolu: C:/Users/<kullanici>/AppData/Roaming/TurkuazLabs/TelefonRehberi/config/preferences.yml",
                    "# 📌 Amac: Kullanici arayuz, yedekleme, senkron ve update tercihlerini kalici tutar.",
                    "# 📌 Config - YAML",
                    "# Version: 3.0.0",
                    "# Aciklama: Program Files altindaki salt-okunur teknik configden bagimsiz kullanici tercihleridir.",
                    "# Bagimli Oldugu Katman: Repository | View | Service",
                    "",
                    THEME_KEY + ": \"" + settings.theme().persistedValue() + "\"",
                    LANGUAGE_KEY + ": \"" + LocaleText.normalizeLanguageCode(settings.languageCode()) + "\"",
                    STARTUP_PAGE_KEY + ": \"" + safePage(settings.startupPage()) + "\"",
                    REMEMBER_WINDOW_KEY + ": \"" + settings.rememberWindow() + "\"",
                    WINDOW_WIDTH_KEY + ": \"" + settings.windowWidth() + "\"",
                    WINDOW_HEIGHT_KEY + ": \"" + settings.windowHeight() + "\"",
                    COMPACT_MODE_KEY + ": \"" + settings.compactMode() + "\"",
                    CONFIRM_DELETE_KEY + ": \"" + settings.confirmDelete() + "\"",
                    AUTO_BACKUP_KEY + ": \"" + settings.autoBackup() + "\"",
                    BACKUP_RETENTION_KEY + ": \"" + settings.backupRetention() + "\"",
                    MOBILE_SYNC_ENABLED_KEY + ": \"" + settings.mobileSyncEnabled() + "\"",
                    MOBILE_SYNC_PORT_KEY + ": \"" + settings.mobileSyncPort() + "\"",
                    UPDATE_ENABLED_KEY + ": \"" + settings.updateEnabled() + "\""
            );
            Files.write(preferencesFile, lines, AppConfig.DATA_CHARSET);
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SETTINGS_WRITE, exception);
        }
    }

    private boolean bool(String value, boolean fallback) {
        return value == null ? fallback : Boolean.parseBoolean(value);
    }

    private int integer(String value, int fallback) {
        try {
            return value == null ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private String safePage(String value) {
        return UiConfig.isKnownPage(value) ? value : UiConfig.PAGE_DASHBOARD;
    }
}
