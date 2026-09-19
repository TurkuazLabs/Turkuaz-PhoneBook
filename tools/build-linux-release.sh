#!/usr/bin/env bash
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/build-linux-release.sh
# 📌 Amac: Java 17 JAR, native Linux launcher ve Linux portable TAR.GZ paketini uretir.
# 📌 Tool - Shell
# Version: 1.3.0
# Aciklama: Merkezi version.yml surumune gore Linux amd64 native launcher, portable paket ve SHA-256 dosyalarini olusturur.
# Bagimli Oldugu Katman: Tool | Config

set -euo pipefail

ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD="${ROOT}/build-linux"
DIST="${ROOT}/dist-linux"
CLASSES="${BUILD}/classes"
PACKAGE_ROOT="${BUILD}/package"
VERSION_FILE="${ROOT}/config/version.yml"

read_yaml_value() {
    local key="$1"
    awk -F ':' -v wanted="${key}" '$1 ~ "^[[:space:]]*" wanted "[[:space:]]*$" {value=$0; sub(/^[^:]*:[[:space:]]*/, "", value); gsub(/^[\"\047]|[\"\047]$/, "", value); print value; exit}' "${VERSION_FILE}"
}

APP_VERSION="$(read_yaml_value app_version)"
LAUNCHER_VERSION="$(read_yaml_value launcher_version)"
EXPECTED_TAG="${1:-}"
if [[ -n "${EXPECTED_TAG}" ]]; then
    EXPECTED_TAG="${EXPECTED_TAG#v}"
    [[ "${EXPECTED_TAG}" == "${APP_VERSION}" ]] || { echo "Tag ve app_version uyusmuyor." >&2; exit 1; }
fi

rm -rf "${BUILD}" "${DIST}"
mkdir -p "${CLASSES}" "${DIST}" "${PACKAGE_ROOT}"

mapfile -t JAVA_SOURCES < <(find "${ROOT}/src/main/java" -name '*.java' -type f | sort)
javac --release 17 --add-modules jdk.httpserver -encoding UTF-8 -d "${CLASSES}" "${JAVA_SOURCES[@]}"
cp -a "${ROOT}/assets" "${CLASSES}/assets"
printf '%s\n' 'Manifest-Version: 1.0' 'Main-Class: com.turkuazlabs.telefonrehberi.Main' '' > "${BUILD}/MANIFEST.MF"
jar --create --file "${DIST}/TelefonRehberi.jar" --manifest "${BUILD}/MANIFEST.MF" -C "${CLASSES}" .

pushd "${ROOT}/launcher/native" >/dev/null
go test ./...
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -trimpath -ldflags='-s -w' -o "${DIST}/TelefonRehberi" ./cmd/telefonrehberi
popd >/dev/null
chmod +x "${DIST}/TelefonRehberi"

APP_SHA="$(sha256sum "${DIST}/TelefonRehberi.jar" | awk '{print $1}')"
LAUNCHER_SHA="$(sha256sum "${DIST}/TelefonRehberi" | awk '{print $1}')"

cat > "${DIST}/update-manifest.yml" <<MANIFEST
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/updates/update-manifest.yml
# 📌 Amac: Linux release update metadata ve SHA-256 degerlerini tanimlar.
# 📌 Modul - YAML
# Version: 2.4.0
# Aciklama: v${APP_VERSION} Linux native launcher release metadata dosyasidir.
# Bagimli Oldugu Katman: Tool

app_version: "${APP_VERSION}"
app_asset: "TelefonRehberi.jar"
app_sha256: "${APP_SHA}"
launcher_version: "${LAUNCHER_VERSION}"
launcher_linux_asset: "TelefonRehberi-linux-amd64"
launcher_linux_sha256: "${LAUNCHER_SHA}"
MANIFEST

