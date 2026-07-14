#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SERVER_DIR="${SERVER_DIR:-$ROOT_DIR/server-smoke}"
TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-360}"
READY_TIMEOUT_SECONDS="${READY_TIMEOUT_SECONDS:-90}"
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
DEFAULT_SERVER_PORT="$((25588 + (BASHPID % 1000)))"
SERVER_PORT="${SERVER_PORT:-}"
ACTIVE_WORLD_DIR=""

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

stop_stale_server() {
	local jar_path="$SERVER_DIR/$SERVER_JAR_NAME"
	pkill -f "$jar_path" >/dev/null 2>&1 || true
}

find_open_port() {
	local start_port="$1"
	local end_port=$((start_port + 200))
	local candidate
	for ((candidate = start_port; candidate <= end_port; candidate++)); do
		if ! ss -ltnH "( sport = :$candidate )" | grep -q .; then
			echo "$candidate"
			return 0
		fi
	done
	echo "Failed to find an open port in range ${start_port}-${end_port}" >&2
	exit 1
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
  if [[ -f "$LOG_FILE" ]]; then
    tail -n 80 "$LOG_FILE" >&2 || true
  fi
  return 1
}

send_command() {
	printf '%s\n' "$1" >&${FIFO_FD}
}

start_server() {
	rm -f "$LOG_FILE" "$FIFO_PATH"
	mkfifo "$FIFO_PATH"
	exec {FIFO_FD}<>"$FIFO_PATH"

	cd "$SERVER_DIR"
	env JAVA_HOME="$JAVA_HOME" PATH="$JAVA_HOME/bin:$PATH" \
		timeout --signal=TERM --kill-after=20s "${TIMEOUT_SECONDS}s" \
		"$JAVA_BIN" -jar "$SERVER_JAR_NAME" nogui --universe "$UNIVERSE_DIR" <"$FIFO_PATH" >/dev/null 2>&1 &
	SERVER_PID=$!

	wait_for_log 'Done \([0-9.]+s\)! For help, type "help"'
	wait_for_log "Starting Minecraft server on \\*:${SERVER_PORT}"
	if rg -q 'FAILED TO BIND TO PORT' "$LOG_FILE"; then
		echo "Server failed to bind to port ${SERVER_PORT}" >&2
		tail -n 120 "$LOG_FILE" >&2 || true
		exit 1
	fi
}

stop_server() {
	send_command 'stop'
	wait "$SERVER_PID"
	SERVER_PID=""
	exec {FIFO_FD}>&- || true
	unset FIFO_FD
	rm -f "$FIFO_PATH"
}

require_file "$JAVA_BIN"
require_file "$SERVER_JAR_SOURCE"
require_file "$MTR_JAR_SOURCE"
require_file "$MOD_JAR"

stop_stale_server
SERVER_PORT="${SERVER_PORT:-$(find_open_port "$DEFAULT_SERVER_PORT")}"

mkdir -p "$SERVER_DIR/mods"
mkdir -p "$UNIVERSE_DIR"
cp "$SERVER_JAR_SOURCE" "$SERVER_DIR/$SERVER_JAR_NAME"
cp "$MTR_JAR_SOURCE" "$SERVER_DIR/mods/$(basename "$MTR_JAR_SOURCE")"
cp "$MOD_JAR" "$SERVER_DIR/mods/$(basename "$MOD_JAR")"
printf 'eula=true\n' >"$SERVER_DIR/eula.txt"
cat >"$SERVER_DIR/server.properties" <<EOF
server-port=${SERVER_PORT}
online-mode=false
enable-rcon=false
enable-query=false
motd=MSD Smoke
level-name=${LEVEL_NAME}
EOF
rm -f "$LOG_FILE" "$FIFO_PATH"
start_server
ACTIVE_WORLD_DIR="$SERVER_DIR/$LEVEL_NAME"
if [[ ! -d "$ACTIVE_WORLD_DIR" ]]; then
	ACTIVE_WORLD_DIR="$UNIVERSE_DIR/$LEVEL_NAME"
fi
if [[ ! -d "$ACTIVE_WORLD_DIR" ]]; then
	ACTIVE_WORLD_DIR="$UNIVERSE_DIR"
fi

send_command 'forceload add 0 -16 110 16'
wait_for_log 'Marked [0-9]+ chunk[s]? in Overworld from|No chunks were marked for force loading'

send_command 'fill 0 63 -5 110 65 5 air'
wait_for_log 'Successfully filled [0-9]+ block\(s\)|No blocks were filled'

send_command 'fill 0 63 -5 110 63 5 stone'
wait_for_log 'Successfully filled [0-9]+ block\(s\)'

send_command 'setblock 0 64 0 msd:yuuni_pids'
wait_for_log 'Changed the block at 0, 64, 0'

send_command 'setblock 1 64 0 msd:display_board_horizontal'
wait_for_log 'Changed the block at 1, 64, 0'

send_command 'setblock 2 64 0 msd:new_catenary_node'
wait_for_log 'Changed the block at 2, 64, 0'

send_command 'setblock 3 64 0 msd:display_board_vertical'
wait_for_log 'Changed the block at 3, 64, 0'

send_command 'setblock 4 64 0 msd:yamanote_4_pids'
wait_for_log 'Changed the block at 4, 64, 0'

send_command 'setblock 5 64 0 msd:yamanote_5_pids'
wait_for_log 'Changed the block at 5, 64, 0'

