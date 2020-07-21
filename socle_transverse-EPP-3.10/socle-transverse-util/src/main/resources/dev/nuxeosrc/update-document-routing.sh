#!/bin/sh

source `dirname $0`/vars.sh

# MAJ de Nuxeo Document Routing
echo
echo MAJ de Nuxeo Document Routing à la version ${NUXEO_PLATFORM_DOCUMENT_ROUTING_VERSION}
cd ${WS_DIR}/nuxeo-platform-document-routing
hg pull
hg up -C -r ${NUXEO_PLATFORM_DOCUMENT_ROUTING_VERSION}
