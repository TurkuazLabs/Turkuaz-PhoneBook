// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/mobile/ios/TurkuazTelefonRehberiIOS/repositories/DeviceContactRepository.swift
// # 📌 Amac: iOS sistem rehberini Contacts framework uzerinden genisletilmis alanlarla okur ve yazar.
// # 📌 Repository - Swift
// # Version: 2.37.1
// # Aciklama: Telefon/e-posta duplicate esleme ve kalici sync UUID mappingi icin native contact identifier dondurur.
// # Bagimli Oldugu Katman: Repository | Model
import Contacts
import Foundation

final class DeviceContactRepository {
    private let store = CNContactStore()

    func ensureAccess() async throws {
        let status = CNContactStore.authorizationStatus(for: .contacts)
        switch status {
        case .authorized, .limited: return
        case .notDetermined:
            let granted = try await store.requestAccess(for: .contacts)
            if !granted { throw ContactRepositoryError.permissionDenied }
        default: throw ContactRepositoryError.permissionDenied
        }
    }

    func findAll() throws -> [MobileContact] {
        let keys = contactKeys()
        let request = CNContactFetchRequest(keysToFetch: keys)
        var contacts: [MobileContact] = []
        try store.enumerateContacts(with: request) { contact, _ in
            let phones = self.phoneMethods(contact.phoneNumbers)
            let emails = self.emailMethods(contact.emailAddresses)
            if phones.isEmpty && emails.isEmpty { return }
            let displayName = [contact.givenName, contact.familyName].filter { !$0.isEmpty }.joined(separator: " ")
            let postal = contact.postalAddresses.first?.value
            let primaryPhone = self.primaryValue(phones)
            let primaryEmail = self.primaryValue(emails)
            contacts.append(MobileContact(
                id: contact.identifier,
                syncUuid: "",
                name: displayName.isEmpty ? (!primaryPhone.isEmpty ? primaryPhone : primaryEmail) : displayName,
                phone: self.valueAt(phones, 0),
                phoneSecondary: self.valueAt(phones, 1),
                phoneWork: self.firstLabel(phones, MobileContactMethod.workLabel),
                email: self.valueAt(emails, 0),
                emailSecondary: self.valueAt(emails, 1),
                company: contact.organizationName,
                jobTitle: contact.jobTitle,
                birthday: self.formatBirthday(contact.birthday),
                website: contact.urlAddresses.first?.value as String? ?? "",
                category: "",
                address: postal?.street ?? "",
                city: postal?.city ?? "",
                district: postal?.state ?? "",
                postalCode: postal?.postalCode ?? "",
                country: postal?.country ?? "",
                notes: "",
                favorite: false,
                phones: phones,
                emails: emails,
                photoBase64: contact.imageDataAvailable ? (contact.thumbnailImageData?.base64EncodedString() ?? "") : ""
            ))
        }
        return contacts
    }

    func ensureDesktopContact(_ contact: MobileContact, preferredContactId: String) throws -> EnsureContactResult {
        let preferred = preferredContactId.trimmingCharacters(in: .whitespacesAndNewlines)
        if !preferred.isEmpty, contactExists(preferred) { return EnsureContactResult(contactId: preferred, created: false) }
        if let matching = try findMatchingIdentifier(contact) { return EnsureContactResult(contactId: matching, created: false) }

        let newContact = CNMutableContact()
        newContact.givenName = contact.name
        newContact.phoneNumbers = contact.phones.map { CNLabeledValue(label: self.cnPhoneLabel($0.label), value: CNPhoneNumber(stringValue: $0.value)) }
        newContact.emailAddresses = contact.emails.map { CNLabeledValue(label: self.cnEmailLabel($0.label), value: $0.value as NSString) }
        newContact.organizationName = contact.company
        newContact.jobTitle = contact.jobTitle
        newContact.postalAddresses = postalValues(contact)
        newContact.birthday = parseBirthday(contact.birthday)
        if !contact.website.isEmpty {
            newContact.urlAddresses = [CNLabeledValue(label: CNLabelURLAddressHomePage, value: contact.website as NSString)]
        }
        if !contact.photoBase64.isEmpty, let data = Data(base64Encoded: contact.photoBase64) { newContact.imageData = data }
        let request = CNSaveRequest()
        request.add(newContact, toContainerWithIdentifier: nil)
        try store.execute(request)
        if !newContact.identifier.isEmpty { return EnsureContactResult(contactId: newContact.identifier, created: true) }
        if let matched = try findMatchingIdentifier(contact) { return EnsureContactResult(contactId: matched, created: true) }
        throw ContactRepositoryError.createdContactNotFound
    }

    func addIfMissing(_ contact: MobileContact) throws -> Bool {
        try ensureDesktopContact(contact, preferredContactId: "").created
    }

    private func contactKeys() -> [CNKeyDescriptor] {
        [
            CNContactIdentifierKey as CNKeyDescriptor,
            CNContactGivenNameKey as CNKeyDescriptor,
            CNContactFamilyNameKey as CNKeyDescriptor,
            CNContactPhoneNumbersKey as CNKeyDescriptor,
            CNContactEmailAddressesKey as CNKeyDescriptor,
            CNContactOrganizationNameKey as CNKeyDescriptor,
            CNContactJobTitleKey as CNKeyDescriptor,
            CNContactPostalAddressesKey as CNKeyDescriptor,
            CNContactBirthdayKey as CNKeyDescriptor,
            CNContactUrlAddressesKey as CNKeyDescriptor,
            CNContactImageDataAvailableKey as CNKeyDescriptor,
            CNContactThumbnailImageDataKey as CNKeyDescriptor
        ]
    }

