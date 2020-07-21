#!/bin/bash

#
# Usage : checkConnection.sh addr1 [addr2] [addr3]...
#
# Script pour lancer un netstat sur la ou les adresses indiquées 
#
#

# les flags existants recherchés dans le netstat
FLAGS="CLOSE_WAIT ESTABLISHED TIME_WAIT LISTEN LAST_ACK FIN_WAIT2 SYN_SENT SYN_RECV FIN_WAIT1 CLOSED CLOSING UNKNOWN"
# séparateur pour les informations contenues dans le fichier
SEP=";"
# le nom du fichier qui sera créé en sortie de script 
FILE="info_netstat.csv"
# le temps de sleep en secondes entre chaque requête netstat
SLEEP_TIME=30

# 
# Usage : checkServer.sh addr strNetstat
#
# Vérifie pour une adresse donnée les flags présents 
#
# argument :
#   - addr : l'adresse du serveur visé
#   - strNetstat : le résultat du netstat effectué 
function checkServer
{
    echo -n "ALL$SEP" >> $FILE
    echo -n "$2" | grep $1 | grep -v grep | wc -l | tr -d "\n" >> $FILE 
    echo -n "$SEP" >> $FILE
    for flag in $FLAGS; do
        checkFlag $1 $flag "$2";
    done
}

# 
# Usage : checkFlag.sh addr flag strNetstat
#
# Vérifie pour une adresse donnée et un flag donné, le nombre de ligne existante 
#
# argument :
#   - addr : l'adresse du serveur visé
#   - flag : le flag recherché
#   - strNetstat : le résultat du netstat effectué 
function checkFlag
{
    echo -n "$2$SEP" >> $FILE
    echo -n "$3" | grep $1 | grep -v grep | grep $2 | wc -l | tr -d "\n" >> $FILE 
    echo -n "$SEP" >> $FILE
}

function showHelp {
    head -n 8 $0 | tail -n 8
    exit 0
}

# Affiche l'aide s'il n'y a pas de paramètres
if [ $# -eq 0 ] ; then showHelp ; fi

echo "**************Lancement du script**************" > $FILE
while [ true ]; do
    netstat=$(netstat -laputen 2>/dev/null)
    for addr in $@; do
        echo -n "$addr$SEP" >> $FILE
        echo -n `date +%Y-%m-%d%t%T`$SEP >> $FILE
        checkServer $addr "$netstat"
        echo >> $FILE;
    done
    sleep $SLEEP_TIME;
done

