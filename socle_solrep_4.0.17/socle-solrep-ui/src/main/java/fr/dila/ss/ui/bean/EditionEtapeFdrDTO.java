package fr.dila.ss.ui.bean;

import fr.dila.st.ui.annot.SwBean;
import javax.ws.rs.FormParam;

@SwBean
public class EditionEtapeFdrDTO {
    @FormParam("dossierId")
    private String dossierId;

    @FormParam("dossierLinkId")
    private String dossierLinkId;

    @FormParam("typeEtape")
    private String typeEtape;

    @FormParam("destinataire")
    private String destinataire;

    @FormParam("echeance")
    private String echeance;

    @FormParam("valAuto")
    private boolean valAuto;

    @FormParam("obligatoire")
    private String obligatoire;

    @FormParam("stepId")
    private String stepId;

    @FormParam("lineIndex")
    private int lineIndex;

    @FormParam("totalNbLevel")
    private int totalNbLevel;

    @FormParam("isModele")
    private boolean isModele;

    public EditionEtapeFdrDTO() {}

    public String getDossierId() {
        return dossierId;
    }

    public void setDossierId(String dossierId) {
        this.dossierId = dossierId;
    }

    public String getDossierLinkId() {
        return dossierLinkId;
    }

    public void setDossierLinkId(String dossierLinkId) {
        this.dossierLinkId = dossierLinkId;
    }

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
    }

    public String getEcheance() {
        return echeance;
    }

    public void setEcheance(String echeance) {
        this.echeance = echeance;
    }

    public boolean getValAuto() {
        return valAuto;
    }

    public void setValAuto(boolean valAuto) {
        this.valAuto = valAuto;
    }

    public String getObligatoire() {
        return obligatoire;
    }

    public void setObligatoire(String obligatoire) {
        this.obligatoire = obligatoire;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public int getTotalNbLevel() {
        return totalNbLevel;
    }

    public void setTotalNbLevel(int totalNbLevel) {
        this.totalNbLevel = totalNbLevel;
    }

    public boolean getIsModele() {
        return isModele;
    }

    public void setIsModele(boolean isModele) {
        this.isModele = isModele;
    }
}
