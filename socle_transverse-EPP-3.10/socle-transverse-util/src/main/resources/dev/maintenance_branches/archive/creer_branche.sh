#checkout trunk sources, copy to branch and checkout from app_version branch 
# param <project> 
#EXPORTS à effectuer dans le script appelant 
#app_name
#app_label
#app_version
#app_distribution
#release_dir

SVN_SERVER="http://lyon-cvs2/dila_solrep"

export changepom="`pwd`/modifpom.sh"
export changecommons="`pwd`/modifcommons.sh"

# Crée les projets
function create_project {
	local ROUGE='\033[1;31m' 
	local NEUTRE='\e[0;m'
    local project=$1
    
    # Création de la branche
    mkdir -p ${release_dir}/${app_name}/${project}
    cd ${release_dir}/${app_name}/${project}
    svn --parents copy ${SVN_SERVER}/${project}/trunk ${SVN_SERVER}/${project}/branches/${app_name}/${app_version} -m "Création de la branche release ${app_label} ${app_version}"
    svn co ${SVN_SERVER}/${project}/branches/${app_name}/${app_version}
	
	# Modification des repertoires "a_livrer";
		#chemin des repertoires update
	#variable prevention de passage de script à effectuer
	message="PAS DE SCRIPTS D'UPDATE A PASSER"
	scripts[0]=""
	if [ -e "${release_dir}/${app_name}/${project}/${app_version}/${app_distribution}" ];then		
		repLdapALivrer="${release_dir}/${app_name}/${project}/${app_version}/${app_distribution}/src/main/resources/init/ldap/update/a_livrer"
		repNxshellALivrer="${release_dir}/${app_name}/${project}/${app_version}/${app_distribution}/src/main/resources/init/nxshell/update/a_livrer"
		repOracleALivrer="${release_dir}/${app_name}/${project}/${app_version}/${app_distribution}/src/main/resources/init/oracle/update/a_livrer"
			#tableau des chemins pour la boucle
		tabChemins[0]=${repLdapALivrer}
		tabChemins[1]=${repNxshellALivrer}
		tabChemins[2]=${repOracleALivrer}
			#si le repertoire "a_livrer" contient des données, 
			#on doit créer un nouveau repertoire avec le numero de app_version et y déplacer les fichiers
		cpt=0;
		for chemin in "${tabChemins[@]}"
		do
			if [ -d "${chemin}" ];then
				var=$(ls -a "${chemin}" | sed -e "/\.$/d" | wc -l)
				if [ $var -gt 1 ];then
					message="IL Y A UN OU PLUSIEURS SCRIPTS A PASSER"
					svn mv ${chemin} ${chemin}/../${app_version}	
					svn commit -m "Déplacement répertoire a_livrer de ${app_label} vers version ${app_version}"
					mkdir -p ${chemin}
					svn add ${chemin}
					svn commit -m "re-création du répertoire a_livrer de l'application ${app_label} ${app_version}"
					if [ $cpt -eq 0 ];then
						scripts[0]="LDAP"
					elif [ $cpt -eq 1 ];then
						scripts[1]="NXSHELL"
					elif [ $cpt -eq 2 ]; then
						scripts[2]="ORACLE"
					fi
				fi
			fi
			cpt=$((${cpt} + 1))
		done
	fi
	

    # Modification du commons.xsd
    pushd .
    cd ${app_version}
    ${changecommons} ${app_version}
    svn commit -m "Mise à jour du commons.xsd de l'application ${app_label} ${project} ${app_version}"
    popd

    # Modification du POM
    pushd .
    cd ${app_version}
    ${changepom} ${app_version}
    svn commit -m "Mise à jour du POM de l'application ${app_label} ${project} ${app_version}"
    popd
    
	echo -e "$ROUGE" "===============" "$NEUTRE" "$message" "$ROUGE" "==============="
	echo -e "${scripts[*]}" "$NEUTRE"
}

create_project "${1}"

