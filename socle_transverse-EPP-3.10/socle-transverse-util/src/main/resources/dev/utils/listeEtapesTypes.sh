#!/bin/sh

#
# Commande qui s'appuie sur ack http://betterthangrep.com et qui liste tous les types d'étapes d'un répertoire
# de dossier EPG

# Mode d'emploi :
# - Déposer une copie de ack dans le repertoire personnel
# - Exécuter la commande dans le répertoire data
# 

~/ack -o -h --no-group -G .*FDR.xml '<ACTIVITE>.*</ACTIVITE' | sort -u| uniq > type_etapes.txt