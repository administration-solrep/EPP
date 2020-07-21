#!/bin/bash
## TODO: Tester ignorer la revision des changements de versions
## TODO: Ajouter switch

MINOR_VERSION=.0
SVN_SERVER="http://lyon-cvs2/dila_solrep/"
PROJECT_LIST=("reponses" "socle_transverse" "reprise")
VERSION=1.2
WORKING_DIR=$HOME/releasedir

# param <msg>
function msgFatalError(){
	local msg=$1
	echo "FATAL ERROR: $msg"
	exit
}

# param <msg>
function msgInfo(){
	local msg=$1
	echo "INFO: $msg"
}

# checkout trunk sources, copy to branch and checkout from version branch 
# param <project> <version>
function upload_project(){
	local project=$1
	local version=$2
	mkdir $WORKING_DIR/$project
	cd $WORKING_DIR/$project
	svn co ${SVN_SERVER}/${project}/trunk
	svn copy ./trunk ${SVN_SERVER}/${project}/branches/$version -m "Test Script creation release reponse ${version}"
	svn co ${SVN_SERVER}/${project}/branches/$version
}

# change version in pom
# param <project> <version>
function commit_version(){
	local project=$1
	local version=$2
	cd $WORKING_DIR/$project
	for POM_FILE in `find $version -name pom.xml`
	do
	    sed "s/[0-9].[0-9].[0-9]-\(RELEASE\|SNAPSHOT\)/$version${MINOR_VERSION}-RELEASE/g" -i $POM_FILE
	done
	cd $version
	svn commit -m "Changement des pom.xml pour la version : ${version}"
}

function exclure_last_commit(){
	local project=$1
	local version=$2
	last_revision=`svn info ${SVN_SERVER}/$project/branches/$version | grep "Révision "  | cut -d: -f2`
	svn merge –record-only –c $last_revision ${SVN_SERVER}/$project/branches/$version
	svn commit
}


# param <project> <version> <tag_name>
function create_tag(){
	local project=$1
	local version=$2
	local tagname=$3
	cd $WORKING_DIR/$project/
	svn copy ./$version ${SVN_SERVER}/${project}/tags/$tagname -m "Tag suite a la creation de la branche release ${version}"
}

# create release branche and change version in POM
# param <project> <version>
function release_project(){
	local project=$1
	local version=$2
	upload_project $project $version
	commit_version $project $version
	create_tag $project $version "$version${MINOR_VERSION}"
	#exclure_last_commit $project $version
}

if [ $# -ne 1 ]; then
	echo "usage : <version example: 1.2>"
	exit
fi

VERSION=$1
WORKING_DIR=$(pwd)/release

msgInfo "=================================================="
msgInfo " WORKING_DIR=$WORKING_DIR"
msgInfo " VERSION=$VERSION"
msgInfo " SVN_SERVER=$SVN_SERVER"
msgInfo "=================================================="

mkdir $WORKING_DIR
res=$?
if [ $res -ne 0 ]; then
	msgFatalError "Failed to create WORKING_DIR"
fi



for PROJECT in ${PROJECT_LIST[*]}
do
	msgInfo "release project $PROJECT"
	release_project $PROJECT $VERSION
done
