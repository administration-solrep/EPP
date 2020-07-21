#!/bin/sh
cd /opt
. ./vars.sh

ZIP=solon-epp-distribution-1.0.0-SNAPSHOT-server.zip
SOLONEPP_DIR=/opt/solonepp-server-1.0.0-SNAPSHOT

ARCHIVETODEPLOY=$1
if [ -z "$ARCHIVETODEPLOY" ]; then
  echo "usage $0 <archive to deploy>"
  exit 1
fi

if [ ! -r "$ARCHIVETODEPLOY" ]; then
  echo "failed to read [$ARCHIVETODEPLOY]"
  exit 1
fi

noreset = $2
# DL / Unzip
killall -9 java
rm -rf ${SOLONEPP_DIR}
echo -----Unzipping
unzip -o ${ARCHIVETODEPLOY}
chmod +x ${SOLONEPP_DIR}/bin/*.sh ${SOLONEPP_DIR}/bin/nuxeoctl

# Udapte du LDAP
./solonepp-ldap-deploy.sh

# Reset de la bdd
if [ "$noreset" = "noreset" ]; then
  echo ----No clear on database : data kept
else
echo -----Clear user in db and reset schema
  #sh resetSchema.sh ${SOLONEPP_DIR}/init/oracle/solonepp-ddl.sql
  sh resetSchema.sh /opt/vide-ddl.sql
  ret=$?
  if [ $ret -ne 0 ]; then
    echo "ERROR DURING RESET SCHEMA. STOP DEPLOYEMENT"
    echo "see /opt/resetschema.log"
    exit
  fi
fi

echo "`date` Démarrage de l'application"
./solonepp-run.sh
echo "`date` Application démarrée, temporisation"

# Temporise pour que Nuxeo finisse de deployer tous ses composants
sleep 30

echo "`date` Prêt pour subir les test"


# Demarrage de Xvfb nécessaire pour les tests selenium
echo -----Selenium tests
pidof Xvfb
ret=$?
if [ $ret -ne 0 ]; then
  startx -- `which Xvfb` :1 -screen 0 1024x768x24 2>&1 >/dev/null &
  export DISPLAY=:1
fi

echo "Deployment finished."

