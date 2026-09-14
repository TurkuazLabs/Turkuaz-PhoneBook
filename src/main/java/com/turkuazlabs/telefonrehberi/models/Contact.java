// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/Contact.java
// # 📌 Amac: Telefon rehberindeki tam kisi kaydini temsil eder.
// # 📌 Model - Java
// # Version: 2.37.0
// # Aciklama: Kisi alanlari, iletisim, hatirlatma ve platformlar arasi kalici sync UUID kimligini immutable kayitta tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

import java.util.List;

public record Contact(
        long id,
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
        String lastContactedDate,
        String syncUuid
) {
    public Contact {
        phones = phones == null ? List.of() : List.copyOf(phones);
        emails = emails == null ? List.of() : List.copyOf(emails);
        photo = photo == null ? null : photo.clone();
        birthdayReminderLeadTime = birthdayReminderLeadTime == null ? ReminderLeadTime.DISABLED : birthdayReminderLeadTime;
        importantDates = importantDates == null ? List.of() : List.copyOf(importantDates);
        keepInTouchInterval = keepInTouchInterval == null ? KeepInTouchInterval.DISABLED : keepInTouchInterval;
        lastContactedDate = lastContactedDate == null ? "" : lastContactedDate;
        syncUuid = syncUuid == null ? "" : syncUuid.trim();
    }

    public Contact(
            long id,
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
        this(id, name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle, birthday, website,
                category, address, city, district, postalCode, country, notes, favorite, phones, emails, photo,
                birthdayReminderLeadTime, importantDates, keepInTouchInterval, lastContactedDate, "");
    }

    public Contact(
            long id,
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
        this(id, name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle, birthday,
                website, category, address, city, district, postalCode, country, notes, favorite, phones, emails, photo,
                ReminderLeadTime.DISABLED, List.of(), KeepInTouchInterval.DISABLED, "", "");
    }

    public Contact(
            long id, String name, String phone, String phoneSecondary, String phoneWork, String email,
            String emailSecondary, String company, String jobTitle, String birthday, String website,
            String category, String address, String city, String district, String postalCode, String country,
            String notes, boolean favorite
    ) {
        this(id, name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle, birthday,
                website, category, address, city, district, postalCode, country, notes, favorite,
                new ContactDraft(name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle,
                        birthday, website, category, address, city, district, postalCode, country, notes, favorite).phones(),
                new ContactDraft(name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle,
                        birthday, website, category, address, city, district, postalCode, country, notes, favorite).emails(),
                null, ReminderLeadTime.DISABLED, List.of(), KeepInTouchInterval.DISABLED, "", "");
    }

    public ContactDraft toDraft() {
        return new ContactDraft(
                name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle, birthday,
                website, category, address, city, district, postalCode, country, notes, favorite, phones, emails, photo,
                birthdayReminderLeadTime, importantDates, keepInTouchInterval, lastContactedDate
        );
    }
}
