#checkout trunk sources, copy to branch and checkout from app_new_version branch 
# param <project> 
#EXPORTS à effectuer dans le script appelant 
#app_name
#app_label
#app_new_version
#app_distribution
#release_dir

export changepom="`pwd`/modifpom.sh"
export changecommons="`pwd`/modifcommons.sh"

# Crée les projets
function create_project {
	local ROUGE='\033[1;31m' 
	local NEUTRE='\e[0;m'
    local project=$1
    
    # Création de la branche
    mkdir -p ${release_dir}/${app_new_version}/${app_name}/
    cd ${release_dir}/${app_new_version}/${app_name}
    git clone ${GIT_SERVER}/${project}.git
    cd ${project}
    if [[ $app_old_version != *"SNAPSHOT"* ]]
    then
	branch_version=${app_old_version}
        git checkout ${app_old_version}
    else
	branch_version=$(echo ${app_new_version} | sed 's/\.[0-9]//2')
	git checkout -b ${branch_version}
    fi
	
	# Modification des repertoires "a_livrer";
		#chemin des repertoires update
	#variable prevention de passage de script à effectuer
	message="PAS DE SCRIPTS D'UPDATE A PASSER"
	scripts[0]=""
	if [ -e "${release_dir}/${app_new_version}/${app_name}/${app_name}/${app_distribution}" ];then		
		repLdapALivrer="${release_dir}/${app_new_version}/${app_name}/${app_name}/${app_distribution}/src/main/resources/init/ldap/update/a_livrer"
		repNxshellALivrer="${release_dir}/${app_new_version}/${app_name}/${app_name}/${app_distribution}/src/main/resources/init/nxshell/update/a_livrer"
		repOracleALivrer="${release_dir}/${app_new_version}/${app_name}/${app_name}/${app_distribution}/src/main/resources/init/oracle/update/a_livrer"
			#tableau des chemins pour la boucle
		tabChemins[0]=${repLdapALivrer}
		tabChemins[1]=${repNxshellALivrer}
		tabChemins[2]=${repOracleALivrer}
			#si le repertoire "a_livrer" contient des données, 
			#on doit créer un nouveau repertoire avec le numero de app_new_version et y déplacer les fichiers
		cpt=0;
		for chemin in "${tabChemins[@]}"
		do
			if [ -d "${chemin}" ];then
				var=$(ls -a "${chemin}" | sed -e "/\.$/d" | wc -l)
				if [ $var -gt 0 ];then
					message="IL Y A UN OU PLUSIEURS SCRIPTS A PASSER"
					mv ${chemin} ${chemin}/../${app_new_version}	
					git commit -am "Déplacement répertoire a_livrer de ${app_label} vers version ${app_new_version}"
					mkdir -p ${chemin}
					git add ${chemin}
					nouveauChemin=${chemin%a_livrer}
					nouveauChemin=$nouveauChemin${app_new_version}
					git add ${nouveauChemin}
					git commit -m "re-création du répertoire a_livrer de l'application ${app_label} ${app_new_version}"
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
    cd ${release_dir}/${app_new_version}/${app_name}/${project}
    ${changecommons} ${app_new_version} ${app_old_version}
    git commit -am "Mise à jour du commons.xsd de l'application ${app_label} ${project} ${app_new_version}"
    popd

    # Modification du POM
    pushd .
    cd ${release_dir}/${app_new_version}/${app_name}/${project}
    ${changepom} ${app_new_version} ${app_old_version}
    git commit -am "Mise à jour du POM de l'application ${app_label} ${project} ${app_new_version}"
    popd
    
	echo -e "$ROUGE" "===============" "$NEUTRE" "$message" "$ROUGE" "==============="
	echo -e "${scripts[*]}" "$NEUTRE"

        git tag ${app_new_version}
   
    if [[ $app_old_version != *"SNAPSHOT"* ]]
    then
	echo " git push origin ${app_old_version}"
    else
	echo " git push origin ${app_new_version}"
    fi
    echo " git push origin ${branch_version}"
}

create_project "${1}"

