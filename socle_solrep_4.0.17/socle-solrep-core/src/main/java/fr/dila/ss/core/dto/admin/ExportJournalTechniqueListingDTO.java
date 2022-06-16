package fr.dila.ss.core.dto.admin;

public class ExportJournalTechniqueListingDTO {
    private final String date;
    private final String utilisateur;
    private final String poste;
    private final String categorie;
    private final String commentaire;
    private final String referenceDossier;

    public ExportJournalTechniqueListingDTO(
        String date,
        String utilisateur,
        String poste,
        String categorie,
        String commentaire,
        String referenceDossier
    ) {
        this.date = date;
        this.utilisateur = utilisateur;
        this.poste = poste;
        this.categorie = categorie;
        this.commentaire = commentaire;
        this.referenceDossier = referenceDossier;
    }

    public String getDate() {
        return date;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public String getPoste() {
        return poste;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public String getReferenceDossier() {
        return referenceDossier;
    }
}
