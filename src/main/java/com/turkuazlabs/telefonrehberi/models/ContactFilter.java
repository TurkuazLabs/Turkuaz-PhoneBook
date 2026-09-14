// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactFilter.java
// # 📌 Amac: Kisi arama ve filtreleme secimlerini tek immutable modelde tasir.
// # 📌 Model - Java
// # Version: 1.1.0
// # Aciklama: Metin aramasi, yapisal filtreler ve kisi listesi siralama secimini temsil eder.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record ContactFilter(
        String query,
        boolean favoritesOnly,
        String company,
        String city,
        String category,
        Long groupId,
        Long tagId,
        ContactSort sort
) {
    public ContactFilter {
        query = safe(query);
        company = safe(company);
        city = safe(city);
        category = safe(category);
        sort = sort == null ? ContactSort.defaultSort() : sort;
    }

    public boolean hasStructuredFilters() {
        return favoritesOnly
                || !company.isBlank()
                || !city.isBlank()
                || !category.isBlank()
                || groupId != null
                || tagId != null;
    }

    public int activeStructuredFilterCount() {
        int count = favoritesOnly ? 1 : 0;
        if (!company.isBlank()) count++;
        if (!city.isBlank()) count++;
        if (!category.isBlank()) count++;
        if (groupId != null) count++;
        if (tagId != null) count++;
        return count;
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
