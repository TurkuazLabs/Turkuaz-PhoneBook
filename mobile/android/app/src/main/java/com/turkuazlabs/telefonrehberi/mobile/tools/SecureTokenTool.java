// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/tools/SecureTokenTool.java
// # 📌 Amac: Mobil senkron tokenini Android Keystore anahtariyla sifreli saklar.
// # 📌 Tool - Java
// # Version: 1.0.1
// # Aciklama: AES/GCM anahtarini AndroidKeyStore icinde tutar; SharedPreferences'e yalniz sifreli token ve IV yazar.
// # Bagimli Oldugu Katman: Tool | Config
package com.turkuazlabs.telefonrehberi.mobile.tools;

import com.turkuazlabs.telefonrehberi.mobile.language.Messages;

import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.turkuazlabs.telefonrehberi.mobile.config.MobileConfig;

import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public final class SecureTokenTool {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;

    public void save(SharedPreferences preferences, String token) {
        String safe = token == null ? "" : token.trim();
        if (safe.isEmpty()) {
            preferences.edit()
                    .remove(MobileConfig.PREF_TOKEN_ENCRYPTED)
                    .remove(MobileConfig.PREF_TOKEN_IV)
                    .remove(MobileConfig.PREF_TOKEN_LEGACY)
                    .apply();
            return;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key());
            byte[] encrypted = cipher.doFinal(safe.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            preferences.edit()
                    .putString(MobileConfig.PREF_TOKEN_ENCRYPTED, Base64.encodeToString(encrypted, Base64.NO_WRAP))
                    .putString(MobileConfig.PREF_TOKEN_IV, Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP))
                    .remove(MobileConfig.PREF_TOKEN_LEGACY)
                    .apply();
        } catch (Exception exception) {
            throw new IllegalStateException(Messages.SECURE_TOKEN_STORE_FAILED, exception);
        }
    }

    public String load(SharedPreferences preferences) {
        String encrypted = preferences.getString(MobileConfig.PREF_TOKEN_ENCRYPTED, "");
        String iv = preferences.getString(MobileConfig.PREF_TOKEN_IV, "");
        if (encrypted != null && !encrypted.isBlank() && iv != null && !iv.isBlank()) {
            try {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(GCM_TAG_BITS, Base64.decode(iv, Base64.NO_WRAP)));
                byte[] plain = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP));
                return new String(plain, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception exception) {
                preferences.edit().remove(MobileConfig.PREF_TOKEN_ENCRYPTED).remove(MobileConfig.PREF_TOKEN_IV).apply();
                return "";
            }
        }
        String legacy = preferences.getString(MobileConfig.PREF_TOKEN_LEGACY, "");
        if (legacy != null && !legacy.isBlank()) {
            save(preferences, legacy);
            return legacy;
        }
        return "";
    }

    private SecretKey key() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        java.security.Key existing = keyStore.getKey(MobileConfig.KEYSTORE_TOKEN_ALIAS, null);
        if (existing instanceof SecretKey secretKey) return secretKey;
        KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        generator.init(new KeyGenParameterSpec.Builder(
                MobileConfig.KEYSTORE_TOKEN_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build());
        return generator.generateKey();
    }
}