send_command 'setblock 6 64 0 msd:yamanote_6_pids'
wait_for_log 'Changed the block at 6, 64, 0'

send_command 'setblock 7 64 0 msd:yamanote_7_pids'
wait_for_log 'Changed the block at 7, 64, 0'

send_command 'setblock 8 64 0 msd:yuuni_2_pids'
wait_for_log 'Changed the block at 8, 64, 0'

send_command 'setblock 9 64 0 msd:yuuni_pids_pole[facing=north]'
wait_for_log 'Changed the block at 9, 64, 0'

send_command 'setblock 10 64 0 msd:yuuni_standing_sign[facing=north,type=0]'
wait_for_log 'Changed the block at 10, 64, 0'

send_command 'setblock 11 64 0 msd:yuuni_standing_sign_1[facing=north,type=0]'
wait_for_log 'Changed the block at 11, 64, 0'

send_command 'setblock 12 64 0 msd:yuuni_standing_sign_pole[facing=north,type=1]'
wait_for_log 'Changed the block at 12, 64, 0'

send_command 'setblock 13 64 0 msd:decoration_floor[facing=east,type=0]'
wait_for_log 'Changed the block at 13, 64, 0'

send_command 'setblock 14 64 0 msd:decoration_ceiling[facing=south,type=0]'
wait_for_log 'Changed the block at 14, 64, 0'

send_command 'setblock 15 64 0 msd:decoration_ceiling_light[facing=west,type=0]'
wait_for_log 'Changed the block at 15, 64, 0'

send_command 'setblock 16 64 0 msd:decoration_stair[facing=north,type=1]'
wait_for_log 'Changed the block at 16, 64, 0'

send_command 'setblock 17 64 0 msd:hall_seat_middle[facing=east,type=2]'
wait_for_log 'Changed the block at 17, 64, 0'

send_command 'setblock 18 64 0 msd:hall_seat_side_mirror[facing=west,type=1]'
wait_for_log 'Changed the block at 18, 64, 0'

send_command 'setblock 19 64 0 msd:surveillance_cameras[facing=north,type=1]'
wait_for_log 'Changed the block at 19, 64, 0'

send_command 'setblock 20 64 0 msd:surveillance_cameras_wall[facing=south,type=0]'
wait_for_log 'Changed the block at 20, 64, 0'

send_command 'setblock 21 64 0 msd:decoration_book[facing=west,type=1]'
wait_for_log 'Changed the block at 21, 64, 0'

send_command 'setblock 22 64 0 msd:decoration_pc[facing=east,type=1]'
wait_for_log 'Changed the block at 22, 64, 0'

send_command 'setblock 23 64 0 msd:yuuni_ticket[facing=north,half=lower]'
wait_for_log 'Changed the block at 23, 64, 0'

send_command 'setblock 23 65 0 msd:yuuni_ticket[facing=north,half=upper]'
wait_for_log 'Changed the block at 23, 65, 0'

send_command 'setblock 24 64 0 msd:railing_stair_start[facing=north,type=1]'
wait_for_log 'Changed the block at 24, 64, 0'

send_command 'setblock 25 64 0 msd:railing_stair[facing=east,type=2]'
wait_for_log 'Changed the block at 25, 64, 0'

send_command 'setblock 26 64 0 msd:railing_stair_end[facing=south,type=3]'
wait_for_log 'Changed the block at 26, 64, 0'

send_command 'setblock 27 64 0 msd:railing_stair_corner[facing=west,type=4]'
wait_for_log 'Changed the block at 27, 64, 0'

send_command 'setblock 28 64 0 msd:railing_stair_corner_2[facing=north,type=5]'
wait_for_log 'Changed the block at 28, 64, 0'

send_command 'setblock 29 64 0 msd:railing_stair_mirror[facing=east,type=2]'
wait_for_log 'Changed the block at 29, 64, 0'

send_command 'setblock 30 64 0 msd:railing_stair_corner_mirror[facing=south,type=4]'
wait_for_log 'Changed the block at 30, 64, 0'

send_command 'setblock 31 64 0 msd:railing_stair_glass_3[facing=west,type=3]'
wait_for_log 'Changed the block at 31, 64, 0'

send_command 'setblock 32 64 0 msd:railing_stair_glass_5[facing=north,type=5]'
wait_for_log 'Changed the block at 32, 64, 0'

send_command 'setblock 33 64 0 msd:railing_stair_glass_mirror_1[facing=east,type=1]'
wait_for_log 'Changed the block at 33, 64, 0'

send_command 'setblock 34 64 0 msd:railing_stair_glass_mirror_4[facing=south,type=4]'
wait_for_log 'Changed the block at 34, 64, 0'

send_command 'setblock 35 64 0 msd:railing_stair_glass_mirror_5[facing=west,type=5]'
wait_for_log 'Changed the block at 35, 64, 0'

send_command 'setblock 36 64 0 msd:catenary_pole[facing=up]'
wait_for_log 'Changed the block at 36, 64, 0'

send_command 'setblock 37 64 0 msd:catenary_rack_1[facing=north]'
wait_for_log 'Changed the block at 37, 64, 0'

send_command 'setblock 38 64 0 msd:short_catenary_rack_side[facing=east]'
wait_for_log 'Changed the block at 38, 64, 0'

send_command 'setblock 39 64 0 msd:electric_pole_side[facing=down]'
wait_for_log 'Changed the block at 39, 64, 0'

