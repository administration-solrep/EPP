#!/bin/sh

rm -rf /tmp/solonepg-server-SNAPSHOT;
/etc/init.d/epg-server_inst1 stop;
cd /tmp;
wget --auth-no-challenge --http-user=adminsolon --http-password=a367318748121171da03475f1b9475aa http://idlv-solon-intel.lyon-dev2.local/jenkins/job/SOLON/job/Trunk/job/solon_epg/lastSuccessfulBuild/artifact/solon-epg-distribution/target/solon-epg-distribution-SNAPSHOT-server.zip;
unzip solon-epg-distribution-SNAPSHOT-server.zip;
rm -rf solon-epg-distribution-SNAPSHOT-server.zip;
rm -rf /opt/epg-server_SNAPSHOT/;
cd /tmp/solonepg-server-SNAPSHOT/init/bin/;
sh install_server.sh /EPG/epg_config/;

