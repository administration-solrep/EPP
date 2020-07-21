#!/bin/sh
ADMIN_DN="cn=ldapadmin,dc=dila,dc=fr"
ADMIN_PW=changeme
HOSTNAME=localhost

# Supprime et recrée la branche SolonEpp
ldapdelete -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x "ou=SolonEpp,dc=dila,dc=fr" -r
ldapadd -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x -f create-solonepp.ldif

