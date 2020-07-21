#!/bin/sh

INJECTION_ID=$1
./scripts/downloadInjectionAndServerLogs.sh
./scripts/clean.sh
./graph
./scripts/upload_injection.sh $1


