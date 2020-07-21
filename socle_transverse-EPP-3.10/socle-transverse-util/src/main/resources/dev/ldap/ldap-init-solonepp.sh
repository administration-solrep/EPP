#!/bin/sh
LDIF_DIR=~/workspace/solon_epp/solon-epp-distribution/src/main/resources/init/ldap
ADMIN_DN="cn=ldapadmin,dc=dila,dc=fr"
ADMIN_PW=changeme
HOSTNAME=localhost

# Supprime et recrée la branche SOLON EPG
ldapdelete -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x "ou=SolonEpp,dc=dila,dc=fr" -r
ldapadd -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x -f ${LDIF_DIR}/create-solonepp.ldif
ldapmodify -h ${HOSTNAME} -D "${ADMIN_DN}" -w ${ADMIN_PW} -x -f ${LDIF_DIR}/modify-solonepp-test.ldif
