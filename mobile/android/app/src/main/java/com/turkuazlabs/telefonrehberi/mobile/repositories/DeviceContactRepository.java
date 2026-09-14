// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/repositories/DeviceContactRepository.java
// # 📌 Amac: Android sistem rehberini ContentProvider uzerinden genisletilmis alanlarla okur ve yazar.
// # 📌 Repository - Java
// # Version: 2.37.1
// # Aciklama: Sinirsiz iletisim alanlarini esler; natural identity fallback yalniz tekil aday bulursa mevcut kisiyle mapping kurar.
// # Bagimli Oldugu Katman: Repository | Model
package com.turkuazlabs.telefonrehberi.mobile.repositories;

import com.turkuazlabs.telefonrehberi.mobile.language.Messages;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;

import com.turkuazlabs.telefonrehberi.mobile.models.MobileContact;
import com.turkuazlabs.telefonrehberi.mobile.models.MobileContactMethod;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class DeviceContactRepository {
    private final ContentResolver resolver;

    public DeviceContactRepository(Context context) { this.resolver = context.getContentResolver(); }

    public List<MobileContact> findAll() {
        List<MobileContact> contacts = new ArrayList<>();
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.STARRED,
                ContactsContract.Contacts.PHOTO_URI
        };
        try (Cursor cursor = resolver.query(
                ContactsContract.Contacts.CONTENT_URI, projection, null, null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )) {
            if (cursor == null) return contacts;
            int idColumn = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
            int starredColumn = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.STARRED);
            int photoColumn = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
            while (cursor.moveToNext()) {
                String id = cursor.getString(idColumn);
                List<MobileContactMethod> phones = readMethods(id, true);
                List<MobileContactMethod> emails = readMethods(id, false);
                if (phones.isEmpty() && emails.isEmpty()) continue;
                WorkValues work = readWork(id);
                AddressValues address = readAddress(id);
                String primaryPhone = primaryValue(phones);
                String displayName = safe(cursor.getString(nameColumn));
                String photoUri = photoColumn >= 0 ? safe(cursor.getString(photoColumn)) : "";
                contacts.add(new MobileContact(
                        id, "",
                        displayName.isEmpty() ? (!primaryPhone.isEmpty() ? primaryPhone : primaryValue(emails)) : displayName,
                        valueAt(phones, 0), valueAt(phones, 1), firstLabel(phones, MobileContactMethod.LABEL_WORK),
                        valueAt(emails, 0), valueAt(emails, 1),
                        work.company, work.title,
                        readSingleData(id, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                                ContactsContract.CommonDataKinds.Event.START_DATE,
                                ContactsContract.CommonDataKinds.Event.TYPE + " = " + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY),
                        readSingleData(id, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                                ContactsContract.CommonDataKinds.Website.URL, null),
                        "", address.formatted, address.city, address.region, address.postalCode, address.country,
                        readSingleData(id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE,
                                ContactsContract.CommonDataKinds.Note.NOTE, null),
                        cursor.getInt(starredColumn) == 1,
                        phones, emails, readPhotoBase64(photoUri)
                ));
            }
        }
        return contacts;
    }

    public EnsureResult ensureDesktopContact(MobileContact contact, String preferredContactId) {
        String preferred = safe(preferredContactId);
        if (!preferred.isEmpty() && contactExists(preferred)) return new EnsureResult(preferred, false);
        String matching = findMatchingContactId(contact);
        if (!matching.isEmpty()) return new EnsureResult(matching, false);

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        addName(operations, contact.name());
        for (MobileContactMethod method : contact.phones()) addPhone(operations, method);
        for (MobileContactMethod method : contact.emails()) addEmail(operations, method);
        addOrganization(operations, contact.company(), contact.jobTitle());
        addAddress(operations, contact);
        addEvent(operations, contact.birthday());
        addWebsite(operations, contact.website());
        addNote(operations, contact.notes());
        addPhoto(operations, contact.photoBase64());
        try {
            ContentProviderResult[] results = resolver.applyBatch(ContactsContract.AUTHORITY, operations);
            if (results.length < 1 || results[0].uri == null) throw new IllegalStateException(Messages.CONTACT_ID_MISSING);
            long rawContactId = ContentUris.parseId(results[0].uri);
            String contactId = contactIdForRawContact(rawContactId);
            if (contactId.isEmpty()) throw new IllegalStateException(Messages.CONTACT_ID_UNRESOLVED);
            return new EnsureResult(contactId, true);
        } catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage(), exception);
        }
    }

    public boolean addIfMissing(MobileContact contact) {
        return ensureDesktopContact(contact, "").created();
    }

    private List<MobileContactMethod> readMethods(String contactId, boolean phone) {
        Uri uri = phone ? ContactsContract.CommonDataKinds.Phone.CONTENT_URI : ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String valueColumn = phone ? ContactsContract.CommonDataKinds.Phone.NUMBER : ContactsContract.CommonDataKinds.Email.ADDRESS;
        String typeColumn = phone ? ContactsContract.CommonDataKinds.Phone.TYPE : ContactsContract.CommonDataKinds.Email.TYPE;
        String labelColumn = phone ? ContactsContract.CommonDataKinds.Phone.LABEL : ContactsContract.CommonDataKinds.Email.LABEL;
        String contactColumn = phone ? ContactsContract.CommonDataKinds.Phone.CONTACT_ID : ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String[] projection = {valueColumn, typeColumn, labelColumn, ContactsContract.Data.IS_PRIMARY};
        List<MobileContactMethod> out = new ArrayList<>();
        try (Cursor cursor = resolver.query(uri, projection, contactColumn + " = ?", new String[]{contactId}, null)) {
            if (cursor == null) return out;
            int valueIndex = cursor.getColumnIndexOrThrow(valueColumn);
            int typeIndex = cursor.getColumnIndexOrThrow(typeColumn);
            int labelIndex = cursor.getColumnIndex(labelColumn);
            int primaryIndex = cursor.getColumnIndex(ContactsContract.Data.IS_PRIMARY);
            while (cursor.moveToNext()) {
                String value = safe(cursor.getString(valueIndex));
                if (value.isEmpty()) continue;
                int type = cursor.getInt(typeIndex);
                String custom = labelIndex >= 0 ? safe(cursor.getString(labelIndex)) : "";
                String label = custom.isEmpty() ? androidLabel(type, phone) : custom;
                boolean primary = primaryIndex >= 0 && cursor.getInt(primaryIndex) == 1;
                out.add(new MobileContactMethod(phone ? MobileContactMethod.PHONE : MobileContactMethod.EMAIL, label, value, primary, out.size()));
            }
        }
        if (!out.isEmpty() && out.stream().noneMatch(MobileContactMethod::primary)) {
            MobileContactMethod first = out.get(0);
            out.set(0, new MobileContactMethod(first.kind(), first.label(), first.value(), true, 0));
        }
        return List.copyOf(out);
    }

    private String androidLabel(int type, boolean phone) {
        if (phone) {
            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) return MobileContactMethod.LABEL_MOBILE;
            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_WORK || type == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE) return MobileContactMethod.LABEL_WORK;
            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_HOME) return MobileContactMethod.LABEL_HOME;
            return MobileContactMethod.LABEL_OTHER;
        }
        if (type == ContactsContract.CommonDataKinds.Email.TYPE_WORK) return MobileContactMethod.LABEL_WORK;
        if (type == ContactsContract.CommonDataKinds.Email.TYPE_HOME) return MobileContactMethod.LABEL_HOME;
        return MobileContactMethod.LABEL_EMAIL;
    }

    private void addPhone(ArrayList<ContentProviderOperation> operations, MobileContactMethod method) {
        if (safe(method.value()).isEmpty()) return;
        int type = phoneType(method.label());
        ContentProviderOperation.Builder builder = dataInsert()
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, method.value())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, type)
                .withValue(ContactsContract.Data.IS_PRIMARY, method.primary() ? 1 : 0);
        if (type == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) builder.withValue(ContactsContract.CommonDataKinds.Phone.LABEL, method.label());
        operations.add(builder.build());
    }

    private void addEmail(ArrayList<ContentProviderOperation> operations, MobileContactMethod method) {
        if (safe(method.value()).isEmpty()) return;
        int type = emailType(method.label());
        ContentProviderOperation.Builder builder = dataInsert()
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, method.value())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, type)
                .withValue(ContactsContract.Data.IS_PRIMARY, method.primary() ? 1 : 0);
        if (type == ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM) builder.withValue(ContactsContract.CommonDataKinds.Email.LABEL, method.label());
        operations.add(builder.build());
    }

    private int phoneType(String label) {
        String value = safe(label).toLowerCase();
        if (value.contains("cep") || value.contains("mobil")) return ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        if (value.equals("is") || value.contains("work")) return ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
        if (value.contains("ev") || value.contains("home")) return ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
        return ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM;
    }

    private int emailType(String label) {
        String value = safe(label).toLowerCase();
        if (value.equals("is") || value.contains("work")) return ContactsContract.CommonDataKinds.Email.TYPE_WORK;
        if (value.contains("ev") || value.contains("home")) return ContactsContract.CommonDataKinds.Email.TYPE_HOME;
        return ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM;
    }

    private WorkValues readWork(String contactId) {
        try (Cursor cursor = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Organization.COMPANY, ContactsContract.CommonDataKinds.Organization.TITLE},
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}, null
        )) {
            if (cursor != null && cursor.moveToFirst()) return new WorkValues(safe(cursor.getString(0)), safe(cursor.getString(1)));
        }
        return new WorkValues("", "");
    }

    private AddressValues readAddress(String contactId) {
        try (Cursor cursor = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                        ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                        ContactsContract.CommonDataKinds.StructuredPostal.REGION,
                        ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY},
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{contactId, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}, null
        )) {
            if (cursor != null && cursor.moveToFirst()) return new AddressValues(
                    safe(cursor.getString(0)), safe(cursor.getString(1)), safe(cursor.getString(2)),
                    safe(cursor.getString(3)), safe(cursor.getString(4)));
        }
        return new AddressValues("", "", "", "", "");
    }

    private String readSingleData(String contactId, String mimeType, String column, String extraSelection) {
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        if (extraSelection != null && !extraSelection.isBlank()) selection += " AND " + extraSelection;
        try (Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{column}, selection, new String[]{contactId, mimeType}, null)) {
            return cursor != null && cursor.moveToFirst() ? safe(cursor.getString(0)) : "";
        }
    }

    private String readPhotoBase64(String photoUri) {
        if (photoUri.isEmpty()) return "";
        try (InputStream input = resolver.openInputStream(Uri.parse(photoUri)); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (input == null) return "";
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) >= 0) output.write(buffer, 0, read);
            return Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP);
        } catch (Exception ignored) { return ""; }
    }

    private void addName(ArrayList<ContentProviderOperation> operations, String name) {
        operations.add(dataInsert().withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());
    }

    private void addOrganization(ArrayList<ContentProviderOperation> operations, String company, String title) {
        if (safe(company).isEmpty() && safe(title).isEmpty()) return;
        operations.add(dataInsert().withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, title).build());
    }

    private void addAddress(ArrayList<ContentProviderOperation> operations, MobileContact contact) {
        if (safe(contact.address()).isEmpty() && safe(contact.city()).isEmpty() && safe(contact.country()).isEmpty()) return;
        operations.add(dataInsert().withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, contact.address())
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, contact.city())
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, contact.district())
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, contact.postalCode())
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, contact.country()).build());
    }

    private void addEvent(ArrayList<ContentProviderOperation> operations, String birthday) {
        if (safe(birthday).isEmpty()) return;
        operations.add(dataInsert().withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, birthday)
                .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY).build());
    }

    private void addWebsite(ArrayList<ContentProviderOperation> operations, String website) {
        if (safe(website).isEmpty()) return;
        operations.add(dataInsert().withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Website.URL, website).build());
    }

    private void addNote(ArrayList<ContentProviderOperation> operations, String note) {
        if (safe(note).isEmpty()) return;
        operations.add(dataInsert().withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Note.NOTE, note).build());
    }

    private void addPhoto(ArrayList<ContentProviderOperation> operations, String photoBase64) {
        if (safe(photoBase64).isEmpty()) return;
        try {
            byte[] bytes = Base64.decode(photoBase64, Base64.DEFAULT);
            operations.add(dataInsert().withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, bytes).build());
        } catch (IllegalArgumentException ignored) { }
    }

    private ContentProviderOperation.Builder dataInsert() {
        return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
    }

    private boolean contactExists(String contactId) {
        try (Cursor cursor = resolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID},
                ContactsContract.Contacts._ID + " = ?", new String[]{contactId}, null
        )) {
            return cursor != null && cursor.moveToFirst();
        }
    }

    private String contactIdForRawContact(long rawContactId) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId);
        try (Cursor cursor = resolver.query(uri, new String[]{ContactsContract.RawContacts.CONTACT_ID}, null, null, null)) {
            return cursor != null && cursor.moveToFirst() ? safe(cursor.getString(0)) : "";
        }
    }

    private String findMatchingContactId(MobileContact contact) {
        java.util.LinkedHashSet<String> candidates = new java.util.LinkedHashSet<>();
        java.util.LinkedHashSet<String> phoneTargets = new java.util.LinkedHashSet<>();
        for (MobileContactMethod method : contact.phones()) {
            String value = normalizePhone(method.value());
            if (!value.isEmpty()) phoneTargets.add(value);
        }
        collectPhoneCandidates(phoneTargets, candidates);
        if (candidates.size() > 1) return "";

        java.util.LinkedHashSet<String> emailTargets = new java.util.LinkedHashSet<>();
        for (MobileContactMethod method : contact.emails()) {
            String value = safe(method.value()).toLowerCase(java.util.Locale.ROOT);
            if (!value.isEmpty()) emailTargets.add(value);
        }
        collectEmailCandidates(emailTargets, candidates);
        return candidates.size() == 1 ? candidates.iterator().next() : "";
    }

    private void collectPhoneCandidates(java.util.Set<String> targets, java.util.Set<String> candidates) {
        if (targets.isEmpty()) return;
        String[] projection = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER};
        try (Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null)) {
            if (cursor == null) return;
            int idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            int valueColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (cursor.moveToNext()) {
                if (targets.contains(normalizePhone(cursor.getString(valueColumn)))) {
                    String id = safe(cursor.getString(idColumn));
                    if (!id.isEmpty()) candidates.add(id);
                    if (candidates.size() > 1) return;
                }
            }
        }
    }

    private void collectEmailCandidates(java.util.Set<String> targets, java.util.Set<String> candidates) {
        if (targets.isEmpty()) return;
        String[] projection = {ContactsContract.CommonDataKinds.Email.CONTACT_ID, ContactsContract.CommonDataKinds.Email.ADDRESS};
        try (Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, null, null, null)) {
            if (cursor == null) return;
            int idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
            int valueColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS);
            while (cursor.moveToNext()) {
                String value = safe(cursor.getString(valueColumn)).toLowerCase(java.util.Locale.ROOT);
                if (targets.contains(value)) {
                    String id = safe(cursor.getString(idColumn));
                    if (!id.isEmpty()) candidates.add(id);
                    if (candidates.size() > 1) return;
                }
            }
        }
    }

    private String primaryValue(List<MobileContactMethod> values) {
        for (MobileContactMethod value : values) if (value.primary()) return value.value();
        return valueAt(values, 0);
    }
    private String valueAt(List<MobileContactMethod> values, int index) { return index < values.size() ? values.get(index).value() : ""; }
    private String firstLabel(List<MobileContactMethod> values, String label) { return values.stream().filter(v -> label.equalsIgnoreCase(v.label())).map(MobileContactMethod::value).findFirst().orElse(""); }
    private String normalizePhone(String phone) { return safe(phone).replaceAll("[^0-9+]", ""); }
    private String safe(String value) { return value == null ? "" : value.trim(); }

    private record WorkValues(String company, String title) { }
    public record EnsureResult(String contactId, boolean created) { }
    private record AddressValues(String formatted, String city, String region, String postalCode, String country) { }
}
