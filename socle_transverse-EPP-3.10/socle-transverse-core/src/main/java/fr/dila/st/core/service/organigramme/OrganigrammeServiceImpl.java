package fr.dila.st.core.service.organigramme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunCallback;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.ecm.platform.uidgen.UIDSequencer;
import org.nuxeo.ecm.platform.uidgen.service.ServiceHelper;
import org.nuxeo.ecm.platform.uidgen.service.UIDGeneratorService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.LDAPSessionContainer;
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
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STException;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.GouvernementNodeImpl;
import fr.dila.st.core.organigramme.InstitutionNodeImpl;
import fr.dila.st.core.organigramme.LDAPSessionContainerImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.organigramme.UserNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.CollectionUtil;
import fr.dila.st.core.util.SessionUtil;
import fr.dila.st.core.util.StringUtil;

/**
 * Implémentation du service de gestion de l'organigramme.
 * 
 * @author FEO, asatre
 */
public abstract class OrganigrammeServiceImpl extends DefaultComponent implements OrganigrammeService {
	private static final int					ID_LDAP_DEFAULT			= 60000000;
	/**
	 * Serial UID.
	 */
	private static final long					serialVersionUID		= -2392698015083550568L;

	/**
	 * Logger.
	 */
	private static final Log					LOGGER					= LogFactory
																				.getLog(OrganigrammeServiceImpl.class);

	private static volatile PersistenceProvider	persistenceProvider;

	private static final String					ENTITE_LABEL_QUERY		= "SELECT e FROM EntiteNode e WHERE e.label = :label AND (e.deleted=false OR e.deleted is NULL) AND (e.dateFin is null OR e.dateFin > :curDate)";
	private static final String					UNITE_LABEL_QUERY		= "SELECT u FROM UniteStructurelleNode u WHERE u.label = :label AND (u.deleted=false OR u.deleted is NULL) AND (u.dateFin is null OR u.dateFin > :curDate)";
	private static final String					POSTE_LABEL_QUERY		= "SELECT p FROM PosteNode p WHERE p.label = :label AND (p.deleted=false OR p.deleted is NULL) AND (p.dateFin is null OR p.dateFin > :curDate)";
	private static final String					ENTITE_LIKE_LABEL_QUERY	= "SELECT e FROM EntiteNode e WHERE e.label LIKE :label AND (e.deleted=false OR e.deleted is NULL) AND (e.dateFin is null OR e.dateFin > :curDate)";
	private static final String					GVT_LIKE_LABEL_QUERY	= "SELECT g FROM GouvernementNode g WHERE g.label LIKE :label AND (g.deleted=false OR g.deleted is NULL) AND (g.dateFin is null OR g.dateFin > :curDate)";
	private static final String					UNITE_LIKE_LABEL_QUERY	= "SELECT u FROM UniteStructurelleNode u WHERE u.label LIKE :label AND (u.deleted=false OR u.deleted is NULL) AND (u.dateFin is null OR u.dateFin > :curDate)";
	private static final String					POSTE_LIKE_LABEL_QUERY	= "SELECT p FROM PosteNode p WHERE p.label LIKE :label AND (p.deleted=false OR p.deleted is NULL) AND (p.dateFin is null OR p.dateFin > :curDate)";
	private static final String					LOCK_ENTITE_QUERY		= "SELECT e FROM EntiteNode e WHERE e.lockUser<>NULL";
	private static final String					LOCK_UNITE_QUERY		= "SELECT u FROM UniteStructurelleNode u WHERE u.lockUser<>NULL";
	private static final String					LOCK_POSTE_QUERY		= "SELECT p FROM PosteNode p WHERE p.lockUser<>NULL";
	protected static final String				INSTIT_LIKE_LABEL_QUERY	= "SELECT i FROM InstitutionNode i WHERE i.label LIKE :label AND (i.deleted=false OR i.deleted is NULL) AND (i.dateFin is null OR i.dateFin > :curDate)";

