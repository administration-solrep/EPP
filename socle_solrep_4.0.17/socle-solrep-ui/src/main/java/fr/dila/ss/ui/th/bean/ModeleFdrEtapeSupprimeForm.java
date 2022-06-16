package fr.dila.ss.ui.th.bean;

public class ModeleFdrEtapeSupprimeForm {
    private String intituleModele;

    private int nbEtapesASupprimer;

    private boolean isParallele;

    public ModeleFdrEtapeSupprimeForm() {
        super();
    }

    public String getIntituleModele() {
        return intituleModele;
    }

    public void setIntituleModele(String intituleModele) {
        this.intituleModele = intituleModele;
    }

    public int getNbEtapesASupprimer() {
        return nbEtapesASupprimer;
    }

    public void setNbEtapesASupprimer(int nbEtapesASupprimer) {
        this.nbEtapesASupprimer = nbEtapesASupprimer;
    }

    public boolean getIsParallele() {
        return isParallele;
    }

    public void setIsParallele(boolean isParallele) {
        this.isParallele = isParallele;
    }
}
