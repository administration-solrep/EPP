#!/bin/sh

USER=$1

if [ -z "$USER" ]; then
  echo "usage : $0 <user>"
  exit
fi

INSTANCE=SOLON
PASSWD=$USER
SCHEMA=$USER

base=export_${INSTANCE}_${SCHEMA}_$(date +%Y%m%d%H%M%S)

echo $base
start=$(date)
echo "START : $start"
ORACLE_SID=${INSTANCE} expdp $USER/$PASSWD dumpfile=$base.dump directory=dtpump logfile=$base.log schemas=${SCHEMA}
echo "END : $(date)  [START: $start]"



