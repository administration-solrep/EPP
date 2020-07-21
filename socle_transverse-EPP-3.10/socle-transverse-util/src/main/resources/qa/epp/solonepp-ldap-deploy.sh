#!/bin/bash

# Script à executer en root, enleve le ldif existant et le recree

SLAPD_COMMAND=/etc/init.d/slapd
LDAP_CONF_DIR=/etc/openldap
LDAP_DIR=/var/lib/ldap
INIT_DIR=/opt/solonepp-server-1.0.0-SNAPSHOT/init/ldap
LDAP_USER=ldap
LDAP_GROUP=ldap

${SLAPD_COMMAND} stop
rm -f ${LDAP_DIR}/*
cp ${INIT_DIR}/*.schema ${LDAP_CONF_DIR}/schema
slapadd -v -l ${INIT_DIR}/create-init.ldif -f ${LDAP_CONF_DIR}/slapd.conf
chown -R ${LDAP_USER}:${LDAP_GROUP} ${LDAP_DIR}
${SLAPD_COMMAND} start

sleep 5

# Ajout de la branche SOLON EPP
echo "init de la branche SOLON EPP"
ldapadd -D "cn=ldapadmin,dc=dila,dc=fr" -w changeme -x -f ${INIT_DIR}/create-solonepp.ldif
ldapmodify -D "cn=ldapadmin,dc=dila,dc=fr" -w changeme -x -f ${INIT_DIR}/modify-solonepp-test.ldif

