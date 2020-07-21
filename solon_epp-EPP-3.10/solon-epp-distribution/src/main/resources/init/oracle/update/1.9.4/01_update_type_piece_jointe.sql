-- Maj vocabulaire
UPDATE VOC_TYPE_PJ SET "label" = 'Lettre conjointe des Présidents' WHERE "id" = 'LETTRE_CONJOINTE_PRESIDENTS';
UPDATE VOC_TYPE_PJ SET "label" = 'Décret du Président de la République' WHERE "id" = 'DECRET_PRESIDENT_REPUBLIQUE';
UPDATE VOC_TYPE_PJ SET "label" = 'Lettre Premier Ministre à l''Assemblée Nationale' WHERE "id" = 'LETTRE_PM_VERS_AN';
UPDATE VOC_TYPE_PJ SET "label" = 'Lettre Premier Ministre au Sénat' WHERE "id" = 'LETTRE_PM_VERS_SENAT';

commit;
