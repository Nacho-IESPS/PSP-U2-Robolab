#!/usr/bin/env bash
set -euo pipefail
bash scripts/compilar.sh
javac --release 21 -cp out -d out src/test/java/es/robolab/retos/reto1/PruebasReto1.java
java -cp out es.robolab.retos.reto1.PruebasReto1
