#!/bin/sh
LDIF_DIR=~/workspace/reponses/reponses-distribution/src/main/resources/init/ldap
ADMIN_DN="cn=ldapadmin,dc=dila,dc=fr"
ADMIN_PW=changeme
HOSTNAME=localhost

# Supprime et recrée la branche Réponses
ldapdelete -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x "ou=Reponses,dc=dila,dc=fr" -r
ldapadd -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x -f ${LDIF_DIR}/create-reponses.ldif
ldapmodify -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x -f ${LDIF_DIR}/modify-reponses-test.ldif
