#!/usr/bin/env bash
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/sync-published-manifest.sh
# 📌 Amac: Yayinlanan update manifestini surum ve branch yarisi kontrolleriyle GitHub main branch'e senkronize eder.
# 📌 Tool - Bash
# Version: 1.0.0
# Aciklama: Release manifestini dogrular, daha yeni main surumunu ezmez ve GitHub Contents API ile atomik metadata commit'i olusturur.
# Bagimli Oldugu Katman: Tool | Config

set -euo pipefail

EXPECTED_TAG="${1:?Expected tag gerekli}"
SOURCE_MANIFEST="${2:?Source manifest gerekli}"
TARGET_MANIFEST="${3:?Target manifest gerekli}"
TARGET_BRANCH="${4:?Target branch gerekli}"

: "${GH_TOKEN:?GH_TOKEN gerekli}"
: "${GITHUB_REPOSITORY:?GITHUB_REPOSITORY gerekli}"

RELEASE_VERSION="${EXPECTED_TAG#v}"
VERSION_CONFIG_PATH="config/version.yml"
APP_VERSION_KEY="app_version"

read_yaml_value() {
    local key="$1"
    local file="$2"
    awk -F ':' -v key="$key" '
        $1 ~ "^[[:space:]]*" key "[[:space:]]*$" {
            gsub(/[ "]/, "", $2)
            print $2
            exit
        }
    ' "$file"
}

read_remote_file() {
    local path="$1"
    gh api "repos/${GITHUB_REPOSITORY}/contents/${path}?ref=${TARGET_BRANCH}" --jq '.content' |
        tr -d '\n' |
        base64 --decode
}

if [[ ! -f "$SOURCE_MANIFEST" ]]; then
    echo "[ERROR] Release manifest bulunamadi: $SOURCE_MANIFEST" >&2
    exit 1
fi

SOURCE_VERSION="$(read_yaml_value "$APP_VERSION_KEY" "$SOURCE_MANIFEST")"
if [[ "$SOURCE_VERSION" != "$RELEASE_VERSION" ]]; then
    echo "[ERROR] Release manifest surumu tag ile uyusmuyor: manifest=$SOURCE_VERSION tag=$RELEASE_VERSION" >&2
    exit 1
fi

MAIN_VERSION_FILE="$(mktemp)"
CURRENT_MANIFEST_FILE="$(mktemp)"
trap 'rm -f "$MAIN_VERSION_FILE" "$CURRENT_MANIFEST_FILE"' EXIT

read_remote_file "$VERSION_CONFIG_PATH" > "$MAIN_VERSION_FILE"
MAIN_VERSION="$(read_yaml_value "$APP_VERSION_KEY" "$MAIN_VERSION_FILE")"

if [[ "$MAIN_VERSION" != "$RELEASE_VERSION" ]]; then
    echo "[SKIP] main app_version=$MAIN_VERSION, release=$RELEASE_VERSION. Eski release metadata main'e yazilmadi."
    exit 0
fi

read_remote_file "$TARGET_MANIFEST" > "$CURRENT_MANIFEST_FILE"
if cmp -s "$SOURCE_MANIFEST" "$CURRENT_MANIFEST_FILE"; then
    echo "[OK] main update manifest zaten $RELEASE_VERSION ile ayni."
    exit 0
fi

TARGET_SHA="$(gh api "repos/${GITHUB_REPOSITORY}/contents/${TARGET_MANIFEST}?ref=${TARGET_BRANCH}" --jq '.sha')"
ENCODED_CONTENT="$(base64 < "$SOURCE_MANIFEST" | tr -d '\n')"
COMMIT_MESSAGE="chore(release): finalize v${RELEASE_VERSION} published metadata"

gh api     --method PUT     "repos/${GITHUB_REPOSITORY}/contents/${TARGET_MANIFEST}"     -f message="$COMMIT_MESSAGE"     -f content="$ENCODED_CONTENT"     -f sha="$TARGET_SHA"     -f branch="$TARGET_BRANCH" >/dev/null

echo "[OK] $TARGET_MANIFEST main branch'e v$RELEASE_VERSION olarak senkronize edildi."
