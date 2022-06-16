#!/bin/sh

#########################################
# Fix du serveur Nuxeo pour la phase de
# développement.
#########################################


function add_template {
	template="$1"
	conf_file="$2"

	sed -i -r "s/^#?nuxeo.templates=(.+)$/nuxeo.templates=\1,$template/" "$conf_file"
	sed -i -r "s/^#?nuxeo.templates=$/nuxeo.templates=$template/" "$conf_file"
	
	echo '[INFO] Template added : ' "$1"
}


if [ $# -lt 1 ]; then
	echo "Usage: $0 nuxeo_server_root" 1>&2
	exit 1
fi

NXS_ROOT=${1%%/}


if [ ! -w $NXS_ROOT/bin/nuxeo-sdk.conf ]; then
	echo "$NXS_ROOT/bin/nuxeo-sdk.conf does not exist or is not writable." 1>&2
	echo "The server must be added in Nuxeo IDE first." 1>&2
	exit 2
fi

# activation de facelets.REFRESH_PERIOD pour le refresh des .xhtml
echo -e "\nfacelets.REFRESH_PERIOD=2" >>$NXS_ROOT/bin/nuxeo-sdk.conf

# activation du serveur de debug sur le port 8787
sed -i -r 's/^#(.*address=8787.*)$/\1/' $NXS_ROOT/bin/nuxeo-sdk.conf

# ajout des templates (tous les paramètres apres le premier sont consideres comme des templates)
for param in "$@"
do
	if [ "$param" != "$1" ]; then
		add_template $param $NXS_ROOT/bin/nuxeo-sdk.conf
	fi
done

# création du répertoire WEB-INF/dev et suppression de son contenu pour le reload des bean
mkdir -p $NXS_ROOT/nxserver/nuxeo.war/WEB-INF/dev
rm -rf $NXS_ROOT/nxserver/nuxeo.war/WEB-INF/dev/*

# liste des plugins installés
if [ `ls -l $NXS_ROOT/nxserver/plugins/*.jar | wc -l` -gt 1 ]; then
        echo '[INFO] Installed plugins:'
        for f in $NXS_ROOT/nxserver/plugins/*.jar; do
	        echo '[INFO]  ' `basename $f`
	done
fi

exit 0
