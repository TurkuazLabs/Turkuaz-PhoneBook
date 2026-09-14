// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/LanAddressTool.java
// # 📌 Amac: Mobil cihazlarin erisebilecegi yerel IPv4 adresini bulur.
// # 📌 Tool - Java
// # Version: 1.0.0
// # Aciklama: Aktif ve loopback olmayan ag arayuzlerinden ilk uygun IPv4 adresini secer.
// # Bagimli Oldugu Katman: Tool
package com.turkuazlabs.telefonrehberi.tools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

public final class LanAddressTool {
    public String findLanAddress() {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }
                for (InetAddress address : Collections.list(networkInterface.getInetAddresses())) {
                    if (address instanceof Inet4Address && !address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException ignored) {
            // Arayuz bilgisi okunamazsa localhost gosterilir.
        }
        return "127.0.0.1";
    }
}
