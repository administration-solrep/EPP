--Ajouter la colonne identifiant metier
ALTER TABLE "VERSION" ADD "IDENTIFIANTMETIER" NVARCHAR2(2000);
COMMIT;