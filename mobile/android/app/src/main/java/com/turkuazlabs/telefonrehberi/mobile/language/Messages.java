// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/language/Messages.java
// # 📌 Amac: Android mobil istemcide kullanilan kullanici mesajlarini sistem diline gore merkezi olarak tutar.
// # 📌 Language - Java
// # Version: 1.4.0
// # Aciklama: Turkce sistemlerde Turkce, diger sistemlerde Ingilizce senkron, HTTP, JSON ve ag hata mesajlari kullanir.
// # Bagimli Oldugu Katman: Language
package com.turkuazlabs.telefonrehberi.mobile.language;

import java.util.Locale;

public final class Messages {
    private static final boolean TURKISH = "tr".equalsIgnoreCase(Locale.getDefault().getLanguage());

    public static final String READY = text("Hazir", "Ready");
    public static final String WORKING = text("Islem yapiliyor...", "Working...");
    public static final String CONNECTION_OK = text("PC baglantisi basarili.", "PC connection successful.");
    public static final String SERVER_REQUIRED = text("PC adresi bos olamaz.", "PC address cannot be empty.");
    public static final String TOKEN_REQUIRED = text("Senkron tokeni bos olamaz.", "Sync token cannot be empty.");
    public static final String CONTACT_PERMISSION_REQUIRED = text(
            "Rehber izni verilmeden senkron yapilamaz.",
            "Contacts permission is required for sync."
    );
    public static final String PULL_RESULT_FORMAT = text(
            "%d PC kaydi telefona eklendi, %d mevcut kayit atlandi.",
            "%d PC contacts added to the phone, %d existing contacts skipped."
    );
    public static final String PUSH_RESULT_FORMAT = text(
            "%d telefon kaydi PC'ye gonderildi.",
            "%d phone contacts sent to the PC."
    );
    public static final String ERROR_PREFIX = text("Hata: ", "Error: ");
    public static final String HTTP_ERROR_FORMAT = "HTTP %d: %s";
    public static final String DESKTOP_API_JSON_INVALID_FORMAT = text(
            "Masaustu API JSON yaniti gecersiz: %s",
            "Desktop API returned invalid JSON: %s"
    );
    public static final String DESKTOP_API_REQUEST_FAILED_FORMAT = text(
            "Masaustu API istegi basarisiz: %s",
            "Desktop API request failed: %s"
    );
    public static final String CONTACT_ID_MISSING = text(
            "Android contact kimligi alinamadi.",
            "Android contact ID could not be obtained."
    );
    public static final String CONTACT_ID_UNRESOLVED = text(
            "Android contact kimligi cozumlenemedi.",
            "Android contact ID could not be resolved."
    );
    public static final String SECURE_TOKEN_STORE_FAILED = text(
            "Senkron tokeni guvenli saklanamadi.",
            "Sync token could not be stored securely."
    );

    public static String httpError(int status, String body) {
        return String.format(Locale.getDefault(), HTTP_ERROR_FORMAT, status, body);
    }

    public static String desktopApiJsonInvalid(String detail) {
        return String.format(Locale.getDefault(), DESKTOP_API_JSON_INVALID_FORMAT, safeDetail(detail));
    }

    public static String desktopApiRequestFailed(String detail) {
        return String.format(Locale.getDefault(), DESKTOP_API_REQUEST_FAILED_FORMAT, safeDetail(detail));
    }

    private static String safeDetail(String detail) {
        return detail == null || detail.isBlank() ? text("Bilinmeyen hata", "Unknown error") : detail;
    }

    private static String text(String turkish, String english) {
        return TURKISH ? turkish : english;
    }

    private Messages() {
    }
}