send_command 'setblock 40 64 0 msd:electric_pole_top_side[facing=north,is_long=false]'
wait_for_log 'Changed the block at 40, 64, 0'

send_command 'setblock 41 64 0 msd:electric_pole_top_both_side[facing=east,is_long=false]'
wait_for_log 'Changed the block at 41, 64, 0'

send_command 'setblock 42 64 0 msd:catenary_node[facing=up,is_connected=false]'
wait_for_log 'Changed the block at 42, 64, 0'

send_command 'setblock 43 64 0 msd:catenary_node_style_2[facing=north,is_connected=false]'
wait_for_log 'Changed the block at 43, 64, 0'

send_command 'setblock 44 64 0 msd:short_catenary_node[facing=east,is_connected=false]'
wait_for_log 'Changed the block at 44, 64, 0'

send_command 'setblock 45 64 0 msd:short_catenary_node_style_2[facing=south,is_connected=false]'
wait_for_log 'Changed the block at 45, 64, 0'

send_command 'setblock 46 64 0 msd:electric_node[facing=west,is_connected=false]'
wait_for_log 'Changed the block at 46, 64, 0'

send_command 'setblock 47 64 0 msd:trans_catenary_node[facing=down,is_connected=false]'
wait_for_log 'Changed the block at 47, 64, 0'

send_command 'setblock 48 64 0 msd:yamanote_railway_sign_pole[facing=north,type=2]'
wait_for_log 'Changed the block at 48, 64, 0'

send_command 'setblock 49 64 0 msd:rigid_catenary_node[facing=true,is_22_5=true,is_45=false,is_connected=false]'
wait_for_log 'Changed the block at 49, 64, 0'

send_command 'setblock 49 64 2 msd:rigid_catenary_node[facing=true,is_22_5=true,is_45=false,is_connected=false]'
wait_for_log 'Changed the block at 49, 64, 2'

send_command 'setblock 50 64 0 msd:catenary_with_long[facing=north,is_connected=false]'
wait_for_log 'Changed the block at 50, 64, 0'

send_command 'setblock 51 64 0 msd:catenary_with_short[facing=east,is_connected=false]'
wait_for_log 'Changed the block at 51, 64, 0'

send_command 'setblock 52 64 0 msd:catenary_with_long_top[facing=south,is_connected=false]'
wait_for_log 'Changed the block at 52, 64, 0'

send_command 'setblock 53 64 0 msd:catenary_with_short_top[facing=west,is_connected=false]'
wait_for_log 'Changed the block at 53, 64, 0'

send_command 'setblock 54 64 0 msd:catenary_with_long_counterweight[facing=north,is_connected=false]'
wait_for_log 'Changed the block at 54, 64, 0'

send_command 'setblock 55 64 0 msd:catenary_with_long_counterweight_mirror[facing=east,is_connected=false]'
wait_for_log 'Changed the block at 55, 64, 0'

send_command 'setblock 56 64 0 msd:catenary_with_short_counterweight[facing=south,is_connected=false]'
wait_for_log 'Changed the block at 56, 64, 0'

send_command 'setblock 57 64 0 msd:catenary_with_short_counterweight_mirror[facing=west,is_connected=false]'
wait_for_log 'Changed the block at 57, 64, 0'

send_command 'setblock 58 64 0 msd:yamanote_railway_sign_2_even[facing=north]'
wait_for_log 'Changed the block at 58, 64, 0'

send_command 'setblock 61 64 0 msd:yamanote_railway_sign_2_odd[facing=east]'
wait_for_log 'Changed the block at 61, 64, 0'

send_command 'setblock 64 64 0 msd:yamanote_railway_sign_3_even[facing=south]'
wait_for_log 'Changed the block at 64, 64, 0'

send_command 'setblock 68 64 0 msd:yamanote_railway_sign_3_odd[facing=west]'
wait_for_log 'Changed the block at 68, 64, 0'

send_command 'setblock 72 64 0 msd:yamanote_railway_sign_4_even[facing=north]'
wait_for_log 'Changed the block at 72, 64, 0'

send_command 'setblock 77 64 0 msd:yamanote_railway_sign_4_odd[facing=east]'
wait_for_log 'Changed the block at 77, 64, 0'

send_command 'setblock 82 64 0 msd:yamanote_railway_sign_5_even[facing=south]'
wait_for_log 'Changed the block at 82, 64, 0'

send_command 'setblock 87 64 0 msd:yamanote_railway_sign_5_odd[facing=west]'
wait_for_log 'Changed the block at 87, 64, 0'

send_command 'setblock 90 64 0 msd:yamanote_railway_sign_6_even[facing=north]'
wait_for_log 'Changed the block at 90, 64, 0'

send_command 'setblock 95 64 0 msd:yamanote_railway_sign_6_odd[facing=east]'
wait_for_log 'Changed the block at 95, 64, 0'

send_command 'setblock 100 64 0 msd:yamanote_railway_sign_7_even[facing=south]'
wait_for_log 'Changed the block at 100, 64, 0'

send_command 'setblock 105 64 0 msd:yamanote_railway_sign_7_odd[facing=west]'
wait_for_log 'Changed the block at 105, 64, 0'

send_command 'data get block 0 64 0'
wait_for_log '0, 64, 0 has the following block data:'

send_command 'data get block 10 64 0'
wait_for_log '10, 64, 0 has the following block data:'

