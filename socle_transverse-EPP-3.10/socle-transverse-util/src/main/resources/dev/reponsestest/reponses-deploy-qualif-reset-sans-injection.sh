#!/bin/sh

echo "deploy, with database reset but no selenium, no injection LDAP QUALIF"
sh reponses-deploy-gen.sh reset noselenium noinjection qualif 

