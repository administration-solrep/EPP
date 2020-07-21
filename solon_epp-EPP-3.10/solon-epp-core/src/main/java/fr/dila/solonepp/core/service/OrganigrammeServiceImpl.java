package fr.dila.solonepp.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunCallback;

import com.google.common.collect.Sets;

import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.organigramme.InstitutionNodeImpl;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Implémentation du service de gestion de l'organigramme de SOLON EPP.
 * 
 * @author jtremeaux
 */
public class OrganigrammeServiceImpl extends fr.dila.st.core.service.organigramme.OrganigrammeServiceImpl implements
		OrganigrammeService {
	/**
	 * Serial version UID
	 */
	private static final long		serialVersionUID	= 3245722454324718854L;
	/**
	 * Logger surcouche socle de log4j
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(OrganigrammeServiceImpl.class);

	private static final String		ALL_INSTIT_QUERY	= "Select i From InstitutionNode i";

	@Override
	public InstitutionNode getInstitution(final String institutionId) throws ClientException {
		return getOrCreatePersistenceProvider().run(true, new RunCallback<InstitutionNode>() {

			@Override
			public InstitutionNode runWith(EntityManager em) throws ClientException {

				return em.find(InstitutionNodeImpl.class, institutionId);
			}
		});
	}

	@Override
	public List<InstitutionNode> getInstitutionParentFromPoste(String posteId) throws ClientException {

		PosteNode poste = (PosteNode) getOrganigrammeNodeById(posteId, OrganigrammeType.POSTE);
		List<InstitutionNode> parents = new ArrayList<InstitutionNode>();

		getInstitutionParent(poste, parents);

		return parents;

	}

	/**
	 * Remonte recursivement l'organigramme (poste / unités structurelles) jusqu'à un noeud de type institution.
	 * 
	 * @param node
	 *            Noeud de l'organigramme (poste ou unité structurelle)
	 * @param resultList
	 *            Liste des institutions (construite par effet de bord)
	 * @throws ClientException
	 */
	private void getInstitutionParent(OrganigrammeNode node, List<InstitutionNode> resultList) throws ClientException {
		// Remonte les unités structurelles parentes
		List<OrganigrammeNode> parentList = getParentList(node);
		for (OrganigrammeNode parentNode : parentList) {
			if (parentNode == null) {
				LOGGER.warn(null, STLogEnumImpl.FAIL_GET_ENTITE_NODE_TEC);
				continue;
			}

			if (parentNode instanceof InstitutionNode) {
				resultList.add((InstitutionNode) parentNode);
			} else if (parentNode instanceof UniteStructurelleNode) {
				getInstitutionParent(parentNode, resultList);
			}
		}
	}

	@Override
	public void validateDeleteNode(CoreSession coreSession, OrganigrammeNode node) throws ClientException {
		// NOP
	}

	@Override
	public List<OrganigrammeNode> getChildrenList(CoreSession coreSession, OrganigrammeNode node,
			Boolean showDeactivedNode) throws ClientException {

		if (node instanceof InstitutionNode) {
			return getChildrenListInstitutionNode((InstitutionNode) node, coreSession, showDeactivedNode);
		} else {
			return super.getChildrenList(coreSession, node, showDeactivedNode);
		}

	}

	/**
	 * Liste des enfants d'une {@link InstitutionNode}
	 * 
	 * @param institutionNode
	 * @param ldapSessionContainer
	 * @param coreSession
	 * @return
	 * @throws ClientException
	 */
	private List<OrganigrammeNode> getChildrenListInstitutionNode(InstitutionNode institutionNode,
			CoreSession coreSession, Boolean showDeactivedNode) throws ClientException {
		List<OrganigrammeNode> childrenList = new ArrayList<OrganigrammeNode>();
		List<UniteStructurelleNode> usChildren = institutionNode.getSubUnitesStructurellesList();
		List<PosteNode> posteChildren = institutionNode.getSubPostesList();

		if (showDeactivedNode) {
			childrenList.addAll(usChildren);
			childrenList.addAll(posteChildren);
		} else {
			Date today = new Date();
			for (UniteStructurelleNode ust : usChildren) {
				if (ust.getDateFin() == null || ust.getDateFin().compareTo(today) > 0) {
					childrenList.add(ust);
				}
			}
			for (PosteNode posteNode : posteChildren) {
				if (posteNode.getDateFin() == null || posteNode.getDateFin().compareTo(today) > 0) {
					childrenList.add(posteNode);
				}
			}
		}
		return childrenList;
	}

	@Override
	public List<OrganigrammeNode> getParentList(OrganigrammeNode node) throws ClientException {

		if (node instanceof PosteNode) {
			return getParentListPosteNodeEPP((PosteNode) node);
		} else if (node instanceof UniteStructurelleNode) {
			return getParentListUniteStructurelleNodeEPP((UniteStructurelleNode) node);
		} else if (node instanceof InstitutionNode) {
			return null;
		} else {
			return super.getParentList(node);
		}

	}

	/**
	 * liste des parents d'un {@link UniteStructurelleNode}
	 * 
	 * @param uniteStructurelleNode
	 * @param ldapSessionContainer
	 * @return
	 * @throws ClientException
	 */
	private List<OrganigrammeNode> getParentListUniteStructurelleNodeEPP(UniteStructurelleNode uniteStructurelleNode)
			throws ClientException {
		List<OrganigrammeNode> parentList = new ArrayList<OrganigrammeNode>();
		List<UniteStructurelleNode> uniteStructurelleParentList = uniteStructurelleNode
				.getUniteStructurelleParentList();
		List<InstitutionNode> institutionParentList = uniteStructurelleNode.getInstitutionParentList();
		parentList.addAll(uniteStructurelleParentList);

		parentList.addAll(institutionParentList);

		return parentList;
	}

	/**
	 * liste des parents d'un {@link PosteNode}
	 * 
	 * @param posteNode
	 * @param ldapSessionContainer
	 * @return
	 * @throws ClientException
	 */
	private List<OrganigrammeNode> getParentListPosteNodeEPP(PosteNode posteNode) throws ClientException {
		List<OrganigrammeNode> parentList = new ArrayList<OrganigrammeNode>();
		List<UniteStructurelleNode> uniteStructurelleParentList = posteNode.getUniteStructurelleParentList();
		List<InstitutionNode> institutionParentList = posteNode.getInstitutionParentList();

		parentList.addAll(uniteStructurelleParentList);
		parentList.addAll(institutionParentList);

		return parentList;
	}

	@Override
	public List<InstitutionNode> getAllInstitutions() throws ClientException {

		return getOrCreatePersistenceProvider().run(true, new RunCallback<List<InstitutionNode>>() {

			@SuppressWarnings("unchecked")
			@Override
			public List<InstitutionNode> runWith(EntityManager em) throws ClientException {
				Query query = em.createQuery(ALL_INSTIT_QUERY);
				return query.getResultList();
			}
		});

	}

	@Override
	public List<STUser> getUserFromInstitution(String institutionId) throws ClientException {
		OrganigrammeNode institutionNode = getInstitution(institutionId);
		return getUsersInSubNode(institutionNode);

	}

	@Override
	public List<PosteNode> getPosteFromInstitution(String institutionId) throws ClientException {
		InstitutionNode institutionNode = getInstitution(institutionId);
		return institutionNode.getSubPostesList();
	}

	/**
	 * Recherche tous les utilisateurs appartenant au noeud spécifié (descend récursivement dans les sous-noeuds de
	 * l'arbre).
	 * 
	 * @param nodeParent
	 *            Noeud à recherche
	 * @param ldapSessionContainer
	 *            Conteneur de session LDAP
	 * @return Liste de postes
	 * @throws ClientException
	 */
	protected List<PosteNode> getPosteInSubNode(OrganigrammeNode nodeParent) throws ClientException {
		List<PosteNode> list = new ArrayList<PosteNode>();

		List<OrganigrammeNode> childrenList = getChildrenList(null, nodeParent, Boolean.TRUE);
		for (OrganigrammeNode node : childrenList) {
			if (node instanceof PosteNode) {
				list.add((PosteNode) node);
			} else {
				list.addAll(getPosteInSubNode(node));
			}
		}

		return list;
	}

	@Override
	public List<STUser> getUserFromInstitutionAndBaseFunction(String institutionId, String baseFunctionId)
			throws ClientException {
		final ProfileService profileService = STServiceLocator.getProfileService();
		Set<STUser> user1Set = new HashSet<STUser>(profileService.getUsersFromBaseFunction(baseFunctionId));
		Set<STUser> user2Set = new HashSet<STUser>(getUserFromInstitution(institutionId));
		return new ArrayList<STUser>(Sets.intersection(user1Set, user2Set));
	}

	@Override
	public List<? extends OrganigrammeNode> getRootNodes() throws ClientException {

		return getAllInstitutions();
	}
}
