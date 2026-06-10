@echo off
title Frontend - Smoke Detection System

echo ============================================
echo   Smoke Detection System - Frontend
echo ============================================
echo.

set "ROOT=%~dp0.."

pushd "%ROOT%\frontend" 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Cannot enter: %ROOT%\frontend
    pause
    exit /b 1
)
echo [OK]  Working dir: %CD%
echo.

call node --version >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Node.js not found in PATH
    pause
    exit /b 1
)
echo [OK]  Node.js ready
echo.

if not exist "node_modules\" (
    echo [INFO] Installing dependencies, please wait...
    call npm install
    if %ERRORLEVEL% neq 0 (
        echo [ERROR] npm install failed
        pause
        exit /b 1
    )
    echo [OK]  Dependencies installed
) else (
    echo [OK]  Dependencies ready
)
echo.

echo ============================================
echo   Dev server: http://localhost:3000
echo   Press Ctrl+C to stop
echo ============================================
echo.

call npx vite --host 0.0.0.0 --port 3000

echo.
echo Server stopped.
pause
