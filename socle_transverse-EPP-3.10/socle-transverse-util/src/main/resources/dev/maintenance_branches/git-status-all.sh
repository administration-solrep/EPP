#!/bin/bash

#-------------------------------------------------------------
# git-status-all.sh : Retourne le status de chaque projet git
# A déposer à la racine du workspace
#-------------------------------------------------------------
for line in $(find . -maxdepth 1 -mindepth 1); do
        if [ -e "$line/.git"  ]; then
                cd $line
                        printf "$(pwd): $(git status)"
                cd ..
        fi
done;
