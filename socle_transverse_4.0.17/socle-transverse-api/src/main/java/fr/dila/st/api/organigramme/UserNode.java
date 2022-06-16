package fr.dila.st.api.organigramme;

public interface UserNode {
    String getId();

    void setId(String anid);

    String getLabel();

    void setLabel(String label);

    OrganigrammeType getType();

    boolean isActive();

    void setActive(Boolean active);
}
