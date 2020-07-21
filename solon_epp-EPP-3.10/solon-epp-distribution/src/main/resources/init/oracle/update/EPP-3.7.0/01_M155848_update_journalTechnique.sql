-- Mise à jour du poste de l'administrateur pour remplacer poste inconnu par un espace

UPDATE NXP_LOGS 
SET NXP_LOGS.LOG_DOC_PATH='' 
WHERE NXP_LOGS.LOG_DOC_PATH='**poste inconnu**' AND NXP_LOGS.LOG_PRINCIPAL_NAME='Administrateur';

commit;