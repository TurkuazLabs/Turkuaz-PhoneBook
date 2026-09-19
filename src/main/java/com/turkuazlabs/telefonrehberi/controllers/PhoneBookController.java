// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/controllers/PhoneBookController.java
// # 📌 Amac: GUI kullanici isteklerini alir ve ilgili servis katmanlarina yonlendirir.
// # 📌 Controller - Java
// # Version: 2.39.0
// # Aciklama: GUI eventlerini Service katmanina baglar; canli dil yenileme sonucunu View'a iletir ve uzun I/O islemlerini EDT disinda calistirir.
// # Bagimli Oldugu Katman: Controller | Service | View | Model | Language
package com.turkuazlabs.telefonrehberi.controllers;

import com.turkuazlabs.telefonrehberi.config.UiConfig;
import com.turkuazlabs.telefonrehberi.language.Messages;
import com.turkuazlabs.telefonrehberi.models.AppSettings;
import com.turkuazlabs.telefonrehberi.models.DuplicateCandidate;
import com.turkuazlabs.telefonrehberi.models.ContactFileFormat;
import com.turkuazlabs.telefonrehberi.models.ImportResult;
import com.turkuazlabs.telefonrehberi.models.SettingsSaveResult;
import com.turkuazlabs.telefonrehberi.models.SmartList;
import com.turkuazlabs.telefonrehberi.models.SmartListDraft;
import com.turkuazlabs.telefonrehberi.models.SyncServerInfo;
import com.turkuazlabs.telefonrehberi.models.SavedContactView;
import com.turkuazlabs.telefonrehberi.services.BackupService;
import com.turkuazlabs.telefonrehberi.services.BulkUndoService;
import com.turkuazlabs.telefonrehberi.services.ContactService;
import com.turkuazlabs.telefonrehberi.services.ContactFilterService;
import com.turkuazlabs.telefonrehberi.services.ContactQuickActionService;
import com.turkuazlabs.telefonrehberi.services.ExternalLinkService;
import com.turkuazlabs.telefonrehberi.services.GroupService;
import com.turkuazlabs.telefonrehberi.services.ImportExportService;
import com.turkuazlabs.telefonrehberi.services.MaintenanceService;
import com.turkuazlabs.telefonrehberi.services.SavedContactViewService;
import com.turkuazlabs.telefonrehberi.services.SettingsService;
import com.turkuazlabs.telefonrehberi.services.SmartListService;
import com.turkuazlabs.telefonrehberi.services.TagService;
import com.turkuazlabs.telefonrehberi.services.ThemeService;
import com.turkuazlabs.telefonrehberi.views.PhoneBookFrame;

import java.nio.file.Path;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.SwingWorker;

public final class PhoneBookController {
    private final ContactService contactService;
    private final BulkUndoService bulkUndoService;
    private final ContactFilterService contactFilterService;
    private final SavedContactViewService savedContactViewService;
    private final ContactQuickActionService contactQuickActionService;
    private final ExternalLinkService externalLinkService;
    private final ThemeService themeService;
    private final SettingsService settingsService;
    private final BackupService backupService;
    private final GroupService groupService;
    private final TagService tagService;
    private final SmartListService smartListService;
    private final MaintenanceService maintenanceService;
    private final ImportExportService importExportService;
    private final PhoneBookFrame view;
    private final SyncServerInfo syncServerInfo;
    private final int migratedContactCount;

    public PhoneBookController(
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
            PhoneBookFrame view,
            SyncServerInfo syncServerInfo,
            int migratedContactCount
    ) {
        this.contactService = contactService;
        this.bulkUndoService = bulkUndoService;
        this.contactFilterService = contactFilterService;
        this.savedContactViewService = savedContactViewService;
        this.contactQuickActionService = contactQuickActionService;
        this.externalLinkService = externalLinkService;
        this.themeService = themeService;
        this.settingsService = settingsService;
        this.backupService = backupService;
        this.groupService = groupService;
        this.tagService = tagService;
        this.smartListService = smartListService;
        this.maintenanceService = maintenanceService;
        this.importExportService = importExportService;
        this.view = view;
        this.syncServerInfo = syncServerInfo;
        this.migratedContactCount = migratedContactCount;
    }

