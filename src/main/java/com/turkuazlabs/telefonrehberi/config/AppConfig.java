// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/AppConfig.java
// # 📌 Amac: Uygulama protokol sabitlerini ve yerel config dosya yollarini merkezi sunar.
// # 📌 Config - Java
// Version: 2.41.0
// Aciklama: Yerellestirilmis urun adi, veri yollari, sync limitleri ve v2.41 tema/release kalite sabitlerini merkezi tutar.
// Bagimli Oldugu Katman: Config | Language
package com.turkuazlabs.telefonrehberi.config;

import com.turkuazlabs.telefonrehberi.language.ProductText;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class AppConfig {
    public static final String APP_NAME = ProductText.APP_NAME;
    public static final String APP_VERSION = "2.41.0";
    public static final String BRAND_SITE_NAME = "TurkuazLabs";
    public static final String PUBLIC_WEBSITE_DISPLAY = "www.turkuazlabs.com";
    public static final String PUBLIC_WEBSITE_URL = "https://www.turkuazlabs.com";
    public static final String SUPPORT_DISPLAY = "buymeacoffee.com/turkuazlabs";
    public static final String SUPPORT_URL = "https://buymeacoffee.com/turkuazlabs";
    public static final List<String> LABEL_COLORS = List.of("turkuaz", "mavi", "yesil", "sari", "turuncu", "kirmizi", "mor", "gri");
    public static final Path APP_ROOT = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
    public static final Path RUNTIME_CONFIG_FILE = APP_ROOT.resolve("config").resolve("app.yml");
    public static final Path LAUNCHER_CONFIG_FILE = APP_ROOT.resolve("config").resolve("launcher.yml");
    public static final RuntimeConfig RUNTIME = RuntimeConfig.load(RUNTIME_CONFIG_FILE);
    public static final Path USER_DATA_ROOT = UserDataPathResolver.resolveDataRoot();
    public static final Path USER_CONFIG_ROOT = UserDataPathResolver.resolveConfigRoot();
    public static final Path USER_CACHE_ROOT = UserDataPathResolver.resolveCacheRoot();
    public static final Path USER_FILES_ROOT = UserFilesPathResolver.resolve();
    public static final Path USER_EXPORT_PATH = USER_FILES_ROOT.resolve("Disari Aktarilanlar");
    public static final Path BACKUP_PATH = USER_FILES_ROOT.resolve("Yedekler");
    public static final Path USER_DATA_MIGRATION_MARKER = USER_DATA_ROOT.resolve(".storage-layout-2.32.1");
    public static final Path USER_PLATFORM_LAYOUT_MARKER = USER_DATA_ROOT.resolve(".storage-layout-2.34.0");
    public static final Path USER_DATABASE_LAYOUT_MARKER = USER_DATA_ROOT.resolve(".storage-layout-2.34.1");
    public static final Charset DATA_CHARSET = StandardCharsets.UTF_8;
    public static final String LEGACY_FIELD_SEPARATOR = "\t";

    public static final Path DATABASE_FILE = DatabasePathResolver.resolve(USER_FILES_ROOT, USER_DATA_ROOT, RUNTIME.databaseFile());
    public static final Path LEGACY_DATA_FILE = resolveUserDataPath(RUNTIME.legacyDataFile());
    public static final Path SYNC_TOKEN_FILE = resolveUserDataPath(RUNTIME.syncTokenFile());
    public static final Path DATA_PATH = DATABASE_FILE.getParent();
    public static final Path LEGACY_OS_DATABASE_FILE = resolveUserDataPath(RUNTIME.databaseFile());
    public static final Path SAVED_VIEWS_PATH = USER_DATA_ROOT.resolve("data").resolve("saved-views");
    public static final Path PREFERENCES_FILE = USER_CONFIG_ROOT.resolve("preferences.yml");
    public static final Path REMINDER_NOTIFICATION_STATE_FILE = USER_CONFIG_ROOT.resolve("reminder-notification-state.yml");
    public static final Path LEGACY_OS_BACKUP_PATH = USER_DATA_ROOT.resolve("backup");
    public static final Path LEGACY_OS_PREFERENCES_FILE = USER_DATA_ROOT.resolve("config").resolve("preferences.yml");

    public static final Path PORTABLE_DATABASE_FILE = resolvePortablePath(RUNTIME.databaseFile());
    public static final Path PORTABLE_LEGACY_DATA_FILE = resolvePortablePath(RUNTIME.legacyDataFile());
    public static final Path PORTABLE_SYNC_TOKEN_FILE = resolvePortablePath(RUNTIME.syncTokenFile());
    public static final Path PORTABLE_SAVED_VIEWS_PATH = APP_ROOT.resolve("data").resolve("saved-views");
    public static final Path PORTABLE_BACKUP_PATH = APP_ROOT.resolve("backup");
    public static final Path PORTABLE_PREFERENCES_FILE = APP_ROOT.resolve("config").resolve("preferences.yml");

    public static final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
    public static final String SQLITE_DRIVER_CLASS = "org.sqlite.JDBC";
    public static final int SQLITE_BUSY_TIMEOUT_MILLISECONDS = RUNTIME.sqliteBusyTimeoutMs();

    public static final boolean MOBILE_SYNC_ENABLED = RUNTIME.mobileSyncEnabled();
    public static final String MOBILE_SYNC_BIND_ADDRESS = RUNTIME.mobileSyncBindAddress();
    public static final int MOBILE_SYNC_PORT = RUNTIME.mobileSyncPort();
    public static final int MOBILE_SYNC_BACKLOG = RUNTIME.mobileSyncBacklog();
    public static final int MOBILE_SYNC_THREADS = RUNTIME.mobileSyncThreads();
    public static final int MOBILE_SYNC_MAX_REQUEST_BYTES = RUNTIME.mobileSyncMaxRequestBytes();
    public static final int SYNC_TOKEN_BYTES = RUNTIME.syncTokenBytes();
    public static final String DEFAULT_PHONE_COUNTRY_ISO = RUNTIME.defaultPhoneCountryIso();
    public static final int BULK_HISTORY_LIMIT = RUNTIME.bulkHistoryLimit();
    public static final int HISTORY_LIMIT = RUNTIME.historyLimit();
    public static final int CONTACT_ACTIVITY_LIMIT = RUNTIME.contactActivityLimit();
    public static final int HISTORY_RETENTION_LIMIT = RUNTIME.historyRetentionLimit();

    public static final String API_STATUS_PATH = "/api/v1/status";
    public static final String API_CONTACTS_PATH = "/api/v1/contacts";
    public static final String API_IMPORT_PATH = "/api/v1/import";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
    public static final String API_FIELD_NAME = "name";
    public static final String API_FIELD_PHONE = "phone";
    public static final String API_FIELD_PHONE_SECONDARY = "phone_secondary";
    public static final String API_FIELD_PHONE_WORK = "phone_work";
    public static final String API_FIELD_EMAIL = "email";
    public static final String API_FIELD_EMAIL_SECONDARY = "email_secondary";
    public static final String API_FIELD_COMPANY = "company";
    public static final String API_FIELD_JOB_TITLE = "job_title";
    public static final String API_FIELD_BIRTHDAY = "birthday";
    public static final String API_FIELD_WEBSITE = "website";
    public static final String API_FIELD_CATEGORY = "category";
    public static final String API_FIELD_ADDRESS = "address";
    public static final String API_FIELD_CITY = "city";
    public static final String API_FIELD_DISTRICT = "district";
    public static final String API_FIELD_POSTAL_CODE = "postal_code";
    public static final String API_FIELD_COUNTRY = "country";
    public static final String API_FIELD_NOTES = "notes";
    public static final String API_FIELD_FAVORITE = "favorite";
    public static final String API_FIELD_PHONES = "phones";
    public static final String API_FIELD_EMAILS = "emails";
    public static final String API_FIELD_PHOTO_BASE64 = "photo_base64";
    public static final String API_FIELD_DEVICE_ID = "device_id";
    public static final String API_FIELD_EXTERNAL_ID = "external_id";
    public static final String API_FIELD_SYNC_UUID = "sync_uuid";

    private static Path resolveUserDataPath(String configuredPath) {
        Path path = Paths.get(configuredPath);
        return path.isAbsolute() ? path.normalize() : USER_DATA_ROOT.resolve(path).normalize();
    }

    private static Path resolvePortablePath(String configuredPath) {
        Path path = Paths.get(configuredPath);
        return path.isAbsolute() ? path.normalize() : APP_ROOT.resolve(path).normalize();
    }

    private AppConfig() {
    }
}
