-- FEV320
-- Ajouter la colonne rectificatif pour l'EVT 22

ALTER TABLE "VERSION" ADD "RECTIFICATIF" NUMBER(1,0);

-- MAJ de la table "VERSION" champs "RECTIFICATIF" = 0 pour EVT22
UPDATE "VERSION" V
SET V."RECTIFICATIF" = '0'
WHERE V."ID" IN
  (SELECT VER."ID"
  FROM "VERSION" VER
  INNER JOIN "EVENEMENT" E
  ON VER."EVENEMENT"        = E."IDEVENEMENT"
  WHERE E."TYPEEVENEMENT" = 'EVT28'
  );
  
COMMIT;