#!/bin/sh

source `dirname $0`/vars.sh

# MAJ de Nuxeo Case Management
echo
echo MAJ de Nuxeo Case Management à la version ${NUXEO_CASE_MANAGEMENT_VERSION}
cd ${WS_DIR}/nuxeo-case-management
hg pull
hg up -C -r ${NUXEO_CASE_MANAGEMENT_VERSION}

