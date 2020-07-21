#!/bin/sh

if [ ! -f ./injection-config.inc ]; then
        echo "ERROR : cannot include injection-config.inc"
        exit
fi
. ./injection-config.inc


if [ ! -d "$1" ]; then 
	echo "no question dir"
	exit 0
fi

PATTERN="file:$1/*.xml"

echo "-----Injection des questions"
currentdir=$(pwd)
cd $INJECTDIR
sh ./doInject.sh $URL "$PATTERN" inject_$(date +%s)
cd $currentdir



