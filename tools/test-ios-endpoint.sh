#!/usr/bin/env bash
# 📄 Dosya Yolu: C:/Projects/TelefonRehberi/tools/test-ios-endpoint.sh
# 📌 Amac: iOS LAN endpoint Tool katmanini macOS Swift compiler ile bagimsiz regression gate olarak calistirir.
# 📌 Tool - Shell
# Version: 1.0.0
# Aciklama: LanEndpointTool.swift ve kalite gate girisini gecici executable olarak derler ve public cleartext HTTP engelini dogrular.
# Bagimli Oldugu Katman: Tool

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "${TMP_DIR}"' EXIT

swiftc \
  "${ROOT_DIR}/mobile/ios/TurkuazTelefonRehberiIOS/tools/LanEndpointTool.swift" \
  "${ROOT_DIR}/mobile/ios/TurkuazTelefonRehberiIOS/quality/LanEndpointToolQualityGateMain.swift" \
  -o "${TMP_DIR}/ios-lan-endpoint-gate"

"${TMP_DIR}/ios-lan-endpoint-gate"
