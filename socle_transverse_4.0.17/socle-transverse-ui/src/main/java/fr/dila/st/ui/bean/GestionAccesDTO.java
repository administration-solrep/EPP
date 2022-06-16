package fr.dila.st.ui.bean;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import javax.ws.rs.FormParam;

@SwBean
public class GestionAccesDTO {
    @FormParam("restrictionAcces")
    @NxProp(
        docType = STConstant.ETAT_APPLICATION_DOCUMENT_TYPE,
        xpath = STSchemaConstant.ETAT_APPLICATION_SCHEMA_PREFIX +
        ":" +
        STSchemaConstant.ETAT_APPLICATION_RESTRICTION_ACCES_PROPERTY
    )
    private boolean restrictionAcces = false;

    @FormParam("affichageBanniere")
    @NxProp(
        docType = STConstant.ETAT_APPLICATION_DOCUMENT_TYPE,
        xpath = STSchemaConstant.ETAT_APPLICATION_SCHEMA_PREFIX + ":" + STSchemaConstant.BANNIERE_AFFICHAGE_PROPERTY
    )
    private boolean affichageBanniere = false;

    @FormParam("descriptionRestriction")
    @NxProp(
        docType = STConstant.ETAT_APPLICATION_DOCUMENT_TYPE,
        xpath = STSchemaConstant.ETAT_APPLICATION_SCHEMA_PREFIX +
        ":" +
        STSchemaConstant.ETAT_APPLICATION_DESCRIPTION_RESTRICTION_PROPERTY
    )
    private String descriptionRestriction;

    @FormParam("messageBanniere")
    @NxProp(
        docType = STConstant.ETAT_APPLICATION_DOCUMENT_TYPE,
        xpath = STSchemaConstant.ETAT_APPLICATION_SCHEMA_PREFIX + ":" + STSchemaConstant.BANNIERE_MESSAGE_PROPERTY
    )
    private String messageBanniere;

    public GestionAccesDTO() {
        super();
    }

    public GestionAccesDTO(
        Boolean restrictionAcces,
        String descriptionRestriction,
        Boolean affichageBanniere,
        String messageBanniere
    ) {
        super();
        this.restrictionAcces = restrictionAcces;
        this.descriptionRestriction = descriptionRestriction;
        this.affichageBanniere = affichageBanniere;
        this.messageBanniere = messageBanniere;
    }

    public boolean isRestrictionAcces() {
        return restrictionAcces;
    }

    public void setRestrictionAcces(boolean restrictionAcces) {
        this.restrictionAcces = restrictionAcces;
    }

    public boolean isAffichageBanniere() {
        return affichageBanniere;
    }

    public void setAffichageBanniere(boolean affichageBanniere) {
        this.affichageBanniere = affichageBanniere;
    }

    public String getDescriptionRestriction() {
        return descriptionRestriction;
    }

    public void setDescriptionRestriction(String descriptionRestriction) {
        this.descriptionRestriction = descriptionRestriction;
    }

    public String getMessageBanniere() {
        return messageBanniere;
    }

    public void setMessageBanniere(String messageBanniere) {
        this.messageBanniere = messageBanniere;
    }
}
