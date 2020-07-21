#!/bin/bash

#-------------------------------------------------------------
# git-branch-all.sh : Retourne la branche de chaque projet git
# A déposer à la racine du workspace.
#-------------------------------------------------------------
COLOR='\033[0;32m'
NC='\033[0m' # No Color
for line in $(find . -maxdepth 1 -mindepth 1); do
        if [ -e "$line/.git"  ]; then
                cd $line
                        printf "$(pwd): ${COLOR}$(git branch | grep \*)${NC}\n"
                cd ..
        fi
done;