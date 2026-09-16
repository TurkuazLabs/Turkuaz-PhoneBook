#!/usr/bin/env bash
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/test-android-endpoint.sh
# 📌 Amac: Android LAN endpoint Tool katmanini Android SDK/JUnit gerektirmeden Java 17 ile test eder.
# 📌 Tool - Shell
# Version: 1.0.0
# Aciklama: Messages, LanEndpointTool ve standalone quality-gate testini gecici klasorde derleyip calistirir.
# Bagimli Oldugu Katman: Tool | Language

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="${ROOT}/build/android-endpoint-quality-gate"
MAIN_JAVA="${ROOT}/mobile/android/app/src/main/java"
TEST_JAVA="${ROOT}/mobile/android/app/src/test/java"

rm -rf "${BUILD_DIR}"
mkdir -p "${BUILD_DIR}"

javac -encoding UTF-8 -d "${BUILD_DIR}" \
  "${MAIN_JAVA}/com/turkuazlabs/telefonrehberi/mobile/language/Messages.java" \
  "${MAIN_JAVA}/com/turkuazlabs/telefonrehberi/mobile/tools/LanEndpointTool.java" \
  "${TEST_JAVA}/com/turkuazlabs/telefonrehberi/mobile/tools/LanEndpointToolTest.java"

java -cp "${BUILD_DIR}" com.turkuazlabs.telefonrehberi.mobile.tools.LanEndpointToolTest
