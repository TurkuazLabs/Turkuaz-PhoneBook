// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/services/SyncService.java
// # 📌 Amac: Android ile masaustu arasindaki iki yonlu rehber senkronizasyonu is kurallarini yonetir.
// # 📌 Service - Java
// Version: 2.37.1
// # Aciklama: Kalici sync UUID mappingi, guvenli LAN/HTTPS endpoint dogrulamasi, duplicate-safe pull ve idempotent push akisini uygular.
// # Bagimli Oldugu Katman: Service | Repository | Tool | Model | Language
package com.turkuazlabs.telefonrehberi.mobile.services;

import com.turkuazlabs.telefonrehberi.mobile.language.Messages;
import com.turkuazlabs.telefonrehberi.mobile.models.MobileContact;
import com.turkuazlabs.telefonrehberi.mobile.models.PullResult;
import com.turkuazlabs.telefonrehberi.mobile.repositories.DeviceContactRepository;
import com.turkuazlabs.telefonrehberi.mobile.repositories.SettingsRepository;
import com.turkuazlabs.telefonrehberi.mobile.tools.DesktopApiTool;
import com.turkuazlabs.telefonrehberi.mobile.tools.LanEndpointTool;

import java.util.List;

public final class SyncService {
    private final DeviceContactRepository contactsRepository;
    private final SettingsRepository settingsRepository;
    private final DesktopApiTool desktopApiTool;
    private final LanEndpointTool lanEndpointTool;

    public SyncService(
            DeviceContactRepository contactsRepository,
            SettingsRepository settingsRepository,
            DesktopApiTool desktopApiTool,
            LanEndpointTool lanEndpointTool
    ) {
        this.contactsRepository = contactsRepository;
        this.settingsRepository = settingsRepository;
        this.desktopApiTool = desktopApiTool;
        this.lanEndpointTool = lanEndpointTool;
    }

    public void saveConnection(String serverUrl, String token) {
        String validatedServerUrl = lanEndpointTool.validateBaseUrl(serverUrl);
        if (token == null || token.trim().isEmpty()) throw new IllegalArgumentException(Messages.TOKEN_REQUIRED);
        settingsRepository.saveConnection(validatedServerUrl, token);
    }

    public String serverUrl() { return settingsRepository.serverUrl(); }
    public String token() { return settingsRepository.token(); }

    public void testConnection() {
        requireSavedConnection();
        desktopApiTool.test(settingsRepository.serverUrl(), settingsRepository.token());
    }

    public PullResult pullDesktopContacts() {
        requireSavedConnection();
        List<MobileContact> desktopContacts = desktopApiTool.fetchContacts(settingsRepository.serverUrl(), settingsRepository.token());
        int added = 0;
        int skipped = 0;
        for (MobileContact contact : desktopContacts) {
            String mappedNativeId = settingsRepository.nativeIdForSyncUuid(contact.syncUuid());
            DeviceContactRepository.EnsureResult result = contactsRepository.ensureDesktopContact(contact, mappedNativeId);
            if (!contact.syncUuid().isBlank()) settingsRepository.saveSyncMapping(result.contactId(), contact.syncUuid());
            if (result.created()) added++; else skipped++;
        }
        return new PullResult(added, skipped);
    }

    public int pushDeviceContacts() {
        requireSavedConnection();
        List<MobileContact> deviceContacts = contactsRepository.findAll();
        String deviceId = settingsRepository.deviceId();
        for (MobileContact contact : deviceContacts) {
            String currentSyncUuid = settingsRepository.syncUuidForNativeId(contact.externalId());
            String canonicalSyncUuid = desktopApiTool.pushContact(
                    settingsRepository.serverUrl(), settingsRepository.token(), deviceId, currentSyncUuid, contact
            );
            if (!canonicalSyncUuid.isBlank()) settingsRepository.saveSyncMapping(contact.externalId(), canonicalSyncUuid);
        }
        return deviceContacts.size();
    }

    private void requireSavedConnection() {
        saveConnection(settingsRepository.serverUrl(), settingsRepository.token());
    }
}
