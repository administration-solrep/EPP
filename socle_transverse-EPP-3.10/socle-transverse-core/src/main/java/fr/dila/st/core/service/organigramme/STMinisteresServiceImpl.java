package fr.dila.st.core.service.organigramme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunCallback;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.logger.MigrationLogger;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * 
 * 
 */
public class STMinisteresServiceImpl extends DefaultComponent implements STMinisteresService {

	/**
	 * Logger.
	 */
	private static final Log					LOGGER						= LogFactory
																					.getLog(STMinisteresServiceImpl.class);
	private static volatile PersistenceProvider	persistenceProvider;

	private final static String					MINISTERES_ENFANTS_QUERY	= "SELECT e1 FROM EntiteNode e1 WHERE e1.parentId = ?1";

	@Override
	public List<EntiteNode> getCurrentMinisteres() throws ClientException {

		// récupère les gouvernements courants
		OrganigrammeNode currentGouv = STServiceLocator.getSTGouvernementService().getCurrentGouvernement();
		List<EntiteNode> minList = new ArrayList<EntiteNode>();

		// récupere les ministères actifs et les ajoute
		GouvernementNode gNode = (GouvernementNode) currentGouv;
		List<EntiteNode> minListTemp = getSubEntiteNode(gNode);
		if (minListTemp != null) {
			for (EntiteNode minNode : minListTemp) {
				if (minNode.isActive() && checkToAddEntite(minNode)) {
					minList.add(minNode);
				}
			}
		}
		return minList;
	}

	@Override
	public List<EntiteNode> getAllMinisteres() throws ClientException {

		// récupère les gouvernements
		List<GouvernementNode> gouvernements = STServiceLocator.getSTGouvernementService().getGouvernementList();
		List<EntiteNode> minList = new ArrayList<EntiteNode>();

		for (OrganigrammeNode gouv : gouvernements) {
			// récupere les ministères et les ajoute
			GouvernementNode gNode = (GouvernementNode) gouv;
			List<EntiteNode> minListTemp = getSubEntiteNode(gNode);
			if (minListTemp != null) {
				for (EntiteNode minNode : minListTemp) {
					if (checkToAddEntite(minNode)) {
						minList.add(minNode);
					}
				}
			}
		}
		return minList;
	}

	@Override
	public List<EntiteNode> getMinisteres(boolean active) throws ClientException {

		// récupère les gouvernements courants
		OrganigrammeNode currentGouv = STServiceLocator.getSTGouvernementService().getCurrentGouvernement();
		List<EntiteNode> minList = new ArrayList<EntiteNode>();

		// récupere les ministères et les ajoute
		GouvernementNode gNode = (GouvernementNode) currentGouv;
		List<EntiteNode> minListTemp = getSubEntiteNode(gNode);
		if (minListTemp != null) {
			for (EntiteNode minNode : minListTemp) {
				if (minNode.isActive() == active && checkToAddEntite(minNode)) {
					minList.add(minNode);
				}
			}
		}
		return minList;
	}

	@Override
	public EntiteNode getEntiteNode(String entiteId) throws ClientException {
		return (EntiteNode) STServiceLocator.getOrganigrammeService().getOrganigrammeNodeById(entiteId,
				OrganigrammeType.MINISTERE);
	}

	@Override
	public EntiteNode createEntite(EntiteNode newEntite) throws ClientException {
		return (EntiteNode) STServiceLocator.getOrganigrammeService().createNode(newEntite);
	}

	@Override
	public void updateEntite(EntiteNode entite) throws ClientException {
		STServiceLocator.getOrganigrammeService().updateNode(entite, Boolean.TRUE);
	}