send_command 'execute if block 0 64 0 msd:yuuni_pids run say msd-yuuni-ok'
wait_for_log 'msd-yuuni-ok'

send_command 'execute if block 1 64 0 msd:display_board_horizontal[type=0] run say msd-display-ok'
wait_for_log 'msd-display-ok'

send_command 'execute if block 2 64 0 msd:new_catenary_node run say msd-catenary-ok'
wait_for_log 'msd-catenary-ok'

send_command 'execute if block 3 64 0 msd:display_board_vertical[type=0] run say msd-display-vertical-ok'
wait_for_log 'msd-display-vertical-ok'

send_command 'execute if block 4 64 0 msd:yamanote_4_pids run say msd-yamanote4-ok'
wait_for_log 'msd-yamanote4-ok'

send_command 'execute if block 5 64 0 msd:yamanote_5_pids run say msd-yamanote5-ok'
wait_for_log 'msd-yamanote5-ok'

send_command 'execute if block 6 64 0 msd:yamanote_6_pids run say msd-yamanote6-ok'
wait_for_log 'msd-yamanote6-ok'

send_command 'execute if block 7 64 0 msd:yamanote_7_pids run say msd-yamanote7-ok'
wait_for_log 'msd-yamanote7-ok'

send_command 'execute if block 8 64 0 msd:yuuni_2_pids run say msd-yuuni2-ok'
wait_for_log 'msd-yuuni2-ok'

send_command 'execute if block 9 64 0 msd:yuuni_pids_pole[facing=north] run say msd-yuuni-pole-ok'
wait_for_log 'msd-yuuni-pole-ok'

send_command 'execute if block 10 64 0 msd:yuuni_standing_sign[facing=north,type=0] run say msd-standing-sign-ok'
wait_for_log 'msd-standing-sign-ok'

send_command 'execute if block 11 64 0 msd:yuuni_standing_sign_1[facing=north,type=0] run say msd-standing-sign-1-ok'
wait_for_log 'msd-standing-sign-1-ok'

send_command 'data merge block 10 64 0 {msd_custom_message0:"local",msd_custom_message1:"rapid",msd_custom_message2:"platform"}'
wait_for_log 'Modified block (entity )?data of (block at )?10, 64, 0'
send_command 'data get block 10 64 0'
wait_for_log '10, 64, 0 has the following block data:'
wait_for_log 'msd_custom_message0'
wait_for_log 'local'
wait_for_log 'msd_custom_message1'
wait_for_log 'rapid'
wait_for_log 'msd_custom_message2'
wait_for_log 'platform'
send_command 'say msd-standing-sign-config-ok'
wait_for_log 'msd-standing-sign-config-ok'

send_command 'data merge block 11 64 0 {msd_custom_message0:"exit"}'
wait_for_log 'Modified block (entity )?data of (block at )?11, 64, 0'
send_command 'data get block 11 64 0'
wait_for_log '11, 64, 0 has the following block data:'
wait_for_log 'msd_custom_message0'
wait_for_log 'exit'
send_command 'say msd-standing-sign-1-config-ok'
wait_for_log 'msd-standing-sign-1-config-ok'

send_command 'execute if block 12 64 0 msd:yuuni_standing_sign_pole[facing=north,type=1] run say msd-standing-sign-pole-ok'
wait_for_log 'msd-standing-sign-pole-ok'

send_command 'execute if block 13 64 0 msd:decoration_floor[facing=east,type=0] run say msd-decoration-floor-ok'
wait_for_log 'msd-decoration-floor-ok'

send_command 'execute if block 14 64 0 msd:decoration_ceiling[facing=south,type=0] run say msd-decoration-ceiling-ok'
wait_for_log 'msd-decoration-ceiling-ok'

send_command 'execute if block 15 64 0 msd:decoration_ceiling_light[facing=west,type=0] run say msd-decoration-ceiling-light-ok'
wait_for_log 'msd-decoration-ceiling-light-ok'

send_command 'execute if block 16 64 0 msd:decoration_stair[facing=north,type=1] run say msd-decoration-stair-ok'
wait_for_log 'msd-decoration-stair-ok'

send_command 'execute if block 17 64 0 msd:hall_seat_middle[facing=east,type=2] run say msd-hall-seat-middle-ok'
wait_for_log 'msd-hall-seat-middle-ok'

send_command 'execute if block 18 64 0 msd:hall_seat_side_mirror[facing=west,type=1] run say msd-hall-seat-side-mirror-ok'
wait_for_log 'msd-hall-seat-side-mirror-ok'

send_command 'execute if block 19 64 0 msd:surveillance_cameras[facing=north,type=1] run say msd-surveillance-ok'
wait_for_log 'msd-surveillance-ok'

send_command 'execute if block 20 64 0 msd:surveillance_cameras_wall[facing=south,type=0] run say msd-surveillance-wall-ok'
wait_for_log 'msd-surveillance-wall-ok'

send_command 'execute if block 21 64 0 msd:decoration_book[facing=west,type=1] run say msd-decoration-book-ok'
wait_for_log 'msd-decoration-book-ok'

send_command 'execute if block 22 64 0 msd:decoration_pc[facing=east,type=1] run say msd-decoration-pc-ok'
wait_for_log 'msd-decoration-pc-ok'

send_command 'execute if block 23 64 0 msd:yuuni_ticket[facing=north,half=lower] run say msd-yuuni-ticket-lower-ok'
wait_for_log 'msd-yuuni-ticket-lower-ok'

