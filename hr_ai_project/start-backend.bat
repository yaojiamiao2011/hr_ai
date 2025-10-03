@echo off
setlocal enabledelayedexpansion

echo Checking if port 8090 is in use...

REM Check if port 8090 is in use
netstat -ano | findstr :8090 >nul
if %errorlevel% == 0 (
    echo Port 8090 is in use. Finding the process...

    REM Get the PID of the process using port 8090
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8090') do (
        set PID=%%a
        echo Terminating process with PID !PID!...
        taskkill /PID !PID! /F
    )

    echo Process terminated.
) else (
    echo Port 8090 is free.
)

echo Starting backend application...
cd /d "%~dp0"
mvn spring-boot:run