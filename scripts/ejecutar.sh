#!/usr/bin/env bash
set -euo pipefail
bash scripts/compilar.sh
java -cp out es.robolab.app.Main
