--Ajouter menu Procédure de Censure
delete from VOC_CATEGORIE_EVENEMENT;
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('PROCEDURE_LEGISLATIVE',0,10000000,'Procédure législative');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('ORGANISATION_SESSION_EXTRAORDINAIRE',0,10000000,'Organisation des sessions extraordinaires');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('CONSULTATION_ASSEMBLEE_PROJET_NOMINATION',0,10000000,'Consultation des assemblées sur les projets de nomination');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('CONVOCATION_CONGRES',0,10000000,'Convocation du congrès au titre de l''article 18 de la constitution');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('DEMANDE_PROLONGATION_INTERVENTION_EXTERIEURE',0,10000000,'Demande de prolongation d''une intervention extérieure');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('RESOLUTION_ARTICLE_34_1',0,10000000,'Résolution de l''article 34-1 de la constitution');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('DEPOT_RAPPORT_PARLEMENT',0,10000000,'Dépôt de rapports au parlement');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('INSERTION_INFORMATION_PARLEMENTAIRE_JO',0,10000000,'Insertion d''informations parlementaires au JO lois et décrets');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('ORGANISME_EXTRA_PARLEMENTAIRE',0,10000000,'Organismes extra-parlementaires');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('DIVERS',0,10000000,'Divers');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('REQUETE_LIBRE',0,10000000,'Requête libre');
insert into VOC_CATEGORIE_EVENEMENT ("id","obsolete","ordering","label") values ('PROCEDURE_CENSURE',0,10000000,'Procédure de Censure');
commit;
