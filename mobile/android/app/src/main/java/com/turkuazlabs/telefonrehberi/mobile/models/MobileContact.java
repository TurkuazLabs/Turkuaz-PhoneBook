// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/models/MobileContact.java
// # 📌 Amac: Android ve masaustu arasinda tasinan genisletilmis kisi kaydini temsil eder.
// # 📌 Model - Java
// # Version: 2.37.0
// # Aciklama: Native contact ID ile platformlar arasi kalici sync UUID kimligini birlikte tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.mobile.models;

import java.util.ArrayList;
import java.util.List;

public record MobileContact(
        String externalId,
        String syncUuid,
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
        List<MobileContactMethod> phones,
        List<MobileContactMethod> emails,
        String photoBase64
) {
    public MobileContact {
        externalId = externalId == null ? "" : externalId;
        syncUuid = syncUuid == null ? "" : syncUuid;
        phones = phones == null ? List.of() : List.copyOf(phones);
        emails = emails == null ? List.of() : List.copyOf(emails);
        photoBase64 = photoBase64 == null ? "" : photoBase64;
    }

    public MobileContact(
            String externalId, String name, String phone, String phoneSecondary, String phoneWork,
            String email, String emailSecondary, String company, String jobTitle, String birthday,
            String website, String category, String address, String city, String district, String postalCode,
            String country, String notes, boolean favorite
    ) {
        this(externalId, "", name, phone, phoneSecondary, phoneWork, email, emailSecondary, company, jobTitle,
                birthday, website, category, address, city, district, postalCode, country, notes, favorite,
                legacyPhones(phone, phoneSecondary, phoneWork), legacyEmails(email, emailSecondary), "");
    }

    private static List<MobileContactMethod> legacyPhones(String first, String second, String work) {
        List<MobileContactMethod> out = new ArrayList<>();
        add(out, MobileContactMethod.PHONE, MobileContactMethod.LABEL_MOBILE, first, true);
        add(out, MobileContactMethod.PHONE, MobileContactMethod.LABEL_OTHER, second, out.isEmpty());
        add(out, MobileContactMethod.PHONE, MobileContactMethod.LABEL_WORK, work, out.isEmpty());
        return List.copyOf(out);
    }

    private static List<MobileContactMethod> legacyEmails(String first, String second) {
        List<MobileContactMethod> out = new ArrayList<>();
        add(out, MobileContactMethod.EMAIL, MobileContactMethod.LABEL_EMAIL, first, true);
        add(out, MobileContactMethod.EMAIL, MobileContactMethod.LABEL_WORK, second, out.isEmpty());
        return List.copyOf(out);
    }

    private static void add(List<MobileContactMethod> out, String kind, String label, String value, boolean primary) {
        if (value != null && !value.isBlank()) out.add(new MobileContactMethod(kind, label, value, primary, out.size()));
    }
}
