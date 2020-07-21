#!/bin/sh

execname=$0
USER=$1
SCHEMA_SRC=$2
SCHEMA_DEST=$3
inputfile=$4

usage(){
 echo "usage : $execname <user> <schema source> <schema dest> <export to import>"
}

if [ $# -ne 4 ]; then
  usage
  exit
fi

BACKUPDIR=/home/oinstall/backups
if [ -z "$USER" -o -z "$SCHEMA_SRC" -o -z "$SCHEMA_DEST" -o -z "$inputfile" -o ! -f "${BACKUPDIR}/$inputfile" ]; then
  echo "input file [${BACKUPDIR}/$inputfile] does not exist"
  usage
  exit
fi

INSTANCE=SOLON
PASSWD=$USER


tmpfile=/tmp/req1.sql

cat > $tmpfile <<EOF

DROP USER "$USER"  CASCADE;
CREATE USER "$USER" IDENTIFIED BY "$PASSWD";

GRANT CONNECT TO "$USER";
GRANT RESOURCE TO "$USER";
GRANT EXECUTE ON SYS.DBMS_CRYPTO TO "$USER";	
GRANT SELECT ON SYS.V_\$SESSION TO "$USER";
GRANT EXECUTE ON CTXSYS.CTX_DDL TO $USER;

GRANT READ ON DIRECTORY dtpump to $USER,system;
GRANT WRITE ON DIRECTORY dtpump to $USER,system;

exit

EOF

importlog=import_${INSTANCE}_${USER}_${SCHEMA_SRC}_${SCHEMA_DEST}_$(date +%Y%m%d%H%M%S).log
#echo "importlog=[$importlog]"

echo "START : $(date +%Y%m%d%H%M%S)"

echo "DROP USER AND RECREATE IT"
ORACLE_SID=$INSTANCE sqlplus -s sys/system as sysdba @$tmpfile

echo "START IMPORT : $(date +%Y%m%d%H%M%S)"

opt=
if [ "$SCHEMA_SRC" != "$SCHEMA_DEST" ]; then
  echo "Import with remapping from ${SCHEMA_SRC} to ${SCHEMA_DEST}"
  opt="REMAP_SCHEMA=${SCHEMA_SRC}:${SCHEMA_DST}"
fi
ORACLE_SID=$INSTANCE impdp $USER/$PASSWD SCHEMAS=$SCHEMA_SRC dumpfile=$inputfile logfile=$importlog $opt directory=dtpump

echo "DONE : $(date +%Y%m%d%H%M%S)"