	private boolean checkToAddEntite(OrganigrammeNode minNode) {
		if (minNode instanceof EntiteNode) {
			EntiteNode entiteNode = (EntiteNode) minNode;
			List<String> functionReadList = entiteNode.getFunctionRead();
			if (!functionReadList.contains(STBaseFunctionConstant.ASSEMBLEES_PARLEMENTAIRES_READER)) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	@Override
	public List<EntiteNode> getMinistereParentFromPoste(final String posteId) throws ClientException {
		final PosteNode poste = (PosteNode) STServiceLocator.getOrganigrammeService().getOrganigrammeNodeById(posteId,
				OrganigrammeType.POSTE);

		if (poste != null && StringUtil.isNotBlank(poste.getParentEntiteId())) {
			return poste.getEntiteParentList();
		}

		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<EntiteNode>>() {

			@Override
			public List<EntiteNode> runWith(EntityManager manager) throws ClientException {
				Set<EntiteNode> ministereList = new HashSet<EntiteNode>();
				if (poste != null) {

					if (poste.getEntiteParentList() != null && !poste.getEntiteParentList().isEmpty()) {
						return poste.getEntiteParentList();
					} else {
						// On récupère les parents des unités pour trouver les entités
						if (poste.getUniteStructurelleParentList() != null) {
							for (UniteStructurelleNode node : poste.getUniteStructurelleParentList()) {
								ministereList.addAll(getEntiteParentsFromUnite(node, manager));
							}
						}
					}
				}
				return new ArrayList<EntiteNode>(ministereList);
			}
		});

	}

	private List<EntiteNode> getEntiteParentsFromUnite(UniteStructurelleNode node, EntityManager manager)
			throws ClientException {
		if (node == null) {
			return new ArrayList<EntiteNode>();
		}

		if (StringUtil.isNotBlank(node.getParentEntiteId())) {
			return node.getEntiteParentList();
		}
		if (StringUtil.isNotBlank(node.getParentUniteId())) {
			List<UniteStructurelleNode> lstParents = node.getUniteStructurelleParentList();
			Set<EntiteNode> entiteParents = new HashSet<EntiteNode>();
			for (UniteStructurelleNode parent : lstParents) {
				entiteParents.addAll(getEntiteParentsFromUnite(parent, manager));
			}
			return new ArrayList<EntiteNode>(entiteParents);
		}
		return new ArrayList<EntiteNode>();

	}

	@Override
	public List<EntiteNode> getMinistereParentFromPostes(Set<String> posteIds) throws ClientException {
		List<EntiteNode> ministereList = new ArrayList<EntiteNode>();
		for (String posteId : posteIds) {
			ministereList.addAll(getMinistereParentFromPoste(posteId));
		}
		return ministereList;
	}

	@Override
	public List<EntiteNode> getMinistereParentFromUniteStructurelle(final String ustId) throws ClientException {
		if (StringUtil.isNotBlank(ustId)) {

			return getOrCreatePersistenceProvider().run(true, new RunCallback<List<EntiteNode>>() {

				@Override
				public List<EntiteNode> runWith(EntityManager manager) throws ClientException {

					List<EntiteNode> ministereList = new ArrayList<EntiteNode>();
					UniteStructurelleNode orgDoc = manager.find(UniteStructurelleNodeImpl.class, ustId);
					if (orgDoc != null) {

						ministereList = orgDoc.getEntiteParentList();
					}

					return ministereList;
				}
			});
		}

		return new ArrayList<EntiteNode>();

	}

	protected List<EntiteNode> getSubEntiteNode(GouvernementNode gouvernement) throws ClientException {
		List<EntiteNode> subEntiteNodeList = new LinkedList<EntiteNode>();

		if (gouvernement == null) {
			return subEntiteNodeList;
		}

		return (List<EntiteNode>) gouvernement.getSubEntitesList();
	}

	@Override
	public List<STUser> getUserFromMinistere(String ministereId) throws ClientException {
		OrganigrammeNode node = STServiceLocator.getOrganigrammeService().getOrganigrammeNodeById(ministereId,
				OrganigrammeType.MINISTERE);
		return STServiceLocator.getOrganigrammeService().getUsersInSubNode(node);
	}

	@Override
	public List<EntiteNode> getMinisteresParents(OrganigrammeNode childNode) throws ClientException {

		ArrayList<EntiteNode> parents = new ArrayList<EntiteNode>();

		getMinistereParent(childNode, parents);

		return parents;
	}

	@Override
	public void getMinistereParent(OrganigrammeNode node, List<EntiteNode> resultList) throws ClientException {
		// Remonte les unités structurelles parentes
		List<OrganigrammeNode> parentList = STServiceLocator.getOrganigrammeService().getParentList(node);
		for (OrganigrammeNode parentNode : parentList) {
			if (parentNode == null) {
				LOGGER.warn("Unité structurelle parente non trouvée");
				continue;
			}

			if (parentNode instanceof EntiteNode) {
				resultList.add((EntiteNode) parentNode);
			} else if (parentNode instanceof UniteStructurelleNode) {
				getMinistereParent(parentNode, resultList);
			}
		}
	}

	@Override
	public void migrateUnchangedEntiteToNewGouvernement(final String entiteId, final String newGouvernementId)
			throws ClientException {

		getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {

			@Override
			public Void runWith(EntityManager manager) throws ClientException {

				final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
				// recupère et set le gvt parent
				GouvernementNode newGouvNode = (GouvernementNode) organigrammeService.getOrganigrammeNodeById(
						newGouvernementId, OrganigrammeType.GOUVERNEMENT);

				OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(entiteId,
						OrganigrammeType.MINISTERE);
				MigrationLogger.getInstance().logMigration(STLogEnumImpl.MIGRATE_MINISTERE_TEC,
						"Migration de l'organigramme [" + node.getLabel() + "] vers [" + node.getLabel() + "]");

				List<OrganigrammeNode> parentList = new ArrayList<OrganigrammeNode>();
				parentList.add(newGouvNode);
				node.setParentList(parentList);

				manager.merge(node);
				manager.flush();

				return null;
			}
		});

	}

