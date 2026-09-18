// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/test/java/com/turkuazlabs/telefonrehberi/ApiContractQualityGateTest.java
// # 📌 Amac: Masaustu, Android, iOS ve mobil senkron dokumani arasindaki API endpoint sozlesmesini regresyon testine alir.
// # 📌 Tool - Java Test
// Version: 1.0.1
// # Aciklama: /api/v1 status, contacts ve import yollarinin uc platform configinde ve MOBILE_SYNC_API.md belgesinde birebir hizali kalmasini dogrular.
// # Bagimli Oldugu Katman: Tool | Config | Language

package com.turkuazlabs.telefonrehberi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ApiContractQualityGateTest {
    private static final List<Endpoint> ENDPOINTS = List.of(
            new Endpoint("status", "/api/v1/status", "/api/status"),
            new Endpoint("contacts", "/api/v1/contacts", "/api/contacts"),
            new Endpoint("import", "/api/v1/import", "/api/import")
    );

    private ApiContractQualityGateTest() {
    }

    public static void main(String[] args) throws Exception {
        Path root = args.length > 0
                ? Path.of(args[0]).toAbsolutePath().normalize()
                : Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();

        String desktop = read(root, "src/main/java/com/turkuazlabs/telefonrehberi/config/AppConfig.java");
        String android = read(root, "mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/config/MobileConfig.java");
        String ios = read(root, "mobile/ios/TurkuazTelefonRehberiIOS/config/MobileConfig.swift");
        String docs = read(root, "docs/MOBILE_SYNC_API.md");

        for (Endpoint endpoint : ENDPOINTS) {
            check(desktop.contains(quote(endpoint.current())),
                    "Desktop AppConfig endpoint eksik: " + endpoint.current());
            check(android.contains(quote(endpoint.current())),
                    "Android MobileConfig endpoint eksik: " + endpoint.current());
            check(ios.contains(quote(endpoint.current())),
                    "iOS MobileConfig endpoint eksik: " + endpoint.current());
            check(docs.contains(endpoint.current()),
                    "MOBILE_SYNC_API.md endpoint eksik: " + endpoint.current());
            check(!docs.contains(endpoint.legacy()),
                    "MOBILE_SYNC_API.md eski endpoint tasiyor: " + endpoint.legacy());
        }

        System.out.println("API_CONTRACT_QUALITY_GATE_OK");
    }

    private static String read(Path root, String relativePath) throws Exception {
        Path file = root.resolve(relativePath).normalize();
        check(file.startsWith(root), "Repo disi quality-gate yolu reddedildi: " + relativePath);
        check(Files.isRegularFile(file), "Quality-gate dosyasi bulunamadi: " + relativePath);
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    private static String quote(String value) {
        return "\"" + value + "\"";
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private record Endpoint(String name, String current, String legacy) {
    }
}
