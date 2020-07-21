#!/bin/sh

. ./utils.inc
. ./config.inc

cmd=$(genCmd)

tmpfile=/tmp/query.sql
cat > $tmpfile <<EOF
  select 1 from DUAL;
EOF

echo $(pwd)
echo $cmd

$cmd -d "$DRIVER" -u "$URL" -l "$SYSUSER" -p "$SYSPASSWORD" -f $tmpfile


