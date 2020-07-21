#!/bin/sh
IC_SERVER=idlv-solon-ic.lyon-dev2.local
ZIP=reponses-distribution-1.0.0-SNAPSHOT-server.zip
SOLONEPG_DIR=/opt/reponses-server
SELENIUM_DIR=/opt/selenium
FUNKLOAD_DIR=/opt/funkload
SELENIUM_RESULT_FILENAME=result-suite.html
SELENIUM_RESULT_FILE=/opt/selenium/${SELENIUM_RESULT_FILENAME}
cd /opt


  # Demarrage des tests selenium
  echo -----Selenium tests
  export PATH=$PATH:/opt/firefox3
  startx -- `which Xvfb` :1 -screen 0 1024x768x24 2>&1 >/dev/null &
  export DISPLAY=:1
  chmod +x ${SELENIUM_DIR}/*.sh;  ${SELENIUM_DIR}/run.sh
  killall -9 Xvfb


  # INJECTION 

  ./injection-auto.sh


