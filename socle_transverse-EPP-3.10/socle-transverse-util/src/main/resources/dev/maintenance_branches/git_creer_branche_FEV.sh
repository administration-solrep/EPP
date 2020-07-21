#!/bin/bash

# Usage : git_creer_branche_FEV [application] [nom de la branche]
#
# Script servant à créer une branche de livraison des applications.
#
# Argument:
#   - application : le trigramme de l'application, accepte comme valeur rep, epg, epp
#   - branche_name : le nom de la nouvelle branche à créer : exemple FEV422

export GIT_SERVER="git@lyovgitlabidl.swordgroup.lan:SOLON"


# Fonction d'affichage de l'aide
function showHelp {
    head -n 9 $0 | tail -n 7
    exit 0
}

# Affiche l'aide si ce n'est pas le bon nombre de paramètres
if [ $# -gt 2 ] ; then showHelp ; fi
if [ $# -lt 2 ] ; then showHelp ; fi

# Pour l'application indiquée on regarde quels sont les projets qui sont concernés
if [[ "${1}" == "rep" ]]; then
	app_name="reponses"
	app_label="Réponses"
	projects="socle_cmf socle_dr socle_transverse socle_solrep reponses"
else
	if [[ "${1}" = "epg" ]]; then

		app_name="solon_epg"
		app_label="SOLON EPG"
		projects="socle_cmf socle_dr socle_transverse socle_solrep solon_epg"
	else
		if [[ "${1}" = "epp" ]]; then
					
			app_name="solon_epp"
			app_label="SOLON EPP"
			projects="socle_cmf socle_dr socle_transverse solon_epp"
		else
			showHelp
		fi
	fi
fi

for i in ${projects}
do

	# Affiche un message de confirmation
	echo -e "\nCréation d'une nouvelle branche ${branch_version} de l'application ${i}\n"
	read -p "Êtes-vous sûr? (o/n)" -n 1
	if [[ ! $REPLY =~ ^[Oo]$ ]]
	then
	    exit 1
	fi

	# Création de la branche

	cd $DILA_WS/${i}

	branch_version=$(echo ${2})
	git branch ${branch_version}
	git checkout ${branch_version}

	echo -e "checkout de la nouvelle branche ${branch_version} effectué\n"

	git push origin ${branch_version}

	echo -e "La branche ${2} a été créé pour le projet ${i}\n"

done



