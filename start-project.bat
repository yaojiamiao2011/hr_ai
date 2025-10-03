@echo off
echo Starting HR AI Project...

REM Start backend application in a new window
start "Backend" /D "D:\workspace\hr_ai\hr_ai_project" start-backend.bat

REM Wait a few seconds for backend to start
timeout /t 5 /nobreak >nul

REM Start frontend application
cd /d "D:\workspace\hr_ai\frontend"
npm run dev

echo Both applications started.
pause