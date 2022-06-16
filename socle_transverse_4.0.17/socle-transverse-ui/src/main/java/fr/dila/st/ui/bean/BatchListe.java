package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class BatchListe {
    private List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<BatchDTO> liste = new ArrayList<>();

    private Integer nbTotal = 0;

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }

    public void setListeColonnes(List<ColonneInfo> listeColonnes) {
        this.listeColonnes = listeColonnes;
    }

    public List<BatchDTO> getListe() {
        return liste;
    }

    public void setListe(List<BatchDTO> liste) {
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

        listeColonnes.add(new ColonneInfo("batch.suivi.debut.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.fin.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.batch.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.serveur.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.tomcat.label", false, false, false, false));
        listeColonnes.add(new ColonneInfo("batch.suivi.erreurs.label", false, false, false, false));
    }
}
