// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/FormCodec.java
// # 📌 Amac: Mobil API form-urlencoded istek govdesini anahtar-deger haritasina donusturur.
// # 📌 Tool - Java
// # Version: 1.0.0
// # Aciklama: UTF-8 URL kodlu form alanlarini parse eder.
// # Bagimli Oldugu Katman: Tool | Config
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public final class FormCodec {
    private static final String PAIR_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    public Map<String, String> decode(String body) {
        Map<String, String> values = new HashMap<>();
        if (body == null || body.isBlank()) {
            return values;
        }
        for (String pair : body.split(PAIR_SEPARATOR)) {
            int separatorIndex = pair.indexOf(KEY_VALUE_SEPARATOR);
            String key = separatorIndex >= 0 ? pair.substring(0, separatorIndex) : pair;
            String value = separatorIndex >= 0 ? pair.substring(separatorIndex + 1) : "";
            values.put(
                    URLDecoder.decode(key, AppConfig.DATA_CHARSET),
                    URLDecoder.decode(value, AppConfig.DATA_CHARSET)
            );
        }
        return values;
    }
}