send_command 'execute if block 23 65 0 msd:yuuni_ticket[facing=north,half=upper] run say msd-yuuni-ticket-upper-ok'
wait_for_log 'msd-yuuni-ticket-upper-ok'

send_command 'execute if block 24 64 0 msd:railing_stair_start[facing=north,type=1] run say msd-railing-start-ok'
wait_for_log 'msd-railing-start-ok'

send_command 'execute if block 25 64 0 msd:railing_stair[facing=east,type=2] run say msd-railing-middle-ok'
wait_for_log 'msd-railing-middle-ok'

send_command 'execute if block 26 64 0 msd:railing_stair_end[facing=south,type=3] run say msd-railing-end-ok'
wait_for_log 'msd-railing-end-ok'

send_command 'execute if block 27 64 0 msd:railing_stair_corner[facing=west,type=4] run say msd-railing-corner-ok'
wait_for_log 'msd-railing-corner-ok'

send_command 'execute if block 28 64 0 msd:railing_stair_corner_2[facing=north,type=5] run say msd-railing-corner-2-ok'
wait_for_log 'msd-railing-corner-2-ok'

send_command 'execute if block 29 64 0 msd:railing_stair_mirror[facing=east,type=2] run say msd-railing-mirror-ok'
wait_for_log 'msd-railing-mirror-ok'

send_command 'execute if block 30 64 0 msd:railing_stair_corner_mirror[facing=south,type=4] run say msd-railing-corner-mirror-ok'
wait_for_log 'msd-railing-corner-mirror-ok'

send_command 'execute if block 31 64 0 msd:railing_stair_glass_3[facing=west,type=3] run say msd-railing-glass-ok'
wait_for_log 'msd-railing-glass-ok'

send_command 'execute if block 32 64 0 msd:railing_stair_glass_5[facing=north,type=5] run say msd-railing-glass-corner-ok'
wait_for_log 'msd-railing-glass-corner-ok'

send_command 'execute if block 33 64 0 msd:railing_stair_glass_mirror_1[facing=east,type=1] run say msd-railing-glass-mirror-start-ok'
wait_for_log 'msd-railing-glass-mirror-start-ok'

send_command 'execute if block 34 64 0 msd:railing_stair_glass_mirror_4[facing=south,type=4] run say msd-railing-glass-mirror-corner-ok'
wait_for_log 'msd-railing-glass-mirror-corner-ok'

send_command 'execute if block 35 64 0 msd:railing_stair_glass_mirror_5[facing=west,type=5] run say msd-railing-glass-mirror-corner-2-ok'
wait_for_log 'msd-railing-glass-mirror-corner-2-ok'

send_command 'execute if block 36 64 0 msd:catenary_pole[facing=up] run say msd-old-catenary-pole-ok'
wait_for_log 'msd-old-catenary-pole-ok'

send_command 'execute if block 37 64 0 msd:catenary_rack_1[facing=north] run say msd-old-catenary-rack-ok'
wait_for_log 'msd-old-catenary-rack-ok'

send_command 'execute if block 38 64 0 msd:short_catenary_rack_side[facing=east] run say msd-old-short-rack-ok'
wait_for_log 'msd-old-short-rack-ok'

send_command 'execute if block 39 64 0 msd:electric_pole_side[facing=down] run say msd-old-electric-side-ok'
wait_for_log 'msd-old-electric-side-ok'

send_command 'execute if block 40 64 0 msd:electric_pole_top_side[facing=north,is_long=false] run say msd-old-electric-top-side-ok'
wait_for_log 'msd-old-electric-top-side-ok'

send_command 'execute if block 41 64 0 msd:electric_pole_top_both_side[facing=east,is_long=false] run say msd-old-electric-top-both-ok'
wait_for_log 'msd-old-electric-top-both-ok'

send_command 'execute if block 42 64 0 msd:catenary_node[facing=up,is_connected=false] run say msd-old-node-catenary-ok'
wait_for_log 'msd-old-node-catenary-ok'

send_command 'execute if block 43 64 0 msd:catenary_node_style_2[facing=north,is_connected=false] run say msd-old-node-catenary-2-ok'
wait_for_log 'msd-old-node-catenary-2-ok'

send_command 'execute if block 44 64 0 msd:short_catenary_node[facing=east,is_connected=false] run say msd-old-node-short-ok'
wait_for_log 'msd-old-node-short-ok'

send_command 'execute if block 45 64 0 msd:short_catenary_node_style_2[facing=south,is_connected=false] run say msd-old-node-short-2-ok'
wait_for_log 'msd-old-node-short-2-ok'

send_command 'execute if block 46 64 0 msd:electric_node[facing=west,is_connected=false] run say msd-old-node-electric-ok'
wait_for_log 'msd-old-node-electric-ok'

send_command 'execute if block 47 64 0 msd:trans_catenary_node[facing=down,is_connected=false] run say msd-old-node-trans-ok'
wait_for_log 'msd-old-node-trans-ok'

send_command 'data merge block 42 64 0 {msd_offset_position_x:0.25d,msd_offset_position_y:-0.125d,msd_offset_position_z:0.5d}'
wait_for_log 'Modified block (entity )?data of (block at )?42, 64, 0'
send_command 'data get block 42 64 0'
wait_for_log '42, 64, 0 has the following block data:'
wait_for_log 'msd_offset_position_x'
wait_for_log '0.25'
wait_for_log 'msd_offset_position_y'
wait_for_log '-0.125'
wait_for_log 'msd_offset_position_z'
wait_for_log '0.5'
send_command 'say msd-old-node-catenary-config-ok'
wait_for_log 'msd-old-node-catenary-config-ok'

