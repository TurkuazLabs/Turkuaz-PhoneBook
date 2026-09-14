// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/config/PhoneCountryCodeCatalog.java
// # 📌 Amac: Telefon girisinde kullanilacak ulke kodu seceneklerini merkezi olarak tanimlar.
// # 📌 Config - Java
// # Version: 2.9.0
// # Aciklama: Turkiye'yi varsayilan tutar, yaygin ulke kodlarini listeler ve ozel kod secenegi sunar.
// # Bagimli Oldugu Katman: Config | Model
package com.turkuazlabs.telefonrehberi.config;

import com.turkuazlabs.telefonrehberi.models.PhoneCountryCode;

import java.util.Comparator;
import java.util.List;

public final class PhoneCountryCodeCatalog {
    private static final PhoneCountryCode CUSTOM = new PhoneCountryCode(PhoneCountryCode.CUSTOM_ISO, "Diger / Ozel Kod", "");

    private static final List<PhoneCountryCode> VALUES = List.of(
            new PhoneCountryCode("TR", "Turkiye", "+90"),
            new PhoneCountryCode("DE", "Almanya", "+49"),
            new PhoneCountryCode("US", "Amerika Birlesik Devletleri", "+1"),
            new PhoneCountryCode("GB", "Birlesik Krallik", "+44"),
            new PhoneCountryCode("AZ", "Azerbaycan", "+994"),
            new PhoneCountryCode("AE", "Birlesik Arap Emirlikleri", "+971"),
            new PhoneCountryCode("SA", "Suudi Arabistan", "+966"),
            new PhoneCountryCode("QA", "Katar", "+974"),
            new PhoneCountryCode("KW", "Kuveyt", "+965"),
            new PhoneCountryCode("BH", "Bahreyn", "+973"),
            new PhoneCountryCode("OM", "Umman", "+968"),
            new PhoneCountryCode("IQ", "Irak", "+964"),
            new PhoneCountryCode("IR", "Iran", "+98"),
            new PhoneCountryCode("IL", "Israil", "+972"),
            new PhoneCountryCode("JO", "Urdun", "+962"),
            new PhoneCountryCode("LB", "Lubnan", "+961"),
            new PhoneCountryCode("SY", "Suriye", "+963"),
            new PhoneCountryCode("EG", "Misir", "+20"),
            new PhoneCountryCode("CY", "Kibris", "+357"),
            new PhoneCountryCode("GR", "Yunanistan", "+30"),
            new PhoneCountryCode("BG", "Bulgaristan", "+359"),
            new PhoneCountryCode("RO", "Romanya", "+40"),
            new PhoneCountryCode("RS", "Sirbistan", "+381"),
            new PhoneCountryCode("BA", "Bosna Hersek", "+387"),
            new PhoneCountryCode("HR", "Hirvatistan", "+385"),
            new PhoneCountryCode("AL", "Arnavutluk", "+355"),
            new PhoneCountryCode("MK", "Kuzey Makedonya", "+389"),
            new PhoneCountryCode("ME", "Karadag", "+382"),
            new PhoneCountryCode("XK", "Kosova", "+383"),
            new PhoneCountryCode("IT", "Italya", "+39"),
            new PhoneCountryCode("FR", "Fransa", "+33"),
            new PhoneCountryCode("ES", "Ispanya", "+34"),
            new PhoneCountryCode("PT", "Portekiz", "+351"),
            new PhoneCountryCode("NL", "Hollanda", "+31"),
            new PhoneCountryCode("BE", "Belcika", "+32"),
            new PhoneCountryCode("LU", "Luksemburg", "+352"),
            new PhoneCountryCode("AT", "Avusturya", "+43"),
            new PhoneCountryCode("CH", "Isvicre", "+41"),
            new PhoneCountryCode("DK", "Danimarka", "+45"),
            new PhoneCountryCode("SE", "Isvec", "+46"),
            new PhoneCountryCode("NO", "Norvec", "+47"),
            new PhoneCountryCode("FI", "Finlandiya", "+358"),
            new PhoneCountryCode("IS", "Izlanda", "+354"),
            new PhoneCountryCode("IE", "Irlanda", "+353"),
            new PhoneCountryCode("PL", "Polonya", "+48"),
            new PhoneCountryCode("CZ", "Cekya", "+420"),
            new PhoneCountryCode("SK", "Slovakya", "+421"),
            new PhoneCountryCode("HU", "Macaristan", "+36"),
            new PhoneCountryCode("SI", "Slovenya", "+386"),
            new PhoneCountryCode("EE", "Estonya", "+372"),
            new PhoneCountryCode("LV", "Letonya", "+371"),
            new PhoneCountryCode("LT", "Litvanya", "+370"),
            new PhoneCountryCode("UA", "Ukrayna", "+380"),
            new PhoneCountryCode("MD", "Moldova", "+373"),
            new PhoneCountryCode("GE", "Gurcistan", "+995"),
            new PhoneCountryCode("AM", "Ermenistan", "+374"),
            new PhoneCountryCode("RU", "Rusya", "+7"),
            new PhoneCountryCode("KZ", "Kazakistan", "+7"),
            new PhoneCountryCode("UZ", "Ozbekistan", "+998"),
            new PhoneCountryCode("TM", "Turkmenistan", "+993"),
            new PhoneCountryCode("KG", "Kirgizistan", "+996"),
            new PhoneCountryCode("TJ", "Tacikistan", "+992"),
            new PhoneCountryCode("AF", "Afganistan", "+93"),
            new PhoneCountryCode("PK", "Pakistan", "+92"),
            new PhoneCountryCode("IN", "Hindistan", "+91"),
            new PhoneCountryCode("BD", "Banglades", "+880"),
            new PhoneCountryCode("LK", "Sri Lanka", "+94"),
            new PhoneCountryCode("NP", "Nepal", "+977"),
            new PhoneCountryCode("CN", "Cin", "+86"),
            new PhoneCountryCode("JP", "Japonya", "+81"),
            new PhoneCountryCode("KR", "Guney Kore", "+82"),
            new PhoneCountryCode("HK", "Hong Kong", "+852"),
            new PhoneCountryCode("TW", "Tayvan", "+886"),
            new PhoneCountryCode("SG", "Singapur", "+65"),
            new PhoneCountryCode("MY", "Malezya", "+60"),
            new PhoneCountryCode("ID", "Endonezya", "+62"),
            new PhoneCountryCode("TH", "Tayland", "+66"),
            new PhoneCountryCode("VN", "Vietnam", "+84"),
            new PhoneCountryCode("PH", "Filipinler", "+63"),
            new PhoneCountryCode("AU", "Avustralya", "+61"),
            new PhoneCountryCode("NZ", "Yeni Zelanda", "+64"),
            new PhoneCountryCode("CA", "Kanada", "+1"),
            new PhoneCountryCode("MX", "Meksika", "+52"),
            new PhoneCountryCode("BR", "Brezilya", "+55"),
            new PhoneCountryCode("AR", "Arjantin", "+54"),
            new PhoneCountryCode("CL", "Sili", "+56"),
            new PhoneCountryCode("CO", "Kolombiya", "+57"),
            new PhoneCountryCode("PE", "Peru", "+51"),
            new PhoneCountryCode("VE", "Venezuela", "+58"),
            new PhoneCountryCode("UY", "Uruguay", "+598"),
            new PhoneCountryCode("PY", "Paraguay", "+595"),
            new PhoneCountryCode("BO", "Bolivya", "+591"),
            new PhoneCountryCode("EC", "Ekvador", "+593"),
            new PhoneCountryCode("ZA", "Guney Afrika", "+27"),
            new PhoneCountryCode("MA", "Fas", "+212"),
            new PhoneCountryCode("DZ", "Cezayir", "+213"),
            new PhoneCountryCode("TN", "Tunus", "+216"),
            new PhoneCountryCode("LY", "Libya", "+218"),
            new PhoneCountryCode("NG", "Nijerya", "+234"),
            new PhoneCountryCode("KE", "Kenya", "+254"),
            new PhoneCountryCode("ET", "Etiyopya", "+251"),
            new PhoneCountryCode("GH", "Gana", "+233"),
            new PhoneCountryCode("SD", "Sudan", "+249")
    );

    private PhoneCountryCodeCatalog() {
    }

    public static List<PhoneCountryCode> selectableValues() {
        PhoneCountryCode defaultValue = byIso(AppConfig.DEFAULT_PHONE_COUNTRY_ISO);
        List<PhoneCountryCode> sorted = VALUES.stream()
                .filter(value -> !value.isoCode().equals(defaultValue.isoCode()))
                .sorted(Comparator.comparing(PhoneCountryCode::countryName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        java.util.ArrayList<PhoneCountryCode> result = new java.util.ArrayList<>();
        result.add(defaultValue);
        result.addAll(sorted);
        result.add(CUSTOM);
        return List.copyOf(result);
    }

    public static PhoneCountryCode byIso(String isoCode) {
        String normalized = isoCode == null ? "" : isoCode.trim().toUpperCase();
        return VALUES.stream()
                .filter(value -> value.isoCode().equals(normalized))
                .findFirst()
                .orElseGet(() -> VALUES.stream().filter(value -> value.isoCode().equals("TR")).findFirst().orElseThrow());
    }

    public static PhoneCountryCode custom() {
        return CUSTOM;
    }

    public static List<PhoneCountryCode> values() {
        return VALUES;
    }
}
