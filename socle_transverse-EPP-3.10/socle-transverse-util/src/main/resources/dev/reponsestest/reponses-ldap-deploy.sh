#!/bin/bash

SLAPD_COMMAND=/etc/init.d/slapd
LDAP_CONF_DIR=/etc/ldap
LDAP_DIR=/var/lib/ldap
INIT_DIR=/opt/reponses-server-1.0.0-SNAPSHOT/init/ldap
LDAP_USER=openldap
LDAP_GROUP=openldap


type=$1

if [ "$type" == "none" ]; then
  echo "No LDAP deployment"
  exit
fi 

if [ "$type" == "qualif" ]; then
  CREATE_LDIFF=${INIT_DIR}/create_reponse_qualif_exported.ldif
  MODIFY_LDIFF=${INIT_DIR}/modify-reponses-test.ldif
elif [ "$type" == "dev" ]; then
  CREATE_LDIFF=${INIT_DIR}/create-reponses.ldif
  MODIFY_LDIFF=${INIT_DIR}/modify-reponses-test.ldif

else
  echo "usage $0 <type (dev|qualif|none)>"
  exit
fi

# Script à executer en root, enleve le ldif existant et le recree


${SLAPD_COMMAND} stop
rm -f ${LDAP_DIR}/*
cp ${INIT_DIR}/*.schema ${LDAP_CONF_DIR}/schema
slapadd -v -l ${INIT_DIR}/create-init.ldif -f ${LDAP_CONF_DIR}/slapd.conf
chown -R ${LDAP_USER}:${LDAP_GROUP} ${LDAP_DIR}
${SLAPD_COMMAND} start

sleep 5

# Ajout de la branche Reponses
echo "init de la branche reponses"
ldapadd -D "cn=ldapadmin,dc=dila,dc=fr" -w changeme -x -f ${CREATE_LDIFF}
ldapmodify -D "cn=ldapadmin,dc=dila,dc=fr" -w changeme -x -f ${MODIFY_LDIFF}


