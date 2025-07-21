@echo off
title Jerry's Quick Mart
echo ========================================
echo    Jerry's Quick Mart - POS System
echo ========================================
echo.
echo Starting application...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or later
    pause
    exit /b 1
)

REM Check if inventory file exists
if not exist "inventory.txt" (
    echo ERROR: inventory.txt file not found
    echo Please ensure inventory.txt is in the same folder
    pause
    exit /b 1
)

REM Run the application
java -jar "target\Jerrys_QuickMart-1.0-SNAPSHOT.jar"

echo.
echo Application finished.
pause 