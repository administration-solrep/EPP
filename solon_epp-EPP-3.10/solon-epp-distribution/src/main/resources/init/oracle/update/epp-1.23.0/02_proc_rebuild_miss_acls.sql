create or replace PROCEDURE "SW_REBUILD_MISSING_READ_ACLS"
-- Rebuild the missing read acls in hierarchy_read_acl
IS
BEGIN

  INSERT INTO hierarchy_read_acl
    SELECT id, nx_get_read_acl_id(id)
      FROM (select h.id from HIERARCHY h left join hierarchy_read_acl hr on h.id = hr.id where h.ISPROPERTY = 0 and hr.id is null);

END;
/

CALL SW_REBUILD_MISSING_READ_ACLS();

commit;
