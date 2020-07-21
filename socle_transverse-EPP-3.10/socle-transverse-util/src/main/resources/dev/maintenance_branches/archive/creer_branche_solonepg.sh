#!/bin/bash

#
# Usage : creer_branche_solonepg [release_dir]
#
# Script servant à créer une branche de livraison du projet SOLON EPG.
#
# Argument:
#   - release_dir : répertoire de travail de la livraison (facultatif) ex. : ~/release
#
SVN_SERVER="http://lyon-cvs2/dila_solrep"
. ./vars.sh

export app_name="epg"
export app_label="SOLON EPG"
export projects="socle_transverse socle_solrep solon_epg socle_dr socle_cmf"
export app_version="${SOLON_EPG_VERSION}"
export app_distribution="solon-epg-distribution"

# Fonction d'affichage de l'aide
function showHelp {
    head -n 10 $0 | tail -n 8
    exit 0
}

# Affiche l'aide si ce n'est pas le bon nombre de paramètres
if [ $# -gt 1 ] ; then showHelp ; fi

# Détermine le répertoire des branches de livraison
export release_dir="${HOME}/release"
if [ $# -eq 1 ]; then export release_dir="${1}"; fi

# Affiche un message de confirmation
echo "Création d'une nouvelle branche de livraison de l'application ${app_label} ${app_version}"
echo "Répertoire de travail local de la branche : ${release_dir}"
read -p "Êtes-vous sûr? (o/n)" -n 1
if [[ ! $REPLY =~ ^[Oo]$ ]]
then
    exit 1
fi

if [ ! -d "${release_dir}" ]; then mkdir "${release_dir}"; fi

for i in ${projects} ; do ./creer_branche.sh "${i}" ; done

	mkdir -p ${release_dir}/${app_name}/solon_epg/trunk/${app_distribution}/src/main/resources/
	cd ${release_dir}/${app_name}/solon_epg/trunk/${app_distribution}/src/main/resources/
	#on récupère le repertoire de distribution pour vider le dossier a_livrer dans un nouveau dossier avec numero de version		
	svn co ${SVN_SERVER}/solon_epg/trunk/${app_distribution}/src/main/resources/init/
	cd init
	# Modification des repertoires "a_livrer";
		#chemin des repertoires update
	#variable prevention de passage de script à effectuer
	repLdapALivrer="${release_dir}/${app_name}/solon_epg/trunk/${app_distribution}/src/main/resources/init/ldap/update/a_livrer"
	repNxshellALivrer="${release_dir}/${app_name}/solon_epg/trunk/${app_distribution}/src/main/resources/init/nxshell/update/a_livrer"
	repOracleALivrer="${release_dir}/${app_name}/solon_epg/trunk/${app_distribution}/src/main/resources/init/oracle/update/a_livrer"
		#tableau des chemins pour la boucle
	tabChemins[0]=${repLdapALivrer}
	tabChemins[1]=${repNxshellALivrer}
	tabChemins[2]=${repOracleALivrer}
		#si le repertoire "a_livrer" contient des données, 
		#on doit créer un nouveau repertoire avec le numero de version et y déplacer les fichiers
	for chemin in "${tabChemins[@]}"
	do
		if [ -d "${chemin}" ];then
			var=$(ls -a "${chemin}" | sed -e "/\.$/d" | wc -l)
			if [ $var -gt 1 ];then
				svn mv ${chemin} ${chemin}/../${app_version}	
				svn commit -m "Déplacement répertoire a_livrer de ${app_label} vers version ${app_version}"
				mkdir -p ${chemin}
				svn add ${chemin}
				svn commit -m "re-création du répertoire a_livrer de l'application ${app_label} ${app_version}"
			fi
		fi
	done
