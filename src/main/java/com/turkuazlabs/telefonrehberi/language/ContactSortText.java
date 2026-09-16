// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/language/ContactSortText.java
// # 📌 Amac: Kisi siralama enum degerlerini kullaniciya sistem diline uygun metinlerle gosterir.
// # 📌 Language - Java
// Version: 1.1.0
// Aciklama: ContactSort teknik degerlerini Turkce locale icin Turkce, diger diller icin Ingilizce fallback etiketlerine cevirir.
// Bagimli Oldugu Katman: Language | Model
package com.turkuazlabs.telefonrehberi.language;

import com.turkuazlabs.telefonrehberi.models.ContactSort;

public final class ContactSortText {
    private ContactSortText() {
    }

    public static String display(ContactSort sort) {
        ContactSort safeSort = sort == null ? ContactSort.defaultSort() : sort;
        return switch (safeSort) {
            case FAVORITES_FIRST -> LocaleText.text(Messages.SORT_FAVORITES_FIRST, "Favorites First");
            case NAME_ASC -> LocaleText.text(Messages.SORT_NAME_ASC, "Name A-Z");
            case NAME_DESC -> LocaleText.text(Messages.SORT_NAME_DESC, "Name Z-A");
            case COMPANY -> LocaleText.text(Messages.SORT_COMPANY, "Company A-Z");
            case CITY -> LocaleText.text(Messages.SORT_CITY, "City A-Z");
            case UPDATED_DESC -> LocaleText.text(Messages.SORT_UPDATED_DESC, "Recently Updated");
        };
    }
}
