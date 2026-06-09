@echo off
REM ============================================================
REM Football Management System — Build & Run (Windows)
REM ============================================================
REM Requirements: JDK 11+ installed and on PATH
REM               junit-4.13.2.jar + hamcrest-core-1.3.jar in lib\
REM ============================================================

set SRC_DIR=src
set TEST_DIR=test
set OUT_DIR=out
set LIB_DIR=lib
set JUNIT_JAR=%LIB_DIR%\junit-4.13.2.jar
set HAMCREST_JAR=%LIB_DIR%\hamcrest-core-1.3.jar
set MAIN_CLASS=football.Main

echo ==========================================
echo  Football Management System Build Script
echo ==========================================

mkdir %OUT_DIR%\main 2>nul
mkdir %OUT_DIR%\test 2>nul

REM -- Compile main
echo.
echo [1/4] Compiling main sources...
dir /s /b %SRC_DIR%\*.java > sources_main.txt
javac -d %OUT_DIR%\main @sources_main.txt
del sources_main.txt
echo       Main sources compiled OK.

REM -- Compile & run tests if JUnit present
if exist %JUNIT_JAR% (
    echo.
    echo [2/4] Compiling test sources...
    dir /s /b %TEST_DIR%\*.java > sources_test.txt
    javac -cp "%OUT_DIR%\main;%JUNIT_JAR%;%HAMCREST_JAR%" -d %OUT_DIR%\test @sources_test.txt
    del sources_test.txt
    echo       Tests compiled OK.

    echo.
    echo [3/4] Running unit tests...
    java -cp "%OUT_DIR%\main;%OUT_DIR%\test;%JUNIT_JAR%;%HAMCREST_JAR%" ^
         org.junit.runner.JUnitCore ^
         football.service.PlayerServiceTest ^
         football.service.TeamServiceTest ^
         football.service.MatchServiceTest
) else (
    echo.
    echo [2/4] Skipping tests - JUnit jars not found in lib\
    echo       Download and place in lib\ folder to enable tests.
)

REM -- Launch app
echo.
echo [4/4] Launching Football Management System...
java -cp %OUT_DIR%\main %MAIN_CLASS%
