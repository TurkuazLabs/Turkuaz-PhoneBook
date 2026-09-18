#!/usr/bin/env bash
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/test-java.sh
# 📌 Amac: Linux Java 17 release quality gate testlerini SQLite JDBC ile derleyip calistirir.
# 📌 Tool - Shell
# Version: 1.3.0
# Aciklama: Genel Java, POSIX sync-token ve cross-platform mobil API endpoint sozlesmesi quality gate testlerini sabitlenmis SQLite JDBC ve SLF4J bagimliliklariyla calistirir.
# Bagimli Oldugu Katman: Tool | Config | Repository | Service | Language

set -euo pipefail
ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
CONFIG="${ROOT}/config/launcher.yml"
BUILD="${ROOT}/build-tests"
CLASSES="${BUILD}/classes"
LIB="${BUILD}/lib"

read_yaml() {
    local key="$1"
    awk -F ':' -v wanted="${key}" '$1 ~ "^[[:space:]]*" wanted "[[:space:]]*$" {value=$0; sub(/^[^:]*:[[:space:]]*/, "", value); gsub(/^["\047]|["\047]$/, "", value); print value; exit}' "${CONFIG}"
}

VERSION="$(read_yaml sqlite_jdbc_version)"
URL_TEMPLATE="$(read_yaml sqlite_jdbc_url)"
EXPECTED_SHA="$(read_yaml sqlite_jdbc_sha256)"
JAR="${LIB}/sqlite-jdbc-${VERSION}.jar"
URL="${URL_TEMPLATE//\{version\}/${VERSION}}"
SLF4J_VERSION="$(read_yaml slf4j_version)"
SLF4J_URL_TEMPLATE="$(read_yaml slf4j_api_url)"
SLF4J_EXPECTED_SHA="$(read_yaml slf4j_api_sha256)"
SLF4J_URL="${SLF4J_URL_TEMPLATE//\{version\}/${SLF4J_VERSION}}"
SLF4J_JAR="${LIB}/slf4j-api-${SLF4J_VERSION}.jar"

rm -rf "${BUILD}"
mkdir -p "${CLASSES}" "${LIB}"
curl --fail --location --silent --show-error "${URL}" --output "${JAR}"
ACTUAL_SHA="$(sha256sum "${JAR}" | awk '{print $1}')"
[[ "${ACTUAL_SHA}" == "${EXPECTED_SHA}" ]] || { echo "SQLite JDBC SHA-256 uyusmuyor." >&2; exit 1; }
curl --fail --location --silent --show-error "${SLF4J_URL}" --output "${SLF4J_JAR}"
SLF4J_ACTUAL_SHA="$(sha256sum "${SLF4J_JAR}" | awk '{print $1}')"
[[ "${SLF4J_ACTUAL_SHA}" == "${SLF4J_EXPECTED_SHA}" ]] || { echo "SLF4J API SHA-256 uyusmuyor." >&2; exit 1; }

mapfile -t SOURCES < <(find "${ROOT}/src/main/java" "${ROOT}/src/test/java" -name '*.java' -type f | sort)
javac --release 17 --add-modules jdk.httpserver -encoding UTF-8 -d "${CLASSES}" "${SOURCES[@]}"
CLASS_PATH="${CLASSES}:${JAR}:${SLF4J_JAR}"
java --add-modules jdk.httpserver -cp "${CLASS_PATH}" com.turkuazlabs.telefonrehberi.QualityGateTest
java --add-modules jdk.httpserver -cp "${CLASS_PATH}" com.turkuazlabs.telefonrehberi.SyncTokenStoreQualityGateTest
java --add-modules jdk.httpserver -cp "${CLASS_PATH}" com.turkuazlabs.telefonrehberi.ApiContractQualityGateTest "${ROOT}"
