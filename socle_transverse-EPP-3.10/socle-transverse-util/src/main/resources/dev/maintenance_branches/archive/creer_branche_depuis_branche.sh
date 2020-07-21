#checkout old branch sources, copy to new branch and checkout from new version branch 
# param <project> <app_old_version_branch> <project_release_dir> <app_name> <app_label> <app_new_version_branch>
SVN_SERVER="http://lyon-cvs2/dila_solrep"

export changepom="`pwd`/modifpom_branche.sh"
export changecommons="`pwd`/modifcommons_branche.sh"

# Crée les projets
function create_project {
    local project=$1

    # Création de la branche
    mkdir -p ${release_dir}/${app_name}/${project}
    cd ${release_dir}/${app_name}/${project}
    svn --parents copy ${SVN_SERVER}/${project}/branches/${app_name}/${app_old_version} ${SVN_SERVER}/${project}/branches/${app_name}/${app_new_version} -m "Création de la branche release ${app_label} ${app_new_version}"
    svn co ${SVN_SERVER}/${project}/branches/${app_name}/${app_new_version}

    # Modification du commons.xsd
    pushd .
    cd ${app_new_version}
    ${changecommons} ${app_old_version} ${app_new_version}
    svn commit -m "Mise à jour du commons.xsd de l'application ${app_label} ${project} ${app_new_version}"
    popd

    # Modification du POM
    pushd .
    cd ${app_new_version}
    ${changepom} ${app_old_version} ${app_new_version}
    svn commit -m "Mise à jour du POM de l'application ${app_label} ${project} ${app_new_version}"
    popd

}

create_project "${1}"


