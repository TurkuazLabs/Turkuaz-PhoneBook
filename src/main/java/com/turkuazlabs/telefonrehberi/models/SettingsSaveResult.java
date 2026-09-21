// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/SettingsSaveResult.java
// # 📌 Amac: Ayar kaydetme sonucunda runtime dil yenileme ve yeniden baslatma gereksinimini tasir.
// # 📌 Model - Java
// # Version: 1.2.0
// # Aciklama: Dil degisikligini canli UI yenilemeye, mobil senkron degisikligini ise restart uyarimina ayirir.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public record SettingsSaveResult(boolean languageChanged, boolean restartRequired) {
}
