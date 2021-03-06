ALTER TABLE "DOSSIER" ADD "DATEDECLARATION" TIMESTAMP(6);
ALTER TABLE "DOSSIER" ADD "DEMANDEVOTE" NUMBER(1,0);
ALTER TABLE "DOSSIER" ADD "DATEVOTE" TIMESTAMP(6);
ALTER TABLE "DOSSIER" ADD "DATEPRESENTATION" TIMESTAMP(6);
ALTER TABLE "DOSSIER" ADD "DATEAUDITION" TIMESTAMP(6);
ALTER TABLE "DOSSIER" ADD "FONCTION" NVARCHAR2(2000);
ALTER TABLE "DOSSIER" ADD "PERSONNE" NVARCHAR2(2000);
ALTER TABLE "DOSSIER" ADD "DATELETTREPM" TIMESTAMP(6);
ALTER TABLE "DOSSIER" ADD "DATEDEMANDE" TIMESTAMP(6);
ALTER TABLE "DOSSIER" ADD "DOSSIERCIBLE" NVARCHAR2(2000);
ALTER TABLE "DOSSIER" ADD "BASELEGALE" NVARCHAR2(2000);
ALTER TABLE "DOSSIER" ADD "ORGANISME" NVARCHAR2(2000);
CREATE TABLE "DOS_GROUPEPARLEMENTAIRE"
(
	"ID" VARCHAR2(36) NOT NULL ENABLE,
	"POS" NUMBER(10,0),
	"ITEM" NVARCHAR2(2000),
	CONSTRAINT "HIER_DOS_GP_PARL_FK" FOREIGN KEY ("ID")	                 
	REFERENCES "HIERARCHY" ("ID") ON DELETE CASCADE ENABLE
);
