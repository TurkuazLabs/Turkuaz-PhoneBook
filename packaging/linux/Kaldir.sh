#!/usr/bin/env bash
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/packaging/linux/Kaldir.sh
# 📌 Amac: Linux kullanici kurulumunu kaldirirken rehber verilerini varsayilan olarak korur.
# 📌 Tool - Shell
# Version: 1.0.0
# Aciklama: Uygulama, desktop entry ve ikonu siler; --remove-data verilirse yalniz teknik XDG verilerini de temizler.
# Bagimli Oldugu Katman: Tool | Config

set -euo pipefail

APP_ID="turkuaz-telefon-rehberi"
INSTALL_ROOT="${HOME}/.local/opt/${APP_ID}"
DATA_ROOT="${XDG_DATA_HOME:-${HOME}/.local/share}/turkuazlabs/telefon-rehberi"
CONFIG_ROOT="${XDG_CONFIG_HOME:-${HOME}/.config}/turkuazlabs/telefon-rehberi"
CACHE_ROOT="${XDG_CACHE_HOME:-${HOME}/.cache}/turkuazlabs/telefon-rehberi"
APPLICATIONS_DIR="${XDG_DATA_HOME:-${HOME}/.local/share}/applications"
ICON_DIR="${XDG_DATA_HOME:-${HOME}/.local/share}/icons/hicolor/256x256/apps"

rm -f "${APPLICATIONS_DIR}/${APP_ID}.desktop"
rm -f "${ICON_DIR}/${APP_ID}.png"
rm -rf "${INSTALL_ROOT}"

if [[ "${1:-}" == "--remove-data" ]]; then
    rm -rf "${DATA_ROOT}" "${CONFIG_ROOT}" "${CACHE_ROOT}"
fi

printf '%s\n' "Turkuaz Telefon Rehberi kaldirildi."
printf '%s\n' "Contacts/Documents altindaki export ve yedekler korunmustur."
