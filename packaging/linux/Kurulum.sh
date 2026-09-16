#!/usr/bin/env bash
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/packaging/linux/Kurulum.sh
# 📌 Amac: Turkuaz PhoneBook Linux portable paketini kullanici hesabina kurar.
# 📌 Tool - Shell
# Version: 1.1.0
# Aciklama: Uygulamayi ~/.local/opt altina kurar; cikti dilini Turkce locale icin Turkce, diger diller icin Ingilizce verir.
# Bagimli Oldugu Katman: Tool | View | Config | Language

set -euo pipefail

APP_ID="turkuaz-telefon-rehberi"
APP_DIR_NAME="TurkuazTelefonRehberi"
SOURCE_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
INSTALL_ROOT="${HOME}/.local/opt/${APP_ID}"
APPLICATIONS_DIR="${XDG_DATA_HOME:-${HOME}/.local/share}/applications"
ICON_DIR="${XDG_DATA_HOME:-${HOME}/.local/share}/icons/hicolor/256x256/apps"
DESKTOP_FILE="${APPLICATIONS_DIR}/${APP_ID}.desktop"
ICON_FILE="${ICON_DIR}/${APP_ID}.png"
LOCALE_VALUE="${LC_ALL:-${LC_MESSAGES:-${LANG:-}}}"

if [[ "${LOCALE_VALUE,,}" == tr* ]]; then
    MSG_INSTALLED="Turkuaz Telefon Rehberi kuruldu."
    MSG_APP="Uygulama"
    MSG_DESKTOP="Masaustu girdisi"
else
    MSG_INSTALLED="Turkuaz PhoneBook installed."
    MSG_APP="Application"
    MSG_DESKTOP="Desktop entry"
fi

mkdir -p "${INSTALL_ROOT}" "${APPLICATIONS_DIR}" "${ICON_DIR}"

if [[ "${SOURCE_DIR}" != "${INSTALL_ROOT}" ]]; then
    rm -rf "${INSTALL_ROOT:?}/"*
    cp -a "${SOURCE_DIR}/." "${INSTALL_ROOT}/"
fi

chmod +x "${INSTALL_ROOT}/TelefonRehberi" "${INSTALL_ROOT}/Kurulum.sh" "${INSTALL_ROOT}/Kaldir.sh"
cp -f "${INSTALL_ROOT}/assets/branding/app-icon-256.png" "${ICON_FILE}"

sed "s|@APP_EXEC@|${INSTALL_ROOT}/TelefonRehberi|g" \
    "${INSTALL_ROOT}/packaging/linux/turkuaz-telefon-rehberi.desktop.in" > "${DESKTOP_FILE}"
chmod 0644 "${DESKTOP_FILE}" "${ICON_FILE}"

if command -v update-desktop-database >/dev/null 2>&1; then
    update-desktop-database "${APPLICATIONS_DIR}" >/dev/null 2>&1 || true
fi
if command -v gtk-update-icon-cache >/dev/null 2>&1; then
    gtk-update-icon-cache -f -t "${XDG_DATA_HOME:-${HOME}/.local/share}/icons/hicolor" >/dev/null 2>&1 || true
fi

printf '%s\n' "${MSG_INSTALLED}"
printf '%s\n' "${MSG_APP}: ${INSTALL_ROOT}/TelefonRehberi"
printf '%s\n' "${MSG_DESKTOP}: ${DESKTOP_FILE}"
