INJECTION_COUNT=$1
INJECTION_DIR=/opt/epg-bench-reports/injections
${INJECTION_COUNT:?    "${INJECTION_COUNT} is not defined"}
ssh funkload "cd ${INJECTION_DIR};sh creation_injection $INJECTION_COUNT"
scp ./4_graph/* funkload:${INJECTION_DIR}/${INJECTION_COUNT}_injection
scp ./0_injection_data/* funkload:${INJECTION_DIR}/${INJECTION_COUNT}_injection


