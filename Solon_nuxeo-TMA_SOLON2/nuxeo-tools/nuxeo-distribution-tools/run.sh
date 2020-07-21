# This tool is building and launching a nuxeo core server given a home directory and a profile 
#!/bin/sh

NXHOME=/tmp/nxserver
JAVA_ARGS=-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n
CLASSPATH=.:target/nuxeo-distribution-tools-0.7-SNAPSHOT-all.jar
# if you want the log to be enabled put in classpath slf4j-log4j12-1.5.6.jar

java $JAVA_ARGS -cp $CLASSPATH org.nuxeo.dev.Main $NXHOME $@  

