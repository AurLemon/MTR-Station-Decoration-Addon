#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SERVER_DIR="${SERVER_DIR:-$ROOT_DIR/server-smoke-custom-text}"
TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-300}"
READY_TIMEOUT_SECONDS="${READY_TIMEOUT_SECONDS:-180}"
JAVA_HOME="${JAVA21_HOME:-$HOME/.gradle/jdks/eclipse_adoptium-21-amd64-linux.2}"
JAVA_BIN="$JAVA_HOME/bin/java"
SERVER_JAR_SOURCE="${SERVER_JAR_SOURCE:-/home/aurlemon/code/Joban-Client-Mod/youer-1.21.1-cb6ddeab-server.jar}"
SERVER_JAR_NAME="server.jar"
LOG_FILE="${LOG_FILE:-$SERVER_DIR/logs/latest.log}"
FIFO_PATH="$SERVER_DIR/.server-stdin"
MOD_JAR="${MOD_JAR:-$(find "$ROOT_DIR/neoforge/build/libs" -maxdepth 1 -type f -name 'neoforge-*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' | head -n 1)}"
MTR_JAR_SOURCE="${MTR_JAR_SOURCE:-$HOME/.gradle/caches/modules-2/files-2.1/maven.modrinth/minecraft-transit-railway/NEOFORGE-4.1.0-beta.2+1.21.1/3c25af1f04b5a9178bafa505ffc2fd265525e60d/minecraft-transit-railway-NEOFORGE-4.1.0-beta.2+1.21.1.jar}"
UNIVERSE_DIR="${UNIVERSE_DIR:-$SERVER_DIR/worlds/run-$$}"
LEVEL_NAME="${LEVEL_NAME:-run-$$}"
SERVER_PORT="${SERVER_PORT:-$((32000 + (BASHPID % 2000)))}"

cleanup() {
  if [[ -n "${SERVER_PID:-}" ]] && kill -0 "$SERVER_PID" >/dev/null 2>&1; then
    printf 'stop\n' >&${FIFO_FD} || true
    wait "$SERVER_PID" || true
  fi
  if [[ -n "${FIFO_FD:-}" ]]; then
    exec {FIFO_FD}>&- || true
  fi
  rm -f "$FIFO_PATH"
}

trap cleanup EXIT INT TERM

require_file() {
  local path="$1"
  if [[ ! -f "$path" ]]; then
    echo "Missing required file: $path" >&2
    exit 1
  fi
}

wait_for_log() {
  local pattern="$1"
  local deadline=$((SECONDS + READY_TIMEOUT_SECONDS))
  while (( SECONDS < deadline )); do
    if [[ -f "$LOG_FILE" ]] && rg -q -- "$pattern" "$LOG_FILE"; then
      return 0
    fi
    sleep 1
  done
  echo "Timed out waiting for log pattern: $pattern" >&2
  tail -n 120 "$LOG_FILE" >&2 || true
  return 1
}

wait_for_server_ready() {
  local done_deadline=$((SECONDS + READY_TIMEOUT_SECONDS))
  while (( SECONDS < done_deadline )); do
    if [[ -f "$LOG_FILE" ]] && rg -q 'Done \([0-9.]+s\)! For help, type "help"' "$LOG_FILE"; then
      break
    fi
    sleep 1
  done

  if ! [[ -f "$LOG_FILE" ]] || ! rg -q 'Done \([0-9.]+s\)! For help, type "help"' "$LOG_FILE"; then
    echo "Timed out waiting for server startup completion" >&2
    tail -n 120 "$LOG_FILE" >&2 || true
    exit 1
  fi

  local ready_deadline=$((SECONDS + 15))
  while (( SECONDS < ready_deadline )); do
    if rg -q "Starting Minecraft server on \\*:${SERVER_PORT}" "$LOG_FILE" && ! rg -q 'FAILED TO BIND TO PORT' "$LOG_FILE"; then
      return 0
    fi
    sleep 1
  done

  echo "Server did not bind cleanly to port ${SERVER_PORT}" >&2
  tail -n 120 "$LOG_FILE" >&2 || true
  exit 1
}

