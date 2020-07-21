#!/bin/bash
CHAINE_REMPLACEE=${1:?"${1} doit être définie comme l'ancien numéro de version"}
REMPLACEMENT=${2:?"${2} doit être définie comme le nouveau numéro de version"}
for POM_FILE in `find $new_version -name pom.xml`
do
	echo "Mise à jour du POM ${POM_FILE} à la version ${REMPLACEMENT}"
	sed "s/$CHAINE_REMPLACEE/$REMPLACEMENT/g" -i $POM_FILE
done

