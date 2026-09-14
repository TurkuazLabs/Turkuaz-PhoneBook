// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/ContactActivityFilter.java
// # 📌 Amac: Kisi profilindeki aktivite zaman cizelgesi filtre tiplerini tanimlar.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Tum kayitlar, degisiklikler, aktarimlar ve geri yukleme hareketlerini magic string kullanmadan tasir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public enum ContactActivityFilter {
    ALL,
    CHANGES,
    TRANSFER,
    RESTORE
}