send_command() {
  printf '%s\n' "$1" >&${FIFO_FD}
}

require_file "$JAVA_BIN"
require_file "$SERVER_JAR_SOURCE"
require_file "$MTR_JAR_SOURCE"
require_file "$MOD_JAR"

mkdir -p "$SERVER_DIR/mods" "$UNIVERSE_DIR" "$(dirname "$LOG_FILE")"
cp "$SERVER_JAR_SOURCE" "$SERVER_DIR/$SERVER_JAR_NAME"
cp "$MTR_JAR_SOURCE" "$SERVER_DIR/mods/$(basename "$MTR_JAR_SOURCE")"
cp "$MOD_JAR" "$SERVER_DIR/mods/$(basename "$MOD_JAR")"
printf 'eula=true\n' >"$SERVER_DIR/eula.txt"
cat >"$SERVER_DIR/server.properties" <<EOF
server-port=${SERVER_PORT}
online-mode=false
enable-rcon=false
enable-query=false
motd=MSD Custom Text Probe
level-name=${LEVEL_NAME}
EOF
rm -f "$LOG_FILE" "$FIFO_PATH"
mkfifo "$FIFO_PATH"
exec {FIFO_FD}<>"$FIFO_PATH"

cd "$SERVER_DIR"
env JAVA_HOME="$JAVA_HOME" PATH="$JAVA_HOME/bin:$PATH" \
  timeout --signal=TERM --kill-after=20s "${TIMEOUT_SECONDS}s" \
  "$JAVA_BIN" -jar "$SERVER_JAR_NAME" nogui --universe "$UNIVERSE_DIR" <"$FIFO_PATH" >/dev/null 2>&1 &
SERVER_PID=$!

wait_for_server_ready
wait_for_log 'MSD server started with dimensions'

send_command 'forceload add 80 0 96 16'
wait_for_log 'Marked [0-9]+ chunk[s]? in Overworld from|No chunks were marked for force loading'
send_command 'fill 80 63 -2 96 66 2 air'
wait_for_log 'Successfully filled [0-9]+ block\(s\)|No blocks were filled'
send_command 'fill 80 63 -2 96 63 2 stone'
wait_for_log 'Successfully filled [0-9]+ block\(s\)'

send_command 'setblock 82 64 0 msd:yuuni_standing_sign[facing=north]'
wait_for_log 'Changed the block at 82, 64, 0'
send_command 'setblock 84 64 0 msd:yuuni_standing_sign_1[facing=north]'
wait_for_log 'Changed the block at 84, 64, 0'

send_command 'msd_probe_custom_text 82 64 0 "Rapid;;1|Bound for West End;;Mind the gap"'
wait_for_log 'MSD custom text updated'
send_command 'data get block 82 64 0'
wait_for_log '82, 64, 0 has the following block data:'
wait_for_log 'msd_custom_message0'
wait_for_log 'msd_custom_message1'
wait_for_log 'msd_custom_message2'
send_command 'say probe-custom-text-sign-3-ok'
wait_for_log 'probe-custom-text-sign-3-ok'

send_command 'msd_probe_custom_text 84 64 0 "Platform 2"'
wait_for_log 'MSD custom text updated'
send_command 'data get block 84 64 0'
wait_for_log '84, 64, 0 has the following block data:'
wait_for_log 'msd_custom_message0'
send_command 'say probe-custom-text-sign-1-ok'
wait_for_log 'probe-custom-text-sign-1-ok'

send_command 'save-all flush'
wait_for_log 'Saved the game'
send_command 'stop'
wait "$SERVER_PID"
SERVER_PID=""

echo "Custom text probe passed. Log: $LOG_FILE"
