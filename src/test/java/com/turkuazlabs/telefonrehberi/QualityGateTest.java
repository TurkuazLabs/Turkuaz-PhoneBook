// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/test/java/com/turkuazlabs/telefonrehberi/QualityGateTest.java
// # 📌 Amac: SQLite backup, sync UUID, history retention, request limiti, kullanici ayarlari ve urun/arayuz yerellestirmesini dogrular.
// # 📌 Tool - Java Test
// Version: 1.8.0
// Aciklama: Java 17 release quality gate; yedekleme, reminder selection, notification preference, yerellestirme ve veri guvenligini dogrular.
// Bagimli Oldugu Katman: Repository | Service | Tool | Config | Model | Language
package com.turkuazlabs.telefonrehberi;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.config.PhoneCountryCodeCatalog;
import com.turkuazlabs.telefonrehberi.language.ContactMethodText;
import com.turkuazlabs.telefonrehberi.language.LocaleText;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.language.PhoneCountryCodeText;
import com.turkuazlabs.telefonrehberi.language.ProductText;
import com.turkuazlabs.telefonrehberi.models.AppSettings;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.PhoneCountryCode;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ThemeMode;
import com.turkuazlabs.telefonrehberi.repositories.ContactHistoryRepository;
import com.turkuazlabs.telefonrehberi.repositories.BackupRepository;
import com.turkuazlabs.telefonrehberi.repositories.ContactRepository;
import com.turkuazlabs.telefonrehberi.repositories.SimpleYamlRepository;
import com.turkuazlabs.telefonrehberi.repositories.UserPreferencesRepository;
import com.turkuazlabs.telefonrehberi.services.ContactService;
import com.turkuazlabs.telefonrehberi.tools.ContactMethodCodec;
import com.turkuazlabs.telefonrehberi.tools.ContactSnapshotCodec;
import com.turkuazlabs.telefonrehberi.tools.FormCodec;
import com.turkuazlabs.telefonrehberi.tools.LanguagePreferenceTool;
import com.turkuazlabs.telefonrehberi.tools.MobileSyncFormTool;
import com.turkuazlabs.telefonrehberi.tools.SQLiteBackupTool;
import com.turkuazlabs.telefonrehberi.tools.SQLiteConnectionProvider;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Locale;
import java.util.List;
import java.util.UUID;

