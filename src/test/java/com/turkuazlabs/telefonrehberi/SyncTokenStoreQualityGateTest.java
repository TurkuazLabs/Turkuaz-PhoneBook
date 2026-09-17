// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/test/java/com/turkuazlabs/telefonrehberi/SyncTokenStoreQualityGateTest.java
// # 📌 Amac: Sync token hedef dizini, idempotency, gecici dosya temizligi ve POSIX izinlarini regresyon testine alir.
// # 📌 Tool - Java Test
// Version: 1.0.0
// # Aciklama: Temiz profilde nested data klasorunun otomatik olusmasini ve POSIX sistemlerde token dosyasinin 0600 kalmasini dogrular.
// # Bagimli Oldugu Katman: Tool | Config | Language
package com.turkuazlabs.telefonrehberi;

import com.turkuazlabs.telefonrehberi.tools.SyncTokenStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

public final class SyncTokenStoreQualityGateTest {
    private static final Set<PosixFilePermission> OWNER_ONLY = EnumSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE
    );

    private SyncTokenStoreQualityGateTest() {
    }

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("telefonrehberi-sync-token-");
        try {
            Path tokenFile = root.resolve("nested").resolve("data").resolve("sync-token.txt");
            SyncTokenStore store = new SyncTokenStore(tokenFile, 32);

            check(Files.notExists(tokenFile.getParent()), "Test baslangicinda token data klasoru zaten var.");
            String first = store.getOrCreate();

            check(Files.isDirectory(tokenFile.getParent()), "Sync token hedef parent klasoru otomatik olusturulmadi.");
            check(Files.isRegularFile(tokenFile), "Sync token dosyasi olusturulmadi.");
            check(first.length() == 64, "32 byte sync token 64 hex karakter olmadi.");
            check(first.matches("[0-9a-f]{64}"), "Sync token lowercase hex formatinda degil.");
            check(first.equals(Files.readString(tokenFile).trim()), "Diskteki sync token uretilen token ile uyusmuyor.");

            String second = store.getOrCreate();
            check(first.equals(second), "Sync token ikinci okumada degisti.");

            try (var files = Files.list(tokenFile.getParent())) {
                boolean partialExists = files.anyMatch(path -> path.getFileName().toString().contains(".partial-"));
                check(!partialExists, "Sync token olusturma sonrasi gecici partial dosya kaldi.");
            }

            if (Files.getFileAttributeView(tokenFile, PosixFileAttributeView.class) != null) {
                Files.setPosixFilePermissions(tokenFile, EnumSet.of(
                        PosixFilePermission.OWNER_READ,
                        PosixFilePermission.OWNER_WRITE,
                        PosixFilePermission.GROUP_READ,
                        PosixFilePermission.OTHERS_READ
                ));
                check(first.equals(store.getOrCreate()), "Izin sikilastirma mevcut tokeni degistirdi.");
                check(OWNER_ONLY.equals(Files.getPosixFilePermissions(tokenFile)), "POSIX sync token izinleri 0600 degil.");
            }

            System.out.println("SYNC_TOKEN_QUALITY_GATE_OK");
        } finally {
            deleteRecursively(root);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static void deleteRecursively(Path root) throws Exception {
        if (Files.notExists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