send_command 'execute if block 48 64 0 msd:yamanote_railway_sign_pole[facing=north,type=2] run say msd-yamanote-sign-pole-ok'
wait_for_log 'msd-yamanote-sign-pole-ok'

send_command 'execute if block 49 64 0 msd:rigid_catenary_node[facing=true,is_22_5=true,is_45=false,is_connected=false] run say msd-rigid-catenary-node-ok'
wait_for_log 'msd-rigid-catenary-node-ok'

send_command 'msd_probe_rigid connect 49 64 0 49 64 2'
wait_for_log 'MSD rigid catenary connected'
send_command 'msd_probe_rigid assert 49 64 0 true'
wait_for_log 'MSD rigid node assertion passed'
send_command 'msd_probe_rigid assert 49 64 2 true'
wait_for_log 'MSD rigid node assertion passed'
send_command 'say msd-rigid-catenary-node-connected-ok'
wait_for_log 'msd-rigid-catenary-node-connected-ok'

send_command 'execute if block 50 64 0 msd:catenary_with_long[facing=north,is_connected=false] run say msd-catenary-with-long-ok'
wait_for_log 'msd-catenary-with-long-ok'

send_command 'execute if block 51 64 0 msd:catenary_with_short[facing=east,is_connected=false] run say msd-catenary-with-short-ok'
wait_for_log 'msd-catenary-with-short-ok'

send_command 'execute if block 52 64 0 msd:catenary_with_long_top[facing=south,is_connected=false] run say msd-catenary-with-long-top-ok'
wait_for_log 'msd-catenary-with-long-top-ok'

send_command 'execute if block 53 64 0 msd:catenary_with_short_top[facing=west,is_connected=false] run say msd-catenary-with-short-top-ok'
wait_for_log 'msd-catenary-with-short-top-ok'

send_command 'execute if block 54 64 0 msd:catenary_with_long_counterweight[facing=north,is_connected=false] run say msd-catenary-with-long-counterweight-ok'
wait_for_log 'msd-catenary-with-long-counterweight-ok'

send_command 'execute if block 55 64 0 msd:catenary_with_long_counterweight_mirror[facing=east,is_connected=false] run say msd-catenary-with-long-counterweight-mirror-ok'
wait_for_log 'msd-catenary-with-long-counterweight-mirror-ok'

send_command 'execute if block 56 64 0 msd:catenary_with_short_counterweight[facing=south,is_connected=false] run say msd-catenary-with-short-counterweight-ok'
wait_for_log 'msd-catenary-with-short-counterweight-ok'

send_command 'execute if block 57 64 0 msd:catenary_with_short_counterweight_mirror[facing=west,is_connected=false] run say msd-catenary-with-short-counterweight-mirror-ok'
wait_for_log 'msd-catenary-with-short-counterweight-mirror-ok'

send_command 'msd_probe_block_entity model 50 64 0 -0.25 0.375 0.125 45'
wait_for_log 'MSD catenary-with-model updated'
send_command 'data get block 50 64 0'
wait_for_log '50, 64, 0 has the following block data:'
wait_for_log 'msd_offset_position_x'
wait_for_log '-0.25'
wait_for_log 'msd_offset_position_y'
wait_for_log '0.375'
wait_for_log 'msd_offset_position_z'
wait_for_log '0.125'
wait_for_log 'msd_rotate_position_y'
wait_for_log '45.0'
send_command 'say msd-catenary-with-long-config-ok'
wait_for_log 'msd-catenary-with-long-config-ok'

send_command 'msd_probe_rigid shape 49 64 0 quadratic 2'
wait_for_log 'MSD rigid catenary shape updated'
send_command 'msd_probe_rigid save'
wait_for_log 'MSD runtime saved'
send_command 'say msd-rigid-catenary-save-ok'
wait_for_log 'msd-rigid-catenary-save-ok'

send_command 'execute if block 58 64 0 msd:yamanote_railway_sign_2_even[facing=north] run say msd-yamanote-railway-sign-2-even-ok'
wait_for_log 'msd-yamanote-railway-sign-2-even-ok'
send_command 'execute if block 59 64 0 msd:yamanote_railway_sign_2_even[facing=south] run say msd-yamanote-railway-sign-2-even-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-2-even-tail-ok'

send_command 'execute if block 61 64 0 msd:yamanote_railway_sign_2_odd[facing=east] run say msd-yamanote-railway-sign-2-odd-ok'
wait_for_log 'msd-yamanote-railway-sign-2-odd-ok'
send_command 'execute if block 61 64 1 msd:yamanote_railway_sign_middle[facing=east] run say msd-yamanote-railway-sign-middle-east-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-east-ok'
send_command 'execute if block 61 64 2 msd:yamanote_railway_sign_2_odd[facing=west] run say msd-yamanote-railway-sign-2-odd-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-2-odd-tail-ok'

send_command 'execute if block 64 64 0 msd:yamanote_railway_sign_3_even[facing=south] run say msd-yamanote-railway-sign-3-even-ok'
wait_for_log 'msd-yamanote-railway-sign-3-even-ok'
send_command 'execute if block 63 64 0 msd:yamanote_railway_sign_3_even[facing=north] run say msd-yamanote-railway-sign-3-even-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-3-even-tail-ok'

