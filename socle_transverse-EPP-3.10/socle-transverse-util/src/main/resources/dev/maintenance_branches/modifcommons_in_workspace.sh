#!/bin/bash
REMPLACEMENT=${1:?"${1} doit être définie comme le numéro de version"}
VALEUR_A_REMPLACER=${2:?"${2} doit être l'ancien numéro de version"}
for COMMONS_FILE in `find ${workspace_dir}/${project}/* $version -name commons.xsd`
do
	echo "Mise à jour du commons.xsd ${COMMONS_FILE} à la version ${REMPLACEMENT}"
	sed "s/$VALEUR_A_REMPLACER/$REMPLACEMENT/g" -i $COMMONS_FILE
done

