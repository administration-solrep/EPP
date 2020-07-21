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


PASSWORD=$USER
$(genCmd) -d "$DRIVER" -u "$URL" -l "$USER" -p "$PASSWORD"  -s -f $DDLFILE



