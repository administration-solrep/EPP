package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class BatchPlanificationListe {
    private List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<BatchPlanificationDTO> liste = new ArrayList<>();

    public BatchPlanificationListe() {
        super();
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }

    public void setListeColonnes(List<ColonneInfo> listeColonnes) {
        this.listeColonnes = listeColonnes;
    }

    public List<BatchPlanificationDTO> getListe() {
        return liste;
    }

    public void setListe(List<BatchPlanificationDTO> liste) {
        this.liste = liste;
    }

    public void buildColonnes() {
        listeColonnes.clear();
        listeColonnes.add(new ColonneInfo("batch.planification.batch.label", false, false, false, false));
        listeColonnes.add(
            new ColonneInfo("batch.planification.dateDernierDeclenchement.label", false, false, false, false)
        );
        listeColonnes.add(
            new ColonneInfo("batch.planification.dateProchainDeclenchement.label", false, false, false, false)
        );
        listeColonnes.add(new ColonneInfo("batch.planification.periodicite.label", false, false, false, false));
    }
}
