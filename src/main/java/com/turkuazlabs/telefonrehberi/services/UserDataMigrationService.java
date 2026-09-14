// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/UserDataMigrationService.java
// # 📌 Amac: Portable ve onceki OS yerlesimlerindeki kullanici verilerini yeni platform klasorlerine kayipsiz tasir.
// # 📌 Service - Java
// # Version: 1.2.0
// # Aciklama: Windows DB bundle'ini Contacts/Veri alanina tasir; eski portable, backup ve preferences migrationlarini korur.
// # Bagimli Oldugu Katman: Service | Repository | Config
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.repositories.UserDataMigrationRepository;

public final class UserDataMigrationService {
    private static final String PORTABLE_MIGRATION_MARKER_CONTENT = "user_data_layout=2.32.1\n";
    private static final String PLATFORM_MIGRATION_MARKER_CONTENT = "user_data_layout=2.34.0\n";
    private static final String DATABASE_MIGRATION_MARKER_CONTENT = "database_layout=2.34.1\n";

    private final UserDataMigrationRepository repository;

    public UserDataMigrationService(UserDataMigrationRepository repository) {
        this.repository = repository;
    }

    public boolean migratePortableUserDataIfNeeded() {
        prepareCurrentLayout();
        boolean migrated = migrateDatabaseLayout();
        migrated |= migratePortableLayout();
        migrated |= migratePlatformLayout();
        return migrated;
    }

    private void prepareCurrentLayout() {
        repository.prepareDirectory(AppConfig.USER_DATA_ROOT);
        repository.prepareDirectory(AppConfig.USER_CONFIG_ROOT);
        repository.prepareDirectory(AppConfig.USER_CACHE_ROOT);
        repository.prepareDirectory(AppConfig.USER_FILES_ROOT);
        repository.prepareDirectory(AppConfig.DATA_PATH);
        repository.prepareDirectory(AppConfig.USER_EXPORT_PATH);
        repository.prepareDirectory(AppConfig.BACKUP_PATH);
    }

    private boolean migrateDatabaseLayout() {
        if (repository.markerExists(AppConfig.USER_DATABASE_LAYOUT_MARKER)) {
            return false;
        }

        boolean migrated = false;
        if (!AppConfig.LEGACY_OS_DATABASE_FILE.equals(AppConfig.DATABASE_FILE)) {
            migrated = repository.copyDatabaseBundleIfMissing(AppConfig.LEGACY_OS_DATABASE_FILE, AppConfig.DATABASE_FILE);
        }

        repository.writeMarker(AppConfig.USER_DATABASE_LAYOUT_MARKER, DATABASE_MIGRATION_MARKER_CONTENT);
        return migrated;
    }

    private boolean migratePortableLayout() {
        if (repository.markerExists(AppConfig.USER_DATA_MIGRATION_MARKER)) {
            return false;
        }

        boolean migrated = false;
        migrated |= repository.copyDatabaseBundleIfMissing(AppConfig.PORTABLE_DATABASE_FILE, AppConfig.DATABASE_FILE);
        migrated |= repository.copyFileIfMissing(AppConfig.PORTABLE_LEGACY_DATA_FILE, AppConfig.LEGACY_DATA_FILE);
        migrated |= repository.copyFileIfMissing(AppConfig.PORTABLE_SYNC_TOKEN_FILE, AppConfig.SYNC_TOKEN_FILE);
        migrated |= repository.copyDirectoryContentsIfMissing(AppConfig.PORTABLE_SAVED_VIEWS_PATH, AppConfig.SAVED_VIEWS_PATH) > 0;
        migrated |= repository.copyDatabaseBackupsIfMissing(AppConfig.PORTABLE_BACKUP_PATH, AppConfig.BACKUP_PATH) > 0;
        migrated |= repository.copyFileIfMissing(AppConfig.PORTABLE_PREFERENCES_FILE, AppConfig.PREFERENCES_FILE);

        repository.writeMarker(AppConfig.USER_DATA_MIGRATION_MARKER, PORTABLE_MIGRATION_MARKER_CONTENT);
        return migrated;
    }

    private boolean migratePlatformLayout() {
        if (repository.markerExists(AppConfig.USER_PLATFORM_LAYOUT_MARKER)) {
            return false;
        }

        boolean migrated = false;
        if (!AppConfig.LEGACY_OS_BACKUP_PATH.equals(AppConfig.BACKUP_PATH)) {
            migrated |= repository.copyDatabaseBackupsIfMissing(AppConfig.LEGACY_OS_BACKUP_PATH, AppConfig.BACKUP_PATH) > 0;
        }
        if (!AppConfig.LEGACY_OS_PREFERENCES_FILE.equals(AppConfig.PREFERENCES_FILE)) {
            migrated |= repository.copyFileIfMissing(AppConfig.LEGACY_OS_PREFERENCES_FILE, AppConfig.PREFERENCES_FILE);
        }

        repository.writeMarker(AppConfig.USER_PLATFORM_LAYOUT_MARKER, PLATFORM_MIGRATION_MARKER_CONTENT);
        return migrated;
    }
}
