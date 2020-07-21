OUTPUT=./monitordb.txt
SID=SOLON
echo "#timestamp var_n_process var_n_session var_n_transaction var_n_open_cursor" | tee  $OUTPUT

while true; do
        v=$(ORACLE_SID=$SID sqlplus -s sys/system as sysdba @monitor.sql | grep -v -e 'Procedure' -e '^$' )
        echo "$(date +%s) $v" | tee -a $OUTPUT
        sleep 1
done