    public void initialize() {
        view.setAddAction(this::addContact);
        view.setUpdateAction(this::updateContact);
        view.setDeleteAction(this::deleteContact);
        view.setRefreshAction(this::refreshAll);
        view.setSearchAction(this::refreshContacts);
        view.setSaveSavedViewAction(this::saveCurrentContactView);
        view.setApplySavedViewAction(this::applySelectedContactView);
        view.setDeleteSavedViewAction(this::deleteSelectedContactView);
        view.setRenameSavedViewAction(this::renameSelectedContactView);
        view.setCopySavedViewAction(this::copySelectedContactView);
        view.setToggleDefaultSavedViewAction(this::toggleDefaultSelectedContactView);
        view.setMobileSyncAction(() -> view.showSyncInfo(syncServerInfo));
        view.setSaveSettingsAction(this::saveSettings);
        view.setBackupAction(this::createBackup);
        view.setBackupCleanupAction(this::cleanupBackups);
        view.setCreateGroupAction(this::createGroup);
        view.setDeleteGroupAction(this::deleteGroup);
        view.setAddGroupContactAction(this::addContactToGroup);
        view.setRemoveGroupContactAction(this::removeContactFromGroup);
        view.setCreateTagAction(this::createTag);
        view.setDeleteTagAction(this::deleteTag);
        view.setAddTagContactAction(this::addContactToTag);
        view.setRemoveTagContactAction(this::removeContactFromTag);
        view.setCreateSmartListAction(this::createSmartList);
        view.setDeleteSmartListAction(this::deleteSmartList);
        view.setRunSmartListAction(this::runSmartList);
        view.setRestoreHistoryAction(this::restoreHistory);
        view.setScanDuplicatesAction(this::scanDuplicates);
        view.setMergeDuplicateAction(this::mergeDuplicate);
        view.setImportAction(this::importContacts);
        view.setExportVcfAction(() -> exportContacts(ContactFileFormat.VCARD));
        view.setExportCsvAction(() -> exportContacts(ContactFileFormat.CSV));
        view.setRestoreTrashAction(this::restoreTrash);
        view.setDeleteTrashAction(this::deleteTrashPermanently);
        view.setRefreshManagementAction(this::refreshManagement);
        view.setQuickCallAction(this::quickCall);
        view.setQuickWhatsAppAction(this::quickWhatsApp);
        view.setQuickEmailAction(this::quickEmail);
        view.setQuickCopyAction(this::quickCopy);
        view.setOpenWebsiteAction(this::openWebsite);
        view.setOpenSupportAction(this::openSupport);
        view.setBulkCompanyAction(this::bulkSetCompany);
        view.setBulkCategoryAction(this::bulkSetCategory);
        view.setBulkAddGroupAction(this::bulkAddToGroup);
        view.setBulkRemoveGroupAction(this::bulkRemoveFromGroup);
        view.setBulkAddTagAction(this::bulkAddToTag);
        view.setBulkRemoveTagAction(this::bulkRemoveFromTag);
        view.setBulkFavoriteAction(() -> bulkSetFavorite(true));
        view.setBulkUnfavoriteAction(() -> bulkSetFavorite(false));
        view.setBulkExportVcfAction(() -> bulkExportContacts(ContactFileFormat.VCARD));
        view.setBulkExportCsvAction(() -> bulkExportContacts(ContactFileFormat.CSV));
        view.setBulkTrashAction(this::bulkMoveToTrash);
        view.setUndoBulkAction(this::undoLastBulkAction);
        view.setRedoBulkAction(this::redoLastBulkAction);
        view.setContactActivityAction(this::refreshContactActivity);

        AppSettings settings = settingsService.loadSettings();
        view.applySettings(settings);
        view.showSyncState(syncServerInfo);
        view.showPage(settings.startupPage());
        refreshSavedContactViews(null);

        refreshAll();
        applyDefaultSavedContactViewAtStartup();
        view.showWindow();

        if (migratedContactCount > 0) {
            view.showMessage(String.format(Messages.MIGRATION_FORMAT, migratedContactCount));
        }
        executeAsync(
                () -> backupService.autoBackupIfNeeded(settings),
                automaticBackup -> automaticBackup.ifPresent(path -> {
                    view.showBackups(backupService.listBackups());
                    view.showMessage(String.format(Messages.AUTO_BACKUP_CREATED_FORMAT, path.getFileName()));
                })
        );
    }

