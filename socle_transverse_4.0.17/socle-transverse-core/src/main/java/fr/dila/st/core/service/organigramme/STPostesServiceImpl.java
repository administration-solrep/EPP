package fr.dila.st.core.service.organigramme;

import static fr.dila.st.api.constant.STConstant.PREFIX_POSTE;
import static fr.dila.st.core.util.PermissionHelper.checkAdminFonctionnel;
import static java.util.Optional.ofNullable;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.service.AbstractPersistenceDefaultComponent;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

public class STPostesServiceImpl extends AbstractPersistenceDefaultComponent implements STPostesService {
    private static final STLogger LOGGER = STLogFactory.getLog(STPostesServiceImpl.class);
    private static final String POSTE_SGG_QUERY =
        "SELECT p FROM PosteNode p WHERE p.superviseurSGG=TRUE AND (p.dateFin is null OR p.dateFin > :curDate) ";
    private static final String POSTE_BDC_QUERY =
        "SELECT p FROM PosteNode p WHERE p.posteBDC=TRUE AND (p.dateFin is null OR p.dateFin > :curDate) ";

    private static final String POSTE_FOR_USER_QUERY =
        "SELECT DISTINCT p FROM PosteNode p WHERE (p.membres LIKE :user OR p.membres LIKE :user2 OR p.membres LIKE :user3 OR p.membres = :user4) AND (p.dateFin is null OR p.dateFin > :curDate)";
    private static final String POSTE_LIGHT_FOR_USER_QUERY =
        "SELECT DISTINCT p.label FROM PosteNode p WHERE (p.membres LIKE :user OR p.membres LIKE :user2 OR p.membres LIKE :user3 OR p.membres = :user4) AND (p.dateFin is null OR p.dateFin > :curDate)";
    private static final String POSTE_ID_FOR_USER_QUERY =
        "SELECT DISTINCT p.idOrganigramme FROM PosteNode p WHERE (p.membres LIKE :user OR p.membres LIKE :user2 OR p.membres LIKE :user3 OR p.membres = :user4) AND (p.dateFin is null OR p.dateFin > :curDate)";
    private static final String POSTES_ENFANTS_QUERY = "SELECT p1 FROM PosteNode p1 WHERE p1.parentUniteId LIKE ?1";
    private static final String POSTES_ENFANTS_ENTITE_PARENT_QUERY =
        "SELECT p1 FROM PosteNode p1 WHERE p1.parentEntiteId LIKE ?1";
    protected static final String POSTES_ENFANTS_INSITUTION_PARENT_QUERY =
        "SELECT p1 FROM PosteNode p1 WHERE p1.parentInstitId LIKE ?1";
    private static final String ALL_POSTE_QUERY = "SELECT p FROM PosteNode p";

    public STPostesServiceImpl() {
        super("organigramme-provider");
    }

    @Override
    public PosteNode getPoste(final String posteId) {
        if (StringUtils.isNotBlank(posteId)) {
            return apply(
                true,
                manager -> {
                    return manager.find(PosteNodeImpl.class, posteId);
                }
            );
        }
        return null;
    }

    @Override
    public String getPosteLabel(String posteId) {
        return ofNullable(posteId)
            .filter(StringUtils::isNotEmpty)
            .map(this::getPoste)
            .map(PosteNode::getLabel)
            .orElse("");
    }

    @Override
    public List<PosteNode> getPostesNodes(Collection<String> postesId) {
        PosteNode nodeDoc = null;
        List<PosteNode> listNode = new ArrayList<>();

        for (String posteId : postesId) {
            nodeDoc = getPoste(posteId);
            if (nodeDoc != null) {
                listNode.add(nodeDoc);
            }
        }
        return listNode;
    }

    @Override
    public PosteNode getBarePosteModel() {
        return new PosteNodeImpl();
    }