send_command 'execute if block 68 64 0 msd:yamanote_railway_sign_3_odd[facing=west] run say msd-yamanote-railway-sign-3-odd-ok'
wait_for_log 'msd-yamanote-railway-sign-3-odd-ok'
send_command 'execute if block 68 64 -1 msd:yamanote_railway_sign_middle[facing=west] run say msd-yamanote-railway-sign-middle-west-1-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-west-1-ok'
send_command 'execute if block 68 64 -2 msd:yamanote_railway_sign_3_odd[facing=east] run say msd-yamanote-railway-sign-3-odd-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-3-odd-tail-ok'

send_command 'execute if block 72 64 0 msd:yamanote_railway_sign_4_even[facing=north] run say msd-yamanote-railway-sign-4-even-ok'
wait_for_log 'msd-yamanote-railway-sign-4-even-ok'
send_command 'execute if block 73 64 0 msd:yamanote_railway_sign_middle[facing=north] run say msd-yamanote-railway-sign-middle-north-4-1-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-north-4-1-ok'
send_command 'execute if block 74 64 0 msd:yamanote_railway_sign_middle[facing=north] run say msd-yamanote-railway-sign-middle-north-4-2-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-north-4-2-ok'
send_command 'execute if block 75 64 0 msd:yamanote_railway_sign_4_even[facing=south] run say msd-yamanote-railway-sign-4-even-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-4-even-tail-ok'

send_command 'execute if block 77 64 0 msd:yamanote_railway_sign_4_odd[facing=east] run say msd-yamanote-railway-sign-4-odd-ok'
wait_for_log 'msd-yamanote-railway-sign-4-odd-ok'
send_command 'execute if block 77 64 1 msd:yamanote_railway_sign_middle[facing=east] run say msd-yamanote-railway-sign-middle-east-4-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-east-4-ok'
send_command 'execute if block 77 64 2 msd:yamanote_railway_sign_4_odd[facing=west] run say msd-yamanote-railway-sign-4-odd-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-4-odd-tail-ok'

send_command 'execute if block 82 64 0 msd:yamanote_railway_sign_5_even[facing=south] run say msd-yamanote-railway-sign-5-even-ok'
wait_for_log 'msd-yamanote-railway-sign-5-even-ok'
send_command 'execute if block 81 64 0 msd:yamanote_railway_sign_middle[facing=south] run say msd-yamanote-railway-sign-middle-south-5-1-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-south-5-1-ok'
send_command 'execute if block 80 64 0 msd:yamanote_railway_sign_middle[facing=south] run say msd-yamanote-railway-sign-middle-south-5-2-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-south-5-2-ok'
send_command 'execute if block 79 64 0 msd:yamanote_railway_sign_5_even[facing=north] run say msd-yamanote-railway-sign-5-even-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-5-even-tail-ok'

send_command 'execute if block 87 64 0 msd:yamanote_railway_sign_5_odd[facing=west] run say msd-yamanote-railway-sign-5-odd-ok'
wait_for_log 'msd-yamanote-railway-sign-5-odd-ok'
send_command 'execute if block 87 64 -1 msd:yamanote_railway_sign_middle[facing=west] run say msd-yamanote-railway-sign-middle-west-5-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-west-5-ok'
send_command 'execute if block 87 64 -2 msd:yamanote_railway_sign_5_odd[facing=east] run say msd-yamanote-railway-sign-5-odd-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-5-odd-tail-ok'

send_command 'execute if block 90 64 0 msd:yamanote_railway_sign_6_even[facing=north] run say msd-yamanote-railway-sign-6-even-ok'
wait_for_log 'msd-yamanote-railway-sign-6-even-ok'
send_command 'execute if block 91 64 0 msd:yamanote_railway_sign_middle[facing=north] run say msd-yamanote-railway-sign-middle-north-6-1-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-north-6-1-ok'
send_command 'execute if block 92 64 0 msd:yamanote_railway_sign_middle[facing=north] run say msd-yamanote-railway-sign-middle-north-6-2-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-north-6-2-ok'
send_command 'execute if block 93 64 0 msd:yamanote_railway_sign_6_even[facing=south] run say msd-yamanote-railway-sign-6-even-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-6-even-tail-ok'

send_command 'execute if block 95 64 0 msd:yamanote_railway_sign_6_odd[facing=east] run say msd-yamanote-railway-sign-6-odd-ok'
wait_for_log 'msd-yamanote-railway-sign-6-odd-ok'
send_command 'execute if block 95 64 1 msd:yamanote_railway_sign_middle[facing=east] run say msd-yamanote-railway-sign-middle-east-6-1-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-east-6-1-ok'
send_command 'execute if block 95 64 2 msd:yamanote_railway_sign_middle[facing=east] run say msd-yamanote-railway-sign-middle-east-6-2-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-east-6-2-ok'
send_command 'execute if block 95 64 3 msd:yamanote_railway_sign_middle[facing=east] run say msd-yamanote-railway-sign-middle-east-6-3-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-east-6-3-ok'
send_command 'execute if block 95 64 4 msd:yamanote_railway_sign_6_odd[facing=west] run say msd-yamanote-railway-sign-6-odd-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-6-odd-tail-ok'

