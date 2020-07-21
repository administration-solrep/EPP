#!/bin/sh

USER=SOLONEPP_QA
DDLFILE=/opt/vide-ddl.sql

if [ -n "$1" ]; then
  DDLFILE=$1
fi

killall -9 java
(cd /opt/dbmanip/scripts/ && sh reset_schema.sh $USER $DDLFILE | tee /opt/resetschema.log )

nberr=0
for e in $(grep "\[SimpleClient\] Processing :" resetschema.log | sed 's/.* OK \([0-9]*\) ERROR.*/\1/' | tail -n 1);  do 
  let nberr+=$e; 
done; 

if [ $nberr -gt 0 ]; then
  exit 1
fi

exit 0
