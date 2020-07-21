#!/bin/bash

# Usage : create_tag_in_workspace [branche] [previous_tag] [tag] [application] [optionnel:repertoire]
#
# Script servant à créer une branche de livraison des applications.
#
# Argument:
#   - branche : le nom de la branche, exemple EPG-3.2
#   - previous_tag : le nom du tag precedent, exemple EPG-3.2.0
#   - tag : le nom du tag, exemple EPG-3.2.1
#   - application : le trigramme de l'application, accepte comme valeur REP, EPG, EPP
#   - répertoire : (optionnel) le répertoire d'où on souhaite créer le tar , par défaut dans ~/workspace/
#
export GIT_SERVER="git@lyovgitlabidl.swordgroup.lan:SOLON"
export branche="${1}"
export previous_tag="${2}"
export tag="${3}"
export application="${4}"
export workspace_dir="${HOME}/workspace"
if [ $# -eq 5 ]; then export workspace_dir="${5}"; fi

# Fonction d'affichage de l'aide
function showHelp {
    head -n 11 $0 | tail -n 9
    exit 0
}


# Affiche l'aide si ce n'est pas le bon nombre de paramètres
if [ $# -gt 5 ] ; then showHelp ; fi
if [ $# -lt 4 ] ; then showHelp ; fi


if [[ "$application" == "REP" ]]; then
	export app_name="reponses"
	export app_label="Réponses"
	export projects="socle_cmf socle_dr socle_transverse socle_solrep reponses"
	export app_distribution="reponses-distribution"
else
	if [[ "$application" = "EPG" ]]; then

		export app_name="solon_epg"
		export app_label="SOLON EPG"
		export projects="socle_cmf socle_dr socle_transverse socle_solrep solon_epg"
		export app_distribution="solon-epg-distribution"
	else
		if [[ "$application" = "EPP" ]]; then
					
			export app_name="solon_epp"
			export app_label="SOLON EPP"
			export projects="socle_cmf socle_dr socle_transverse solon_epp"
			export app_distribution="solon-epp-distribution"
		else
			showHelp
		fi
	fi
fi

# Affiche un message de confirmation
echo "Création d'une nouvelle branche de livraison de l'application ${app_label} ${app_version}"
echo "Répertoire de travail local de la branche : ${workspace_dir}"
read -p "Êtes-vous sûr? (o/n)" -n 1
if [[ ! $REPLY =~ ^[Oo]$ ]]
then
    exit 1
fi

for i in ${projects} ; do ./git_creer_tag.sh "${i}" ; done

exit 0
