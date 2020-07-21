package fr.dila.st.core.service.organigramme;

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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunCallback;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.LDAPSessionContainer;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * @WARNING les methodes ayant en parametre un {@link LDAPSessionContainer} ne ferment jamais les sessions ! La
 *          fermeture est faite par la fonction appellante
 */
public class STPostesServiceImpl extends DefaultComponent implements STPostesService {

	private static final STLogger				LOGGER									= STLogFactory
																								.getLog(STPostesServiceImpl.class);
	private static volatile PersistenceProvider	persistenceProvider;
	private final static String					POSTE_SGG_QUERY							= "SELECT p FROM PosteNode p WHERE p.superviseurSGG=TRUE AND (p.dateFin is null OR p.dateFin > :curDate) ";
	private final static String					POSTE_BDC_QUERY							= "SELECT p FROM PosteNode p WHERE p.posteBDC=TRUE AND (p.dateFin is null OR p.dateFin > :curDate) ";

	private final static String					POSTE_FOR_USER_QUERY					= "SELECT DISTINCT p FROM PosteNode p WHERE (p.membres LIKE :user OR p.membres LIKE :user2 OR p.membres LIKE :user3 OR p.membres = :user4) AND (p.dateFin is null OR p.dateFin > :curDate)";
	private final static String					POSTE_LIGHT_FOR_USER_QUERY				= "SELECT DISTINCT p.label FROM PosteNode p WHERE (p.membres LIKE :user OR p.membres LIKE :user2 OR p.membres LIKE :user3 OR p.membres = :user4) AND (p.dateFin is null OR p.dateFin > :curDate)";
	private final static String					POSTE_ID_FOR_USER_QUERY					= "SELECT DISTINCT p.idOrganigramme FROM PosteNode p WHERE (p.membres LIKE :user OR p.membres LIKE :user2 OR p.membres LIKE :user3 OR p.membres = :user4) AND (p.dateFin is null OR p.dateFin > :curDate)";
	private final static String					POSTES_ENFANTS_QUERY					= "SELECT p1 FROM PosteNode p1 WHERE p1.parentUniteId LIKE ?1";
	private final static String					POSTES_ENFANTS_ENTITE_PARENT_QUERY		= "SELECT p1 FROM PosteNode p1 WHERE p1.parentEntiteId LIKE ?1";
	protected static final String				POSTES_ENFANTS_INSITUTION_PARENT_QUERY	= "SELECT p1 FROM PosteNode p1 WHERE p1.parentInstitId LIKE ?1";
	private final static String					ALL_POSTE_QUERY							= "SELECT p FROM PosteNode p";

	@Override
	public PosteNode getPoste(final String posteId) throws ClientException {
		if (StringUtil.isNotBlank(posteId)) {
			return getOrCreatePersistenceProvider().run(true, new RunCallback<PosteNode>() {

				@Override
				public PosteNode runWith(EntityManager manager) throws ClientException {

					return manager.find(PosteNodeImpl.class, posteId);

				}
			});
		}
		return null;
	}

	@Override
	public List<PosteNode> getPostesNodes(Collection<String> postesId) throws ClientException {
		PosteNode nodeDoc = null;
		List<PosteNode> listNode = new ArrayList<PosteNode>();

		for (String posteId : postesId) {
			nodeDoc = getPoste(posteId);
			if (nodeDoc != null) {
				listNode.add(nodeDoc);
			}
		}
		return listNode;
	}

	@Override
	public PosteNode getBarePosteModel() throws ClientException {
		return new PosteNodeImpl();
	}

	@Override
	public void createPoste(CoreSession coreSession, PosteNode newPoste) throws ClientException {
		if (newPoste != null) {
			// Suppression des espaces avant et après le libellé M157222
			if (newPoste.getLabel() != null) {
				newPoste.setLabel(newPoste.getLabel().trim());
			}
			STServiceLocator.getOrganigrammeService().createNode(newPoste);
			// Lève un événement de création de poste
			EventProducer eventProducer = STServiceLocator.getEventProducer();
			Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
			eventProperties.put(STEventConstant.ORGANIGRAMME_NODE_ID_EVENT_PARAM, newPoste.getId());
			eventProperties.put(STEventConstant.ORGANIGRAMME_NODE_LABEL_EVENT_PARAM, newPoste.getLabel());
			InlineEventContext eventContext = new InlineEventContext(coreSession, coreSession.getPrincipal(),
					eventProperties);
			eventProducer.fireEvent(eventContext.newEvent(STEventConstant.POSTE_CREATED_EVENT));
		}
	}

