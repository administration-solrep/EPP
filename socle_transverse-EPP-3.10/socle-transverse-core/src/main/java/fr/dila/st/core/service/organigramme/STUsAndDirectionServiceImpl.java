package fr.dila.st.core.service.organigramme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunCallback;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * 
 * 
 */
public class STUsAndDirectionServiceImpl extends DefaultComponent implements STUsAndDirectionService {
	private static volatile PersistenceProvider	persistenceProvider;

	private final static String					UNITE_STRUCT_ENFANTS_QUERY					= "SELECT u1 FROM UniteStructurelleNode u1 WHERE u1.parentUniteId LIKE ?1";
	private final static String					UNITE_STRUCT_ENFANTS_ENTITE_PARENT_QUERY	= "SELECT u1 FROM UniteStructurelleNode u1 WHERE u1.parentEntiteId LIKE ?1";
	private final static String					UNITE_STRUCT_ENFANTS_INSTIT_PARENT_QUERY	= "SELECT u1 FROM UniteStructurelleNode u1 WHERE u1.parentInstitId LIKE ?1";

	private final static String					DIRECTION_FOR_USER_QUERY					= "SELECT DISTINCT u FROM UniteStructurelleNode u, PosteNode p WHERE p.membres LIKE :user AND u.type='DIR' AND u.dateFin>:curDate AND p.parentUniteId=u.idOrganigramme";
	private final static String					DIRECTION_LIGHT_FOR_USER_QUERY				= "SELECT DISTINCT u.label FROM UniteStructurelleNode u, PosteNode p WHERE p.membres LIKE :user AND u.type='DIR' AND u.dateFin>:curDate AND p.parentUniteId=u.idOrganigramme";

	@Override
	public List<STUser> getUserFromUniteStructurelle(String uniteStructurelleId) throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();

		OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(uniteStructurelleId,
				OrganigrammeType.UNITE_STRUCTURELLE);
		return organigrammeService.getUsersInSubNode(node);
	}

	@Override
	public void updateUniteStructurelle(UniteStructurelleNode uniteStructurelle) throws ClientException {
		STServiceLocator.getOrganigrammeService().updateNode(uniteStructurelle, Boolean.TRUE);
	}

	@Override
	public UniteStructurelleNode createUniteStructurelle(UniteStructurelleNode newUniteStructurelle)
			throws ClientException {
		return (UniteStructurelleNode) STServiceLocator.getOrganigrammeService().createNode(newUniteStructurelle);
	}

	@Override
	public UniteStructurelleNode getBareUniteStructurelleModel() throws ClientException {
		return new UniteStructurelleNodeImpl();
	}

	@Override
	public List<OrganigrammeNode> getDirectionFromPoste(String posteId) throws ClientException {
		List<OrganigrammeNode> directionsList = new ArrayList<OrganigrammeNode>();

		OrganigrammeNode node = STServiceLocator.getOrganigrammeService().getOrganigrammeNodeById(posteId,
				OrganigrammeType.POSTE);
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
	private void getDirectionParent(List<OrganigrammeNode> directionsList, OrganigrammeNode node)
			throws ClientException {
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
	public UniteStructurelleNode getUniteStructurelleNode(String usId) throws ClientException {
		return (UniteStructurelleNode) STServiceLocator.getOrganigrammeService().getOrganigrammeNodeById(usId,
				OrganigrammeType.UNITE_STRUCTURELLE);
	}

	@Override
	public List<UniteStructurelleNode> getDirectionListFromMinistere(EntiteNode ministereNode) throws ClientException {
		List<UniteStructurelleNode> list = new ArrayList<UniteStructurelleNode>();

		for (UniteStructurelleNode child : ministereNode.getSubUnitesStructurellesList()) {
			if (child.getType() == OrganigrammeType.DIRECTION) {
				list.add(child);
			}
		}
		return list;
	}

	@Override
	public List<String> findUserFromUniteStructurelle(String uniteStructurelleId) throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(uniteStructurelleId,
				OrganigrammeType.UNITE_STRUCTURELLE);
		if (node != null) {
			return organigrammeService.findUserInSubNode(node);
		} else {
			return new ArrayList<String>();
		}
	}

	@Override
	public List<UniteStructurelleNode> getUniteStructurelleParent(final UniteStructurelleNode node)
			throws ClientException {
		return node.getUniteStructurelleParentList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UniteStructurelleNode> getUniteStructurelleEnfant(final String nodeID, final OrganigrammeType type)
			throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<UniteStructurelleNode>>() {

			@Override
			public List<UniteStructurelleNode> runWith(EntityManager manager) throws ClientException {
				List<UniteStructurelleNode> lstUniteStructurelleNodes = new ArrayList<UniteStructurelleNode>();

				Query query = null;
				if (type == OrganigrammeType.MINISTERE) {
					query = manager.createQuery(UNITE_STRUCT_ENFANTS_ENTITE_PARENT_QUERY);
				} else if (type == OrganigrammeType.DIRECTION || type == OrganigrammeType.UNITE_STRUCTURELLE) {
					query = manager.createQuery(UNITE_STRUCT_ENFANTS_QUERY);
				} else if (type == OrganigrammeType.INSTITUTION) {
					query = manager.createQuery(UNITE_STRUCT_ENFANTS_INSTIT_PARENT_QUERY);
				}
				if (StringUtil.isNotBlank(nodeID) && query != null) {
					query.setParameter(1, "%" + nodeID + "%");
					lstUniteStructurelleNodes = query.getResultList();
				}
				return lstUniteStructurelleNodes;

			}
		});

	}

	@Override
	public List<EntiteNode> getEntiteParent(final UniteStructurelleNode node) throws ClientException {
		return node.getEntiteParentList();
	}

	private static PersistenceProvider getOrCreatePersistenceProvider() {
		if (persistenceProvider == null) {
			synchronized (STUsAndDirectionServiceImpl.class) {
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
			synchronized (STUsAndDirectionServiceImpl.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	protected List<String> extractIdsFromElement(List<? extends OrganigrammeNode> lstOrga) {
		List<String> lstIds = new ArrayList<String>();

		for (OrganigrammeNode orga : lstOrga) {
			lstIds.add(orga.getId().toString());
		}

		return lstIds;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UniteStructurelleNode> getDirectionsParentsFromUser(final String userId) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<UniteStructurelleNode>>() {

			@Override
			public List<UniteStructurelleNode> runWith(EntityManager manager) throws ClientException {
				List<UniteStructurelleNode> lstNodes = new ArrayList<UniteStructurelleNode>();
				if (StringUtil.isNotEmpty(userId)) {
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
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDirectionNameParentsFromUser(final String userId) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<String>>() {

			@Override
			public List<String> runWith(EntityManager manager) throws ClientException {
				List<String> lstNodes = new ArrayList<String>();
				if (StringUtil.isNotEmpty(userId)) {
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
		});
	}
}
