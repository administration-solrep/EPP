CREATE OR REPLACE PROCEDURE "NX_UPDATE_READ_ACLS"
-- Rebuild only necessary read acls
IS
  update_count PLS_INTEGER;
BEGIN
  --
  -- 1/ New documents, no new ACL
  MERGE INTO hierarchy_read_acl t
    USING (SELECT DISTINCT(m.hierarchy_id) id
            FROM aclr_modified m
            JOIN hierarchy h ON m.hierarchy_id = h.id
            WHERE m.is_new = 1) s
    ON (t.id = s.id)
    WHEN NOT MATCHED THEN
      INSERT (id, acl_id) VALUES (s.id, nx_get_read_acl_id(s.id));
  DELETE FROM aclr_modified WHERE is_new = 1;
  --
  -- 2/ Compute the new read ACLS for updated documents
  UPDATE hierarchy_read_acl SET acl_id = nx_get_read_acl_id(id) WHERE id IN (
    SELECT h.id
      FROM hierarchy h
      start with h.id IN (select hierarchy_id from aclr_modified)
      connect by prior h.id = h.parentid);
  DELETE FROM aclr_modified;
  --
END;
/