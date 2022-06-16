package fr.dila.ss.ui.bean.fdr;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.st.ui.annot.NxProp;

public class CreationEtapeLineDTO {
    public static final String MINISTERE = "ministere";
    private static final String NON = "non";
    public static final String SGG = "sgg";

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_TYPE_PROPERTY
    )
    private String typeEtape;

    private String destinataire;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY
    )
    private String mailboxId;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_DEADLINE_PROPERTY
    )
    private String echeance;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY
    )
    private Boolean valAuto;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY
    )
    private Boolean obligatoireMinistere = Boolean.FALSE;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY
    )
    private boolean obligatoireSgg = Boolean.FALSE;

    private String typeDestinataire;

    public String getTypeEtape() {
        return typeEtape;
    }

    public void setTypeEtape(String typeEtape) {
        this.typeEtape = typeEtape;
    }

    public String getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
        this.mailboxId = "poste-" + destinataire;
    }

    public String getMailboxId() {
        return mailboxId;
    }

    public String getEcheance() {
        return echeance;
    }

    public void setEcheance(String echeance) {
        this.echeance = echeance;
    }

    public Boolean getValAuto() {
        return valAuto;
    }

    public void setValAuto(Boolean valAuto) {
        this.valAuto = valAuto;
    }

    public String getObligatoire() {
        if (Boolean.TRUE.equals(this.obligatoireMinistere)) {
            return MINISTERE;
        }

        if (Boolean.TRUE.equals(this.obligatoireSgg)) {
            return SGG;
        }

        return NON;
    }

    public void setObligatoire(String obligatoire) {
        this.obligatoireMinistere = MINISTERE.equals(obligatoire);
        this.obligatoireSgg = SGG.equals(obligatoire);
    }

    public String getTypeDestinataire() {
        return typeDestinataire;
    }

    public void setTypeDestinataire(String typeDestinataire) {
        this.typeDestinataire = typeDestinataire;
    }
}
