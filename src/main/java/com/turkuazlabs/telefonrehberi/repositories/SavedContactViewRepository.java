// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/repositories/SavedContactViewRepository.java
// # 📌 Amac: Kaydedilmis kisi gorunumlerini OS kullanici veri dizinindeki data/saved-views altinda YAML dosyalari olarak saklar.
// # 📌 Repository - Java
// # Version: 1.1.0
// # Aciklama: Her gorunumu ayri YAML dosyasinda saklar; varsayilan gorunum ve filtre metinlerini guvenli olarak kalici hale getirir.
// # Bagimli Oldugu Katman: Repository | Model | Config | Language
package com.turkuazlabs.telefonrehberi.repositories;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.ContactFilter;
import com.turkuazlabs.telefonrehberi.models.ContactSort;
import com.turkuazlabs.telefonrehberi.models.SavedContactView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class SavedContactViewRepository {
    private static final String FILE_EXTENSION = ".yml";
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name_b64";
    private static final String QUERY_KEY = "query_b64";
    private static final String FAVORITES_KEY = "favorites_only";
    private static final String COMPANY_KEY = "company_b64";
    private static final String CITY_KEY = "city_b64";
    private static final String CATEGORY_KEY = "category_b64";
    private static final String GROUP_ID_KEY = "group_id";
    private static final String TAG_ID_KEY = "tag_id";
    private static final String SORT_KEY = "sort";
    private static final String DEFAULT_VIEW_KEY = "default_view";

    private final SimpleYamlRepository yamlRepository = new SimpleYamlRepository();

    public List<SavedContactView> findAll() {
        Path directory = AppConfig.SAVED_VIEWS_PATH;
        if (Files.notExists(directory)) {
            return List.of();
        }
        try {
            List<SavedContactView> result = new ArrayList<>();
            try (var stream = Files.list(directory)) {
                stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(FILE_EXTENSION))
                        .forEach(path -> read(path).ifPresent(result::add));
            }
            return result.stream()
                    .sorted(Comparator.comparing(SavedContactView::name, String.CASE_INSENSITIVE_ORDER))
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SAVED_VIEW_READ, exception);
        }
    }

    public void save(SavedContactView view) {
        Path path = pathFor(view.id());
        ContactFilter filter = view.filter();
        List<String> lines = List.of(
                "# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/data/saved-views/" + view.id() + FILE_EXTENSION,
                "# 📌 Amac: Kaydedilmis kisi gorunumu filtre ve siralama durumunu saklar.",
                "# 📌 Repository - YAML",
                "# Version: 1.1.0",
                "# Aciklama: Turkuaz Telefon Rehberi kaydedilmis gorunum verisidir.",
                "# Bagimli Oldugu Katman: Repository | Model",
                "",
                line(ID_KEY, view.id()),
                line(NAME_KEY, encode(view.name())),
                line(QUERY_KEY, encode(filter.query())),
                line(FAVORITES_KEY, Boolean.toString(filter.favoritesOnly())),
                line(COMPANY_KEY, encode(filter.company())),
                line(CITY_KEY, encode(filter.city())),
                line(CATEGORY_KEY, encode(filter.category())),
                line(GROUP_ID_KEY, nullableLong(filter.groupId())),
                line(TAG_ID_KEY, nullableLong(filter.tagId())),
                line(SORT_KEY, filter.sort().name()),
                line(DEFAULT_VIEW_KEY, Boolean.toString(view.defaultView()))
        );
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, lines, AppConfig.DATA_CHARSET);
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SAVED_VIEW_WRITE, exception);
        }
    }

    public void delete(String id) {
        try {
            Files.deleteIfExists(pathFor(id));
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SAVED_VIEW_WRITE, exception);
        }
    }

    private java.util.Optional<SavedContactView> read(Path path) {
        Map<String, String> values = yamlRepository.read(path);
        String id = values.getOrDefault(ID_KEY, "").trim();
        String name = decode(values.get(NAME_KEY));
        if (id.isBlank() || name.isBlank()) {
            return java.util.Optional.empty();
        }
        ContactFilter filter = new ContactFilter(
                decode(values.get(QUERY_KEY)),
                Boolean.parseBoolean(values.getOrDefault(FAVORITES_KEY, "false")),
                decode(values.get(COMPANY_KEY)),
                decode(values.get(CITY_KEY)),
                decode(values.get(CATEGORY_KEY)),
                parseLong(values.get(GROUP_ID_KEY)),
                parseLong(values.get(TAG_ID_KEY)),
                parseSort(values.get(SORT_KEY))
        );
        boolean defaultView = Boolean.parseBoolean(values.getOrDefault(DEFAULT_VIEW_KEY, "false"));
        return java.util.Optional.of(new SavedContactView(id, name, filter, defaultView));
    }

    private Path pathFor(String id) {
        String safeId = id == null ? "" : id.replaceAll("[^a-zA-Z0-9-]", "");
        if (safeId.isBlank()) {
            throw new IllegalArgumentException(Messages.ERROR_SAVED_VIEW_INVALID);
        }
        return AppConfig.SAVED_VIEWS_PATH.resolve(safeId + FILE_EXTENSION);
    }

    private String line(String key, String value) {
        return key + ": \"" + (value == null ? "" : value.replace("\"", "")) + "\"";
    }

    private String encode(String value) {
        String safe = value == null ? "" : value;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(safe.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        try {
            return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    private String nullableLong(Long value) {
        return value == null ? "" : Long.toString(value);
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private ContactSort parseSort(String value) {
        if (value == null || value.isBlank()) {
            return ContactSort.defaultSort();
        }
        try {
            return ContactSort.valueOf(value.trim());
        } catch (IllegalArgumentException exception) {
            return ContactSort.defaultSort();
        }
    }
}
