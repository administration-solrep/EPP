CREATE OR REPLACE PROCEDURE NX_CLUSTER_INVAL(i VARCHAR2, f VARCHAR2, k INTEGER, nid VARCHAR)
IS
BEGIN
  FOR c IN (SELECT nodeid FROM cluster_nodes WHERE nodeid <> nid) LOOP
    INSERT INTO cluster_invals (nodeid, id, fragments, kind) VALUES (c.nodeid, i, f, k);
  END LOOP;
END;
/
