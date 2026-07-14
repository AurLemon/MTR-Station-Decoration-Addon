#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TARGET_DIRS=("$ROOT_DIR/fabric/src/main/java" "$ROOT_DIR/neoforge/src/main/java")

echo "== Legacy import usage =="
rg -No 'import (org\.mtr\.mapping|org\.mtr\.mod)[^;]+;' "${TARGET_DIRS[@]}" \
	| sed 's/^import //' \
	| sed 's/;$//' \
	| sort \
	| uniq -c \
	| sort -nr \
	| head -n 160

echo
echo "== Fabric-only entrypoints =="
rg -n 'net\.fabricmc|ModInitializer|ClientModInitializer|ModMenuApi|mixin' "$ROOT_DIR/fabric/src/main/java" \
	| head -n 160
