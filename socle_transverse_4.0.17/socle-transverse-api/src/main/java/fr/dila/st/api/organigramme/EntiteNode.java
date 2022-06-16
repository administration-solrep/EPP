package fr.dila.st.api.organigramme;

/**
 * Représentation d'un noeud ministère de l'organigramme.
 *
 * @author Fabio Esposito
 */
public interface EntiteNode extends WithSubUSNode, WithSubPosteNode {
    String getEdition();

    void setEdition(String edition);

    Long getOrdre();

    void setOrdre(Long ordre);

    String getFormule();

    void setFormule(String formule);

    String getMembreGouvernement();

    void setMembreGouvernement(String membreGouvernement);

    String getMembreGouvernementNom();

    void setMembreGouvernementNom(String membreGouvernementNom);

    String getMembreGouvernementPrenom();

    void setMembreGouvernementPrenom(String membreGouvernementPrenom);

    String getMembreGouvernementCivilite();

    void setMembreGouvernementCivilite(String membreGouvernementCivilite);

    GouvernementNode getParentGouvernement();

    void setParentGouvernement(String parentGouvernement);

    String getParentId();

    /**
     * @return the norMinistere
     */
    String getNorMinistere();

    /**
     * @param norMinistere
     *            the norMinistere to set
     */
    void setNorMinistere(String norMinistere);

    /**
     * @return the suiviActiviteNormative
     */
    boolean isSuiviActiviteNormative();

    /**
     * @param suiviActiviteNormative
     *            the suiviActiviteNormative to set
     */
    void setSuiviActiviteNormative(boolean suiviActiviteNormative);
}
