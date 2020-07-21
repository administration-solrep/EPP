
DRIVER=oracle.jdbc.driver.OracleDriver
HOST=idlv-solon-ora.lyon-dev2.local
PORT=1521
INSTANCE=SOLON
URL="jdbc:oracle:thin:@$HOST:$PORT:$INSTANCE"
SYSUSER="sys as sysdba"
SYSPASSWORD=solon


EXTLIBDIR=../extlib
JDBCCLIENTDIR=../jdbcclient-1.0.0-SNAPSHOT

REPONSESSERVERDIR=/opt/reponses-server_inst1/
JDBCCLIENTARCHIVE=jdbcclient-1.0.0-SNAPSHOT-assembly.zip
JDBCCLIENTURL='http://idlv-ic.lyon-dev2.local:8180/jenkins/view/naiad/view/naiad-tools/view/trunk/job/fr.sword.naiad.tools.jdbcclient/lastSuccessfulBuild/artifact/target/jdbcclient-1.0.0-SNAPSHOT-assembly.zip'




