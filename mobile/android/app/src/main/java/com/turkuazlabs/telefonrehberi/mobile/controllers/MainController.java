// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/controllers/MainController.java
// # 📌 Amac: Android UI olaylarini alir ve SyncService katmanina yonlendirir.
// # 📌 Controller - Java
// # Version: 1.0.0
// # Aciklama: Runtime rehber izinlerini ve background senkron cagrilarini yonetir; is kurali barindirmaz.
// # Bagimli Oldugu Katman: Controller | Service | View | Language | Config
package com.turkuazlabs.telefonrehberi.mobile.controllers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.turkuazlabs.telefonrehberi.mobile.config.MobileConfig;
import com.turkuazlabs.telefonrehberi.mobile.language.Messages;
import com.turkuazlabs.telefonrehberi.mobile.models.PullResult;
import com.turkuazlabs.telefonrehberi.mobile.services.SyncService;
import com.turkuazlabs.telefonrehberi.mobile.views.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainController {
    private enum PendingAction { NONE, PULL, PUSH }

    private final MainActivity view;
    private final SyncService service;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private PendingAction pendingAction = PendingAction.NONE;

    public MainController(MainActivity view, SyncService service) {
        this.view = view;
        this.service = service;
    }

    public void initialize() {
        view.showConnection(service.serverUrl(), service.token());
        view.setStatus(Messages.READY);
    }

    public void testConnection() {
        if (!saveConnectionFromView()) {
            return;
        }
        runAsync(() -> {
            service.testConnection();
            return Messages.CONNECTION_OK;
        });
    }

    public void pullFromDesktop() {
        if (!saveConnectionFromView()) {
            return;
        }
        if (!hasContactPermissions()) {
            pendingAction = PendingAction.PULL;
            requestContactPermissions();
            return;
        }
        executePull();
    }

    public void pushToDesktop() {
        if (!saveConnectionFromView()) {
            return;
        }
        if (!hasContactPermissions()) {
            pendingAction = PendingAction.PUSH;
            requestContactPermissions();
            return;
        }
        executePush();
    }

    public void onPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode != MobileConfig.PERMISSION_REQUEST_CONTACTS) {
            return;
        }
        boolean granted = grantResults.length >= 2;
        for (int result : grantResults) {
            granted = granted && result == PackageManager.PERMISSION_GRANTED;
        }
        if (!granted) {
            pendingAction = PendingAction.NONE;
            view.setStatus(Messages.CONTACT_PERMISSION_REQUIRED);
            return;
        }
        PendingAction action = pendingAction;
        pendingAction = PendingAction.NONE;
        if (action == PendingAction.PULL) {
            executePull();
        } else if (action == PendingAction.PUSH) {
            executePush();
        }
    }

    public void destroy() {
        executor.shutdownNow();
    }

    private void executePull() {
        view.setStatus(Messages.WORKING);
        executor.execute(() -> {
            try {
                PullResult result = service.pullDesktopContacts();
                postStatus(String.format(Messages.PULL_RESULT_FORMAT, result.added(), result.skipped()));
            } catch (Exception exception) {
                postStatus(Messages.ERROR_PREFIX + safeMessage(exception));
            }
        });
    }

    private void executePush() {
        view.setStatus(Messages.WORKING);
        executor.execute(() -> {
            try {
                int count = service.pushDeviceContacts();
                postStatus(String.format(Messages.PUSH_RESULT_FORMAT, count));
            } catch (Exception exception) {
                postStatus(Messages.ERROR_PREFIX + safeMessage(exception));
            }
        });
    }

    private boolean saveConnectionFromView() {
        try {
            service.saveConnection(view.serverUrl(), view.token());
            return true;
        } catch (IllegalArgumentException exception) {
            view.setStatus(exception.getMessage());
            return false;
        }
    }

    private boolean hasContactPermissions() {
        return view.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && view.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestContactPermissions() {
        view.requestPermissions(
                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                MobileConfig.PERMISSION_REQUEST_CONTACTS
        );
    }

    private void runAsync(CheckedAction action) {
        view.setStatus(Messages.WORKING);
        executor.execute(() -> {
            try {
                postStatus(action.run());
            } catch (Exception exception) {
                postStatus(Messages.ERROR_PREFIX + safeMessage(exception));
            }
        });
    }

    private void postStatus(String message) {
        mainHandler.post(() -> view.setStatus(message));
    }

    private String safeMessage(Exception exception) {
        return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
    }

    @FunctionalInterface
    private interface CheckedAction {
        String run() throws Exception;
    }
}
