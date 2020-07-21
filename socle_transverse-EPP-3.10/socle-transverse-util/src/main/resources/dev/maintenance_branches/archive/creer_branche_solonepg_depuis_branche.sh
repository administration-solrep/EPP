#!/bin/bash

#
# Usage : creer_branche_solonepg_depuis_branche <old_version_branch> <new_version_branch> [release_dir]
#
# Script servant à créer une branche de livraison du projet SOLON EPG à partir d'une autre branche.
#
# Argument:
#	- old_version_branch : ancien numero de version de la branche qu'on souhaite copier (obligatoire) ex. : 1.6.14.2
#	- new_version_branch : nouveau numero de version de la branche (obligatoire) ex. : 1.6.14.3
#   - release_dir : répertoire de travail de la livraison (facultatif) ex. : ~/release
#

export app_name="epg"
export app_label="SOLON EPG"
export projects="socle_transverse socle_solrep solon_epg socle_dr socle_cmf"
export app_old_version="${1}"
export app_new_version="${2}"

# Fonction d'affichage de l'aide
function showHelp {
    head -n 10 $0 | tail -n 8
    exit 0
}

# Affiche l'aide si ce n'est pas le bon nombre de paramètres
if [ $# -gt 3 ] ; then showHelp ; fi
if [ $# -lt 2 ] ; then showHelp ; fi

# Détermine le répertoire des branches de livraison
export release_dir="${HOME}/release"
if [ $# -eq 3 ]; then export release_dir="${3}"; fi

# Affiche un message de confirmation
echo "Création d'une nouvelle branche de livraison de l'application ${app_label} avec numero de version : ${app_new_version} à partir de la version : ${app_old_version}"
echo "Répertoire de travail local de la branche : ${release_dir}"
read -p "Êtes-vous sûr? (o/n)" -n 1
if [[ ! $REPLY =~ ^[Oo]$ ]]
then
    exit 1
fi

if [ ! -d "${release_dir}" ]; then mkdir "${release_dir}"; fi

for i in ${projects} ; do ./creer_branche_depuis_branche.sh "$i" ; done
