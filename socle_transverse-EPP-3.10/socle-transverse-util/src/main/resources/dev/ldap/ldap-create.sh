#!/bin/bash

# Script à exécuter en root, supprime la base LDAP existante et recrée la structure minimale.
# Script pour OpenSUSE

# set default value
: ${LDIF_DIR:=/platform/home/user/workspace/solon_epg/solon-epg-distribution/src/main/resources/init/ldap}
: ${SLAPD_RUNDIR:=}
LDAP_CONF_DIR=/etc/openldap
# Debian : LDAP_CONF_DIR=/etc/ldap
LDAP_DIR=/var/lib/ldap
LDAP_USER=ldap
LDAP_GROUP=ldap
# Debian : LDAP_USER=openldap
# Debian : LDAP_GROUP=openldap

slapd_service() {
  if [ -x /etc/init.d/slapd ]; then
    /etc/init.d/slapd "$1"
  elif [ -x /etc/init.d/ldap ]; then
    /etc/init.d/ldap "$1"
  else
    systemctl "$1" slapd
  fi
}

slapd_service stop
rm -f ${LDAP_DIR}/*
# remove unused slapd.d configuration directory
rm -rf /etc/openldap/slapd.d/
cp ${LDAP_CONF_DIR}/slapd.conf ${LDAP_CONF_DIR}/slapd.conf.old
cp ${LDIF_DIR}/slapd.conf ${LDAP_CONF_DIR}/slapd.conf
cp ${LDIF_DIR}/dila.schema ${LDAP_CONF_DIR}/schema
slapadd -v -l ${LDIF_DIR}/create-init.ldif -f ${LDAP_CONF_DIR}/slapd.conf
chown -R ${LDAP_USER}:${LDAP_GROUP} ${LDAP_DIR}
chown -R ${LDAP_USER}:${LDAP_GROUP} ${LDAP_CONF_DIR}/slapd.conf
if [ -n "${SLAPD_RUNDIR}" ]; then
    # pid file not compatible with redhat scripts
    # override when needed
    sed -i -e 's@/var/run/slapd/slapd.pid@'"${SLAPD_RUNDIR}"'/slapd.pid@' ${LDAP_CONF_DIR}/slapd.conf
    sed -i -e 's@/var/run/slapd/slapd.args@'"${SLAPD_RUNDIR}"'/splad.args@' ${LDAP_CONF_DIR}/slapd.conf
fi
slapd_service start

