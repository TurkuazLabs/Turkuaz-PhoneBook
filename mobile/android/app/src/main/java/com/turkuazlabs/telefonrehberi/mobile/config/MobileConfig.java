// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/config/MobileConfig.java
// # 📌 Amac: Android mobil istemcinin teknik sabitlerini merkezi olarak tutar.
// # 📌 Config - Java
// # Version: 2.37.0
// # Aciklama: Genisletilmis API JSON/form alanlari, endpoint, header, HTTP, preference ve izin sabitlerini tanimlar.
// # Bagimli Oldugu Katman: Config
package com.turkuazlabs.telefonrehberi.mobile.config;

public final class MobileConfig {
    public static final String PREFERENCES_NAME = "turkuaz_phonebook_mobile";
    public static final String PREF_SERVER_URL = "server_url";
    public static final String PREF_TOKEN_LEGACY = "sync_token";
    public static final String PREF_TOKEN_ENCRYPTED = "sync_token_encrypted";
    public static final String PREF_TOKEN_IV = "sync_token_iv";
    public static final String PREF_SYNC_MAP_NATIVE_PREFIX = "sync_map_native_";
    public static final String PREF_SYNC_MAP_UUID_PREFIX = "sync_map_uuid_";
    public static final String KEYSTORE_TOKEN_ALIAS = "turkuaz_phonebook_sync_token";
    public static final String PREF_DEVICE_ID = "device_id";
    public static final String STATUS_PATH = "/api/v1/status";
    public static final String CONTACTS_PATH = "/api/v1/contacts";
    public static final String IMPORT_PATH = "/api/v1/import";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded; charset=UTF-8";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String JSON_CONTACTS = "contacts";
    public static final String JSON_ID = "id";
    public static final String FIELD_SYNC_UUID = "sync_uuid";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_PHONE_SECONDARY = "phone_secondary";
    public static final String FIELD_PHONE_WORK = "phone_work";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_EMAIL_SECONDARY = "email_secondary";
    public static final String FIELD_COMPANY = "company";
    public static final String FIELD_JOB_TITLE = "job_title";
    public static final String FIELD_BIRTHDAY = "birthday";
    public static final String FIELD_WEBSITE = "website";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_DISTRICT = "district";
    public static final String FIELD_POSTAL_CODE = "postal_code";
    public static final String FIELD_COUNTRY = "country";
    public static final String FIELD_NOTES = "notes";
    public static final String FIELD_FAVORITE = "favorite";
    public static final String FIELD_PHONES = "phones";
    public static final String FIELD_EMAILS = "emails";
    public static final String FIELD_PHOTO_BASE64 = "photo_base64";
    public static final String FORM_DEVICE_ID = "device_id";
    public static final String FORM_EXTERNAL_ID = "external_id";
    public static final String FORM_SYNC_UUID = "sync_uuid";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final int HTTP_SUCCESS_MIN = 200;
    public static final int HTTP_SUCCESS_MAX_EXCLUSIVE = 300;
    public static final int HTTP_CONNECT_TIMEOUT_MS = 10000;
    public static final int HTTP_READ_TIMEOUT_MS = 20000;
    public static final int PERMISSION_REQUEST_CONTACTS = 1001;

    private MobileConfig() {
    }
}
