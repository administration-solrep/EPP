#!/bin/sh

#########################################
# Fix un projet Eclipse en forçant la
# compilation de src/main/seam dans
# bin/seam.
#
# Après avoir regénéré le projet Eclipse
# via Maven et clean le projet, lancer
# le script puis rafraichir le projet
# dans Eclipse (et vérifier que le
# projet a bien été rebuild sans les
# beans).
#########################################


if [ $# -lt 1 ]; then
	echo "Usage: $0 eclipse_project" 1>&2
	exit 1
fi

ECLIPSE_CLASSPATH_FILE="${1%%/}/.classpath"


if [ `grep '<classpathentry kind="src" path="src/main/seam" including="\*\*/\*.java"/>' $ECLIPSE_CLASSPATH_FILE | wc -l` -lt 1 ]; then
	echo "$ECLIPSE_CLASSPATH_FILE does not seem to reference folder src/main/seam." 1>&2
	echo "Try mvn clean eclipse:clean eclipse:eclipse." 1>&2
	exit 2
fi

sed -i 's/<classpathentry kind="src" path="src\/main\/seam" including="\*\*\/\*.java"\/>/<classpathentry kind="src" output="bin\/seam" path="src\/main\/seam"\/>/' $ECLIPSE_CLASSPATH_FILE

exit 0
