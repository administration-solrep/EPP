#!/bin/bash
REMPLACEMENT=${1:?"${1} doit être définie comme le numéro de version"}
VALEUR_A_REMPLACER=${2:?"${2} doit être l'ancien numéro de version"}
for POM_FILE in `find ${release_dir}/${app_new_version}/${app_name}/ $version -name pom.xml`
do
	echo "Mise à jour du POM ${POM_FILE} à la version ${REMPLACEMENT}"
	sed "s/$VALEUR_A_REMPLACER/$REMPLACEMENT/g" -i $POM_FILE
done

