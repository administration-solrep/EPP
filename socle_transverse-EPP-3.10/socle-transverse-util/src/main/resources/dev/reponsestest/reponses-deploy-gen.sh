#!/bin/sh
IC_SERVER=idlv-solon-ic.lyon-dev2.local
ZIP=reponses-distribution-1.0.0-SNAPSHOT-server.zip
APPLI_DIR=/opt/reponses-server-1.0.0-SNAPSHOT
SELENIUM_DIR=/opt/selenium
FUNKLOAD_DIR=/opt/funkload
SELENIUM_RESULT_FILENAME=result-suite.html
SELENIUM_RESULT_FILE=/opt/selenium/${SELENIUM_RESULT_FILENAME}
cd /opt

execname=$0

usage(){
  echo "usage $execname <reset|noreset> <selenium|noselenium> <injection|noinjection> <qualif|dev|none>"
}

if [ $# -ne 4 ]; then
  usage
  exit
fi

reset=$1
selenium=$2
injection=$3
ldap=$4

# DL / Unzip
echo -----Downloading
rm -f ${ZIP}
wget --no-verbose "http://${IC_SERVER}:8080/hudson/job/reponses-distribution%20(qa)/ws/reponses-distribution/target/${ZIP}"

/etc/init.d/reponses-server_inst1 stop


rm -rf ${APPLI_DIR}
rm -rf ${SELENIUM_DIR}
rm -rf ${FUNKLOAD_DIR}

if [ "$reset" = "reset" ]; then
  echo -----Clear user in db and reset schema
  sh resetSchema.sh
else
  echo ----No clear on database : data kept
fi

echo -----Unzipping
unzip ${ZIP} 
cd ${APPLI_DIR}/init/bin
APPLI_INSTALL_DIR=/opt/reponses-server_$(cat ${APPLI_DIR}/init/version)
CONFIG_DIR=/opt/reponses_config

echo "remove : ${APPLI_INSTALL_DIR}"
rm -rf ${APPLI_INSTALL_DIR}
rm -rf ${CONFIG_DIR}
sh install_config.sh /opt/ test 1
sh install_server.sh ${CONFIG_DIR} 

cd /opt/
# Udapte du LDAP
echo "Update du ldap : trace dans ldap.log"
./reponses-ldap-deploy.sh $ldap  > ldap.log


# Demarrage de l'application
/etc/init.d/reponses-server_inst1 start



if [ "$reset" = "reset" ]; then
  waittime=180
  echo "Attente du démarrage de l'application ($waittime sec)"
  sleep $waittime


  if [ "$selenium" = "selenium" ]; then

    sed -i 's/localhost:8080/localhost:8180/' ${SELENIUM_DIR}/run.sh

    # Demarrage des tests selenium
    echo -----Selenium tests
    export PATH=$PATH:/opt/firefox3
    startx -- `which Xvfb` :1 -screen 0 1024x768x24 2>&1 >/dev/null &
    export DISPLAY=:1
    chmod +x ${SELENIUM_DIR}/*.sh;  ${SELENIUM_DIR}/run.sh
    killall -9 Xvfb
  else 
    echo "SELENIUM SKIPPED"
  fi

  if [ "$injection" = "injection" ]; then
    # INJECTION 
    echo "INJECTION"
    ./injection-auto.sh
  else 
    echo "INJECTION SKIPPED"
  fi
fi


