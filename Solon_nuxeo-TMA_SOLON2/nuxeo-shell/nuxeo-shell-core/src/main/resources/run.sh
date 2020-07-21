#!/bin/sh

#JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

java ${JAVA_OPTS} -cp $(dirname $0) org.nuxeo.shell.Main $@
