// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/models/PullResult.java
// # 📌 Amac: PC'den telefona senkron sonucunu tasir.
// # 📌 Model - Java
// # Version: 1.1.0
// # Aciklama: Eklenen ve zaten mevcut oldugu icin atlanan kisi sayilarini immutable olarak tutar.
// # Bagimli Oldugu Katman: Model
package com.turkuazlabs.telefonrehberi.mobile.models;

public final class PullResult {
    private final int added;
    private final int skipped;

    public PullResult(int added, int skipped) {
        this.added = added;
        this.skipped = skipped;
    }

    public int added() { return added; }
    public int skipped() { return skipped; }
}
