// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactDraft.java
// # 📌 Amac: Yeni veya guncellenecek kisi form verisini kimliksiz olarak tasir.
// # 📌 Model - Java
// # Version: 2.33.0
// # Aciklama: Legacy alanlar, sinirsiz iletisim satirlari, profil fotografi, onemli tarihler ve iletisimde kal ayarlarini tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

import java.util.ArrayList;
import java.util.List;

public record ContactDraft(
        String name,
        String phone,
        String phoneSecondary,
        String phoneWork,
        String email,
        String emailSecondary,
        String company,
        String jobTitle,
        String birthday,
        String website,
        String category,
        String address,
        String city,
        String district,
        String postalCode,
        String country,
        String notes,
        boolean favorite,
        List<ContactMethod> phones,
        List<ContactMethod> emails,
        byte[] photo,
        ReminderLeadTime birthdayReminderLeadTime,
        List<ImportantDate> importantDates,
        KeepInTouchInterval keepInTouchInterval,
        String lastContactedDate
) {
    public ContactDraft {
        phones = phones == null ? List.of() : List.copyOf(phones);
        emails = emails == null ? List.of() : List.copyOf(emails);
        photo = photo == null ? null : photo.clone();
        birthdayReminderLeadTime = birthdayReminderLeadTime == null ? ReminderLeadTime.DISABLED : birthdayReminderLeadTime;
        importantDates = importantDates == null ? List.of() : List.copyOf(importantDates);
        keepInTouchInterval = keepInTouchInterval == null ? KeepInTouchInterval.DISABLED : keepInTouchInterval;
        lastContactedDate = lastContactedDate == null ? "" : lastContactedDate;
    }

    public ContactDraft(
            String name,
            String phone,
            String phoneSecondary,
            String phoneWork,
            String email,
            String emailSecondary,
            String company,
            String jobTitle,
            String birthday,
            String website,
            String category,
            String address,
            String city,
            String district,
            String postalCode,
            String country,
            String notes,
            boolean favorite,
            List<ContactMethod> phones,
            List<ContactMethod> emails,
            byte[] photo
    ) {
        this(
                name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle, birthday,
                website, category, address, city, district, postalCode, country, notes, favorite, phones, emails, photo,
                ReminderLeadTime.DISABLED, List.of(), KeepInTouchInterval.DISABLED, ""
        );
    }

    public ContactDraft(
            String name,
            String phone,
            String phoneSecondary,
            String phoneWork,
            String email,
            String emailSecondary,
            String company,
            String jobTitle,
            String birthday,
            String website,
            String category,
            String address,
            String city,
            String district,
            String postalCode,
            String country,
            String notes,
            boolean favorite
    ) {
        this(
                name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle, birthday,
                website, category, address, city, district, postalCode, country, notes, favorite,
                legacyPhones(phone, phoneSecondary, phoneWork), legacyEmails(email, emailSecondary), null,
                ReminderLeadTime.DISABLED, List.of(), KeepInTouchInterval.DISABLED, ""
        );
    }

    public String primaryPhoneValue() { return firstValue(phones, phone); }
    public String primaryEmailValue() { return firstValue(emails, email); }

    public ContactDraft withFavorite(boolean value) {
        return copy(company, category, value);
    }

    public ContactDraft withCompany(String value) {
        return new ContactDraft(
                name, phone, phoneSecondary, phoneWork, email, emailSecondary, value, jobTitle, birthday, website,
                category, address, city, district, postalCode, country, notes, favorite, phones, emails, photo,
                birthdayReminderLeadTime, importantDates, keepInTouchInterval, lastContactedDate
        );
    }

    public ContactDraft withCategory(String value) {
        return new ContactDraft(
                name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle, birthday, website,
                value, address, city, district, postalCode, country, notes, favorite, phones, emails, photo,
                birthdayReminderLeadTime, importantDates, keepInTouchInterval, lastContactedDate
        );
    }

    private ContactDraft copy(String companyValue, String categoryValue, boolean favoriteValue) {
        return new ContactDraft(
                name, phone, phoneSecondary, phoneWork, email, emailSecondary, companyValue, jobTitle, birthday, website,
                categoryValue, address, city, district, postalCode, country, notes, favoriteValue, phones, emails, photo,
                birthdayReminderLeadTime, importantDates, keepInTouchInterval, lastContactedDate
        );
    }

    private static String firstValue(List<ContactMethod> methods, String fallback) {
        for (ContactMethod method : methods) if (method.primary() && !method.value().isBlank()) return method.value();
        for (ContactMethod method : methods) if (!method.value().isBlank()) return method.value();
        return fallback == null ? "" : fallback;
    }

    private static List<ContactMethod> legacyPhones(String primary, String secondary, String work) {
        List<ContactMethod> values = new ArrayList<>();
        add(values, ContactMethod.PHONE, ContactMethod.LABEL_MOBILE, primary, true);
        add(values, ContactMethod.PHONE, ContactMethod.LABEL_OTHER, secondary, values.isEmpty());
        add(values, ContactMethod.PHONE, ContactMethod.LABEL_WORK, work, values.isEmpty());
        return List.copyOf(values);
    }

    private static List<ContactMethod> legacyEmails(String primary, String secondary) {
        List<ContactMethod> values = new ArrayList<>();
        add(values, ContactMethod.EMAIL, ContactMethod.LABEL_EMAIL, primary, true);
        add(values, ContactMethod.EMAIL, ContactMethod.LABEL_WORK, secondary, values.isEmpty());
        return List.copyOf(values);
    }

    private static void add(List<ContactMethod> values, String kind, String label, String value, boolean primary) {
        if (value != null && !value.isBlank()) values.add(new ContactMethod(kind, label, value, primary, values.size()));
    }
}
