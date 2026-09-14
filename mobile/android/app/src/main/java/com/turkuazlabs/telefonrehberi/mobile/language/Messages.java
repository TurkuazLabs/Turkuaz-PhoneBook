// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/language/Messages.java
// # 📌 Amac: Android mobil istemcide kullanilan kullanici mesajlarini merkezi olarak tutar.
// # 📌 Language - Java
// # Version: 1.2.0
// # Aciklama: Senkron, izin, guvenli token ve contact kimlik hata mesajlarini tanimlar.
// # Bagimli Oldugu Katman: Language
package com.turkuazlabs.telefonrehberi.mobile.language;

public final class Messages {
    public static final String READY = "Hazir";
    public static final String WORKING = "Islem yapiliyor...";
    public static final String CONNECTION_OK = "PC baglantisi basarili.";
    public static final String SERVER_REQUIRED = "PC adresi bos olamaz.";
    public static final String TOKEN_REQUIRED = "Senkron tokeni bos olamaz.";
    public static final String CONTACT_PERMISSION_REQUIRED = "Rehber izni verilmeden senkron yapilamaz.";
    public static final String PULL_RESULT_FORMAT = "%d PC kaydi telefona eklendi, %d mevcut kayit atlandi.";
    public static final String PUSH_RESULT_FORMAT = "%d telefon kaydi PC'ye gonderildi.";
    public static final String ERROR_PREFIX = "Hata: ";
    public static final String HTTP_ERROR_FORMAT = "HTTP %d: %s";
    public static final String CONTACT_ID_MISSING = "Android contact kimligi alinamadi.";
    public static final String CONTACT_ID_UNRESOLVED = "Android contact kimligi cozumlenemedi.";
    public static final String SECURE_TOKEN_STORE_FAILED = "Senkron tokeni guvenli saklanamadi.";

    public static String httpError(int status, String body) {
        return String.format(HTTP_ERROR_FORMAT, status, body);
    }

    private Messages() {
    }
}
