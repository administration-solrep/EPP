set serveroutput on

DECLARE

  var_n_process NUMBER(10);
  var_n_session NUMBER(10);
  var_n_transaction NUMBER(10);
  var_n_open_cursor NUMBER(10);

  var_line_buf VARCHAR2(255) := '';
  fHandler UTL_FILE.FILE_TYPE;

BEGIN
  dbms_output.enable();

  SELECT COUNT(1) INTO var_n_process FROM  v$process;
  SELECT COUNT(1) INTO var_n_session FROM  v$session;
  SELECT COUNT(1) INTO var_n_transaction FROM v$transaction;
  SELECT COUNT(1) INTO var_n_open_cursor FROM v$open_cursor;

  --var_line_buf := '#var_n_process var_n_session var_n_transaction var_n_open_cursor';
  --dbms_output.put_line(var_line_buf);

  var_line_buf := '';
  var_line_buf := var_line_buf||var_n_process||'    ';
  var_line_buf := var_line_buf|| var_n_session||'    ';
  var_line_buf := var_line_buf|| var_n_transaction||'    ';
  var_line_buf := var_line_buf|| var_n_open_cursor||'    ';

  dbms_output.put_line(var_line_buf);

END;
/

exit
