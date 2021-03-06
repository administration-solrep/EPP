update cslk_corbeillelist set ITEM= 'CORBEILLE_AN_NOMINATIONS_ET_AUDITIONS' where ITEM = 'CORBEILLE_AN_ORGANISME_EXTRA';

update cslk_corbeillelist set ITEM= 'CORBEILLE_AN_DIQS_DIVERS' where ITEM = 'CORBEILLE_AN_SEANCE_DIVERS';

UPDATE CSLK_CORBEILLELIST c SET c.ITEM = 'CORBEILLE_AN_RESOLUTIONS' WHERE c.ID IN (SELECT cl.id From CASE_LINK cl INNER JOIN EVENEMENT e on e.id = cl.CASEDOCUMENTID 
 WHERE e.TYPEEVENEMENT IN ('EVT43BIS','EVT39','EVT40','EVT41','EVT43','EVT53-07','GENERIQUE07','ALERTE07')) AND c.ITEM = 'CORBEILLE_AN_DIQS_DIVERS';

insert into CSLK_CORBEILLELIST (ID,POS,ITEM) select c.ID, '10', 'CORBEILLE_AN_ORGANISATION_SESSION' from CSLK_CORBEILLELIST c, case_link cl, evenement e where e.id = cl.CASEDOCUMENTID and c.id=cl.id and
 e.TYPEEVENEMENT IN ('EVT35','EVT53-05','GENERIQUE05','ALERTE05','EVT29','EVT30','EVT53-03','GENERIQUE03','ALERTE03') AND c.ITEM = 'CORBEILLE_AN_DIQS_DIVERS';

insert into CSLK_CORBEILLELIST (ID,POS,ITEM) select c.ID, '10', 'CORBEILLE_AN_DELCARATIONS_DU_GOUVERNEMENT' from CSLK_CORBEILLELIST c, case_link cl, evenement e where e.id = cl.CASEDOCUMENTID and c.id=cl.id and
 e.TYPEEVENEMENT IN ('EVT36','EVT38','EVT53-06','GENERIQUE06','ALERTE06') AND c.ITEM = 'CORBEILLE_AN_DIQS_DIVERS';

insert into CSLK_CORBEILLELIST (ID,POS,ITEM) select c.ID, '10', 'CORBEILLE_AN_NOMINATIONS_ET_AUDITIONS' from CSLK_CORBEILLELIST c, case_link cl, evenement e where e.id = cl.CASEDOCUMENTID and c.id=cl.id and
 e.TYPEEVENEMENT IN ('EVT32','EVT34','GENERIQUE04','ALERTE04','EVT53-04') AND c.ITEM = 'CORBEILLE_AN_DIQS_DIVERS';

update cslk_corbeillelist set ITEM= 'CORBEILLE_SENAT_RAPPORTS_ET_DOCUMENTS' where ITEM = 'CORBEILLE_SENAT_RAPPORT';

commit;