    private void addContact() {
        execute(() -> {
            contactService.addContact(view.readContactInput());
            view.clearForm();
            refreshAll();
            view.showMessage(Messages.ADDED);
        });
    }

    private void updateContact() {
        execute(() -> {
            contactService.updateContact(view.requireSelectedContactId(), view.readContactInput());
            refreshAll();
            view.showMessage(Messages.UPDATED);
        });
    }

    private void deleteContact() {
        execute(() -> {
            long id = view.requireSelectedContactId();
            if (view.confirmDelete()) {
                contactService.deleteContact(id);
                view.clearForm();
                refreshAll();
                view.showMessage(Messages.DELETED);
            }
        });
    }

    private void createGroup() {
        execute(() -> {
            String name = view.promptGroupName();
            if (name == null) return;
            String color = view.promptColor();
            if (color == null) return;
            groupService.createGroup(name, color);
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(Messages.GROUP_CREATED);
        });
    }

    private void deleteGroup() {
        execute(() -> {
            groupService.deleteGroup(view.requireSelectedGroupId());
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(Messages.GROUP_DELETED);
        });
    }

    private void addContactToGroup() {
        execute(() -> {
            groupService.addContact(view.requireSelectedGroupId(), view.requireSelectedContactId());
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(Messages.GROUP_CONTACT_ADDED);
        });
    }

    private void removeContactFromGroup() {
        execute(() -> {
            groupService.removeContact(view.requireSelectedGroupId(), view.requireSelectedContactId());
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(Messages.GROUP_CONTACT_REMOVED);
        });
    }

    private void createTag() {
        execute(() -> {
            String name = view.promptTagName();
            if (name == null) return;
            String color = view.promptColor();
            if (color == null) return;
            tagService.createTag(name, color);
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(Messages.TAG_CREATED);
        });
    }

    private void deleteTag() {
        execute(() -> {
            tagService.deleteTag(view.requireSelectedTagId());
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(Messages.TAG_DELETED);
        });
    }

    private void addContactToTag() {
        execute(() -> {
            tagService.addContact(view.requireSelectedTagId(), view.requireSelectedContactId());
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(Messages.TAG_CONTACT_ADDED);
        });
    }

    private void removeContactFromTag() {
        execute(() -> {
            tagService.removeContact(view.requireSelectedTagId(), view.requireSelectedContactId());
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(Messages.TAG_CONTACT_REMOVED);
        });
    }

    private void createSmartList() {
        execute(() -> {
            SmartListDraft draft = view.promptSmartListDraft();
            if (draft == null) return;
            smartListService.create(draft);
            refreshManagement();
            view.showMessage(Messages.SMART_CREATED);
        });
    }

    private void deleteSmartList() {
        execute(() -> {
            smartListService.delete(view.requireSelectedSmartList().id());
            refreshManagement();
            view.showMessage(Messages.SMART_DELETED);
        });
    }

    private void runSmartList() {
        execute(() -> {
            SmartList rule = view.requireSelectedSmartList();
            var contacts = smartListService.evaluate(rule);
            view.showContacts(contacts);
            view.showPage(UiConfig.PAGE_CONTACTS);
            view.showMessage(String.format(Messages.SMART_RESULT_FORMAT, contacts.size()));
        });
    }

    private void restoreHistory() {
        execute(() -> {
            contactService.restoreHistory(view.requireSelectedHistoryId());
            refreshAll();
            view.showMessage(Messages.HISTORY_RESTORED);
        });
    }

    private void scanDuplicates() {
        executeAsync(maintenanceService::findDuplicates, view::showDuplicates);
    }

    private void mergeDuplicate() {
        execute(() -> {
            DuplicateCandidate candidate = view.requireSelectedDuplicate();
            if (!view.confirmDuplicateMerge()) return;
            maintenanceService.merge(candidate.primary().id(), candidate.duplicate().id());
            refreshAll();
            scanDuplicates();
            view.showMessage(Messages.DUPLICATE_MERGED);
        });
    }

    private void importContacts() {
        Path path = view.chooseImportFile();
        if (path == null) return;
        executeAsync(
                () -> importExportService.importFile(path),
                result -> {
                    refreshAll();
                    view.showMessage(String.format(Messages.IMPORT_RESULT_FORMAT, result.imported(), result.skipped()));
                }
        );
    }

