package fr.dila.ss.api.client;

import java.util.Date;

public interface InjectionGvtDTO {
    void setNor(String nor);

    String getNor();

    String getOrdreProtocolaireSolon();

    void setOrdreProtocolaireSolon(String ordreProtocolaireSolon);

    String getOrdreProtocolaireReponses();

    void setOrdreProtocolaireReponses(String ordreProtocolaireReponses);

    String getLibelleCourt();

    void setLibelleCourt(String libelleCourt);

    String getLibelleLong();

    void setLibelleLong(String libelleLong);

    String getFormule();

    void setFormule(String formule);

    String getCivilite();

    void setCivilite(String civilite);

    String getPrenom();

    void setPrenom(String prenom);

    String getNom();

    void setNom(String nom);

    String getPrenomNom();

    void setPrenomNom(String prenomNom);

    Date getDateDeDebut();

    void setDateDeDebut(Date dateDeDebut);

    Date getDateDeFin();

    void setDateDeFin(Date dateDeFin);

    String getNorEPP();

    void setNorEPP(String norEPP);

    boolean isNouvelleEntiteEPP();

    void setNouvelleEntiteEPP(boolean nouvelleEntiteEPP);

    boolean isaCreerSolon();

    void setaCreerSolon(boolean aCreerSolon);

    boolean isaModifierSolon();

    void setaModifierSolon(boolean aModifierSolon);

    boolean isaCreerReponses();

    void setaCreerReponses(boolean aCreerReponses);

    boolean isGvt();

    void setGvt(boolean isGvt);

    String getId();

    void setId(String id);

    boolean isaModifierReponses();

    void setaModifierReponses(boolean aModifierReponses);

    String getOldOrdreProtocolaireReponses();

    void setOldOrdreProtocolaireReponses(String ordreProtocolaireReponses);

    String getIdOrganigramme();

    void setIdOrganigramme(String idOrganigramme);

    String getTypeModification();
    void setTypeModification(String typeModification);
}
