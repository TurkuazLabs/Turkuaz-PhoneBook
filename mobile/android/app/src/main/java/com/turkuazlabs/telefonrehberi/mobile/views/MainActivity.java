// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/views/MainActivity.java
// # 📌 Amac: Android mobil senkron kullanici arayuzunu sunar.
// # 📌 View - Java
// # Version: 1.0.0
// # Aciklama: PC adresi/token alanlarini, senkron butonlarini ve durum mesajini gosterir.
// # Bagimli Oldugu Katman: View | Controller | Service | Repository | Tool
package com.turkuazlabs.telefonrehberi.mobile.views;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.turkuazlabs.telefonrehberi.mobile.R;
import com.turkuazlabs.telefonrehberi.mobile.controllers.MainController;
import com.turkuazlabs.telefonrehberi.mobile.repositories.DeviceContactRepository;
import com.turkuazlabs.telefonrehberi.mobile.repositories.SettingsRepository;
import com.turkuazlabs.telefonrehberi.mobile.services.SyncService;
import com.turkuazlabs.telefonrehberi.mobile.tools.DesktopApiTool;

public final class MainActivity extends Activity {
    private EditText serverUrlField;
    private EditText tokenField;
    private TextView statusText;
    private MainController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverUrlField = findViewById(R.id.serverUrlField);
        tokenField = findViewById(R.id.tokenField);
        statusText = findViewById(R.id.statusText);
        Button testButton = findViewById(R.id.testButton);
        Button pullButton = findViewById(R.id.pullButton);
        Button pushButton = findViewById(R.id.pushButton);

        SettingsRepository settingsRepository = new SettingsRepository(this);
        SyncService service = new SyncService(
                new DeviceContactRepository(this),
                settingsRepository,
                new DesktopApiTool()
        );
        controller = new MainController(this, service);
        testButton.setOnClickListener(view -> controller.testConnection());
        pullButton.setOnClickListener(view -> controller.pullFromDesktop());
        pushButton.setOnClickListener(view -> controller.pushToDesktop());
        controller.initialize();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        controller.onPermissionsResult(requestCode, grantResults);
    }

    @Override
    protected void onDestroy() {
        if (controller != null) {
            controller.destroy();
        }
        super.onDestroy();
    }

    public String serverUrl() { return serverUrlField.getText().toString(); }
    public String token() { return tokenField.getText().toString(); }

    public void showConnection(String serverUrl, String token) {
        serverUrlField.setText(serverUrl);
        tokenField.setText(token);
    }

    public void setStatus(String message) {
        statusText.setText(message);
    }
}
