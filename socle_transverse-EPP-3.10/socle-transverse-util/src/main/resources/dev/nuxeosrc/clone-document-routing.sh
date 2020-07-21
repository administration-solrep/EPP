#!/bin/sh
. ./vars.sh

pushd .
rm -rf ${WS_DIR}/nuxeo-platform-document-routing
cd ${WS_DIR}
hg clone -r ${NUXEO_PLATFORM_DOCUMENT_ROUTING_VERSION} https://hg.nuxeo.org/addons/nuxeo-platform-document-routing || exit 1
cd nuxeo-platform-document-routing
popd
