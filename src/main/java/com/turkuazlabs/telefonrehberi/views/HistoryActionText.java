// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/HistoryActionText.java
// # 📌 Amac: Gecmis islem kodlarini kullanici dostu arayuz metinlerine cevirir.
// # 📌 View - Java
// # Version: 1.1.0
// # Aciklama: HistoryAction enum degerlerini kullanici dostu etiket ve timeline aciklamalariyla eslestirir.
// # Bagimli Oldugu Katman: View | Model | Language
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.HistoryAction;

public final class HistoryActionText {
    private HistoryActionText() {
    }

    public static String label(String storageValue) {
        if (storageValue == null || storageValue.isBlank()) {
            return Messages.HISTORY_ACTION_UNKNOWN;
        }
        try {
            return label(HistoryAction.valueOf(storageValue));
        } catch (IllegalArgumentException exception) {
            return Messages.HISTORY_ACTION_UNKNOWN;
        }
    }

    public static String description(String storageValue) {
        if (storageValue == null || storageValue.isBlank()) {
            return Messages.ACTIVITY_DESCRIPTION_UNKNOWN;
        }
        try {
            return description(HistoryAction.valueOf(storageValue));
        } catch (IllegalArgumentException exception) {
            return Messages.ACTIVITY_DESCRIPTION_UNKNOWN;
        }
    }

    private static String label(HistoryAction action) {
        return switch (action) {
            case CREATE -> Messages.HISTORY_ACTION_CREATE;
            case UPDATE -> Messages.HISTORY_ACTION_UPDATE;
            case DELETE -> Messages.HISTORY_ACTION_DELETE;
            case RESTORE -> Messages.HISTORY_ACTION_RESTORE;
            case MOBILE_SYNC -> Messages.HISTORY_ACTION_MOBILE_SYNC;
            case MERGE -> Messages.HISTORY_ACTION_MERGE;
            case IMPORT -> Messages.HISTORY_ACTION_IMPORT;
            case HISTORY_RESTORE -> Messages.HISTORY_ACTION_HISTORY_RESTORE;
        };
    }

    private static String description(HistoryAction action) {
        return switch (action) {
            case CREATE -> Messages.ACTIVITY_DESCRIPTION_CREATE;
            case UPDATE -> Messages.ACTIVITY_DESCRIPTION_UPDATE;
            case DELETE -> Messages.ACTIVITY_DESCRIPTION_DELETE;
            case RESTORE -> Messages.ACTIVITY_DESCRIPTION_RESTORE;
            case MOBILE_SYNC -> Messages.ACTIVITY_DESCRIPTION_MOBILE_SYNC;
            case MERGE -> Messages.ACTIVITY_DESCRIPTION_MERGE;
            case IMPORT -> Messages.ACTIVITY_DESCRIPTION_IMPORT;
            case HISTORY_RESTORE -> Messages.ACTIVITY_DESCRIPTION_HISTORY_RESTORE;
        };
    }
}