PACKAGE_NAME="TelefonRehberi-Linux-v${APP_VERSION}"
PACKAGE="${PACKAGE_ROOT}/${PACKAGE_NAME}"
mkdir -p "${PACKAGE}/"{app,assets,cache,config,lib,logs,runtime,updates,updates/app-backups,packaging/linux}
cp "${DIST}/TelefonRehberi" "${PACKAGE}/TelefonRehberi"
cp "${DIST}/TelefonRehberi.jar" "${PACKAGE}/app/TelefonRehberi.jar"
cp -a "${ROOT}/assets/." "${PACKAGE}/assets/"
cp "${ROOT}/config/launcher.yml" "${PACKAGE}/config/launcher.yml"
cp "${ROOT}/config/app.yml" "${PACKAGE}/config/app.yml"
cp "${ROOT}/config/version.yml" "${PACKAGE}/config/version.yml"
cp "${DIST}/update-manifest.yml" "${PACKAGE}/updates/update-manifest.yml"
cp "${ROOT}/packaging/linux/Kurulum.sh" "${PACKAGE}/Kurulum.sh"
cp "${ROOT}/packaging/linux/Kaldir.sh" "${PACKAGE}/Kaldir.sh"
cp -a "${ROOT}/packaging/linux/." "${PACKAGE}/packaging/linux/"
cp "${ROOT}/README.md" "${PACKAGE}/README.md"
for DOC in LICENSE SECURITY.md THIRD_PARTY_NOTICES.md; do
    [[ -f "${ROOT}/${DOC}" ]] && cp "${ROOT}/${DOC}" "${PACKAGE}/${DOC}"
done
chmod +x "${PACKAGE}/TelefonRehberi" "${PACKAGE}/Kurulum.sh" "${PACKAGE}/Kaldir.sh" "${PACKAGE}/packaging/linux/"*.sh

cat > "${PACKAGE}/config/state.yml" <<STATE
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/config/state.yml
# 📌 Amac: Kurulu uygulama ve native launcher surum durumunu tutar.
# 📌 Modul - YAML
# Version: 2.4.0
# Aciklama: Linux native launcher yerel surum bilgisidir.
# Bagimli Oldugu Katman: Repository

app_version: "${APP_VERSION}"
launcher_version: "${LAUNCHER_VERSION}"
STATE

cat > "${PACKAGE}/CHECKSUMS.txt" <<CHECKSUMS
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/CHECKSUMS.txt
# 📌 Amac: Linux portable paketteki kritik release dosyalarinin SHA-256 degerlerini listeler.
# 📌 Modul - Text
# Version: 1.1.0
# Aciklama: Native Linux launcher ve JAR butunluk kontroludur.
# Bagimli Oldugu Katman: Tool

${LAUNCHER_SHA}  TelefonRehberi
${APP_SHA}  app/TelefonRehberi.jar
CHECKSUMS

TARBALL="${DIST}/${PACKAGE_NAME}-FULL.tar.gz"
tar -C "${PACKAGE_ROOT}" -czf "${TARBALL}" "${PACKAGE_NAME}"
TAR_SHA="$(sha256sum "${TARBALL}" | awk '{print $1}')"
cat > "${DIST}/CHECKSUMS-LINUX.txt" <<CHECKSUMS
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/dist-linux/CHECKSUMS-LINUX.txt
# 📌 Amac: Linux GitHub Release asset SHA-256 degerlerini listeler.
# 📌 Modul - Text
# Version: 1.1.0
# Aciklama: Linux launcher ve portable TAR.GZ butunluk kontroludur.
# Bagimli Oldugu Katman: Tool

${LAUNCHER_SHA}  TelefonRehberi-linux-amd64
${TAR_SHA}  ${PACKAGE_NAME}-FULL.tar.gz
CHECKSUMS

cp "${DIST}/TelefonRehberi" "${DIST}/TelefonRehberi-linux-amd64"
printf '%s\n' "Linux release build tamamlandi. App=${APP_VERSION} Launcher=${LAUNCHER_VERSION}"
ls -lh "${DIST}"
