package fr.dila.ss.ui.bean;

import fr.dila.ss.ui.bean.fdr.FdrTableDTO;
import fr.dila.ss.ui.enums.LabelColonne;
import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.platform.actions.Action;

public class FdrDTO {
    private List<Action> tabActions = new ArrayList<>();
    private List<Action> secondTabActions = new ArrayList<>();
    protected final List<ColonneInfo> listeColonnes = new ArrayList<>();
    private Action substitutionAction;
    private FdrTableDTO table;
    private String id;

    public FdrDTO() {
        super();
    }

    public List<Action> getTabActions() {
        return tabActions;
    }

    public void setTabActions(List<Action> tabActions) {
        this.tabActions = tabActions;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }

    public void buildColonnesFdr() {
        listeColonnes.clear();

        listeColonnes.add(new ColonneInfo(LabelColonne.ETAT.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.NOTE.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.ACTION.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.POSTE.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.UTILISATEUR.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.ECHEANCE.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.TRAITE.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.OBLIGATOIRE.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.ACTIONS.getLabel(), false, true, false, false));
    }

    public void buildColonnesFdrModele() {
        listeColonnes.clear();

        listeColonnes.add(new ColonneInfo(LabelColonne.ACTION.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.POSTE.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.OBLIGATOIRE.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.VAL_AUTO.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.ACTIONS.getLabel(), false, true, false, false));
    }

    public void buildColonnesFdrModeleSubstitution() {
        listeColonnes.clear();

        listeColonnes.add(new ColonneInfo(LabelColonne.ACTION.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.POSTE.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.OBLIGATOIRE.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.VAL_AUTO.getLabel(), false, true, false, true));
        listeColonnes.add(new ColonneInfo(LabelColonne.VIDE.getLabel(), false, true, false, false));
    }

    public Action getSubstitutionAction() {
        return substitutionAction;
    }

    public void setSubstitutionAction(Action substitutionAction) {
        this.substitutionAction = substitutionAction;
    }

    public FdrTableDTO getTable() {
        return table;
    }

    public void setTable(FdrTableDTO table) {
        this.table = table;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Action> getSecondTabActions() {
        return secondTabActions;
    }

    public void setSecondTabActions(List<Action> secondTabActions) {
        this.secondTabActions = secondTabActions;
    }
}
