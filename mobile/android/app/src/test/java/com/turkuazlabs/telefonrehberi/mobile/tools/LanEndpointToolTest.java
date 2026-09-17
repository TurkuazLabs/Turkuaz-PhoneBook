// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/test/java/com/turkuazlabs/telefonrehberi/mobile/tools/LanEndpointToolTest.java
// # 📌 Amac: Android LAN endpoint guvenlik kurallarini harici test frameworku olmadan dogrular.
// # 📌 Tool - Java
// Version: 1.0.1
// # 📌 Aciklama: Private/loopback/link-local HTTP hedeflerini ve HTTPS'i kabul; uzak cleartext HTTP, gecersiz port ve gecersiz base URL'leri reddetme kurallarini test eder.
// # 📌 Bagimli Oldugu Katman: Tool | Language
package com.turkuazlabs.telefonrehberi.mobile.tools;

public final class LanEndpointToolTest {
    private final LanEndpointTool tool = new LanEndpointTool();

    public static void main(String[] args) {
        LanEndpointToolTest test = new LanEndpointToolTest();
        test.acceptsTrustedLanHttpTargets();
        test.acceptsHttpsTargets();
        test.rejectsRemoteCleartextHttpTargets();
        test.rejectsInvalidBaseUrls();
        System.out.println("Android LanEndpointTool quality gate: SUCCESS");
    }

    private void acceptsTrustedLanHttpTargets() {
        expectAccepted("http://192.168.1.10:8787", "http://192.168.1.10:8787");
        expectAccepted("http://10.0.0.2:8787/", "http://10.0.0.2:8787");
        expectAccepted("http://172.16.1.2:8787", "http://172.16.1.2:8787");
        expectAccepted("http://172.31.255.254:8787", "http://172.31.255.254:8787");
        expectAccepted("http://169.254.10.5:8787", "http://169.254.10.5:8787");
        expectAccepted("http://100.64.0.1:8787", "http://100.64.0.1:8787");
        expectAccepted("http://127.0.0.1:8787", "http://127.0.0.1:8787");
        expectAccepted("http://localhost:8787", "http://localhost:8787");
        expectAccepted("http://phonebook-pc:8787", "http://phonebook-pc:8787");
        expectAccepted("http://pc.local:8787", "http://pc.local:8787");
        expectAccepted("http://router.home.arpa:8787", "http://router.home.arpa:8787");
        expectAccepted("http://[::1]:8787", "http://[::1]:8787");
        expectAccepted("http://[fd12::1]:8787", "http://[fd12::1]:8787");
        expectAccepted("http://[fe80::1]:8787", "http://[fe80::1]:8787");
        expectAccepted("http://192.168.1.10:65535", "http://192.168.1.10:65535");
    }

    private void acceptsHttpsTargets() {
        expectAccepted("https://phonebook.example.com", "https://phonebook.example.com");
        expectAccepted("https://203.0.113.25:9443", "https://203.0.113.25:9443");
    }

    private void rejectsRemoteCleartextHttpTargets() {
        expectRejected("http://example.com:8787");
        expectRejected("http://8.8.8.8:8787");
        expectRejected("http://172.15.1.1:8787");
        expectRejected("http://172.32.1.1:8787");
        expectRejected("http://192.169.1.1:8787");
        expectRejected("http://[2001:4860:4860::8888]:8787");
    }

    private void rejectsInvalidBaseUrls() {
        expectRejected("");
        expectRejected("192.168.1.10:8787");
        expectRejected("ftp://192.168.1.10:8787");
        expectRejected("http://user@192.168.1.10:8787");
        expectRejected("http://192.168.1.10:8787/api");
        expectRejected("http://192.168.1.10:8787/?token=x");
        expectRejected("http://192.168.1.10:8787/#fragment");
        expectRejected("http://192.168.1.10:0");
        expectRejected("http://192.168.1.10:65536");
    }

    private void expectAccepted(String input, String expected) {
        String actual = tool.validateBaseUrl(input);
        if (!expected.equals(actual)) {
            throw new AssertionError("Beklenen URL: " + expected + ", gelen: " + actual);
        }
    }

    private void expectRejected(String input) {
        try {
            tool.validateBaseUrl(input);
            throw new AssertionError("URL reddedilmeliydi: " + input);
        } catch (IllegalArgumentException expected) {
            // Beklenen sonuc.
        }
    }
}
