package fr.dila.st.core.organigramme;

import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UserNode;

/**
 * Représentation d'un neoud de l'organigramme.
 *
 * @author Fabio Esposito
 */
public class UserNodeImpl implements UserNode {
    // private String fullPath;
    private String label;
    private String id;
    private Boolean active;

    /**
     * Default constructor
     */
    public UserNodeImpl() {
        // do nothing
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public OrganigrammeType getType() {
        return OrganigrammeType.USER;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }
}
