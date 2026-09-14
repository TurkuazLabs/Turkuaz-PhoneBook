// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/PhoneNumberTool.java
// # 📌 Amac: Ulke kodu secimi ile telefon numarasini birlestirir, gorunur bicimlendirir ve temel E.164 uzunluk kontrolu yapar.
// # 📌 Tool - Java
// # Version: 2.10.0
// # Aciklama: Telefonlari +ulkeKoduUlusalNumara biciminde saklar; ulkeye gore ornek, otomatik bosluklandirma ve uzunluk uyarisi saglar.
// # Bagimli Oldugu Katman: Tool | Model | Config
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.PhoneCountryCodeCatalog;
import com.turkuazlabs.telefonrehberi.config.PhoneNumberRuleCatalog;
import com.turkuazlabs.telefonrehberi.models.PhoneCountryCode;
import com.turkuazlabs.telefonrehberi.models.PhoneNumberRule;
import com.turkuazlabs.telefonrehberi.models.PhoneValidationResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PhoneNumberTool {
    private static final int E164_MAX_TOTAL_DIGITS = 15;

    public String compose(PhoneCountryCode country, String customDialCode, String nationalNumber) {
        if (country == null) return normalizeExisting(nationalNumber);
        String dialCode = country.custom() ? normalizeDialCode(customDialCode) : country.dialCode();
        String nationalDigits = normalizeNationalDigits(country, nationalNumber);
        if (dialCode.isBlank()) return nationalDigits;
        return nationalDigits.isBlank() ? dialCode : dialCode + nationalDigits;
    }

    public PhoneCountryCode detectCountry(String storedValue, String defaultIso) {
        String normalized = normalizeExisting(storedValue);
        if (!normalized.startsWith("+")) return PhoneCountryCodeCatalog.byIso(defaultIso);
        return PhoneCountryCodeCatalog.values().stream()
                .filter(value -> normalized.startsWith(value.dialCode()))
                .sorted(Comparator.comparingInt((PhoneCountryCode value) -> value.dialCode().length()).reversed())
                .findFirst()
                .orElse(PhoneCountryCodeCatalog.custom());
    }

    public String nationalPart(String storedValue, PhoneCountryCode country, String defaultIso) {
        String normalized = normalizeExisting(storedValue);
        PhoneCountryCode effective = country == null ? PhoneCountryCodeCatalog.byIso(defaultIso) : country;
        if (effective.custom()) {
            if (!normalized.startsWith("+")) return digits(normalized);
            String customCode = customDialCode(normalized, effective);
            String codeDigits = digits(customCode);
            String allDigits = normalized.substring(1);
            return allDigits.startsWith(codeDigits) ? allDigits.substring(codeDigits.length()) : allDigits;
        }
        if (normalized.startsWith(effective.dialCode())) {
            return normalized.substring(effective.dialCode().length());
        }
        return normalizeNationalDigits(effective, normalized);
    }

    public String customDialCode(String storedValue, PhoneCountryCode country) {
        if (country == null || !country.custom()) return "";
        String normalized = normalizeExisting(storedValue);
        if (!normalized.startsWith("+")) return "";
        String digits = normalized.substring(1);
        int codeLength = Math.min(3, digits.length());
        if (codeLength < 1) return "";
        return "+" + digits.substring(0, codeLength);
    }

    public String normalizeDialCode(String value) {
        String digits = digits(value);
        return digits.isBlank() ? "" : "+" + digits;
    }

    public String normalizeExisting(String value) {
        if (value == null || value.isBlank()) return "";
        String trimmed = value.trim();
        String digits = digits(trimmed);
        if (digits.isBlank()) return "";
        return trimmed.startsWith("+") ? "+" + digits : digits;
    }

    public String formatNational(PhoneCountryCode country, String value) {
        String nationalDigits = normalizeNationalDigits(country, value);
        if (nationalDigits.isBlank()) return "";
        PhoneNumberRule rule = PhoneNumberRuleCatalog.byIso(country == null || country.custom() ? "" : country.isoCode());
        List<String> parts = splitByGroups(nationalDigits, rule.displayGroups());
        return String.join(" ", parts);
    }

    public String exampleNational(PhoneCountryCode country) {
        PhoneNumberRule rule = PhoneNumberRuleCatalog.byIso(country == null || country.custom() ? "" : country.isoCode());
        return rule.exampleNational();
    }

    public PhoneValidationResult validate(PhoneCountryCode country, String customDialCode, String nationalNumber) {
        String nationalDigits = normalizeNationalDigits(country, nationalNumber);
        PhoneNumberRule rule = PhoneNumberRuleCatalog.byIso(country == null || country.custom() ? "" : country.isoCode());
        if (nationalDigits.isBlank()) {
            return new PhoneValidationResult(
                    PhoneValidationResult.Status.EMPTY,
                    0,
                    rule.minNationalDigits(),
                    rule.maxNationalDigits()
            );
        }

        String dialCode = country != null && country.custom() ? normalizeDialCode(customDialCode) : country == null ? "" : country.dialCode();
        int totalDigits = digits(dialCode).length() + nationalDigits.length();
        if (nationalDigits.length() < rule.minNationalDigits()) {
            return new PhoneValidationResult(
                    PhoneValidationResult.Status.TOO_SHORT,
                    nationalDigits.length(),
                    rule.minNationalDigits(),
                    rule.maxNationalDigits()
            );
        }
        if (nationalDigits.length() > rule.maxNationalDigits() || totalDigits > E164_MAX_TOTAL_DIGITS) {
            return new PhoneValidationResult(
                    PhoneValidationResult.Status.TOO_LONG,
                    nationalDigits.length(),
                    rule.minNationalDigits(),
                    rule.maxNationalDigits()
            );
        }
        return new PhoneValidationResult(
                PhoneValidationResult.Status.VALID,
                nationalDigits.length(),
                rule.minNationalDigits(),
                rule.maxNationalDigits()
        );
    }

    private String normalizeNationalDigits(PhoneCountryCode country, String value) {
        String nationalDigits = digits(value);
        if (country != null && "TR".equals(country.isoCode()) && nationalDigits.startsWith("0")) {
            nationalDigits = nationalDigits.substring(1);
        }
        return nationalDigits;
    }

    private List<String> splitByGroups(String digits, List<Integer> groups) {
        if (groups == null || groups.isEmpty()) return List.of(digits);
        List<String> parts = new ArrayList<>();
        int offset = 0;
        for (Integer group : groups) {
            if (group == null || group < 1 || offset >= digits.length()) continue;
            int end = Math.min(offset + group, digits.length());
            parts.add(digits.substring(offset, end));
            offset = end;
        }
        if (offset < digits.length()) {
            parts.add(digits.substring(offset));
        }
        return parts;
    }

    private String digits(String value) {
        return value == null ? "" : value.replaceAll("[^0-9]", "");
    }
}
