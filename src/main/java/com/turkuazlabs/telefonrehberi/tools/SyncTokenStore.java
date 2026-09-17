// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/SyncTokenStore.java
// # 📌 Amac: Mobil senkron API erisim anahtarini guvenli rastgele olarak olusturur ve yerelde saklar.
// # 📌 Tool - Java
// Version: 1.3.0
// # Aciklama: Token hedefinin kendi parent dizinini hazirlar; test edilebilir hedef/token boyutu enjeksiyonu, POSIX 0600 izinleri ve race-safe yayinlama uygular.
// # Bagimli Oldugu Katman: Tool | Config | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.HexFormat;
import java.util.Set;

public final class SyncTokenStore {
    private static final Set<PosixFilePermission> OWNER_ONLY = EnumSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE
    );

    private final SecureRandom secureRandom;
    private final Path target;
    private final int tokenBytes;

    public SyncTokenStore() {
        this(AppConfig.SYNC_TOKEN_FILE, AppConfig.SYNC_TOKEN_BYTES);
    }

    public SyncTokenStore(Path target, int tokenBytes) {
        if (target == null || target.getParent() == null) {
            throw new IllegalArgumentException("Sync token target path must have a parent directory.");
        }
        if (tokenBytes < 16) {
            throw new IllegalArgumentException("Sync token size must be at least 16 bytes.");
        }
        this.target = target.toAbsolutePath().normalize();
        this.tokenBytes = tokenBytes;
        this.secureRandom = new SecureRandom();
    }

    public synchronized String getOrCreate() {
        Path temporary = null;
        try {
            Files.createDirectories(target.getParent());
            boolean targetExisted = Files.exists(target);
            if (targetExisted) {
                String existing = readExistingToken(target);
                if (!existing.isBlank()) {
                    return existing;
                }
            }

            String candidate = createToken();
            temporary = createSecureTemporaryFile(target);
            Files.writeString(
                    temporary,
                    candidate,
                    AppConfig.DATA_CHARSET,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            try {
                publishTemporary(temporary, target, targetExisted);
                temporary = null;
                securePermissions(target);
                return candidate;
            } catch (FileAlreadyExistsException race) {
                String winner = readExistingToken(target);
                if (!winner.isBlank()) {
                    return winner;
                }
                throw new IOException("Sync token hedef dosyasi eszamanli olusturuldu ancak bos kaldi.", race);
            }
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_SYNC_TOKEN, exception);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException ignored) {
                    // Ana hata sonucunu golgeleme.
                }
            }
        }
    }

    private String readExistingToken(Path path) throws IOException {
        securePermissions(path);
        return Files.readString(path, AppConfig.DATA_CHARSET).trim();
    }

    private String createToken() {
        byte[] bytes = new byte[tokenBytes];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private Path createSecureTemporaryFile(Path target) throws IOException {
        Path parent = target.getParent();
        String prefix = target.getFileName().toString() + ".partial-";
        if (isPosix(parent)) {
            return Files.createTempFile(
                    parent,
                    prefix,
                    ".tmp",
                    PosixFilePermissions.asFileAttribute(OWNER_ONLY)
            );
        }
        return Files.createTempFile(parent, prefix, ".tmp");
    }

    private void publishTemporary(Path temporary, Path target, boolean replaceExisting) throws IOException {
        if (!replaceExisting) {
            Files.move(temporary, target);
            return;
        }
        try {
            Files.move(
                    temporary,
                    target,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException unsupported) {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private boolean isPosix(Path path) {
        return Files.getFileAttributeView(path, PosixFileAttributeView.class) != null;
    }

    private void securePermissions(Path path) throws IOException {
        if (isPosix(path)) {
            Files.setPosixFilePermissions(path, OWNER_ONLY);
        }
    }
}
