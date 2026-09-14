// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/services/MobileSyncService.java
// # 📌 Amac: Mobil cihazlarla rehber senkronizasyonu icin servis katmani is akisini yonetir.
// # 📌 Service - Java
// # Version: 2.37.2
// # Aciklama: Kalici sync UUID kimligiyle idempotent mobil import, token ve LAN durum bilgisini yonetir.
// # Bagimli Oldugu Katman: Service | Model | Tool | Config
package com.turkuazlabs.telefonrehberi.services;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.MobileSyncImportRequest;
import com.turkuazlabs.telefonrehberi.models.SyncServerInfo;
import com.turkuazlabs.telefonrehberi.tools.LanAddressTool;
import com.turkuazlabs.telefonrehberi.tools.SyncTokenStore;

import java.util.List;

public final class MobileSyncService {
    private final ContactService contactService;
    private final SyncTokenStore tokenStore;
    private final LanAddressTool lanAddressTool;

    public MobileSyncService(ContactService contactService, SyncTokenStore tokenStore, LanAddressTool lanAddressTool) {
        this.contactService = contactService;
        this.tokenStore = tokenStore;
        this.lanAddressTool = lanAddressTool;
    }

    public String token() { return tokenStore.getOrCreate(); }

    public SyncServerInfo serverInfo(boolean running, int port) {
        return new SyncServerInfo(running, lanAddressTool.findLanAddress(), port, token());
    }

    public List<Contact> listContacts() { return contactService.listContacts(); }

    public Contact importContact(MobileSyncImportRequest request) {
        if (request == null) throw new IllegalArgumentException(Messages.ERROR_SYNC_EMPTY_REQUEST);
        return contactService.importFromMobile(
                request.draft(), request.deviceId(), request.externalId(), request.syncUuid()
        );
    }
}