	@Override
	public void updatePoste(CoreSession coreSession, PosteNode poste) throws ClientException {
		if (poste != null) {
			// Suppression des espaces avant et après le libellé M157222
			if (poste.getLabel() != null) {
				poste.setLabel(poste.getLabel().trim());
			}
			STServiceLocator.getOrganigrammeService().updateNode(poste, Boolean.TRUE);
			// Lève un événement de mise à jour de poste
			EventProducer eventProducer = STServiceLocator.getEventProducer();
			Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
			eventProperties.put(STEventConstant.ORGANIGRAMME_NODE_ID_EVENT_PARAM, poste.getId());
			eventProperties.put(STEventConstant.ORGANIGRAMME_NODE_LABEL_EVENT_PARAM, poste.getLabel());
			InlineEventContext eventContext = new InlineEventContext(coreSession, coreSession.getPrincipal(),
					eventProperties);
			eventProducer.fireEvent(eventContext.newEvent(STEventConstant.POSTE_UPDATED_EVENT));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PosteNode getSGGPosteNode() throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<PosteNode>() {
			@Override
			public PosteNode runWith(EntityManager manager) throws ClientException {
				Query query = manager.createQuery(POSTE_SGG_QUERY);
				query.setParameter("curDate", Calendar.getInstance());

				List<PosteNode> lstPostes = query.getResultList();
				if (lstPostes != null && !lstPostes.isEmpty()) {
					return lstPostes.get(0);
				}
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PosteNode> getPosteBdcNodeList() throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<PosteNode>>() {
			@Override
			public List<PosteNode> runWith(EntityManager manager) throws ClientException {
				Query query = manager.createQuery(POSTE_BDC_QUERY);
				query.setParameter("curDate", Calendar.getInstance());

				List<PosteNode> lstPostes = query.getResultList();
				if (lstPostes != null && !lstPostes.isEmpty()) {
					return lstPostes;
				}
				return null;
			}
		});

	}

	@Override
	public boolean isPosteBdcInEachEntiteFromGouvernement(OrganigrammeNode gouvernementNode) throws ClientException {
		List<OrganigrammeNode> children = STServiceLocator.getOrganigrammeService().getChildrenList(null,
				gouvernementNode, Boolean.TRUE);
		for (OrganigrammeNode child : children) {
			if (child instanceof EntiteNode) {
				boolean isPosteBdc = isPosteBdcInSubNode(child, null);
				if (!isPosteBdc) {
					LOGGER.warn(null, STLogEnumImpl.FAIL_GET_POSTE_TEC, "Aucun poste BDC pour " + child.getLabel());
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Renvoie true si au moins un des enfants est un poste bdc
	 * 
	 * @param node
	 * @param session
	 * @param ldapSessionContainer
	 * @return
	 * @throws ClientException
	 */
	private boolean isPosteBdcInSubNode(OrganigrammeNode node, CoreSession session) throws ClientException {
		List<OrganigrammeNode> children = STServiceLocator.getOrganigrammeService().getChildrenList(session, node,
				Boolean.TRUE);

		for (OrganigrammeNode child : children) {
			if (child instanceof PosteNode) {
				if (((PosteNode) child).isPosteBdc()) {
					return true;
				}
			} else {
				boolean isPosteBdc = isPosteBdcInSubNode(child, session);
				if (isPosteBdc) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public PosteNode getPosteBdcInEntite(String entiteId) throws ClientException {
		OrganigrammeNode entiteNode = STServiceLocator.getOrganigrammeService().getOrganigrammeNodeById(entiteId,
				OrganigrammeType.MINISTERE);
		return getPosteBdcInSubNode(entiteNode);
	}

	/**
	 * Renvoie le poste bdc s'il y en a un dans les noeuds enfants
	 * 
	 * @param node
	 * @param ldapSessionContainer
	 * @return
	 * @throws ClientException
	 */
	private PosteNode getPosteBdcInSubNode(OrganigrammeNode node) throws ClientException {
		List<OrganigrammeNode> children = STServiceLocator.getOrganigrammeService().getChildrenList(null, node,
				Boolean.TRUE);

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
	public List<OrganigrammeNode> getPosteBdcListInEntite(String entiteId) throws ClientException {
		OrganigrammeNode entiteNode = STServiceLocator.getOrganigrammeService().getOrganigrammeNodeById(entiteId,
				OrganigrammeType.MINISTERE);
		List<OrganigrammeNode> bdcList = new ArrayList<OrganigrammeNode>();
		getPosteBdcListInSubNode(entiteNode, bdcList);
		return bdcList;
	}

	/**
	 * Renvoie le poste bdc s'il y en a un dans les noeuds enfants
	 * 
	 * @param node
	 * @param ldapSessionContainer
	 * @return
	 * @throws ClientException
	 */
	private void getPosteBdcListInSubNode(OrganigrammeNode node, List<OrganigrammeNode> bdcList) throws ClientException {
		if (bdcList == null) {
			bdcList = new ArrayList<OrganigrammeNode>();
		}

		List<OrganigrammeNode> children = STServiceLocator.getOrganigrammeService().getChildrenList(null, node,
				Boolean.TRUE);
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
	public List<STUser> getUserFromPoste(String posteId) throws ClientException {
		PosteNode orgaNode = getPoste(posteId);
		return orgaNode.getUserList();
	}

	@Override
	public List<String> getUserNamesFromPoste(String posteId) throws ClientException {
		PosteNode posteNode = getPoste(posteId);
		if (posteNode != null) {
			return posteNode.getMembers();
		}

		return new ArrayList<String>();
	}

	@Override
	public void deactivateBdcPosteList(List<OrganigrammeNode> bdcList) throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		// désactive le(s) bdc
		for (OrganigrammeNode toCancelBdcNode : bdcList) {
			PosteNode bdcPosteNode = (PosteNode) toCancelBdcNode;
			bdcPosteNode.setPosteBdc(false);
			organigrammeService.updateNode(bdcPosteNode, false);
		}
	}

	@Override
	public void addBdcPosteToNewPosteBdc(final Map<String, List<OrganigrammeNode>> posteBdcToMigrate)
			throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		final List<OrganigrammeNode> nodeToSaveList = new ArrayList<OrganigrammeNode>();
		final Map<String, Set<String>> mapMembers = new HashMap<String, Set<String>>();

		for (final Entry<String, List<OrganigrammeNode>> entryOldPosteBdc : posteBdcToMigrate.entrySet()) {
			final List<OrganigrammeNode> postesBdc = entryOldPosteBdc.getValue();
			final OrganigrammeNode oldPosteBdc = organigrammeService.getOrganigrammeNodeById(entryOldPosteBdc.getKey(),
					OrganigrammeType.POSTE);
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
			final OrganigrammeNode newPosteBdc = organigrammeService.getOrganigrammeNodeById(entryNewPosteBdc.getKey(),
					OrganigrammeType.POSTE);
			((PosteNode) newPosteBdc).setMembers(new ArrayList<String>(entryNewPosteBdc.getValue()));
			nodeToSaveList.add(newPosteBdc);
		}
		organigrammeService.updateNodes(nodeToSaveList, false);
	}

	@Override
	public boolean userHasOnePosteOnly(PosteNode posteNode) throws ClientException {
		List<STUser> userList = posteNode.getUserList();
		for (STUser user : userList) {
			if (user.getPostes().size() == 1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> getPosteIdInSubNode(OrganigrammeNode node) throws ClientException {
		List<String> list = new ArrayList<String>();
		List<OrganigrammeNode> children = STServiceLocator.getOrganigrammeService().getChildrenList(null, node,
				Boolean.TRUE);

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

	private static PersistenceProvider getOrCreatePersistenceProvider() {
		if (persistenceProvider == null) {
			synchronized (STPostesServiceImpl.class) {
				if (persistenceProvider == null) {
					activatePersistenceProvider();
				}
			}
		}
		return persistenceProvider;
	}

	private static void activatePersistenceProvider() {
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
			synchronized (STPostesServiceImpl.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	public List<EntiteNode> getEntitesParents(final PosteNode poste) throws ClientException {
		return STServiceLocator.getSTMinisteresService().getMinistereParentFromPoste(poste.getId());
	}

	public List<UniteStructurelleNode> getUniteStructurelleParentList(final PosteNode poste) throws ClientException {
		return poste.getUniteStructurelleParentList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PosteNode> getAllPostesForUser(final String userId) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<PosteNode>>() {

			@Override
			public List<PosteNode> runWith(EntityManager manager) throws ClientException {
				List<PosteNode> lstNodes = new ArrayList<PosteNode>();

				if (StringUtil.isNotEmpty(userId)) {
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
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllPosteNameForUser(final String userId) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<String>>() {

			@Override
			public List<String> runWith(EntityManager manager) throws ClientException {
				List<String> lstNodes = new ArrayList<String>();

				if (StringUtil.isNotEmpty(userId)) {
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
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllPosteIdsForUser(final String userId) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<String>>() {

			@Override
			public List<String> runWith(EntityManager manager) throws ClientException {
				List<String> lstNodes = new ArrayList<String>();

				if (StringUtil.isNotEmpty(userId)) {
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
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PosteNode> getPosteNodeEnfant(final String nodeId, final OrganigrammeType type) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<PosteNode>>() {
			@Override
			public List<PosteNode> runWith(EntityManager manager) throws ClientException {
				List<PosteNode> lstPosteNodes = new ArrayList<PosteNode>();

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
		});
	}

	@Override
	public void addUserToPostes(final List<String> postes, final String username) throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {
			@Override
			public Void runWith(EntityManager manager) throws ClientException {
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

				return null;
			}
		});
	}

	@Override
	public void removeUserFromPoste(final String posteId, final String userName) throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {
			@Override
			public Void runWith(EntityManager manager) throws ClientException {
				PosteNode poste = getPoste(posteId);
				List<String> members = poste.getMembers();
				if (members.contains(userName)) {
					members.remove(userName);
					poste.setMembers(members);
					manager.merge(poste);
					manager.flush();
				}

				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PosteNode> getAllPostes() {
		try {
			return getOrCreatePersistenceProvider().run(true, new RunCallback<List<PosteNode>>() {
				@Override
				public List<PosteNode> runWith(EntityManager manager) throws ClientException {
					Query query = manager.createQuery(ALL_POSTE_QUERY);

					List<PosteNode> lstPostes = query.getResultList();
					if (lstPostes != null && !lstPostes.isEmpty()) {
						return lstPostes;
					}
					return null;
				}
			});
		} catch (ClientException e) {
			return null;
		}
	}

	@Override
	public String deleteUserFromAllPostes(final String userId) {
		try {
			return getOrCreatePersistenceProvider().run(true, new RunCallback<String>() {
				@Override
				public String runWith(EntityManager manager) throws ClientException {
					// suppression des utilisateurs en début et milieu de chaine
					String queryUpdatePost = "UPDATE PosteNode p SET p.membres = REPLACE(p.membres, '" + userId
							+ ";','') WHERE p.membres LIKE '%" + userId + "%'";
					Query query = manager.createQuery(queryUpdatePost);
					query.executeUpdate();
					// suppression des utilisateurs en fin de chaine
					String queryUpdateEndPoste = "UPDATE PosteNode p SET p.membres = REPLACE(p.membres, ';" + userId
							+ "','') WHERE p.membres LIKE '%" + userId + "'";
					Query queryEndPoste = manager.createQuery(queryUpdateEndPoste);
					queryEndPoste.executeUpdate();
					// Suppression des utilisateurs qui sont seuls dans le poste
					String queryUpdatePosteOneUser = "UPDATE PosteNode p SET p.membres = REPLACE(p.membres, '" + userId
							+ "','') WHERE p.membres LIKE '" + userId + "'";
					Query queryOneUser = manager.createQuery(queryUpdatePosteOneUser);
					queryOneUser.executeUpdate();
					return "L'utilisateur " + userId + " a été supprimé";
				}
			});
		} catch (ClientException e) {
			return "Une erreur s'est produite " + e.toString();
		}
	}
}
