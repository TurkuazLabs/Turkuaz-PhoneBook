// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/ApiJsonView.java
// # 📌 Amac: Mobil senkron HTTP API cevaplarini JSON olarak uretir.
// # 📌 View - Java
// # Version: 2.37.0
// # Aciklama: Kisi listesi, sinirsiz telefon/e-posta, profil fotografi, durum ve hata cevaplarini serialize eder.
// # Bagimli Oldugu Katman: View | Model
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;

import java.util.Base64;
import java.util.List;

public final class ApiJsonView {
    public String contacts(List<Contact> contacts) {
        StringBuilder json = new StringBuilder("{\"contacts\":[");
        for (int index = 0; index < contacts.size(); index++) {
            if (index > 0) json.append(',');
            appendContact(json, contacts.get(index));
        }
        return json.append("]}").toString();
    }

    public String status(String version) { return "{\"ok\":true,\"version\":\"" + escape(version) + "\"}"; }
    public String imported(long id, String syncUuid) { return "{\"ok\":true,\"id\":" + id + ",\"sync_uuid\":\"" + escape(syncUuid) + "\"}"; }
    public String error(String message) { return "{\"ok\":false,\"error\":\"" + escape(message) + "\"}"; }

    private void appendContact(StringBuilder json, Contact contact) {
        json.append('{');
        number(json, "id", contact.id());
        text(json, "sync_uuid", contact.syncUuid());
        text(json, "name", contact.name());
        text(json, "phone", contact.phone());
        text(json, "phone_secondary", contact.phoneSecondary());
        text(json, "phone_work", contact.phoneWork());
        text(json, "email", contact.email());
        text(json, "email_secondary", contact.emailSecondary());
        methods(json, "phones", contact.phones());
        methods(json, "emails", contact.emails());
        text(json, "company", contact.company());
        text(json, "job_title", contact.jobTitle());
        text(json, "birthday", contact.birthday());
        text(json, "website", contact.website());
        text(json, "category", contact.category());
        text(json, "address", contact.address());
        text(json, "city", contact.city());
        text(json, "district", contact.district());
        text(json, "postal_code", contact.postalCode());
        text(json, "country", contact.country());
        text(json, "notes", contact.notes());
        text(json, "photo_base64", contact.photo() == null ? "" : Base64.getEncoder().encodeToString(contact.photo()));
        json.append(",\"favorite\":").append(contact.favorite());
        json.append('}');
    }

    private void methods(StringBuilder json, String key, List<ContactMethod> methods) {
        json.append(",\"").append(key).append("\":[");
        for (int index = 0; index < methods.size(); index++) {
            ContactMethod method = methods.get(index);
            if (index > 0) json.append(',');
            json.append('{');
            json.append("\"kind\":\"").append(escape(method.kind())).append("\"");
            json.append(",\"label\":\"").append(escape(method.label())).append("\"");
            json.append(",\"value\":\"").append(escape(method.value())).append("\"");
            json.append(",\"primary\":").append(method.primary());
            json.append(",\"position\":").append(method.position());
            json.append('}');
        }
        json.append(']');
    }

    private void number(StringBuilder json, String key, long value) { json.append('\"').append(key).append("\":").append(value); }
    private void text(StringBuilder json, String key, String value) { json.append(",\"").append(key).append("\":\"").append(escape(value)).append('\"'); }
    private String escape(String value) {
        String safe = value == null ? "" : value;
        return safe.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
