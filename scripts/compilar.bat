@echo off
setlocal
cd /d "%~dp0.."
if not exist out\classes mkdir out\classes
javac -encoding UTF-8 -d out\classes src\main\java\es\robolab\core\*.java src\main\java\es\robolab\retos\reto1\*.java src\main\java\es\robolab\app\*.java src\test\java\es\robolab\retos\reto1\*.java
if errorlevel 1 exit /b 1
echo Compilacion completada.
