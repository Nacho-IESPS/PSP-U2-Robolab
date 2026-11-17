#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p out/classes
find src/main/java src/test/java -name '*.java' > out/sources.txt
javac -encoding UTF-8 -d out/classes @out/sources.txt
printf 'Compilación completada.\n'
