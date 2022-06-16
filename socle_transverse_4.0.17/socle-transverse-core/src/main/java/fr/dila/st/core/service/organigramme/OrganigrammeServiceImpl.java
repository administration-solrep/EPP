package fr.dila.st.core.service.organigramme;

import static fr.dila.st.api.organigramme.OrganigrammeType.MINISTERE;
import static fr.dila.st.core.util.PermissionHelper.checkAdminFonctionnel;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.organigramme.DirSessionContainer;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.api.organigramme.WithSubEntitiesNode;
import fr.dila.st.api.organigramme.WithSubPosteNode;
import fr.dila.st.api.organigramme.WithSubUSNode;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STException;
import fr.dila.st.core.organigramme.DirSessionContainerImpl;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.GouvernementNodeImpl;
import fr.dila.st.core.organigramme.InstitutionNodeImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.organigramme.UserNodeImpl;
import fr.dila.st.core.service.AbstractPersistenceDefaultComponent;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.uidgen.UIDGeneratorService;
import org.nuxeo.ecm.core.uidgen.UIDSequencer;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Implémentation du service de gestion de l'organigramme.
 *
 * @author FEO, asatre
 */
public abstract class OrganigrammeServiceImpl
    extends AbstractPersistenceDefaultComponent
    implements OrganigrammeService {
    private static final int ID_LDAP_DEFAULT = 60000000;
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -2392698015083550568L;

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog(OrganigrammeServiceImpl.class);

    private static final String ENTITE_LABEL_QUERY =
        "SELECT e FROM EntiteNode e WHERE e.label = :label AND (e.deleted=false OR e.deleted is NULL) AND (e.dateFin is null OR e.dateFin > :curDate)";
    private static final String UNITE_LABEL_QUERY =
        "SELECT u FROM UniteStructurelleNode u WHERE u.label = :label AND (u.deleted=false OR u.deleted is NULL) AND (u.dateFin is null OR u.dateFin > :curDate)";
    private static final String POSTE_LABEL_QUERY =
        "SELECT p FROM PosteNode p WHERE p.label = :label AND (p.deleted=false OR p.deleted is NULL) AND (p.dateFin is null OR p.dateFin > :curDate)";
    private static final String ENTITE_LIKE_LABEL_QUERY =
        "SELECT e FROM EntiteNode e WHERE LOWER(e.label) LIKE LOWER(:label) AND (e.deleted=false OR e.deleted is NULL) AND (e.dateFin is null OR e.dateFin > :curDate)";
    private static final String GVT_LIKE_LABEL_QUERY =
        "SELECT g FROM GouvernementNode g WHERE LOWER(g.label) LIKE LOWER(:label) AND (g.deleted=false OR g.deleted is NULL) AND (g.dateFin is null OR g.dateFin > :curDate)";
    private static final String UNITE_LIKE_LABEL_QUERY =
        "SELECT u FROM UniteStructurelleNode u WHERE LOWER(u.label) LIKE LOWER(:label) AND (u.deleted=false OR u.deleted is NULL) AND (u.dateFin is null OR u.dateFin > :curDate)";
    private static final String POSTE_LIKE_LABEL_QUERY =
        "SELECT p FROM PosteNode p WHERE LOWER(p.label) LIKE LOWER(:label) AND (p.deleted=false OR p.deleted is NULL) AND (p.dateFin is null OR p.dateFin > :curDate)";
    private static final String LOCK_ENTITE_QUERY = "SELECT e FROM EntiteNode e WHERE e.lockUser is not NULL";
    private static final String LOCK_UNITE_QUERY = "SELECT u FROM UniteStructurelleNode u WHERE u.lockUser is not NULL";
    private static final String LOCK_POSTE_QUERY = "SELECT p FROM PosteNode p WHERE p.lockUser is not NULL";
    protected static final String INSTIT_LIKE_LABEL_QUERY =
        "SELECT i FROM InstitutionNode i WHERE LOWER(i.label) LIKE LOWER(:label) AND (i.deleted=false OR i.deleted is NULL) AND (i.dateFin is null OR i.dateFin > :curDate)";

    public OrganigrammeServiceImpl() {
        super("organigramme-provider");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends OrganigrammeNode> T getOrganigrammeNodeById(final String nodeId, final OrganigrammeType type) {
        // Cas si on nous retourne un identifiant null ou vide
        if (StringUtils.isNotBlank(nodeId)) {
            OrganigrammeNode node = apply(
                true,
                manager -> {
                    switch (type) {
                        case GOUVERNEMENT:
                            return manager.find(GouvernementNodeImpl.class, nodeId);
                        case UNITE_STRUCTURELLE:
                        case DIRECTION:
                            return manager.find(UniteStructurelleNodeImpl.class, nodeId);
                        case INSTITUTION:
                            return manager.find(InstitutionNodeImpl.class, nodeId);
                        case MINISTERE:
                            return manager.find(EntiteNodeImpl.class, nodeId);
                        case POSTE:
                            return manager.find(PosteNodeImpl.class, nodeId);
                        default:
                            return null;
                    }
                }
            );
            return (T) node;
        }

        return null;
    }

    @Override
    public OrganigrammeNode getOrganigrammeNodeById(final String nodeId) {
        OrganigrammeNode node = getOrganigrammeNodeById(nodeId, OrganigrammeType.GOUVERNEMENT);
        if (node == null) {
            node = getOrganigrammeNodeById(nodeId, OrganigrammeType.MINISTERE);
        }
        if (node == null) {
            node = getOrganigrammeNodeById(nodeId, OrganigrammeType.DIRECTION);
        }
        if (node == null) {
            node = getOrganigrammeNodeById(nodeId, OrganigrammeType.POSTE);
        }
        return node;
    }

    @Override
    public List<OrganigrammeNode> getOrganigrammeNodesById(Map<String, OrganigrammeType> elems) {
        List<OrganigrammeNode> list = new ArrayList<>();
        for (Entry<String, OrganigrammeType> nodeElem : elems.entrySet()) {
            OrganigrammeNode node = getOrganigrammeNodeById(nodeElem.getKey(), nodeElem.getValue());
            if (node != null) {
                list.add(node);
            }
        }
        return list;
    }

    protected OrganigrammeNode adaptDocModelToOrganigrammeNode(DocumentModel doc) {
        if (doc == null) {
            return null;
        }
        return doc.getAdapter(OrganigrammeNode.class);
    }

    @Override
    public List<? extends OrganigrammeNode> getRootNodes() {
        return STServiceLocator.getSTGouvernementService().getGouvernementList();
    }

    @Override
    public Object deleteFromDn(CoreSession session, final OrganigrammeNode node, final boolean notifyUser) {
        if (node != null) {
            if (node.getType() == MINISTERE) {
                checkAdminFonctionnel(session, "organigramme.entite.delete.permission.denied");
            }
            return apply(
                true,
                manager -> {
                    try (CloseableCoreSession coreSession = SessionUtil.openSession()) {
                        return recursiveDeleteNode(coreSession, node, notifyUser, manager);
                    }
                }
            );
        }

        return null;
    }

    /**
     * Met les noeuds à l'etat deleted cherche les postes dans les enfants et
     * empêche la suppression si des FDR actives sont liées à ces postes
     *
     * et renvoi un message d'erreur au client
     *
     * @return un Optional d'un objet NodeDeletionProblemsExcelGenerator
     */
    protected NodeDeletionProblemsExcelGenerator recursiveDeleteNode(
        CoreSession coreSession,
        OrganigrammeNode nodeToDelete,
        boolean notifyUser,
        EntityManager manager
    ) {
        List<OrganigrammeNode> nodeToUpdateList = new ArrayList<>();

        // Vérifie si le noeud peut être supprimé
        Collection<OrganigrammeNodeDeletionProblem> problems = validateDeleteNode(coreSession, nodeToDelete);

        if (problems.isEmpty()) {
            // Aucun problème on peut supprimer !

            // met deleted à true
            recursiveDelete(coreSession, nodeToDelete, nodeToUpdateList, manager);

            if (notifyUser) {
                notifyNodeDeletionToUser(nodeToUpdateList);
            }

            // sauve la liste
            for (OrganigrammeNode node : nodeToUpdateList) {
                manager.merge(node);
                manager.flush();
            }

            return null;
        } else {
            // On initialise l'objet qui permettra par la suite de générer à la demande le
            // fichier Excel reprenant tous les problèmes rencontrés
            return new NodeDeletionProblemsExcelGenerator(nodeToDelete, problems);
        }
    }

    private void notifyNodeDeletionToUser(List<OrganigrammeNode> nodeToUpdateList) {
        // Envoie un mail aux utilisateurs du poste et les enlève de la liste des
        // membres
        STMailService stMailService = STServiceLocator.getSTMailService();
        String objet = "Suppression d'un poste";
        StringBuilder texte = new StringBuilder();
        for (OrganigrammeNode node : nodeToUpdateList) {
            if (node instanceof PosteNode) {
                PosteNode poste = (PosteNode) node;
                if (poste.getUserList() != null && !poste.getUserList().isEmpty()) {
                    texte.append("Le poste ").append(poste.getLabel()).append(" vient d'être supprimé.");
                    try {
                        stMailService.sendMailNotificationToUserList(poste.getUserList(), objet, texte.toString());
                    } catch (NuxeoException e) {
                        LOGGER.error("Erreur d'envoi du mail de suppression de poste : " + poste.getLabel(), e);
                    }
                    poste.setMembers(null);
                }
            }
        }
    }

    /**
     * Met le boolean deleted a true et ajoute les noeuds dans une liste à sauver. Supprime les enfants s'ils n'ont
     * qu'un parent
     */
    protected void recursiveDelete(
        CoreSession session,
        OrganigrammeNode nodeToDelete,
        List<OrganigrammeNode> nodeToSaveList,
        EntityManager manager
    ) {
        nodeToDelete.setDeleted(true);
        nodeToDelete.setDateFin(new Date());
        nodeToSaveList.add(nodeToDelete);

        OrganigrammeNode nodeUpToDate = getOrganigrammeNodeById(nodeToDelete.getId(), nodeToDelete.getType());
        List<OrganigrammeNode> nodeList = getChildrenList(session, nodeUpToDate, Boolean.TRUE);

        for (OrganigrammeNode childNode : nodeList) {
            if (childNode.getParentListSize() == 1) {
                recursiveDelete(session, childNode, nodeToSaveList, manager);
            } else {
                nodeToSaveList.add(childNode);
            }
        }
        notifyDelete(session, nodeToDelete);
    }

    @Override
    public void disableNodeFromDn(CoreSession session, final String selectedNode, final OrganigrammeType type) {
        if (type == MINISTERE) {
            checkAdminFonctionnel(session, "organigramme.entite.disable.permission.denied");
        }
        accept(true, manager -> updateActivationNodeFromDn(selectedNode, type, Calendar.getInstance(), true, manager));
    }

    @Override
    public void disableNodeFromDnNoChildrenCheck(final String selectedNode, final OrganigrammeType type) {
        accept(true, manager -> updateActivationNodeFromDn(selectedNode, type, Calendar.getInstance(), false, manager));
    }

    @Override
    public void enableNodeFromDn(CoreSession session, final String selectedNode, final OrganigrammeType type) {
        if (type == MINISTERE) {
            checkAdminFonctionnel(session, "organigramme.entite.disable.permission.denied");
        }
        accept(true, manager -> updateActivationNodeFromDn(selectedNode, type, null, false, manager));
    }

    protected void updateActivationNodeFromDn(
        String selectedNode,
        OrganigrammeType type,
        Calendar cal,
        boolean childrenCheck,
        EntityManager manager
    ) {
        setDateFin(selectedNode, type, cal, childrenCheck, manager);
    }

    protected void setDateFin(
        final String selectedNode,
        final OrganigrammeType type,
        final Calendar cal,
        final boolean childrenCheck,
        EntityManager manager
    ) {
        final JournalService journalService = STServiceLocator.getJournalService();
        OrganigrammeNode node = getOrganigrammeNodeById(selectedNode, type);

        try (CloseableCoreSession coreSession = SessionUtil.openSession()) {
            // On applique les mêmes vérifications que pour la suppression.
            if (childrenCheck && !validateDeleteNode(coreSession, node).isEmpty()) {
                // Validation en échec, désactivation impossible
                throw new NuxeoException("Impossible de désactiver ce noeud");
            }

            String eventName;
            String eventComment;
            if (cal == null) {
                node.setDateFin(cal);
                eventComment = "Activation dans l'organigramme [" + node.getLabel() + "]";
                eventName = STEventConstant.NODE_ACTIVATION_EVENT;
            } else {
                node.setDateFin(cal.getTime());
                eventComment = "Désactivation dans l'organigramme [" + node.getLabel() + "]";
                eventName = STEventConstant.NODE_DESACTIVATION_EVENT;
            }

            manager.merge(node);
            manager.flush();
            journalService.journaliserActionAdministration(coreSession, eventName, eventComment);
        }
    }

    @Override
    public List<OrganigrammeNode> getLockedNodes() {
        return apply(true, manager -> new ArrayList<>(getLockedNodes(manager)));
    }

    @SuppressWarnings({ "unchecked" })
    protected List<OrganigrammeNode> getLockedNodes(EntityManager manager) {
        List<OrganigrammeNode> nodes = new ArrayList<>();

        Query query = manager.createQuery(LOCK_ENTITE_QUERY);
        List<Object> entries = query.getResultList();
        query = manager.createQuery(LOCK_UNITE_QUERY);
        entries.addAll(query.getResultList());
        query = manager.createQuery(LOCK_POSTE_QUERY);
        entries.addAll(query.getResultList());

        for (Object entry : entries) {
            if (entry instanceof EntiteNode) {
                nodes.add((EntiteNode) entry);
            } else if (entry instanceof UniteStructurelleNode) {
                nodes.add((UniteStructurelleNode) entry);
            } else if (entry instanceof PosteNode) {
                nodes.add((PosteNode) entry);
            }
        }

        return nodes;
    }

    @Override
    public String getMailFromUsername(String username) {
        DocumentModelList entries = null;

        try (DirSessionContainer dirSessionContainer = new DirSessionContainerImpl()) {
            Map<String, Serializable> filter = new HashMap<>();
            filter.put("username", username);
            entries = dirSessionContainer.getSessionUser().query(filter);

            String data = "";
            if (entries.size() == 1) {
                data = (String) entries.get(0).getProperty("user", "email");
            }
            return data;
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    @Override
    public UserNode getUserNode(String userId) {
        UserManager userManager = STServiceLocator.getUserManager();
        DocumentModel user = userManager.getUserModel(userId);
        if (user == null) {
            return null;
        }
        STUser stUser = user.getAdapter(STUser.class);
        UserNodeImpl userNode = new UserNodeImpl();

        userNode.setLabel(stUser.getFullName());
        userNode.setId(userId);
        return userNode;
    }

    @Override
    public OrganigrammeNode createNode(final OrganigrammeNode node) {
        return apply(
            true,
            manager -> {
                try (CloseableCoreSession coreSession = SessionUtil.openSession()) {
                    OrganigrammeNode dataSaved = null;

                    // récupération d'un id unique
                    if (node.getId() == null || "".equals(node.getId().trim())) {
                        node.setId(getNextId(manager, node));
                    }

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(
                            "Création de : " +
                            node.getLabel() +
                            " dans la base de données avec l'identifiant " +
                            node.getId()
                        );
                    }
                    dataSaved = manager.merge(node);
                    manager.flush();
                    notifyCreation(coreSession, node);
                    return dataSaved;
                }
            }
        );
    }

    @Override
    public void updateNode(final OrganigrammeNode node, final Boolean notifyJournal) {
        accept(true, manager -> updateNode(node, notifyJournal, manager));
    }

    private void updateNode(OrganigrammeNode node, Boolean notify, EntityManager manager) {
        try (CloseableCoreSession coreSession = SessionUtil.openSession()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Mise à jour de : " + node.getId() + " dans la base de données");
            }

            OrganigrammeNode oldNode = getOrganigrammeNodeById(node.getId(), node.getType());

            manager.merge(node);
            manager.flush();
            if (Boolean.TRUE.equals(notify)) {
                notifyUpdate(coreSession, node);
            }

            EventContext evtCtx = new EventContextImpl(coreSession, coreSession.getPrincipal());
            evtCtx.setProperties(
                ImmutableMap.of(
                    STEventConstant.ORGANIGRAMME_NODE_MODIFIED_PARAM_OLD_NODE,
                    oldNode,
                    STEventConstant.ORGANIGRAMME_NODE_MODIFIED_PARAM_NODE,
                    node
                )
            );
            ServiceUtil
                .getRequiredService(EventService.class)
                .fireEvent(STEventConstant.ORGANIGRAMME_NODE_MODIFIED, evtCtx);
        }
    }

    @Override
    public void updateNodes(final List<? extends OrganigrammeNode> listNode, final Boolean notifyJournal) {
        accept(
            true,
            em -> {
                for (OrganigrammeNode node : listNode) {
                    updateNode(node, notifyJournal, em);
                }
            }
        );
    }

    /**
     * Recherche tous les utilisateurs appartenant au noeud spécifié (descend récursivement dans les sous-noeuds de
     * l'arbre).
     *
     * @param nodeParent
     *            Noeud à recherche
     * @return Liste d'utilisateurs
     */
    @Override
    public List<STUser> getUsersInSubNode(OrganigrammeNode nodeParent) {
        List<STUser> list = new ArrayList<>();
        List<OrganigrammeNode> childrenList = getChildrenList(null, nodeParent, true);
        for (OrganigrammeNode node : childrenList) {
            if (node instanceof PosteNode) {
                list.addAll(((PosteNode) node).getUserList());
            } else {
                list.addAll(getUsersInSubNode(node));
            }
        }
        return list;
    }

    @Override
    public boolean lockOrganigrammeNode(CoreSession session, OrganigrammeNode node) {
        String locker = node.getLockUserName();
        final String userId = session.getPrincipal().getName();

        if (Objects.equals(locker, userId)) {
            node.setLockDate(Calendar.getInstance().getTime());
            updateNode(node, false);
            return true;
        }

        if (node.getLockUserName() == null) {
            node.setLockUserName(userId);
            node.setLockDate(Calendar.getInstance().getTime());
            updateNode(node, false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unlockOrganigrammeNode(final OrganigrammeNode node) {
        return apply(
            false,
            manager -> {
                OrganigrammeNode dataToEdit = null;
                // on recharge le noeud pour ne pas enregistrer d'autres modifications que le lock
                if (node instanceof EntiteNode) {
                    dataToEdit = manager.find(EntiteNodeImpl.class, node.getId());
                } else if (node instanceof GouvernementNode) {
                    dataToEdit = manager.find(GouvernementNodeImpl.class, node.getId());
                } else if (node instanceof UniteStructurelleNode) {
                    dataToEdit = manager.find(UniteStructurelleNodeImpl.class, node.getId());
                } else if (node instanceof PosteNode) {
                    dataToEdit = manager.find(PosteNodeImpl.class, node.getId());
                }
                if (dataToEdit != null) {
                    // Parce qu'il faut préciser le type à cause de la surcharge on doit faire ce magnifique cast
                    dataToEdit.setLockDate((Calendar) null);
                    dataToEdit.setLockUserName(null);
                    updateNode(dataToEdit, false);

                    return true;
                }
                return false;
            }
        );
    }

    @Override
    public boolean checkEntiteUniqueLabelInParent(CoreSession session, EntiteNode node) {
        return getParentList(node)
            .stream()
            .noneMatch(parent -> existsEntiteWithSameLabelAndNorInList(session, node, parent));
    }

    private boolean existsEntiteWithSameLabelAndNorInList(
        CoreSession session,
        EntiteNode node,
        OrganigrammeNode parentNode
    ) {
        return getChildrenList(session, parentNode, false)
            .stream()
            .filter(child -> child instanceof EntiteNode)
            .anyMatch(child -> hasEntiteSameLabelAndNor((EntiteNode) child, node));
    }

    private boolean hasEntiteSameLabelAndNor(EntiteNode child, EntiteNode node) {
        return (
            (node.getId() == null || !child.getId().equals(node.getId())) &&
            child.getLabel().trim().equals(node.getLabel().trim()) &&
            (node.getNorMinistere() == null || node.getNorMinistere().equals(child.getNorMinistere()))
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean checkUniqueLabel(final OrganigrammeNode node) {
        return apply(
            true,
            manager -> {
                final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
                Query query = null;
                if (node instanceof EntiteNode) {
                    query = manager.createQuery(ENTITE_LABEL_QUERY);
                } else if (node instanceof PosteNode) {
                    query = manager.createQuery(POSTE_LABEL_QUERY);
                } else if (node instanceof UniteStructurelleNode) {
                    query = manager.createQuery(UNITE_LABEL_QUERY);
                }

                if (query != null) {
                    query.setParameter("label", node.getLabel());
                    query.setParameter("curDate", Calendar.getInstance());
                    List<OrganigrammeNode> groupModelList = query.getResultList();

                    if (groupModelList.isEmpty()) {
                        return true;
                    } else {
                        // cas de l'édition, on ne prend pas en compte le noeud concerné dans les resultats de recherche
                        for (OrganigrammeNode nodeModel : groupModelList) {
                            if (!nodeModel.getId().equals(node.getId())) {
                                List<EntiteNode> entiteParentResultNode = new ArrayList<>();
                                ministeresService.getMinistereParent(nodeModel, entiteParentResultNode);
                                List<EntiteNode> entiteParent = new ArrayList<>();
                                ministeresService.getMinistereParent(node, entiteParent);
                                if (CollectionUtils.containsAny(entiteParent, entiteParentResultNode)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            }
        );
    }

    @Override
    public List<OrganigrammeNode> getOrganigrameLikeLabels(final String label, final List<OrganigrammeType> types) {
        return types
            .stream()
            .map(type -> getOrganigrameLikeLabel(label, type))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<OrganigrammeNode> getOrganigrameLikeLabel(final String label, final OrganigrammeType type) {
        return apply(
            true,
            manager -> {
                Query query = null;
                switch (type) {
                    case GOUVERNEMENT:
                        query = manager.createQuery(GVT_LIKE_LABEL_QUERY);
                        break;
                    case UNITE_STRUCTURELLE:
                    case DIRECTION:
                        query = manager.createQuery(UNITE_LIKE_LABEL_QUERY);
                        break;
                    case INSTITUTION:
                        query = manager.createQuery(INSTIT_LIKE_LABEL_QUERY);
                        break;
                    case MINISTERE:
                        query = manager.createQuery(ENTITE_LIKE_LABEL_QUERY);
                        break;
                    case POSTE:
                        query = manager.createQuery(POSTE_LIKE_LABEL_QUERY);
                        break;
                    default:
                        break;
                }

                if (query != null) {
                    query.setParameter("label", "%" + label + "%");
                    query.setParameter("curDate", Calendar.getInstance());
                    return query.getResultList();
                }
                return new ArrayList<>();
            }
        );
    }

    protected void notifyDelete(CoreSession session, OrganigrammeNode node) {
        final JournalService journalService = STServiceLocator.getJournalService();
        String comment = "Suppression dans l'organigramme [" + node.getLabel() + "]";
        journalService.journaliserActionAdministration(session, STEventConstant.NODE_DELETED_EVENT, comment);
    }

    protected void notifyCreation(CoreSession session, OrganigrammeNode node) {
        final JournalService journalService = STServiceLocator.getJournalService();
        String comment = "Création dans l'organigramme [" + node.getLabel() + "]";
        journalService.journaliserActionAdministration(session, STEventConstant.NODE_CREATED_EVENT, comment);
    }

    protected void notifyUpdate(CoreSession session, OrganigrammeNode node) {
        final JournalService journalService = STServiceLocator.getJournalService();
        String comment = "Modification dans l'organigramme [" + node.getLabel() + "]";
        journalService.journaliserActionAdministration(session, STEventConstant.NODE_MODIFIED_EVENT, comment);
    }

    @Override
    public void copyNodeWithoutUser(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode) {
        copyNode(coreSession, nodeToCopy, parentNode, Boolean.FALSE);
    }

    @Override
    public void copyNodeWithUsers(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode) {
        copyNode(coreSession, nodeToCopy, parentNode, Boolean.TRUE);
    }

    private void copyNode(
        CoreSession coreSession,
        OrganigrammeNode nodeToCopy,
        OrganigrammeNode parentNode,
        Boolean withUser
    ) {
        Map<String, OrganigrammeNode> oldAndNewIdTable = new HashMap<>();
        recursivCopyNode(coreSession, nodeToCopy, parentNode, oldAndNewIdTable, withUser);
    }

    /**
     * Copie l'élement nodeToCopy et les sous éléments de la branche récursivement
     *
     * @param coreSession
     * @param nodeToCopy
     * @param parentNode
     * @param oldAndNewIdTable
     * @param withUsers
     */
    private void recursivCopyNode(
        CoreSession coreSession,
        OrganigrammeNode nodeToCopy,
        OrganigrammeNode parentNode,
        Map<String, OrganigrammeNode> oldAndNewIdTable,
        boolean withUsers
    ) {
        OrganigrammeNode newNode;

        if (nodeToCopy instanceof EntiteNodeImpl) {
            newNode = new EntiteNodeImpl((EntiteNode) nodeToCopy);
        } else if (nodeToCopy instanceof GouvernementNodeImpl) {
            newNode = new GouvernementNodeImpl((GouvernementNode) nodeToCopy);
        } else if (nodeToCopy instanceof UniteStructurelleNodeImpl) {
            newNode = new UniteStructurelleNodeImpl((UniteStructurelleNode) nodeToCopy);
        } else if (nodeToCopy instanceof PosteNodeImpl) {
            newNode = new PosteNodeImpl((PosteNode) nodeToCopy);
        } else {
            throw new STException("Type de noeud inconnu");
        }

        List<OrganigrammeNode> parentList = new ArrayList<>();
        parentList.add(parentNode);
        newNode.setParentList(parentList);

        if (!withUsers && newNode instanceof PosteNode) {
            ((PosteNode) newNode).setMembers(null);
        }

        if (newNode instanceof EntiteNode || newNode instanceof UniteStructurelleNode || newNode instanceof PosteNode) {
            createNode(newNode);
            if (newNode instanceof PosteNode) {
                STServiceLocator.getSTPostesService().createPoste(coreSession, (PosteNode) newNode);
            }
        }

        oldAndNewIdTable.put(nodeToCopy.getId(), newNode);

        List<OrganigrammeNode> nodeList = getChildrenList(coreSession, nodeToCopy, true);
        parentList.clear();
        for (OrganigrammeNode childNode : nodeList) {
            if (oldAndNewIdTable.get(childNode.getId()) != null) {
                OrganigrammeNode newChildNode = oldAndNewIdTable.get(childNode.getId());
                OrganigrammeNode newParentNode = oldAndNewIdTable.get(nodeToCopy.getId());
                // Add link
                parentList = getParentList(newChildNode);
                parentList.add(newParentNode);
                newChildNode.setParentList(parentList);
                updateNode(newChildNode, false);
            } else {
                recursivCopyNode(coreSession, childNode, newNode, oldAndNewIdTable, withUsers);
            }
        }
    }

    private static List<OrganigrammeNode> getChildrenList(
        OrganigrammeNode node,
        CoreSession coreSession,
        boolean onlyFirstchild,
        boolean showDeactivedNode
    ) {
        List<OrganigrammeNode> result = new ArrayList<>();

        if (node instanceof EntiteNode) {
            if (onlyFirstchild) {
                result = getFirstChildWithSubNodes((EntiteNode) node, coreSession, showDeactivedNode);
            } else {
                result = getChildrenListWithSubNodes((EntiteNode) node, coreSession, showDeactivedNode);
            }
        } else if (node instanceof InstitutionNode) {
            if (onlyFirstchild) {
                result = getFirstChildWithSubNodes((InstitutionNode) node, coreSession, showDeactivedNode);
            } else {
                result = getChildrenListWithSubNodes((InstitutionNode) node, coreSession, showDeactivedNode);
            }
        } else if (node instanceof GouvernementNode) {
            if (onlyFirstchild) {
                result = getFirstChildGouvernementNode(node, coreSession);
            } else {
                result = getChildrenListGouvernementNode((GouvernementNode) node, coreSession, showDeactivedNode);
            }
        } else if (node instanceof UniteStructurelleNode) {
            if (onlyFirstchild) {
                result = getFirstChildWithSubNodes((UniteStructurelleNode) node, coreSession, showDeactivedNode);
            } else {
                result = getChildrenListWithSubNodes((UniteStructurelleNode) node, coreSession, showDeactivedNode);
            }
        }

        return result;
    }

    private static <T extends WithSubUSNode & WithSubPosteNode> List<OrganigrammeNode> getFirstChildWithSubNodes(
        T node,
        CoreSession coreSession,
        boolean showDeactivedNode
    ) {
        List<OrganigrammeNode> result = new ArrayList<>();
        OrganigrammeNode child = getFirstChildSubUSNode(node, coreSession, showDeactivedNode);
        if (child != null) {
            result.add(child);
        } else {
            child = getFirstChildSubPosteNode(node, coreSession, showDeactivedNode);
            if (child != null) {
                result.add(child);
            }
        }
        return result;
    }

    private static List<OrganigrammeNode> getFirstChildGouvernementNode(
        OrganigrammeNode node,
        CoreSession coreSession
    ) {
        List<OrganigrammeNode> result = new ArrayList<>();
        OrganigrammeNode child = getFirstChildSubEntityNode((GouvernementNode) node, coreSession);
        if (child != null) {
            result.add(child);
        }

        return result;
    }

    private static OrganigrammeNode getFirstChildSubUSNode(
        WithSubUSNode subUSNode,
        CoreSession coreSession,
        boolean showDeactivedNode
    ) {
        return subUSNode
            .getSubUnitesStructurellesList()
            .stream()
            .filter(uniteStruct -> isAccessibleNode(uniteStruct, coreSession, showDeactivedNode))
            .findFirst()
            .orElse(null);
    }

    private static OrganigrammeNode getFirstChildSubEntityNode(
        WithSubEntitiesNode subEntityNode,
        CoreSession coreSession
    ) {
        return subEntityNode
            .getSubEntitesList()
            .stream()
            .filter(entite -> entite != null && !entite.getDeleted() && entite.isReadGranted(coreSession))
            .findFirst()
            .orElse(null);
    }

    private static OrganigrammeNode getFirstChildSubPosteNode(
        WithSubPosteNode subPosteNode,
        CoreSession coreSession,
        boolean showDeactivedNode
    ) {
        return subPosteNode
            .getSubPostesList()
            .stream()
            .filter(poste -> isAccessibleNode(poste, coreSession, showDeactivedNode))
            .findFirst()
            .orElse(null);
    }

    /**
     * List des enfant d'un noeud de type {@link GouvernementNode} avec un conteneur de session LDAP
     */
    private static List<OrganigrammeNode> getChildrenListGouvernementNode(
        GouvernementNode gouvernementNode,
        CoreSession coreSession,
        boolean showDeactivedNode
    ) {
        List<OrganigrammeNode> childrenList = new ArrayList<>(
            getAccessibleNodes(coreSession, gouvernementNode.getSubEntitesList(), showDeactivedNode)
        );

        // tri des entités
        childrenList.sort(
            (node1, node2) -> {
                if (node1 instanceof EntiteNode && node2 instanceof EntiteNode) {
                    EntiteNode entite1 = (EntiteNode) node1;
                    EntiteNode entite2 = (EntiteNode) node2;
                    Long ordre1 = entite1.getOrdre();
                    Long ordre2 = entite2.getOrdre();
                    if (ordre1 == null) {
                        if (ordre2 == null) {
                            return entite1.getLabel().compareTo(entite2.getLabel());
                        } else {
                            return 1;
                        }
                    } else if (ordre2 == null) {
                        return -1;
                    }
                    return entite1.getOrdre().compareTo(entite2.getOrdre());
                } else if (node1 instanceof EntiteNode && node2 instanceof UniteStructurelleNode) {
                    return -1;
                } else if (node1 instanceof UniteStructurelleNode && node2 instanceof EntiteNode) {
                    return 1;
                } else {
                    return 0;
                }
            }
        );

        return childrenList;
    }

    protected static <T extends WithSubUSNode & WithSubPosteNode> List<OrganigrammeNode> getChildrenListWithSubNodes(
        T nodeWithSubNode,
        CoreSession coreSession,
        boolean showDeactivedNode
    ) {
        List<OrganigrammeNode> childrenList = new ArrayList<>();
        childrenList.addAll(
            getAccessibleNodes(coreSession, nodeWithSubNode.getSubUnitesStructurellesList(), showDeactivedNode)
        );
        childrenList.addAll(getAccessibleNodes(coreSession, nodeWithSubNode.getSubPostesList(), showDeactivedNode));

        return childrenList;
    }

    protected static List<OrganigrammeNode> getAccessibleNodes(
        CoreSession coreSession,
        List<? extends OrganigrammeNode> nodes,
        boolean showDeactivedNode
    ) {
        List<OrganigrammeNode> accessibleNodes = new ArrayList<>();
        for (OrganigrammeNode node : nodes) {
            if (isAccessibleNode(node, coreSession, showDeactivedNode)) {
                accessibleNodes.add(node);
            }
        }
        return accessibleNodes;
    }

    private static <T extends OrganigrammeNode> boolean isAccessibleNode(
        T node,
        CoreSession coreSession,
        boolean showDeactivedNode
    ) {
        return (
            node != null &&
            (node.isActive() || showDeactivedNode) &&
            !node.getDeleted() &&
            node.isReadGranted(coreSession)
        );
    }

    @Override
    public List<OrganigrammeNode> getParentList(OrganigrammeNode node) {
        List<OrganigrammeNode> parentList = new ArrayList<>();
        // On remonte les parents pour les postes, Unité structurelle, et Entites
        if (node instanceof PosteNode) {
            parentList.addAll(((PosteNode) node).getUniteStructurelleParentList());
            parentList.addAll(((PosteNode) node).getEntiteParentList());
        } else if (node instanceof EntiteNode) {
            parentList.add(
                STServiceLocator.getSTGouvernementService().getGouvernement(((EntiteNode) node).getParentId())
            );
        } else if (node instanceof UniteStructurelleNode) {
            // Ajoute les unités structurelles parentes à la liste des parents
            parentList.addAll(((UniteStructurelleNode) node).getUniteStructurelleParentList());
            // Ajoute les entités parentes à la liste des parents
            parentList.addAll(((UniteStructurelleNode) node).getEntiteParentList());
        }

        return parentList;
    }

    @Override
    public List<OrganigrammeNode> getChildrenList(
        CoreSession coreSession,
        OrganigrammeNode node,
        Boolean showDeactivedNode
    ) {
        return getChildrenList(node, coreSession, false, showDeactivedNode);
    }

    @Override
    public boolean checkNodeContainsChild(final OrganigrammeNode node, final OrganigrammeNode childNode) {
        if (node.getId().equals(childNode.getId())) {
            return true;
        }

        return getParentList(childNode).stream().anyMatch(parent -> checkNodeContainsChild(node, parent));
    }

    /**
     * Valide qu'un noeud de l'organigramme peut être supprimé. Certaines règles
     * vérifiées sont dépendantes de l'application considérée (REP ou EPG), d'autres
     * sont communes à REP et EPG.<br/>
     * L'objet retourné est la liste de problèmes rencontrés lors de la scrutation
     * de l'élément de l'organigramme. La liste est vide si aucun problème n'est
     * repéré.
     *
     * @param coreSession session
     * @param node        noeud dont on souhaite savoir s'il peut être supprimé.
     * @return un set potentiellement vide d'objets
     *         OrganigrammeNodeDeletionProblem.
     */
    protected Set<OrganigrammeNodeDeletionProblem> validateDeleteNode(CoreSession coreSession, OrganigrammeNode node) {
        return new HashSet<>();
    }

    @Override
    public OrganigrammeNode getFirstChild(OrganigrammeNode parent, CoreSession session, Boolean showDeactivedNode) {
        List<OrganigrammeNode> list = getChildrenList(parent, session, true, showDeactivedNode);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public boolean isUserInSubNode(OrganigrammeNode nodeParent, String username) {
        if (nodeParent == null || username == null) {
            return false;
        }

        return getChildrenList(null, nodeParent, true)
            .stream()
            .anyMatch(
                n -> {
                    if (n instanceof PosteNode) {
                        return ((PosteNode) n).getMembers().stream().anyMatch(id -> id.equals(username));
                    } else {
                        return isUserInSubNode(n, username);
                    }
                }
            );
    }

    @Override
    public List<String> getAllUserInSubNode(Set<String> ministereIds) {
        return ministereIds
            .stream()
            .map(
                id -> STServiceLocator.getOrganigrammeService().getOrganigrammeNodeById(id, OrganigrammeType.MINISTERE)
            )
            .map(OrganigrammeNode.class::cast)
            .map(this::getUserInSubNode)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    private Set<String> getUserInSubNode(OrganigrammeNode nodeParent) {
        return getChildrenList(null, nodeParent, true)
            .stream()
            .map(
                node -> {
                    if (node instanceof PosteNode) {
                        return ((PosteNode) node).getMembers();
                    } else {
                        return getUserInSubNode(node);
                    }
                }
            )
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public List<String> findUserInSubNode(OrganigrammeNode nodeParent) {
        List<String> list = new ArrayList<>();
        if (nodeParent != null) {
            List<OrganigrammeNode> childrenList = getChildrenList(null, nodeParent, true);
            for (OrganigrammeNode node : childrenList) {
                if (node instanceof PosteNode) {
                    list.addAll(((PosteNode) node).getMembers());
                } else {
                    list.addAll(findUserInSubNode(node));
                }
            }
        }
        return list;
    }

    private String getNextId(EntityManager manager, Object organigrammeElem) {
        // récupération d'un id unique
        final UIDGeneratorService uidGeneratorService = ServiceUtil.getRequiredService(UIDGeneratorService.class);
        UIDSequencer sequencer = uidGeneratorService.getSequencer();
        String entiteId = null;
        OrganigrammeNode elem;

        do {
            entiteId = String.valueOf(ID_LDAP_DEFAULT + sequencer.getNextLong("ORGANIGRAMME_SEQUENCER"));
            elem = (OrganigrammeNode) manager.find(organigrammeElem.getClass(), entiteId);
        } while (elem != null);
        return entiteId;
    }

    @Override
    public List<PosteNode> getAllSubPostes(OrganigrammeNode minNode) {
        List<PosteNode> result = new ArrayList<>();

        if (minNode instanceof WithSubPosteNode) {
            result.addAll(((WithSubPosteNode) minNode).getSubPostesList());
        }

        if (minNode instanceof WithSubUSNode) {
            List<UniteStructurelleNode> usList = ((WithSubUSNode) minNode).getSubUnitesStructurellesList();

            for (UniteStructurelleNode usNode : usList) {
                result.addAll(getAllSubPostes(usNode));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> query(String queryStr, Map<String, Object> params) {
        return apply(
            true,
            em -> {
                Query query = em.createQuery(queryStr);
                if (params != null && !params.isEmpty()) {
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        query.setParameter(entry.getKey(), entry.getValue());
                    }
                }
                return (List<T>) query.getResultList();
            }
        );
    }
}
