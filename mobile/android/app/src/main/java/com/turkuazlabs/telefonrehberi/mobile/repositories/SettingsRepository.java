// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/repositories/SettingsRepository.java
// # 📌 Amac: Android mobil senkron baglanti ayarlarini, guvenli tokeni ve sync UUID mappinglerini kalici saklar.
// # 📌 Repository - Java
// # Version: 2.37.1
// # Aciklama: Server URL ve cihaz UUID'sini SharedPreferences'te, tokeni Android Keystore ile sifreli; native ID <-> sync UUID mappinglerini cift yonlu tutar.
// # Bagimli Oldugu Katman: Repository | Config | Tool
package com.turkuazlabs.telefonrehberi.mobile.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.turkuazlabs.telefonrehberi.mobile.config.MobileConfig;
import com.turkuazlabs.telefonrehberi.mobile.tools.SecureTokenTool;

import java.util.UUID;

public final class SettingsRepository {
    private final SharedPreferences preferences;
    private final SecureTokenTool secureTokenTool;

    public SettingsRepository(Context context) {
        preferences = context.getSharedPreferences(MobileConfig.PREFERENCES_NAME, Context.MODE_PRIVATE);
        secureTokenTool = new SecureTokenTool();
    }

    public void saveConnection(String serverUrl, String token) {
        preferences.edit().putString(MobileConfig.PREF_SERVER_URL, normalizeUrl(serverUrl)).apply();
        secureTokenTool.save(preferences, token);
    }

    public String serverUrl() { return preferences.getString(MobileConfig.PREF_SERVER_URL, ""); }
    public String token() { return secureTokenTool.load(preferences); }

    public String deviceId() {
        String existing = preferences.getString(MobileConfig.PREF_DEVICE_ID, "");
        if (existing != null && !existing.trim().isEmpty()) return existing;
        String created = UUID.randomUUID().toString();
        preferences.edit().putString(MobileConfig.PREF_DEVICE_ID, created).apply();
        return created;
    }

    public void saveSyncMapping(String nativeContactId, String syncUuid) {
        String nativeId = safe(nativeContactId);
        String uuid = safe(syncUuid);
        if (nativeId.isEmpty() || uuid.isEmpty()) return;
        String previousUuid = safe(preferences.getString(MobileConfig.PREF_SYNC_MAP_NATIVE_PREFIX + nativeId, ""));
        String previousNativeId = safe(preferences.getString(MobileConfig.PREF_SYNC_MAP_UUID_PREFIX + uuid, ""));
        SharedPreferences.Editor editor = preferences.edit();
        if (!previousUuid.isEmpty() && !previousUuid.equals(uuid)) {
            editor.remove(MobileConfig.PREF_SYNC_MAP_UUID_PREFIX + previousUuid);
        }
        if (!previousNativeId.isEmpty() && !previousNativeId.equals(nativeId)) {
            editor.remove(MobileConfig.PREF_SYNC_MAP_NATIVE_PREFIX + previousNativeId);
        }
        editor.putString(MobileConfig.PREF_SYNC_MAP_NATIVE_PREFIX + nativeId, uuid)
                .putString(MobileConfig.PREF_SYNC_MAP_UUID_PREFIX + uuid, nativeId)
                .apply();
    }

    public String syncUuidForNativeId(String nativeContactId) {
        return preferences.getString(MobileConfig.PREF_SYNC_MAP_NATIVE_PREFIX + safe(nativeContactId), "");
    }

    public String nativeIdForSyncUuid(String syncUuid) {
        return preferences.getString(MobileConfig.PREF_SYNC_MAP_UUID_PREFIX + safe(syncUuid), "");
    }

    private String normalizeUrl(String value) {
        String normalized = safe(value);
        while (normalized.endsWith("/")) normalized = normalized.substring(0, normalized.length() - 1);
        return normalized;
    }

    private String safe(String value) { return value == null ? "" : value.trim(); }
}
