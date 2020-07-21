#!/bin/sh

source `dirname $0`/vars.sh

# MAJ de Nuxeo
echo
echo Reconstruction des métadonnées Eclipse de Nuxeo
cd ${WS_DIR}/nuxeo
mvn eclipse:clean eclipse:eclipse

# MAJ de Nuxeo Document Routing
echo
echo Reconstruction des métadonnées Eclipse de Nuxeo Document Routing
cd ${WS_DIR}/nuxeo-platform-document-routing
mvn eclipse:clean eclipse:eclipse

# MAJ de Nuxeo Case Management
echo Reconstruction des métadonnées Eclipse de Nuxeo Case Management
cd ${WS_DIR}/nuxeo-case-management
mvn eclipse:clean eclipse:eclipse

echo Fini, maintenant rafraichissez les sources de Nuxeo dans Eclipse !
