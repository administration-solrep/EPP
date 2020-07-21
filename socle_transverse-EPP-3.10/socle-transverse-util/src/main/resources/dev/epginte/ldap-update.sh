#!/bin/sh
INIT_BIN_DIR=solonepg-server-1.0.0-SNAPSHOT/init
REMOTE_DIR=/root/epg
scp -r $INIT_BIN_DIR/ldap idlv-solon-intel.lyon-dev2.local:${REMOTE_DIR}
ssh idlv-solon-intel.lyon-dev2.local "${REMOTE_DIR}/solonepg-ldap-deploy.sh"
