package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class BatchDetailListe {
    private List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<BatchDetailDTO> liste = new ArrayList<>();

    private String nom;

    private Integer nbTotal = 0;

    public BatchDetailListe() {
        super();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }

    public void setListeColonnes(List<ColonneInfo> listeColonnes) {
        this.listeColonnes = listeColonnes;
    }

    public List<BatchDetailDTO> getListe() {
        return liste;
    }

    public void setListe(List<BatchDetailDTO> liste) {
        this.liste = liste;
    }

    public Integer getNbTotal() {
        return nbTotal;
    }

    public void setNbTotal(Integer nbTotal) {
        this.nbTotal = nbTotal;
    }

    public void buildColonnes() {
        listeColonnes.clear();
        listeColonnes.add(new ColonneInfo("batch.suivi.batch.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.type.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.debutBatch.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.finBatch.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.duree.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.erreurs.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.resultats.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.message.label", false, false, false, false));
    }
}
