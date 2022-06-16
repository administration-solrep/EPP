package fr.dila.st.api.organigramme;

import fr.dila.st.api.user.STUser;
import java.util.List;

/**
 * Représentation d'un noeud poste de l'organigramme.
 *
 * @author Fabio Esposito
 */
public interface PosteNode extends OrganigrammeNode {
    /**
     * Retourne la liste des agents du poste.
     *
     * @return Liste des agents du poste
     */
    List<String> getMembers();

    /**
     * Renseigne la liste des agents du poste.
     *
     * @param members
     *            Liste des agents du poste
     */
    void setMembers(List<String> members);

    /**
     * Retourne la liste des entités parentes.
     *
     * @return Liste des entités parentes
     */
    List<EntiteNode> getEntiteParentList();

    /**
     * Renseigne la liste des entités parentes.
     *
     * param parentList Liste des entités parentes
     */
    void setEntiteParentList(List<EntiteNode> parentList);

    /**
     * Retourne la liste des unités structurelles parentes.
     *
     * @return Liste des unités structurelles parentes
     */
    List<UniteStructurelleNode> getUniteStructurelleParentList();

    /**
     * Renseigne la liste des unités structurelles parentes.
     *
     * param parentList Liste des unités structurelles parentes
     */
    void setUniteStructurelleParentList(List<UniteStructurelleNode> parentList);

    /**
     * Retourne la liste des utilisateurs actifs du poste.
     *
     * @return Liste des utilisateurs du poste
     */
    List<STUser> getUserList();

    /**
     * Retourne le nombre d'utilisateurs du poste.
     *
     * @return Nombre d'utilisateurs du poste
     */
    int getUserListSize();

    /**
     * True si le poste est un bureau des cabinets.
     *
     * @return posteBdc
     */
    boolean isPosteBdc();

    /**
     * True si le poste est un bureau des cabinets.
     *
     * @param posteBdc
     */
    void setPosteBdc(boolean posteBdc);

    /**
     * True si le poste est un poste du sgg
     */
    boolean isChargeMissionSGG();

    /**
     * True si le poste est un poste du sgg
     *
     * @param chargeMissionSGG
     */
    void setChargeMissionSGG(boolean chargeMissionSGG);

    /**
     * True si le poste est un poste du premier ministre
     */
    boolean isConseillerPM();

    /**
     * True si le poste est un poste du premier ministre
     *
     * @param conseillerPM
     */
    void setConseillerPM(boolean conseillerPM);

    /**
     * Renseigne l'url au webservice de notifications
     */
    void setWsUrl(String wsUrl);

    /**
     * Renseigne le nom d'utilisateur webservice
     */
    void setWsUser(String wsUser);

    /**
     * Renseigne le mot de passe webservice
     */
    void setWsPassword(String wsPassword);

    /**
     * Renseigne l'alias de la clé SSL
     */
    void setWsKeyAlias(String keyAlias);

    /**
     * Retourne l'url au webservice de notifications
     */
    String getWsUrl();

    /**
     * Retourne le nom d'utilisateur webservice
     */
    String getWsUser();

    /**
     * Retourne le mot de passe webservice
     */
    String getWsPassword();

    /**
     * retourne l'alias de la clé SSL
     */
    String getWsKeyAlias();

    /**
     * True si le poste est un poste webservice.
     *
     * @return posteWs
     */
    boolean isPosteWs();

    /**
     * True si le poste est un poste webservice.
     *
     * @param posteWs
     */
    void setPosteWs(boolean posteWs);

    String getParentId();

    public boolean isSuperviseurSGG();

    public void setSuperviseurSGG(boolean superviseurSGG);

    String getParentEntiteId();

    void setParentEntiteId(String parentEntiteId);

    String getParentUniteId();

    void setParentUniteId(String parentUniteId);

    List<String> getParentEntiteIds();

    void setParentEntiteIds(List<String> list);

    List<String> getParentUnitIds();

    void setParentUnitIds(List<String> list);

    void setParentInstitIds(List<String> list);

    List<String> getParentInstitIds();

    List<InstitutionNode> getInstitutionParentList();

    void setInstitutionParentList(List<InstitutionNode> instututionParentList);

    EntiteNode getFirstEntiteParent();

    UniteStructurelleNode getFirstUSParent();
}
