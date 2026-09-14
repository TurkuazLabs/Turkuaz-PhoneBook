// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/controllers/MobileSyncController.java
// # 📌 Amac: LAN uzerinden gelen mobil HTTP isteklerini alir ve MobileSyncService katmanina yonlendirir.
// # 📌 Controller - Java
// # Version: 3.2.0
// # Aciklama: Controller yalniz HTTP routing, sabit zamanli yetkilendirme ve Service/Tool cagrisini yapar; HTTP executor yasam dongusu kontrolludur.
// # Bagimli Oldugu Katman: Controller | Service | Tool | View | Config | Language
package com.turkuazlabs.telefonrehberi.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.SyncServerInfo;
import com.turkuazlabs.telefonrehberi.services.MobileSyncService;
import com.turkuazlabs.telefonrehberi.tools.MobileSyncFormTool;
import com.turkuazlabs.telefonrehberi.views.ApiJsonView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MobileSyncController {
    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_METHOD_NOT_ALLOWED = 405;
    private static final int HTTP_PAYLOAD_TOO_LARGE = 413;
    private static final int HTTP_INTERNAL_ERROR = 500;

    private final MobileSyncService service;
    private final MobileSyncFormTool formTool;
    private final ApiJsonView jsonView;
    private HttpServer server;
    private ExecutorService executor;
    private int activePort = AppConfig.MOBILE_SYNC_PORT;

    public MobileSyncController(MobileSyncService service, MobileSyncFormTool formTool, ApiJsonView jsonView) {
        this.service = service;
        this.formTool = formTool;
        this.jsonView = jsonView;
    }

    public synchronized void start(int port) {
        if (server != null) return;
        activePort = port;
        try {
            server = HttpServer.create(
                    new InetSocketAddress(AppConfig.MOBILE_SYNC_BIND_ADDRESS, activePort),
                    AppConfig.MOBILE_SYNC_BACKLOG
            );
            server.createContext(AppConfig.API_STATUS_PATH, this::handleStatus);
            server.createContext(AppConfig.API_CONTACTS_PATH, this::handleContacts);
            server.createContext(AppConfig.API_IMPORT_PATH, this::handleImport);
            executor = Executors.newFixedThreadPool(AppConfig.MOBILE_SYNC_THREADS);
            server.setExecutor(executor);
            server.start();
        } catch (IOException exception) {
            server = null;
            shutdownExecutor();
            throw new IllegalStateException(Messages.ERROR_SYNC_SERVER_START, exception);
        }
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
        shutdownExecutor();
    }

    public SyncServerInfo serverInfo() { return service.serverInfo(server != null, activePort); }

    private void handleStatus(HttpExchange exchange) throws IOException {
        if (!authorize(exchange)) return;
        if (!requireMethod(exchange, AppConfig.HTTP_GET)) return;
        send(exchange, HTTP_OK, jsonView.status(AppConfig.APP_VERSION));
    }

    private void handleContacts(HttpExchange exchange) throws IOException {
        if (!authorize(exchange)) return;
        if (!requireMethod(exchange, AppConfig.HTTP_GET)) return;
        try {
            send(exchange, HTTP_OK, jsonView.contacts(service.listContacts()));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            send(exchange, HTTP_INTERNAL_ERROR, jsonView.error(exception.getMessage()));
        }
    }

    private void handleImport(HttpExchange exchange) throws IOException {
        if (!authorize(exchange)) return;
        if (!requireMethod(exchange, AppConfig.HTTP_POST)) return;
        try {
            Contact contact = service.importContact(formTool.decode(
                    exchange.getRequestBody(), AppConfig.MOBILE_SYNC_MAX_REQUEST_BYTES
            ));
            send(exchange, HTTP_CREATED, jsonView.imported(contact.id(), contact.syncUuid()));
        } catch (IllegalArgumentException exception) {
            int status = Messages.ERROR_SYNC_REQUEST_TOO_LARGE.equals(exception.getMessage())
                    ? HTTP_PAYLOAD_TOO_LARGE : HTTP_BAD_REQUEST;
            send(exchange, status, jsonView.error(exception.getMessage()));
        } catch (IllegalStateException exception) {
            send(exchange, HTTP_INTERNAL_ERROR, jsonView.error(exception.getMessage()));
        }
    }

    private boolean authorize(HttpExchange exchange) throws IOException {
        String provided = exchange.getRequestHeaders().getFirst(AppConfig.AUTHORIZATION_HEADER);
        String expected = AppConfig.BEARER_PREFIX + service.token();
        if (!constantTimeEquals(expected, provided)) {
            send(exchange, HTTP_UNAUTHORIZED, jsonView.error(Messages.ERROR_SYNC_UNAUTHORIZED));
            return false;
        }
        return true;
    }

    private boolean constantTimeEquals(String expected, String provided) {
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        byte[] providedBytes = (provided == null ? "" : provided).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expectedBytes, providedBytes);
    }

    private void shutdownExecutor() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    private boolean requireMethod(HttpExchange exchange, String expectedMethod) throws IOException {
        if (!expectedMethod.equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, HTTP_METHOD_NOT_ALLOWED, jsonView.error(Messages.ERROR_SYNC_METHOD));
            return false;
        }
        return true;
    }

    private void send(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(AppConfig.CONTENT_TYPE_HEADER, AppConfig.CONTENT_TYPE_JSON);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
