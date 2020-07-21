#!/bin/sh


USER=$1

INSTANCE=SOLON

if [ -z "$USER" ]; then
  echo "USER undefined"
  exit
fi

tmpfile=/tmp/req_createuser.sql

cat > $tmpfile <<EOF

create user "$USER" identified by "$USER";

GRANT CONNECT TO "$USER";
GRANT RESOURCE TO "$USER";
GRANT EXECUTE ON SYS.DBMS_CRYPTO TO "$USER";	
GRANT SELECT ON SYS.V_\$SESSION TO "$USER";
GRANT EXECUTE ON CTXSYS.CTX_DDL TO "$USER";

exit

EOF


ORACLE_SID=$INSTANCE sqlplus sys/system as sysdba @$tmpfile


