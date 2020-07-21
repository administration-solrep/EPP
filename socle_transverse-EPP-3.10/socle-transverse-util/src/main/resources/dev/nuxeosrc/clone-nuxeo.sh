#!/bin/sh
. ./vars.sh

pushd .
rm -rf ${WS_DIR}/nuxeo
cd ${WS_DIR}
hg clone -r ${NUXEO_VERSION} https://hg.nuxeo.org/nuxeo/ || exit 1
cd nuxeo
sed -e "s/VERSION=.*/VERSION=${NUXEO_VERSION}/" clone.sh > clone2.sh
chmod +x clone2.sh
./clone2.sh
popd

