// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/android/app/src/main/java/com/turkuazlabs/telefonrehberi/mobile/tools/LanEndpointTool.java
// # 📌 Amac: Android mobil senkron sunucu adresini guvenli HTTP/HTTPS ve LAN kurallarina gore dogrular.
// # 📌 Tool - Java
// Version: 1.0.0
// Aciklama: Cleartext HTTP'yi yalniz loopback, private/link-local IP ve yerel hostname hedeflerine sinirlar; uzak hedeflerde HTTPS gerektirir.
// Bagimli Oldugu Katman: Tool | Language
package com.turkuazlabs.telefonrehberi.mobile.tools;

import com.turkuazlabs.telefonrehberi.mobile.language.Messages;

import java.net.URI;
import java.util.Locale;

public final class LanEndpointTool {
    public String validateBaseUrl(String value) {
        String normalized = value == null ? "" : value.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(Messages.SERVER_REQUIRED);
        }

        final URI uri;
        try {
            uri = URI.create(normalized);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(Messages.SERVER_URL_INVALID, exception);
        }

        String scheme = safeLower(uri.getScheme());
        String host = uri.getHost();
        if ((!"http".equals(scheme) && !"https".equals(scheme))
                || host == null || host.isBlank()
                || uri.getUserInfo() != null
                || uri.getQuery() != null
                || uri.getFragment() != null
                || (uri.getPath() != null && !uri.getPath().isBlank() && !"/".equals(uri.getPath()))) {
            throw new IllegalArgumentException(Messages.SERVER_URL_INVALID);
        }

        if ("http".equals(scheme) && !isLocalOrPrivateHost(host)) {
            throw new IllegalArgumentException(Messages.SERVER_HTTP_LOCAL_ONLY);
        }
        return normalized;
    }

    boolean isLocalOrPrivateHost(String value) {
        String host = safeLower(value);
        if (host.startsWith("[") && host.endsWith("]")) {
            host = host.substring(1, host.length() - 1);
        }
        int zoneIndex = host.indexOf('%');
        if (zoneIndex >= 0) {
            host = host.substring(0, zoneIndex);
        }

        if (host.equals("localhost") || host.equals("localhost.localdomain")) {
            return true;
        }
        if (host.endsWith(".local") || host.endsWith(".lan") || host.endsWith(".home.arpa")) {
            return true;
        }
        if (!host.contains(".") && !host.contains(":")) {
            return true;
        }
        if (isPrivateIpv4(host)) {
            return true;
        }
        if (host.startsWith("::ffff:")) {
            return isPrivateIpv4(host.substring("::ffff:".length()));
        }
        return isPrivateIpv6(host);
    }

    private boolean isPrivateIpv4(String host) {
        String[] parts = host.split("\\.", -1);
        if (parts.length != 4) return false;
        int[] octets = new int[4];
        try {
            for (int index = 0; index < parts.length; index++) {
                if (parts[index].isEmpty() || parts[index].length() > 3) return false;
                octets[index] = Integer.parseInt(parts[index]);
                if (octets[index] < 0 || octets[index] > 255) return false;
            }
        } catch (NumberFormatException exception) {
            return false;
        }
        return octets[0] == 10
                || octets[0] == 127
                || (octets[0] == 169 && octets[1] == 254)
                || (octets[0] == 172 && octets[1] >= 16 && octets[1] <= 31)
                || (octets[0] == 192 && octets[1] == 168)
                || (octets[0] == 100 && octets[1] >= 64 && octets[1] <= 127);
    }

    private boolean isPrivateIpv6(String host) {
        if (host.equals("::1")) return true;
        if (host.startsWith("fc") || host.startsWith("fd")) return true;
        if (host.length() < 3 || !host.startsWith("fe")) return false;
        char third = host.charAt(2);
        return third == '8' || third == '9' || third == 'a' || third == 'b';
    }

    private String safeLower(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