	@Override
	public void migrateEntiteToNewGouvernement(String currentMin, String newMin) throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();

		OrganigrammeNode currentEntiteNode = organigrammeService.getOrganigrammeNodeById(currentMin,
				OrganigrammeType.MINISTERE);
		OrganigrammeNode newEntiteNode = organigrammeService
				.getOrganigrammeNodeById(newMin, OrganigrammeType.MINISTERE);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Début migration de l'organigramme [" + currentEntiteNode.getLabel() + "] vers ["
					+ newEntiteNode.getLabel() + "]");
		}
		List<OrganigrammeNode> nodeToSaveList = new ArrayList<OrganigrammeNode>();

		List<OrganigrammeNode> childList = organigrammeService.getChildrenList(null, currentEntiteNode, Boolean.TRUE);
		for (OrganigrammeNode childNode : childList) {
			List<OrganigrammeNode> parentList = organigrammeService.getParentList(childNode);
			parentList.remove(currentEntiteNode);
			if (!parentList.contains(newEntiteNode)) {
				parentList.add(newEntiteNode);
			}
			childNode.setParentList(parentList);
			nodeToSaveList.add(childNode);
		}

		// désactive l'ancienne entité
		List<OrganigrammeNode> parentEntite = STServiceLocator.getOrganigrammeService()
				.getParentList(currentEntiteNode);
		if (parentEntite != null && !parentEntite.isEmpty() && parentEntite.get(0) instanceof GouvernementNode
				&& parentEntite.get(0).getDateFin() != null) {
			currentEntiteNode.setDateFin(parentEntite.get(0).getDateFin());
		} else {
			currentEntiteNode.setDateFin(new Date());
		}
		nodeToSaveList.add(currentEntiteNode);

		// sauve les nodes modifiés
		STServiceLocator.getOrganigrammeService().updateNodes(nodeToSaveList, false);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Fin migration de l'organigramme [" + currentEntiteNode.getLabel() + "] vers ["
					+ newEntiteNode.getLabel() + "]");
		}
	}

	@Override
	public List<EntiteNode> getEntiteNodes(Collection<String> entiteIds) throws ClientException {

		List<EntiteNode> lstEntites = new ArrayList<EntiteNode>();

		for (String id : entiteIds) {
			if (StringUtil.isNotBlank(id)) {
				lstEntites.add(getEntiteNode(id));
			}
		}

		return lstEntites;

	}

	@Override
	public EntiteNode getBareEntiteModel() throws ClientException {
		return new EntiteNodeImpl();
	}

	@Override
	public List<String> findUserFromMinistere(String ministereId) throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(ministereId, OrganigrammeType.MINISTERE);
		if (node != null) {
			return organigrammeService.findUserInSubNode(node);
		}
		return new ArrayList<String>();
	}

	private static PersistenceProvider getOrCreatePersistenceProvider() {
		if (persistenceProvider == null) {
			synchronized (STMinisteresServiceImpl.class) {
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
			synchronized (STMinisteresServiceImpl.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EntiteNode> getEntiteNodeEnfant(final String nodeID) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<EntiteNode>>() {

			@Override
			public List<EntiteNode> runWith(EntityManager manager) throws ClientException {
				List<EntiteNode> lstEntiteNodes = new ArrayList<EntiteNode>();
				Query query = manager.createQuery(MINISTERES_ENFANTS_QUERY);
				if (StringUtil.isNotBlank(nodeID)) {
					query.setParameter(1, nodeID);
					lstEntiteNodes = query.getResultList();
				}
				return lstEntiteNodes;
			}
		});

	}
}
