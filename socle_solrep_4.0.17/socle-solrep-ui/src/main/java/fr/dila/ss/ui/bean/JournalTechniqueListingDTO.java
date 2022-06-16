package fr.dila.ss.ui.bean;

public class JournalTechniqueListingDTO extends JournalDossierListingDTO {
    private String referenceDossier;

    public String getReferenceDossier() {
        return referenceDossier;
    }

    public void setReferenceDossier(String referenceDossier) {
        this.referenceDossier = referenceDossier;
    }

    public JournalTechniqueListingDTO() {
        super();
    }
}
