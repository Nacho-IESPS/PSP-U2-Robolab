@echo off
if not exist out mkdir out
dir /s /b src\main\java\*.java > out\sources.txt
javac --release 21 -encoding UTF-8 -d out @out\sources.txt
