package fr.dila.st.core.service.organigramme;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.AbstractPersistenceDefaultComponent;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;

/**
 *
 *
 */
public class STUsAndDirectionServiceImpl
    extends AbstractPersistenceDefaultComponent
    implements STUsAndDirectionService {
    private static final String UNITE_STRUCT_ENFANTS_QUERY =
        "SELECT u1 FROM UniteStructurelleNode u1 WHERE u1.parentUniteId LIKE ?1";
    private static final String UNITE_STRUCT_ENFANTS_ENTITE_PARENT_QUERY =
        "SELECT u1 FROM UniteStructurelleNode u1 WHERE u1.parentEntiteId LIKE ?1";
    private static final String UNITE_STRUCT_ENFANTS_INSTIT_PARENT_QUERY =
        "SELECT u1 FROM UniteStructurelleNode u1 WHERE u1.parentInstitId LIKE ?1";

    private static final String DIRECTION_FOR_USER_QUERY =
        "SELECT DISTINCT u FROM UniteStructurelleNode u, PosteNode p WHERE p.membres LIKE :user AND u.type='DIR' AND u.dateFin>:curDate AND p.parentUniteId=u.idOrganigramme";
    private static final String DIRECTION_LIGHT_FOR_USER_QUERY =
        "SELECT DISTINCT u.label FROM UniteStructurelleNode u, PosteNode p WHERE p.membres LIKE :user AND u.type='DIR' AND u.dateFin>:curDate AND p.parentUniteId=u.idOrganigramme";

    public STUsAndDirectionServiceImpl() {
        super("organigramme-provider");
    }

    @Override
    public List<STUser> getUserFromUniteStructurelle(String uniteStructurelleId) {
        final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();

        OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(
            uniteStructurelleId,
            OrganigrammeType.UNITE_STRUCTURELLE
        );
        return organigrammeService.getUsersInSubNode(node);
    }

    @Override
    public void updateUniteStructurelle(UniteStructurelleNode uniteStructurelle) {
        STServiceLocator.getOrganigrammeService().updateNode(uniteStructurelle, Boolean.TRUE);
    }

    @Override
    public UniteStructurelleNode createUniteStructurelle(UniteStructurelleNode newUniteStructurelle) {
        return (UniteStructurelleNode) STServiceLocator.getOrganigrammeService().createNode(newUniteStructurelle);
    }

    @Override
    public UniteStructurelleNode getBareUniteStructurelleModel() {
        return new UniteStructurelleNodeImpl();
    }

    @Override
    public List<OrganigrammeNode> getDirectionFromPoste(String posteId) {
        List<OrganigrammeNode> directionsList = new ArrayList<>();

        OrganigrammeNode node = STServiceLocator
            .getOrganigrammeService()
            .getOrganigrammeNodeById(posteId, OrganigrammeType.POSTE);
        if (node != null) {
            getDirectionParent(directionsList, node);
        }

        return directionsList;
    }

    /**
     * Méthode récursive qui remontent les parents et retourne les directions
     *
     * @param directionsList
     * @param node
     * @throws ClientException
     */
    private void getDirectionParent(List<OrganigrammeNode> directionsList, OrganigrammeNode node) {
        List<OrganigrammeNode> parentList = STServiceLocator.getOrganigrammeService().getParentList(node);
        for (OrganigrammeNode parentNode : parentList) {
            if (parentNode != null) {
                if (OrganigrammeType.DIRECTION.equals(parentNode.getType())) {
                    directionsList.add(parentNode);
                    getDirectionParent(directionsList, parentNode);
                } else if (OrganigrammeType.UNITE_STRUCTURELLE.equals(parentNode.getType())) {
                    getDirectionParent(directionsList, parentNode);
                }
            }
        }
    }

    @Override
    public List<OrganigrammeNode> getUniteStructurelleFromPoste(String posteId) {
        List<OrganigrammeNode> unitesStructurellesList = new ArrayList<>();

        OrganigrammeNode node = STServiceLocator
            .getOrganigrammeService()
            .getOrganigrammeNodeById(posteId, OrganigrammeType.POSTE);
        if (node != null) {
            getUniteStructurelleParent(unitesStructurellesList, node);
        }

        return unitesStructurellesList;
    }

    /**
     * Méthode récursive qui remontent les parents et retourne les directions
     *
     * @param directionsList
     * @param node
     * @throws ClientException
     */
    private void getUniteStructurelleParent(List<OrganigrammeNode> unitesStructurellesList, OrganigrammeNode node) {
        List<OrganigrammeNode> parentList = STServiceLocator.getOrganigrammeService().getParentList(node);
        for (OrganigrammeNode parentNode : parentList) {
            if (parentNode != null) {
                if (
                    OrganigrammeType.DIRECTION.equals(parentNode.getType()) ||
                    OrganigrammeType.UNITE_STRUCTURELLE.equals(parentNode.getType())
                ) {
                    unitesStructurellesList.add(parentNode);
                    getDirectionParent(unitesStructurellesList, parentNode);
                }
            }
        }
    }

    @Override
    public UniteStructurelleNode getUniteStructurelleNode(String usId) {
        return (UniteStructurelleNode) STServiceLocator
            .getOrganigrammeService()
            .getOrganigrammeNodeById(usId, OrganigrammeType.UNITE_STRUCTURELLE);
    }

    @Override
    public boolean isDirectionFromMinistere(UniteStructurelleNode directionNode, EntiteNode ministereNode) {
        if (ministereNode == null || directionNode == null) {
            return false;
        }

        return STServiceLocator.getOrganigrammeService().checkNodeContainsChild(ministereNode, directionNode);
    }

    @Override
    public List<UniteStructurelleNode> getDirectionListFromMinistere(EntiteNode ministereNode) {
        List<UniteStructurelleNode> list = new ArrayList<>();

        for (UniteStructurelleNode child : ministereNode.getSubUnitesStructurellesList()) {
            if (child.getType() == OrganigrammeType.DIRECTION) {
                list.add(child);
            }
        }
        return list;
    }

    @Override
    public List<String> findUserFromUniteStructurelle(String uniteStructurelleId) {
        final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(
            uniteStructurelleId,
            OrganigrammeType.UNITE_STRUCTURELLE
        );
        if (node != null) {
            return organigrammeService.findUserInSubNode(node);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<UniteStructurelleNode> getUniteStructurelleParent(final UniteStructurelleNode node) {
        return node.getUniteStructurelleParentList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UniteStructurelleNode> getUniteStructurelleEnfant(final String nodeID, final OrganigrammeType type) {
        return apply(
            true,
            manager -> {
                List<UniteStructurelleNode> lstUniteStructurelleNodes = new ArrayList<>();

                Query query = null;
                if (type == OrganigrammeType.MINISTERE) {
                    query = manager.createQuery(UNITE_STRUCT_ENFANTS_ENTITE_PARENT_QUERY);
                } else if (type == OrganigrammeType.DIRECTION || type == OrganigrammeType.UNITE_STRUCTURELLE) {
                    query = manager.createQuery(UNITE_STRUCT_ENFANTS_QUERY);
                } else if (type == OrganigrammeType.INSTITUTION) {
                    query = manager.createQuery(UNITE_STRUCT_ENFANTS_INSTIT_PARENT_QUERY);
                }
                if (StringUtils.isNotBlank(nodeID) && query != null) {
                    query.setParameter(1, "%" + nodeID + "%");
                    lstUniteStructurelleNodes = query.getResultList();
                }
                return lstUniteStructurelleNodes;
            }
        );
    }

    @Override
    public List<EntiteNode> getEntiteParent(final UniteStructurelleNode node) {
        return node.getEntiteParentList();
    }

    protected List<String> extractIdsFromElement(List<? extends OrganigrammeNode> lstOrga) {
        List<String> lstIds = new ArrayList<>();

        for (OrganigrammeNode orga : lstOrga) {
            lstIds.add(orga.getId().toString());
        }

        return lstIds;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UniteStructurelleNode> getDirectionsParentsFromUser(final String userId) {
        return apply(
            true,
            manager -> {
                List<UniteStructurelleNode> lstNodes = new ArrayList<>();
                if (StringUtils.isNotEmpty(userId)) {
                    Query query = manager.createQuery(DIRECTION_FOR_USER_QUERY);
                    if (userId.contains("%")) {
                        query.setParameter("user", userId);
                    } else {
                        query.setParameter("user", "%" + userId + "%");
                    }
                    query.setParameter("curDate", Calendar.getInstance());
                    lstNodes = query.getResultList();
                }
                return lstNodes;
            }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getDirectionNameParentsFromUser(final String userId) {
        return apply(
            true,
            manager -> {
                List<String> lstNodes = new ArrayList<>();
                if (StringUtils.isNotEmpty(userId)) {
                    Query query = manager.createQuery(DIRECTION_LIGHT_FOR_USER_QUERY);
                    if (userId.contains("%")) {
                        query.setParameter("user", userId);
                    } else {
                        query.setParameter("user", "%" + userId + "%");
                    }
                    query.setParameter("curDate", Calendar.getInstance());
                    lstNodes = query.getResultList();
                }
                return lstNodes;
            }
        );
    }

    @Override
    public String getLabel(String idUniteStructurelle, String defaultLabel) {
        return Optional
            .ofNullable(getUniteStructurelleNode(idUniteStructurelle))
            .map(UniteStructurelleNode::getLabel)
            .orElse(defaultLabel);
    }

    @Override
    public String getLabel(String directionPilote) {
        return getLabel(directionPilote, null);
    }
}
