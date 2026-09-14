// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/MobileSyncFormTool.java
// # 📌 Amac: Mobil HTTP form payloadini boyut sinirli okuyup tipli import modeline donusturur.
// # 📌 Tool - Java
// # Version: 1.0.0
// # Aciklama: Controller icindeki decode/mapping isini ayirir; request body limitini uygular ve ContactDraft olusturur.
// # Bagimli Oldugu Katman: Tool | Model | Config | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.MobileSyncImportRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

public final class MobileSyncFormTool {
    private static final int BUFFER_SIZE = 8192;
    private final FormCodec formCodec;
    private final ContactMethodCodec methodCodec;

    public MobileSyncFormTool(FormCodec formCodec, ContactMethodCodec methodCodec) {
        this.formCodec = formCodec;
        this.methodCodec = methodCodec;
    }

    public MobileSyncImportRequest decode(InputStream inputStream, int maxBytes) throws IOException {
        String body = new String(readBounded(inputStream, maxBytes), AppConfig.DATA_CHARSET);
        Map<String, String> form = formCodec.decode(body);
        ContactDraft legacy = new ContactDraft(
                value(form, AppConfig.API_FIELD_NAME), value(form, AppConfig.API_FIELD_PHONE),
                value(form, AppConfig.API_FIELD_PHONE_SECONDARY), value(form, AppConfig.API_FIELD_PHONE_WORK),
                value(form, AppConfig.API_FIELD_EMAIL), value(form, AppConfig.API_FIELD_EMAIL_SECONDARY),
                value(form, AppConfig.API_FIELD_COMPANY), value(form, AppConfig.API_FIELD_JOB_TITLE),
                value(form, AppConfig.API_FIELD_BIRTHDAY), value(form, AppConfig.API_FIELD_WEBSITE),
                value(form, AppConfig.API_FIELD_CATEGORY), value(form, AppConfig.API_FIELD_ADDRESS),
                value(form, AppConfig.API_FIELD_CITY), value(form, AppConfig.API_FIELD_DISTRICT),
                value(form, AppConfig.API_FIELD_POSTAL_CODE), value(form, AppConfig.API_FIELD_COUNTRY),
                value(form, AppConfig.API_FIELD_NOTES), Boolean.parseBoolean(value(form, AppConfig.API_FIELD_FAVORITE))
        );
        var phones = methodCodec.decode(value(form, AppConfig.API_FIELD_PHONES));
        var emails = methodCodec.decode(value(form, AppConfig.API_FIELD_EMAILS));
        byte[] photo = decodePhoto(value(form, AppConfig.API_FIELD_PHOTO_BASE64));
        ContactDraft draft = new ContactDraft(
                legacy.name(), legacy.phone(), legacy.phoneSecondary(), legacy.phoneWork(), legacy.email(), legacy.emailSecondary(),
                legacy.company(), legacy.jobTitle(), legacy.birthday(), legacy.website(), legacy.category(), legacy.address(),
                legacy.city(), legacy.district(), legacy.postalCode(), legacy.country(), legacy.notes(), legacy.favorite(),
                phones.isEmpty() ? legacy.phones() : phones, emails.isEmpty() ? legacy.emails() : emails, photo
        );
        return new MobileSyncImportRequest(
                draft,
                value(form, AppConfig.API_FIELD_DEVICE_ID),
                value(form, AppConfig.API_FIELD_EXTERNAL_ID),
                value(form, AppConfig.API_FIELD_SYNC_UUID)
        );
    }

    private byte[] readBounded(InputStream inputStream, int maxBytes) throws IOException {
        if (maxBytes < 1) throw new IllegalArgumentException(Messages.ERROR_SYNC_REQUEST_TOO_LARGE);
        ByteArrayOutputStream output = new ByteArrayOutputStream(Math.min(maxBytes, BUFFER_SIZE));
        byte[] buffer = new byte[BUFFER_SIZE];
        int total = 0;
        int read;
        while ((read = inputStream.read(buffer)) >= 0) {
            total += read;
            if (total > maxBytes) throw new IllegalArgumentException(Messages.ERROR_SYNC_REQUEST_TOO_LARGE);
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private byte[] decodePhoto(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Base64.getDecoder().decode(value); }
        catch (IllegalArgumentException ignored) { return null; }
    }

    private String value(Map<String, String> form, String key) {
        return form.getOrDefault(key, "");
    }
}
