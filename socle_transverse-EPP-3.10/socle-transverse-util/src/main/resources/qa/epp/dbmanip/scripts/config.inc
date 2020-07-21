
DRIVER=oracle.jdbc.driver.OracleDriver
HOST=idlv-db-ora-112-6.lyon-dev2.local
PORT=1521
INSTANCE=ORA112
URL="jdbc:oracle:thin:@$HOST:$PORT:$INSTANCE"
SYSUSER="sys as sysdba"
SYSPASSWORD=system


EXTLIBDIR=../extlib
JDBCCLIENTDIR=../jdbcclient-1.0.0-SNAPSHOT

REPONSESSERVERDIR=/opt/solonepp-server-1.0.0-SNAPSHOT/
JDBCCLIENTARCHIVE=jdbcclient-1.0.0-SNAPSHOT-assembly.zip
JDBCCLIENTURL='http://idlv-ic.lyon-dev2.local:8180/jenkins/view/naiad/view/naiad-tools/view/trunk/job/fr.sword.naiad.tools.jdbcclient/lastSuccessfulBuild/artifact/target/jdbcclient-1.0.0-SNAPSHOT-assembly.zip'




