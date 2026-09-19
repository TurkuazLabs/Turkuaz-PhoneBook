// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/Main.java
// # 📌 Amac: Uygulama katmanlarini olusturur ve tum servis/repository/tool bagimliliklarini baslatir.
// # 📌 Bootstrap - Java
// # Version: 2.39.0
// # Aciklama: XDG/Contacts platform yerlesimi, SQLite, hatirlatmalar, mobil API ve GUI wiring islemlerini yapar.
// # Bagimli Oldugu Katman: Controller | Service | Repository | Tool | View
package com.turkuazlabs.telefonrehberi;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.config.UserDataPathResolver;
import com.turkuazlabs.telefonrehberi.controllers.MobileSyncController;
import com.turkuazlabs.telefonrehberi.controllers.PhoneBookController;
import com.turkuazlabs.telefonrehberi.language.LocaleText;
import com.turkuazlabs.telefonrehberi.models.AppSettings;
import com.turkuazlabs.telefonrehberi.models.SyncServerInfo;
import com.turkuazlabs.telefonrehberi.repositories.BackupRepository;
import com.turkuazlabs.telefonrehberi.repositories.ContactHistoryRepository;
import com.turkuazlabs.telefonrehberi.repositories.ContactRepository;
import com.turkuazlabs.telefonrehberi.repositories.GroupRepository;
import com.turkuazlabs.telefonrehberi.repositories.SmartListRepository;
import com.turkuazlabs.telefonrehberi.repositories.SavedContactViewRepository;
import com.turkuazlabs.telefonrehberi.repositories.TagRepository;
import com.turkuazlabs.telefonrehberi.repositories.UserPreferencesRepository;
import com.turkuazlabs.telefonrehberi.repositories.UserDataMigrationRepository;
import com.turkuazlabs.telefonrehberi.services.BackupService;
import com.turkuazlabs.telefonrehberi.services.BulkUndoService;
import com.turkuazlabs.telefonrehberi.services.ContactService;
import com.turkuazlabs.telefonrehberi.services.ContactFilterService;
import com.turkuazlabs.telefonrehberi.services.ContactQuickActionService;
import com.turkuazlabs.telefonrehberi.services.ExternalLinkService;
import com.turkuazlabs.telefonrehberi.services.GroupService;
import com.turkuazlabs.telefonrehberi.services.ImportExportService;
import com.turkuazlabs.telefonrehberi.services.MaintenanceService;
import com.turkuazlabs.telefonrehberi.services.MigrationService;
import com.turkuazlabs.telefonrehberi.services.MobileSyncService;
import com.turkuazlabs.telefonrehberi.services.SavedContactViewService;
import com.turkuazlabs.telefonrehberi.services.SettingsService;
import com.turkuazlabs.telefonrehberi.services.SmartListService;
import com.turkuazlabs.telefonrehberi.services.TagService;
import com.turkuazlabs.telefonrehberi.services.ThemeService;
import com.turkuazlabs.telefonrehberi.services.UserDataMigrationService;
import com.turkuazlabs.telefonrehberi.tools.BrandAssetTool;
import com.turkuazlabs.telefonrehberi.tools.ContactSnapshotCodec;
import com.turkuazlabs.telefonrehberi.tools.DesktopActionTool;
import com.turkuazlabs.telefonrehberi.tools.CsvContactTool;
import com.turkuazlabs.telefonrehberi.tools.FlatLafThemeTool;
import com.turkuazlabs.telefonrehberi.tools.FormCodec;
import com.turkuazlabs.telefonrehberi.tools.ContactMethodCodec;
import com.turkuazlabs.telefonrehberi.tools.MobileSyncFormTool;
import com.turkuazlabs.telefonrehberi.tools.SQLiteBackupTool;
import com.turkuazlabs.telefonrehberi.tools.LanAddressTool;
import com.turkuazlabs.telefonrehberi.tools.LanguagePreferenceTool;
import com.turkuazlabs.telefonrehberi.tools.LegacyTsvReader;
import com.turkuazlabs.telefonrehberi.tools.SQLiteConnectionProvider;
import com.turkuazlabs.telefonrehberi.tools.SyncTokenStore;
import com.turkuazlabs.telefonrehberi.tools.VCardContactTool;
import com.turkuazlabs.telefonrehberi.views.ApiJsonView;
import com.turkuazlabs.telefonrehberi.views.PhoneBookFrame;

