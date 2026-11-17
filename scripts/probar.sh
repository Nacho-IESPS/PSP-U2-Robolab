#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
./scripts/compilar.sh
java -cp out/classes es.robolab.retos.reto1.PruebasReto1