    private func contactExists(_ identifier: String) -> Bool {
        do {
            _ = try store.unifiedContact(withIdentifier: identifier, keysToFetch: [CNContactIdentifierKey as CNKeyDescriptor])
            return true
        } catch { return false }
    }

    private func findMatchingIdentifier(_ contact: MobileContact) throws -> String? {
        let phoneTargets = Set(contact.phones.map { normalizePhone($0.value) }.filter { !$0.isEmpty })
        let emailTargets = Set(contact.emails.map { $0.value.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() }.filter { !$0.isEmpty })
        if phoneTargets.isEmpty && emailTargets.isEmpty { return nil }
        let request = CNContactFetchRequest(keysToFetch: [
            CNContactIdentifierKey as CNKeyDescriptor,
            CNContactPhoneNumbersKey as CNKeyDescriptor,
            CNContactEmailAddressesKey as CNKeyDescriptor
        ])
        var matches = Set<String>()
        try store.enumerateContacts(with: request) { current, _ in
            let phoneMatch = current.phoneNumbers.contains { phoneTargets.contains(self.normalizePhone($0.value.stringValue)) }
            let emailMatch = current.emailAddresses.contains {
                emailTargets.contains(($0.value as String).trimmingCharacters(in: .whitespacesAndNewlines).lowercased())
            }
            if phoneMatch || emailMatch { matches.insert(current.identifier) }
        }
        return matches.count == 1 ? matches.first : nil
    }

    private func phoneMethods(_ values: [CNLabeledValue<CNPhoneNumber>]) -> [MobileContactMethod] {
        values.enumerated().map { index, value in
            MobileContactMethod(kind: MobileContactMethod.phoneKind, label: displayLabel(value.label, fallback: MobileContactMethod.phoneLabel), value: value.value.stringValue, primary: index == 0, position: index)
        }
    }

    private func emailMethods(_ values: [CNLabeledValue<NSString>]) -> [MobileContactMethod] {
        values.enumerated().map { index, value in
            MobileContactMethod(kind: MobileContactMethod.emailKind, label: displayLabel(value.label, fallback: MobileContactMethod.emailLabel), value: value.value as String, primary: index == 0, position: index)
        }
    }

    private func displayLabel(_ label: String?, fallback: String) -> String {
        guard let label, !label.isEmpty else { return fallback }
        if label == CNLabelPhoneNumberMobile { return MobileContactMethod.mobileLabel }
        if label == CNLabelWork { return MobileContactMethod.workLabel }
        if label == CNLabelHome { return MobileContactMethod.homeLabel }
        if label == CNLabelOther { return MobileContactMethod.otherLabel }
        return CNLabeledValue<NSString>.localizedString(forLabel: label)
    }

    private func cnPhoneLabel(_ label: String) -> String {
        let value = label.lowercased()
        if value.contains("cep") || value.contains("mobil") { return CNLabelPhoneNumberMobile }
        if value == "is" || value.contains("work") { return CNLabelWork }
        if value.contains("ev") || value.contains("home") { return CNLabelHome }
        if value == "diger" { return CNLabelOther }
        return label
    }

    private func cnEmailLabel(_ label: String) -> String {
        let value = label.lowercased()
        if value == "is" || value.contains("work") { return CNLabelWork }
        if value.contains("ev") || value.contains("home") { return CNLabelHome }
        if value == MobileContactMethod.otherLabel.lowercased() || value == MobileContactMethod.emailLabel.lowercased() { return CNLabelOther }
        return label
    }

    private func postalValues(_ contact: MobileContact) -> [CNLabeledValue<CNPostalAddress>] {
        if contact.address.isEmpty && contact.city.isEmpty && contact.country.isEmpty { return [] }
        let address = CNMutablePostalAddress()
        address.street = contact.address
        address.city = contact.city
        address.state = contact.district
        address.postalCode = contact.postalCode
        address.country = contact.country
        return [CNLabeledValue(label: CNLabelHome, value: address)]
    }

    private func formatBirthday(_ components: DateComponents?) -> String {
        guard let components, let month = components.month, let day = components.day else { return "" }
        if let year = components.year { return String(format: "%04d-%02d-%02d", year, month, day) }
        return String(format: "--%02d-%02d", month, day)
    }

    private func parseBirthday(_ value: String) -> DateComponents? {
        if value.hasPrefix("--") {
            let parts = value.dropFirst(2).split(separator: "-").compactMap { Int($0) }
            guard parts.count == 2 else { return nil }
            return DateComponents(month: parts[0], day: parts[1])
        }
        let parts = value.split(separator: "-").compactMap { Int($0) }
        guard parts.count == 3 else { return nil }
        return DateComponents(year: parts[0], month: parts[1], day: parts[2])
    }

    private func primaryValue(_ values: [MobileContactMethod]) -> String { values.first(where: { $0.primary })?.value ?? values.first?.value ?? "" }
    private func valueAt(_ values: [MobileContactMethod], _ index: Int) -> String { index < values.count ? values[index].value : "" }
    private func firstLabel(_ values: [MobileContactMethod], _ label: String) -> String { values.first(where: { $0.label.caseInsensitiveCompare(label) == .orderedSame })?.value ?? "" }
    private func normalizePhone(_ value: String) -> String { value.filter { $0.isNumber || $0 == "+" } }
}

enum ContactRepositoryError: LocalizedError {
    case permissionDenied
    case createdContactNotFound

    var errorDescription: String? {
        switch self {
        case .permissionDenied: return Messages.contactsPermissionRequired
        case .createdContactNotFound: return Messages.createdContactNotFound
        }
    }
}
