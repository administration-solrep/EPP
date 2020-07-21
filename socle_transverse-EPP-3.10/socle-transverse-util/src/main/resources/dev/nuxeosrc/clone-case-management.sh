#!/bin/sh
. ./vars.sh

pushd .
rm -rf ${WS_DIR}/nuxeo-case-management
cd ${WS_DIR}
hg clone -r ${NUXEO_CASE_MANAGEMENT_VERSION} https://hg.nuxeo.org/nuxeo-case-management/ || exit 1
cd nuxeo-case-management
popd