    private void exportContacts(ContactFileFormat format) {
        Path path = view.chooseExportFile(format);
        if (path == null) return;
        executeAsync(
                () -> {
                    importExportService.exportFile(path, format);
                    return path;
                },
                exportedPath -> view.showMessage(String.format(Messages.EXPORT_DONE_FORMAT, exportedPath.getFileName()))
        );
    }

    private void restoreTrash() {
        execute(() -> {
            contactService.restoreTrashContact(view.requireSelectedTrashContactId());
            refreshAll();
            view.showMessage(Messages.TRASH_RESTORED);
        });
    }

    private void deleteTrashPermanently() {
        execute(() -> {
            long id = view.requireSelectedTrashContactId();
            if (!view.confirmPermanentDelete()) return;
            maintenanceService.permanentlyDelete(id);
            refreshAll();
            view.showMessage(Messages.TRASH_DELETED);
        });
    }

    private void quickCall() {
        execute(() -> contactQuickActionService.call(requireSelectedContact()));
    }

    private void quickWhatsApp() {
        execute(() -> contactQuickActionService.openWhatsApp(requireSelectedContact()));
    }

    private void quickEmail() {
        execute(() -> contactQuickActionService.email(requireSelectedContact()));
    }

    private void quickCopy() {
        execute(() -> {
            String copied = contactQuickActionService.copyPreferred(requireSelectedContact());
            view.showMessage(String.format(Messages.QUICK_ACTION_COPY_DONE_FORMAT, copied));
        });
    }

    private void bulkSetCompany() {
        execute(() -> {
            String company = view.promptBulkCompany();
            if (company == null) return;
            int changed = bulkUndoService.setCompanies(view.requireSelectedContactIds(), company);
            refreshAll();
            refreshContactFilterContext();
            view.showMessage(String.format(Messages.BULK_COMPANY_DONE_FORMAT, changed));
            showCurrentUndoNotice();
        });
    }

    private void bulkSetCategory() {
        execute(() -> {
            String category = view.promptBulkCategory();
            if (category == null) return;
            int changed = bulkUndoService.setCategories(view.requireSelectedContactIds(), category);
            refreshAll();
            refreshContactFilterContext();
            view.showMessage(String.format(Messages.BULK_CATEGORY_DONE_FORMAT, changed));
            showCurrentUndoNotice();
        });
    }

    private void bulkAddToGroup() {
        execute(() -> {
            List<Long> contactIds = view.requireSelectedContactIds();
            Long groupId = view.promptBulkGroupId();
            if (groupId == null) return;
            int added = bulkUndoService.addToGroup(groupId, contactIds);
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(String.format(Messages.BULK_GROUP_ADDED_FORMAT, added));
            showCurrentUndoNotice();
        });
    }

    private void bulkRemoveFromGroup() {
        execute(() -> {
            List<Long> contactIds = view.requireSelectedContactIds();
            Long groupId = view.promptBulkRemoveGroupId();
            if (groupId == null) return;
            int removed = bulkUndoService.removeFromGroup(groupId, contactIds);
            refreshManagement();
            refreshContactFilterContext();
            refreshContacts();
            view.showMessage(String.format(Messages.BULK_GROUP_REMOVED_FORMAT, removed));
            showCurrentUndoNotice();
        });
    }

    private void bulkAddToTag() {
        execute(() -> {
            List<Long> contactIds = view.requireSelectedContactIds();
            Long tagId = view.promptBulkTagId();
            if (tagId == null) return;
            int added = bulkUndoService.addToTag(tagId, contactIds);
            refreshManagement();
            refreshContactFilterContext();
            view.showMessage(String.format(Messages.BULK_TAG_ADDED_FORMAT, added));
            showCurrentUndoNotice();
        });
    }

    private void bulkRemoveFromTag() {
        execute(() -> {
            List<Long> contactIds = view.requireSelectedContactIds();
            Long tagId = view.promptBulkRemoveTagId();
            if (tagId == null) return;
            int removed = bulkUndoService.removeFromTag(tagId, contactIds);
            refreshManagement();
            refreshContactFilterContext();
            refreshContacts();
            view.showMessage(String.format(Messages.BULK_TAG_REMOVED_FORMAT, removed));
            showCurrentUndoNotice();
        });
    }

