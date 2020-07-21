#!/bin/sh

. ./utils.inc
. ./config.inc

sh checkRequirement.sh

USER=$1
DDLFILE=$2

# param <exec>
usage(){
  echo "usage $1 <user> <ddlfile>"
}

if [ -z "$USER" ]; then
  usage $0
  exit
fi

if [ -z "$DDLFILE" ]; then
  usage $0
  exit
fi

if [ ! -f "$DDLFILE" ]; then
  usage $0
  exit
fi



cmd=$(genCmd)

tmpfile=/tmp/query.sql

## KILL SESSION 

info "Look for session with user $USER"
genFileToSelectSession "$USER" $tmpfile



OUTPUT=/tmp/tmpoutput
$cmd -d "$DRIVER" -u "$URL" -l "$SYSUSER" -p "$SYSPASSWORD"  -q -f $tmpfile > $OUTPUT

genFileToKillSession $OUTPUT $tmpfile

while [ -f $tmpfile ]; do
  info "Kill Session"
  cat $tmpfile
  $cmd -d "$DRIVER" -u "$URL" -l "$SYSUSER" -p "$SYSPASSWORD"  -f $tmpfile 

  sleep 10


  # recherche si encore des sessions

  $cmd -d "$DRIVER" -u "$URL" -l "$SYSUSER" -p "$SYSPASSWORD"  -q -f $tmpfile > $OUTPUT

  genFileToKillSession $OUTPUT $tmpfile
 
done

## DROP USER
info "Remove user $USER"
genFileToDropUser "$USER" $tmpfile 

$cmd -d "$DRIVER" -u "$URL" -l "$SYSUSER" -p "$SYSPASSWORD" -f $tmpfile


## CREATE USER
info "create user $USER"
genFileToCreateReponseUser "$USER" $tmpfile

$(genCmd) -d "$DRIVER" -u "$URL" -l "$SYSUSER" -p "$SYSPASSWORD"  -f $tmpfile


## APPLY DDL
info "apply ddl"
PASSWORD=$USER
$(genCmd) -d "$DRIVER" -u "$URL" -l "$USER" -p "$PASSWORD"  -s -f $DDLFILE


info "reset done"

