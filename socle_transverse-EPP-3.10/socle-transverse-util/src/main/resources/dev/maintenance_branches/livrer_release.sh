#!/bin/bash

# Usage : livrer_release [old_version] [new_version] [application] [répertoire (optionnel)]
#
# Script servant à créer une branche de livraison des applications.
#
# Argument:
#   - old_version : le chiffre de la version ex 1.0.0-SNAPSHOT ou rep-2.0.0
#   - new_version : le chiffre de la version ex 2.0.0
#   - application : le trigramme de l'application, accepte comme valeur REP, EPG, EPP
#   - répertoire : (optionnel) le répertoire où on souhaite tirer la branche 
#
export GIT_SERVER="git@lyovgitlabidl.swordgroup.lan:SOLON"
export app_new_version="${3}-${2}"


# Fonction d'affichage de l'aide
function showHelp {
    head -n 11 $0 | tail -n 9
    exit 0
}

# Affiche l'aide si ce n'est pas le bon nombre de paramètres
if [ $# -gt 4 ] ; then showHelp ; fi
if [ $# -lt 3 ] ; then showHelp ; fi

# Détermine le répertoire des branches de livraison
export release_dir="${HOME}/release"
if [ $# -eq 4 ]; then export release_dir="${4}"; fi

if [[ "${3}" == "REP" ]]; then
	export app_name="reponses"
	export app_label="Réponses"
	export projects="socle_cmf socle_dr socle_transverse socle_solrep reponses"
	export app_distribution="reponses-distribution"
else
	if [[ "${3}" = "EPG" ]]; then

		export app_name="solon_epg"
		export app_label="SOLON EPG"
		export projects="socle_cmf socle_dr socle_transverse socle_solrep solon_epg"
		export app_distribution="solon-epg-distribution"
	else
		if [[ "${3}" = "EPP" ]]; then
					
			export app_name="solon_epp"
			export app_label="SOLON EPP"
			export projects="socle_cmf socle_dr socle_transverse solon_epp"
			export app_distribution="solon-epp-distribution"
		else
			showHelp
		fi
	fi
fi

if [[ "${1}" != *"${3}"* && "${1}" != *"SNAPSHOT"* ]]
then
	export app_old_version="${3}-${1}"
else
	export app_old_version="${1}"
fi

# Affiche un message de confirmation
echo "Création d'une nouvelle branche de livraison de l'application ${app_label} ${app_version}"
echo "Répertoire de travail local de la branche : ${release_dir}"
read -p "Êtes-vous sûr? (o/n)" -n 1
if [[ ! $REPLY =~ ^[Oo]$ ]]
then
    exit 1
fi

if [ ! -d "${release_dir}" ]; then mkdir "${release_dir}"; fi

for i in ${projects} ; do ./git_creer_branche.sh "${i}" ; done

exit 0
