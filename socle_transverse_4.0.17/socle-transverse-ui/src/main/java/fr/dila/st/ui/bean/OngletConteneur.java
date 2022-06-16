package fr.dila.st.ui.bean;

import fr.dila.st.ui.enums.ActionCategory;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.platform.actions.Action;

public class OngletConteneur {
    private List<Onglet> onglets;

    private String name;

    private boolean currentTabAllowed = false;

    public OngletConteneur() {}

    public List<Onglet> getOnglets() {
        return onglets;
    }

    public void setOnglets(List<Onglet> onglets) {
        this.onglets = onglets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCurrentTabAllowed() {
        return currentTabAllowed;
    }

    public void setCurrentTabAllowed(boolean currentTabAllowed) {
        this.currentTabAllowed = currentTabAllowed;
    }

    public static OngletConteneur actionsToTabs(
        SpecificContext context,
        ActionCategory actionCategory,
        String current
    ) {
        return actionsToTabs(context.getActions(actionCategory), current);
    }

    public static OngletConteneur actionsToTabs(List<Action> actions, String current) {
        OngletConteneur conteneur = new OngletConteneur();
        List<Onglet> onglets = new ArrayList<>();
        for (Action action : actions) {
            Onglet onglet = new Onglet();
            onglet.setLabel(action.getLabel());
            onglet.setId((String) action.getProperties().get("name"));
            if (onglet.getId().equals(current)) {
                conteneur.setCurrentTabAllowed(true);
                onglet.setFragmentFile((String) action.getProperties().get("fragmentFile"));
                onglet.setFragmentName((String) action.getProperties().get("fragmentName"));
                onglet.setIsActif(true);
            }
            onglet.setScript((String) action.getProperties().get("script"));
            onglets.add(onglet);
        }
        conteneur.setOnglets(onglets);
        return conteneur;
    }
}
