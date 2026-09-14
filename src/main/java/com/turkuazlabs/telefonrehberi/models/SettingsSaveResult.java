// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/SettingsSaveResult.java
// # 📌 Amac: Ayar kaydetme sonucunda yeniden baslatma gereksinimini View katmanina tasir.
// # 📌 Model - Java
// # Version: 1.0.0
// # Aciklama: Runtime senkron ayari degistiginde kullaniciya yeniden baslatma bilgisi verilmesini saglar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record SettingsSaveResult(boolean restartRequired) {
}