public final class QualityGateTest {
    private QualityGateTest() {
    }

    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        Path root = Files.createTempDirectory("telefonrehberi-quality-");
        try {
            testProductNameLocalization();
            testDesktopMessageLocalization();
            testContactMethodLabelLocalization();
            testPhoneCountryLocalizationAndDefault();
            testBackupIncludesCommittedWalData(root.resolve("backup"));
            testWeeklyBackupRetention(root.resolve("weekly-backup"));
            testSyncUuidIsIdempotent(root.resolve("sync"));
            testHistoryRetention(root.resolve("history"));
            testMobileRequestLimitAndMapping();
            testDueReminderSelection(root.resolve("reminders"));
            testUserPreferencesRoundTrip(root.resolve("preferences"));
            System.out.println("QUALITY_GATE_OK");
        } finally {
            deleteRecursively(root);
        }
    }

    private static void testProductNameLocalization() {
        String expected = "tr".equalsIgnoreCase(Locale.getDefault().getLanguage())
                ? ProductText.APP_NAME_TR
                : ProductText.APP_NAME_EN;
        check(expected.equals(ProductText.APP_NAME), "Sistem diline gore urun adi yerellestirmesi hatali.");
        check("Turkuaz".equals(ProductText.BRAND_NAME), "Dil bagimsiz Turkuaz marka adi degisti.");
    }

    private static void testDesktopMessageLocalization() {
        boolean turkish = "tr".equalsIgnoreCase(Locale.getDefault().getLanguage());
        check(ProductText.APP_NAME.equals(Messages.WINDOW_TITLE), "Masaustu pencere adi ProductText ile uyusmuyor.");
        check((turkish ? "Kisiler" : "Contacts").equals(Messages.NAV_CONTACTS), "Kisiler navigasyon metni yerellestirme hatasi.");
        check((turkish ? "Ayarlar" : "Settings").equals(Messages.NAV_SETTINGS), "Ayarlar navigasyon metni yerellestirme hatasi.");
        check((turkish ? "Hata" : "Error").equals(Messages.ERROR_TITLE), "Hata basligi yerellestirme hatasi.");
        check((turkish ? "Yedekleme" : "Backup").equals(Messages.BACKUP_TITLE), "Yedekleme basligi yerellestirme hatasi.");
    }

    private static void testContactMethodLabelLocalization() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            check("Mobile".equals(ContactMethodText.displayLabel("Cep")), "Cep etiketi Ingilizce Mobile olmadi.");
            check("Email".equals(ContactMethodText.displayLabel("E-posta")), "E-posta etiketi Ingilizce Email olmadi.");
            check("Cep".equals(ContactMethodText.storageLabel("Cep", "Mobile", "Cep")), "Localized display canonical Cep storage degerini bozdu.");
            check("VIP".equals(ContactMethodText.storageLabel("Cep", "VIP", "Cep")), "Kullanici ozel etiketi saklanmadi.");

            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            check("Cep".equals(ContactMethodText.displayLabel("Cep")), "Cep etiketi Turkce locale'de degisti.");
            check("E-posta".equals(ContactMethodText.displayLabel("Email")), "English Email etiketi Turkce display'e cevrilmedi.");
        } finally {
            Locale.setDefault(original);
        }
    }

    private static void testPhoneCountryLocalizationAndDefault() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            PhoneCountryCode germany = new PhoneCountryCode("DE", "Almanya", "+49");
            check("Germany (+49)".equals(PhoneCountryCodeText.display(germany)), "DE ulke adi Ingilizce gosterilemedi.");
            check("Other / Custom Code".equals(PhoneCountryCodeText.display(new PhoneCountryCode("CUSTOM", "Diger / Ozel Kod", ""))), "Custom ulke kodu Ingilizce yerellestirilemedi.");

            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            check(PhoneCountryCodeText.display(germany).startsWith("Almanya"), "DE ulke adi Turkce gosterilemedi.");
        } finally {
            Locale.setDefault(original);
        }

        String localeCountry = original.getCountry() == null ? "" : original.getCountry().toUpperCase(Locale.ROOT);
        boolean supported = PhoneCountryCodeCatalog.values().stream()
                .anyMatch(value -> value.isoCode().equals(localeCountry));
        String expectedIso = supported
                ? localeCountry
                : ("tr".equalsIgnoreCase(original.getLanguage()) ? "TR" : "US");
        check(expectedIso.equals(AppConfig.DEFAULT_PHONE_COUNTRY_ISO), "AUTO telefon ulke ISO destek/fallback kuraliyla uyusmuyor.");
        check(!"AUTO".equals(AppConfig.DEFAULT_PHONE_COUNTRY_ISO), "AUTO telefon ulke ISO runtime degerine cozulmedi.");
    }

    private static void testBackupIncludesCommittedWalData(Path root) throws Exception {
        Files.createDirectories(root);
        Path database = root.resolve("source.db");
        SQLiteConnectionProvider provider = new SQLiteConnectionProvider(database, 5000);
        try (Connection connection = provider.open(); Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA wal_autocheckpoint = 0");
            statement.execute("CREATE TABLE backup_probe(value TEXT NOT NULL)");
            statement.execute("INSERT INTO backup_probe(value) VALUES('wal-visible')");

            Path snapshot = root.resolve("snapshot.db");
            SQLiteBackupTool backupTool = new SQLiteBackupTool(provider);
            backupTool.createVerifiedSnapshot(snapshot);
            backupTool.verifyIntegrity(snapshot);

            try (Connection backup = DriverManager.getConnection("jdbc:sqlite:" + snapshot.toAbsolutePath());
                 Statement query = backup.createStatement();
                 ResultSet rows = query.executeQuery("SELECT value FROM backup_probe")) {
                check(rows.next(), "Backup probe satiri eksik.");
                check("wal-visible".equals(rows.getString(1)), "WAL verisi yedege girmedi.");
            }
        }
    }

    private static void testWeeklyBackupRetention(Path root) throws Exception {
        Files.createDirectories(root);
        BackupRepository repository = new BackupRepository(root, null);
        Instant now = Instant.now();

        for (int index = 0; index < 6; index++) {
            Path backup = root.resolve(String.format("telefon-rehberi-202609%02d-120000-000.db", 10 + index));
            Files.writeString(backup, "backup-" + index, StandardCharsets.UTF_8);
            Files.setLastModifiedTime(backup, FileTime.from(now.minus(Duration.ofDays(5 - index))));
        }

        check(repository.hasBackupWithin(Duration.ofDays(7)),
                "Son 7 gun icindeki yedek haftalik otomatik yedegi engellemedi.");
        int deleted = repository.prune(5);
        check(deleted == 1, "5 yedek retention fazladan tek eski yedegi silmedi.");
        var backups = repository.listBackups();
        check(backups.size() == 5, "Retention sonrasi tam 5 yedek kalmadi.");
        check(backups.get(0).getFileName().toString().contains("20260915"),
                "Retention en yeni yedegi korumadi.");

        for (Path backup : backups) {
            Files.setLastModifiedTime(backup, FileTime.from(now.minus(Duration.ofDays(8))));
        }
        check(!repository.hasBackupWithin(Duration.ofDays(7)),
                "7 gunden eski yedek haftalik otomatik yedegi gereksiz engelledi.");
    }

    private static void testSyncUuidIsIdempotent(Path root) throws Exception {
        Files.createDirectories(root);
        SQLiteConnectionProvider provider = new SQLiteConnectionProvider(root.resolve("contacts.db"), 5000);
        ContactRepository repository = new ContactRepository(provider);

        Contact desktop = repository.insert(draft("Ahmet", "+905551111111", "ahmet@example.com"));
        check(!desktop.syncUuid().isBlank(), "Yeni kisi sync UUID almadi.");
        UUID.fromString(desktop.syncUuid());

        Contact pulledBack = repository.upsertExternal(
                draft("Ahmet Guncel", "+905551111111", "ahmet@example.com"),
                "android-a", "943", desktop.syncUuid()
        );
        check(pulledBack.id() == desktop.id(), "Sync UUID ayni kisiyi bulmadi.");
        check(repository.count() == 1, "Sync UUID upsert duplicate uretti.");

        Contact repeated = repository.upsertExternal(
                draft("Ahmet Tekrar", "+905551111111", "ahmet@example.com"),
                "android-a", "943", ""
        );
        check(repeated.id() == desktop.id(), "Device/external fallback ayni kisiyi bulmadi.");
        check(repository.count() == 1, "Tekrarlanan mobile push duplicate uretti.");

        ContactDraft reinstallDraft = draft("Ahmet Reinstall", "+905551111111", "ahmet@example.com");
        Contact reinstallTarget = repository.findExternalTarget(
                reinstallDraft, "android-reinstalled", "1201", ""
        ).orElseThrow();
        check(reinstallTarget.id() == desktop.id(), "Natural identity hedef cozumleme mevcut kisiyi bulmadi.");
        Contact reinstalledDevice = repository.upsertExternal(
                reinstallDraft, "android-reinstalled", "1201", ""
        );
        check(reinstalledDevice.id() == desktop.id(), "Unique telefon/e-posta fallback app reinstall sonrasinda kisiyi bulmadi.");
        check(repository.count() == 1, "App reinstall natural identity fallback duplicate uretti.");

        Contact mobileNew = repository.upsertExternal(
                draft("Ayse", "+905552222222", "ayse@example.com"),
                "android-a", "944", ""
        );
        Contact mobileNewAgain = repository.upsertExternal(
                draft("Ayse Guncel", "+905552222222", "ayse@example.com"),
                "android-a", "944", mobileNew.syncUuid()
        );
        check(mobileNew.id() == mobileNewAgain.id(), "Yeni mobil kisi canonical UUID ile eslesmedi.");
        check(repository.count() == 2, "Mobil idempotency toplam kisi sayisini bozdu.");

        repository.transferSyncIdentities(mobileNew.id(), desktop.id());
        Contact aliasResolved = repository.findBySyncUuid(mobileNew.syncUuid()).orElseThrow();
        check(aliasResolved.id() == desktop.id(), "Merge sonrasi sync UUID alias canonical kisiye gitmedi.");
        Contact externalAliasResolved = repository.findByExternal("android-a", "944").orElseThrow();
        check(externalAliasResolved.id() == desktop.id(), "Merge sonrasi external alias canonical kisiye gitmedi.");

        try (Connection connection = provider.open();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT value FROM schema_meta WHERE key='schema_version'")) {
            check(resultSet.next() && "4".equals(resultSet.getString(1)), "Schema version metadata eksik.");
        }
    }

    private static void testHistoryRetention(Path root) throws Exception {
        Files.createDirectories(root);
        SQLiteConnectionProvider provider = new SQLiteConnectionProvider(root.resolve("history.db"), 5000);
        ContactHistoryRepository repository = new ContactHistoryRepository(provider, 3);
        for (int index = 1; index <= 5; index++) {
            repository.add(1L, "UPDATE_" + index, "Test", "snapshot-" + index);
        }
        var values = repository.findRecent(100);
        check(values.size() == 3, "History retention limiti uygulanmadi.");
        check("UPDATE_5".equals(values.get(0).action()), "En yeni history kaydi korunmadi.");
        check("UPDATE_3".equals(values.get(2).action()), "History prune yanlis kaydi korudu.");
    }

    private static void testMobileRequestLimitAndMapping() throws Exception {
        MobileSyncFormTool tool = new MobileSyncFormTool(new FormCodec(), new ContactMethodCodec());
        String syncUuid = UUID.randomUUID().toString();
        String body = form("name", "Test Kisi") + "&" + form("phone", "+905553333333")
                + "&" + form("device_id", "android-test") + "&" + form("external_id", "55")
                + "&" + form("sync_uuid", syncUuid);
        var request = tool.decode(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)), 4096);
        check("android-test".equals(request.deviceId()), "Device ID decode hatali.");
        check("55".equals(request.externalId()), "External ID decode hatali.");
        check(syncUuid.equals(request.syncUuid()), "Sync UUID decode hatali.");

        boolean rejected = false;
        try {
            tool.decode(new ByteArrayInputStream(new byte[128]), 64);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "Mobil request body limiti uygulanmadi.");
    }

    private static void testDueReminderSelection(Path root) throws Exception {
        Files.createDirectories(root);
        SQLiteConnectionProvider provider = new SQLiteConnectionProvider(
                root.resolve("contacts.db"), AppConfig.SQLITE_BUSY_TIMEOUT_MILLISECONDS
        );
        ContactRepository repository = new ContactRepository(provider);
        ContactService service = new ContactService(
                repository, new ContactHistoryRepository(provider), new ContactSnapshotCodec()
        );

        LocalDate today = LocalDate.now();
        service.addContact(new ContactDraft(
                "Birthday Due", "+905550000001", "", "", "", "", "", "", today.toString(), "", "", "", "", "", "", "", "", false,
                List.of(), List.of(), null, ReminderLeadTime.SAME_DAY, List.of(), KeepInTouchInterval.DISABLED, ""
        ));
        service.addContact(new ContactDraft(
                "Keep In Touch Due", "+905550000002", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", false,
                List.of(), List.of(), null, ReminderLeadTime.DISABLED, List.of(), KeepInTouchInterval.MONTHLY,
                today.minusDays(31).toString()
        ));
        service.addContact(new ContactDraft(
                "No Reminder", "+905550000003", "", "", "", "", "", "", today.toString(), "", "", "", "", "", "", "", "", false,
                List.of(), List.of(), null, ReminderLeadTime.DISABLED, List.of(), KeepInTouchInterval.DISABLED, ""
        ));

        var due = service.listDueReminderContacts();
        check(due.size() == 2, "Aktif reminder kisi secimi beklenen 2 kaydi dondurmedi.");
        check(due.stream().anyMatch(contact -> "Birthday Due".equals(contact.name())), "Dogum gunu reminder kaydi eksik.");
        check(due.stream().anyMatch(contact -> "Keep In Touch Due".equals(contact.name())), "Keep In Touch reminder kaydi eksik.");
    }

    private static void testUserPreferencesRoundTrip(Path root) throws Exception {
        Files.createDirectories(root);
        Path preferences = root.resolve("preferences.yml");
        UserPreferencesRepository repository = new UserPreferencesRepository(new SimpleYamlRepository(), preferences);
        AppSettings expected = new AppSettings(
                ThemeMode.DARK, LocaleText.LANGUAGE_ENGLISH_CODE, "contacts", true, 1280, 800, true, true,
                true, 5, true, 18787, false, true
        );
        repository.save(expected);
        AppSettings actual = repository.load();
        check(actual.theme() == expected.theme(), "Tema preference round-trip hatali.");
        check(actual.backupRetention() == 5, "Yedek retention preference 5 olmadi.");
        check(LocaleText.LANGUAGE_ENGLISH_CODE.equals(actual.languageCode()), "Dil preference round-trip hatali.");
        check(new LanguagePreferenceTool().readLanguageCode(preferences, root.resolve("legacy-preferences.yml"))
                .equals(LocaleText.LANGUAGE_ENGLISH_CODE), "Bootstrap dil tercihi okunamadi.");
        check(actual.mobileSyncEnabled(), "Mobile sync preference saklanmadi.");
        check(actual.mobileSyncPort() == 18787, "Mobile sync port preference saklanmadi.");
        check(!actual.updateEnabled(), "Update preference saklanmadi.");
        check(actual.reminderNotificationsEnabled(), "Reminder notification preference saklanmadi.");

        Files.writeString(preferences, "backup_retention: \"14\"\n", StandardCharsets.UTF_8);
        AppSettings legacy = repository.load();
        check(legacy.backupRetention() == 5, "Legacy backup_retention 14 degeri 5'e clamp edilmedi.");
        check(!legacy.reminderNotificationsEnabled(), "Legacy preferences icin reminder notification varsayilani kapali olmali.");
    }

    private static ContactDraft draft(String name, String phone, String email) {
        return new ContactDraft(
                name, phone, "", "", email, "", "", "", "", "", "", "", "", "", "", "", "", false
        );
    }

    private static String form(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    private static void deleteRecursively(Path root) throws Exception {
        if (Files.notExists(root)) return;
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
        }
    }
}
