#!/bin/bash

BASEURL=http://localhost:8080/solon-epp
URL=$BASEURL/site/automation
JAR=$(find . -name "nuxeo-shell-*.jar")

USER=Administrator
PASSWORD=$USER
LOCATION=


CMDOPTS="-u $USER -p $PASSWORD"
[ -n "$LOCATION" ] && CMDOPTS="$CMDOPTS -d $LOCATION"

JAVA=$JAVA_HOME/bin/java

echo $JAVA -cp $JAR org.nuxeo.shell.Main $URL $CMDOPTS

$JAVA -cp $JAR org.nuxeo.shell.Main $URL $CMDOPTS $@

