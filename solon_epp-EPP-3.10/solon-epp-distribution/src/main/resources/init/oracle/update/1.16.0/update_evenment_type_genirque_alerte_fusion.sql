UPDATE EVENEMENT SET TYPEEVENEMENT='ALERTE01'   WHERE TYPEEVENEMENT='ALERTE';
UPDATE EVENEMENT SET TYPEEVENEMENT='GENERIQUE01'   WHERE TYPEEVENEMENT='GENERIQUE';
UPDATE EVENEMENT SET TYPEEVENEMENT='EVT53-01'   WHERE TYPEEVENEMENT='EVT53';

COMMIT;