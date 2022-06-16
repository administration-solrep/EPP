package fr.dila.ss.ui.bean.fdr;

import fr.dila.st.core.client.AbstractMapDTO;
import java.util.ArrayList;
import org.nuxeo.ecm.platform.actions.Action;

public class FeuilleRouteDTO extends AbstractMapDTO {
    private static final long serialVersionUID = 1L;

    private static final String ID = "id";
    private static final String ETAT = "etat";
    private static final String INTITULE = "intitule";
    private static final String AUTEUR = "auteur";
    private static final String MINISTERE = "ministere";
    private static final String DERNIERE_MODIF = "derniereModif";
    private static final String DOC_ID_FOR_SELECTION = "docIdForSelection";
    private static final String ACTIONS = "actions";
    private static final String LOCK = "lock";
    private static final String LOCKOWNER = "lockOwner";

    public FeuilleRouteDTO() {
        super();
    }

    public FeuilleRouteDTO(
        String id,
        String etat,
        String intitule,
        String ministere,
        String auteur,
        String derniereModif,
        Boolean lock,
        String lockOwner
    ) {
        super();
        setId(id);
        setDocIdForSelection(id);
        setEtat(etat);
        setIntitule(intitule);
        setMinistere(ministere);
        setAuteur(auteur);
        setDerniereModif(derniereModif);
        setLock(lock);
        setLockOwner(lockOwner);
    }

    public String getId() {
        return getString(ID);
    }

    public void setId(String id) {
        put(ID, id);
    }

    public String getEtat() {
        return getString(ETAT);
    }

    public void setEtat(String etat) {
        put(ETAT, etat);
    }

    public String getIntitule() {
        return getString(INTITULE);
    }

    public void setIntitule(String intitule) {
        put(INTITULE, intitule);
    }

    public String getMinistere() {
        return getString(MINISTERE);
    }

    public void setMinistere(String ministere) {
        put(MINISTERE, ministere);
    }

    public String getAuteur() {
        return getString(AUTEUR);
    }

    public void setAuteur(String auteur) {
        put(AUTEUR, auteur);
    }

    public String getDerniereModif() {
        return getString(DERNIERE_MODIF);
    }

    public void setDerniereModif(String derniereModif) {
        put(DERNIERE_MODIF, derniereModif);
    }

    public Boolean getlock() {
        return getBoolean(LOCK);
    }

    public void setLock(Boolean lock) {
        put(LOCK, lock);
    }

    public String getlockOwner() {
        return getString(LOCKOWNER);
    }

    public void setLockOwner(String lockOwner) {
        put(LOCKOWNER, lockOwner);
    }

    @Override
    public String getType() {
        return "FeuilleRoute";
    }

    @Override
    public String getDocIdForSelection() {
        return getString(DOC_ID_FOR_SELECTION);
    }

    public void setDocIdForSelection(String docIdForSelection) {
        put(DOC_ID_FOR_SELECTION, docIdForSelection);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Action> getActions() {
        return (ArrayList<Action>) get(ACTIONS);
    }

    public void setActions(ArrayList<Action> actions) {
        put(ACTIONS, actions);
    }
}
