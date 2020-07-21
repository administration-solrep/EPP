#!/bin/sh

if [ ! -f ./injection-config.inc ]; then
        echo "ERROR : cannot include injection-config.inc"
        exit
fi
. ./injection-config.inc


if [ ! -d "$QUESTIONDIR" ]; then 
	echo "no question dir : $QUESTIONDIR : injection skipped"
	exit 0
fi

# SPL : ne pas mettre a jour 
# le nouvel injecteur n'est pas compatible avec le LDAP de dev
#./injection-deploy.sh

echo "arret du process de la base local eventuelle d'une injection  precedente"

# tentative d'arret si provient du meme injecteur
sh ./manip_db.sh stop

# si l'arret precedent n'a pas suffit
dbpid=$(ps -edf | grep "java .*injectiondb" | grep -v color | awk -F ' ' '{print $2}')
if [ -n "$dbpid" ]; then
  kill -9 $dbpid
fi

# deplacement du repertoire pour recommencer une injection 
# sur une base locale vide
if [ -d "./data" ]; then
  mv data data_$(date +%Y%m%d%H%M%S)
fi

echo "-----Feuille de route"
currentdir=$(pwd)
cd $INJECTDIR
sh ./manip_db.sh start
sh ./fdrCreate.sh $URL
sh ./manip_db.sh stop
sleep 2
sh ./manip_db.sh clear

cd $currentdir

echo "-----Injection des questions"
./injection-injectdir.sh "$QUESTIONDIR"

