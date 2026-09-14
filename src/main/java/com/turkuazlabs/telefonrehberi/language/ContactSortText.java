// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/ContactSortText.java
// # 📌 Amac: Kisi siralama enum degerlerini kullaniciya gosterilecek metinlere cevirir.
// # 📌 Language - Java
// # Version: 1.0.0
// # Aciklama: ContactSort teknik degerlerinin arayuzde okunur Turkce adlarla gosterilmesini saglar.
// # Bagimli Oldugu Katman: Language | Model
package com.turkuazlabs.telefonrehberi.language;

import com.turkuazlabs.telefonrehberi.models.ContactSort;

public final class ContactSortText {
    private ContactSortText() {
    }

    public static String display(ContactSort sort) {
        ContactSort safeSort = sort == null ? ContactSort.defaultSort() : sort;
        return switch (safeSort) {
            case FAVORITES_FIRST -> Messages.SORT_FAVORITES_FIRST;
            case NAME_ASC -> Messages.SORT_NAME_ASC;
            case NAME_DESC -> Messages.SORT_NAME_DESC;
            case COMPANY -> Messages.SORT_COMPANY;
            case CITY -> Messages.SORT_CITY;
            case UPDATED_DESC -> Messages.SORT_UPDATED_DESC;
        };
    }
}
