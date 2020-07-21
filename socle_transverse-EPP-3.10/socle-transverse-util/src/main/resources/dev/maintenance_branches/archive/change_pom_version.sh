#!/bin/bash

#Modifie les version dans les pom suite a la creation
#d'une branche
# organisation des fichier
# <base dir> /
#    reponses /
#         <version> /
#    socle_transverse /
#         <version> /
# Suite au branchement la version attendu est 1.0.0-SNAPSHOT
# les valeurs '1.0.0-SNAPSHOT' seront remplace par version
# Exemple de la valeur pour version 1.4.6
#

SVN_SERVER="http://lyon-cvs2/dila_solrep/"
PROJECT_LIST=("reponses" "socle_transverse")

if [ $# -ne 2 ]; then
  echo "Usage : <base dir> <version>"
  echo "  will process pom in "
  
  for PROJECT in ${PROJECT_LIST[*]}; do
    echo "    <base dir>/$PROJECT/<version>/*"
  done
  exit
fi

WORKING_DIR=$1
VERSION=$2

for PROJECT in ${PROJECT_LIST[*]}; do
  curdir=$WORKING_DIR/$PROJECT/$VERSION/
  echo "Process [$curdir]"
  for f in $(find $curdir -name "pom.xml"); do
    sed -i "s/1.0.0-SNAPSHOT/$VERSION/" $f
  done
done

