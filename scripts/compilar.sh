#!/usr/bin/env bash
set -euo pipefail
mkdir -p out
find src/main/java -name '*.java' > out/sources.txt
javac -encoding UTF-8 --release 21 -d out @out/sources.txt
