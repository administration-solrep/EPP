#!/bin/sh

if [ ! -f ./injection-config.inc ]; then
	echo "ERROR : cannot include injection-config.inc"
	exit
fi
. ./injection-config.inc

kill -9 $(ps -edf | grep java | grep -v -e "grep" -e "nuxeo" | awk -F ' ' '{print $2}')

if [ ! -d "$QUESTIONDIR" ]; then 
	echo "no question dir : $QUESTIONDIR : injection skipped"
	exit 0
fi

echo "-----Download"
rm -f $ZIP
wget "http://${IC_SERVER}/hudson/job/solrep-injection-reponses/lastSuccessfulBuild/artifact/solrep-injection-reponses/target/$ZIP"

echo "-----Unzip"
rm -rf $INJECTDIR
unzip $ZIP