    private void bulkSetFavorite(boolean favorite) {
        execute(() -> {
            int changed = bulkUndoService.setFavorites(view.requireSelectedContactIds(), favorite);
            refreshAll();
            view.showMessage(String.format(
                    favorite ? Messages.BULK_FAVORITE_DONE_FORMAT : Messages.BULK_UNFAVORITE_DONE_FORMAT, changed
            ));
            showCurrentUndoNotice();
        });
    }

    private void bulkExportContacts(ContactFileFormat format) {
        List<Long> contactIds;
        try {
            contactIds = view.requireSelectedContactIds();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            view.showError(exception.getMessage());
            return;
        }
        Path path = view.chooseExportFile(format);
        if (path == null) return;
        executeAsync(
                () -> importExportService.exportContacts(path, format, contactIds),
                exported -> view.showMessage(String.format(Messages.BULK_EXPORT_DONE_FORMAT, exported, path.getFileName()))
        );
    }

    private void bulkMoveToTrash() {
        execute(() -> {
            List<Long> contactIds = view.requireSelectedContactIds();
            if (!view.confirmBulkTrash(contactIds.size())) return;
            int deleted = bulkUndoService.moveToTrash(contactIds);
            refreshAll();
            view.showMessage(String.format(Messages.BULK_TRASH_DONE_FORMAT, deleted));
            showCurrentUndoNotice();
        });
    }

    private void showCurrentUndoNotice() {
        bulkUndoService.currentLabel().ifPresent(view::showUndoNotice);
    }

    private void undoLastBulkAction() {
        execute(() -> {
            String label = bulkUndoService.undo();
            refreshAll();
            view.showMessage(String.format(Messages.UNDO_DONE_FORMAT, label));
            bulkUndoService.redoLabel().ifPresent(view::showRedoNotice);
        });
    }

    private void redoLastBulkAction() {
        execute(() -> {
            String label = bulkUndoService.redo();
            refreshAll();
            view.showMessage(String.format(Messages.REDO_DONE_FORMAT, label));
            bulkUndoService.currentLabel().ifPresent(view::showUndoNotice);
        });
    }

    private void openWebsite() {
        execute(externalLinkService::openWebsite);
    }

    private void openSupport() {
        execute(externalLinkService::openSupport);
    }

