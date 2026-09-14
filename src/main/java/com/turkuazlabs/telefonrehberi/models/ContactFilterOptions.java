// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactFilterOptions.java
// # 📌 Amac: Kisi filtre panelinde gosterilecek dinamik secenek listelerini tasir.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Firma, sehir, kategori, grup ve etiket filtre seceneklerini immutable listeler halinde sunar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

import java.util.List;

public record ContactFilterOptions(
        List<String> companies,
        List<String> cities,
        List<String> categories,
        List<ContactFilterOption> groups,
        List<ContactFilterOption> tags
) {
    public ContactFilterOptions {
        companies = companies == null ? List.of() : List.copyOf(companies);
        cities = cities == null ? List.of() : List.copyOf(cities);
        categories = categories == null ? List.of() : List.copyOf(categories);
        groups = groups == null ? List.of() : List.copyOf(groups);
        tags = tags == null ? List.of() : List.copyOf(tags);
    }
}
