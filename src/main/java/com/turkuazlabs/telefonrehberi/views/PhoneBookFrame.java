// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/views/PhoneBookFrame.java
// # 📌 Amac: FlatLaf tabanli modern masaustu telefon rehberi GUI'sini sunar.
// # 📌 View - Java
// # Version: 2.39.0
// # Aciklama: Kompakt kisi profili, activity timeline, hatirlatmalar, masaustu bildirim tercihi ve gruplandirilmis navigasyon sunar.
// # Bagimli Oldugu Katman: View | Model | Config | Language
package com.turkuazlabs.telefonrehberi.views;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.config.UiConfig;
import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;
import com.turkuazlabs.telefonrehberi.language.LocaleText;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.language.ContactSortText;
import com.turkuazlabs.telefonrehberi.language.ReminderText;
import com.turkuazlabs.telefonrehberi.models.AppSettings;
import com.turkuazlabs.telefonrehberi.models.Contact;
import com.turkuazlabs.telefonrehberi.models.ContactActivityFeed;
import com.turkuazlabs.telefonrehberi.models.ContactActivityFilter;
import com.turkuazlabs.telefonrehberi.models.ContactDraft;
import com.turkuazlabs.telefonrehberi.models.ContactMethod;
import com.turkuazlabs.telefonrehberi.models.ContactReminderSummary;
import com.turkuazlabs.telefonrehberi.models.ImportantDate;
import com.turkuazlabs.telefonrehberi.models.ImportantDateType;
import com.turkuazlabs.telefonrehberi.models.KeepInTouchInterval;
import com.turkuazlabs.telefonrehberi.models.ReminderLeadTime;
import com.turkuazlabs.telefonrehberi.models.ContactSort;
import com.turkuazlabs.telefonrehberi.models.DuplicateCandidate;
import com.turkuazlabs.telefonrehberi.models.ContactFileFormat;
import com.turkuazlabs.telefonrehberi.models.ContactFilter;
import com.turkuazlabs.telefonrehberi.models.ContactFilterOption;
import com.turkuazlabs.telefonrehberi.models.ContactFilterOptions;
import com.turkuazlabs.telefonrehberi.models.GroupRecord;
import com.turkuazlabs.telefonrehberi.models.HistoryEntry;
import com.turkuazlabs.telefonrehberi.models.SmartList;
import com.turkuazlabs.telefonrehberi.models.SmartListDraft;
import com.turkuazlabs.telefonrehberi.models.SavedContactView;
import com.turkuazlabs.telefonrehberi.models.TagRecord;
import com.turkuazlabs.telefonrehberi.models.DashboardSummary;
import com.turkuazlabs.telefonrehberi.models.SyncServerInfo;
import com.turkuazlabs.telefonrehberi.models.ThemeMode;
import com.turkuazlabs.telefonrehberi.tools.BrandAssetTool;
import com.turkuazlabs.telefonrehberi.tools.ContactPhotoTool;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PhoneBookFrame extends JFrame {
    private final Image appIcon;
    private final BrandAssetTool brandAssetTool = new BrandAssetTool();
    private final Image lightBrandIcon;
    private final Image darkBrandIcon;
    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(pageLayout);
    private final List<JButton> navigationButtons = new ArrayList<>();
    private final Map<String, JButton> navigationByPage = new HashMap<>();
    private final List<CollapsibleNavigationSection> navigationSections = new ArrayList<>();
    private final Map<JButton, CollapsibleNavigationSection> navigationSectionByButton = new HashMap<>();
    private JButton activeNavigationButton;
    private JButton favoritesNavigationButton;

    private final JTextField searchField = new JTextField(UiConfig.SEARCH_COLUMNS);
    private final JToggleButton favoritesOnlyCheckBox = new JToggleButton(Messages.FAVORITES_ONLY);
    private final JComboBox<String> companyFilterCombo = new JComboBox<>();
    private final JComboBox<String> cityFilterCombo = new JComboBox<>();
    private final JComboBox<String> categoryFilterCombo = new JComboBox<>();
    private final JComboBox<ContactFilterOption> groupFilterCombo = new JComboBox<>();
    private final JComboBox<ContactFilterOption> tagFilterCombo = new JComboBox<>();
    private final JComboBox<ContactSort> sortFilterCombo = new JComboBox<>(ContactSort.values());
    private final JComboBox<String> savedViewCombo = new JComboBox<>();
    private final JButton saveSavedViewButton = ModernButtons.chip(Messages.SAVED_VIEW_SAVE_BUTTON);
    private final JButton manageSavedViewButton = ModernButtons.chip(Messages.SAVED_VIEW_MANAGE_BUTTON);
    private final JPopupMenu savedViewMenu = new JPopupMenu();
    private final JMenuItem renameSavedViewMenuItem = new JMenuItem(Messages.SAVED_VIEW_RENAME_BUTTON);
    private final JMenuItem copySavedViewMenuItem = new JMenuItem(Messages.SAVED_VIEW_COPY_BUTTON);
    private final JMenuItem defaultSavedViewMenuItem = new JMenuItem(Messages.SAVED_VIEW_SET_DEFAULT_BUTTON);
    private final JMenuItem deleteSavedViewMenuItem = new JMenuItem(Messages.SAVED_VIEW_DELETE_BUTTON);
    private final JButton filterToggleButton = ModernButtons.chip(Messages.FILTERS_BUTTON);
    private final JButton savedViewPickerButton = ModernButtons.chip(Messages.SAVED_VIEW_PICKER_BUTTON);
    private final JButton contactBrowserMoreButton = ModernButtons.chip(Messages.CONTACT_BROWSER_MORE_BUTTON);
    private final JPanel filterDrawerPanel = new JPanel();
    private final FilterChipPanel activeFilterChipPanel = new FilterChipPanel();
    private boolean updatingFilterControls;
    private boolean updatingSavedViewControls;
    private String defaultSavedViewName = "";

    private final JTextField nameField = field();
    private final ContactMethodsPanel phoneMethodsPanel = new ContactMethodsPanel(ContactMethod.PHONE, ContactMethod.LABEL_MOBILE);
    private final ContactMethodsPanel emailMethodsPanel = new ContactMethodsPanel(ContactMethod.EMAIL, ContactMethod.LABEL_EMAIL);
    private final ContactPhotoTool photoTool = new ContactPhotoTool();
    private byte[] pendingPhotoData;
    private final JTextField companyField = field();
    private final JTextField jobTitleField = field();
    private final JTextField birthdayField = field();
    private final JComboBox<ReminderLeadTime> birthdayReminderCombo = new JComboBox<>(ReminderLeadTime.values());
    private final ImportantDatesPanel importantDatesPanel = new ImportantDatesPanel();
    private final JComboBox<KeepInTouchInterval> keepInTouchCombo = new JComboBox<>(KeepInTouchInterval.values());
    private final JTextField lastContactedField = field();
    private final JTextField websiteField = field();
    private final JTextField categoryField = field();
    private final JTextArea addressArea = area();
    private final JTextField cityField = field();
    private final JTextField districtField = field();
    private final JTextField postalCodeField = field();
    private final JTextField countryField = field();
    private final JTextArea notesArea = area();
    private final JCheckBox favoriteCheckBox = new JCheckBox(Messages.FAVORITE_LABEL);

    private final JButton addButton = primaryButton(Messages.ADD_BUTTON);
    private final JButton deleteButton = dangerButton(Messages.DELETE_BUTTON);
    private final JButton cancelButton = secondaryButton(Messages.CANCEL_BUTTON);
    private final JButton clearButton = secondaryButton(Messages.CLEAR_BUTTON);
    private final JButton refreshButton = secondaryButton(Messages.REFRESH_BUTTON);
    private final JButton saveSettingsButton = primaryButton(Messages.SAVE_SETTINGS_BUTTON);
    private final JButton backupNowButton = primaryButton(Messages.BACKUP_NOW_BUTTON);
    private final JButton backupCleanupButton = secondaryButton(Messages.BACKUP_CLEANUP_BUTTON);
    private final JButton photoActionsButton = secondaryButton(Messages.PHOTO_ACTIONS_BUTTON);
    private final JButton quickCallButton = quickActionButton(Messages.QUICK_CALL_BUTTON, QuickGlyph.CALL);
    private final JButton quickWhatsAppButton = quickActionButton(Messages.QUICK_WHATSAPP_BUTTON, QuickGlyph.WHATSAPP);
    private final JButton quickEmailButton = quickActionButton(Messages.QUICK_EMAIL_BUTTON, QuickGlyph.EMAIL);
    private final JButton quickCopyButton = quickActionButton(Messages.QUICK_COPY_BUTTON, QuickGlyph.COPY);
    private final ResponsiveActionPanel quickActionsPanel = new ResponsiveActionPanel(
            UiConfig.QUICK_ACTION_WIDE_COLUMNS,
            UiConfig.QUICK_ACTION_COMPACT_COLUMNS,
            UiConfig.QUICK_ACTION_COMPACT_BREAKPOINT,
            UiConfig.QUICK_ACTION_GAP,
            UiConfig.QUICK_ACTION_GAP
    );
    private final JButton editContactButton = secondaryButton(Messages.EDIT_CONTACT_BUTTON);
    private final CardLayout contactModeLayout = new CardLayout();
    private final JPanel contactModeContainer = new JPanel(contactModeLayout);
    private JPanel contactProfileSummaryPanel;
    private final JLabel bulkSelectionCountLabel = new JLabel();
    private final JLabel bulkSelectionSummaryLabel = new JLabel();
    private final JButton bulkCompanyButton = secondaryButton(Messages.BULK_COMPANY_BUTTON);
    private final JButton bulkCategoryButton = secondaryButton(Messages.BULK_CATEGORY_BUTTON);
    private final JButton bulkAddGroupButton = secondaryButton(Messages.BULK_ADD_GROUP_BUTTON);
    private final JButton bulkRemoveGroupButton = secondaryButton(Messages.BULK_REMOVE_GROUP_BUTTON);
    private final JButton bulkAddTagButton = secondaryButton(Messages.BULK_ADD_TAG_BUTTON);
    private final JButton bulkRemoveTagButton = secondaryButton(Messages.BULK_REMOVE_TAG_BUTTON);
    private final JButton bulkFavoriteButton = primaryButton(Messages.BULK_FAVORITE_BUTTON);
    private final JButton bulkUnfavoriteButton = secondaryButton(Messages.BULK_UNFAVORITE_BUTTON);
    private final JButton bulkExportVcfButton = secondaryButton(Messages.BULK_EXPORT_VCF_BUTTON);
    private final JButton bulkExportCsvButton = secondaryButton(Messages.BULK_EXPORT_CSV_BUTTON);
    private final JButton bulkTrashButton = dangerButton(Messages.BULK_TRASH_BUTTON);

    private final DefaultListModel<Contact> contactListModel = new DefaultListModel<>();
    private final ContactListCellRenderer contactListRenderer = new ContactListCellRenderer();
    private final JList<Contact> contactList = new JList<>(contactListModel);
    private final JLabel contactCountLabel = new JLabel(String.format(Messages.STATUS_COUNT_FORMAT, 0));
    private final JLabel statusLabel = new JLabel(Messages.STATUS_READY, SwingConstants.LEFT);
    private final JLabel syncStatusLabel = new JLabel(Messages.STATUS_SYNC_STOPPED, SwingConstants.RIGHT);
    private final JLabel undoNoticeLabel = new JLabel();
    private final JButton undoNoticeButton = ModernButtons.chip(Messages.UNDO_BUTTON);
    private final JButton redoNoticeButton = ModernButtons.chip(Messages.REDO_BUTTON);
    private final UndoNoticePanel undoNoticePanel = new UndoNoticePanel();
    private Timer undoNoticeTimer;

    private final JLabel detailNameLabel = new JLabel(Messages.DETAIL_EMPTY_TITLE);
    private final JLabel detailCompanyLabel = new JLabel(Messages.DETAIL_EMPTY_SUBTITLE);
    private final JLabel detailPhoneLabel = new JLabel();
    private final JLabel detailEmailLabel = new JLabel();
    private final JLabel detailCategoryLabel = new JLabel();
    private final JLabel detailBirthdayLabel = new JLabel();
    private final JLabel detailBirthdayReminderLabel = new JLabel();
    private final JLabel detailImportantDatesLabel = new JLabel();
    private final JLabel detailKeepInTouchLabel = new JLabel();
    private final JLabel detailReminderStatusLabel = new JLabel();
    private final JLabel detailCompanyInfoLabel = new JLabel();
    private final JLabel detailJobTitleInfoLabel = new JLabel();
    private final JLabel detailWebsiteInfoLabel = new JLabel();
    private final JLabel detailAddressInfoLabel = new JLabel();
    private final JLabel detailCityInfoLabel = new JLabel();
    private final JLabel detailDistrictInfoLabel = new JLabel();
    private final JLabel detailPostalCodeInfoLabel = new JLabel();
    private final JLabel detailCountryInfoLabel = new JLabel();
    private final JLabel detailNotesInfoLabel = new JLabel();
    private final ModernPillLabel detailFavoriteLabel = new ModernPillLabel(Messages.FAVORITE_BADGE);
    private final InitialsAvatarPanel avatarPanel = new InitialsAvatarPanel();
    private final JPanel detailChipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    private final JPanel contactActivityTimelinePanel = new JPanel();
    private final JToggleButton activityAllButton = new JToggleButton();
    private final JToggleButton activityChangesButton = new JToggleButton();
    private final JToggleButton activityTransferButton = new JToggleButton();
    private final JToggleButton activityRestoreButton = new JToggleButton();
    private ContactActivityFilter contactActivityFilter = ContactActivityFilter.ALL;
    private Map<Long, List<GroupRecord>> groupMemberships = Map.of();
    private Map<Long, List<TagRecord>> tagMemberships = Map.of();

    private final JLabel totalValue = statValue();
    private final JLabel favoriteValue = statValue();
    private final JLabel birthdayValue = statValue();
    private final JLabel followUpValue = statValue();
    private final JLabel companyValue = statValue();
    private final JLabel cityValue = statValue();
    private final JLabel dashboardSyncStatusLabel = new JLabel(Messages.DASHBOARD_SYNC_STOPPED, SwingConstants.RIGHT);
    private final JLabel dashboardBackupStatusLabel = new JLabel(Messages.DASHBOARD_BACKUP_NONE, SwingConstants.RIGHT);

    private final JLabel syncAddressLabel = managementValueLabel();
    private final JLabel syncTokenLabel = managementValueLabel();
    private final JLabel syncPageStatusLabel = managementValueLabel();
    private final JLabel backupCountLabel = new JLabel(Messages.BACKUP_NONE);
    private final JLabel backupLastLabel = new JLabel(Messages.BACKUP_NONE);
    private final DefaultListModel<String> backupListModel = new DefaultListModel<>();
    private final JList<String> backupList = new JList<>(backupListModel);
    private final DefaultListModel<GroupRecord> groupListModel = new DefaultListModel<>();
    private final JList<GroupRecord> groupList = new JList<>(groupListModel);
    private List<GroupRecord> groups = List.of();
    private final DefaultListModel<TagRecord> tagListModel = new DefaultListModel<>();
    private final JList<TagRecord> tagList = new JList<>(tagListModel);
    private List<TagRecord> tags = List.of();
    private final DefaultListModel<SmartList> smartListModel = new DefaultListModel<>();
    private final JList<SmartList> smartList = new JList<>(smartListModel);
    private List<SmartList> smartLists = List.of();

    private final JLabel groupDetailNameLabel = managementTitleLabel();
    private final JLabel groupDetailColorLabel = managementValueLabel();
    private final JLabel groupDetailCountLabel = managementValueLabel();
    private final JLabel groupDetailContactLabel = managementValueLabel();
    private final JLabel tagDetailNameLabel = managementTitleLabel();
    private final JLabel tagDetailColorLabel = managementValueLabel();
    private final JLabel tagDetailCountLabel = managementValueLabel();
    private final JLabel tagDetailContactLabel = managementValueLabel();
    private final JLabel smartDetailNameLabel = managementTitleLabel();
    private final JLabel smartDetailFieldLabel = managementValueLabel();
    private final JLabel smartDetailOperatorLabel = managementValueLabel();
    private final JLabel smartDetailValueLabel = managementValueLabel();
    private final DefaultListModel<HistoryEntry> historyListModel = new DefaultListModel<>();
    private final JList<HistoryEntry> historyList = new JList<>(historyListModel);
    private List<HistoryEntry> historyEntries = List.of();
    private final JLabel historyDetailNameLabel = managementTitleLabel();
    private final JLabel historyDetailActionLabel = managementValueLabel();
    private final JLabel historyDetailDateLabel = managementValueLabel();
    private final JLabel historyDetailContactIdLabel = managementValueLabel();

    private final DefaultListModel<DuplicateCandidate> duplicateListModel = new DefaultListModel<>();
    private final JList<DuplicateCandidate> duplicateList = new JList<>(duplicateListModel);
    private List<DuplicateCandidate> duplicateCandidates = List.of();
    private final JLabel duplicateStatusLabel = new JLabel(Messages.DUPLICATE_NONE);
    private final JLabel duplicatePrimaryLabel = managementTitleLabel();
    private final JLabel duplicateSecondaryLabel = managementValueLabel();
    private final JLabel duplicateReasonLabel = managementValueLabel();

    private final DefaultListModel<Contact> trashListModel = new DefaultListModel<>();
    private final JList<Contact> trashList = new JList<>(trashListModel);
    private List<Contact> trashContacts = List.of();
    private final JLabel trashDetailNameLabel = managementTitleLabel();
    private final JLabel trashDetailPhoneLabel = managementValueLabel();
    private final JLabel trashDetailEmailLabel = managementValueLabel();
    private final JLabel trashDetailCompanyLabel = managementValueLabel();
    private final JLabel trashDetailCategoryLabel = managementValueLabel();


    private final JComboBox<String> languageCombo = new JComboBox<>(new String[] {
            Messages.LANGUAGE_SYSTEM, Messages.LANGUAGE_TURKISH, Messages.LANGUAGE_ENGLISH
    });
    private final JComboBox<String> startupPageCombo = new JComboBox<>(new String[] {
            Messages.STARTUP_DASHBOARD, Messages.STARTUP_CONTACTS
    });
    private final JCheckBox rememberWindowCheckBox = new JCheckBox(Messages.REMEMBER_WINDOW_LABEL);
    private final JCheckBox confirmDeleteCheckBox = new JCheckBox(Messages.CONFIRM_DELETE_LABEL);
    private final JCheckBox reminderNotificationsCheckBox = new JCheckBox(Messages.REMINDER_NOTIFICATIONS_LABEL);
    private final JComboBox<String> themeCombo = new JComboBox<>(new String[] {
            Messages.THEME_LIGHT, Messages.THEME_DARK
    });
    private final JCheckBox compactModeCheckBox = new JCheckBox(Messages.COMPACT_MODE_LABEL);
    private final JCheckBox autoBackupCheckBox = new JCheckBox(Messages.AUTO_BACKUP_LABEL);
    private final JSpinner backupRetentionSpinner = new JSpinner(new SpinnerNumberModel(
            UiConfig.DEFAULT_BACKUP_RETENTION, 1, UiConfig.MAX_BACKUP_RETENTION, 1
    ));
    private final JCheckBox syncEnabledCheckBox = new JCheckBox(Messages.SYNC_ENABLED_LABEL);
    private final JSpinner syncPortSpinner = new JSpinner(new SpinnerNumberModel(
            AppConfig.MOBILE_SYNC_PORT, 1024, 65535, 1
    ));
    private final JCheckBox updateEnabledCheckBox = new JCheckBox(Messages.UPDATE_ENABLED_LABEL);
    private final JLabel databasePathLabel = new JLabel(AppConfig.DATABASE_FILE.toString());
    private final JLabel backupPathLabel = new JLabel(AppConfig.BACKUP_PATH.toString());
    private final JLabel backupPagePathLabel = new JLabel(AppConfig.BACKUP_PATH.toString());

    private AppSettings activeSettings;

    private Runnable addAction = () -> { };
    private Runnable updateAction = () -> { };
    private Runnable deleteAction = () -> { };
    private Runnable refreshAction = () -> { };
    private Runnable searchAction = () -> { };
    private Runnable mobileSyncAction = () -> { };
    private Runnable saveSettingsAction = () -> { };
    private Runnable backupAction = () -> { };
    private Runnable backupCleanupAction = () -> { };
    private Runnable createGroupAction = () -> { };
    private Runnable deleteGroupAction = () -> { };
    private Runnable addGroupContactAction = () -> { };
    private Runnable removeGroupContactAction = () -> { };
    private Runnable createTagAction = () -> { };
    private Runnable deleteTagAction = () -> { };
    private Runnable addTagContactAction = () -> { };
    private Runnable removeTagContactAction = () -> { };
    private Runnable createSmartListAction = () -> { };
    private Runnable deleteSmartListAction = () -> { };
    private Runnable runSmartListAction = () -> { };
    private Runnable restoreHistoryAction = () -> { };
    private Runnable scanDuplicatesAction = () -> { };
    private Runnable mergeDuplicateAction = () -> { };
    private Runnable importAction = () -> { };
    private Runnable exportVcfAction = () -> { };
    private Runnable exportCsvAction = () -> { };
    private Runnable restoreTrashAction = () -> { };
    private Runnable deleteTrashAction = () -> { };
    private Runnable refreshManagementAction = () -> { };
    private Runnable quickCallAction = () -> { };
    private Runnable quickWhatsAppAction = () -> { };
    private Runnable quickEmailAction = () -> { };
    private Runnable quickCopyAction = () -> { };
    private Runnable openWebsiteAction = () -> { };
    private Runnable openSupportAction = () -> { };
    private Runnable saveSavedViewAction = () -> { };
    private Runnable applySavedViewAction = () -> { };
    private Runnable deleteSavedViewAction = () -> { };
    private Runnable renameSavedViewAction = () -> { };
    private Runnable copySavedViewAction = () -> { };
    private Runnable toggleDefaultSavedViewAction = () -> { };
    private Runnable bulkCompanyAction = () -> { };
    private Runnable bulkCategoryAction = () -> { };
    private Runnable bulkAddGroupAction = () -> { };
    private Runnable bulkRemoveGroupAction = () -> { };
    private Runnable bulkAddTagAction = () -> { };
    private Runnable bulkRemoveTagAction = () -> { };
    private Runnable bulkFavoriteAction = () -> { };
    private Runnable bulkUnfavoriteAction = () -> { };
    private Runnable bulkExportVcfAction = () -> { };
    private Runnable bulkExportCsvAction = () -> { };
    private Runnable bulkTrashAction = () -> { };
    private Runnable undoBulkAction = () -> { };
    private Runnable redoBulkAction = () -> { };
    private Runnable contactActivityAction = () -> { };

    public PhoneBookFrame(Image appIcon) {
        super(String.format(Messages.WINDOW_TITLE_VERSION_FORMAT, AppConfig.APP_NAME, AppConfig.APP_VERSION));
        this.appIcon = appIcon;
        this.lightBrandIcon = appIcon == null ? null : brandAssetTool.createThemeVariant(appIcon, false);
        this.darkBrandIcon = appIcon == null ? null : brandAssetTool.createThemeVariant(appIcon, true);
        configureFrame();
        configureFlatLafProperties();
        configureContactList();
        configureReminderControls();
        updateContactActionState(false);
        setContentPane(buildShell());
        bindInternalEvents();
        bindKeyboardShortcuts();
    }

    public void setAddAction(Runnable action) { addAction = action; }
    public void setUpdateAction(Runnable action) { updateAction = action; }
    public void setDeleteAction(Runnable action) { deleteAction = action; }
    public void setRefreshAction(Runnable action) { refreshAction = action; }
    public void setSearchAction(Runnable action) { searchAction = action; }
    public void setMobileSyncAction(Runnable action) { mobileSyncAction = action; }
    public void setSaveSettingsAction(Runnable action) { saveSettingsAction = action; }
    public void setBackupAction(Runnable action) { backupAction = action; }
    public void setBackupCleanupAction(Runnable action) { backupCleanupAction = action; }
    public void setCreateGroupAction(Runnable action) { createGroupAction = action; }
    public void setDeleteGroupAction(Runnable action) { deleteGroupAction = action; }
    public void setAddGroupContactAction(Runnable action) { addGroupContactAction = action; }
    public void setRemoveGroupContactAction(Runnable action) { removeGroupContactAction = action; }
    public void setCreateTagAction(Runnable action) { createTagAction = action; }
    public void setDeleteTagAction(Runnable action) { deleteTagAction = action; }
    public void setAddTagContactAction(Runnable action) { addTagContactAction = action; }
    public void setRemoveTagContactAction(Runnable action) { removeTagContactAction = action; }
    public void setCreateSmartListAction(Runnable action) { createSmartListAction = action; }
    public void setDeleteSmartListAction(Runnable action) { deleteSmartListAction = action; }
    public void setRunSmartListAction(Runnable action) { runSmartListAction = action; }
    public void setRestoreHistoryAction(Runnable action) { restoreHistoryAction = action; }
    public void setScanDuplicatesAction(Runnable action) { scanDuplicatesAction = action; }
    public void setMergeDuplicateAction(Runnable action) { mergeDuplicateAction = action; }
    public void setImportAction(Runnable action) { importAction = action; }
    public void setExportVcfAction(Runnable action) { exportVcfAction = action; }
    public void setExportCsvAction(Runnable action) { exportCsvAction = action; }
    public void setRestoreTrashAction(Runnable action) { restoreTrashAction = action; }
    public void setDeleteTrashAction(Runnable action) { deleteTrashAction = action; }
    public void setRefreshManagementAction(Runnable action) { refreshManagementAction = action; }
    public void setQuickCallAction(Runnable action) { quickCallAction = action; }
    public void setQuickWhatsAppAction(Runnable action) { quickWhatsAppAction = action; }
    public void setQuickEmailAction(Runnable action) { quickEmailAction = action; }
    public void setQuickCopyAction(Runnable action) { quickCopyAction = action; }
    public void setOpenWebsiteAction(Runnable action) { openWebsiteAction = action; }
    public void setOpenSupportAction(Runnable action) { openSupportAction = action; }
    public void setSaveSavedViewAction(Runnable action) { saveSavedViewAction = action; }
    public void setApplySavedViewAction(Runnable action) { applySavedViewAction = action; }
    public void setDeleteSavedViewAction(Runnable action) { deleteSavedViewAction = action; }
    public void setRenameSavedViewAction(Runnable action) { renameSavedViewAction = action; }
    public void setCopySavedViewAction(Runnable action) { copySavedViewAction = action; }
    public void setToggleDefaultSavedViewAction(Runnable action) { toggleDefaultSavedViewAction = action; }
    public void setBulkCompanyAction(Runnable action) { bulkCompanyAction = action; }
    public void setBulkCategoryAction(Runnable action) { bulkCategoryAction = action; }
    public void setBulkAddGroupAction(Runnable action) { bulkAddGroupAction = action; }
    public void setBulkRemoveGroupAction(Runnable action) { bulkRemoveGroupAction = action; }
    public void setBulkAddTagAction(Runnable action) { bulkAddTagAction = action; }
    public void setBulkRemoveTagAction(Runnable action) { bulkRemoveTagAction = action; }
    public void setBulkFavoriteAction(Runnable action) { bulkFavoriteAction = action; }
    public void setBulkUnfavoriteAction(Runnable action) { bulkUnfavoriteAction = action; }
    public void setBulkExportVcfAction(Runnable action) { bulkExportVcfAction = action; }
    public void setBulkExportCsvAction(Runnable action) { bulkExportCsvAction = action; }
    public void setBulkTrashAction(Runnable action) { bulkTrashAction = action; }
    public void setUndoBulkAction(Runnable action) { undoBulkAction = action; }
    public void setRedoBulkAction(Runnable action) { redoBulkAction = action; }
    public void setContactActivityAction(Runnable action) { contactActivityAction = action; }

    public void showUndoNotice(String label) {
        showHistoryNotice(label, Messages.UNDO_NOTICE_FORMAT, true, false);
    }

    public void showRedoNotice(String label) {
        showHistoryNotice(label, Messages.REDO_NOTICE_FORMAT, false, true);
    }

    private void showHistoryNotice(String label, String messageFormat, boolean showUndo, boolean showRedo) {
        String safeLabel = label == null ? "" : label.trim();
        if (safeLabel.isEmpty()) {
            hideUndoNotice();
            return;
        }
        undoNoticeLabel.setText(String.format(messageFormat, safeLabel));
        undoNoticeButton.setVisible(showUndo);
        redoNoticeButton.setVisible(showRedo);
        undoNoticePanel.setVisible(true);
        if (undoNoticeTimer == null) {
            undoNoticeTimer = new Timer(UiConfig.UNDO_NOTICE_TIMEOUT_MS, event -> hideUndoNotice());
            undoNoticeTimer.setRepeats(false);
        }
        undoNoticeTimer.restart();
    }

    public void hideUndoNotice() {
        if (undoNoticeTimer != null) undoNoticeTimer.stop();
        undoNoticePanel.setVisible(false);
    }

    public ContactDraft readContactInput() {
        List<ContactMethod> phones = phoneMethodsPanel.getMethods();
        List<ContactMethod> emails = emailMethodsPanel.getMethods();
        return new ContactDraft(
                nameField.getText(), methodValue(phones, 0), methodValue(phones, 1), methodByLabel(phones, ContactMethod.LABEL_WORK),
                methodValue(emails, 0), methodValue(emails, 1), companyField.getText(), jobTitleField.getText(),
                birthdayField.getText(), websiteField.getText(), categoryField.getText(), addressArea.getText(),
                cityField.getText(), districtField.getText(), postalCodeField.getText(), countryField.getText(),
                notesArea.getText(), favoriteCheckBox.isSelected(), phones, emails, pendingPhotoData,
                (ReminderLeadTime) birthdayReminderCombo.getSelectedItem(), importantDatesPanel.getDates(),
                (KeepInTouchInterval) keepInTouchCombo.getSelectedItem(), lastContactedField.getText()
        );
    }

    private String methodValue(List<ContactMethod> values, int index) {
        return index >= 0 && index < values.size() ? values.get(index).value() : "";
    }

    private String methodByLabel(List<ContactMethod> values, String label) {
        return values.stream().filter(value -> label.equalsIgnoreCase(value.label())).map(ContactMethod::value).findFirst().orElse("");
    }

    public AppSettings readSettingsInput() {
        ThemeMode theme = Messages.THEME_DARK.equals(themeCombo.getSelectedItem()) ? ThemeMode.DARK : ThemeMode.LIGHT;
        String startupPage = Messages.STARTUP_CONTACTS.equals(startupPageCombo.getSelectedItem())
                ? UiConfig.PAGE_CONTACTS : UiConfig.PAGE_DASHBOARD;
        return new AppSettings(
                theme,
                selectedLanguageCode(),
                startupPage,
                rememberWindowCheckBox.isSelected(),
                getWidth(),
                getHeight(),
                compactModeCheckBox.isSelected(),
                confirmDeleteCheckBox.isSelected(),
                autoBackupCheckBox.isSelected(),
                ((Number) backupRetentionSpinner.getValue()).intValue(),
                syncEnabledCheckBox.isSelected(),
                ((Number) syncPortSpinner.getValue()).intValue(),
                updateEnabledCheckBox.isSelected(),
                reminderNotificationsCheckBox.isSelected()
        );
    }

    private String selectedLanguageCode() {
        Object selected = languageCombo.getSelectedItem();
        if (Messages.LANGUAGE_TURKISH.equals(selected)) return LocaleText.LANGUAGE_TURKISH_CODE;
        if (Messages.LANGUAGE_ENGLISH.equals(selected)) return LocaleText.LANGUAGE_ENGLISH_CODE;
        return LocaleText.LANGUAGE_SYSTEM_CODE;
    }

    private String languageLabel(String languageCode) {
        return switch (LocaleText.normalizeLanguageCode(languageCode)) {
            case LocaleText.LANGUAGE_TURKISH_CODE -> Messages.LANGUAGE_TURKISH;
            case LocaleText.LANGUAGE_ENGLISH_CODE -> Messages.LANGUAGE_ENGLISH;
            default -> Messages.LANGUAGE_SYSTEM;
        };
    }

    public long requireSelectedContactId() {
        List<Contact> selected = contactList.getSelectedValuesList();
        if (selected.isEmpty()) {
            throw new IllegalArgumentException(Messages.SELECT_CONTACT);
        }
        if (selected.size() != 1) {
            throw new IllegalArgumentException(Messages.SELECT_SINGLE_CONTACT);
        }
        return selected.get(0).id();
    }

    public List<Long> requireSelectedContactIds() {
        List<Contact> selected = contactList.getSelectedValuesList();
        if (selected.isEmpty()) throw new IllegalArgumentException(Messages.SELECT_CONTACT);
        return selected.stream().map(Contact::id).distinct().toList();
    }

    public String getSearchQuery() { return searchField.getText(); }
    public boolean isFavoritesOnly() { return favoritesOnlyCheckBox.isSelected(); }

    public ContactFilter readContactFilter() {
        return new ContactFilter(
                searchField.getText(),
                favoritesOnlyCheckBox.isSelected(),
                selectedTextFilter(companyFilterCombo),
                selectedTextFilter(cityFilterCombo),
                selectedTextFilter(categoryFilterCombo),
                selectedIdFilter(groupFilterCombo),
                selectedIdFilter(tagFilterCombo),
                selectedContactSort()
        );
    }

    public void showContactFilterOptions(ContactFilterOptions options) {
        ContactFilter current = readContactFilter();
        ContactFilterOptions safeOptions = options == null
                ? new ContactFilterOptions(List.of(), List.of(), List.of(), List.of(), List.of())
                : options;
        updatingFilterControls = true;
        try {
            fillTextFilter(companyFilterCombo, Messages.FILTER_ALL_COMPANIES, safeOptions.companies(), current.company());
            fillTextFilter(cityFilterCombo, Messages.FILTER_ALL_CITIES, safeOptions.cities(), current.city());
            fillTextFilter(categoryFilterCombo, Messages.FILTER_ALL_CATEGORIES, safeOptions.categories(), current.category());
            fillIdFilter(groupFilterCombo, Messages.FILTER_ALL_GROUPS, safeOptions.groups(), current.groupId());
            fillIdFilter(tagFilterCombo, Messages.FILTER_ALL_TAGS, safeOptions.tags(), current.tagId());
        } finally {
            updatingFilterControls = false;
        }
        rebuildActiveFilterChips();
    }

    public void showSavedContactViews(List<SavedContactView> views, String selectedName) {
        String target = selectedName == null ? selectedSavedContactViewName() : selectedName.trim();
        defaultSavedViewName = views == null ? "" : views.stream()
                .filter(SavedContactView::defaultView)
                .map(SavedContactView::name)
                .findFirst()
                .orElse("");
        updatingSavedViewControls = true;
        try {
            savedViewCombo.removeAllItems();
            savedViewCombo.addItem(Messages.SAVED_VIEW_PLACEHOLDER);
            if (views != null) {
                views.stream().map(SavedContactView::name).forEach(savedViewCombo::addItem);
            }
            if (!target.isBlank()) {
                selectSavedViewName(target);
            }
        } finally {
            updatingSavedViewControls = false;
        }
        savedViewCombo.repaint();
        updateSavedViewActionState();
    }

    public String selectedSavedContactViewName() {
        if (savedViewCombo.getSelectedIndex() <= 0) return "";
        Object selected = savedViewCombo.getSelectedItem();
        return selected == null ? "" : selected.toString().trim();
    }

    public String requireSelectedSavedContactViewName() {
        String name = selectedSavedContactViewName();
        if (name.isBlank()) throw new IllegalArgumentException(Messages.ERROR_SAVED_VIEW_NOT_FOUND);
        return name;
    }

    public String promptSavedContactViewName() {
        Object value = JOptionPane.showInputDialog(
                this, Messages.SAVED_VIEW_NAME_PROMPT, Messages.SAVED_VIEW_SAVE_BUTTON, JOptionPane.PLAIN_MESSAGE,
                null, null, selectedSavedContactViewName()
        );
        return value == null ? null : value.toString().trim();
    }

    public String promptRenameSavedContactViewName(String currentName) {
        Object value = JOptionPane.showInputDialog(
                this, Messages.SAVED_VIEW_RENAME_PROMPT, Messages.SAVED_VIEW_RENAME_BUTTON, JOptionPane.PLAIN_MESSAGE,
                null, null, currentName
        );
        return value == null ? null : value.toString().trim();
    }

    public String promptCopySavedContactViewName(String currentName) {
        Object value = JOptionPane.showInputDialog(
                this, Messages.SAVED_VIEW_COPY_PROMPT, Messages.SAVED_VIEW_COPY_BUTTON, JOptionPane.PLAIN_MESSAGE,
                null, null, String.format(Messages.SAVED_VIEW_COPY_NAME_FORMAT, currentName)
        );
        return value == null ? null : value.toString().trim();
    }

    public boolean confirmDeleteSavedContactView(String name) {
        return JOptionPane.showConfirmDialog(
                this, String.format(Messages.SAVED_VIEW_DELETE_CONFIRM_FORMAT, name),
                Messages.SAVED_VIEW_DELETE_CONFIRM_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    public void applyContactFilter(ContactFilter filter) {
        if (filter == null) return;
        updatingFilterControls = true;
        try {
            searchField.setText(filter.query());
            favoritesOnlyCheckBox.setSelected(filter.favoritesOnly());
            selectTextFilter(companyFilterCombo, filter.company());
            selectTextFilter(cityFilterCombo, filter.city());
            selectTextFilter(categoryFilterCombo, filter.category());
            selectIdFilter(groupFilterCombo, filter.groupId());
            selectIdFilter(tagFilterCombo, filter.tagId());
            sortFilterCombo.setSelectedItem(filter.sort());
        } finally {
            updatingFilterControls = false;
        }
        rebuildActiveFilterChips();
    }

    public void showContacts(List<Contact> contacts) {
        List<Long> selectedIds = contactList.getSelectedValuesList().stream().map(Contact::id).toList();
        contactListModel.clear();
        contacts.forEach(contactListModel::addElement);
        String countText = String.format(Messages.STATUS_COUNT_FORMAT, contacts.size());
        statusLabel.setText(countText);
        contactCountLabel.setText(countText);

        int[] selectedIndices = selectedIds.stream()
                .mapToInt(this::indexOfContact)
                .filter(index -> index >= 0)
                .toArray();
        if (selectedIndices.length > 0) {
            contactList.setSelectedIndices(selectedIndices);
        } else if (!contacts.isEmpty()) {
            contactList.setSelectedIndex(0);
        } else {
            contactList.clearSelection();
            clearDetailCard();
            updateContactActionState(false);
        }
    }

    private long selectedContactIdOrZero() {
        Contact selected = contactList.getSelectedValue();
        return selected == null ? 0L : selected.id();
    }

    public long selectedContactId() {
        return selectedContactIdOrZero();
    }

    public ContactActivityFilter readContactActivityFilter() {
        return contactActivityFilter;
    }

    public void showContactActivity(ContactActivityFeed feed) {
        ContactActivityFeed value = feed == null ? ContactActivityFeed.empty() : feed;
        activityAllButton.setText(String.format(Messages.ACTIVITY_FILTER_ALL, value.totalCount()));
        activityChangesButton.setText(String.format(Messages.ACTIVITY_FILTER_CHANGES, value.changeCount()));
        activityTransferButton.setText(String.format(Messages.ACTIVITY_FILTER_TRANSFER, value.transferCount()));
        activityRestoreButton.setText(String.format(Messages.ACTIVITY_FILTER_RESTORE, value.restoreCount()));
        updateActivityFilterSelection();

        contactActivityTimelinePanel.removeAll();
        if (value.entries().isEmpty()) {
            JLabel empty = new JLabel(Messages.ACTIVITY_EMPTY);
            empty.setForeground(ModernThemePalette.textSecondary());
            empty.setBorder(new EmptyBorder(UiConfig.ACTIVITY_ROW_PADDING_Y, 0, UiConfig.ACTIVITY_ROW_PADDING_Y, 0));
            contactActivityTimelinePanel.add(empty);
        } else {
            for (int index = 0; index < value.entries().size(); index++) {
                contactActivityTimelinePanel.add(new ContactActivityTimelineRow(
                        value.entries().get(index),
                        index == value.entries().size() - 1
                ));
            }
        }
        contactActivityTimelinePanel.revalidate();
        contactActivityTimelinePanel.repaint();
    }

    private int indexOfContact(long contactId) {
        if (contactId <= 0L) {
            return -1;
        }
        for (int index = 0; index < contactListModel.size(); index++) {
            if (contactListModel.get(index).id() == contactId) {
                return index;
            }
        }
        return -1;
    }

    public void showDashboard(DashboardSummary summary) {
        totalValue.setText(Integer.toString(summary.totalContacts()));
        favoriteValue.setText(Integer.toString(summary.favoriteContacts()));
        birthdayValue.setText(Integer.toString(summary.birthdaysToday()));
        followUpValue.setText(Integer.toString(summary.followUpsDue()));
        companyValue.setText(Integer.toString(summary.companies()));
        cityValue.setText(Integer.toString(summary.cities()));
    }

    public void showSyncState(SyncServerInfo info) {
        syncStatusLabel.setText(info.running() ? Messages.STATUS_SYNC_RUNNING : Messages.STATUS_SYNC_STOPPED);
        syncStatusLabel.setToolTipText(String.format(Messages.STATUS_SYNC_TOOLTIP_FORMAT, info.address(), info.port()));
        dashboardSyncStatusLabel.setText(info.running() ? Messages.DASHBOARD_SYNC_ACTIVE : Messages.DASHBOARD_SYNC_STOPPED);
        dashboardSyncStatusLabel.setForeground(info.running() ? ModernThemePalette.accentStrong() : ModernThemePalette.textSecondary());
        syncPageStatusLabel.setText(info.running() ? Messages.SYNC_STATUS_ACTIVE : Messages.SYNC_STATUS_STOPPED);
        syncPageStatusLabel.setForeground(info.running() ? ModernThemePalette.accentStrong() : ModernThemePalette.textSecondary());
        syncAddressLabel.setText(String.format(Messages.SYNC_ADDRESS_FORMAT, info.address(), info.port()));
        syncTokenLabel.setText(String.format(Messages.SYNC_TOKEN_FORMAT, info.token()));
    }

    public void showBackups(List<Path> backups) {
        backupListModel.clear();
        backups.forEach(path -> backupListModel.addElement(path.getFileName().toString()));
        backupCountLabel.setText(String.format(Messages.BACKUP_COUNT_FORMAT, backups.size()));
        backupLastLabel.setText(backups.isEmpty()
                ? Messages.BACKUP_NONE
                : String.format(Messages.BACKUP_LAST_FORMAT, backups.get(0).getFileName()));
        dashboardBackupStatusLabel.setText(backups.isEmpty()
                ? Messages.DASHBOARD_BACKUP_NONE
                : backups.get(0).getFileName().toString());
        dashboardBackupStatusLabel.setForeground(backups.isEmpty()
                ? ModernThemePalette.textSecondary()
                : ModernThemePalette.accentStrong());
    }

    public void showGroups(List<GroupRecord> values) {
        groups = values == null ? List.of() : List.copyOf(values);
        groupListModel.clear();
        groups.forEach(groupListModel::addElement);
        if (!groups.isEmpty() && groupList.getSelectedIndex() < 0) groupList.setSelectedIndex(0);
        updateGroupDetail();
    }

    public void showTags(List<TagRecord> values) {
        tags = values == null ? List.of() : List.copyOf(values);
        tagListModel.clear();
        tags.forEach(tagListModel::addElement);
        if (!tags.isEmpty() && tagList.getSelectedIndex() < 0) tagList.setSelectedIndex(0);
        updateTagDetail();
    }

    public void showContactMemberships(Map<Long, List<GroupRecord>> groupValues, Map<Long, List<TagRecord>> tagValues) {
        groupMemberships = groupValues == null ? Map.of() : Map.copyOf(groupValues);
        tagMemberships = tagValues == null ? Map.of() : Map.copyOf(tagValues);
        Contact selected = contactList.getSelectedValue();
        if (selected != null) rebuildMembershipChips(selected.id());
    }

    private void rebuildMembershipChips(long contactId) {
        detailChipsPanel.removeAll();
        for (GroupRecord group : groupMemberships.getOrDefault(contactId, List.of())) detailChipsPanel.add(new ModernPillLabel(group.name()));
        for (TagRecord tag : tagMemberships.getOrDefault(contactId, List.of())) detailChipsPanel.add(new ModernPillLabel(tag.name()));
        if (detailChipsPanel.getComponentCount() == 0) detailChipsPanel.add(new JLabel(Messages.EMPTY_VALUE));
        detailChipsPanel.revalidate();
        detailChipsPanel.repaint();
    }

    public void showSmartLists(List<SmartList> values) {
        smartLists = values == null ? List.of() : List.copyOf(values);
        smartListModel.clear();
        smartLists.forEach(smartListModel::addElement);
        if (!smartLists.isEmpty() && smartList.getSelectedIndex() < 0) smartList.setSelectedIndex(0);
        updateSmartListDetail();
    }

    public void showHistory(List<HistoryEntry> values) {
        historyEntries = values == null ? List.of() : List.copyOf(values);
        historyListModel.clear();
        historyEntries.forEach(historyListModel::addElement);
        if (!historyEntries.isEmpty() && historyList.getSelectedIndex() < 0) historyList.setSelectedIndex(0);
        updateHistoryDetail();
    }

    public void showDuplicates(List<DuplicateCandidate> values) {
        duplicateCandidates = values == null ? List.of() : List.copyOf(values);
        duplicateListModel.clear();
        duplicateCandidates.forEach(duplicateListModel::addElement);
        duplicateStatusLabel.setText(duplicateCandidates.isEmpty()
                ? Messages.DUPLICATE_NONE
                : String.format(Messages.DUPLICATE_COUNT_FORMAT, duplicateCandidates.size()));
        if (!duplicateCandidates.isEmpty() && duplicateList.getSelectedIndex() < 0) duplicateList.setSelectedIndex(0);
        updateDuplicateDetail();
    }

    public void showTrash(List<Contact> values) {
        trashContacts = values == null ? List.of() : List.copyOf(values);
        trashListModel.clear();
        trashContacts.forEach(trashListModel::addElement);
        if (!trashContacts.isEmpty() && trashList.getSelectedIndex() < 0) trashList.setSelectedIndex(0);
        updateTrashDetail();
    }

    public long requireSelectedGroupId() {
        int index = groupList.getSelectedIndex();
        if (index < 0 || index >= groups.size()) throw new IllegalArgumentException(Messages.ERROR_SELECTION_REQUIRED);
        return groups.get(index).id();
    }

    public long requireSelectedTagId() {
        int index = tagList.getSelectedIndex();
        if (index < 0 || index >= tags.size()) throw new IllegalArgumentException(Messages.ERROR_SELECTION_REQUIRED);
        return tags.get(index).id();
    }

    public SmartList requireSelectedSmartList() {
        int index = smartList.getSelectedIndex();
        if (index < 0 || index >= smartLists.size()) throw new IllegalArgumentException(Messages.ERROR_SELECTION_REQUIRED);
        return smartLists.get(index);
    }

    public long requireSelectedHistoryId() {
        HistoryEntry selected = historyList.getSelectedValue();
        if (selected == null) throw new IllegalArgumentException(Messages.ERROR_SELECTION_REQUIRED);
        return selected.id();
    }

    public DuplicateCandidate requireSelectedDuplicate() {
        DuplicateCandidate selected = duplicateList.getSelectedValue();
        if (selected == null) throw new IllegalArgumentException(Messages.ERROR_SELECTION_REQUIRED);
        return selected;
    }

    public long requireSelectedTrashContactId() {
        Contact selected = trashList.getSelectedValue();
        if (selected == null) throw new IllegalArgumentException(Messages.ERROR_SELECTION_REQUIRED);
        return selected.id();
    }

    public String promptGroupName() {
        return JOptionPane.showInputDialog(this, Messages.GROUP_NAME_PROMPT, Messages.GROUPS_TITLE, JOptionPane.PLAIN_MESSAGE);
    }

    public String promptTagName() {
        return JOptionPane.showInputDialog(this, Messages.TAG_NAME_PROMPT, Messages.TAGS_TITLE, JOptionPane.PLAIN_MESSAGE);
    }

    public Long promptBulkGroupId() {
        return promptBulkGroupSelection(Messages.BULK_GROUP_PROMPT, Messages.BULK_ADD_GROUP_BUTTON);
    }

    public Long promptBulkRemoveGroupId() {
        return promptBulkGroupSelection(Messages.BULK_GROUP_REMOVE_PROMPT, Messages.BULK_REMOVE_GROUP_BUTTON);
    }

    private Long promptBulkGroupSelection(String prompt, String title) {
        if (groups.isEmpty()) {
            showMessage(Messages.BULK_GROUP_EMPTY);
            return null;
        }
        String[] names = groups.stream().map(GroupRecord::name).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(
                this, prompt, title, JOptionPane.PLAIN_MESSAGE, null, names, names[0]
        );
        if (selected == null) return null;
        return groups.stream().filter(group -> group.name().equals(selected)).map(GroupRecord::id).findFirst().orElse(null);
    }

    public Long promptBulkTagId() {
        return promptBulkTagSelection(Messages.BULK_TAG_PROMPT, Messages.BULK_ADD_TAG_BUTTON);
    }

    public Long promptBulkRemoveTagId() {
        return promptBulkTagSelection(Messages.BULK_TAG_REMOVE_PROMPT, Messages.BULK_REMOVE_TAG_BUTTON);
    }

    private Long promptBulkTagSelection(String prompt, String title) {
        if (tags.isEmpty()) {
            showMessage(Messages.BULK_TAG_EMPTY);
            return null;
        }
        String[] names = tags.stream().map(TagRecord::name).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(
                this, prompt, title, JOptionPane.PLAIN_MESSAGE, null, names, names[0]
        );
        if (selected == null) return null;
        return tags.stream().filter(tag -> tag.name().equals(selected)).map(TagRecord::id).findFirst().orElse(null);
    }

    public String promptBulkCompany() {
        String value = JOptionPane.showInputDialog(this, Messages.BULK_COMPANY_PROMPT, Messages.BULK_COMPANY_BUTTON, JOptionPane.PLAIN_MESSAGE);
        return value == null ? null : value.trim();
    }

    public String promptBulkCategory() {
        String value = JOptionPane.showInputDialog(this, Messages.BULK_CATEGORY_PROMPT, Messages.BULK_CATEGORY_BUTTON, JOptionPane.PLAIN_MESSAGE);
        return value == null ? null : value.trim();
    }

    public boolean confirmBulkTrash(int count) {
        return JOptionPane.showConfirmDialog(
                this, String.format(Messages.BULK_TRASH_CONFIRM_FORMAT, count), Messages.BULK_TRASH_CONFIRM_TITLE,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    public String promptColor() {
        String[] colors = AppConfig.LABEL_COLORS.toArray(new String[0]);
        return (String) JOptionPane.showInputDialog(this, Messages.COLOR_PROMPT, Messages.INFO_TITLE,
                JOptionPane.PLAIN_MESSAGE, null, colors, colors[0]);
    }

    public SmartListDraft promptSmartListDraft() {
        String name = JOptionPane.showInputDialog(this, Messages.SMART_NAME_PROMPT, Messages.SMART_LISTS_TITLE, JOptionPane.PLAIN_MESSAGE);
        if (name == null) return null;
        String[] fields = {"name", "phone", "email", "company", "job_title", "city", "country", "category", "notes", "birthday", "favorite"};
        String field = (String) JOptionPane.showInputDialog(this, Messages.SMART_FIELD_PROMPT, Messages.SMART_LISTS_TITLE,
                JOptionPane.PLAIN_MESSAGE, null, fields, fields[0]);
        if (field == null) return null;
        String[] operators = {"contains", "equals", "is_empty", "not_empty", "true"};
        String operator = (String) JOptionPane.showInputDialog(this, Messages.SMART_OPERATOR_PROMPT, Messages.SMART_LISTS_TITLE,
                JOptionPane.PLAIN_MESSAGE, null, operators, operators[0]);
        if (operator == null) return null;
        String value = "";
        if (!"is_empty".equals(operator) && !"not_empty".equals(operator) && !"true".equals(operator)) {
            value = JOptionPane.showInputDialog(this, Messages.SMART_VALUE_PROMPT, Messages.SMART_LISTS_TITLE, JOptionPane.PLAIN_MESSAGE);
            if (value == null) return null;
        }
        return new SmartListDraft(name, field, operator, value);
    }

    public Path chooseImportFile() {
        JFileChooser chooser = new JFileChooser(AppConfig.USER_FILES_ROOT.toFile());
        chooser.setDialogTitle(Messages.FILE_CHOOSER_IMPORT_TITLE);
        chooser.setFileFilter(new FileNameExtensionFilter(Messages.FILE_CHOOSER_CONTACT_FILTER, ContactFileFormat.extensions()));
        return chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().toPath() : null;
    }

    public Path chooseExportFile(ContactFileFormat format) {
        JFileChooser chooser = new JFileChooser(AppConfig.USER_EXPORT_PATH.toFile());
        chooser.setDialogTitle(Messages.FILE_CHOOSER_EXPORT_TITLE);
        chooser.setFileFilter(new FileNameExtensionFilter(format.displayName(), format.extension()));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return null;
        Path path = chooser.getSelectedFile().toPath();
        if (!format.matchesFileName(path.getFileName().toString())) {
            path = path.resolveSibling(path.getFileName() + format.dottedExtension());
        }
        return path;
    }

    public boolean confirmPermanentDelete() {
        return JOptionPane.showConfirmDialog(this, Messages.TRASH_PERMANENT_CONFIRM, Messages.DELETE_CONFIRM_TITLE,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public boolean confirmDuplicateMerge() {
        return JOptionPane.showConfirmDialog(this, Messages.DUPLICATE_MERGE_CONFIRM, Messages.MAINTENANCE_TITLE,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public void applySettings(AppSettings settings) {
        activeSettings = settings;
        themeCombo.setSelectedItem(settings.theme() == ThemeMode.DARK ? Messages.THEME_DARK : Messages.THEME_LIGHT);
        languageCombo.setSelectedItem(languageLabel(settings.languageCode()));
        startupPageCombo.setSelectedItem(UiConfig.PAGE_CONTACTS.equals(settings.startupPage())
                ? Messages.STARTUP_CONTACTS : Messages.STARTUP_DASHBOARD);
        rememberWindowCheckBox.setSelected(settings.rememberWindow());
        confirmDeleteCheckBox.setSelected(settings.confirmDelete());
        compactModeCheckBox.setSelected(settings.compactMode());
        autoBackupCheckBox.setSelected(settings.autoBackup());
        backupRetentionSpinner.setValue(settings.backupRetention());
        syncEnabledCheckBox.setSelected(settings.mobileSyncEnabled());
        syncPortSpinner.setValue(settings.mobileSyncPort());
        updateEnabledCheckBox.setSelected(settings.updateEnabled());
        reminderNotificationsCheckBox.setSelected(settings.reminderNotificationsEnabled());
        contactList.setFixedCellHeight(settings.compactMode() ? UiConfig.CONTACT_LIST_ROW_HEIGHT_COMPACT : UiConfig.CONTACT_LIST_ROW_HEIGHT);
        if (settings.rememberWindow()) {
            setSize(Math.max(UiConfig.WINDOW_MIN_WIDTH, settings.windowWidth()),
                    Math.max(UiConfig.WINDOW_MIN_HEIGHT, settings.windowHeight()));
        }
        setThemeMode(settings.theme());
    }

    public void setThemeMode(ThemeMode mode) {
        themeCombo.setSelectedItem(mode == ThemeMode.DARK ? Messages.THEME_DARK : Messages.THEME_LIGHT);
        refreshWindowIcons(mode == ThemeMode.DARK);
    }

    private void refreshWindowIcons(boolean dark) {
        if (appIcon == null) return;
        List<Image> images = brandAssetTool.createWindowIconImages(appIcon, dark);
        if (!images.isEmpty()) setIconImages(images);
    }

    public void showPage(String page) {
        String resolvedPage = UiConfig.isKnownPage(page) ? page : UiConfig.PAGE_DASHBOARD;
        pageLayout.show(pageContainer, resolvedPage);
        JButton target = UiConfig.PAGE_CONTACTS.equals(resolvedPage) && favoritesOnlyCheckBox.isSelected()
                ? favoritesNavigationButton
                : navigationByPage.get(resolvedPage);
        if (target != null) {
            openNavigationSectionFor(target);
            activateNav(target);
        }
    }

    public void openContactsForNew() {
        favoritesOnlyCheckBox.setSelected(false);
        showPage(UiConfig.PAGE_CONTACTS);
        clearForm();
    }

    public void clearForm() {
        contactList.clearSelection();
        for (JTextField value : List.of(
                nameField, companyField, jobTitleField, birthdayField, lastContactedField, websiteField, categoryField, cityField, districtField,
                postalCodeField, countryField
        )) {
            value.setText("");
        }
        addressArea.setText("");
        notesArea.setText("");
        favoriteCheckBox.setSelected(false);
        phoneMethodsPanel.clearMethods();
        emailMethodsPanel.clearMethods();
        importantDatesPanel.clearDates();
        birthdayReminderCombo.setSelectedItem(ReminderLeadTime.DISABLED);
        keepInTouchCombo.setSelectedItem(KeepInTouchInterval.DISABLED);
        pendingPhotoData = null;
        clearDetailCard();
        updateContactActionState(false);
        showContactEditMode();
    }

    private void updateContactActionState(boolean existingContact) {
        addButton.setText(existingContact ? Messages.UPDATE_BUTTON : Messages.ADD_BUTTON);
        deleteButton.setVisible(existingContact);
        cancelButton.setVisible(true);
    }

    private void showContactViewMode() {
        if (contactProfileSummaryPanel != null) contactProfileSummaryPanel.setVisible(true);
        contactModeLayout.show(contactModeContainer, UiConfig.CONTACT_MODE_VIEW);
        quickActionsPanel.setVisible(true);
        editContactButton.setVisible(true);
    }

    private void showContactEditMode() {
        if (contactProfileSummaryPanel != null) contactProfileSummaryPanel.setVisible(true);
        contactModeLayout.show(contactModeContainer, UiConfig.CONTACT_MODE_EDIT);
        quickActionsPanel.setVisible(false);
        editContactButton.setVisible(false);
        nameField.requestFocusInWindow();
    }

    private void showBulkSelectionMode(List<Contact> selected) {
        if (contactProfileSummaryPanel != null) contactProfileSummaryPanel.setVisible(false);
        bulkSelectionCountLabel.setText(String.format(Messages.BULK_SELECTION_COUNT_FORMAT, selected.size()));
        bulkSelectionSummaryLabel.setText(String.format(Messages.BULK_SELECTION_SUMMARY_FORMAT, bulkSelectionNames(selected)));
        contactModeLayout.show(contactModeContainer, UiConfig.CONTACT_MODE_BULK);
        quickActionsPanel.setVisible(false);
        editContactButton.setVisible(false);
    }

    private String bulkSelectionNames(List<Contact> selected) {
        List<String> names = selected.stream()
                .limit(UiConfig.BULK_PREVIEW_NAME_LIMIT)
                .map(Contact::name)
                .toList();
        String value = String.join(", ", names);
        int remaining = selected.size() - names.size();
        return remaining > 0 ? value + String.format(Messages.BULK_SELECTION_MORE_FORMAT, remaining) : value;
    }

    private void cancelContactEdit() {
        Contact selected = contactList.getSelectedValue();
        if (selected == null) {
            clearForm();
            return;
        }
        fillFormFromSelection();
    }

    public boolean confirmDelete() {
        if (activeSettings != null && !activeSettings.confirmDelete()) {
            return true;
        }
        return JOptionPane.showConfirmDialog(
                this, Messages.DELETE_CONFIRM, Messages.DELETE_CONFIRM_TITLE,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    public void showSyncInfo(SyncServerInfo info) {
        String message = info.running()
                ? String.format(Messages.MOBILE_SYNC_RUNNING_FORMAT, info.address(), info.port(), info.token())
                : String.format(Messages.MOBILE_SYNC_STOPPED_FORMAT, info.address(), info.port(), info.token());
        JOptionPane.showMessageDialog(this, message, Messages.MOBILE_SYNC_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, Messages.INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    public void showWindow() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void configureReminderControls() {
        birthdayReminderCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof ReminderLeadTime lead) setText(ReminderText.leadTime(lead));
                return this;
            }
        });
        keepInTouchCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof KeepInTouchInterval interval) setText(ReminderText.keepInTouch(interval));
                return this;
            }
        });
        birthdayReminderCombo.setSelectedItem(ReminderLeadTime.DISABLED);
        keepInTouchCombo.setSelectedItem(KeepInTouchInterval.DISABLED);
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UiConfig.WINDOW_SIZE);
        setMinimumSize(UiConfig.WINDOW_MIN_SIZE);
        refreshWindowIcons(ModernThemePalette.isDark());
    }

    private void configureFlatLafProperties() {
        applyTextFieldStyle(searchField, Messages.SEARCH_HINT, true);
        applyTextFieldStyle(nameField, Messages.NAME_HINT, false);
        applyTextFieldStyle(birthdayField, Messages.BIRTHDAY_HINT, false);
        applyTextFieldStyle(lastContactedField, Messages.LAST_CONTACTED_HINT, false);
        applyTextFieldStyle(categoryField, Messages.CATEGORY_HINT, false);
        applyTextFieldStyle(companyField, Messages.COMPANY_HINT, false);
        applyTextFieldStyle(jobTitleField, Messages.JOB_TITLE_HINT, false);
        applyTextFieldStyle(websiteField, Messages.WEBSITE_HINT, false);
        applyTextFieldStyle(cityField, Messages.CITY_HINT, false);
        applyTextFieldStyle(districtField, Messages.DISTRICT_HINT, false);
        applyTextFieldStyle(postalCodeField, Messages.POSTAL_CODE_HINT, false);
        applyTextFieldStyle(countryField, Messages.COUNTRY_HINT, false);
        for (JButton button : List.of(
                addButton, deleteButton, cancelButton, clearButton, refreshButton,
                saveSettingsButton, backupNowButton, photoActionsButton
        )) {
            button.putClientProperty(UiConfig.CLIENT_BUTTON_TYPE, UiConfig.BUTTON_TYPE_ROUND_RECT);
        }
    }

    private void applyTextFieldStyle(JTextField field, String placeholder, boolean clearButton) {
        field.putClientProperty(UiConfig.CLIENT_ROUND_RECT, true);
        if (placeholder != null) {
            field.putClientProperty(UiConfig.CLIENT_PLACEHOLDER_TEXT, placeholder);
        }
        if (clearButton) {
            field.putClientProperty(UiConfig.CLIENT_SHOW_CLEAR_BUTTON, true);
        }
    }

    private void configureContactList() {
        contactList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        contactList.setToolTipText(Messages.BULK_SELECTION_HINT);
        contactList.setCellRenderer(contactListRenderer);
        contactList.setFixedCellHeight(UiConfig.CONTACT_LIST_ROW_HEIGHT);
        contactList.setBackground(ModernThemePalette.surface());
        contactList.setForeground(ModernThemePalette.textPrimary());
        contactList.setBorder(BorderFactory.createEmptyBorder());
        contactList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                int index = contactList.locationToIndex(event.getPoint());
                if (index >= 0 && !contactList.getCellBounds(index, index).contains(event.getPoint())) {
                    index = -1;
                }
                contactListRenderer.setHoveredIndex(index);
                contactList.repaint();
            }
        });
        contactList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent event) {
                contactListRenderer.setHoveredIndex(-1);
                contactList.repaint();
            }
        });
    }

    private JPanel buildShell() {
        JPanel root = new AppBackgroundPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder());
        pageContainer.setOpaque(false);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildPages(), BorderLayout.CENTER);
        root.add(buildStatusPanel(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildSidebar() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(22, 18, 16, 18));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        if (appIcon != null) {
            brand.add(new JLabel(new ThemeBrandIcon(lightBrandIcon, darkBrandIcon, 50)), BorderLayout.WEST);
        }
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(sidebarLabel(Messages.BRAND_TITLE, Font.BOLD, 19));
        text.add(Box.createVerticalStrut(1));
        text.add(sidebarLabel(Messages.BRAND_SUBTITLE, Font.BOLD, 13));
        brand.add(text, BorderLayout.CENTER);
        content.add(brand);
        content.add(Box.createVerticalStrut(7));
        JLabel tagline = sidebarLabel(Messages.BRAND_TAGLINE, Font.PLAIN, 11);
        tagline.setForeground(ModernThemePalette.sidebarMutedText());
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tagline);
        content.add(Box.createVerticalStrut(18));

        content.add(sidebarSectionLabel(Messages.NAV_SECTION_MAIN));
        content.add(Box.createVerticalStrut(6));
        JButton dashboardButton = navButton(Messages.NAV_DASHBOARD, NavGlyph.HOME, UiConfig.PAGE_DASHBOARD, () -> showPage(UiConfig.PAGE_DASHBOARD));
        content.add(dashboardButton);
        content.add(navGap());
        content.add(navButton(Messages.NAV_CONTACTS, NavGlyph.CONTACTS, UiConfig.PAGE_CONTACTS, () -> {
            favoritesOnlyCheckBox.setSelected(false);
            rebuildActiveFilterChips();
            showPage(UiConfig.PAGE_CONTACTS);
            searchAction.run();
        }));
        content.add(navGap());
        favoritesNavigationButton = navButton(Messages.NAV_FAVORITES, NavGlyph.STAR, UiConfig.PAGE_CONTACTS, () -> {
            favoritesOnlyCheckBox.setSelected(true);
            rebuildActiveFilterChips();
            showPage(UiConfig.PAGE_CONTACTS);
            searchAction.run();
        });
        content.add(favoritesNavigationButton);
        content.add(Box.createVerticalStrut(12));

        CollapsibleNavigationSection organizeSection = navigationSection(Messages.NAV_SECTION_ORGANIZE);
        addSectionNavigationButton(organizeSection, Messages.NAV_GROUPS, NavGlyph.GROUPS, UiConfig.PAGE_GROUPS,
                () -> { showPage(UiConfig.PAGE_GROUPS); refreshManagementAction.run(); });
        addSectionNavigationButton(organizeSection, Messages.NAV_TAGS, NavGlyph.TAGS, UiConfig.PAGE_TAGS,
                () -> { showPage(UiConfig.PAGE_TAGS); refreshManagementAction.run(); });
        addSectionNavigationButton(organizeSection, Messages.NAV_SMART_LISTS, NavGlyph.SMART, UiConfig.PAGE_SMART_LISTS,
                () -> { showPage(UiConfig.PAGE_SMART_LISTS); refreshManagementAction.run(); });
        content.add(organizeSection);
        content.add(Box.createVerticalStrut(5));

        CollapsibleNavigationSection dataToolsSection = navigationSection(Messages.NAV_SECTION_DATA_TOOLS);
        addSectionNavigationButton(dataToolsSection, Messages.NAV_IMPORT_EXPORT, NavGlyph.TRANSFER, UiConfig.PAGE_IMPORT_EXPORT,
                () -> showPage(UiConfig.PAGE_IMPORT_EXPORT));
        addSectionNavigationButton(dataToolsSection, Messages.NAV_SYNC, NavGlyph.SYNC, UiConfig.PAGE_SYNC,
                () -> showPage(UiConfig.PAGE_SYNC));
        addSectionNavigationButton(dataToolsSection, Messages.NAV_BACKUP, NavGlyph.BACKUP, UiConfig.PAGE_BACKUP,
                () -> showPage(UiConfig.PAGE_BACKUP));
        addSectionNavigationButton(dataToolsSection, Messages.NAV_MAINTENANCE, NavGlyph.MAINTENANCE, UiConfig.PAGE_MAINTENANCE,
                () -> { showPage(UiConfig.PAGE_MAINTENANCE); scanDuplicatesAction.run(); });
        content.add(dataToolsSection);
        content.add(Box.createVerticalStrut(5));

        CollapsibleNavigationSection archiveSection = navigationSection(Messages.NAV_SECTION_ARCHIVE);
        addSectionNavigationButton(archiveSection, Messages.NAV_HISTORY, NavGlyph.HISTORY, UiConfig.PAGE_HISTORY,
                () -> { showPage(UiConfig.PAGE_HISTORY); refreshManagementAction.run(); });
        addSectionNavigationButton(archiveSection, Messages.NAV_TRASH, NavGlyph.TRASH, UiConfig.PAGE_TRASH,
                () -> { showPage(UiConfig.PAGE_TRASH); refreshManagementAction.run(); });
        content.add(archiveSection);
        content.add(Box.createVerticalStrut(14));
        content.add(sidebarDivider());
        content.add(Box.createVerticalStrut(8));
        content.add(navButton(Messages.NAV_SETTINGS, NavGlyph.SETTINGS, UiConfig.PAGE_SETTINGS, () -> showPage(UiConfig.PAGE_SETTINGS)));
        content.add(navGap());
        content.add(navButton(Messages.NAV_ABOUT, NavGlyph.INFO, UiConfig.PAGE_ABOUT, () -> showPage(UiConfig.PAGE_ABOUT)));
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(UiConfig.SIDEBAR_WIDTH, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        JPanel wrapper = new SidebarSurfacePanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setPreferredSize(new Dimension(UiConfig.SIDEBAR_WIDTH, 0));
        wrapper.add(scroll, BorderLayout.CENTER);
        activateNav(dashboardButton);
        return wrapper;
    }

    private Component navGap() {
        return Box.createVerticalStrut(3);
    }

    private Component sidebarDivider() {
        JPanel divider = new JPanel();
        divider.setBackground(ModernThemePalette.border());
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(UiConfig.SIDEBAR_WIDTH - 36, 1));
        return divider;
    }

    private JLabel sidebarSectionLabel(String text) {
        JLabel label = sidebarLabel(text, Font.BOLD, 11);
        label.setForeground(ModernThemePalette.sidebarMutedText());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private CollapsibleNavigationSection navigationSection(String title) {
        CollapsibleNavigationSection section = new CollapsibleNavigationSection(title, false);
        section.setToggleAction(() -> toggleNavigationSection(section));
        navigationSections.add(section);
        return section;
    }

    private JButton addSectionNavigationButton(
            CollapsibleNavigationSection section,
            String text,
            NavGlyph glyph,
            String pageId,
            Runnable action
    ) {
        JButton button = navButton(text, glyph, pageId, action);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, UiConfig.NAV_CHILD_HEIGHT));
        button.setPreferredSize(new Dimension(UiConfig.SIDEBAR_WIDTH - 36, UiConfig.NAV_CHILD_HEIGHT));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setBorder(new EmptyBorder(8, 24, 8, 10));
        button.setIconTextGap(12);
        section.addNavigationButton(button);
        navigationSectionByButton.put(button, section);
        return button;
    }

    private void toggleNavigationSection(CollapsibleNavigationSection section) {
        boolean expand = !section.isExpanded();
        if (expand) {
            openNavigationSection(section);
        } else {
            section.setExpanded(false);
        }
    }

    private void openNavigationSectionFor(JButton button) {
        CollapsibleNavigationSection section = navigationSectionByButton.get(button);
        if (section != null) {
            openNavigationSection(section);
        }
    }

    private void openNavigationSection(CollapsibleNavigationSection target) {
        for (CollapsibleNavigationSection section : navigationSections) {
            section.setExpanded(section == target);
        }
    }

    private JButton navButton(String text, NavGlyph glyph, String pageId, Runnable action) {
        JButton button = new ModernNavButton(text, new NavIcon(glyph));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, UiConfig.NAV_HEIGHT));
        button.setPreferredSize(new Dimension(UiConfig.SIDEBAR_WIDTH - 36, UiConfig.NAV_HEIGHT));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(14);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setForeground(ModernThemePalette.sidebarText());
        button.setBackground(ModernThemePalette.sidebarStart());
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.addActionListener(event -> {
            activateNav(button);
            action.run();
        });
        navigationButtons.add(button);
        if (pageId != null && !pageId.isBlank()) {
            navigationByPage.putIfAbsent(pageId, button);
        }
        return button;
    }

    private void activateNav(JButton button) {
        activeNavigationButton = button;
        CollapsibleNavigationSection targetSection = navigationSectionByButton.get(button);
        if (targetSection != null) {
            openNavigationSection(targetSection);
        } else {
            for (CollapsibleNavigationSection section : navigationSections) {
                section.setExpanded(false);
            }
        }
        for (JButton navigationButton : navigationButtons) {
            boolean active = navigationButton == activeNavigationButton;
            navigationButton.setForeground(active ? ModernThemePalette.sidebarActiveText() : ModernThemePalette.sidebarText());
            if (navigationButton instanceof ModernNavButton modernNavButton) {
                modernNavButton.setActive(active);
            }
        }
        for (CollapsibleNavigationSection section : navigationSections) {
            section.setActiveChild(section.containsNavigationButton(activeNavigationButton));
        }
    }

    private JPanel buildPages() {
        pageContainer.add(buildDashboardPage(), UiConfig.PAGE_DASHBOARD);
        pageContainer.add(buildContactsPage(), UiConfig.PAGE_CONTACTS);
        pageContainer.add(buildGroupsPage(), UiConfig.PAGE_GROUPS);
        pageContainer.add(buildTagsPage(), UiConfig.PAGE_TAGS);
        pageContainer.add(buildSmartListsPage(), UiConfig.PAGE_SMART_LISTS);
        pageContainer.add(buildHistoryPage(), UiConfig.PAGE_HISTORY);
        pageContainer.add(buildMaintenancePage(), UiConfig.PAGE_MAINTENANCE);
        pageContainer.add(buildImportExportPage(), UiConfig.PAGE_IMPORT_EXPORT);
        pageContainer.add(buildTrashPage(), UiConfig.PAGE_TRASH);
        pageContainer.add(buildSyncPage(), UiConfig.PAGE_SYNC);
        pageContainer.add(buildBackupPage(), UiConfig.PAGE_BACKUP);
        pageContainer.add(buildSettingsPage(), UiConfig.PAGE_SETTINGS);
        pageContainer.add(buildAboutPage(), UiConfig.PAGE_ABOUT);
        return pageContainer;
    }

    private JPanel pagePanel() {
        JPanel panel = new JPanel(new BorderLayout(UiConfig.UI_GAP, UiConfig.UI_GAP));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(UiConfig.UI_PADDING, UiConfig.UI_PADDING, UiConfig.UI_PADDING, UiConfig.UI_PADDING));
        return panel;
    }

    private JPanel buildDashboardPage() {
        JPanel page = pagePanel();
        page.add(pageHeader(Messages.DASHBOARD_TITLE, Messages.DASHBOARD_SUBTITLE), BorderLayout.NORTH);

        VerticalScrollablePanel content = new VerticalScrollablePanel();

        ResponsiveCardGridPanel stats = new ResponsiveCardGridPanel(
                UiConfig.DASHBOARD_STATS_WIDE_COLUMNS,
                UiConfig.DASHBOARD_STATS_MEDIUM_COLUMNS,
                UiConfig.DASHBOARD_STATS_COMPACT_COLUMNS,
                UiConfig.DASHBOARD_STATS_MEDIUM_BREAKPOINT,
                UiConfig.DASHBOARD_STATS_COMPACT_BREAKPOINT,
                UiConfig.DASHBOARD_GRID_GAP,
                UiConfig.DASHBOARD_GRID_GAP
        );
        stats.addCard(statCard(Messages.STAT_TOTAL, Messages.STAT_TOTAL_HINT, totalValue, NavGlyph.CONTACTS));
        stats.addCard(statCard(Messages.STAT_FAVORITES, Messages.STAT_FAVORITES_HINT, favoriteValue, NavGlyph.STAR));
        stats.addCard(statCard(Messages.STAT_BIRTHDAYS, Messages.STAT_BIRTHDAYS_HINT, birthdayValue, NavGlyph.HISTORY));
        stats.addCard(statCard(Messages.STAT_FOLLOW_UP, Messages.STAT_FOLLOW_UP_HINT, followUpValue, NavGlyph.HISTORY));
        stats.addCard(statCard(Messages.STAT_COMPANIES, Messages.STAT_COMPANIES_HINT, companyValue, NavGlyph.GROUPS));
        stats.addCard(statCard(Messages.STAT_CITIES, Messages.STAT_CITIES_HINT, cityValue, NavGlyph.HOME));
        content.add(stats);
        content.add(Box.createVerticalStrut(UiConfig.DASHBOARD_SECTION_GAP));

        ResponsiveCardGridPanel primaryCards = new ResponsiveCardGridPanel(
                UiConfig.DASHBOARD_FEATURE_WIDE_COLUMNS,
                UiConfig.DASHBOARD_FEATURE_MEDIUM_COLUMNS,
                UiConfig.DASHBOARD_FEATURE_COMPACT_COLUMNS,
                UiConfig.DASHBOARD_FEATURE_MEDIUM_BREAKPOINT,
                UiConfig.DASHBOARD_FEATURE_COMPACT_BREAKPOINT,
                UiConfig.DASHBOARD_GRID_GAP,
                UiConfig.DASHBOARD_GRID_GAP
        );
        primaryCards.addCard(buildQuickActionsCard());
        primaryCards.addCard(buildSystemStatusCard());
        content.add(primaryCards);
        content.add(Box.createVerticalStrut(UiConfig.DASHBOARD_SECTION_GAP));
        content.add(buildDashboardWorkspaceCard());
        content.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        page.add(scrollPane, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildQuickActionsCard() {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 16));
        card.setMinimumSize(new Dimension(0, UiConfig.DASHBOARD_FEATURE_CARD_MIN_HEIGHT));

        JPanel heading = dashboardSectionHeading(
                NavGlyph.CONTACTS,
                Messages.DASHBOARD_QUICK_TITLE,
                Messages.DASHBOARD_QUICK_TEXT
        );
        card.add(heading, BorderLayout.NORTH);

        ResponsiveActionPanel actions = new ResponsiveActionPanel(
                UiConfig.DASHBOARD_ACTION_WIDE_COLUMNS,
                UiConfig.DASHBOARD_ACTION_COMPACT_COLUMNS,
                UiConfig.DASHBOARD_ACTION_COMPACT_BREAKPOINT,
                UiConfig.QUICK_ACTION_GAP,
                UiConfig.QUICK_ACTION_GAP
        );
        JButton newButton = primaryButton(Messages.NEW_CONTACT_BUTTON);
        JButton contactsButton = secondaryButton(Messages.OPEN_CONTACTS_BUTTON);
        newButton.addActionListener(event -> openContactsForNew());
        contactsButton.addActionListener(event -> showPage(UiConfig.PAGE_CONTACTS));
        actions.add(newButton);
        actions.add(contactsButton);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildSystemStatusCard() {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setMinimumSize(new Dimension(0, UiConfig.DASHBOARD_FEATURE_CARD_MIN_HEIGHT));
        card.add(dashboardSectionHeading(
                NavGlyph.SETTINGS,
                Messages.DASHBOARD_STATUS_TITLE,
                Messages.DASHBOARD_STATUS_TEXT
        ), BorderLayout.NORTH);

        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.add(statusRow(NavGlyph.SYNC, Messages.DASHBOARD_STATUS_SYNC, dashboardSyncStatusLabel));
        rows.add(Box.createVerticalStrut(8));
        rows.add(statusRow(NavGlyph.BACKUP, Messages.DASHBOARD_STATUS_BACKUP, dashboardBackupStatusLabel));
        rows.add(Box.createVerticalStrut(8));
        JLabel data = new JLabel(Messages.DASHBOARD_STATUS_READY, SwingConstants.RIGHT);
        data.setForeground(ModernThemePalette.accentStrong());
        rows.add(statusRow(NavGlyph.HOME, Messages.DASHBOARD_STATUS_DATABASE, data));
        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildDashboardWorkspaceCard() {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 16));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(Messages.DASHBOARD_WORKSPACE_TITLE);
        title.setForeground(ModernThemePalette.textPrimary());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 19f));
        JLabel description = new JLabel("<html><div style='width:760px;'>" + Messages.DASHBOARD_WORKSPACE_TEXT + "</div></html>");
        description.setForeground(ModernThemePalette.textSecondary());
        description.setFont(description.getFont().deriveFont(Font.PLAIN, 13f));
        heading.add(title);
        heading.add(Box.createVerticalStrut(7));
        heading.add(description);
        card.add(heading, BorderLayout.NORTH);

        ResponsiveCardGridPanel capabilities = new ResponsiveCardGridPanel(
                UiConfig.DASHBOARD_CAPABILITY_WIDE_COLUMNS,
                UiConfig.DASHBOARD_CAPABILITY_MEDIUM_COLUMNS,
                UiConfig.DASHBOARD_CAPABILITY_COMPACT_COLUMNS,
                UiConfig.DASHBOARD_CAPABILITY_MEDIUM_BREAKPOINT,
                UiConfig.DASHBOARD_CAPABILITY_COMPACT_BREAKPOINT,
                UiConfig.DASHBOARD_GRID_GAP,
                UiConfig.DASHBOARD_GRID_GAP
        );
        capabilities.addCard(dashboardCapabilityItem(NavGlyph.SMART, Messages.DASHBOARD_FEATURE_FIND_TITLE, Messages.DASHBOARD_FEATURE_FIND_TEXT));
        capabilities.addCard(dashboardCapabilityItem(NavGlyph.GROUPS, Messages.DASHBOARD_FEATURE_ORGANIZE_TITLE, Messages.DASHBOARD_FEATURE_ORGANIZE_TEXT));
        capabilities.addCard(dashboardCapabilityItem(NavGlyph.BACKUP, Messages.DASHBOARD_FEATURE_PROTECT_TITLE, Messages.DASHBOARD_FEATURE_PROTECT_TEXT));
        card.add(capabilities, BorderLayout.CENTER);
        return card;
    }

    private JPanel dashboardSectionHeading(NavGlyph glyph, String title, String description) {
        JPanel heading = new JPanel(new BorderLayout(12, 0));
        heading.setOpaque(false);
        heading.add(new DashboardIconBadge(glyph), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(ModernThemePalette.textPrimary());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        JLabel descriptionLabel = new JLabel("<html><div style='width:390px;'>" + description + "</div></html>");
        descriptionLabel.setForeground(ModernThemePalette.textSecondary());
        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.PLAIN, 12.5f));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(5));
        text.add(descriptionLabel);
        heading.add(text, BorderLayout.CENTER);
        return heading;
    }

    private JPanel statusRow(NavGlyph glyph, String title, JLabel value) {
        DashboardStatusRowPanel row = new DashboardStatusRowPanel();
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(new EmptyBorder(9, 11, 9, 11));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, UiConfig.DASHBOARD_STATUS_ROW_HEIGHT));
        row.setPreferredSize(new Dimension(0, UiConfig.DASHBOARD_STATUS_ROW_HEIGHT));

        JLabel icon = new JLabel(new NavIcon(glyph));
        icon.setForeground(ModernThemePalette.accentStrong());
        icon.setPreferredSize(new Dimension(UiConfig.DASHBOARD_ICON_SIZE + 4, UiConfig.DASHBOARD_ICON_SIZE + 4));
        row.add(icon, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(ModernThemePalette.textPrimary());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13f));
        row.add(titleLabel, BorderLayout.CENTER);

        value.setFont(value.getFont().deriveFont(Font.BOLD, 12.5f));
        value.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JPanel statCard(String title, String hint, JLabel value, NavGlyph glyph) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setMinimumSize(new Dimension(UiConfig.STAT_CARD_MIN_WIDTH, UiConfig.DASHBOARD_STAT_CARD_HEIGHT));
        card.setPreferredSize(new Dimension(UiConfig.STAT_CARD_MIN_WIDTH, UiConfig.DASHBOARD_STAT_CARD_HEIGHT));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.add(new DashboardIconBadge(glyph), BorderLayout.WEST);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 12f));
        titleLabel.setForeground(ModernThemePalette.textSecondary());
        top.add(titleLabel, BorderLayout.CENTER);
        card.add(top, BorderLayout.NORTH);

        value.setForeground(ModernThemePalette.textPrimary());
        card.add(value, BorderLayout.CENTER);

        JLabel hintLabel = new JLabel(hint);
        hintLabel.setForeground(ModernThemePalette.textSecondary());
        hintLabel.setFont(hintLabel.getFont().deriveFont(Font.PLAIN, 11f));
        card.add(hintLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel dashboardCapabilityItem(NavGlyph glyph, String title, String description) {
        DashboardStatusRowPanel item = new DashboardStatusRowPanel();
        item.setLayout(new BorderLayout(12, 0));
        item.setBorder(new EmptyBorder(14, 14, 14, 14));
        item.setMinimumSize(new Dimension(0, UiConfig.DASHBOARD_CAPABILITY_MIN_HEIGHT));
        item.add(new DashboardIconBadge(glyph), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(ModernThemePalette.textPrimary());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13.5f));
        JLabel body = new JLabel("<html><div style='width:250px;'>" + description + "</div></html>");
        body.setForeground(ModernThemePalette.textSecondary());
        body.setFont(body.getFont().deriveFont(Font.PLAIN, 12f));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(5));
        text.add(body);
        item.add(text, BorderLayout.CENTER);
        return item;
    }

    private JPanel modernCard() {
        RoundedCardPanel card = new RoundedCardPanel();
        card.setBorder(new EmptyBorder(UiConfig.CARD_PADDING, UiConfig.CARD_PADDING, UiConfig.CARD_PADDING, UiConfig.CARD_PADDING));
        return card;
    }

    private JPanel buildContactsPage() {
        JPanel page = pagePanel();
        page.add(pageHeader(Messages.HEADER_TITLE, Messages.HEADER_SUBTITLE), BorderLayout.NORTH);

        JSplitPane workspace = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildContactBrowserCard(),
                buildContactWorkspaceCard()
        );
        workspace.setBorder(BorderFactory.createEmptyBorder());
        workspace.setOpaque(false);
        workspace.setContinuousLayout(true);
        workspace.setOneTouchExpandable(false);
        workspace.setDividerSize(UiConfig.CONTACT_SPLIT_DIVIDER_SIZE);
        workspace.setResizeWeight(UiConfig.CONTACT_BROWSER_WEIGHT);
        workspace.setDividerLocation(UiConfig.CONTACT_BROWSER_DEFAULT_WIDTH);
        workspace.setMinimumSize(new Dimension(0, 0));
        workspace.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                clampContactWorkspaceDivider(workspace);
            }
        });

        page.add(workspace, BorderLayout.CENTER);
        return page;
    }

    private void clampContactWorkspaceDivider(JSplitPane workspace) {
        int availableWidth = workspace.getWidth();
        if (availableWidth <= 0) {
            return;
        }
        int dividerSize = workspace.getDividerSize();
        int minLocation = UiConfig.CONTACT_BROWSER_MIN_WIDTH;
        int maxLocation = Math.min(
                UiConfig.CONTACT_BROWSER_MAX_WIDTH,
                availableWidth - UiConfig.CONTACT_WORKSPACE_MIN_WIDTH - dividerSize
        );
        if (maxLocation < minLocation) {
            return;
        }
        int weightedLocation = (int) Math.round(availableWidth * UiConfig.CONTACT_BROWSER_WEIGHT);
        int targetLocation = Math.max(minLocation, Math.min(maxLocation, weightedLocation));
        if (workspace.getDividerLocation() != targetLocation) {
            workspace.setDividerLocation(targetLocation);
        }
    }

    private void applyProfileHeaderDensity(int width) {
        boolean compact = width > 0 && width < UiConfig.PROFILE_COMPACT_BREAKPOINT;
        int avatarSize = compact ? UiConfig.PROFILE_AVATAR_COMPACT_SIZE : UiConfig.PROFILE_AVATAR_SIZE;
        Dimension avatarDimension = new Dimension(avatarSize, avatarSize);
        avatarPanel.setPreferredSize(avatarDimension);
        avatarPanel.setMinimumSize(avatarDimension);
        avatarPanel.setMaximumSize(avatarDimension);
        detailNameLabel.setFont(detailNameLabel.getFont().deriveFont(
                Font.BOLD,
                compact ? UiConfig.PROFILE_NAME_COMPACT_FONT_SIZE : UiConfig.PROFILE_NAME_FONT_SIZE
        ));
        detailCompanyLabel.setFont(detailCompanyLabel.getFont().deriveFont(
                Font.PLAIN,
                compact ? UiConfig.PROFILE_COMPANY_COMPACT_FONT_SIZE : UiConfig.PROFILE_COMPANY_FONT_SIZE
        ));
        avatarPanel.revalidate();
    }

    private void configureFilterDrawer() {
        filterDrawerPanel.removeAll();
        filterDrawerPanel.setOpaque(false);
        filterDrawerPanel.setLayout(new GridLayout(0, 2, UiConfig.FILTER_PANEL_GAP, UiConfig.FILTER_PANEL_GAP));
        filterDrawerPanel.setVisible(false);
        filterDrawerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, UiConfig.FILTER_DRAWER_MAX_HEIGHT));

        configureFilterCombo(companyFilterCombo);
        configureFilterCombo(cityFilterCombo);
        configureFilterCombo(categoryFilterCombo);
        configureFilterCombo(groupFilterCombo);
        configureFilterCombo(tagFilterCombo);
        configureFilterCombo(sortFilterCombo);
        sortFilterCombo.setRenderer(new ContactSortCellRenderer());
        sortFilterCombo.setSelectedItem(ContactSort.defaultSort());

        filterDrawerPanel.add(filterControlCell(Messages.FILTER_COMPANY_LABEL, companyFilterCombo));
        filterDrawerPanel.add(filterControlCell(Messages.FILTER_CITY_LABEL, cityFilterCombo));
        filterDrawerPanel.add(filterControlCell(Messages.FILTER_CATEGORY_LABEL, categoryFilterCombo));
        filterDrawerPanel.add(filterControlCell(Messages.FILTER_GROUP_LABEL, groupFilterCombo));
        filterDrawerPanel.add(filterControlCell(Messages.FILTER_TAG_LABEL, tagFilterCombo));
        filterDrawerPanel.add(filterControlCell(Messages.SORT_LABEL, sortFilterCombo));
    }

    private void configureFilterCombo(JComboBox<?> comboBox) {
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, UiConfig.FILTER_CONTROL_HEIGHT));
        comboBox.setPreferredSize(new Dimension(150, UiConfig.FILTER_CONTROL_HEIGHT));
        comboBox.putClientProperty(UiConfig.CLIENT_ROUND_RECT, true);
    }

    private JPanel filterControlCell(String label, JComponent control) {
        JPanel cell = new JPanel(new BorderLayout(0, 3));
        cell.setOpaque(false);

        JLabel labelView = new JLabel(label);
        labelView.setForeground(ModernThemePalette.textSecondary());
        labelView.setFont(labelView.getFont().deriveFont(Font.BOLD, 10.5f));
        cell.add(labelView, BorderLayout.NORTH);
        cell.add(control, BorderLayout.CENTER);
        return cell;
    }

    private String selectedTextFilter(JComboBox<String> comboBox) {
        Object selected = comboBox.getSelectedItem();
        return comboBox.getSelectedIndex() <= 0 || selected == null ? "" : selected.toString().trim();
    }

    private Long selectedIdFilter(JComboBox<ContactFilterOption> comboBox) {
        ContactFilterOption selected = (ContactFilterOption) comboBox.getSelectedItem();
        return selected == null || selected.all() ? null : selected.id();
    }

    private ContactSort selectedContactSort() {
        Object selected = sortFilterCombo.getSelectedItem();
        return selected instanceof ContactSort sort ? sort : ContactSort.defaultSort();
    }

    private void fillTextFilter(JComboBox<String> comboBox, String allLabel, List<String> values, String selectedValue) {
        comboBox.removeAllItems();
        comboBox.addItem(allLabel);
        values.forEach(comboBox::addItem);
        if (selectedValue != null && !selectedValue.isBlank()) {
            for (int index = 1; index < comboBox.getItemCount(); index++) {
                String value = comboBox.getItemAt(index);
                if (value.equalsIgnoreCase(selectedValue)) {
                    comboBox.setSelectedIndex(index);
                    return;
                }
            }
        }
        comboBox.setSelectedIndex(0);
    }

    private void fillIdFilter(
            JComboBox<ContactFilterOption> comboBox,
            String allLabel,
            List<ContactFilterOption> values,
            Long selectedId
    ) {
        comboBox.removeAllItems();
        comboBox.addItem(new ContactFilterOption(null, allLabel));
        values.forEach(comboBox::addItem);
        if (selectedId != null) {
            for (int index = 1; index < comboBox.getItemCount(); index++) {
                ContactFilterOption value = comboBox.getItemAt(index);
                if (value.id() != null && value.id().longValue() == selectedId.longValue()) {
                    comboBox.setSelectedIndex(index);
                    return;
                }
            }
        }
        comboBox.setSelectedIndex(0);
    }

    private void selectTextFilter(JComboBox<String> comboBox, String value) {
        String target = value == null ? "" : value.trim();
        comboBox.setSelectedIndex(0);
        if (target.isBlank()) return;
        for (int index = 1; index < comboBox.getItemCount(); index++) {
            String item = comboBox.getItemAt(index);
            if (item != null && item.equalsIgnoreCase(target)) {
                comboBox.setSelectedIndex(index);
                return;
            }
        }
    }

    private void selectIdFilter(JComboBox<ContactFilterOption> comboBox, Long id) {
        comboBox.setSelectedIndex(0);
        if (id == null) return;
        for (int index = 1; index < comboBox.getItemCount(); index++) {
            ContactFilterOption item = comboBox.getItemAt(index);
            if (item != null && item.id() != null && item.id().equals(id)) {
                comboBox.setSelectedIndex(index);
                return;
            }
        }
    }

    private void selectSavedViewName(String name) {
        for (int index = 1; index < savedViewCombo.getItemCount(); index++) {
            String item = savedViewCombo.getItemAt(index);
            if (item != null && item.equalsIgnoreCase(name)) {
                savedViewCombo.setSelectedIndex(index);
                return;
            }
        }
        savedViewCombo.setSelectedIndex(0);
    }

    private void clearSavedViewSelection() {
        if (updatingSavedViewControls || updatingFilterControls || savedViewCombo.getSelectedIndex() <= 0) return;
        updatingSavedViewControls = true;
        try {
            savedViewCombo.setSelectedIndex(0);
        } finally {
            updatingSavedViewControls = false;
        }
        updateSavedViewActionState();
    }

    private void updateSavedViewActionState() {
        boolean selected = savedViewCombo.getSelectedIndex() > 0;
        manageSavedViewButton.setEnabled(selected);
        savedViewPickerButton.setToolTipText(selected
                ? String.format(Messages.SAVED_VIEW_SELECTED_TOOLTIP_FORMAT, selectedSavedContactViewName())
                : Messages.SAVED_VIEW_PICKER_TOOLTIP);
        renameSavedViewMenuItem.setEnabled(selected);
        copySavedViewMenuItem.setEnabled(selected);
        defaultSavedViewMenuItem.setEnabled(selected);
        deleteSavedViewMenuItem.setEnabled(selected);
        String selectedName = selectedSavedContactViewName();
        boolean selectedIsDefault = !selectedName.isBlank() && selectedName.equalsIgnoreCase(defaultSavedViewName);
        defaultSavedViewMenuItem.setText(selectedIsDefault
                ? Messages.SAVED_VIEW_CLEAR_DEFAULT_BUTTON
                : Messages.SAVED_VIEW_SET_DEFAULT_BUTTON);
    }

    private void toggleFilterDrawer() {
        boolean visible = !filterDrawerPanel.isVisible();
        filterDrawerPanel.setVisible(visible);
        rebuildActiveFilterChips();
        filterDrawerPanel.getParent().revalidate();
        filterDrawerPanel.getParent().repaint();
    }

    private void handleStructuredFilterChanged() {
        if (updatingFilterControls) return;
        clearSavedViewSelection();
        rebuildActiveFilterChips();
        searchAction.run();
    }

    private void clearStructuredFilters() {
        clearSavedViewSelection();
        updatingFilterControls = true;
        try {
            favoritesOnlyCheckBox.setSelected(false);
            resetFilterCombo(companyFilterCombo);
            resetFilterCombo(cityFilterCombo);
            resetFilterCombo(categoryFilterCombo);
            resetFilterCombo(groupFilterCombo);
            resetFilterCombo(tagFilterCombo);
        } finally {
            updatingFilterControls = false;
        }
        rebuildActiveFilterChips();
        JButton contactsButton = navigationByPage.get(UiConfig.PAGE_CONTACTS);
        if (contactsButton != null) activateNav(contactsButton);
        searchAction.run();
    }

    private void resetFilterCombo(JComboBox<?> comboBox) {
        if (comboBox.getItemCount() > 0) comboBox.setSelectedIndex(0);
    }

    private void rebuildActiveFilterChips() {
        activeFilterChipPanel.removeAll();
        ContactFilter filter = readContactFilter();

        if (filter.favoritesOnly()) {
            addFilterChip(Messages.FILTER_CHIP_FAVORITES, () -> favoritesOnlyCheckBox.setSelected(false));
        }
        if (!filter.company().isBlank()) {
            addFilterChip(String.format(Messages.FILTER_CHIP_FORMAT, Messages.FILTER_COMPANY_LABEL, filter.company()),
                    () -> resetFilterCombo(companyFilterCombo));
        }
        if (!filter.city().isBlank()) {
            addFilterChip(String.format(Messages.FILTER_CHIP_FORMAT, Messages.FILTER_CITY_LABEL, filter.city()),
                    () -> resetFilterCombo(cityFilterCombo));
        }
        if (!filter.category().isBlank()) {
            addFilterChip(String.format(Messages.FILTER_CHIP_FORMAT, Messages.FILTER_CATEGORY_LABEL, filter.category()),
                    () -> resetFilterCombo(categoryFilterCombo));
        }
        ContactFilterOption group = (ContactFilterOption) groupFilterCombo.getSelectedItem();
        if (group != null && !group.all()) {
            addFilterChip(String.format(Messages.FILTER_CHIP_FORMAT, Messages.FILTER_GROUP_LABEL, group.label()),
                    () -> resetFilterCombo(groupFilterCombo));
        }
        ContactFilterOption tag = (ContactFilterOption) tagFilterCombo.getSelectedItem();
        if (tag != null && !tag.all()) {
            addFilterChip(String.format(Messages.FILTER_CHIP_FORMAT, Messages.FILTER_TAG_LABEL, tag.label()),
                    () -> resetFilterCombo(tagFilterCombo));
        }
        if (filter.sort() != ContactSort.defaultSort()) {
            addFilterChip(String.format(Messages.FILTER_CHIP_FORMAT, Messages.SORT_LABEL, ContactSortText.display(filter.sort())),
                    () -> sortFilterCombo.setSelectedItem(ContactSort.defaultSort()));
        }

        boolean hasActiveChips = activeFilterChipPanel.getComponentCount() > 0;
        activeFilterChipPanel.setVisible(hasActiveChips && !filterDrawerPanel.isVisible());
        filterToggleButton.setText(filterDrawerPanel.isVisible()
                ? Messages.FILTERS_HIDE_BUTTON
                : (filter.activeStructuredFilterCount() > 0
                    ? String.format(Messages.FILTERS_ACTIVE_FORMAT, filter.activeStructuredFilterCount())
                    : Messages.FILTERS_BUTTON));
        activeFilterChipPanel.revalidate();
        activeFilterChipPanel.repaint();
    }

    private void addFilterChip(String label, Runnable resetAction) {
        JButton chip = ModernButtons.chip(String.format(Messages.FILTER_CHIP_REMOVE_FORMAT, label));
        chip.setToolTipText(label);
        chip.addActionListener(event -> {
            clearSavedViewSelection();
            updatingFilterControls = true;
            try {
                resetAction.run();
            } finally {
                updatingFilterControls = false;
            }
            rebuildActiveFilterChips();
            if (!favoritesOnlyCheckBox.isSelected()) {
                JButton contactsButton = navigationByPage.get(UiConfig.PAGE_CONTACTS);
                if (contactsButton != null) activateNav(contactsButton);
            }
            searchAction.run();
        });
        activeFilterChipPanel.add(chip);
    }

    private JPanel buildContactBrowserCard() {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, UiConfig.UI_SMALL_GAP));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.setMinimumSize(new Dimension(UiConfig.CONTACT_BROWSER_MIN_WIDTH, 0));

        JPanel heading = new JPanel(new BorderLayout(UiConfig.UI_SMALL_GAP, 0));
        heading.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(Messages.CONTACT_BROWSER_TITLE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        contactCountLabel.setForeground(ModernThemePalette.textSecondary());
        contactCountLabel.setFont(contactCountLabel.getFont().deriveFont(Font.PLAIN, 11.5f));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(contactCountLabel);
        heading.add(titleBox, BorderLayout.CENTER);

        clearButton.setFont(clearButton.getFont().deriveFont(Font.BOLD, 11.5f));
        clearButton.setToolTipText(Messages.NEW_CONTACT_BUTTON);
        heading.add(clearButton, BorderLayout.EAST);

        searchField.putClientProperty(UiConfig.CLIENT_SHOW_CLEAR_BUTTON, true);

        favoritesOnlyCheckBox.setText(Messages.CONTACT_BROWSER_FAVORITES_BUTTON);
        favoritesOnlyCheckBox.setFont(favoritesOnlyCheckBox.getFont().deriveFont(Font.BOLD, 11.5f));
        favoritesOnlyCheckBox.setFocusPainted(false);
        favoritesOnlyCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
        favoritesOnlyCheckBox.putClientProperty(UiConfig.CLIENT_BUTTON_TYPE, UiConfig.BUTTON_TYPE_ROUND_RECT);
        favoritesOnlyCheckBox.setToolTipText(Messages.FAVORITES_ONLY);

        savedViewPickerButton.setToolTipText(Messages.SAVED_VIEW_PICKER_TOOLTIP);
        contactBrowserMoreButton.setToolTipText(Messages.CONTACT_BROWSER_MORE_TOOLTIP);

        JPanel compactToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        compactToolbar.setOpaque(false);
        compactToolbar.add(favoritesOnlyCheckBox);
        compactToolbar.add(savedViewPickerButton);
        compactToolbar.add(filterToggleButton);
        compactToolbar.add(contactBrowserMoreButton);

        configureFilterDrawer();

        activeFilterChipPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        activeFilterChipPanel.setVisible(false);

        JPanel searchAndFilters = new JPanel();
        searchAndFilters.setOpaque(false);
        searchAndFilters.setLayout(new BoxLayout(searchAndFilters, BoxLayout.Y_AXIS));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        compactToolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterDrawerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchAndFilters.add(searchField);
        searchAndFilters.add(Box.createVerticalStrut(7));
        searchAndFilters.add(compactToolbar);
        searchAndFilters.add(Box.createVerticalStrut(5));
        searchAndFilters.add(filterDrawerPanel);
        searchAndFilters.add(activeFilterChipPanel);

        JPanel north = new JPanel(new BorderLayout(0, 10));
        north.setOpaque(false);
        north.add(heading, BorderLayout.NORTH);
        north.add(searchAndFilters, BorderLayout.SOUTH);
        card.add(north, BorderLayout.NORTH);

        JScrollPane listScroll = new JScrollPane(
                contactList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        listScroll.getVerticalScrollBar().setUnitIncrement(18);
        listScroll.getViewport().setBackground(ModernThemePalette.surface());
        card.add(listScroll, BorderLayout.CENTER);
        return card;
    }

    private void showSavedViewPickerMenu() {
        JPopupMenu menu = new JPopupMenu();
        if (savedViewCombo.getItemCount() <= 1) {
            JMenuItem empty = new JMenuItem(Messages.SAVED_VIEW_EMPTY);
            empty.setEnabled(false);
            menu.add(empty);
        } else {
            for (int index = 1; index < savedViewCombo.getItemCount(); index++) {
                String name = savedViewCombo.getItemAt(index);
                String label = name != null && name.equalsIgnoreCase(defaultSavedViewName)
                        ? Messages.SAVED_VIEW_DEFAULT_PREFIX + name
                        : name;
                JMenuItem viewItem = new JMenuItem(label);
                int targetIndex = index;
                viewItem.addActionListener(event -> savedViewCombo.setSelectedIndex(targetIndex));
                menu.add(viewItem);
            }
        }
        menu.addSeparator();
        JMenuItem save = new JMenuItem(Messages.SAVED_VIEW_SAVE_MENU);
        save.addActionListener(event -> saveSavedViewAction.run());
        menu.add(save);

        JMenuItem manage = new JMenuItem(Messages.SAVED_VIEW_MANAGE_MENU);
        manage.setEnabled(savedViewCombo.getSelectedIndex() > 0);
        manage.addActionListener(event -> SwingUtilities.invokeLater(() -> {
            updateSavedViewActionState();
            savedViewMenu.show(savedViewPickerButton, 0, savedViewPickerButton.getHeight());
        }));
        menu.add(manage);
        menu.show(savedViewPickerButton, 0, savedViewPickerButton.getHeight());
    }

    private void showContactBrowserMoreMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem clearFilters = new JMenuItem(Messages.CONTACT_BROWSER_CLEAR_FILTERS_MENU);
        clearFilters.setEnabled(readContactFilter().hasStructuredFilters());
        clearFilters.addActionListener(event -> clearStructuredFilters());
        menu.add(clearFilters);
        menu.addSeparator();
        JMenuItem refresh = new JMenuItem(Messages.CONTACT_BROWSER_REFRESH_MENU);
        refresh.addActionListener(event -> refreshAction.run());
        menu.add(refresh);
        menu.show(contactBrowserMoreButton, 0, contactBrowserMoreButton.getHeight());
    }

    private JPanel buildContactWorkspaceCard() {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, UiConfig.UI_GAP));
        card.setMinimumSize(new Dimension(UiConfig.CONTACT_WORKSPACE_MIN_WIDTH, 0));
        contactProfileSummaryPanel = buildProfileSummaryPanel();
        card.add(contactProfileSummaryPanel, BorderLayout.NORTH);

        contactModeContainer.setOpaque(false);
        contactModeContainer.add(buildContactOverviewPanel(), UiConfig.CONTACT_MODE_VIEW);
        contactModeContainer.add(buildFormPanel(), UiConfig.CONTACT_MODE_EDIT);
        contactModeContainer.add(buildBulkSelectionPanel(), UiConfig.CONTACT_MODE_BULK);
        card.add(contactModeContainer, BorderLayout.CENTER);
        contactModeLayout.show(contactModeContainer, UiConfig.CONTACT_MODE_EDIT);
        return card;
    }

    private JPanel buildBulkSelectionPanel() {
        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.setBorder(new EmptyBorder(2, 2, 8, 8));

        JPanel summary = modernCard();
        summary.setLayout(new BorderLayout(0, 14));
        summary.setBorder(new EmptyBorder(20, 20, 20, 20));
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(Messages.BULK_SELECTION_TITLE);
        title.setForeground(ModernThemePalette.textPrimary());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        bulkSelectionCountLabel.setForeground(ModernThemePalette.accentStrong());
        bulkSelectionCountLabel.setFont(bulkSelectionCountLabel.getFont().deriveFont(Font.BOLD, 14f));
        bulkSelectionSummaryLabel.setForeground(ModernThemePalette.textSecondary());
        bulkSelectionSummaryLabel.setFont(bulkSelectionSummaryLabel.getFont().deriveFont(Font.PLAIN, 12.5f));
        heading.add(title);
        heading.add(Box.createVerticalStrut(7));
        heading.add(bulkSelectionCountLabel);
        heading.add(Box.createVerticalStrut(5));
        heading.add(bulkSelectionSummaryLabel);
        summary.add(heading, BorderLayout.CENTER);
        content.add(summary);
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));

        content.add(buildBulkActionCard(
                Messages.BULK_EDIT_SECTION,
                bulkCompanyButton, bulkCategoryButton, bulkFavoriteButton, bulkUnfavoriteButton
        ));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));
        content.add(buildBulkActionCard(
                Messages.BULK_MEMBERSHIP_SECTION,
                bulkAddGroupButton, bulkRemoveGroupButton, bulkAddTagButton, bulkRemoveTagButton
        ));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));
        content.add(buildBulkActionCard(
                Messages.BULK_TRANSFER_SECTION,
                bulkExportVcfButton, bulkExportCsvButton, bulkTrashButton
        ));
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildBulkActionCard(String title, JButton... buttons) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        JLabel heading = new JLabel(title);
        heading.setForeground(ModernThemePalette.textSecondary());
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 11.5f));
        card.add(heading, BorderLayout.NORTH);

        ResponsiveActionPanel actions = new ResponsiveActionPanel(
                UiConfig.BULK_ACTION_WIDE_COLUMNS, UiConfig.BULK_ACTION_COMPACT_COLUMNS,
                UiConfig.BULK_ACTION_COMPACT_BREAKPOINT, UiConfig.UI_SMALL_GAP, UiConfig.UI_SMALL_GAP
        );
        for (JButton button : buttons) actions.add(button);
        card.add(actions, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildProfileSummaryPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 8, 0));

        JPanel hero = new JPanel(new BorderLayout(18, 0));
        hero.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(UiConfig.PROFILE_AVATAR_SIZE, UiConfig.PROFILE_AVATAR_SIZE));
        avatarPanel.setMinimumSize(new Dimension(UiConfig.PROFILE_AVATAR_SIZE, UiConfig.PROFILE_AVATAR_SIZE));
        avatarPanel.setMaximumSize(new Dimension(UiConfig.PROFILE_AVATAR_SIZE, UiConfig.PROFILE_AVATAR_SIZE));
        hero.add(avatarPanel, BorderLayout.WEST);

        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        detailNameLabel.setFont(detailNameLabel.getFont().deriveFont(Font.BOLD, UiConfig.PROFILE_NAME_FONT_SIZE));
        detailNameLabel.setForeground(ModernThemePalette.textPrimary());
        detailNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailCompanyLabel.setFont(detailCompanyLabel.getFont().deriveFont(Font.PLAIN, UiConfig.PROFILE_COMPANY_FONT_SIZE));
        detailCompanyLabel.setForeground(ModernThemePalette.textSecondary());
        detailCompanyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailFavoriteLabel.setVisible(false);
        detailFavoriteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        identity.add(Box.createVerticalGlue());
        identity.add(detailNameLabel);
        identity.add(Box.createVerticalStrut(4));
        identity.add(detailCompanyLabel);
        identity.add(Box.createVerticalStrut(10));
        identity.add(detailFavoriteLabel);
        identity.add(Box.createVerticalGlue());
        hero.add(identity, BorderLayout.CENTER);

        editContactButton.setVisible(false);
        JPanel editWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        editWrap.setOpaque(false);
        editWrap.add(editContactButton);
        hero.add(editWrap, BorderLayout.EAST);
        hero.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                applyProfileHeaderDensity(hero.getWidth());
            }
        });

        wrapper.add(hero, BorderLayout.NORTH);

        quickActionsPanel.setOpaque(false);
        quickActionsPanel.add(quickCallButton);
        quickActionsPanel.add(quickWhatsAppButton);
        quickActionsPanel.add(quickEmailButton);
        quickActionsPanel.add(quickCopyButton);
        quickActionsPanel.setVisible(false);
        wrapper.add(quickActionsPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildContactOverviewPanel() {
        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.setBorder(new EmptyBorder(2, 2, 8, 8));

        ResponsiveInfoPanel basic = new ResponsiveInfoPanel();
        basic.addField(Messages.CATEGORY_LABEL, detailCategoryLabel);
        basic.addField(Messages.BIRTHDAY_LABEL, detailBirthdayLabel);
        content.add(profileSection(Messages.FORM_BASIC_SECTION_TITLE, Messages.PROFILE_BASIC_SUBTITLE, basic));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));

        ResponsiveInfoPanel reminders = new ResponsiveInfoPanel();
        reminders.addField(Messages.PROFILE_BIRTHDAY_REMINDER_LABEL, detailBirthdayReminderLabel);
        reminders.addFullField(Messages.PROFILE_IMPORTANT_DATES_LABEL, detailImportantDatesLabel);
        reminders.addField(Messages.PROFILE_KEEP_IN_TOUCH_LABEL, detailKeepInTouchLabel);
        reminders.addFullField(Messages.PROFILE_REMINDER_TITLE, detailReminderStatusLabel);
        content.add(profileSection(Messages.PROFILE_REMINDER_TITLE, Messages.PROFILE_REMINDER_SUBTITLE, reminders));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));

        ResponsiveInfoPanel contact = new ResponsiveInfoPanel();
        contact.addField(Messages.PHONE_METHODS_TITLE, detailPhoneLabel);
        contact.addField(Messages.EMAIL_METHODS_TITLE, detailEmailLabel);
        content.add(profileSection(Messages.TAB_CONTACT, Messages.PROFILE_CONTACT_SUBTITLE, contact));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));

        content.add(buildContactActivitySection());
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));

        ResponsiveInfoPanel work = new ResponsiveInfoPanel();
        work.addField(Messages.COMPANY_LABEL, detailCompanyInfoLabel);
        work.addField(Messages.JOB_TITLE_LABEL, detailJobTitleInfoLabel);
        work.addFullField(Messages.WEBSITE_LABEL, detailWebsiteInfoLabel);
        content.add(profileSection(Messages.FORM_WORK_SECTION_TITLE, Messages.PROFILE_WORK_SUBTITLE, work));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));

        ResponsiveInfoPanel address = new ResponsiveInfoPanel();
        address.addFullField(Messages.ADDRESS_LABEL, detailAddressInfoLabel);
        address.addField(Messages.DISTRICT_LABEL, detailDistrictInfoLabel);
        address.addField(Messages.CITY_LABEL, detailCityInfoLabel);
        address.addField(Messages.POSTAL_CODE_LABEL, detailPostalCodeInfoLabel);
        address.addField(Messages.COUNTRY_LABEL, detailCountryInfoLabel);
        content.add(profileSection(Messages.FORM_ADDRESS_SECTION_TITLE, Messages.PROFILE_ADDRESS_SUBTITLE, address));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));

        ResponsiveInfoPanel other = new ResponsiveInfoPanel();
        other.addFullField(Messages.NOTES_LABEL, detailNotesInfoLabel);
        detailChipsPanel.setOpaque(false);
        other.addFullField(Messages.PROFILE_MEMBERSHIP_LABEL, detailChipsPanel);
        content.add(profileSection(Messages.FORM_OTHER_SECTION_TITLE, Messages.PROFILE_OTHER_SUBTITLE, other));
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildContactActivitySection() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, UiConfig.ACTIVITY_FILTER_GAP, 0));
        filters.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        configureActivityFilterButton(activityAllButton, ContactActivityFilter.ALL, group);
        configureActivityFilterButton(activityChangesButton, ContactActivityFilter.CHANGES, group);
        configureActivityFilterButton(activityTransferButton, ContactActivityFilter.TRANSFER, group);
        configureActivityFilterButton(activityRestoreButton, ContactActivityFilter.RESTORE, group);
        filters.add(activityAllButton);
        filters.add(activityChangesButton);
        filters.add(activityTransferButton);
        filters.add(activityRestoreButton);
        filters.setAlignmentX(Component.LEFT_ALIGNMENT);

        contactActivityTimelinePanel.setOpaque(false);
        contactActivityTimelinePanel.setLayout(new BoxLayout(contactActivityTimelinePanel, BoxLayout.Y_AXIS));
        contactActivityTimelinePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(filters);
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));
        content.add(contactActivityTimelinePanel);
        showContactActivity(ContactActivityFeed.empty());
        return profileSection(Messages.ACTIVITY_TITLE, Messages.ACTIVITY_SUBTITLE, content);
    }

    private void configureActivityFilterButton(
            JToggleButton button,
            ContactActivityFilter filter,
            ButtonGroup group
    ) {
        group.add(button);
        button.setFocusPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 11f));
        button.putClientProperty(UiConfig.CLIENT_BUTTON_TYPE, UiConfig.BUTTON_TYPE_ROUND_RECT);
        button.addActionListener(event -> {
            contactActivityFilter = filter;
            updateActivityFilterSelection();
            contactActivityAction.run();
        });
    }

    private void updateActivityFilterSelection() {
        activityAllButton.setSelected(contactActivityFilter == ContactActivityFilter.ALL);
        activityChangesButton.setSelected(contactActivityFilter == ContactActivityFilter.CHANGES);
        activityTransferButton.setSelected(contactActivityFilter == ContactActivityFilter.TRANSFER);
        activityRestoreButton.setSelected(contactActivityFilter == ContactActivityFilter.RESTORE);
    }

    private JPanel profileSection(String title, String subtitle, JComponent component) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(15, 17, 17, 17));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel titleView = new JLabel(title);
        titleView.setForeground(ModernThemePalette.textPrimary());
        titleView.setFont(titleView.getFont().deriveFont(Font.BOLD, UiConfig.EDITOR_SECTION_TITLE_SIZE));
        JLabel subtitleView = new JLabel(subtitle);
        subtitleView.setForeground(ModernThemePalette.textSecondary());
        subtitleView.setFont(subtitleView.getFont().deriveFont(Font.PLAIN, UiConfig.EDITOR_SECTION_SUBTITLE_SIZE));

        heading.add(titleView);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitleView);
        card.add(heading, BorderLayout.NORTH);
        card.add(component, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 13f));
        tabs.putClientProperty(UiConfig.CLIENT_TABBED_TAB_TYPE, UiConfig.TABBED_TAB_TYPE_CARD);
        tabs.putClientProperty(UiConfig.CLIENT_TABBED_SHOW_TAB_SEPARATORS, false);
        tabs.putClientProperty(UiConfig.CLIENT_TABBED_TAB_HEIGHT, UiConfig.EDITOR_TAB_HEIGHT);
        tabs.addTab(Messages.TAB_BASIC, buildBasicTab());
        tabs.addTab(Messages.TAB_CONTACT, buildContactTab());
        tabs.addTab(Messages.TAB_WORK, buildWorkTab());
        tabs.addTab(Messages.TAB_ADDRESS, buildAddressTab());
        tabs.addTab(Messages.TAB_OTHER, buildOtherTab());
        wrapper.add(tabs, BorderLayout.CENTER);

        ResponsiveActionPanel actions = new ResponsiveActionPanel(
                UiConfig.FORM_ACTION_WIDE_COLUMNS,
                UiConfig.FORM_ACTION_COMPACT_COLUMNS,
                UiConfig.CONTACT_ACTION_COMPACT_BREAKPOINT,
                UiConfig.UI_SMALL_GAP,
                UiConfig.UI_SMALL_GAP
        );
        actions.setBorder(new EmptyBorder(UiConfig.UI_SMALL_GAP, 0, 0, 0));
        actions.add(cancelButton);
        actions.add(deleteButton);
        actions.add(addButton);
        wrapper.add(actions, BorderLayout.SOUTH);
        return wrapper;
    }

    private JComponent buildBasicTab() {
        ResponsiveFormPanel mainForm = new ResponsiveFormPanel();
        mainForm.addField(Messages.NAME_LABEL, nameField);
        mainForm.addField(Messages.CATEGORY_LABEL, categoryField);

        ResponsiveFormPanel profileForm = new ResponsiveFormPanel();
        profileForm.addField(Messages.PHOTO_LABEL, photoActionsButton);
        profileForm.addFullComponent(favoriteCheckBox);

        VerticalScrollablePanel content = editorTabContent();
        content.add(editorSection(Messages.FORM_BASIC_SECTION_TITLE, Messages.FORM_BASIC_SECTION_SUBTITLE, mainForm));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));
        content.add(editorSection(Messages.FORM_PROFILE_SECTION_TITLE, Messages.FORM_PROFILE_SECTION_SUBTITLE, profileForm));
        return editorScroll(content);
    }

    private JComponent buildContactTab() {
        VerticalScrollablePanel content = editorTabContent();
        content.add(editorSection(Messages.PHONE_METHODS_TITLE, Messages.FORM_PHONE_SECTION_SUBTITLE, phoneMethodsPanel));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));
        content.add(editorSection(Messages.EMAIL_METHODS_TITLE, Messages.FORM_EMAIL_SECTION_SUBTITLE, emailMethodsPanel));
        return editorScroll(content);
    }

    private JComponent buildWorkTab() {
        ResponsiveFormPanel form = new ResponsiveFormPanel();
        form.addField(Messages.COMPANY_LABEL, companyField);
        form.addField(Messages.JOB_TITLE_LABEL, jobTitleField);
        form.addFullField(Messages.WEBSITE_LABEL, websiteField);

        VerticalScrollablePanel content = editorTabContent();
        content.add(editorSection(Messages.FORM_WORK_SECTION_TITLE, Messages.FORM_WORK_SECTION_SUBTITLE, form));
        return editorScroll(content);
    }

    private JComponent buildAddressTab() {
        ResponsiveFormPanel form = new ResponsiveFormPanel();
        form.addFullField(Messages.ADDRESS_LABEL, editorAreaScroll(addressArea, UiConfig.EDITOR_AREA_HEIGHT));
        form.addField(Messages.DISTRICT_LABEL, districtField);
        form.addField(Messages.CITY_LABEL, cityField);
        form.addField(Messages.POSTAL_CODE_LABEL, postalCodeField);
        form.addField(Messages.COUNTRY_LABEL, countryField);

        VerticalScrollablePanel content = editorTabContent();
        content.add(editorSection(Messages.FORM_ADDRESS_SECTION_TITLE, Messages.FORM_ADDRESS_SECTION_SUBTITLE, form));
        return editorScroll(content);
    }

    private JComponent buildOtherTab() {
        ResponsiveFormPanel important = new ResponsiveFormPanel();
        important.addField(Messages.BIRTHDAY_LABEL, birthdayField);
        important.addField(Messages.BIRTHDAY_REMINDER_LABEL, birthdayReminderCombo);
        important.addFullComponent(importantDatesPanel);

        ResponsiveFormPanel keepInTouch = new ResponsiveFormPanel();
        keepInTouch.addField(Messages.KEEP_IN_TOUCH_INTERVAL_LABEL, keepInTouchCombo);
        keepInTouch.addField(Messages.LAST_CONTACTED_LABEL, lastContactedField);

        ResponsiveFormPanel notes = new ResponsiveFormPanel();
        notes.addFullField(Messages.NOTES_LABEL, editorAreaScroll(notesArea, UiConfig.EDITOR_NOTES_HEIGHT));

        VerticalScrollablePanel content = editorTabContent();
        content.add(editorSection(Messages.IMPORTANT_DATES_SECTION_TITLE, Messages.IMPORTANT_DATES_SECTION_SUBTITLE, important));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));
        content.add(editorSection(Messages.KEEP_IN_TOUCH_SECTION_TITLE, Messages.KEEP_IN_TOUCH_SECTION_SUBTITLE, keepInTouch));
        content.add(Box.createVerticalStrut(UiConfig.UI_SMALL_GAP));
        content.add(editorSection(Messages.FORM_OTHER_SECTION_TITLE, Messages.FORM_OTHER_SECTION_SUBTITLE, notes));
        return editorScroll(content);
    }

    private VerticalScrollablePanel editorTabContent() {
        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.setBorder(new EmptyBorder(10, 6, 10, 6));
        return content;
    }

    private JScrollPane editorScroll(VerticalScrollablePanel content) {
        JScrollPane scroll = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        return scroll;
    }

    private JScrollPane editorAreaScroll(JTextArea area, int height) {
        JScrollPane scroll = new JScrollPane(
                area,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setPreferredSize(new Dimension(0, height));
        scroll.setMinimumSize(new Dimension(0, height));
        return scroll;
    }

    private JPanel editorSection(String title, String subtitle, JComponent component) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(16, 18, 18, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel titleView = new JLabel(title);
        titleView.setForeground(ModernThemePalette.textPrimary());
        titleView.setFont(titleView.getFont().deriveFont(Font.BOLD, UiConfig.EDITOR_SECTION_TITLE_SIZE));
        JLabel subtitleView = new JLabel(subtitle);
        subtitleView.setForeground(ModernThemePalette.textSecondary());
        subtitleView.setFont(subtitleView.getFont().deriveFont(Font.PLAIN, UiConfig.EDITOR_SECTION_SUBTITLE_SIZE));

        heading.add(titleView);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitleView);
        card.add(heading, BorderLayout.NORTH);
        card.add(component, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildGroupsPage() {
        groupList.setCellRenderer(new ManagementListCellRenderer());
        groupList.setFixedCellHeight(UiConfig.MANAGEMENT_LIST_ROW_HEIGHT);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton create = primaryButton(Messages.GROUP_NEW_BUTTON);
        JButton addContact = secondaryButton(Messages.GROUP_ADD_CONTACT_BUTTON);
        JButton removeContact = secondaryButton(Messages.GROUP_REMOVE_CONTACT_BUTTON);
        JButton delete = dangerButton(Messages.GROUP_DELETE_BUTTON);
        create.addActionListener(event -> createGroupAction.run());
        addContact.addActionListener(event -> addGroupContactAction.run());
        removeContact.addActionListener(event -> removeGroupContactAction.run());
        delete.addActionListener(event -> deleteGroupAction.run());

        return buildModernManagementPage(
                Messages.GROUPS_TITLE,
                Messages.GROUPS_SUBTITLE,
                groupList,
                create,
                buildGroupDetailCard(addContact, removeContact, delete)
        );
    }

    private JPanel buildTagsPage() {
        tagList.setCellRenderer(new ManagementListCellRenderer());
        tagList.setFixedCellHeight(UiConfig.MANAGEMENT_LIST_ROW_HEIGHT);
        tagList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton create = primaryButton(Messages.TAG_NEW_BUTTON);
        JButton addContact = secondaryButton(Messages.TAG_ADD_CONTACT_BUTTON);
        JButton removeContact = secondaryButton(Messages.TAG_REMOVE_CONTACT_BUTTON);
        JButton delete = dangerButton(Messages.TAG_DELETE_BUTTON);
        create.addActionListener(event -> createTagAction.run());
        addContact.addActionListener(event -> addTagContactAction.run());
        removeContact.addActionListener(event -> removeTagContactAction.run());
        delete.addActionListener(event -> deleteTagAction.run());

        return buildModernManagementPage(
                Messages.TAGS_TITLE,
                Messages.TAGS_SUBTITLE,
                tagList,
                create,
                buildTagDetailCard(addContact, removeContact, delete)
        );
    }

    private JPanel buildSmartListsPage() {
        smartList.setCellRenderer(new ManagementListCellRenderer());
        smartList.setFixedCellHeight(UiConfig.MANAGEMENT_LIST_ROW_HEIGHT);
        smartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton create = primaryButton(Messages.SMART_NEW_BUTTON);
        JButton run = primaryButton(Messages.SMART_RUN_BUTTON);
        JButton delete = dangerButton(Messages.SMART_DELETE_BUTTON);
        create.addActionListener(event -> createSmartListAction.run());
        run.addActionListener(event -> runSmartListAction.run());
        delete.addActionListener(event -> deleteSmartListAction.run());

        return buildModernManagementPage(
                Messages.SMART_LISTS_TITLE,
                Messages.SMART_LISTS_SUBTITLE,
                smartList,
                create,
                buildSmartListDetailCard(run, delete)
        );
    }

    private JPanel buildModernManagementPage(
            String title,
            String subtitle,
            JList<?> list,
            JButton createButton,
            JPanel detailCard
    ) {
        JPanel page = pagePanel();
        page.add(pageHeader(title, subtitle), BorderLayout.NORTH);

        ResponsiveCardGridPanel grid = new ResponsiveCardGridPanel(
                UiConfig.MANAGEMENT_GRID_WIDE_COLUMNS,
                UiConfig.MANAGEMENT_GRID_COMPACT_COLUMNS,
                UiConfig.MANAGEMENT_GRID_COMPACT_COLUMNS,
                UiConfig.MANAGEMENT_GRID_BREAKPOINT,
                UiConfig.PROFILE_INFO_COMPACT_BREAKPOINT,
                UiConfig.MANAGEMENT_GRID_GAP,
                UiConfig.MANAGEMENT_GRID_GAP
        );
        grid.addCard(buildManagementListCard(list, createButton));
        grid.addCard(detailCard);

        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.add(grid);
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildManagementListCard(JList<?> list, JButton createButton) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setPreferredSize(new Dimension(0, UiConfig.MANAGEMENT_LIST_CARD_HEIGHT));
        card.setMinimumSize(new Dimension(0, 320));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(Messages.MANAGEMENT_ITEMS_TITLE);
        title.setForeground(ModernThemePalette.textPrimary());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
        JLabel subtitle = new JLabel(Messages.MANAGEMENT_ITEMS_SUBTITLE);
        subtitle.setForeground(ModernThemePalette.textSecondary());
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(subtitle);
        header.add(text, BorderLayout.CENTER);
        header.add(createButton, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        list.setBackground(ModernThemePalette.surfaceElevated());
        list.setSelectionBackground(ModernThemePalette.surfaceElevated());
        list.setSelectionForeground(ModernThemePalette.textPrimary());
        JScrollPane listScroll = new JScrollPane(
                list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        listScroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        card.add(listScroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildGroupDetailCard(JButton addContact, JButton removeContact, JButton delete) {
        JPanel body = managementDetailBody(
                Messages.MANAGEMENT_GROUP_DETAIL_TITLE,
                groupDetailNameLabel,
                managementDetailRow(Messages.MANAGEMENT_COLOR_LABEL, groupDetailColorLabel),
                managementDetailRow(Messages.MANAGEMENT_CONTACT_COUNT_LABEL, groupDetailCountLabel),
                managementDetailRow(Messages.MANAGEMENT_SELECTED_CONTACT_LABEL, groupDetailContactLabel)
        );
        body.add(Box.createVerticalGlue());
        body.add(managementActions(addContact, removeContact, delete));
        return managementDetailCard(body);
    }

    private JPanel buildTagDetailCard(JButton addContact, JButton removeContact, JButton delete) {
        JPanel body = managementDetailBody(
                Messages.MANAGEMENT_TAG_DETAIL_TITLE,
                tagDetailNameLabel,
                managementDetailRow(Messages.MANAGEMENT_COLOR_LABEL, tagDetailColorLabel),
                managementDetailRow(Messages.MANAGEMENT_CONTACT_COUNT_LABEL, tagDetailCountLabel),
                managementDetailRow(Messages.MANAGEMENT_SELECTED_CONTACT_LABEL, tagDetailContactLabel)
        );
        body.add(Box.createVerticalGlue());
        body.add(managementActions(addContact, removeContact, delete));
        return managementDetailCard(body);
    }

    private JPanel buildSmartListDetailCard(JButton run, JButton delete) {
        JPanel body = managementDetailBody(
                Messages.MANAGEMENT_SMART_DETAIL_TITLE,
                smartDetailNameLabel,
                managementDetailRow(Messages.MANAGEMENT_RULE_FIELD_LABEL, smartDetailFieldLabel),
                managementDetailRow(Messages.MANAGEMENT_RULE_OPERATOR_LABEL, smartDetailOperatorLabel),
                managementDetailRow(Messages.MANAGEMENT_RULE_VALUE_LABEL, smartDetailValueLabel)
        );
        body.add(Box.createVerticalGlue());
        body.add(managementActions(run, delete));
        return managementDetailCard(body);
    }

    private JPanel managementDetailCard(JPanel body) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(0, UiConfig.MANAGEMENT_DETAIL_CARD_HEIGHT));
        card.setMinimumSize(new Dimension(0, 320));
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel managementDetailBody(String eyebrow, JLabel title, JPanel... rows) {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel eyebrowLabel = new JLabel(eyebrow.toUpperCase());
        eyebrowLabel.setForeground(ModernThemePalette.accentStrong());
        eyebrowLabel.setFont(eyebrowLabel.getFont().deriveFont(Font.BOLD, 11.5f));
        eyebrowLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(eyebrowLabel);
        body.add(Box.createVerticalStrut(10));
        body.add(title);
        body.add(Box.createVerticalStrut(22));
        for (JPanel row : rows) {
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(row);
            body.add(Box.createVerticalStrut(10));
        }
        return body;
    }

    private JPanel managementDetailRow(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernThemePalette.border()),
                new EmptyBorder(12, 14, 12, 14)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JLabel labelView = new JLabel(label);
        labelView.setForeground(ModernThemePalette.textSecondary());
        labelView.setFont(labelView.getFont().deriveFont(Font.BOLD, 11.5f));
        value.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(labelView, BorderLayout.WEST);
        row.add(value, BorderLayout.CENTER);
        return row;
    }

    private JPanel managementActions(JButton... buttons) {
        ResponsiveActionPanel actions = new ResponsiveActionPanel(
                UiConfig.MANAGEMENT_ACTION_WIDE_COLUMNS,
                UiConfig.MANAGEMENT_ACTION_COMPACT_COLUMNS,
                UiConfig.MANAGEMENT_ACTION_BREAKPOINT,
                UiConfig.UI_SMALL_GAP,
                UiConfig.UI_SMALL_GAP
        );
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (JButton button : buttons) {
            actions.add(button);
        }
        return actions;
    }

    private void updateGroupDetail() {
        GroupRecord group = groupList.getSelectedValue();
        if (group == null) {
            groupDetailNameLabel.setText(Messages.MANAGEMENT_NO_SELECTION);
            groupDetailColorLabel.setText(Messages.EMPTY_VALUE);
            groupDetailCountLabel.setText(Messages.EMPTY_VALUE);
        } else {
            groupDetailNameLabel.setText(group.name());
            groupDetailColorLabel.setText(group.color());
            groupDetailCountLabel.setText(String.format(Messages.MANAGEMENT_CONTACT_COUNT_FORMAT, group.contactCount()));
        }
        groupDetailContactLabel.setText(selectedContactManagementText());
    }

    private void updateTagDetail() {
        TagRecord tag = tagList.getSelectedValue();
        if (tag == null) {
            tagDetailNameLabel.setText(Messages.MANAGEMENT_NO_SELECTION);
            tagDetailColorLabel.setText(Messages.EMPTY_VALUE);
            tagDetailCountLabel.setText(Messages.EMPTY_VALUE);
        } else {
            tagDetailNameLabel.setText(tag.name());
            tagDetailColorLabel.setText(tag.color());
            tagDetailCountLabel.setText(String.format(Messages.MANAGEMENT_CONTACT_COUNT_FORMAT, tag.contactCount()));
        }
        tagDetailContactLabel.setText(selectedContactManagementText());
    }

    private void updateSmartListDetail() {
        SmartList rule = smartList.getSelectedValue();
        if (rule == null) {
            smartDetailNameLabel.setText(Messages.MANAGEMENT_NO_SELECTION);
            smartDetailFieldLabel.setText(Messages.EMPTY_VALUE);
            smartDetailOperatorLabel.setText(Messages.EMPTY_VALUE);
            smartDetailValueLabel.setText(Messages.EMPTY_VALUE);
            return;
        }
        smartDetailNameLabel.setText(rule.name());
        smartDetailFieldLabel.setText(Messages.smartFieldLabel(rule.field()));
        smartDetailOperatorLabel.setText(Messages.smartOperatorLabel(rule.operator()));
        smartDetailValueLabel.setText(rule.value() == null || rule.value().isBlank()
                ? Messages.MANAGEMENT_RULE_VALUE_EMPTY
                : rule.value());
    }

    private String selectedContactManagementText() {
        Contact selected = contactList.getSelectedValue();
        return selected == null ? Messages.MANAGEMENT_NO_CONTACT_SELECTED : selected.name();
    }

    private JPanel buildHistoryPage() {
        historyList.setCellRenderer(new OperationsListCellRenderer());
        historyList.setFixedCellHeight(UiConfig.OPERATIONS_LIST_ROW_HEIGHT);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton refresh = secondaryButton(Messages.HISTORY_REFRESH_BUTTON);
        JButton restore = primaryButton(Messages.HISTORY_RESTORE_BUTTON);
        refresh.addActionListener(event -> refreshManagementAction.run());
        restore.addActionListener(event -> restoreHistoryAction.run());

        return buildOperationsManagementPage(
                Messages.HISTORY_TITLE,
                Messages.HISTORY_SUBTITLE,
                historyList,
                Messages.HISTORY_ITEMS_TITLE,
                Messages.HISTORY_ITEMS_SUBTITLE,
                refresh,
                buildHistoryDetailCard(restore)
        );
    }

    private JPanel buildMaintenancePage() {
        duplicateList.setCellRenderer(new OperationsListCellRenderer());
        duplicateList.setFixedCellHeight(UiConfig.OPERATIONS_LIST_ROW_HEIGHT);
        duplicateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton scan = secondaryButton(Messages.DUPLICATE_SCAN_BUTTON);
        JButton merge = primaryButton(Messages.DUPLICATE_MERGE_BUTTON);
        scan.addActionListener(event -> scanDuplicatesAction.run());
        merge.addActionListener(event -> mergeDuplicateAction.run());

        return buildOperationsManagementPage(
                Messages.MAINTENANCE_TITLE,
                Messages.MAINTENANCE_SUBTITLE,
                duplicateList,
                Messages.DUPLICATE_ITEMS_TITLE,
                Messages.DUPLICATE_ITEMS_SUBTITLE,
                scan,
                buildDuplicateDetailCard(merge)
        );
    }

    private JPanel buildImportExportPage() {
        JPanel page = pagePanel();
        page.add(pageHeader(Messages.IMPORT_EXPORT_TITLE, Messages.IMPORT_EXPORT_SUBTITLE), BorderLayout.NORTH);

        JButton importButton = primaryButton(Messages.IMPORT_BUTTON);
        JButton vcfButton = secondaryButton(Messages.EXPORT_VCF_BUTTON);
        JButton csvButton = secondaryButton(Messages.EXPORT_CSV_BUTTON);
        importButton.addActionListener(event -> importAction.run());
        vcfButton.addActionListener(event -> exportVcfAction.run());
        csvButton.addActionListener(event -> exportCsvAction.run());

        ResponsiveCardGridPanel grid = new ResponsiveCardGridPanel(
                UiConfig.TRANSFER_GRID_WIDE_COLUMNS,
                UiConfig.TRANSFER_GRID_MEDIUM_COLUMNS,
                UiConfig.TRANSFER_GRID_COMPACT_COLUMNS,
                UiConfig.TRANSFER_GRID_MEDIUM_BREAKPOINT,
                UiConfig.TRANSFER_GRID_COMPACT_BREAKPOINT,
                UiConfig.TRANSFER_GRID_GAP,
                UiConfig.TRANSFER_GRID_GAP
        );
        grid.addCard(transferActionCard(
                Messages.TRANSFER_IMPORT_EYEBROW,
                Messages.IMPORT_BUTTON,
                Messages.TRANSFER_IMPORT_DESCRIPTION,
                Messages.TRANSFER_IMPORT_META,
                importButton
        ));
        grid.addCard(transferActionCard(
                Messages.TRANSFER_VCF_EYEBROW,
                Messages.EXPORT_VCF_BUTTON,
                Messages.TRANSFER_VCF_DESCRIPTION,
                Messages.TRANSFER_VCF_META,
                vcfButton
        ));
        grid.addCard(transferActionCard(
                Messages.TRANSFER_CSV_EYEBROW,
                Messages.EXPORT_CSV_BUTTON,
                Messages.TRANSFER_CSV_DESCRIPTION,
                Messages.TRANSFER_CSV_META,
                csvButton
        ));

        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.add(grid);
        content.add(Box.createVerticalStrut(UiConfig.MANAGEMENT_GRID_GAP));
        content.add(buildTransferInfoCard());
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildTrashPage() {
        trashList.setCellRenderer(new OperationsListCellRenderer());
        trashList.setFixedCellHeight(UiConfig.OPERATIONS_LIST_ROW_HEIGHT);
        trashList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton refresh = secondaryButton(Messages.TRASH_REFRESH_BUTTON);
        JButton restore = primaryButton(Messages.TRASH_RESTORE_BUTTON);
        JButton delete = dangerButton(Messages.TRASH_DELETE_BUTTON);
        refresh.addActionListener(event -> refreshManagementAction.run());
        restore.addActionListener(event -> restoreTrashAction.run());
        delete.addActionListener(event -> deleteTrashAction.run());

        return buildOperationsManagementPage(
                Messages.TRASH_TITLE,
                Messages.TRASH_SUBTITLE,
                trashList,
                Messages.TRASH_ITEMS_TITLE,
                Messages.TRASH_ITEMS_SUBTITLE,
                refresh,
                buildTrashDetailCard(restore, delete)
        );
    }

    private JPanel buildOperationsManagementPage(
            String title,
            String subtitle,
            JList<?> list,
            String listTitle,
            String listSubtitle,
            JButton headerAction,
            JPanel detailCard
    ) {
        JPanel page = pagePanel();
        page.add(pageHeader(title, subtitle), BorderLayout.NORTH);

        ResponsiveCardGridPanel grid = new ResponsiveCardGridPanel(
                UiConfig.OPERATIONS_GRID_WIDE_COLUMNS,
                UiConfig.OPERATIONS_GRID_COMPACT_COLUMNS,
                UiConfig.OPERATIONS_GRID_COMPACT_COLUMNS,
                UiConfig.OPERATIONS_GRID_BREAKPOINT,
                UiConfig.PROFILE_INFO_COMPACT_BREAKPOINT,
                UiConfig.MANAGEMENT_GRID_GAP,
                UiConfig.MANAGEMENT_GRID_GAP
        );
        grid.addCard(buildOperationsListCard(list, listTitle, listSubtitle, headerAction));
        grid.addCard(detailCard);

        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.add(grid);
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildOperationsListCard(JList<?> list, String titleText, String subtitleText, JButton headerAction) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setPreferredSize(new Dimension(0, UiConfig.OPERATIONS_CARD_HEIGHT));
        card.setMinimumSize(new Dimension(0, UiConfig.OPERATIONS_CARD_MIN_HEIGHT));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(titleText);
        title.setForeground(ModernThemePalette.textPrimary());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setForeground(ModernThemePalette.textSecondary());
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(subtitle);
        header.add(text, BorderLayout.CENTER);
        header.add(headerAction, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        list.setBackground(ModernThemePalette.surfaceElevated());
        list.setSelectionBackground(ModernThemePalette.surfaceElevated());
        list.setSelectionForeground(ModernThemePalette.textPrimary());
        JScrollPane listScroll = new JScrollPane(
                list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        listScroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        card.add(listScroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildHistoryDetailCard(JButton restore) {
        JPanel body = managementDetailBody(
                Messages.HISTORY_DETAIL_TITLE,
                historyDetailNameLabel,
                managementDetailRow(Messages.HISTORY_ACTION_LABEL, historyDetailActionLabel),
                managementDetailRow(Messages.HISTORY_DATE_LABEL, historyDetailDateLabel),
                managementDetailRow(Messages.HISTORY_CONTACT_ID_LABEL, historyDetailContactIdLabel)
        );
        body.add(Box.createVerticalGlue());
        body.add(managementActions(restore));
        return operationsDetailCard(body);
    }

    private JPanel buildDuplicateDetailCard(JButton merge) {
        JPanel body = managementDetailBody(
                Messages.DUPLICATE_DETAIL_TITLE,
                duplicatePrimaryLabel,
                managementDetailRow(Messages.DUPLICATE_SECONDARY_LABEL, duplicateSecondaryLabel),
                managementDetailRow(Messages.DUPLICATE_REASON_LABEL, duplicateReasonLabel),
                managementDetailRow(Messages.DUPLICATE_STATUS_LABEL, duplicateStatusLabel)
        );
        body.add(Box.createVerticalStrut(8));
        JLabel hint = new JLabel(Messages.DUPLICATE_PRIMARY_HINT);
        hint.setForeground(ModernThemePalette.textSecondary());
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 12f));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(hint);
        body.add(Box.createVerticalGlue());
        body.add(managementActions(merge));
        return operationsDetailCard(body);
    }

    private JPanel buildTrashDetailCard(JButton restore, JButton delete) {
        JPanel body = managementDetailBody(
                Messages.TRASH_DETAIL_TITLE,
                trashDetailNameLabel,
                managementDetailRow(Messages.TRASH_PHONE_LABEL, trashDetailPhoneLabel),
                managementDetailRow(Messages.TRASH_EMAIL_LABEL, trashDetailEmailLabel),
                managementDetailRow(Messages.TRASH_COMPANY_LABEL, trashDetailCompanyLabel),
                managementDetailRow(Messages.TRASH_CATEGORY_LABEL, trashDetailCategoryLabel)
        );
        body.add(Box.createVerticalGlue());
        body.add(managementActions(restore, delete));
        return operationsDetailCard(body);
    }

    private JPanel operationsDetailCard(JPanel body) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(0, UiConfig.OPERATIONS_CARD_HEIGHT));
        card.setMinimumSize(new Dimension(0, UiConfig.OPERATIONS_CARD_MIN_HEIGHT));
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel transferActionCard(String eyebrow, String titleText, String description, String meta, JButton button) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 18));
        card.setPreferredSize(new Dimension(0, UiConfig.TRANSFER_CARD_HEIGHT));
        card.setMinimumSize(new Dimension(0, UiConfig.TRANSFER_CARD_MIN_HEIGHT));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel eyebrowLabel = new JLabel(eyebrow);
        eyebrowLabel.setForeground(ModernThemePalette.accentStrong());
        eyebrowLabel.setFont(eyebrowLabel.getFont().deriveFont(Font.BOLD, 11.5f));
        JLabel heading = new JLabel(titleText);
        heading.setForeground(ModernThemePalette.textPrimary());
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 18f));
        JTextArea body = new JTextArea(description);
        body.setEditable(false);
        body.setFocusable(false);
        body.setOpaque(false);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setForeground(ModernThemePalette.textSecondary());
        body.setFont(body.getFont().deriveFont(Font.PLAIN, 13f));
        body.setBorder(BorderFactory.createEmptyBorder());
        JLabel metaLabel = new JLabel(meta);
        metaLabel.setForeground(ModernThemePalette.textSecondary());
        metaLabel.setFont(metaLabel.getFont().deriveFont(Font.BOLD, 11.5f));

        text.add(eyebrowLabel);
        text.add(Box.createVerticalStrut(8));
        text.add(heading);
        text.add(Box.createVerticalStrut(12));
        text.add(body);
        text.add(Box.createVerticalGlue());
        text.add(Box.createVerticalStrut(14));
        text.add(metaLabel);

        card.add(text, BorderLayout.CENTER);
        card.add(button, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTransferInfoCard() {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(14, 0));
        card.setPreferredSize(new Dimension(0, UiConfig.TRANSFER_INFO_CARD_HEIGHT));
        card.setMinimumSize(new Dimension(0, UiConfig.TRANSFER_INFO_CARD_HEIGHT));

        JLabel icon = new JLabel(Messages.OPERATION_INFO_ICON, SwingConstants.CENTER);
        icon.setForeground(ModernThemePalette.accentStrong());
        icon.setFont(icon.getFont().deriveFont(Font.BOLD, 20f));
        icon.setPreferredSize(new Dimension(34, 34));
        card.add(icon, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(Messages.TRANSFER_INFO_TITLE);
        title.setForeground(ModernThemePalette.textPrimary());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14.5f));
        JLabel body = new JLabel(Messages.TRANSFER_INFO_TEXT);
        body.setForeground(ModernThemePalette.textSecondary());
        body.setFont(body.getFont().deriveFont(Font.PLAIN, 12.5f));
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(body);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private void updateHistoryDetail() {
        HistoryEntry entry = historyList.getSelectedValue();
        if (entry == null) {
            historyDetailNameLabel.setText(Messages.MANAGEMENT_NO_SELECTION);
            historyDetailActionLabel.setText(Messages.EMPTY_VALUE);
            historyDetailDateLabel.setText(Messages.EMPTY_VALUE);
            historyDetailContactIdLabel.setText(Messages.EMPTY_VALUE);
            return;
        }
        historyDetailNameLabel.setText(entry.contactName());
        historyDetailActionLabel.setText(HistoryActionText.label(entry.action()));
        historyDetailDateLabel.setText(valueOrDash(entry.createdAt()));
        historyDetailContactIdLabel.setText(Long.toString(entry.contactId()));
    }

    private void updateDuplicateDetail() {
        DuplicateCandidate candidate = duplicateList.getSelectedValue();
        if (candidate == null) {
            duplicatePrimaryLabel.setText(Messages.MANAGEMENT_NO_SELECTION);
            duplicateSecondaryLabel.setText(Messages.EMPTY_VALUE);
            duplicateReasonLabel.setText(Messages.EMPTY_VALUE);
            return;
        }
        duplicatePrimaryLabel.setText(candidate.primary().name());
        duplicateSecondaryLabel.setText(candidate.duplicate().name());
        duplicateReasonLabel.setText(valueOrDash(candidate.reason()));
    }

    private void updateTrashDetail() {
        Contact contact = trashList.getSelectedValue();
        if (contact == null) {
            trashDetailNameLabel.setText(Messages.MANAGEMENT_NO_SELECTION);
            trashDetailPhoneLabel.setText(Messages.EMPTY_VALUE);
            trashDetailEmailLabel.setText(Messages.EMPTY_VALUE);
            trashDetailCompanyLabel.setText(Messages.EMPTY_VALUE);
            trashDetailCategoryLabel.setText(Messages.EMPTY_VALUE);
            return;
        }
        trashDetailNameLabel.setText(contact.name());
        trashDetailPhoneLabel.setText(valueOrDash(contact.phone()));
        trashDetailEmailLabel.setText(valueOrDash(contact.email()));
        trashDetailCompanyLabel.setText(valueOrDash(contact.company()));
        trashDetailCategoryLabel.setText(valueOrDash(contact.category()));
    }

    private JPanel buildSyncPage() {
        JPanel page = pagePanel();
        page.add(pageHeader(Messages.SYNC_PAGE_TITLE, Messages.SYNC_PAGE_SUBTITLE), BorderLayout.NORTH);

        ResponsiveCardGridPanel statusGrid = new ResponsiveCardGridPanel(
                UiConfig.SYNC_GRID_WIDE_COLUMNS,
                UiConfig.SYNC_GRID_MEDIUM_COLUMNS,
                UiConfig.SYNC_GRID_COMPACT_COLUMNS,
                UiConfig.SYNC_GRID_MEDIUM_BREAKPOINT,
                UiConfig.SYNC_GRID_COMPACT_BREAKPOINT,
                UiConfig.UI_SMALL_GAP,
                UiConfig.UI_SMALL_GAP
        );
        statusGrid.addCard(syncStatusCard(
                Messages.SYNC_STATUS_CARD_TITLE,
                Messages.SYNC_STATUS_CARD_DESC,
                syncPageStatusLabel
        ));
        statusGrid.addCard(syncStatusCard(
                Messages.SYNC_ADDRESS_CARD_TITLE,
                Messages.SYNC_ADDRESS_CARD_DESC,
                syncAddressLabel
        ));
        statusGrid.addCard(syncStatusCard(
                Messages.SYNC_TOKEN_CARD_TITLE,
                Messages.SYNC_TOKEN_CARD_DESC,
                syncTokenLabel
        ));

        JPanel infoCard = modernCard();
        infoCard.setLayout(new BorderLayout(18, 0));
        infoCard.setMinimumSize(new Dimension(0, UiConfig.SYNC_INFO_CARD_HEIGHT));
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, UiConfig.SYNC_INFO_CARD_HEIGHT));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        DashboardIconBadge badge = new DashboardIconBadge(NavGlyph.SYNC);
        infoCard.add(badge, BorderLayout.WEST);

        JPanel infoText = new JPanel();
        infoText.setOpaque(false);
        infoText.setLayout(new BoxLayout(infoText, BoxLayout.Y_AXIS));
        JLabel infoTitle = new JLabel(Messages.SYNC_INFO_TITLE);
        infoTitle.setForeground(ModernThemePalette.textPrimary());
        infoTitle.setFont(infoTitle.getFont().deriveFont(Font.BOLD, 16f));
        JLabel infoBody = new JLabel("<html><div style='width:620px;'>" + Messages.SYNC_INFO_TEXT + "</div></html>");
        infoBody.setForeground(ModernThemePalette.textSecondary());
        infoBody.setFont(infoBody.getFont().deriveFont(Font.PLAIN, 12.5f));
        infoText.add(infoTitle);
        infoText.add(Box.createVerticalStrut(7));
        infoText.add(infoBody);
        infoCard.add(infoText, BorderLayout.CENTER);

        JButton detailsButton = primaryButton(Messages.SYNC_DETAILS_BUTTON);
        detailsButton.addActionListener(event -> mobileSyncAction.run());
        JPanel actionWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionWrap.setOpaque(false);
        actionWrap.add(detailsButton);
        infoCard.add(actionWrap, BorderLayout.SOUTH);

        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.add(statusGrid);
        content.add(Box.createVerticalStrut(UiConfig.UI_GAP));
        content.add(infoCard);
        content.add(Box.createVerticalGlue());

        page.add(verticalPageScroll(content), BorderLayout.CENTER);
        return page;
    }

    private JPanel syncStatusCard(String titleText, String description, JLabel valueLabel) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setMinimumSize(new Dimension(0, UiConfig.SYNC_STATUS_CARD_HEIGHT));
        card.setPreferredSize(new Dimension(0, UiConfig.SYNC_STATUS_CARD_HEIGHT));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(titleText);
        title.setForeground(ModernThemePalette.textPrimary());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 15f));
        JLabel body = new JLabel("<html><div style='width:250px;'>" + description + "</div></html>");
        body.setForeground(ModernThemePalette.textSecondary());
        body.setFont(body.getFont().deriveFont(Font.PLAIN, 11.5f));
        text.add(title);
        text.add(Box.createVerticalStrut(6));
        text.add(body);
        card.add(text, BorderLayout.NORTH);

        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 14f));
        card.add(valueLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildBackupPage() {
        JPanel page = pagePanel();
        page.add(pageHeader(Messages.BACKUP_TITLE, Messages.BACKUP_SUBTITLE), BorderLayout.NORTH);

        ResponsiveCardGridPanel grid = new ResponsiveCardGridPanel(
                UiConfig.BACKUP_GRID_WIDE_COLUMNS,
                UiConfig.BACKUP_GRID_COMPACT_COLUMNS,
                UiConfig.BACKUP_GRID_COMPACT_COLUMNS,
                UiConfig.BACKUP_GRID_BREAKPOINT,
                UiConfig.PROFILE_INFO_COMPACT_BREAKPOINT,
                UiConfig.MANAGEMENT_GRID_GAP,
                UiConfig.MANAGEMENT_GRID_GAP
        );
        grid.addCard(buildBackupSummaryCard());
        grid.addCard(buildBackupHistoryCard());

        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.add(grid);
        content.add(Box.createVerticalGlue());
        page.add(verticalPageScroll(content), BorderLayout.CENTER);
        return page;
    }

    private JPanel buildBackupSummaryCard() {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 16));
        card.setMinimumSize(new Dimension(0, UiConfig.BACKUP_SUMMARY_CARD_HEIGHT));
        card.setPreferredSize(new Dimension(0, UiConfig.BACKUP_SUMMARY_CARD_HEIGHT));

        JPanel header = settingsCardHeader(Messages.BACKUP_SUMMARY_TITLE, Messages.BACKUP_SUMMARY_DESC);
        card.add(header, BorderLayout.NORTH);

        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        backupCountLabel.setForeground(ModernThemePalette.textPrimary());
        backupCountLabel.setFont(backupCountLabel.getFont().deriveFont(Font.BOLD, 14f));
        backupLastLabel.setForeground(ModernThemePalette.textSecondary());
        backupLastLabel.setFont(backupLastLabel.getFont().deriveFont(Font.PLAIN, 12.5f));
        rows.add(backupCountLabel);
        rows.add(Box.createVerticalStrut(8));
        rows.add(backupLastLabel);
        rows.add(Box.createVerticalStrut(16));
        rows.add(settingsPathRow(Messages.BACKUP_LOCATION_LABEL, backupPagePathLabel));
        card.add(rows, BorderLayout.CENTER);

        JPanel action = new JPanel(new BorderLayout(12, 0));
        action.setOpaque(false);
        JPanel text = settingsCardHeader(Messages.BACKUP_ACTION_TITLE, Messages.BACKUP_ACTION_DESC);
        action.add(text, BorderLayout.CENTER);
        action.add(backupNowButton, BorderLayout.EAST);
        card.add(action, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildBackupHistoryCard() {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setMinimumSize(new Dimension(0, UiConfig.BACKUP_HISTORY_CARD_HEIGHT));
        card.setPreferredSize(new Dimension(0, UiConfig.BACKUP_HISTORY_CARD_HEIGHT));
        card.add(settingsCardHeader(Messages.BACKUP_HISTORY_TITLE, Messages.BACKUP_HISTORY_DESC), BorderLayout.NORTH);

        backupList.setCellRenderer(new BackupListCellRenderer());
        backupList.setBackground(ModernThemePalette.surfaceElevated());
        backupList.setSelectionBackground(ModernThemePalette.surfaceElevated());
        backupList.setSelectionForeground(ModernThemePalette.textPrimary());
        JScrollPane listScroll = new JScrollPane(
                backupList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        listScroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        card.add(listScroll, BorderLayout.CENTER);

        JPanel cleanup = new JPanel(new BorderLayout(12, 0));
        cleanup.setOpaque(false);
        JLabel cleanupHint = new JLabel(Messages.BACKUP_CLEANUP_HINT);
        cleanupHint.setForeground(ModernThemePalette.textSecondary());
        cleanupHint.setFont(cleanupHint.getFont().deriveFont(Font.PLAIN, 11.5f));
        cleanup.add(cleanupHint, BorderLayout.CENTER);
        cleanup.add(backupCleanupButton, BorderLayout.EAST);
        card.add(cleanup, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildSettingsPage() {
        JPanel page = pagePanel();
        page.add(pageHeader(Messages.SETTINGS_TITLE, Messages.SETTINGS_SUBTITLE), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.putClientProperty(UiConfig.CLIENT_TABBED_TAB_TYPE, UiConfig.TABBED_TAB_TYPE_CARD);
        tabs.putClientProperty(UiConfig.CLIENT_TABBED_SHOW_TAB_SEPARATORS, false);
        tabs.addTab(Messages.SETTINGS_TAB_GENERAL, settingsGeneralPanel());
        tabs.addTab(Messages.SETTINGS_TAB_APPEARANCE, settingsAppearancePanel());
        tabs.addTab(Messages.SETTINGS_TAB_BACKUP, settingsBackupPanel());
        tabs.addTab(Messages.SETTINGS_TAB_SYNC, settingsSyncPanel());
        tabs.addTab(Messages.SETTINGS_TAB_UPDATE, settingsUpdatePanel());
        page.add(tabs, BorderLayout.CENTER);

        JPanel footer = modernCard();
        footer.setLayout(new BorderLayout(16, 0));
        footer.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel hint = new JLabel(Messages.SETTINGS_SAVE_HINT);
        hint.setForeground(ModernThemePalette.textSecondary());
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 12f));
        footer.add(hint, BorderLayout.CENTER);
        footer.add(saveSettingsButton, BorderLayout.EAST);
        page.add(footer, BorderLayout.SOUTH);
        return page;
    }

    private JPanel settingsGeneralPanel() {
        ResponsiveFormPanel behaviorForm = new ResponsiveFormPanel();
        behaviorForm.addField(Messages.LANGUAGE_LABEL, languageCombo);
        behaviorForm.addField(Messages.STARTUP_PAGE_LABEL, startupPageCombo);
        behaviorForm.addFullComponent(rememberWindowCheckBox);
        behaviorForm.addFullComponent(confirmDeleteCheckBox);
        behaviorForm.addFullComponent(reminderNotificationsCheckBox);

        JLabel reminderHint = new JLabel("<html><div style='width:620px;'>" + Messages.REMINDER_NOTIFICATIONS_HINT + "</div></html>");
        reminderHint.setForeground(ModernThemePalette.textSecondary());
        reminderHint.setFont(reminderHint.getFont().deriveFont(Font.PLAIN, 12f));
        behaviorForm.addFullComponent(reminderHint);

        JLabel languageHint = new JLabel("<html><div style='width:620px;'>" + Messages.LANGUAGE_RESTART_HINT + "</div></html>");
        languageHint.setForeground(ModernThemePalette.textSecondary());
        languageHint.setFont(languageHint.getFont().deriveFont(Font.PLAIN, 12f));
        behaviorForm.addFullComponent(languageHint);

        JPanel behavior = settingsSectionCard(
                Messages.SETTINGS_GENERAL_CARD_TITLE,
                Messages.SETTINGS_GENERAL_CARD_DESC,
                behaviorForm,
                UiConfig.SETTINGS_CARD_MIN_HEIGHT
        );

        JPanel paths = new JPanel();
        paths.setOpaque(false);
        paths.setLayout(new BoxLayout(paths, BoxLayout.Y_AXIS));
        paths.add(settingsPathRow(Messages.DATA_FILE_LABEL, databasePathLabel));
        paths.add(Box.createVerticalStrut(10));
        paths.add(settingsPathRow(Messages.BACKUP_FOLDER_LABEL, backupPathLabel));

        JPanel data = settingsSectionCard(
                Messages.SETTINGS_DATA_CARD_TITLE,
                Messages.SETTINGS_DATA_CARD_DESC,
                paths,
                UiConfig.SETTINGS_DATA_CARD_MIN_HEIGHT
        );
        return settingsTabPage(behavior, data);
    }

    private JPanel settingsAppearancePanel() {
        ResponsiveFormPanel form = new ResponsiveFormPanel();
        form.addField(Messages.THEME_LABEL, themeCombo);
        form.addFullComponent(compactModeCheckBox);

        JPanel card = settingsSectionCard(
                Messages.SETTINGS_APPEARANCE_CARD_TITLE,
                Messages.SETTINGS_APPEARANCE_CARD_DESC,
                form,
                UiConfig.SETTINGS_CARD_MIN_HEIGHT
        );

        JLabel themeHint = new JLabel("<html><div style='width:620px;'>" + Messages.THEME_DESCRIPTION + "</div></html>");
        themeHint.setForeground(ModernThemePalette.textSecondary());
        themeHint.setFont(themeHint.getFont().deriveFont(Font.PLAIN, 12f));
        themeHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel hintCard = modernCard();
        hintCard.setLayout(new BorderLayout());
        hintCard.add(themeHint, BorderLayout.CENTER);
        hintCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        return settingsTabPage(card, hintCard);
    }

    private JPanel settingsBackupPanel() {
        ResponsiveFormPanel form = new ResponsiveFormPanel();
        form.addFullComponent(autoBackupCheckBox);
        form.addField(Messages.BACKUP_RETENTION_LABEL, backupRetentionSpinner);
        return settingsTabPage(settingsSectionCard(
                Messages.SETTINGS_BACKUP_CARD_TITLE,
                Messages.SETTINGS_BACKUP_CARD_DESC,
                form,
                UiConfig.SETTINGS_CARD_MIN_HEIGHT
        ));
    }

    private JPanel settingsSyncPanel() {
        ResponsiveFormPanel form = new ResponsiveFormPanel();
        form.addFullComponent(syncEnabledCheckBox);
        form.addField(Messages.SYNC_PORT_LABEL, syncPortSpinner);

        JPanel card = settingsSectionCard(
                Messages.SETTINGS_SYNC_CARD_TITLE,
                Messages.SETTINGS_SYNC_CARD_DESC,
                form,
                UiConfig.SETTINGS_CARD_MIN_HEIGHT
        );
        JLabel restartHint = new JLabel("<html><div style='width:640px;'>" + Messages.SETTINGS_SYNC_RESTART_HINT + "</div></html>");
        restartHint.setForeground(ModernThemePalette.textSecondary());
        restartHint.setFont(restartHint.getFont().deriveFont(Font.PLAIN, 12f));
        JPanel hintCard = modernCard();
        hintCard.setLayout(new BorderLayout());
        hintCard.add(restartHint, BorderLayout.CENTER);
        hintCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        return settingsTabPage(card, hintCard);
    }

    private JPanel settingsUpdatePanel() {
        ResponsiveFormPanel form = new ResponsiveFormPanel();
        form.addFullComponent(updateEnabledCheckBox);
        return settingsTabPage(settingsSectionCard(
                Messages.SETTINGS_UPDATE_CARD_TITLE,
                Messages.SETTINGS_UPDATE_CARD_DESC,
                form,
                UiConfig.SETTINGS_CARD_MIN_HEIGHT
        ));
    }

    private JPanel settingsSectionCard(String titleText, String description, JComponent content, int minimumHeight) {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 16));
        card.setMinimumSize(new Dimension(0, minimumHeight));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(settingsCardHeader(titleText, description), BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel settingsCardHeader(String titleText, String description) {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(titleText);
        title.setForeground(ModernThemePalette.textPrimary());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        JLabel body = new JLabel("<html><div style='width:620px;'>" + description + "</div></html>");
        body.setForeground(ModernThemePalette.textSecondary());
        body.setFont(body.getFont().deriveFont(Font.PLAIN, 12f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(body);
        return header;
    }

    private JPanel settingsPathRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernThemePalette.border()),
                new EmptyBorder(11, 13, 11, 13)
        ));
        JLabel label = new JLabel(labelText);
        label.setForeground(ModernThemePalette.textSecondary());
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11.5f));
        valueLabel.setForeground(ModernThemePalette.textPrimary());
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.PLAIN, 11.5f));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        valueLabel.setToolTipText(valueLabel.getText());
        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        return row;
    }

    private JPanel settingsTabPage(JPanel... cards) {
        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.setBorder(new EmptyBorder(14, 2, 14, 2));
        for (int index = 0; index < cards.length; index++) {
            JPanel card = cards[index];
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(card);
            if (index < cards.length - 1) {
                content.add(Box.createVerticalStrut(UiConfig.SETTINGS_CONTENT_GAP));
            }
        }
        content.add(Box.createVerticalGlue());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(verticalPageScroll(content), BorderLayout.CENTER);
        return wrapper;
    }

    private JScrollPane verticalPageScroll(JComponent content) {
        JScrollPane scroll = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        return scroll;
    }

    private JPanel buildAboutPage() {
        JPanel page = pagePanel();
        page.add(pageHeader(Messages.ABOUT_TITLE, Messages.ABOUT_DESCRIPTION), BorderLayout.NORTH);

        VerticalScrollablePanel content = new VerticalScrollablePanel();
        content.add(buildAboutOverviewCard());
        content.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(UiConfig.SCROLL_UNIT_INCREMENT);
        page.add(scrollPane, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildAboutOverviewCard() {
        JPanel card = modernCard();
        card.setLayout(new BorderLayout(0, 18));
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 390));

        JPanel top = new JPanel(new BorderLayout(22, 0));
        top.setOpaque(false);

        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setBorder(new EmptyBorder(6, 10, 6, 18));
        if (appIcon != null) {
            JLabel iconLabel = new JLabel(new ThemeBrandIcon(lightBrandIcon, darkBrandIcon, 156));
            iconPanel.add(iconLabel, BorderLayout.NORTH);
        }
        top.add(iconPanel, BorderLayout.WEST);

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(ModernThemePalette.border());
        separator.setPreferredSize(new Dimension(1, 164));
        top.add(separator, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(0, 24, 0, 0));
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel versionLabel = new JLabel(String.format(Messages.ABOUT_VERSION_FORMAT, AppConfig.APP_VERSION));
        versionLabel.setForeground(ModernThemePalette.textPrimary());
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.BOLD, 16f));
        info.add(versionLabel);
        info.add(Box.createVerticalStrut(16));
        info.add(aboutInfoRow(Messages.ABOUT_SITE_LABEL, new LinkLabel(AppConfig.BRAND_SITE_NAME, () -> openWebsiteAction.run())));
        info.add(Box.createVerticalStrut(12));
        info.add(aboutInfoRow(Messages.ABOUT_WEB_LABEL, new LinkLabel(AppConfig.PUBLIC_WEBSITE_DISPLAY, () -> openWebsiteAction.run())));
        info.add(Box.createVerticalStrut(12));
        info.add(aboutInfoRow(Messages.ABOUT_SUPPORT_LABEL, new LinkLabel(AppConfig.SUPPORT_DISPLAY, () -> openSupportAction.run())));
        info.add(Box.createVerticalStrut(18));

        JSeparator line = new JSeparator();
        line.setForeground(ModernThemePalette.border());
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        info.add(line);
        info.add(Box.createVerticalStrut(18));

        JLabel summary = new JLabel("<html><div style='width:520px;'>" + Messages.ABOUT_SUMMARY + "</div></html>");
        summary.setForeground(ModernThemePalette.textSecondary());
        summary.setFont(summary.getFont().deriveFont(Font.PLAIN, 14f));
        info.add(summary);
        info.add(Box.createVerticalGlue());
        top.add(info, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(buildAboutCalloutPanel(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel aboutInfoRow(String label, JLabel valueView) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel labelView = new JLabel(label + ":");
        labelView.setForeground(ModernThemePalette.textPrimary());
        labelView.setFont(labelView.getFont().deriveFont(Font.BOLD, 13f));
        labelView.setPreferredSize(new Dimension(92, 24));
        valueView.setFont(valueView.getFont().deriveFont(Font.PLAIN, 13f));

        row.add(labelView, BorderLayout.WEST);
        row.add(valueView, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildAboutCalloutPanel() {
        JPanel callout = new JPanel(new BorderLayout(16, 0));
        callout.setOpaque(false);
        callout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, ModernThemePalette.accent()),
                new EmptyBorder(18, 22, 18, 22)
        ));
        callout.setBackground(ModernThemePalette.accentSoft());
        callout.setOpaque(true);
        callout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        JLabel icon = new JLabel("i");
        icon.setForeground(ModernThemePalette.accentStrong());
        icon.setFont(icon.getFont().deriveFont(Font.BOLD, 24f));
        icon.setBorder(new EmptyBorder(0, 0, 0, 4));
        callout.add(icon, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(Messages.ABOUT_CALLOUT_TITLE);
        title.setForeground(ModernThemePalette.textPrimary());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        JLabel subtitle = new JLabel(Messages.ABOUT_CALLOUT_TEXT);
        subtitle.setForeground(ModernThemePalette.textSecondary());
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 14f));

        text.add(title);
        text.add(Box.createVerticalStrut(6));
        text.add(subtitle);
        callout.add(text, BorderLayout.CENTER);
        return callout;
    }

    private JPanel pageHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(2, 2, 10, 2));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(ModernThemePalette.textPrimary());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, (float) UiConfig.HEADER_TITLE_SIZE));
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(ModernThemePalette.textSecondary());
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, (float) UiConfig.HEADER_SUBTITLE_SIZE));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitleLabel);
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusPanel() {
        JPanel wrapper = new StatusSurfacePanel();
        wrapper.setLayout(new BorderLayout());
        JPanel status = new JPanel(new BorderLayout(UiConfig.UI_GAP, 0));
        status.setOpaque(false);
        status.setBorder(new EmptyBorder(10, UiConfig.UI_PADDING, 11, UiConfig.UI_PADDING));
        statusLabel.setForeground(ModernThemePalette.textSecondary());
        syncStatusLabel.setForeground(ModernThemePalette.textSecondary());
        status.setPreferredSize(new Dimension(0, 40));

        undoNoticePanel.setLayout(new FlowLayout(FlowLayout.CENTER, UiConfig.UNDO_NOTICE_GAP, UiConfig.UNDO_NOTICE_PADDING_Y));
        undoNoticePanel.setBorder(new EmptyBorder(0, UiConfig.UNDO_NOTICE_PADDING_X, 0, UiConfig.UNDO_NOTICE_PADDING_X));
        undoNoticePanel.setVisible(false);
        undoNoticeLabel.setForeground(ModernThemePalette.textPrimary());
        undoNoticeLabel.setFont(undoNoticeLabel.getFont().deriveFont(Font.BOLD, 12f));
        undoNoticeButton.addActionListener(event -> {
            hideUndoNotice();
            undoBulkAction.run();
        });
        redoNoticeButton.addActionListener(event -> {
            hideUndoNotice();
            redoBulkAction.run();
        });
        undoNoticePanel.add(undoNoticeLabel);
        undoNoticePanel.add(undoNoticeButton);
        undoNoticePanel.add(redoNoticeButton);

        status.add(statusLabel, BorderLayout.WEST);
        status.add(undoNoticePanel, BorderLayout.CENTER);
        status.add(syncStatusLabel, BorderLayout.EAST);
        wrapper.add(status, BorderLayout.CENTER);
        return wrapper;
    }

    private void bindInternalEvents() {
        addButton.addActionListener(event -> {
            if (selectedContactIdOrZero() > 0L) {
                updateAction.run();
            } else {
                addAction.run();
            }
        });
        deleteButton.addActionListener(event -> deleteAction.run());
        cancelButton.addActionListener(event -> cancelContactEdit());
        clearButton.addActionListener(event -> openContactsForNew());
        refreshButton.addActionListener(event -> refreshAction.run());
        saveSettingsButton.addActionListener(event -> saveSettingsAction.run());
        backupNowButton.addActionListener(event -> backupAction.run());
        backupCleanupButton.addActionListener(event -> backupCleanupAction.run());
        photoActionsButton.addActionListener(event -> showPhotoActions());
        quickCallButton.addActionListener(event -> quickCallAction.run());
        quickWhatsAppButton.addActionListener(event -> quickWhatsAppAction.run());
        quickEmailButton.addActionListener(event -> quickEmailAction.run());
        quickCopyButton.addActionListener(event -> quickCopyAction.run());
        editContactButton.addActionListener(event -> showContactEditMode());
        favoritesOnlyCheckBox.addActionListener(event -> {
            if (updatingFilterControls) return;
            clearSavedViewSelection();
            rebuildActiveFilterChips();
            searchAction.run();
            JButton target = favoritesOnlyCheckBox.isSelected()
                    ? favoritesNavigationButton
                    : navigationByPage.get(UiConfig.PAGE_CONTACTS);
            if (target != null) {
                activateNav(target);
            }
        });
        filterToggleButton.addActionListener(event -> toggleFilterDrawer());
        savedViewPickerButton.addActionListener(event -> showSavedViewPickerMenu());
        contactBrowserMoreButton.addActionListener(event -> showContactBrowserMoreMenu());
        companyFilterCombo.addActionListener(event -> handleStructuredFilterChanged());
        cityFilterCombo.addActionListener(event -> handleStructuredFilterChanged());
        categoryFilterCombo.addActionListener(event -> handleStructuredFilterChanged());
        groupFilterCombo.addActionListener(event -> handleStructuredFilterChanged());
        tagFilterCombo.addActionListener(event -> handleStructuredFilterChanged());
        sortFilterCombo.addActionListener(event -> handleStructuredFilterChanged());
        savedViewCombo.addActionListener(event -> {
            if (updatingSavedViewControls) return;
            updateSavedViewActionState();
            if (savedViewCombo.getSelectedIndex() > 0) applySavedViewAction.run();
        });
        saveSavedViewButton.addActionListener(event -> saveSavedViewAction.run());
        manageSavedViewButton.addActionListener(event -> {
            updateSavedViewActionState();
            savedViewMenu.show(manageSavedViewButton, 0, manageSavedViewButton.getHeight());
        });
        renameSavedViewMenuItem.addActionListener(event -> renameSavedViewAction.run());
        copySavedViewMenuItem.addActionListener(event -> copySavedViewAction.run());
        defaultSavedViewMenuItem.addActionListener(event -> toggleDefaultSavedViewAction.run());
        deleteSavedViewMenuItem.addActionListener(event -> deleteSavedViewAction.run());
        bulkCompanyButton.addActionListener(event -> bulkCompanyAction.run());
        bulkCategoryButton.addActionListener(event -> bulkCategoryAction.run());
        bulkAddGroupButton.addActionListener(event -> bulkAddGroupAction.run());
        bulkRemoveGroupButton.addActionListener(event -> bulkRemoveGroupAction.run());
        bulkAddTagButton.addActionListener(event -> bulkAddTagAction.run());
        bulkRemoveTagButton.addActionListener(event -> bulkRemoveTagAction.run());
        bulkFavoriteButton.addActionListener(event -> bulkFavoriteAction.run());
        bulkUnfavoriteButton.addActionListener(event -> bulkUnfavoriteAction.run());
        bulkExportVcfButton.addActionListener(event -> bulkExportVcfAction.run());
        bulkExportCsvButton.addActionListener(event -> bulkExportCsvAction.run());
        bulkTrashButton.addActionListener(event -> bulkTrashAction.run());

        contactList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                handleContactSelectionChanged();
                updateGroupDetail();
                updateTagDetail();
            }
        });
        groupList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) updateGroupDetail();
        });
        tagList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) updateTagDetail();
        });
        smartList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) updateSmartListDetail();
        });
        historyList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) updateHistoryDetail();
        });
        duplicateList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) updateDuplicateDetail();
        });
        trashList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) updateTrashDetail();
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void changed() {
                if (updatingFilterControls) return;
                clearSavedViewSelection();
                searchAction.run();
            }
            @Override public void insertUpdate(DocumentEvent event) { changed(); }
            @Override public void removeUpdate(DocumentEvent event) { changed(); }
            @Override public void changedUpdate(DocumentEvent event) { changed(); }
        });
    }

    private void handleContactSelectionChanged() {
        List<Contact> selected = contactList.getSelectedValuesList();
        if (selected.size() > 1) {
            showBulkSelectionMode(selected);
            return;
        }
        if (selected.size() == 1) {
            fillFormFromSelection();
            return;
        }
        clearDetailCard();
        updateContactActionState(false);
    }

    private void fillFormFromSelection() {
        Contact contact = contactList.getSelectedValue();
        if (contact == null) {
            return;
        }
        nameField.setText(contact.name());
        phoneMethodsPanel.setMethods(contact.phones());
        emailMethodsPanel.setMethods(contact.emails());
        pendingPhotoData = contact.photo() == null ? null : contact.photo().clone();
        companyField.setText(contact.company());
        jobTitleField.setText(contact.jobTitle());
        birthdayField.setText(contact.birthday());
        birthdayReminderCombo.setSelectedItem(contact.birthdayReminderLeadTime());
        importantDatesPanel.setDates(contact.importantDates());
        keepInTouchCombo.setSelectedItem(contact.keepInTouchInterval());
        lastContactedField.setText(contact.lastContactedDate());
        websiteField.setText(contact.website());
        categoryField.setText(contact.category());
        addressArea.setText(contact.address());
        cityField.setText(contact.city());
        districtField.setText(contact.district());
        postalCodeField.setText(contact.postalCode());
        countryField.setText(contact.country());
        notesArea.setText(contact.notes());
        favoriteCheckBox.setSelected(contact.favorite());
        showDetail(contact);
        updateContactActionState(true);
        showContactViewMode();
        contactActivityAction.run();
    }

    private void showDetail(Contact contact) {
        avatarPanel.setContact(contact, photoTool);
        detailNameLabel.setText(contact.name());
        detailCompanyLabel.setText(blank(contact.jobTitle()) ? valueOrDash(contact.company())
                : valueOrDash(contact.company()) + " - " + contact.jobTitle());
        detailPhoneLabel.setText(contactMethodSummary(Messages.PHONE_METHODS_TITLE, contact.phones(), contact.phone()));
        detailEmailLabel.setText(contactMethodSummary(Messages.EMAIL_METHODS_TITLE, contact.emails(), contact.email()));
        detailCategoryLabel.setText(valueOrDash(contact.category()));
        detailBirthdayLabel.setText(valueOrDash(contact.birthday()));
        detailBirthdayReminderLabel.setText(ReminderText.leadTime(contact.birthdayReminderLeadTime()));
        detailImportantDatesLabel.setText(importantDateSummary(contact.importantDates()));
        detailKeepInTouchLabel.setText(ReminderText.keepInTouch(contact.keepInTouchInterval()));
        detailReminderStatusLabel.setText(Messages.EMPTY_VALUE);
        detailCompanyInfoLabel.setText(valueOrDash(contact.company()));
        detailJobTitleInfoLabel.setText(valueOrDash(contact.jobTitle()));
        detailWebsiteInfoLabel.setText(valueOrDash(contact.website()));
        detailAddressInfoLabel.setText(textSummary(contact.address()));
        detailCityInfoLabel.setText(valueOrDash(contact.city()));
        detailDistrictInfoLabel.setText(valueOrDash(contact.district()));
        detailPostalCodeInfoLabel.setText(valueOrDash(contact.postalCode()));
        detailCountryInfoLabel.setText(valueOrDash(contact.country()));
        detailNotesInfoLabel.setText(textSummary(contact.notes()));
        detailFavoriteLabel.setVisible(contact.favorite());
        rebuildMembershipChips(contact.id());
    }

    public void showContactReminderSummary(ContactReminderSummary summary) {
        Contact selected = contactList.getSelectedValue();
        if (selected == null || summary == null) {
            detailReminderStatusLabel.setText(Messages.EMPTY_VALUE);
            return;
        }
        if (!selected.keepInTouchInterval().enabled()) {
            detailKeepInTouchLabel.setText(ReminderText.keepInTouch(selected.keepInTouchInterval()));
        } else if (summary.nextContactDate().isBlank()) {
            detailKeepInTouchLabel.setText(Messages.PROFILE_KEEP_IN_TOUCH_NO_DATE);
        } else if (summary.keepInTouchDue()) {
            detailKeepInTouchLabel.setText(String.format(
                    Messages.PROFILE_KEEP_IN_TOUCH_DUE_FORMAT,
                    ReminderText.keepInTouch(selected.keepInTouchInterval()), summary.nextContactDate()
            ));
        } else {
            detailKeepInTouchLabel.setText(String.format(
                    Messages.PROFILE_KEEP_IN_TOUCH_NEXT_FORMAT,
                    ReminderText.keepInTouch(selected.keepInTouchInterval()), summary.nextContactDate()
            ));
        }
        detailReminderStatusLabel.setText(summary.activeImportantDateReminders() > 0
                ? String.format(Messages.PROFILE_ACTIVE_REMINDERS_FORMAT, summary.activeImportantDateReminders())
                : Messages.EMPTY_VALUE);
    }

    private String importantDateSummary(List<ImportantDate> values) {
        if (values == null || values.isEmpty()) return Messages.EMPTY_VALUE;
        List<String> lines = new ArrayList<>();
        for (ImportantDate value : values) {
            if (value == null || blank(value.dateValue())) continue;
            lines.add(String.format(
                    Messages.PROFILE_IMPORTANT_DATE_FORMAT,
                    htmlEscape(value.label()), htmlEscape(value.dateValue()), htmlEscape(ReminderText.leadTime(value.reminderLeadTime()))
            ));
        }
        return htmlLines(lines);
    }

    private String contactMethodSummary(String title, List<ContactMethod> methods, String fallback) {
        List<String> lines = new ArrayList<>();
        if (methods != null) {
            for (ContactMethod method : methods) {
                if (method != null && !blank(method.value())) {
                    String label = blank(method.label()) ? title : method.label();
                    lines.add("<b>" + htmlEscape(label) + ":</b> " + htmlEscape(method.value()));
                }
            }
        }
        if (lines.isEmpty() && !blank(fallback)) {
            lines.add(htmlEscape(fallback));
        }
        return htmlLines(lines);
    }

    private String textSummary(String value) {
        if (blank(value)) return Messages.EMPTY_VALUE;
        return "<html>" + htmlEscape(value).replace("\n", "<br>") + "</html>";
    }

    private String htmlLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) return Messages.EMPTY_VALUE;
        return "<html>" + String.join("<br>", lines) + "</html>";
    }

    private String htmlEscape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void clearDetailCard() {
        avatarPanel.clearContact();
        detailNameLabel.setText(Messages.DETAIL_EMPTY_TITLE);
        detailCompanyLabel.setText(Messages.DETAIL_EMPTY_SUBTITLE);
        detailPhoneLabel.setText(Messages.EMPTY_VALUE);
        detailEmailLabel.setText(Messages.EMPTY_VALUE);
        detailCategoryLabel.setText(Messages.EMPTY_VALUE);
        detailBirthdayLabel.setText(Messages.EMPTY_VALUE);
        detailBirthdayReminderLabel.setText(Messages.EMPTY_VALUE);
        detailImportantDatesLabel.setText(Messages.EMPTY_VALUE);
        detailKeepInTouchLabel.setText(Messages.EMPTY_VALUE);
        detailReminderStatusLabel.setText(Messages.EMPTY_VALUE);
        detailCompanyInfoLabel.setText(Messages.EMPTY_VALUE);
        detailJobTitleInfoLabel.setText(Messages.EMPTY_VALUE);
        detailWebsiteInfoLabel.setText(Messages.EMPTY_VALUE);
        detailAddressInfoLabel.setText(Messages.EMPTY_VALUE);
        detailCityInfoLabel.setText(Messages.EMPTY_VALUE);
        detailDistrictInfoLabel.setText(Messages.EMPTY_VALUE);
        detailPostalCodeInfoLabel.setText(Messages.EMPTY_VALUE);
        detailCountryInfoLabel.setText(Messages.EMPTY_VALUE);
        detailNotesInfoLabel.setText(Messages.EMPTY_VALUE);
        detailFavoriteLabel.setVisible(false);
        detailChipsPanel.removeAll();
        detailChipsPanel.revalidate();
        detailChipsPanel.repaint();
        showContactActivity(ContactActivityFeed.empty());
    }

    private String valueOrDash(String value) {
        return blank(value) ? Messages.EMPTY_VALUE : value;
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private void showPhotoActions() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem select = new JMenuItem(Messages.PHOTO_SELECT_BUTTON);
        JMenuItem remove = new JMenuItem(Messages.PHOTO_REMOVE_BUTTON);
        select.addActionListener(event -> chooseContactPhoto());
        remove.addActionListener(event -> {
            pendingPhotoData = null;
            avatarPanel.setPreviewPhoto(null, nameField.getText(), photoTool);
        });
        menu.add(select);
        menu.add(remove);
        menu.show(photoActionsButton, 0, photoActionsButton.getHeight());
    }

    private void chooseContactPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(Messages.PHOTO_SELECT_BUTTON);
        chooser.setFileFilter(new FileNameExtensionFilter(Messages.PHOTO_FILE_FILTER, "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            pendingPhotoData = photoTool.loadAndNormalize(chooser.getSelectedFile().toPath());
            avatarPanel.setPreviewPhoto(pendingPhotoData, nameField.getText(), photoTool);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    private void bindKeyboardShortcuts() {
        int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F, menuMask), UiConfig.ACTION_FOCUS_SEARCH);
        getRootPane().getActionMap().put(UiConfig.ACTION_FOCUS_SEARCH, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) {
                showPage(UiConfig.PAGE_CONTACTS);
                searchField.requestFocusInWindow();
            }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_N, menuMask), UiConfig.ACTION_CLEAR_FORM);
        getRootPane().getActionMap().put(UiConfig.ACTION_CLEAR_FORM, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { openContactsForNew(); }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), UiConfig.ACTION_REFRESH);
        getRootPane().getActionMap().put(UiConfig.ACTION_REFRESH, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { refreshAction.run(); }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuMask), UiConfig.ACTION_UNDO_BULK);
        getRootPane().getActionMap().put(UiConfig.ACTION_UNDO_BULK, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { undoBulkAction.run(); }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, menuMask), UiConfig.ACTION_REDO_BULK);
        getRootPane().getActionMap().put(UiConfig.ACTION_REDO_BULK, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { redoBulkAction.run(); }
        });
    }

    private static JTextField field() {
        return new JTextField(UiConfig.FORM_COLUMNS);
    }

    private static JTextArea area() {
        JTextArea area = new JTextArea(UiConfig.TEXT_AREA_ROWS, UiConfig.TEXT_AREA_COLUMNS);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private static JButton primaryButton(String text) {
        return ModernButtons.primary(text);
    }

    private static JButton secondaryButton(String text) {
        return ModernButtons.secondary(text);
    }

    private static JButton textActionButton(String text) {
        return ModernButtons.text(text);
    }

    private static JButton dangerButton(String text) {
        return ModernButtons.danger(text);
    }

    private static JButton quickActionButton(String text, QuickGlyph glyph) {
        return ModernButtons.quick(text, new QuickActionIcon(glyph));
    }

    private static JLabel statValue() {
        JLabel label = new JLabel("0");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 32f));
        return label;
    }

    private static JLabel managementTitleLabel() {
        JLabel label = new JLabel(Messages.MANAGEMENT_NO_SELECTION);
        label.setForeground(ModernThemePalette.textPrimary());
        label.setFont(label.getFont().deriveFont(Font.BOLD, 21f));
        return label;
    }

    private static JLabel managementValueLabel() {
        JLabel label = new JLabel(Messages.EMPTY_VALUE);
        label.setForeground(ModernThemePalette.textPrimary());
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 13.5f));
        return label;
    }


    private JLabel sidebarLabel(String text, int style, int size) {
        JLabel label = new JLabel(text);
        label.setForeground(ModernThemePalette.sidebarText());
        label.setFont(label.getFont().deriveFont(style, size));
        return label;
    }

    private static final class FormBuilder {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private int row;

        private void add(String label, JComponent component) {
            GridBagConstraints left = constraints(0, row, 0.0);
            left.anchor = GridBagConstraints.WEST;
            panel.add(new JLabel(label), left);
            GridBagConstraints right = constraints(1, row, 1.0);
            panel.add(component, right);
            row++;
        }

        private void addArea(String label, JTextArea area) {
            GridBagConstraints left = constraints(0, row, 0.0);
            left.anchor = GridBagConstraints.NORTHWEST;
            panel.add(new JLabel(label), left);
            GridBagConstraints right = constraints(1, row, 1.0);
            right.fill = GridBagConstraints.BOTH;
            right.weighty = 1.0;
            panel.add(new JScrollPane(area), right);
            row++;
        }

        private void addFull(JComponent component) {
            GridBagConstraints full = constraints(0, row, 1.0);
            full.gridwidth = 2;
            panel.add(component, full);
            row++;
        }

        private void addReadOnly(String label, JLabel value) {
            GridBagConstraints left = constraints(0, row, 0.0);
            left.anchor = GridBagConstraints.WEST;
            panel.add(new JLabel(label), left);
            GridBagConstraints right = constraints(1, row, 1.0);
            value.setToolTipText(value.getText());
            panel.add(value, right);
            row++;
        }

        private JPanel finish() {
            GridBagConstraints filler = constraints(0, row, 1.0);
            filler.gridwidth = 2;
            filler.weighty = 1.0;
            filler.fill = GridBagConstraints.BOTH;
            panel.add(Box.createGlue(), filler);
            panel.setBorder(new EmptyBorder(UiConfig.UI_PADDING, UiConfig.UI_PADDING, UiConfig.UI_PADDING, UiConfig.UI_PADDING));
            return panel;
        }

        private static GridBagConstraints constraints(int x, int y, double weightX) {
            GridBagConstraints value = new GridBagConstraints();
            value.gridx = x;
            value.gridy = y;
            value.weightx = weightX;
            value.fill = GridBagConstraints.HORIZONTAL;
            value.insets = new Insets(UiConfig.UI_SMALL_GAP, UiConfig.UI_SMALL_GAP, UiConfig.UI_SMALL_GAP, UiConfig.UI_SMALL_GAP);
            return value;
        }
    }

    private enum QuickGlyph { CALL, WHATSAPP, EMAIL, COPY }

    private static final class QuickActionIcon implements Icon {
        private final QuickGlyph glyph;

        private QuickActionIcon(QuickGlyph glyph) {
            this.glyph = glyph;
        }

        @Override
        public int getIconWidth() { return UiConfig.QUICK_ACTION_ICON_SIZE; }

        @Override
        public int getIconHeight() { return UiConfig.QUICK_ACTION_ICON_SIZE; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(ModernThemePalette.accentStrong());
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int w = getIconWidth();
                int h = getIconHeight();
                switch (glyph) {
                    case CALL -> {
                        g.drawArc(x + 2, y + 1, w - 4, h - 4, 205, 125);
                        g.drawLine(x + 3, y + 11, x + 6, y + 14);
                        g.drawLine(x + 12, y + 4, x + 15, y + 7);
                    }
                    case WHATSAPP -> {
                        g.drawOval(x + 2, y + 2, w - 5, h - 5);
                        g.drawLine(x + 4, y + h - 4, x + 2, y + h - 1);
                        g.drawArc(x + 5, y + 5, w - 10, h - 10, 205, 125);
                    }
                    case EMAIL -> {
                        g.drawRoundRect(x + 1, y + 3, w - 3, h - 7, 3, 3);
                        g.drawLine(x + 2, y + 5, x + w / 2, y + h / 2);
                        g.drawLine(x + w - 2, y + 5, x + w / 2, y + h / 2);
                    }
                    case COPY -> {
                        g.drawRoundRect(x + 5, y + 2, w - 7, h - 7, 3, 3);
                        g.drawRoundRect(x + 2, y + 5, w - 7, h - 7, 3, 3);
                    }
                }
            } finally {
                g.dispose();
            }
        }
    }

    private static final class ThemeBrandIcon implements Icon {
        private final Image light;
        private final Image dark;
        private final int size;

        private ThemeBrandIcon(Image light, Image dark, int size) {
            this.light = light;
            this.dark = dark;
            this.size = size;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Image image = ModernThemePalette.isDark() ? dark : light;
            if (image != null) graphics.drawImage(image, x, y, size, size, component);
        }
    }

    private enum NavGlyph { HOME, CONTACTS, STAR, GROUPS, TAGS, SMART, HISTORY, MAINTENANCE, TRANSFER, TRASH, SYNC, BACKUP, SETTINGS, INFO }

    private static final class NavIcon implements Icon {
        private final NavGlyph glyph;

        private NavIcon(NavGlyph glyph) {
            this.glyph = glyph;
        }

        @Override public int getIconWidth() { return UiConfig.NAV_ICON_SIZE; }
        @Override public int getIconHeight() { return UiConfig.NAV_ICON_SIZE; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(component.getForeground());
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int w = getIconWidth();
                int h = getIconHeight();
                switch (glyph) {
                    case HOME -> {
                        int[] xs = {x + 2, x + w / 2, x + w - 2};
                        int[] ys = {y + 8, y + 2, y + 8};
                        g.drawPolyline(xs, ys, 3);
                        g.drawRect(x + 4, y + 8, w - 8, h - 10);
                    }
                    case CONTACTS -> {
                        g.drawOval(x + 5, y + 2, 8, 8);
                        g.drawArc(x + 2, y + 10, 14, 10, 0, 180);
                    }
                    case STAR -> {
                        int cx = x + w / 2, cy = y + h / 2;
                        int r1 = 8, r2 = 3;
                        int[] xs = new int[10];
                        int[] ys = new int[10];
                        for (int i = 0; i < 10; i++) {
                            double angle = -Math.PI / 2 + i * Math.PI / 5;
                            int r = i % 2 == 0 ? r1 : r2;
                            xs[i] = cx + (int) Math.round(Math.cos(angle) * r);
                            ys[i] = cy + (int) Math.round(Math.sin(angle) * r);
                        }
                        g.drawPolygon(xs, ys, 10);
                    }
                    case GROUPS -> {
                        g.drawOval(x + 2, y + 3, 6, 6);
                        g.drawOval(x + 10, y + 3, 6, 6);
                        g.drawArc(x, y + 9, 9, 8, 0, 180);
                        g.drawArc(x + 8, y + 9, 9, 8, 0, 180);
                    }
                    case TAGS -> {
                        int[] xs = {x + 2, x + 11, x + 16, x + 9, x + 2};
                        int[] ys = {y + 3, y + 3, y + 9, y + 16, y + 9};
                        g.drawPolygon(xs, ys, 5);
                        g.fillOval(x + 5, y + 6, 2, 2);
                    }
                    case SMART -> {
                        g.drawOval(x + 3, y + 3, 12, 12);
                        g.drawLine(x + 9, y + 1, x + 9, y + 5);
                        g.drawLine(x + 9, y + 13, x + 9, y + 17);
                        g.drawLine(x + 1, y + 9, x + 5, y + 9);
                        g.drawLine(x + 13, y + 9, x + 17, y + 9);
                    }
                    case HISTORY -> {
                        g.drawOval(x + 2, y + 2, 14, 14);
                        g.drawLine(x + 9, y + 5, x + 9, y + 10);
                        g.drawLine(x + 9, y + 10, x + 13, y + 12);
                    }
                    case MAINTENANCE -> {
                        g.drawLine(x + 3, y + 15, x + 14, y + 4);
                        g.drawOval(x + 11, y + 2, 5, 5);
                        g.drawRect(x + 2, y + 11, 5, 5);
                    }
                    case TRANSFER -> {
                        g.drawLine(x + 2, y + 6, x + 15, y + 6);
                        g.drawLine(x + 12, y + 3, x + 15, y + 6);
                        g.drawLine(x + 12, y + 9, x + 15, y + 6);
                        g.drawLine(x + 16, y + 13, x + 3, y + 13);
                        g.drawLine(x + 6, y + 10, x + 3, y + 13);
                        g.drawLine(x + 6, y + 16, x + 3, y + 13);
                    }
                    case TRASH -> {
                        g.drawRect(x + 5, y + 5, 9, 11);
                        g.drawLine(x + 3, y + 4, x + 16, y + 4);
                        g.drawLine(x + 7, y + 2, x + 12, y + 2);
                    }
                    case SYNC -> {
                        g.drawArc(x + 2, y + 3, 13, 12, 35, 210);
                        g.drawArc(x + 3, y + 3, 13, 12, 215, 210);
                    }
                    case BACKUP -> {
                        g.drawRoundRect(x + 2, y + 3, 14, 12, 4, 4);
                        g.drawLine(x + 5, y + 8, x + 13, y + 8);
                    }
                    case SETTINGS -> {
                        g.drawOval(x + 3, y + 3, 12, 12);
                        g.drawOval(x + 7, y + 7, 4, 4);
                    }
                    case INFO -> {
                        g.drawOval(x + 2, y + 2, 14, 14);
                        g.drawLine(x + 9, y + 8, x + 9, y + 13);
                        g.fillOval(x + 8, y + 5, 2, 2);
                    }
                }
            } finally {
                g.dispose();
            }
        }
    }

    private static final class AppBackgroundPanel extends JPanel {
        private AppBackgroundPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(ModernThemePalette.background());
                g.fillRect(0, 0, getWidth(), getHeight());
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static final class SidebarSurfacePanel extends JPanel {
        private SidebarSurfacePanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(ModernThemePalette.sidebarStart());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(ModernThemePalette.border());
                g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static final class UndoNoticePanel extends JPanel {
        private UndoNoticePanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(ModernThemePalette.accentSoft());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), UiConfig.UNDO_NOTICE_ARC, UiConfig.UNDO_NOTICE_ARC);
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static final class StatusSurfacePanel extends JPanel {
        private StatusSurfacePanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setColor(ModernThemePalette.surface());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(ModernThemePalette.border());
                g.drawLine(0, 0, getWidth(), 0);
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static final class ModernNavButton extends JButton {
        private boolean active;

        private ModernNavButton(String text, Icon icon) {
            super(text, icon);
            setRolloverEnabled(true);
        }

        private void setActive(boolean value) {
            active = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active || getModel().isRollover()) {
                    g.setColor(active ? ModernThemePalette.sidebarActive() : ModernThemePalette.sidebarHover());
                    g.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                }
                if (active) {
                    g.setColor(ModernThemePalette.accent());
                    g.fillRoundRect(3, 10, 3, Math.max(18, getHeight() - 20), 3, 3);
                }
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static final class DashboardIconBadge extends JPanel {
        private DashboardIconBadge(NavGlyph glyph) {
            setOpaque(false);
            setLayout(new GridBagLayout());
            setPreferredSize(new Dimension(UiConfig.DASHBOARD_ICON_BADGE_SIZE, UiConfig.DASHBOARD_ICON_BADGE_SIZE));
            setMinimumSize(getPreferredSize());
            setMaximumSize(getPreferredSize());
            JLabel icon = new JLabel(new NavIcon(glyph));
            icon.setForeground(ModernThemePalette.accentStrong());
            add(icon);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(ModernThemePalette.accentSoft());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), UiConfig.DASHBOARD_ICON_ARC, UiConfig.DASHBOARD_ICON_ARC);
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static final class DashboardStatusRowPanel extends JPanel {
        private DashboardStatusRowPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(ModernThemePalette.surfaceMuted());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), UiConfig.DASHBOARD_ICON_ARC, UiConfig.DASHBOARD_ICON_ARC);
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static final class ModernPillLabel extends JLabel {
        private ModernPillLabel(String text) {
            super(text, SwingConstants.CENTER);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 14, 8, 14));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(ModernThemePalette.accentSoft());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            } finally {
                g.dispose();
            }
            setForeground(ModernThemePalette.isDark() ? new Color(196, 238, 240) : ModernThemePalette.accentStrong());
            super.paintComponent(graphics);
        }
    }

    private static final class RoundedCardPanel extends JPanel {
        private RoundedCardPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadow = UiConfig.CARD_SHADOW_SIZE;
                int x = shadow / 2;
                int y = shadow / 2;
                int width = Math.max(1, getWidth() - shadow);
                int height = Math.max(1, getHeight() - shadow - UiConfig.CARD_SHADOW_OFFSET);
                g.setColor(ModernThemePalette.shadow());
                g.fillRoundRect(x, y + UiConfig.CARD_SHADOW_OFFSET, width, height, UiConfig.CARD_ARC, UiConfig.CARD_ARC);
                g.setColor(ModernThemePalette.surfaceElevated());
                g.fillRoundRect(x, y, width, height, UiConfig.CARD_ARC, UiConfig.CARD_ARC);
                g.setColor(ModernThemePalette.border());
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(x, y, width, height, UiConfig.CARD_ARC, UiConfig.CARD_ARC);
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static final class InitialsAvatarPanel extends JPanel {
        private String initials = "";
        private byte[] photo;

        private void setContact(Contact contact, ContactPhotoTool photoTool) {
            setPreviewPhoto(contact == null ? null : contact.photo(), contact == null ? "" : contact.name(), photoTool);
        }

        private void setPreviewPhoto(byte[] bytes, String name, ContactPhotoTool photoTool) {
            photo = bytes == null ? null : bytes.clone();
            setNameValue(name);
        }

        private void clearContact() {
            photo = null;
            setNameValue("");
        }

        private void setNameValue(String name) {
            if (name == null || name.isBlank()) initials = "";
            else {
                String[] parts = name.trim().split("\\s+");
                String first = parts[0].substring(0, 1).toUpperCase();
                String second = parts.length > 1 ? parts[parts.length - 1].substring(0, 1).toUpperCase() : "";
                initials = first + second;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight()) - 6;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                java.awt.Shape clip = new java.awt.geom.Ellipse2D.Double(x, y, size, size);
                if (photo != null && photo.length > 0) {
                    try {
                        java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(photo));
                        if (image != null) {
                            java.awt.Shape oldClip = g.getClip();
                            g.clip(clip);
                            double scale = Math.max((double) size / image.getWidth(), (double) size / image.getHeight());
                            int width = (int) Math.round(image.getWidth() * scale);
                            int height = (int) Math.round(image.getHeight() * scale);
                            g.drawImage(image, x + (size - width) / 2, y + (size - height) / 2, width, height, null);
                            g.setClip(oldClip);
                            return;
                        }
                    } catch (java.io.IOException ignored) { }
                }
                g.setColor(UiConfig.BRAND_PRIMARY);
                g.fill(clip);
                if (!initials.isBlank()) {
                    g.setColor(Color.WHITE);
                    g.setFont(getFont().deriveFont(Font.BOLD, Math.max(18f, size * 0.30f)));
                    int width = g.getFontMetrics().stringWidth(initials);
                    int baseline = y + (size + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2;
                    g.drawString(initials, x + (size - width) / 2, baseline);
                }
            } finally { g.dispose(); }
        }
    }
}
