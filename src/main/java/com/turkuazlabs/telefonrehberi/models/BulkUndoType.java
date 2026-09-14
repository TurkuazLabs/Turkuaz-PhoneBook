// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/models/BulkUndoType.java
// # 📌 Amac: Toplu undo/redo gecmis kaydinin hangi islemi uygulayacagini tip guvenli tanimlar.
// # 📌 Model - Java
// # Version: 1.1.0
// # Aciklama: Kisi snapshot, grup/etiket uyeligi ve Cop Kutusu undo/redo turlerini merkezi enum olarak sunar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.models;

public enum BulkUndoType {
    CONTACT_SNAPSHOT,
    GROUP_ADD,
    GROUP_REMOVE,
    TAG_ADD,
    TAG_REMOVE,
    TRASH
}