send_command 'execute if block 100 64 0 msd:yamanote_railway_sign_7_even[facing=south] run say msd-yamanote-railway-sign-7-even-ok'
wait_for_log 'msd-yamanote-railway-sign-7-even-ok'
send_command 'execute if block 99 64 0 msd:yamanote_railway_sign_middle[facing=south] run say msd-yamanote-railway-sign-middle-south-7-1-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-south-7-1-ok'
send_command 'execute if block 98 64 0 msd:yamanote_railway_sign_middle[facing=south] run say msd-yamanote-railway-sign-middle-south-7-2-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-south-7-2-ok'
send_command 'execute if block 97 64 0 msd:yamanote_railway_sign_7_even[facing=north] run say msd-yamanote-railway-sign-7-even-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-7-even-tail-ok'

send_command 'execute if block 105 64 0 msd:yamanote_railway_sign_7_odd[facing=west] run say msd-yamanote-railway-sign-7-odd-ok'
wait_for_log 'msd-yamanote-railway-sign-7-odd-ok'
send_command 'execute if block 105 64 -1 msd:yamanote_railway_sign_middle[facing=west] run say msd-yamanote-railway-sign-middle-west-7-1-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-west-7-1-ok'
send_command 'execute if block 105 64 -2 msd:yamanote_railway_sign_middle[facing=west] run say msd-yamanote-railway-sign-middle-west-7-2-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-west-7-2-ok'
send_command 'execute if block 105 64 -3 msd:yamanote_railway_sign_middle[facing=west] run say msd-yamanote-railway-sign-middle-west-7-3-ok'
wait_for_log 'msd-yamanote-railway-sign-middle-west-7-3-ok'
send_command 'execute if block 105 64 -4 msd:yamanote_railway_sign_7_odd[facing=east] run say msd-yamanote-railway-sign-7-odd-tail-ok'
wait_for_log 'msd-yamanote-railway-sign-7-odd-tail-ok'

send_command 'data merge block 58 64 0 {yamanote_selected_ids:[L;11L,22L],yamanote_sign_length0:"platform",yamanote_sign_length1:"station"}'
wait_for_log 'Modified block (entity )?data of (block at )?58, 64, 0'
send_command 'data get block 58 64 0'
wait_for_log '58, 64, 0 has the following block data:'
wait_for_log 'yamanote_selected_ids'
wait_for_log '11'
wait_for_log '22'
wait_for_log 'yamanote_sign_length0'
wait_for_log 'platform'
wait_for_log 'yamanote_sign_length1'
wait_for_log 'station'
send_command 'say msd-yamanote-railway-sign-config-ok'
wait_for_log 'msd-yamanote-railway-sign-config-ok'

send_command 'forceload remove 0 -16 110 16'
wait_for_log 'Unmarked [0-9]+ chunk[s]? in Overworld from|No chunks were marked for force loading'

stop_server

start_server
send_command 'forceload add 0 -16 110 16'
wait_for_log 'Marked [0-9]+ chunk[s]? in Overworld from|No chunks were marked for force loading'
send_command 'data get block 10 64 0'
wait_for_log '10, 64, 0 has the following block data:'
wait_for_log 'msd_custom_message0'
wait_for_log 'local'
wait_for_log 'msd_custom_message1'
wait_for_log 'rapid'
wait_for_log 'msd_custom_message2'
wait_for_log 'platform'
send_command 'say msd-standing-sign-persisted-ok'
wait_for_log 'msd-standing-sign-persisted-ok'
send_command 'data get block 58 64 0'
wait_for_log '58, 64, 0 has the following block data:'
wait_for_log 'yamanote_selected_ids'
wait_for_log '11'
wait_for_log '22'
wait_for_log 'yamanote_sign_length0'
wait_for_log 'platform'
wait_for_log 'yamanote_sign_length1'
wait_for_log 'station'
send_command 'say msd-yamanote-railway-sign-persisted-ok'
wait_for_log 'msd-yamanote-railway-sign-persisted-ok'
send_command 'forceload remove 0 -16 110 16'
wait_for_log 'Unmarked [0-9]+ chunk[s]? in Overworld from|No chunks were marked for force loading'
stop_server

RIGID_DIR_REL="$(find "$ACTIVE_WORLD_DIR" -type d -path '*/msd/minecraft/overworld/rigid_catenaries' | head -n 1 | sed "s#^$ACTIVE_WORLD_DIR/##")"
if [[ -z "$RIGID_DIR_REL" ]]; then
  echo "Missing rigid catenary save directory under $ACTIVE_WORLD_DIR" >&2
  exit 1
fi

mapfile -t RIGID_SAVE_FILES < <(find "$ACTIVE_WORLD_DIR/$RIGID_DIR_REL" -type f)
if (( ${#RIGID_SAVE_FILES[@]} == 0 )); then
  echo "Rigid catenary save data was not written" >&2
  find "$ACTIVE_WORLD_DIR/$RIGID_DIR_REL" -maxdepth 2 >&2 || true
  exit 1
fi

if ! printf '%s\0' "${RIGID_SAVE_FILES[@]}" | xargs -0 strings | rg -q '^QUADRATIC$'; then
  echo "Rigid catenary save data does not contain QUADRATIC shape marker" >&2
  printf '%s\n' "${RIGID_SAVE_FILES[@]}" >&2
  exit 1
fi

echo "MSD server smoke passed. Log: $LOG_FILE"
