#!/bin/bash
REMPLACEMENT=${1:?"${1} doit être définie comme le numéro de version"}
for COMMONS_FILE in `find $version -name commons.xsd`
do
	echo "Mise à jour du commons.xsd ${COMMONS_FILE} à la version ${REMPLACEMENT}"
	sed "s/[0-9].[0-9].[0-9]-\(RELEASE\|SNAPSHOT\)/$REMPLACEMENT/g" -i $COMMONS_FILE
done

