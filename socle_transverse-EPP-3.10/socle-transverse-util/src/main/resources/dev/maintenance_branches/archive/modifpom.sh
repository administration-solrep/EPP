#!/bin/bash
REMPLACEMENT=${1:?"${1} doit être définie comme le numéro de version"}
for POM_FILE in `find $version -name pom.xml`
do
	echo "Mise à jour du POM ${POM_FILE} à la version ${REMPLACEMENT}"
	sed "s/[0-9].[0-9].[0-9]-\(RELEASE\|SNAPSHOT\)/$REMPLACEMENT/g" -i $POM_FILE
done

