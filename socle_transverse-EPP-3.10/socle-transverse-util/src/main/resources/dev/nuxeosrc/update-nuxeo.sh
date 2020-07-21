#!/bin/sh

source `dirname $0`/vars.sh

# MAJ de Nuxeo
echo
echo MAJ de Nuxeo à la version ${NUXEO_VERSION}
cd ${WS_DIR}/nuxeo
hg pull
hg up -C -r ${NUXEO_VERSION}
for x in nuxeo-common nuxeo-runtime nuxeo-core \
    nuxeo-services nuxeo-theme nuxeo-webengine nuxeo-jsf \
    nuxeo-gwt nuxeo-features nuxeo-dm \
    nuxeo-distribution; do
    echo "Updating ${x}"
    cd ${WS_DIR}/nuxeo/${x} && hg pull && hg up -C -r ${NUXEO_VERSION}
done
