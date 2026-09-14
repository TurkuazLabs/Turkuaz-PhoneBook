// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/SavedContactViewService.java
// # 📌 Amac: Kaydedilmis kisi gorunumlerinin olusturma, guncelleme, yeniden adlandirma, kopyalama, varsayilan secimi ve silme is kurallarini yonetir.
// # 📌 Service - Java
// # Version: 1.1.0
// # Aciklama: Gorunum adlarini dogrular, isim cakismalarini engeller ve tek varsayilan gorunum kuralini Repository katmaninda kalici tutar.
// # Bagimli Oldugu Katman: Service | Repository | Model | Language
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.ContactFilter;
import com.turkuazlabs.telefonrehberi.models.SavedContactView;
import com.turkuazlabs.telefonrehberi.repositories.SavedContactViewRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class SavedContactViewService {
    private static final int MAX_NAME_LENGTH = 60;

    private final SavedContactViewRepository repository;

    public SavedContactViewService(SavedContactViewRepository repository) {
        this.repository = repository;
    }

    public List<SavedContactView> list() {
        return repository.findAll();
    }

    public Optional<SavedContactView> defaultView() {
        return repository.findAll().stream().filter(SavedContactView::defaultView).findFirst();
    }

    public SavedContactView save(String name, ContactFilter filter) {
        String safeName = validateName(name);
        SavedContactView existing = findByNameOrNull(safeName);
        String id = existing == null ? UUID.randomUUID().toString() : existing.id();
        boolean defaultView = existing != null && existing.defaultView();
        SavedContactView saved = new SavedContactView(id, safeName, filter, defaultView);
        repository.save(saved);
        return saved;
    }

    public SavedContactView rename(String currentName, String newName) {
        SavedContactView current = requireByName(currentName);
        String safeNewName = validateName(newName);
        SavedContactView collision = findByNameOrNull(safeNewName);
        if (collision != null && !collision.id().equals(current.id())) {
            throw new IllegalArgumentException(Messages.ERROR_SAVED_VIEW_NAME_EXISTS);
        }
        SavedContactView renamed = current.withName(safeNewName);
        repository.save(renamed);
        return renamed;
    }

    public SavedContactView copy(String sourceName, String newName) {
        SavedContactView source = requireByName(sourceName);
        String safeNewName = validateName(newName);
        if (findByNameOrNull(safeNewName) != null) {
            throw new IllegalArgumentException(Messages.ERROR_SAVED_VIEW_NAME_EXISTS);
        }
        SavedContactView copy = new SavedContactView(UUID.randomUUID().toString(), safeNewName, source.filter(), false);
        repository.save(copy);
        return copy;
    }

    public SavedContactView setDefault(String name) {
        SavedContactView target = requireByName(name);
        for (SavedContactView view : repository.findAll()) {
            boolean shouldBeDefault = view.id().equals(target.id());
            if (view.defaultView() != shouldBeDefault) {
                repository.save(view.withDefaultView(shouldBeDefault));
            }
        }
        return requireByName(target.name());
    }

    public void clearDefault() {
        for (SavedContactView view : repository.findAll()) {
            if (view.defaultView()) {
                repository.save(view.withDefaultView(false));
            }
        }
    }

    public SavedContactView requireByName(String name) {
        String safeName = validateName(name);
        SavedContactView found = findByNameOrNull(safeName);
        if (found == null) {
            throw new IllegalArgumentException(Messages.ERROR_SAVED_VIEW_NOT_FOUND);
        }
        return found;
    }

    public void deleteByName(String name) {
        SavedContactView found = requireByName(name);
        repository.delete(found.id());
    }

    private SavedContactView findByNameOrNull(String name) {
        return repository.findAll().stream()
                .filter(view -> view.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private String validateName(String value) {
        String name = value == null ? "" : value.trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException(Messages.ERROR_SAVED_VIEW_NAME_REQUIRED);
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(String.format(Messages.ERROR_SAVED_VIEW_NAME_TOO_LONG, MAX_NAME_LENGTH));
        }
        return name;
    }
}
