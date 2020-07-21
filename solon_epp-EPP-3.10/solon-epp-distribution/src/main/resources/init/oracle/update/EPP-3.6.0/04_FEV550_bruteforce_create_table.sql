CREATE TABLE TENTATIVES_CONNEXION(
    identifiant VARCHAR2(150) NOT NULL,
    compteur INTEGER,
    date_debut_blocage TIMESTAMP(6)
);

commit;

