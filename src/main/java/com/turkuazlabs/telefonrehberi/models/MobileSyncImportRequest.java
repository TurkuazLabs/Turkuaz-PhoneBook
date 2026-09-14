// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/MobileSyncImportRequest.java
// # 📌 Amac: Mobil import isteginin parse edilmis tipli verisini Service katmanina tasir.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: ContactDraft ile cihaz, native kisi ve kalici sync UUID kimliklerini tek request modelinde tutar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record MobileSyncImportRequest(
        ContactDraft draft,
        String deviceId,
        String externalId,
        String syncUuid
) { }
