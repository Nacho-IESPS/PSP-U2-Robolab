@echo off
cd /d "%~dp0.."
call scripts\compilar.bat
if errorlevel 1 pause & exit /b 1
java -Dfile.encoding=UTF-8 -cp out\classes es.robolab.retos.reto1.PruebasReto1
pause
