#!/usr/bin/env bash
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/packaging/linux/Kurulum.sh
# 📌 Amac: Turkuaz Telefon Rehberi Linux portable paketini kullanici hesabina kurar.
# 📌 Tool - Shell
# Version: 1.0.0
# Aciklama: Uygulamayi ~/.local/opt altina, desktop entry ve ikonu XDG kullanici alanina kurar.
# Bagimli Oldugu Katman: Tool | View | Config

set -euo pipefail

APP_ID="turkuaz-telefon-rehberi"
APP_DIR_NAME="TurkuazTelefonRehberi"
SOURCE_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
INSTALL_ROOT="${HOME}/.local/opt/${APP_ID}"
APPLICATIONS_DIR="${XDG_DATA_HOME:-${HOME}/.local/share}/applications"
ICON_DIR="${XDG_DATA_HOME:-${HOME}/.local/share}/icons/hicolor/256x256/apps"
DESKTOP_FILE="${APPLICATIONS_DIR}/${APP_ID}.desktop"
ICON_FILE="${ICON_DIR}/${APP_ID}.png"

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

printf '%s\n' "Turkuaz Telefon Rehberi kuruldu."
printf '%s\n' "Uygulama: ${INSTALL_ROOT}/TelefonRehberi"
printf '%s\n' "Masaustu girdisi: ${DESKTOP_FILE}"
