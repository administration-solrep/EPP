package fr.dila.st.api.organigramme;

import java.util.List;

/**
 * Représentation d'un noeud unité structurelle de l'organigramme.
 *
 * @author Fabio Esposito
 */
public interface UniteStructurelleNode extends OrganigrammeNode, WithSubPosteNode, WithSubUSNode {
    /**
     * Retourne la liste des entités parentes.
     *
     * @return Liste des entités parentes
     */
    List<EntiteNode> getEntiteParentList();

    /**
     * Renseigne la liste des entités parentes.
     *
     * @param entiteParentList
     *            Liste des entités parentes
     */
    void setEntiteParentList(List<EntiteNode> entiteParentList);

    /**
     * Retourne la liste des unités structurelles parentes.
     *
     * @return Liste des unités structurelles parentes
     */
    List<UniteStructurelleNode> getUniteStructurelleParentList();

    /**
     * Renseigne la liste des unités structurelles parentes.
     *
     * @param parentList
     *            Liste des unités structurelles parentes
     */
    void setUniteStructurelleParentList(List<UniteStructurelleNode> parentList);

    /**
     * Indique si cette unité structurelle contient le poste de BDC.
     *
     * @return vrai si un poste de bdc est contenu dans cette unité structurelle.
     */
    boolean containsBDC();

    void setType(OrganigrammeType type);

    void setTypeValue(String type);

    String getTypeValue();

    String getParentId();

    String getParentEntiteId();

    void setParentEntiteId(String parentEntiteId);

    String getParentUniteId();

    void setParentUniteId(String parentUniteId);

    void setParentEntiteIds(List<String> list);

    List<String> getParentEntiteIds();

    void setParentUnitIds(List<String> list);

    List<String> getParentUnitIds();

    List<NorDirection> getNorDirectionList();

    void setNorDirectionList(List<NorDirection> norDirectionList);

    String getNorDirectionForMinistereId(String ministereId);

    String getNorDirection(String ministereId);

    void setNorDirectionForMinistereId(String ministereId, String nor);

    List<InstitutionNode> getInstitutionParentList();

    void setInstitutionParentList(List<InstitutionNode> instututionParentList);

    List<String> getParentInstitIds();

    void setParentInstitIds(List<String> list);
}