    @Override
    public void createPoste(CoreSession coreSession, PosteNode newPoste) {
        if (newPoste.isPosteWs()) {
            checkAdminFonctionnel(coreSession, "organigramme.postews.create.permission.denied");
        }
        if (newPoste != null) {
            // Suppression des espaces avant et après le libellé M157222
            if (newPoste.getLabel() != null) {
                newPoste.setLabel(newPoste.getLabel().trim());
            }
            STServiceLocator.getOrganigrammeService().createNode(newPoste);
            // Lève un événement de création de poste
            EventProducer eventProducer = STServiceLocator.getEventProducer();
            Map<String, Serializable> eventProperties = new HashMap<>();
            eventProperties.put(STEventConstant.ORGANIGRAMME_NODE_ID_EVENT_PARAM, newPoste.getId());
            eventProperties.put(STEventConstant.ORGANIGRAMME_NODE_LABEL_EVENT_PARAM, newPoste.getLabel());
            InlineEventContext eventContext = new InlineEventContext(
                coreSession,
                coreSession.getPrincipal(),
                eventProperties
            );
            eventProducer.fireEvent(eventContext.newEvent(STEventConstant.POSTE_CREATED_EVENT));
        }
    }

    @Override
    public void updatePoste(CoreSession coreSession, PosteNode poste) {
        if (poste != null) {
            // Suppression des espaces avant et après le libellé M157222
            if (poste.getLabel() != null) {
                poste.setLabel(poste.getLabel().trim());
            }
            STServiceLocator.getOrganigrammeService().updateNode(poste, Boolean.TRUE);
            // Lève un événement de mise à jour de poste
            EventProducer eventProducer = STServiceLocator.getEventProducer();
            Map<String, Serializable> eventProperties = new HashMap<>();
            eventProperties.put(STEventConstant.ORGANIGRAMME_NODE_ID_EVENT_PARAM, poste.getId());
            eventProperties.put(STEventConstant.ORGANIGRAMME_NODE_LABEL_EVENT_PARAM, poste.getLabel());
            InlineEventContext eventContext = new InlineEventContext(
                coreSession,
                coreSession.getPrincipal(),
                eventProperties
            );
            eventProducer.fireEvent(eventContext.newEvent(STEventConstant.POSTE_UPDATED_EVENT));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PosteNode getSGGPosteNode() {
        return apply(
            true,
            manager -> {
                Query query = manager.createQuery(POSTE_SGG_QUERY);
                query.setParameter("curDate", Calendar.getInstance());

                List<PosteNode> lstPostes = query.getResultList();
                if (lstPostes != null && !lstPostes.isEmpty()) {
                    return lstPostes.get(0);
                }
                return null;
            }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PosteNode> getPosteBdcNodeList() {
        return apply(
            true,
            manager -> {
                Query query = manager.createQuery(POSTE_BDC_QUERY);
                query.setParameter("curDate", Calendar.getInstance());

                List<PosteNode> lstPostes = query.getResultList();
                if (lstPostes != null && !lstPostes.isEmpty()) {
                    return lstPostes;
                }
                return null;
            }
        );
    }

    @Override
    public List<String> getEntiteWithoutBDCInGouvernement(OrganigrammeNode gouvernementNode) {
        List<OrganigrammeNode> children = STServiceLocator
            .getOrganigrammeService()
            .getChildrenList(null, gouvernementNode, Boolean.TRUE);

        return children
            .stream()
            .filter(EntiteNode.class::isInstance)
            .filter(child -> !isPosteBdcInSubNode(child, null))
            .map(OrganigrammeNode::getLabel)
            .collect(Collectors.toList());
    }

    /**
     * Renvoie true si au moins un des enfants est un poste bdc
     *
     * @param node
     * @param session
     * @return
     */
    private boolean isPosteBdcInSubNode(OrganigrammeNode node, CoreSession session) {
        List<OrganigrammeNode> children = STServiceLocator
            .getOrganigrammeService()
            .getChildrenList(session, node, Boolean.TRUE);

        boolean hasPosteBDC = children
            .stream()
            .anyMatch(
                subNode ->
                    (subNode instanceof PosteNode && ((PosteNode) subNode).isPosteBdc()) ||
                    isPosteBdcInSubNode(subNode, session)
            );

        if (!hasPosteBDC && node instanceof EntiteNode) {
            LOGGER.warn(null, STLogEnumImpl.FAIL_GET_POSTE_TEC, "Aucun poste BDC pour " + node.getLabel());
        }

        return hasPosteBDC;
    }

    @Override
    public PosteNode getPosteBdcInEntite(String entiteId) {
        OrganigrammeNode entiteNode = STServiceLocator
            .getOrganigrammeService()
            .getOrganigrammeNodeById(entiteId, OrganigrammeType.MINISTERE);
        return getPosteBdcInSubNode(entiteNode);
    }

    /**
     * Renvoie le poste bdc s'il y en a un dans les noeuds enfants
     *
     * @param node
     * @return
     */
    private PosteNode getPosteBdcInSubNode(OrganigrammeNode node) {
        List<OrganigrammeNode> children = STServiceLocator
            .getOrganigrammeService()
            .getChildrenList(null, node, Boolean.TRUE);

        for (OrganigrammeNode child : children) {
            if (child instanceof PosteNode) {
                if (((PosteNode) child).isPosteBdc()) {
                    return (PosteNode) child;
                }
            } else {
                OrganigrammeNode posteBdc = getPosteBdcInSubNode(child);
                if (posteBdc != null) {
                    return (PosteNode) posteBdc;
                }
            }
        }

        return null;
    }

    @Override
    public List<OrganigrammeNode> getPosteBdcListInEntite(String entiteId) {
        OrganigrammeNode entiteNode = STServiceLocator
            .getOrganigrammeService()
            .getOrganigrammeNodeById(entiteId, OrganigrammeType.MINISTERE);
        List<OrganigrammeNode> bdcList = new ArrayList<>();
        getPosteBdcListInSubNode(entiteNode, bdcList);
        return bdcList;
    }

    /**
     * Renvoie le poste bdc s'il y en a un dans les noeuds enfants
     *
     * @param node
     * @return
     */
    private void getPosteBdcListInSubNode(OrganigrammeNode node, List<OrganigrammeNode> bdcList) {
        if (bdcList == null) {
            bdcList = new ArrayList<>();
        }

        List<OrganigrammeNode> children = STServiceLocator
            .getOrganigrammeService()
            .getChildrenList(null, node, Boolean.TRUE);
        for (OrganigrammeNode child : children) {
            if (child instanceof PosteNode) {
                if (((PosteNode) child).isPosteBdc()) {
                    bdcList.add(child);
                }
            } else {
                getPosteBdcListInSubNode(child, bdcList);
            }
        }
    }

    @Override
    public List<STUser> getUserFromPoste(String posteId) {
        PosteNode orgaNode = getPoste(posteId);
        return orgaNode.getUserList();
    }

    @Override
    public List<String> getUserNamesFromPoste(String posteId) {
        PosteNode posteNode = getPoste(posteId);
        if (posteNode != null) {
            return posteNode.getMembers();
        }

        return new ArrayList<>();
    }

    @Override
    public void deactivateBdcPosteList(List<OrganigrammeNode> bdcList) {
        final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        // désactive le(s) bdc
        for (OrganigrammeNode toCancelBdcNode : bdcList) {
            PosteNode bdcPosteNode = (PosteNode) toCancelBdcNode;
            bdcPosteNode.setPosteBdc(false);
            organigrammeService.updateNode(bdcPosteNode, false);
        }
    }

    @Override
    public void addBdcPosteToNewPosteBdc(final Map<String, List<OrganigrammeNode>> posteBdcToMigrate) {
        final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        final List<OrganigrammeNode> nodeToSaveList = new ArrayList<>();
        final Map<String, Set<String>> mapMembers = new HashMap<>();

        for (final Entry<String, List<OrganigrammeNode>> entryOldPosteBdc : posteBdcToMigrate.entrySet()) {
            final List<OrganigrammeNode> postesBdc = entryOldPosteBdc.getValue();
            final OrganigrammeNode oldPosteBdc = organigrammeService.getOrganigrammeNodeById(
                entryOldPosteBdc.getKey(),
                OrganigrammeType.POSTE
            );
            final List<String> members = ((PosteNode) oldPosteBdc).getMembers();
            for (final OrganigrammeNode newPosteBdc : postesBdc) {
                if (mapMembers.get(newPosteBdc.getId()) == null) {
                    mapMembers.put(newPosteBdc.getId(), new HashSet<String>());
                }
                final Set<String> newMembers = mapMembers.get(newPosteBdc.getId());
                newMembers.addAll(members);
                newMembers.addAll(((PosteNode) newPosteBdc).getMembers());
                mapMembers.put(newPosteBdc.getId(), newMembers);
            }
        }

        for (Entry<String, Set<String>> entryNewPosteBdc : mapMembers.entrySet()) {
            final OrganigrammeNode newPosteBdc = organigrammeService.getOrganigrammeNodeById(
                entryNewPosteBdc.getKey(),
                OrganigrammeType.POSTE
            );
            ((PosteNode) newPosteBdc).setMembers(new ArrayList<>(entryNewPosteBdc.getValue()));
            nodeToSaveList.add(newPosteBdc);
        }
        organigrammeService.updateNodes(nodeToSaveList, false);
    }

    @Override
    public boolean userHasOnePosteOnly(PosteNode posteNode) {
        List<STUser> userList = posteNode.getUserList();
        for (STUser user : userList) {
            if (user.getPostes().size() == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<STUser> getUsersHavingOnePosteOnly(PosteNode posteNode) {
        return posteNode
            .getUserList()
            .stream()
            .filter(user -> user.isActive() && user.getPostes().size() == 1)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getPosteIdInSubNode(OrganigrammeNode node) {
        List<String> list = new ArrayList<>();
        List<OrganigrammeNode> children = STServiceLocator
            .getOrganigrammeService()
            .getChildrenList(null, node, Boolean.TRUE);

        for (OrganigrammeNode child : children) {
            if (child instanceof PosteNode) {
                list.add(child.getId());
            } else {
                List<String> childList = getPosteIdInSubNode(child);
                if (childList != null && !childList.isEmpty()) {
                    list.addAll(childList);
                }
            }
        }
        return list;
    }

    @Override
    public List<EntiteNode> getEntitesParents(final PosteNode poste) {
        return STServiceLocator.getSTMinisteresService().getMinistereParentFromPoste(poste.getId());
    }

    @Override
    public List<UniteStructurelleNode> getUniteStructurelleParentList(final PosteNode poste) {
        return poste.getUniteStructurelleParentList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PosteNode> getAllPostesForUser(final String userId) {
        return apply(
            true,
            manager -> {
                List<PosteNode> lstNodes = new ArrayList<>();

                if (StringUtils.isNotEmpty(userId)) {
                    Query query = manager.createQuery(POSTE_FOR_USER_QUERY);
                    if (userId.contains("%")) {
                        query.setParameter("user", userId);
                        query.setParameter("user2", userId);
                        query.setParameter("user3", userId);
                        query.setParameter("user4", userId);
                    } else {
                        query.setParameter("user", "%;" + userId);
                        query.setParameter("user2", userId + ";%");
                        query.setParameter("user3", "%;" + userId + ";%");
                        query.setParameter("user4", userId);
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
    public List<String> getAllPosteNameForUser(final String userId) {
        return apply(
            true,
            manager -> {
                List<String> lstNodes = new ArrayList<>();

                if (StringUtils.isNotEmpty(userId)) {
                    Query query = manager.createQuery(POSTE_LIGHT_FOR_USER_QUERY);
                    if (userId.contains("%")) {
                        query.setParameter("user", userId);
                        query.setParameter("user2", userId);
                        query.setParameter("user3", userId);
                        query.setParameter("user4", userId);
                    } else {
                        query.setParameter("user", "%;" + userId);
                        query.setParameter("user2", userId + ";%");
                        query.setParameter("user3", "%;" + userId + ";%");
                        query.setParameter("user4", userId);
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
    public List<String> getAllPosteIdsForUser(final String userId) {
        return apply(
            true,
            manager -> {
                List<String> lstNodes = new ArrayList<>();
                if (StringUtils.isNotEmpty(userId)) {
                    Query query = manager.createQuery(POSTE_ID_FOR_USER_QUERY);
                    if (userId.contains("%")) {
                        query.setParameter("user", userId);
                        query.setParameter("user2", userId);
                        query.setParameter("user3", userId);
                        query.setParameter("user4", userId);
                    } else {
                        query.setParameter("user", "%;" + userId);
                        query.setParameter("user2", userId + ";%");
                        query.setParameter("user3", "%;" + userId + ";%");
                        query.setParameter("user4", userId);
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
    public List<PosteNode> getPosteNodeEnfant(final String nodeId, final OrganigrammeType type) {
        return apply(
            true,
            manager -> {
                List<PosteNode> lstPosteNodes = new ArrayList<>();

                Query query = null;
                if (type == OrganigrammeType.MINISTERE) {
                    query = manager.createQuery(POSTES_ENFANTS_ENTITE_PARENT_QUERY);
                } else if (type == OrganigrammeType.DIRECTION || type == OrganigrammeType.UNITE_STRUCTURELLE) {
                    query = manager.createQuery(POSTES_ENFANTS_QUERY);
                } else if (type == OrganigrammeType.INSTITUTION) {
                    query = manager.createQuery(POSTES_ENFANTS_INSITUTION_PARENT_QUERY);
                }
                if (nodeId != null && query != null) {
                    query.setParameter(1, "%" + nodeId + "%");
                    lstPosteNodes = query.getResultList();
                }
                return lstPosteNodes;
            }
        );
    }

    @Override
    public void addUserToPostes(final List<String> postes, final String username) {
        accept(
            true,
            manager -> {
                List<PosteNode> lstPostes = getPostesNodes(postes);
                for (PosteNode node : lstPostes) {
                    if (!node.getMembers().contains(username)) {
                        List<String> membres = node.getMembers();
                        membres.add(username);
                        node.setMembers(membres);
                        manager.merge(node);
                        manager.flush();
                    }
                }
            }
        );
    }

    @Override
    public void removeUserFromPoste(final String posteId, final String userName) {
        accept(
            true,
            manager -> {
                PosteNode poste = getPoste(posteId);
                List<String> members = poste.getMembers();
                if (members.contains(userName)) {
                    members.remove(userName);
                    poste.setMembers(members);
                    manager.merge(poste);
                    manager.flush();
                }
            }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PosteNode> getAllPostes() {
        try {
            return apply(
                true,
                manager -> {
                    Query query = manager.createQuery(ALL_POSTE_QUERY);

                    List<PosteNode> lstPostes = query.getResultList();
                    if (lstPostes != null && !lstPostes.isEmpty()) {
                        return lstPostes;
                    }
                    return null;
                }
            );
        } catch (NuxeoException e) {
            return null;
        }
    }

    @Override
    public String deleteUserFromAllPostes(final String userId) {
        try {
            return apply(
                true,
                manager -> {
                    // suppression des utilisateurs en début de chaine
                    String queryUpdatePost =
                        "UPDATE Poste p SET p.membres = REGEXP_REPLACE(p.membres, '^" +
                        userId +
                        ";','') WHERE p.membres LIKE '" +
                        userId +
                        "%'";
                    executeUpdateQuery(manager, queryUpdatePost);

                    // suppression des utilisateurs en milieu de chaine
                    queryUpdatePost =
                        "UPDATE Poste p SET p.membres = REPLACE(p.membres, ';" +
                        userId +
                        ";',';') WHERE p.membres LIKE '%" +
                        userId +
                        "%'";
                    executeUpdateQuery(manager, queryUpdatePost);

                    // suppression des utilisateurs en fin de chaine
                    queryUpdatePost =
                        "UPDATE Poste p SET p.membres = REGEXP_REPLACE(p.membres, ';" +
                        userId +
                        "$','') WHERE p.membres LIKE '%" +
                        userId +
                        "'";
                    executeUpdateQuery(manager, queryUpdatePost);

                    // Suppression des utilisateurs qui sont seuls dans le poste
                    queryUpdatePost = "UPDATE Poste p SET p.membres = '' WHERE p.membres = '" + userId + "'";
                    executeUpdateQuery(manager, queryUpdatePost);
                    return "L'utilisateur " + userId + " a été supprimé";
                }
            );
        } catch (NuxeoException e) {
            return "Une erreur s'est produite " + e;
        }
    }

    private static void executeUpdateQuery(EntityManager manager, String updateQuery) {
        Query query = manager.createNativeQuery(updateQuery);
        query.executeUpdate();
    }

    @Override
    public boolean haveAnyCommonPoste(String userId1, String userId2) {
        return CollectionUtils.containsAny(getAllPosteIdsForUser(userId1), getAllPosteIdsForUser(userId2));
    }

    @Override
    public String prefixPosteId(String id) {
        return StringUtils.prependIfMissing(id, PREFIX_POSTE);
    }
}
