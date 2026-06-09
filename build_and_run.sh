#!/bin/bash
# ============================================================
# Football Management System — Build & Run Script
# ============================================================
# Requirements: JDK 11+ and junit-4.13.jar / hamcrest-core.jar
#               in the lib/ folder (see README for download links)
# ============================================================

set -e

SRC_DIR="src"
TEST_DIR="test"
OUT_DIR="out"
LIB_DIR="lib"
JUNIT_JAR="$LIB_DIR/junit-4.13.2.jar"
HAMCREST_JAR="$LIB_DIR/hamcrest-core-1.3.jar"
MAIN_CLASS="football.Main"

echo "========================================"
echo " Football Management System Build Script"
echo "========================================"

# Create output directory
mkdir -p "$OUT_DIR/main" "$OUT_DIR/test"

# ── 1. Compile main source ────────────────────────────────────────────────────
echo ""
echo "[1/4] Compiling main sources..."
find "$SRC_DIR" -name "*.java" > sources_main.txt
javac -d "$OUT_DIR/main" @sources_main.txt
rm sources_main.txt
echo "      Main sources compiled OK."

# ── 2. Compile tests (only if JUnit jars present) ────────────────────────────
if [ -f "$JUNIT_JAR" ] && [ -f "$HAMCREST_JAR" ]; then
    echo ""
    echo "[2/4] Compiling test sources..."
    find "$TEST_DIR" -name "*.java" > sources_test.txt
    javac -cp "$OUT_DIR/main:$JUNIT_JAR:$HAMCREST_JAR" -d "$OUT_DIR/test" @sources_test.txt
    rm sources_test.txt
    echo "      Test sources compiled OK."

    # ── 3. Run tests ──────────────────────────────────────────────────────────
    echo ""
    echo "[3/4] Running unit tests..."
    java -cp "$OUT_DIR/main:$OUT_DIR/test:$JUNIT_JAR:$HAMCREST_JAR" \
         org.junit.runner.JUnitCore \
         football.service.PlayerServiceTest \
         football.service.TeamServiceTest \
         football.service.MatchServiceTest
else
    echo ""
    echo "[2/4] Skipping tests — JUnit jars not found in lib/."
    echo "      Download from:"
    echo "      https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar"
    echo "      https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
    echo "      Place both files in the lib/ folder, then re-run this script."
fi

# ── 4. Launch application ─────────────────────────────────────────────────────
echo ""
echo "[4/4] Launching Football Management System..."
java -cp "$OUT_DIR/main" "$MAIN_CLASS"
