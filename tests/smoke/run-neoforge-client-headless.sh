#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-600}"
READY_TIMEOUT_SECONDS="${READY_TIMEOUT_SECONDS:-360}"
LOG_FILE="${LOG_FILE:-$ROOT_DIR/neoforge/run/logs/latest.log}"
FALLBACK_LOG_FILE="${FALLBACK_LOG_FILE:-$ROOT_DIR/neoforge/run/logs/debug.log}"
GRADLE_ARGS=("$@")

kill_tree() {
  local pid="$1"
  local children=()
  mapfile -t children < <(pgrep -P "$pid" || true)
  for child in "${children[@]}"; do
    kill_tree "$child"
  done
  kill "$pid" >/dev/null 2>&1 || true
}

cleanup() {
  if [[ -n "${CLIENT_WRAPPER_PID:-}" ]]; then
    kill_tree "$CLIENT_WRAPPER_PID"
  fi
  pkill -f 'java .*forgeclientdev' >/dev/null 2>&1 || true
  pkill -f 'gradlew .*runClient' >/dev/null 2>&1 || true
}

trap cleanup EXIT INT TERM

wait_for_log() {
  local pattern="$1"
  local deadline=$((SECONDS + READY_TIMEOUT_SECONDS))
  while (( SECONDS < deadline )); do
    if [[ -f "$LOG_FILE" ]] && rg -q "$pattern" "$LOG_FILE"; then
      return 0
    fi
    if [[ -f "$FALLBACK_LOG_FILE" ]] && rg -q "$pattern" "$FALLBACK_LOG_FILE"; then
      return 0
    fi
    sleep 1
  done
  echo "Timed out waiting for client log pattern: $pattern" >&2
  if [[ -f "$LOG_FILE" ]]; then
    tail -n 80 "$LOG_FILE" >&2 || true
  fi
  if [[ -f "$FALLBACK_LOG_FILE" ]]; then
    tail -n 80 "$FALLBACK_LOG_FILE" >&2 || true
  fi
  return 1
}

cd "$ROOT_DIR"
cleanup
rm -f "$LOG_FILE"
rm -f "$FALLBACK_LOG_FILE"

export SDL_AUDIODRIVER="${SDL_AUDIODRIVER:-dummy}"
export ALSOFT_DRIVERS="${ALSOFT_DRIVERS:-null}"

xvfb-run -a timeout --signal=TERM --kill-after=20s "${TIMEOUT_SECONDS}s" \
  bash ./gradlew :neoforge:runClient -Pminecraft_version=1.21.1 --no-daemon "${GRADLE_ARGS[@]}" &
CLIENT_WRAPPER_PID=$!

wait_for_log 'MTR Station Decoration Addon NeoForge client initialized'
wait_for_log 'Reloading ResourceManager:'
wait_for_log 'mod/msd'

cleanup
wait "$CLIENT_WRAPPER_PID" || true
echo "NeoForge headless client smoke passed. Log: ${LOG_FILE:-$FALLBACK_LOG_FILE}"