import javax.swing.SwingUtilities;
import java.awt.Image;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        LanguagePreferenceTool languagePreferenceTool = new LanguagePreferenceTool();
        LocaleText.applyLanguage(languagePreferenceTool.readLanguageCode(
                UserDataPathResolver.resolveConfigRoot().resolve("preferences.yml"),
                UserDataPathResolver.resolveDataRoot().resolve("config").resolve("preferences.yml")
        ));

        UserDataMigrationService userDataMigrationService = new UserDataMigrationService(new UserDataMigrationRepository());
        userDataMigrationService.migratePortableUserDataIfNeeded();

        UserPreferencesRepository preferencesRepository = new UserPreferencesRepository();
        ThemeService themeService = new ThemeService(preferencesRepository, new FlatLafThemeTool());
        themeService.applySavedTheme();

        SQLiteConnectionProvider connectionProvider = new SQLiteConnectionProvider();
        ContactRepository contactRepository = new ContactRepository(connectionProvider);
        ContactHistoryRepository historyRepository = new ContactHistoryRepository(connectionProvider);
        GroupRepository groupRepository = new GroupRepository(connectionProvider);
        TagRepository tagRepository = new TagRepository(connectionProvider);
        SmartListRepository smartListRepository = new SmartListRepository(connectionProvider);
        SavedContactViewRepository savedContactViewRepository = new SavedContactViewRepository();

        MigrationService migrationService = new MigrationService(contactRepository, new LegacyTsvReader());
        int migratedContactCount = migrationService.migrateLegacyContactsIfNeeded();

        ContactService contactService = new ContactService(contactRepository, historyRepository, new ContactSnapshotCodec());
        ContactFilterService contactFilterService = new ContactFilterService(contactRepository, groupRepository, tagRepository);
        SavedContactViewService savedContactViewService = new SavedContactViewService(savedContactViewRepository);
        DesktopActionTool desktopActionTool = new DesktopActionTool();
        ContactQuickActionService contactQuickActionService = new ContactQuickActionService(desktopActionTool);
        ExternalLinkService externalLinkService = new ExternalLinkService(desktopActionTool);
        GroupService groupService = new GroupService(groupRepository);
        TagService tagService = new TagService(tagRepository);
        BulkUndoService bulkUndoService = new BulkUndoService(contactService, groupService, tagService, AppConfig.BULK_HISTORY_LIMIT);
        SmartListService smartListService = new SmartListService(smartListRepository, contactService);
        MaintenanceService maintenanceService = new MaintenanceService(contactService, groupService, tagService);
        ImportExportService importExportService = new ImportExportService(contactService, new CsvContactTool(), new VCardContactTool());
        BackupService backupService = new BackupService(new BackupRepository(new SQLiteBackupTool(connectionProvider)));
        SettingsService settingsService = new SettingsService(preferencesRepository, themeService);

        MobileSyncService mobileSyncService = new MobileSyncService(contactService, new SyncTokenStore(), new LanAddressTool());
        MobileSyncController mobileSyncController = new MobileSyncController(
                mobileSyncService, new MobileSyncFormTool(new FormCodec(), new ContactMethodCodec()), new ApiJsonView()
        );

        AppSettings startupSettings = settingsService.loadSettings();
        if (startupSettings.mobileSyncEnabled()) {
            try {
                mobileSyncController.start(startupSettings.mobileSyncPort());
            } catch (IllegalStateException ignored) {
                // GUI serverInfo ile baslatma hatasini pasif durum olarak gosterir.
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(mobileSyncController::stop, "telefonrehberi-sync-shutdown"));
        SyncServerInfo syncServerInfo = mobileSyncController.serverInfo();
        Image appIcon = new BrandAssetTool().loadAppIcon().orElse(null);

        SwingUtilities.invokeLater(() -> startGui(
                contactService, bulkUndoService, contactFilterService, savedContactViewService, contactQuickActionService, externalLinkService, themeService, settingsService, backupService, groupService, tagService,
                smartListService, maintenanceService, importExportService,
                syncServerInfo, migratedContactCount, appIcon
        ));
    }

    private static void startGui(
            ContactService contactService,
            BulkUndoService bulkUndoService,
            ContactFilterService contactFilterService,
            SavedContactViewService savedContactViewService,
            ContactQuickActionService contactQuickActionService,
            ExternalLinkService externalLinkService,
            ThemeService themeService,
            SettingsService settingsService,
            BackupService backupService,
            GroupService groupService,
            TagService tagService,
            SmartListService smartListService,
            MaintenanceService maintenanceService,
            ImportExportService importExportService,
            SyncServerInfo syncServerInfo,
            int migratedContactCount,
            Image appIcon
    ) {
        PhoneBookFrame view = new PhoneBookFrame(appIcon);
        PhoneBookController controller = new PhoneBookController(
                contactService, bulkUndoService, contactFilterService, savedContactViewService, contactQuickActionService, externalLinkService, themeService, settingsService, backupService,
                groupService, tagService, smartListService, maintenanceService, importExportService,
                view, syncServerInfo, migratedContactCount
        );
        controller.initialize();
    }
}