    private com.turkuazlabs.telefonrehberi.models.Contact requireSelectedContact() {
        long id = view.requireSelectedContactId();
        return contactService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(Messages.ERROR_CONTACT_NOT_FOUND));
    }

    private void saveCurrentContactView() {
        execute(() -> {
            String name = view.promptSavedContactViewName();
            if (name == null) return;
            SavedContactView saved = savedContactViewService.save(name, view.readContactFilter());
            refreshSavedContactViews(saved.name());
            view.showMessage(String.format(Messages.SAVED_VIEW_SAVED_FORMAT, saved.name()));
        });
    }

    private void applySelectedContactView() {
        execute(() -> {
            String name = view.requireSelectedSavedContactViewName();
            SavedContactView saved = savedContactViewService.requireByName(name);
            view.applyContactFilter(saved.filter());
            refreshContacts();
            view.showMessage(String.format(Messages.SAVED_VIEW_APPLIED_FORMAT, saved.name()));
        });
    }

    private void deleteSelectedContactView() {
        execute(() -> {
            String name = view.requireSelectedSavedContactViewName();
            if (!view.confirmDeleteSavedContactView(name)) return;
            savedContactViewService.deleteByName(name);
            refreshSavedContactViews("");
            view.showMessage(String.format(Messages.SAVED_VIEW_DELETED_FORMAT, name));
        });
    }

    private void renameSelectedContactView() {
        execute(() -> {
            String currentName = view.requireSelectedSavedContactViewName();
            String newName = view.promptRenameSavedContactViewName(currentName);
            if (newName == null) return;
            SavedContactView renamed = savedContactViewService.rename(currentName, newName);
            refreshSavedContactViews(renamed.name());
            view.showMessage(String.format(Messages.SAVED_VIEW_RENAMED_FORMAT, renamed.name()));
        });
    }

    private void copySelectedContactView() {
        execute(() -> {
            String sourceName = view.requireSelectedSavedContactViewName();
            String copyName = view.promptCopySavedContactViewName(sourceName);
            if (copyName == null) return;
            SavedContactView copied = savedContactViewService.copy(sourceName, copyName);
            refreshSavedContactViews(copied.name());
            view.showMessage(String.format(Messages.SAVED_VIEW_COPIED_FORMAT, copied.name()));
        });
    }

    private void toggleDefaultSelectedContactView() {
        execute(() -> {
            String name = view.requireSelectedSavedContactViewName();
            SavedContactView selected = savedContactViewService.requireByName(name);
            if (selected.defaultView()) {
                savedContactViewService.clearDefault();
                refreshSavedContactViews(selected.name());
                view.showMessage(Messages.SAVED_VIEW_DEFAULT_CLEARED);
                return;
            }
            SavedContactView updated = savedContactViewService.setDefault(name);
            refreshSavedContactViews(updated.name());
            view.showMessage(String.format(Messages.SAVED_VIEW_DEFAULT_SET_FORMAT, updated.name()));
        });
    }

    private void applyDefaultSavedContactViewAtStartup() {
        savedContactViewService.defaultView().ifPresent(saved -> {
            view.applyContactFilter(saved.filter());
            refreshSavedContactViews(saved.name());
            refreshContacts();
        });
    }

    private void refreshSavedContactViews(String selectedName) {
        view.showSavedContactViews(savedContactViewService.list(), selectedName);
    }

    private void refreshAll() {
        refreshContactFilterContext();
        view.showDashboard(contactService.dashboardSummary());
        view.showBackups(backupService.listBackups());
        refreshManagement();
    }

    private void refreshContactFilterContext() {
        view.showContactFilterOptions(contactFilterService.options());
        refreshContacts();
    }

    private void refreshManagement() {
        execute(() -> {
            view.showGroups(groupService.listGroups());
            view.showTags(tagService.listTags());
            view.showContactMemberships(groupService.membershipsByContact(), tagService.membershipsByContact());
            view.showSmartLists(smartListService.listSmartLists());
            view.showHistory(contactService.listHistory());
            view.showTrash(contactService.listTrash());
        });
    }

    private void refreshContacts() {
        execute(() -> view.showContacts(contactFilterService.filter(view.readContactFilter())));
    }

    private void refreshContactActivity() {
        execute(() -> {
            long contactId = view.selectedContactId();
            view.showContactActivity(contactService.contactActivity(contactId, view.readContactActivityFilter()));
            view.showContactReminderSummary(contactService.reminderSummary(contactId));
        });
    }

    private void saveSettings() {
        execute(() -> {
            SettingsSaveResult result = settingsService.saveSettings(view.readSettingsInput());
            AppSettings saved = settingsService.loadSettings();
            if (result.languageChanged()) {
                view.refreshLanguage(saved);
            } else {
                view.applySettings(saved);
            }
            view.showMessage(result.restartRequired() ? Messages.SETTINGS_SAVED_RESTART : Messages.SETTINGS_SAVED);
        });
    }

    private void createBackup() {
        AppSettings settings = settingsService.loadSettings();
        executeAsync(
                () -> backupService.backupNow(settings.backupRetention()),
                path -> {
                    view.showBackups(backupService.listBackups());
                    view.showMessage(String.format(Messages.BACKUP_CREATED_FORMAT, path.getFileName()));
                }
        );
    }

    private void cleanupBackups() {
        AppSettings settings = settingsService.loadSettings();
        executeAsync(
                () -> backupService.cleanupExcessBackups(settings.backupRetention()),
                deleted -> {
                    view.showBackups(backupService.listBackups());
                    view.showMessage(String.format(Messages.BACKUP_CLEANUP_DONE_FORMAT, deleted));
                }
        );
    }

    private <T> void executeAsync(Supplier<T> backgroundAction, Consumer<T> successAction) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() {
                return backgroundAction.get();
            }

            @Override
            protected void done() {
                try {
                    successAction.accept(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    view.showError(Messages.ERROR_OPERATION_INTERRUPTED);
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    String message = cause == null || cause.getMessage() == null || cause.getMessage().isBlank()
                            ? Messages.ERROR_OPERATION_FAILED
                            : cause.getMessage();
                    view.showError(message);
                } catch (IllegalArgumentException | IllegalStateException exception) {
                    view.showError(exception.getMessage());
                }
            }
        }.execute();
    }

    private void execute(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            view.showError(exception.getMessage());
        }
    }
}
