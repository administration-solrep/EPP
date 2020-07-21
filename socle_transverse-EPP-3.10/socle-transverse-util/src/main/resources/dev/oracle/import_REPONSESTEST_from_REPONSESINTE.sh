#!/bin/sh

if [ $# -ne 1 ]; then
  echo "usage : $0 <export file to import>"
fi

sh import.sh REPONSES_TEST REPONSES_INTE REPONSES_TEST $1

