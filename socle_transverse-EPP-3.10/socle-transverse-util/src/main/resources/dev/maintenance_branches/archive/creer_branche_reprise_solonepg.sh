#!/bin/bash

#
# Usage : creer_branche_reprise_solonepg [release_dir]
#
# Script servant à créer une branche de livraison du projet reprise, concernant l'injecteur SOLON EPG.
#
# Argument:
#   - release_dir : répertoire de travail de la livraison (facultatif) ex. : ~/release
#

. ./vars.sh

export app_name="epg"
export app_label="reprise SOLON EPG"
export projects="reprise"
export app_version="${REPRISE_SOLON_EPG_VERSION}"

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

for i in ${projects} ; do ./creer_branche.sh "${i}" "${app_version}" "${release_dir}" "${app_name}" "${app_label}"; done
