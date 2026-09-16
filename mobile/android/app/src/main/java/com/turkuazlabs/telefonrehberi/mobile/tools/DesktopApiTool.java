// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/tools/DesktopApiTool.java
// # 📌 Amac: Android istemcinin masaustu LAN senkron API'siyle HTTP iletisimi yapan adaptorudur.
// # 📌 Tool - Java
// # Version: 2.37.2
// # Aciklama: HTTP, ag ve JSON parse hatalarini Language katmanindan yerellestirerek sarar; kisileri ve sync UUID yanitlarini guvenli sekilde ayrıştırır.
// # Bagimli Oldugu Katman: Tool | Config | Model | Language
package com.turkuazlabs.telefonrehberi.mobile.tools;

import com.turkuazlabs.telefonrehberi.mobile.config.MobileConfig;
import com.turkuazlabs.telefonrehberi.mobile.language.Messages;
import com.turkuazlabs.telefonrehberi.mobile.models.MobileContact;
import com.turkuazlabs.telefonrehberi.mobile.models.MobileContactMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class DesktopApiTool {
    public void test(String baseUrl, String token) {
        request(baseUrl + MobileConfig.STATUS_PATH, token, MobileConfig.HTTP_GET, null);
    }

    public List<MobileContact> fetchContacts(String baseUrl, String token) {
        String json = request(baseUrl + MobileConfig.CONTACTS_PATH, token, MobileConfig.HTTP_GET, null);
        try {
            JSONObject root = new JSONObject(json);
            JSONArray array = root.getJSONArray(MobileConfig.JSON_CONTACTS);
            List<MobileContact> contacts = new ArrayList<>();
            for (int index = 0; index < array.length(); index++) {
                JSONObject item = array.getJSONObject(index);
                List<MobileContactMethod> phones = methods(item.optJSONArray(MobileConfig.FIELD_PHONES));
                List<MobileContactMethod> emails = methods(item.optJSONArray(MobileConfig.FIELD_EMAILS));
                contacts.add(new MobileContact(
                        String.valueOf(item.getLong(MobileConfig.JSON_ID)), text(item, MobileConfig.FIELD_SYNC_UUID), text(item, MobileConfig.FIELD_NAME),
                        text(item, MobileConfig.FIELD_PHONE), text(item, MobileConfig.FIELD_PHONE_SECONDARY), text(item, MobileConfig.FIELD_PHONE_WORK),
                        text(item, MobileConfig.FIELD_EMAIL), text(item, MobileConfig.FIELD_EMAIL_SECONDARY), text(item, MobileConfig.FIELD_COMPANY),
                        text(item, MobileConfig.FIELD_JOB_TITLE), text(item, MobileConfig.FIELD_BIRTHDAY), text(item, MobileConfig.FIELD_WEBSITE),
                        text(item, MobileConfig.FIELD_CATEGORY), text(item, MobileConfig.FIELD_ADDRESS), text(item, MobileConfig.FIELD_CITY),
                        text(item, MobileConfig.FIELD_DISTRICT), text(item, MobileConfig.FIELD_POSTAL_CODE), text(item, MobileConfig.FIELD_COUNTRY),
                        text(item, MobileConfig.FIELD_NOTES), item.optBoolean(MobileConfig.FIELD_FAVORITE, false), phones, emails,
                        text(item, MobileConfig.FIELD_PHOTO_BASE64)
                ));
            }
            return contacts;
        } catch (JSONException exception) {
            throw jsonError(exception);
        }
    }

    public String pushContact(String baseUrl, String token, String deviceId, String syncUuid, MobileContact contact) {
        List<String> values = new ArrayList<>();
        values.add(encode(MobileConfig.FIELD_NAME, contact.name()));
        values.add(encode(MobileConfig.FIELD_PHONE, contact.phone()));
        values.add(encode(MobileConfig.FIELD_PHONE_SECONDARY, contact.phoneSecondary()));
        values.add(encode(MobileConfig.FIELD_PHONE_WORK, contact.phoneWork()));
        values.add(encode(MobileConfig.FIELD_EMAIL, contact.email()));
        values.add(encode(MobileConfig.FIELD_EMAIL_SECONDARY, contact.emailSecondary()));
        values.add(encode(MobileConfig.FIELD_PHONES, encodeMethods(contact.phones())));
        values.add(encode(MobileConfig.FIELD_EMAILS, encodeMethods(contact.emails())));
        values.add(encode(MobileConfig.FIELD_PHOTO_BASE64, contact.photoBase64()));
        values.add(encode(MobileConfig.FIELD_COMPANY, contact.company()));
        values.add(encode(MobileConfig.FIELD_JOB_TITLE, contact.jobTitle()));
        values.add(encode(MobileConfig.FIELD_BIRTHDAY, contact.birthday()));
        values.add(encode(MobileConfig.FIELD_WEBSITE, contact.website()));
        values.add(encode(MobileConfig.FIELD_CATEGORY, contact.category()));
        values.add(encode(MobileConfig.FIELD_ADDRESS, contact.address()));
        values.add(encode(MobileConfig.FIELD_CITY, contact.city()));
        values.add(encode(MobileConfig.FIELD_DISTRICT, contact.district()));
        values.add(encode(MobileConfig.FIELD_POSTAL_CODE, contact.postalCode()));
        values.add(encode(MobileConfig.FIELD_COUNTRY, contact.country()));
        values.add(encode(MobileConfig.FIELD_NOTES, contact.notes()));
        values.add(encode(MobileConfig.FIELD_FAVORITE, Boolean.toString(contact.favorite())));
        values.add(encode(MobileConfig.FORM_DEVICE_ID, deviceId));
        values.add(encode(MobileConfig.FORM_EXTERNAL_ID, contact.externalId()));
        values.add(encode(MobileConfig.FORM_SYNC_UUID, syncUuid));
        String response = request(baseUrl + MobileConfig.IMPORT_PATH, token, MobileConfig.HTTP_POST, String.join("&", values));
        try {
            return new JSONObject(response).optString(MobileConfig.FIELD_SYNC_UUID, "");
        } catch (JSONException exception) {
            throw jsonError(exception);
        }
    }

    private List<MobileContactMethod> methods(JSONArray array) {
        if (array == null) return List.of();
        List<MobileContactMethod> out = new ArrayList<>();
        for (int index = 0; index < array.length(); index++) {
            JSONObject value = array.optJSONObject(index);
            if (value == null) continue;
            out.add(new MobileContactMethod(
                    value.optString("kind", ""), value.optString("label", ""), value.optString("value", ""),
                    value.optBoolean("primary", false), value.optInt("position", index)
            ));
        }
        return List.copyOf(out);
    }

    private String encodeMethods(List<MobileContactMethod> methods) {
        StringBuilder out = new StringBuilder();
        for (MobileContactMethod method : methods) {
            if (method.value().isBlank()) continue;
            if (out.length() > 0) out.append('~');
            out.append(component(method.kind())).append('|').append(component(method.label())).append('|')
                    .append(component(method.value())).append('|').append(method.primary() ? '1' : '0').append('|').append(method.position());
        }
        return out.toString();
    }

    private String component(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException exception) {
            throw new IllegalStateException(Messages.desktopApiRequestFailed(exception.getMessage()), exception);
        }
    }

    private String text(JSONObject item, String key) {
        return item.optString(key, "");
    }

    private IllegalStateException jsonError(JSONException exception) {
        return new IllegalStateException(Messages.desktopApiJsonInvalid(exception.getMessage()), exception);
    }

    private String request(String url, String token, String method, String body) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setConnectTimeout(MobileConfig.HTTP_CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(MobileConfig.HTTP_READ_TIMEOUT_MS);
            connection.setRequestMethod(method);
            connection.setRequestProperty(MobileConfig.AUTHORIZATION_HEADER, MobileConfig.BEARER_PREFIX + token);
            connection.setRequestProperty(MobileConfig.ACCEPT_HEADER, MobileConfig.CONTENT_TYPE_JSON);
            if (body != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty(MobileConfig.CONTENT_TYPE_HEADER, MobileConfig.CONTENT_TYPE_FORM);
                connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
            }
            int status = connection.getResponseCode();
            InputStream stream = status >= MobileConfig.HTTP_SUCCESS_MIN && status < MobileConfig.HTTP_SUCCESS_MAX_EXCLUSIVE
                    ? connection.getInputStream() : connection.getErrorStream();
            String response = read(stream);
            if (status < MobileConfig.HTTP_SUCCESS_MIN || status >= MobileConfig.HTTP_SUCCESS_MAX_EXCLUSIVE) {
                throw new ApiRequestException(Messages.httpError(status, response));
            }
            return response;
        } catch (ApiRequestException exception) {
            throw new IllegalStateException(exception.getMessage(), exception);
        } catch (Exception exception) {
            throw new IllegalStateException(Messages.desktopApiRequestFailed(exception.getMessage()), exception);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private String read(InputStream stream) throws Exception {
        if (stream == null) return "";
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
        }
        return result.toString();
    }

    private String encode(String key, String value) {
        try {
            return URLEncoder.encode(key, StandardCharsets.UTF_8.name()) + "=" + URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException exception) {
            throw new IllegalStateException(Messages.desktopApiRequestFailed(exception.getMessage()), exception);
        }
    }

    private static final class ApiRequestException extends RuntimeException {
        private ApiRequestException(String message) {
            super(message);
        }
    }
}
