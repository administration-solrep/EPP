--FEV 505 pour le conseil d etat: ajout de donnees sur le jeton pour les pieces complementaires et le type de modification

ALTER
        TABLE JETON_DOC
ADD (
        TYPE_MODIFICATION NVARCHAR2(200),
        ID_COMPLEMENTAIRE NVARCHAR2(200)
);

COMMIT;

