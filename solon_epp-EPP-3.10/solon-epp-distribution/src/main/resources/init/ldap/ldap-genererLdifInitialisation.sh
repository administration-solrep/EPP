#!/bin/bash
# Ce script permet de générer un fichier ldif (préfixé "final_") à partir d'un premier
# Dans ce nouveau fichier, les mots de passe et les adresses mail sont modifiés pour correspondre aux paramètres
# Il prend en paramètre :
# 1 - le fichier ldif à modifier : il doit se trouver dans le même répertoire
# 2 - l'extension mail à ajouter
# 3 - le mot de passe [optionnel]
## tools nécessaires : base64

if [[ $# != 3 ]]
then 
	if [[ $# != 2 ]]
	then 
		echo "La syntaxe correcte est la suivante :"
		echo "ldap-genererLdifInitialisation.sh 'nom fichier.ldif' 'extension mail' 'mot de passe' ou "
		echo "ldap-genererLdifInitialisation.sh 'nom fichier.ldif' 'extension mail'"
		echo " "
		echo "Exemple : sh ldap-genererLdifInitialisation.sh 'reponses.ldif' 'reponses.com' 'secret'"
		exit 1
	else 
		echo "Entrez un mot de passe pour tous les utilisateurs :"
		read -r mdp
	fi
else
	mdp=$3
fi

fichier="final_"$1
# Encodage du mot de passe en base64
encodedMdp=$(echo -n ${mdp} | base64)


### Boucle principale de lecture du fichier
# Gestion des champs
mailOK=0
pswdOK=0
toPrint=0
# Pour la gestion des lignes vides
wasComment=1
# Pour la gestion des champs sur 2 lignes
lastLineWasMail=0
lastLineWasPswd=0
uid="###"
old_IFS=$IFS
IFS=$'\n'
for ligne in $(cat $1)
do
	toPrint=1
	# Si la derniere ligne était un pswd ou un mail, on vérifie qu'il n'était pas sur 2 lignes
	if [ ${lastLineWasMail} -eq 1 ] || [ ${lastLineWasPswd} -eq 1 ]
	then
		if [ ${ligne:0:1} = " " ] || [ ${ligne:0:1} = "\t" ]
		then
			toPrint=0
		else
			lastLineWasMail=0
			lastLineWasPswd=0
		fi
	fi
	# Permet de trouver les commentaires et d'ajouter des espaces
	if [ ${ligne:0:1} = "#" ]
	then
		if [ ${wasComment} -eq 0 ]
		then
			echo    >> "${fichier}"
			wasComment=1
		fi
		# En plus, on réinitialise les paramètres sachant que les commentaires marquent le début d'une nouvelle entrée
		uid="###"
		mailOK=0
		pswdOK=0
	elif [ ${wasComment} -eq 1 ]
	then
		wasComment=0
	fi
	if [ "${uid}" = "###" ]
	then
		# Dans ce cas on cherche un nouvel uid
		result=$(echo "${ligne}" | grep "dn: uid=")
		if [ ${#result} -gt 2 ]
		then
			# Récupération de l'uid sauf pour Administrator
			uid=$(echo "${result#dn:*=}")
			uid=$(echo "${uid%%,*}")
			if [ ${uid} = "Administrator" ]
			then
				uid="###"
			fi
		fi
	else
		# Un uid est renseigne, il faut chercher un champ mail et password
		result=$(echo "${ligne}" | grep "mail: ")
		if [ ${#result} -gt 2 ]
		then
			# formatage du champ
			echo "mail: ${uid}@$2" >> "${fichier}"
			mailOK=1
			toPrint=0
			lastLineWasMail=1
		fi
		result=$(echo "${ligne}" | grep "userPassword:: ")
                if [ ${#result} -gt 2 ]
                then
                        # formatage du champ
                        echo "userPassword:: ${encodedMdp}" >> "${fichier}"
                	pswdOK=1
			toPrint=0
			lastLineWasPswd=1
		fi
	fi
	if [ ${mailOK} -eq 1 ] && [ ${pswdOK} -eq 1 ]
	then
		mailOK=0
		pswdOK=0
		uid="###"
	fi
	if [ ${toPrint} -eq 1 ]
	then
		echo "${ligne}" >> "${fichier}"
	fi
done
IFS=$old_IFS


echo "Fin du traitement"
