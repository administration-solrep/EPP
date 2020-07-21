#!/bin/sh

source ~/graph_injection_epg/scripts/vars.sh
EPG_SERVER=idlv-solon-intej3.lyon-dev2.local
EPG_INJECTEUR_DIR=/root/injecteur
EPG_INJECTER_LOGFILE="nohup.out"
INJECTION_FILE_NAME="injection.out"

## Download injecteur log
scp root@$EPG_SERVER:${EPG_INJECTEUR_DIR}/${EPG_INJECTER_LOGFILE} ${INJECTION_DATA_DIR}/$INJECTION_FILE_NAME

## Download server log
scp root@$EPG_SERVER:/var/log/epg-server_inst1/server.log server_logs



