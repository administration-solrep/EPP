#!/bin/sh

#JAVA_OPT="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

export HOST=localhost:8080
export HOST_EPG_LOCAL=localhost:8180
export HOME_DIR=/case-management
export USER=Administrator
export PASSWORD=Administrator

curl -fs http://${HOST}/reponses
if [ "$?" -eq "0" ]; then
  BASE_URL="http://${HOST}/reponses"
else
  curl -fs http://${HOST_EPG_LOCAL}/solon-epg
  if [ "$?" -eq "0" ]; then
    BASE_URL="http://${HOST_EPG_LOCAL}/solon-epg"
  else
    BASE_URL="http://${HOST}/solon-epp"
    HOME_DIR=/case-management
  fi
fi
java ${JAVA_OPT} -cp nuxeo-shell-5.4.2-I20110404_0115.jar org.nuxeo.shell.Main ${BASE_URL}/site/automation -u ${USER} -p ${PASSWORD} -d ${HOME_DIR} $@