	@Override
	public OrganigrammeNode getOrganigrammeNodeById(final String nodeId, final OrganigrammeType type)
			throws ClientException {

		// Cas si on nous retourne un identifiant null ou vide
		if (StringUtil.isNotBlank(nodeId)) {
			return getOrCreatePersistenceProvider().run(true, new RunCallback<OrganigrammeNode>() {

				@Override
				public OrganigrammeNode runWith(EntityManager manager) {

					OrganigrammeNode node;
					switch (type) {
						case GOUVERNEMENT:
							node = manager.find(GouvernementNodeImpl.class, nodeId);
							break;
						case UNITE_STRUCTURELLE:
						case DIRECTION:
							node = manager.find(UniteStructurelleNodeImpl.class, nodeId);
							break;
						case INSTITUTION:
							node = manager.find(InstitutionNodeImpl.class, nodeId);
							break;
						case MINISTERE:
							node = manager.find(EntiteNodeImpl.class, nodeId);
							break;
						case POSTE:
							node = manager.find(PosteNodeImpl.class, nodeId);
							break;
						default:
							node = null;
							break;
					}

					return node;
				}
			});
		}

		return null;

	}

	@Override
	public List<OrganigrammeNode> getOrganigrammeNodesById(Map<String, OrganigrammeType> elems) throws ClientException {
		List<OrganigrammeNode> list = new ArrayList<OrganigrammeNode>();
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
	public List<? extends OrganigrammeNode> getRootNodes() throws ClientException {

		return STServiceLocator.getSTGouvernementService().getGouvernementList();

	}

	@Override
	public void deleteFromDn(final OrganigrammeNode node, final boolean notifyUser) throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {

			@Override
			public Void runWith(EntityManager manager) throws ClientException {
				CoreSession coreSession = null;
				try {
					coreSession = SessionUtil.getCoreSession();
					if (node != null) {
						recursiveDeleteNode(coreSession, node, notifyUser, manager);
					}
				} finally {
					SessionUtil.close(coreSession);
				}
				return null;
			}
		});

	}

	/**
	 * Met les noeuds à l'etat deleted cherche les postes dans les enfants et empêche la suppression si des FDR actives
	 * sont liées à ces postes
	 * 
	 * et renvoi un message d'erreur au client
	 * 
	 * @param coreSession
	 * @param nodeToDelete
	 * @param ldapSessionContainer
	 * @throws ClientException
	 */
	protected void recursiveDeleteNode(CoreSession coreSession, OrganigrammeNode nodeToDelete, boolean notifyUser,
			EntityManager manager) throws ClientException {
		List<OrganigrammeNode> nodeToUpdateList = new ArrayList<OrganigrammeNode>();

		// Vérifie si le noeud peut être supprimé
		validateDeleteNode(coreSession, nodeToDelete);

		// met deleted à true
		recursiveDelete(coreSession, nodeToDelete, nodeToUpdateList, manager);

		if (notifyUser) {
			// Envoie un mail aux utilisateurs du poste et les enlève de la liste des membres
			STMailService stMailService = STServiceLocator.getSTMailService();
			String objet = "Suppression d'un poste";
			StringBuilder texte = new StringBuilder();
			for (OrganigrammeNode node : nodeToUpdateList) {
				if (node instanceof PosteNode) {
					PosteNode poste = (PosteNode) node;
					if (poste.getUserList() != null && !poste.getUserList().isEmpty()) {
						texte.append("Le poste ").append(poste.getLabel()).append(" vient d'être supprimé.");
						try {
							stMailService.sendMailNotificationToUserList(coreSession, poste.getUserList(), objet,
									texte.toString());
						} catch (ClientException e) {
							LOGGER.error("Erreur d'envoi du mail de suppression de poste : " + poste.getLabel(), e);
						}
						poste.setMembers(null);
					}
				}
			}
		}

		// sauve la liste
		for (OrganigrammeNode node : nodeToUpdateList) {
			manager.merge(node);
			manager.flush();
		}
	}

	/**
	 * Met le boolean deleted a true et ajoute les noeuds dans une liste à sauver. Supprime les enfants s'ils n'ont
	 * qu'un parent
	 * 
	 * @param nodeToDelete
	 * @param nodeToSaveList
	 * @param ldapSessionContainer
	 * @throws ClientException
	 */
	protected void recursiveDelete(CoreSession session, OrganigrammeNode nodeToDelete,
			List<OrganigrammeNode> nodeToSaveList, EntityManager manager) throws ClientException {
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
	public void disableNodeFromDn(final String selectedNode, final OrganigrammeType type) throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {

			@Override
			public Void runWith(EntityManager manager) throws ClientException {
				updateActivationNodeFromDn(selectedNode, type, Calendar.getInstance(), true, manager);
				return null;
			}
		});
	}

	@Override
	public void disableNodeFromDnNoChildrenCheck(final String selectedNode, final OrganigrammeType type)
			throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {

			@Override
			public Void runWith(EntityManager manager) throws ClientException {
				updateActivationNodeFromDn(selectedNode, type, Calendar.getInstance(), false, manager);
				return null;
			}
		});
	}

	@Override
	public void enableNodeFromDn(final String selectedNode, final OrganigrammeType type) throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {

			@Override
			public Void runWith(EntityManager manager) throws ClientException {

				updateActivationNodeFromDn(selectedNode, type, null, false, manager);
				return null;
			}

		});
	}

	protected void updateActivationNodeFromDn(String selectedNode, OrganigrammeType type, Calendar cal,
			boolean childrenCheck, EntityManager manager) throws ClientException {

		setDateFin(selectedNode, type, cal, childrenCheck, manager);

	}

	protected void setDateFin(final String selectedNode, final OrganigrammeType type, final Calendar cal,
			final boolean childrenCheck, EntityManager manager) throws ClientException {

		final JournalService journalService = STServiceLocator.getJournalService();
		OrganigrammeNode node = getOrganigrammeNodeById(selectedNode, type);

		CoreSession coreSession = null;

		try {

			coreSession = SessionUtil.getCoreSession();

			// On applique les mêmes vérifications que pour la suppression.
			if (childrenCheck) {
				validateDeleteNode(coreSession, node);

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
		} finally {
			SessionUtil.close(coreSession);
		}

	}

	@Override
	public List<OrganigrammeNode> getLockedNodes() throws ClientException {

		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<OrganigrammeNode>>() {

			@Override
			public List<OrganigrammeNode> runWith(EntityManager manager) throws ClientException {
				List<OrganigrammeNode> nodes = new ArrayList<OrganigrammeNode>();
				nodes.addAll(getLockedNodes(manager));
				return nodes;
			}
		});

	}

	@SuppressWarnings({ "unchecked" })
	protected List<OrganigrammeNode> getLockedNodes(EntityManager manager) throws ClientException {
		List<OrganigrammeNode> nodes = new ArrayList<OrganigrammeNode>();

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
	public String getMailFromUid(String uid) throws ClientException {
		LDAPSessionContainer ldapSessionContainer = new LDAPSessionContainerImpl();
		DocumentModelList entries = null;

		try {
			Map<String, Serializable> filter = new HashMap<String, Serializable>();
			filter.put("uid", uid);
			entries = ldapSessionContainer.getSessionUser().query(filter);

			String data = "";
			if (entries.size() == 1) {
				data = (String) entries.get(0).getProperty("user", "email");
			}
			return data;
		} finally {
			ldapSessionContainer.closeAll();
		}
	}

	@Override
	public UserNode getUserNode(String userId) throws ClientException {
		UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel user = userManager.getUserModel(userId);
		if (user == null) {
			return null;
		}
		STUser stUser = user.getAdapter(STUser.class);
		UserNodeImpl userNode = new UserNodeImpl();
		StringBuilder label = new StringBuilder();
		if (!StringUtils.isEmpty(stUser.getFirstName())) {
			label.append(stUser.getFirstName());
			label.append(" ");
		}
		if (!StringUtils.isEmpty(stUser.getLastName())) {
			label.append(stUser.getLastName());
		}

		userNode.setLabel(label.toString());
		userNode.setId(userId);
		return userNode;
	}

	@Override
	public OrganigrammeNode createNode(final OrganigrammeNode node) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<OrganigrammeNode>() {

			@Override
			public OrganigrammeNode runWith(EntityManager manager) throws ClientException {
				CoreSession coreSession = null;
				try {
					coreSession = SessionUtil.getCoreSession();
					OrganigrammeNode dataSaved = null;

					// récupération d'un id unique
					if (node.getId() == null || node.getId().trim().equals("")) {
						node.setId(getNextId(manager, node));
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Création de : " + node.getLabel()
								+ " dans la base de données avec l'identifiant " + node.getId());
					}
					dataSaved = manager.merge(node);
					manager.flush();
					notifyCreation(coreSession, node);
					return dataSaved;
				} finally {
					SessionUtil.close(coreSession);
				}
			}

		});

	}

	@Override
	public void updateNode(final OrganigrammeNode node, final Boolean notifyJournal) throws ClientException {

		getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {

			@Override
			public Void runWith(EntityManager manager) throws ClientException {
				updateNode(node, notifyJournal, manager);
				return null;
			}
		});

	}

	private void updateNode(OrganigrammeNode node, Boolean notify, EntityManager manager) throws ClientException {
		CoreSession coreSession = null;
		try {
			coreSession = SessionUtil.getCoreSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Mise à jour de : " + node.getId() + " dans la base de données");
			}
			manager.merge(node);
			manager.flush();
			if (notify) {
				notifyUpdate(coreSession, node);
			}
		} finally {
			SessionUtil.close(coreSession);
		}
	}

	@Override
	public void updateNodes(final List<? extends OrganigrammeNode> listNode, final Boolean notifyJournal)
			throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {

			@Override
			public Void runWith(EntityManager em) throws ClientException {

				for (OrganigrammeNode node : listNode) {

					updateNode(node, notifyJournal, em);
				}
				return null;
			}
		});
	}

	/**
	 * Recherche tous les utilisateurs appartenant au noeud spécifié (descend récursivement dans les sous-noeuds de
	 * l'arbre).
	 * 
	 * @param nodeParent
	 *            Noeud à recherche
	 * @param ldapSessionContainer
	 *            Conteneur de session LDAP
	 * @return Liste d'utilisateurs
	 * @throws ClientException
	 */
	@Override
	public List<STUser> getUsersInSubNode(OrganigrammeNode nodeParent) throws ClientException {
		List<STUser> list = new ArrayList<STUser>();
		List<OrganigrammeNode> childrenList = getChildrenList(null, nodeParent, Boolean.TRUE);
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
	public boolean lockOrganigrammeNode(CoreSession session, OrganigrammeNode node) throws ClientException {
		String locker = node.getLockUserName();
		final String userId = session.getPrincipal().getName();

		if (locker != null && userId.equals(locker)) {
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
	public boolean unlockOrganigrammeNode(final OrganigrammeNode node) throws ClientException {
		return getOrCreatePersistenceProvider().run(false, new RunCallback<Boolean>() {

			@Override
			public Boolean runWith(EntityManager manager) throws ClientException {

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
		});

	}

	@Override
	public boolean checkUniqueLabelInParent(CoreSession session, OrganigrammeNode node) throws ClientException {

		List<OrganigrammeNode> parentList = getParentList(node);

		for (OrganigrammeNode parentNode : parentList) {
			List<OrganigrammeNode> childrenList = getChildrenList(session, parentNode, Boolean.TRUE);
			for (OrganigrammeNode children : childrenList) {
				if (children.getLabel().trim().equals(node.getLabel().trim())
						&& (node.getId() == null || !children.getId().equals(node.getId()))) {
					return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkUniqueLabel(final OrganigrammeNode node) throws ClientException {

		return getOrCreatePersistenceProvider().run(true, new RunCallback<Boolean>() {

			@Override
			public Boolean runWith(EntityManager manager) throws ClientException {
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
								List<EntiteNode> entiteParentResultNode = new ArrayList<EntiteNode>();
								ministeresService.getMinistereParent(nodeModel, entiteParentResultNode);
								List<EntiteNode> entiteParent = new ArrayList<EntiteNode>();
								ministeresService.getMinistereParent(node, entiteParent);
								if (CollectionUtil.containsOneElement(entiteParent, entiteParentResultNode)) {
									return false;
								}
							}
						}
					}
				}
				return true;
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrganigrammeNode> getOrganigrameLikeLabel(final String label, final OrganigrammeType type)
			throws ClientException {

		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<OrganigrammeNode>>() {

			@Override
			public List<OrganigrammeNode> runWith(EntityManager manager) throws ClientException {
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
					query.setParameter("label", label + "%");
					query.setParameter("curDate", Calendar.getInstance());
					List<OrganigrammeNode> groupModelList = query.getResultList();

					return groupModelList;
				}
				return new ArrayList<OrganigrammeNode>();
			}
		});

	}

	protected void notifyDelete(CoreSession session, OrganigrammeNode node) throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		String comment = "Suppression dans l'organigramme [" + node.getLabel() + "]";
		journalService.journaliserActionAdministration(session, STEventConstant.NODE_DELETED_EVENT, comment);
	}

	protected void notifyCreation(CoreSession session, OrganigrammeNode node) throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		String comment = "Création dans l'organigramme [" + node.getLabel() + "]";
		journalService.journaliserActionAdministration(session, STEventConstant.NODE_CREATED_EVENT, comment);
	}

	protected void notifyUpdate(CoreSession session, OrganigrammeNode node) throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		String comment = "Modification dans l'organigramme [" + node.getLabel() + "]";
		journalService.journaliserActionAdministration(session, STEventConstant.NODE_MODIFIED_EVENT, comment);
	}

	@Override
	public void copyNodeWithoutUser(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode)
			throws ClientException {
		copyNode(coreSession, nodeToCopy, parentNode, Boolean.FALSE);
	}

	@Override
	public void copyNodeWithUsers(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode)
			throws ClientException {
		copyNode(coreSession, nodeToCopy, parentNode, Boolean.TRUE);
	}

	private void copyNode(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode,
			Boolean withUser) throws ClientException {
		Map<String, OrganigrammeNode> oldAndNewIdTable = new HashMap<String, OrganigrammeNode>();
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
	 * @throws ClientException
	 */
	private void recursivCopyNode(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode,
			Map<String, OrganigrammeNode> oldAndNewIdTable, boolean withUsers) throws ClientException {

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

		List<OrganigrammeNode> parentList = new ArrayList<OrganigrammeNode>();
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

		List<OrganigrammeNode> nodeList = getChildrenList(coreSession, nodeToCopy, Boolean.TRUE);
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

	private List<OrganigrammeNode> getChildrenList(OrganigrammeNode node, CoreSession coreSession,
			Boolean onlyFirstchild, Boolean showDeactivedNode) throws ClientException {
		List<OrganigrammeNode> result = new ArrayList<OrganigrammeNode>();

		if (showDeactivedNode == null) {
			showDeactivedNode = Boolean.TRUE;
		}

		if (node instanceof EntiteNode) {
			if (onlyFirstchild) {
				result = getFirstChildEntiteNode(node, coreSession, null, showDeactivedNode);
			} else {
				result = getChildrenListEntiteNode((EntiteNode) node, coreSession, showDeactivedNode);
			}
		} else if (node instanceof GouvernementNode) {
			if (onlyFirstchild) {
				result = getFirstChildGouvernementNode(node, coreSession, null, showDeactivedNode);
			} else {
				result = getChildrenListGouvernementNode((GouvernementNode) node, coreSession, showDeactivedNode);
			}
		} else if (node instanceof UniteStructurelleNode) {
			if (onlyFirstchild) {
				result = getFirstChildUniteStructurelleNode(node, coreSession, null, showDeactivedNode);
			} else {
				result = getChildrenListUniteStructurelleNode((UniteStructurelleNode) node, coreSession,
						showDeactivedNode);
			}
		}

		return result;
	}

	private List<OrganigrammeNode> getFirstChildUniteStructurelleNode(OrganigrammeNode node, CoreSession coreSession,
			List<OrganigrammeNode> result, Boolean showDeactivedNode) throws ClientException {

		OrganigrammeNode child = getFirstChildSubUSNode((UniteStructurelleNode) node, coreSession, showDeactivedNode);
		if (child != null) {
			result = new ArrayList<OrganigrammeNode>();
			result.add(child);
		} else {
			child = getFirstChildSubPosteNode((UniteStructurelleNode) node, coreSession, showDeactivedNode);
			if (child != null) {
				result = new ArrayList<OrganigrammeNode>();
				result.add(child);
			}
		}
		return result;
	}

	private List<OrganigrammeNode> getFirstChildGouvernementNode(OrganigrammeNode node, CoreSession coreSession,
			List<OrganigrammeNode> result, Boolean showDeactivedNode) throws ClientException {
		OrganigrammeNode child = getFirstChildSubEntityNode((GouvernementNode) node, coreSession);
		if (child != null) {
			result = new ArrayList<OrganigrammeNode>();
			result.add(child);
		}

		return result;
	}

	private List<OrganigrammeNode> getFirstChildEntiteNode(OrganigrammeNode node, CoreSession coreSession,
			List<OrganigrammeNode> result, Boolean showDeactivedNode) throws ClientException {

		OrganigrammeNode child = getFirstChildSubUSNode((EntiteNode) node, coreSession, showDeactivedNode);
		if (child != null) {
			result = new ArrayList<OrganigrammeNode>();
			result.add(child);
		} else {
			child = getFirstChildSubPosteNode((EntiteNode) node, coreSession, showDeactivedNode);
			if (child != null) {
				result = new ArrayList<OrganigrammeNode>();
				result.add(child);
			}
		}
		return result;
	}

	private OrganigrammeNode getFirstChildSubUSNode(WithSubUSNode subUSNode, CoreSession coreSession,
			Boolean showDeactivedNode) throws ClientException {
		List<UniteStructurelleNode> usChildren = subUSNode.getSubUnitesStructurellesList();

		for (UniteStructurelleNode uniteStruct : usChildren) {

			if (uniteStruct != null && (uniteStruct.isActive() || showDeactivedNode) && !uniteStruct.getDeleted()
					&& uniteStruct.isReadGranted(coreSession)) {
				return uniteStruct;
			}
		}

		return null;
	}

	private OrganigrammeNode getFirstChildSubEntityNode(WithSubEntitiesNode subEntityNode, CoreSession coreSession)
			throws ClientException {
		List<EntiteNode> entiteChildren = (List<EntiteNode>) subEntityNode.getSubEntitesList();

		for (EntiteNode entite : entiteChildren) {

			if (entite != null && !entite.getDeleted() && entite.isReadGranted(coreSession)) {
				return entite;
			}
		}
		return null;
	}

	private OrganigrammeNode getFirstChildSubPosteNode(WithSubPosteNode subPosteNode, CoreSession coreSession,
			Boolean showDeactivedNode) throws ClientException {
		List<PosteNode> posteChildren = (List<PosteNode>) subPosteNode.getSubPostesList();

		for (PosteNode poste : posteChildren) {
			if (poste != null && (poste.isActive() || showDeactivedNode) && !poste.getDeleted()
					&& poste.isReadGranted(coreSession)) {
				return poste;
			}
		}
		return null;
	}

	/**
	 * List des enfant d'un noeud de type {@link UniteStructurelleNode} avec un conteneur de session LDAP
	 * 
	 * @param uniteStructurelleNode
	 * @param ldapSessionContainer
	 * @param coreSession
	 * @param showDeactivedNode
	 * @return
	 * @throws ClientException
	 */
	private List<OrganigrammeNode> getChildrenListUniteStructurelleNode(UniteStructurelleNode uniteStructurelleNode,
			CoreSession coreSession, Boolean showDeactivedNode) throws ClientException {
		List<OrganigrammeNode> childrenList = new ArrayList<OrganigrammeNode>();

		childrenList.addAll(getAccessibleNodes(coreSession, uniteStructurelleNode.getSubUnitesStructurellesList(),
				showDeactivedNode));
		childrenList
				.addAll(getAccessibleNodes(coreSession, uniteStructurelleNode.getSubPostesList(), showDeactivedNode));

		return childrenList;
	}

	/**
	 * List des enfant d'un noeud de type {@link GouvernementNode} avec un conteneur de session LDAP
	 * 
	 * @param uniteStructurelleNode
	 * @param coreSession
	 * @param showDeactivedNode
	 * @return
	 * @throws ClientException
	 */
	private List<OrganigrammeNode> getChildrenListGouvernementNode(GouvernementNode gouvernementNode,
			CoreSession coreSession, Boolean showDeactivedNode) throws ClientException {
		List<OrganigrammeNode> childrenList = new ArrayList<OrganigrammeNode>();

		childrenList.addAll(getAccessibleNodes(coreSession, gouvernementNode.getSubEntitesList(), showDeactivedNode));

		// tri des entités
		Collections.sort(childrenList, new Comparator<OrganigrammeNode>() {
			@Override
			public int compare(OrganigrammeNode node1, OrganigrammeNode node2) {

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
		});

		return childrenList;
	}

	/**
	 * List des enfant d'un noeud de type {@link EntiteNode} avec un conteneur de session LDAP
	 * 
	 * @param uniteStructurelleNode
	 * @param ldapSessionContainer
	 * @param coreSession
	 * @param showDeactivedNode
	 * @return
	 * @throws ClientException
	 */
	protected List<OrganigrammeNode> getChildrenListEntiteNode(EntiteNode entiteNode, CoreSession coreSession,
			Boolean showDeactivedNode) throws ClientException {
		List<OrganigrammeNode> childrenList = new ArrayList<OrganigrammeNode>();
		childrenList.addAll(getAccessibleNodes(coreSession, entiteNode.getSubUnitesStructurellesList(),
				showDeactivedNode));
		childrenList.addAll(getAccessibleNodes(coreSession, entiteNode.getSubPostesList(), showDeactivedNode));

		return childrenList;
	}

	protected List<OrganigrammeNode> getAccessibleNodes(CoreSession coreSession,
			List<? extends OrganigrammeNode> nodes, Boolean showDeactivedNode) {
		List<OrganigrammeNode> accessibleNodes = new ArrayList<OrganigrammeNode>();
		for (OrganigrammeNode node : nodes) {
			if (node != null && (node.isActive() || showDeactivedNode) && !node.getDeleted()
					&& node.isReadGranted(coreSession)) {
				accessibleNodes.add(node);
			}
		}
		return accessibleNodes;
	}

	@Override
	public List<OrganigrammeNode> getParentList(final OrganigrammeNode node) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<OrganigrammeNode>>() {

			@Override
			public List<OrganigrammeNode> runWith(EntityManager manager) throws ClientException {
				return getParentList(node, manager);
			}
		});
	}

	@Override
	public List<OrganigrammeNode> getParentList(OrganigrammeNode node, EntityManager manager) throws ClientException {
		List<OrganigrammeNode> parentList = new ArrayList<OrganigrammeNode>();
		// On remonte les parents pour les postes, Unité structurelle, et Entites
		if (node instanceof PosteNode) {
			parentList.addAll(((PosteNode) node).getUniteStructurelleParentList());
			parentList.addAll(((PosteNode) node).getEntiteParentList());
		} else if (node instanceof EntiteNode) {
			parentList.add(STServiceLocator.getSTGouvernementService().getGouvernement(
					((EntiteNode) node).getParentId()));
		} else if (node instanceof UniteStructurelleNode) {
			// Ajoute les unités structurelles parentes à la liste des parents
			parentList.addAll(((UniteStructurelleNode) node).getUniteStructurelleParentList());
			// Ajoute les entités parentes à la liste des parents
			parentList.addAll(((UniteStructurelleNode) node).getEntiteParentList());
		}

		return parentList;
	}

	@Override
	public List<OrganigrammeNode> getChildrenList(CoreSession coreSession, OrganigrammeNode node,
			Boolean showDeactivedNode) throws ClientException {

		return getChildrenList(node, coreSession, Boolean.FALSE, showDeactivedNode);

	}

	@Override
	public boolean checkParentListContainsChildren(final UniteStructurelleNode usNode,
			final OrganigrammeNode childNode, final Boolean showDeactivedNode) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<Boolean>() {

			@Override
			public Boolean runWith(EntityManager manager) throws ClientException {

				List<OrganigrammeNode> parentList = getParentList(usNode, manager);
				return checkParentListContainsChildren(parentList, childNode, showDeactivedNode);
			}
		});

	}

	private boolean checkParentListContainsChildren(List<OrganigrammeNode> parentList, OrganigrammeNode childNode,
			Boolean showDeactivedNode) throws ClientException {

		for (OrganigrammeNode parentNode : parentList) {
			if (parentNode.getId().equals(childNode.getId())) {
				return true;
			}
		}
		List<OrganigrammeNode> childrenList = getChildrenList(null, childNode, showDeactivedNode);

		for (OrganigrammeNode child : childrenList) {
			if (child instanceof UniteStructurelleNode) {
				return checkParentListContainsChildren(parentList, child, showDeactivedNode);
			}
		}

		return false;
	}

	protected abstract void validateDeleteNode(CoreSession coreSession, OrganigrammeNode node) throws ClientException;

	@Override
	public OrganigrammeNode getFirstChild(OrganigrammeNode parent, CoreSession session, Boolean showDeactivedNode)
			throws ClientException {

		List<OrganigrammeNode> list = getChildrenList(parent, session, Boolean.TRUE, showDeactivedNode);
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}

	}

	@Override
	public List<String> findUserInSubNode(OrganigrammeNode nodeParent) throws ClientException {
		List<String> list = new ArrayList<String>();
		if (nodeParent != null) {
			List<OrganigrammeNode> childrenList = getChildrenList(null, nodeParent, Boolean.TRUE);
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

	protected static PersistenceProvider getOrCreatePersistenceProvider() {
		if (persistenceProvider == null) {
			synchronized (OrganigrammeServiceImpl.class) {
				if (persistenceProvider == null) {
					activatePersistenceProvider();
				}
			}
		}
		return persistenceProvider;
	}

	protected static void activatePersistenceProvider() {
		Thread thread = Thread.currentThread();
		ClassLoader last = thread.getContextClassLoader();
		try {
			thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
			PersistenceProviderFactory persistenceProviderFactory = Framework
					.getLocalService(PersistenceProviderFactory.class);
			persistenceProvider = persistenceProviderFactory.newProvider("organigramme-provider");
			persistenceProvider.openPersistenceUnit();
		} finally {
			thread.setContextClassLoader(last);
		}
	}

	@Override
	public void deactivate(ComponentContext context) throws Exception {
		deactivatePersistenceProvider();
	}

	private static void deactivatePersistenceProvider() {
		if (persistenceProvider != null) {
			synchronized (OrganigrammeServiceImpl.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	private String getNextId(EntityManager manager, Object organigrammeElem) {
		// récupération d'un id unique
		final UIDGeneratorService uidGeneratorService = ServiceHelper.getUIDGeneratorService();
		UIDSequencer sequencer = uidGeneratorService.getSequencer();
		String entiteId = null;
		OrganigrammeNode elem;

		do {
			entiteId = String.valueOf(ID_LDAP_DEFAULT + sequencer.getNext("ORGANIGRAMME_SEQUENCER"));
			elem = (OrganigrammeNode) manager.find(organigrammeElem.getClass(), entiteId);

		} while (elem != null);
		return entiteId;
	}
	
	@Override
	public List<PosteNode> getAllSubPostes(OrganigrammeNode minNode) throws ClientException {
		List<PosteNode> result = new ArrayList<PosteNode>();
		
		if (minNode instanceof WithSubPosteNode) {
			result.addAll(((WithSubPosteNode)minNode).getSubPostesList());
		}
		
		if (minNode instanceof WithSubUSNode) {
			List<UniteStructurelleNode> usList = ((WithSubUSNode)minNode).getSubUnitesStructurellesList();
			
			for (UniteStructurelleNode usNode : usList) {
				result.addAll(getAllSubPostes(usNode));
			}
		}
		return result;
	}
}
