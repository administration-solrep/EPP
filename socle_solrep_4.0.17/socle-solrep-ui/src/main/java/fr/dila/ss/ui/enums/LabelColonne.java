package fr.dila.ss.ui.enums;

public enum LabelColonne {
    ETAT("fdr.etat.label"),
    NOTE("fdr.note.header.label"),
    ACTION("fdr.action.label"),
    POSTE("fdr.poste.label"),
    UTILISATEUR("fdr.utilisateur.label"),
    ECHEANCE("fdr.echeance.label"),
    TRAITE("fdr.traite.label"),
    TYPE_ETAPE("fdr.typeEtape.label"),
    TYPE_DESTINATAIRE("fdr.typeDestinataire.label"),
    OBLIGATOIRE("fdr.required.label"),
    VAL_AUTO("fdr.val.auto.label"),
    ACTIONS("header.label.actions"),
    VIDE("fdr.colonne.vide.label");

    private String label;

    LabelColonne(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
