INSERT INTO VOC_TYPE_PJ ("id","obsolete","ordering","label") values ('DEMANDE_DECLARATION',0,10000000,'Demande de déclaration');
INSERT INTO VOC_TYPE_PJ ("id","obsolete","ordering","label") values ('DOCUMENTS',0,10000000,'Documents');
UPDATE VOC_TYPE_PJ SET VOC_TYPE_PJ."label" = 'Insertions au JO-LD' where VOC_TYPE_PJ."id" = 'INSERTION_JOLD';
commit;
