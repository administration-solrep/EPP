#!/bin/bash
# (FEV521)
# Ce script permet de générer un fichier ldif dans lequel :
# - les personnes qui n'ont pas le profil "Administrateur Fonctionnel" sont modifiées afin que leur mdp soit identifié comme périmé
# - les personnes qui ont le profil "Administrateur fonctionnel" sont modifiées afin que leur mdp soit initialisé avec la valeur du login suivi d'une chaine de caractère indiquée en paramètre
# Il prend en paramètre :
# 1 - l'hote ou se trouve le LDAP
# 2 - le compte à utiliser pour interroger le LDAP
# 3 - le mot de passe à utiliser pour interroger le LDAP
# 4 - nom du fichier LDIF à générer
# 5 - suffixe pour le mot de passe
## tools nécessaires : base64

if [[ $# != 5 ]]
then 
	echo "La syntaxe correcte est la suivante :"
	echo "ldap-genererLdifEnvironnement 'hote LDAP' 'compte LDAP' ' mot de passe LDAP' 'nom de fichier' 'suffixe'"
	echo " "
	echo "Exemple : sh ldap-script.sh 'localhost' 'cn=nuxeo5,ou=SolonEpp,dc=dila,dc=fr' 'changeme' 'ldapscript.ldif' 'suffixe'"
	exit 1
else 
	echo "Hote LDAP : "$1
	echo "Compte LDAP : "$2
	echo "Mot de passe LDAP : "$3
	echo "Fichier généré : "$4
	echo "Suffixe : "$5
fi

hote=$1
compte=$2
mdp=$3
fichier=$4
suffixe=$5

### Modification des administrateurs fonctionnels
echo "#################################################" >> ${fichier}
echo "# Modification des administrateurs fonctionnels #" >> ${fichier}
echo "#################################################" >> ${fichier}

### Interrogation du LDAP
admins=($(ldapsearch -x -h ${hote} -D ${compte} -w ${mdp} -b "cn=Administrateur fonctionnel,ou=groups,ou=SolonEpp,dc=dila,dc=fr" -s sub -LLL uniqueMember | perl -p00e 's/\r?\n //g' | grep uniqueMember | grep ou=people | perl -p00e 's/uniqueMember:(.*)/$1/g'))

for ((i=0; i < ${#admins[@]}; i++))
do
	### Extraction du login
	login=($( echo ${admins[$i]} | perl -p00e 's/uid=([a-zA-Z0-9\-_]*),(.*)/$1/g'))
	
	### Nouveau mot de passe
	password=${login}${suffixe}
	
	### Ecriture du fichier
	echo "# Utilisateur" ${login} >> ${fichier}
	echo "dn: "${admins[$i]} >> ${fichier}
	echo "changetype: modify" >> ${fichier}
	echo "replace: userPassword" >> ${fichier}
	echo "userPassword: "${password} >> ${fichier}
	echo "" >> ${fichier}
done

### Modification des non-administrateurs fonctionnels
echo "######################################################" >> ${fichier}
echo "# Modification des non- administrateurs fonctionnels #" >> ${fichier}
echo "######################################################" >> ${fichier}

SAVEIFS=$IFS
IFS=$'\n'
allusers=($(ldapsearch -x -h ${hote} -D ${compte} -w ${mdp} -b "ou=SolonEpp,dc=dila,dc=fr" -s sub "objectclass=personne" dn -LLL | grep dn | perl -p00e 's/(.*):(.*)/$2/g'))

for ((i=0; i < ${#allusers[@]}; i++))
do
	user=${allusers[$i]}
	if ! [[ ${admins[*]} =~ ${user} ]]
	then
		# L'utilisateur n'est pas un admin fonctionnel, le mot de passe est identifié comme périmé
		echo "# Utilisateur" ${user} >> ${fichier}
		if [[ $user == " uid="* ]]
		then
			echo "dn: "${user} >> ${fichier}
		else
			echo "dn:: "${user} >> ${fichier}
		fi
		echo "changetype: modify" >> ${fichier}
		echo "replace: passwordReset" >> ${fichier}
		echo "passwordReset: TRUE" >> ${fichier}
		echo "" >> ${fichier}
	fi 
done

IFS=$SAVEIFS
echo "Fin du traitement"
