#!/bin/sh

# param <msg>
info(){
  echo "$(date +'%Y-%m-%d %H:%M:%S') $1"
}

genCmd(){
  prepareCmd "../extlib ../jdbcclient-1.0.0-SNAPSHOT"
}

prepareCmd() {
  local inputs=$1
  local CP=
  local f=
  for f in $(find  $inputs -name "*.jar"); do
    CP=$CP:$f
  done

  if [ -n "$CP" ]; then
    CP="$(echo $CP | sed s/'^://')"
  fi

  local OPTS=-Djava.security.egd=file:///dev/urandom
  OPTS="$OPTS -Dfile.encoding=UTF-8"

  echo "java -cp $CP $OPTS fr.sword.naiad.tools.jdbcclient.run.simple.SimpleClient"
}

# param <user> <output file>
genFileToSelectSession(){
  local USER=$1
  local output=$2

  cat > $output <<EOF

SELECT SID,SERIAL# FROM V_\$SESSION WHERE USERNAME = '$USER';

EOF

}

# param <input file> <output file>
# output file is not created if there is no query to do
genFileToKillSession(){
  local input=$1
  local output=$2
  local aline=
  local sid=
  local serial=

  rm -rf $output
  grep SimpleProcessResult $input | sed 's/.*] //' | grep -v SID | while read aline; do
        value=$(echo $aline | sed 's/[^0-9]*\([0-9]*\)[^0-9]*\([0-9]*\).*/sid=\1;serial=\2/')
        eval $value
        echo "ALTER SYSTEM KILL SESSION '$sid,$serial' IMMEDIATE;" >> $output
        echo >> $output
  done

}


# param <user> <output file>
genFileToDropUser(){
  local user=$1
  local output=$2

  cat > $output <<EOF

-- TEST true select 1 from all_users where username = '$USER';
DROP USER $USER CASCADE;

EOF

  
}


# param <user> <output file>
genFileToCreateReponseUser(){
  local user=$1
  local output=$2

cat > $output <<EOF

CREATE USER "$USER" identified by "$USER";

GRANT CONNECT TO "$USER";

GRANT RESOURCE TO "$USER";

GRANT EXECUTE ON SYS.DBMS_CRYPTO TO "$USER";

GRANT SELECT ON SYS.V_\$SESSION TO "$USER";

GRANT SELECT ON SYS.GV_\$SESSION TO "$USER";

GRANT EXECUTE ON CTXSYS.CTX_DDL TO "$USER";

EOF

}





