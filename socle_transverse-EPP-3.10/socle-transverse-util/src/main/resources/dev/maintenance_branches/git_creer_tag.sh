#checkout trunk sources, copy to branch and checkout from tag branch 
# param <project> 
#EXPORTS à effectuer dans le script appelant 
#app_name
#app_label
#tag
#app_distribution
#workspace_dir

export changepom="`pwd`/modifpom_in_workspace.sh"
export changecommons="`pwd`/modifcommons_in_workspace.sh"
export project="${1}"

# Crée les projets
function create_project {
	local ROUGE='\033[1;31m' 
	local NEUTRE='\e[0;m'
    local currentproject=$1

    echo "cd ${workspace_dir}/${currentproject}"
    cd ${workspace_dir}/${currentproject}
    #Switch sur la branche
    git checkout ${branche}
    git pull
   	
    # Modification du commons.xsd
    pushd .
    echo "cd ${workspace_dir}/${currentproject}"
    cd ${workspace_dir}/${currentproject}
    ${changecommons} ${tag} ${previous_tag}
    git commit -am "Mise à jour du commons.xsd de l'application ${project} ${tag}"
    popd

    # Modification du POM
    pushd .
 	echo "cd ${workspace_dir}/${currentproject}"
    cd ${workspace_dir}/${currentproject}
    ${changepom} ${tag} ${previous_tag}
    git commit -am "Mise à jour du POM de l'application ${project} ${tag}"
    popd
    
	echo -e "$ROUGE" "===============" "$NEUTRE" "$message" "$ROUGE" "==============="
	echo -e "${scripts[*]}" "$NEUTRE"

    cd ${workspace_dir}/${currentproject}
    echo "Push des commits modifs commons & modif poms"
    git push
    echo "Mise en place du tag ${tag} sur ${workspace_dir}/${currentproject}"
    git tag -a ${tag} -m "Tag de l'application en ${tag}"
    git push --tags
 
#    echo ' pour push les modifification, executez les commandes suivantes : '
#    echo 'git tag -a ${tag} -m "Tag de l application en ${tag}"'
#    echo 'git push --tags'
}

create_project "${1}"

