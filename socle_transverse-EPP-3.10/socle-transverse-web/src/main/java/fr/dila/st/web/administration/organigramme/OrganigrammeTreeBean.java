package fr.dila.st.web.administration.organigramme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeExpandedEvent;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.organigramme.AlphabeticalAndBdcOrderComparator;
import fr.dila.st.core.organigramme.UserNodeImpl;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Classe de gestion de l'arbre contenant l'organigramme.
 * 
 * @author FEO
 */
@Name("organigrammeTree")
@Scope(ScopeType.SESSION)
public class OrganigrammeTreeBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long								serialVersionUID			= 1L;

	private static final Log								log							= LogFactory
																								.getLog(OrganigrammeTreeBean.class);

	protected List<OrganigrammeDataTreeNode>				rootNodes;

	protected Boolean										showDeactivedNode			= Boolean.FALSE;

	public static final String								ORGANIGRAMME_CHANGED_EVENT	= "OrganigrammeChangedEvent";

	protected String										filterMinistereId			= null;

	protected List<OrganigrammeDataTreeNode>				filteredNodes;

	protected Map<String, List<OrganigrammeDataTreeNode>>	filteredNodeMap;

	@In(create = true, required = false)
	protected CoreSession									documentManager;

	protected Boolean										visible						= Boolean.FALSE;

	/**
	 * Constructeur de OrganigrammeTreeBean.
	 */
	public OrganigrammeTreeBean() {
		filteredNodeMap = new HashMap<String, List<OrganigrammeDataTreeNode>>();
	}

	/**
	 * charge l'arbre contenant l'organigramme
	 * 
	 * @throws ClientException
	 */
	private List<OrganigrammeDataTreeNode> loadTree() throws ClientException {

		List<OrganigrammeDataTreeNode> rootNodes = new ArrayList<OrganigrammeDataTreeNode>();

		List<? extends OrganigrammeNode> baseGroups = STServiceLocator.getOrganigrammeService().getRootNodes();

		Date today = new Date();
		String ministereId = null;

		for (OrganigrammeNode groupNode : baseGroups) {
			if (groupNode.getDateFin() == null || groupNode.getDateFin().compareTo(today) > 0 || showDeactivedNode) {
				ministereId = groupNode.getId();
				OrganigrammeDataTreeNode dataTreeNode = new OrganigrammeDataTreeNode(groupNode, ministereId);
				dataTreeNode.setOpened(true);
				addSubGroups(dataTreeNode, Boolean.FALSE);
				for (OrganigrammeDataTreeNode node : dataTreeNode.getChildren()) {
					addSubGroups(node, Boolean.TRUE);
				}
				rootNodes.add(dataTreeNode);
			}
		}

		return rootNodes;
	}

	public void changeExpandListener(NodeExpandedEvent event) throws ClientException {
		UIComponent component = event.getComponent();
		if (component instanceof UITree) {
			UITree treeComponent = (UITree) component;
			Object value = treeComponent.getRowData();
			if (value instanceof OrganigrammeDataTreeNode) {
				OrganigrammeDataTreeNode dataTreeNode = (OrganigrammeDataTreeNode) value;
				if (!dataTreeNode.isOpened()) {
					dataTreeNode.setOpened(true);
					// reload les enfants afin d'afficher les éventuelles modifs ou verrous
					addSubGroups(dataTreeNode, Boolean.FALSE);
					// charge les sous enfants pour afficher le +
					for (OrganigrammeDataTreeNode node : dataTreeNode.getChildren()) {
						addSubGroups(node, Boolean.TRUE);
					}
				} else {
					dataTreeNode.setOpened(false);
				}
			}
		}
	}

	public Boolean adviseNodeOpened(UITree treeComponent) {
		Object value = treeComponent.getRowData();
		if (value instanceof OrganigrammeDataTreeNode) {
			OrganigrammeDataTreeNode dataTreeNode = (OrganigrammeDataTreeNode) value;
			return dataTreeNode.isOpened();
		}
		return null;
	}

	/**
	 * Méthode qui ajoute les sous-groupes d'un noeud
	 * 
	 * @param onlyFirstChild
	 * 
	 * @param group
	 *            groupe contenant les sous groupes
	 * @param nodeParent
	 *            noeud dans lequel les sous-groupes sont ajoutés
	 * @throws ClientException
	 */
	protected void addSubGroups(OrganigrammeDataTreeNode dataTreeNode, Boolean onlyFirstChild) throws ClientException {
		List<OrganigrammeNode> subGroups = null;

		// Efface les enfants existant
		dataTreeNode.getChildren().clear();

		OrganigrammeNode group = dataTreeNode.getNode();

		if (group != null) {
			if (onlyFirstChild) {
				OrganigrammeNode node = STServiceLocator.getOrganigrammeService().getFirstChild(group, documentManager,
						showDeactivedNode);
				if (node != null) {
					subGroups = new ArrayList<OrganigrammeNode>();
					subGroups.add(node);
				}
			} else {
				subGroups = STServiceLocator.getOrganigrammeService().getChildrenList(documentManager, group,
						showDeactivedNode);
			}

			if (subGroups != null && !subGroups.isEmpty()) {
				// Tri sur la liste pour classer les Unites Structurelles et postes par ordre alphabetique et suivant la
				// possession d'un bdc
				Collections.sort(subGroups, new AlphabeticalAndBdcOrderComparator());

				for (OrganigrammeNode childGroup : subGroups) {
					String ministereId = dataTreeNode.getMinistereId();
					if (childGroup instanceof EntiteNode) {
						ministereId = childGroup.getId();
					}

					dataTreeNode.addChild(new OrganigrammeDataTreeNode(childGroup, ministereId, dataTreeNode));
				}
			}

			if (group instanceof PosteNode) {
				addUsers(dataTreeNode);
			}
		}
	}

	protected void addUsers(OrganigrammeDataTreeNode dataTreeNode) throws ClientException {
		List<String> userList = null;

		OrganigrammeNode posteNode = dataTreeNode.getNode();

		userList = ((PosteNode) posteNode).getMembers();

		List<OrganigrammeDataTreeNode> children = new ArrayList<OrganigrammeDataTreeNode>();
		List<String> usersLastName = new ArrayList<String>();

		final UserManager userManager = STServiceLocator.getUserManager();
		for (String userId : userList) {
			DocumentModel user = userManager.getUserModel(userId);
			if (user == null) {
				log.debug("Utilisateur <" + userId
						+ "> défini dans un poste, mais non existant dans la branche utilisateurs");
				continue;
			}

			STUser stUser = user.getAdapter(STUser.class);
			if (stUser.isDeleted()) {
				continue;
			}
			if (stUser.isActive() || showDeactivedNode) {
				UserNode userNode = new UserNodeImpl();
				StringBuilder label = new StringBuilder();
				String lastName = " ";

				if (!StringUtils.isEmpty(stUser.getFirstName())) {
					label.append(stUser.getFirstName());
					label.append(" ");
				}
				if (!StringUtils.isEmpty(stUser.getLastName())) {
					lastName = stUser.getLastName();
					label.append(lastName);
					usersLastName.add(lastName);
				}

				Collections.sort(usersLastName);

				userNode.setActive(stUser.isActive());
				userNode.setLabel(label.toString());
				userNode.setId(userId);
				if (usersLastName.indexOf(lastName) >= 0) {
					children.add(usersLastName.indexOf(lastName), new OrganigrammeDataTreeNode(userNode));
				} else {
					log.error("L'utilisateur " + lastName
							+ " a des données corrompues, veuillez le modifier/supprimmer.");
				}
			}
		}
		dataTreeNode.setChildren(children);

	}

	/**
	 * Méthode qui renvoie l'organigramme complet.
	 * 
	 * @return l'organigramme chargé
	 * @throws ClientException
	 */
	public List<OrganigrammeDataTreeNode> getOrganigramme() throws ClientException {
		if (rootNodes == null) {
			rootNodes = loadTree();
		}
		return rootNodes;
	}

	/**
	 * Méthode qui renvoie l'organigramme avec le filtre par ministère spécifié
	 * 
	 * @param ministereId
	 * @param filtreMemeSiAdminSgg
	 * @return List<OrganigrammeDataTreeNode>
	 * @throws ClientException
	 */
	public List<OrganigrammeDataTreeNode> getOrganigramme(String ministereId, boolean filtreMemeSiProfilSgg)
			throws ClientException {
		if (rootNodes == null) {
			rootNodes = loadTree();
		}

		STPrincipal principal = (STPrincipal) documentManager.getPrincipal();
		// pas de filtre
		if (ministereId.isEmpty() || principal.isMemberOf(STBaseFunctionConstant.PROFIL_SGG) && !filtreMemeSiProfilSgg) {
			return rootNodes;
		}

		// filtre par ministère
		if (ministereId.equals(filterMinistereId) && filteredNodes != null) {
			return filteredNodes;
		}

		filterMinistereId = ministereId;
		filteredNodes = loadTree();

		List<OrganigrammeDataTreeNode> filteredMinNodes = new ArrayList<OrganigrammeDataTreeNode>();
		if (filterMinistereId == null || filterMinistereId.equals("null")) {
			// Récupération du ministère de l'utilisateur
			final UserManager userManager = STServiceLocator.getUserManager();
			DocumentModel userDoc = userManager.getUserModel(principal.getName());
			STUser stUser = userDoc.getAdapter(STUser.class);
			if (stUser != null) {
				STUserService stUserService = STServiceLocator.getSTUserService();
				List<String> listIdsMinistere = stUserService.getAllUserMinisteresId(stUser.getUsername());
				for (OrganigrammeDataTreeNode gvtNode : filteredNodes) {
					filteredMinNodes.clear();
					for (OrganigrammeDataTreeNode minNode : gvtNode.getChildren()) {
						if (listIdsMinistere.contains(minNode.getMinistereId())) {
							filteredMinNodes.add(minNode);
						}
					}
					gvtNode.setChildren(filteredMinNodes);
				}
			}
		} else {
			for (OrganigrammeDataTreeNode gvtNode : filteredNodes) {
				filteredMinNodes.clear();
				for (OrganigrammeDataTreeNode minNode : gvtNode.getChildren()) {
					if (minNode.getMinistereId().equals(filterMinistereId)) {
						filteredMinNodes.add(minNode);
					}
				}
				gvtNode.setChildren(filteredMinNodes);
			}
		}

		return filteredNodes;
	}

	/**
	 * Méthode qui renvoie l'organigramme avec le filtre par ministère spécifié
	 * 
	 * @return
	 * @throws ClientException
	 */
	public List<OrganigrammeDataTreeNode> getOrganigramme(String ministereId) throws ClientException {
		return getOrganigramme(ministereId, false);
	}

	@Observer(ORGANIGRAMME_CHANGED_EVENT)
	public void organigrammeChanged() throws ClientException {
		updateTree();
		filteredNodes = null;
	}

	public Boolean getShowDeactivedNode() {
		return showDeactivedNode;
	}

	public void setShowDeactivedNode(Boolean showDeactivedNode) throws ClientException {
		if (!this.showDeactivedNode.equals(showDeactivedNode)) {
			this.showDeactivedNode = showDeactivedNode;
			updateTree();
		}
	}

	public void cleanTree() {
		rootNodes = null;
		filteredNodes = null;
	}

	/**
	 * Mise à jour de l'arbre
	 * 
	 * @throws ClientException
	 */
	private void updateTree() throws ClientException {

		if (rootNodes == null) {
			rootNodes = loadTree();
			return;
		}

		List<? extends OrganigrammeNode> ldapGvtNodeList = STServiceLocator.getOrganigrammeService().getRootNodes();

		int position = 0;
		for (OrganigrammeNode ldapNode : ldapGvtNodeList) {
			OrganigrammeDataTreeNode ldapGvtDataTreeNode = new OrganigrammeDataTreeNode(ldapNode);

			if (rootNodes.contains(ldapGvtDataTreeNode)) {
				int index = rootNodes.indexOf(ldapGvtDataTreeNode);
				OrganigrammeDataTreeNode dataTreeNode = rootNodes.get(index);

				if (ldapNode.isActive() || showDeactivedNode) {
					dataTreeNode.setNode(ldapNode);
				} else {
					rootNodes.remove(index);
					position--;
				}
				position++;

			} else if (ldapNode.isActive() || showDeactivedNode) {
				rootNodes.add(position, ldapGvtDataTreeNode);
				addSubGroups(ldapGvtDataTreeNode, Boolean.FALSE);
				position++;
			}

		}

		List<OrganigrammeDataTreeNode> newDataTreeNodeList = new ArrayList<OrganigrammeDataTreeNode>();
		// Suppression des noeuds qui étaient dans la liste d'affichage mais ne sont plus présent dans la liste ldap
		for (OrganigrammeDataTreeNode dataTreeNode : rootNodes) {
			OrganigrammeNode originalNode = dataTreeNode.getNode();
			if (ldapGvtNodeList.contains(originalNode) && (originalNode.isActive() || showDeactivedNode)) {
				newDataTreeNodeList.add(dataTreeNode);
			}
		}
		rootNodes = newDataTreeNodeList;

		for (OrganigrammeDataTreeNode rootNode : rootNodes) {
			if (rootNode.isOpened()) {
				recursiveUpdateTreeNode(rootNode);
			} else {
				addSubGroups(rootNode, Boolean.FALSE);
			}
		}
	}

	/**
	 * Mise à jour des enfants d'un élement de l'arbre, appelle récursif pour les enfants ouverts
	 * 
	 * @param toUpdateDataTreeNode
	 * @throws ClientException
	 */
	protected void recursiveUpdateTreeNode(OrganigrammeDataTreeNode toUpdateDataTreeNode) throws ClientException {

		List<OrganigrammeDataTreeNode> childDataTreeNodeList = toUpdateDataTreeNode.getChildren();
		Set<OrganigrammeNode> ldapChildNodeList = new LinkedHashSet<OrganigrammeNode>(STServiceLocator
				.getOrganigrammeService()
				.getChildrenList(documentManager, toUpdateDataTreeNode.getNode(), Boolean.TRUE));

		// position du noeud dans la liste d'enfants
		int position = 0;
		for (OrganigrammeNode ldapNode : ldapChildNodeList) {
			OrganigrammeDataTreeNode ldapDataTreeNode = new OrganigrammeDataTreeNode(ldapNode,
					toUpdateDataTreeNode.getMinistereId(), toUpdateDataTreeNode);
			// le noeud existe déjà dans la liste d'affichage, on le met à jour
			// puis, s'il est ouvert on appelle cette méthode sur le noeud pour traiter les enfants
			if (childDataTreeNodeList.contains(ldapDataTreeNode)) {
				int index = childDataTreeNodeList.indexOf(ldapDataTreeNode);
				OrganigrammeDataTreeNode dataTreeNode = childDataTreeNodeList.get(index);
				// Si actif ou affichage des inactifs
				if (ldapNode.isActive() || showDeactivedNode) {
					dataTreeNode.setNode(ldapNode);
					// S'il s'agit d'un poste on charge les utilisateus, sinon
					// s'il est ouvert on traite les enfants sinon on charge les enfants pour l'affichage du +
					if (dataTreeNode.isOpened()) {
						if (ldapNode instanceof PosteNode) {
							dataTreeNode.getChildren().clear();
							addUsers(dataTreeNode);
						} else {
							recursiveUpdateTreeNode(dataTreeNode);
						}
					} else {
						addSubGroups(dataTreeNode, Boolean.TRUE);
					}
				} else {
					// Sinon on l'enlève de la liste d'affichage
					childDataTreeNodeList.remove(dataTreeNode);
					position--;
				}
				position++;
				// Sinon, il s'agit d'un nouveau noeud, on l'ajoute s'il est actif ou si on affiche les inactifs
			} else if (ldapNode.isActive() || showDeactivedNode) {
				childDataTreeNodeList.add(position, ldapDataTreeNode);
				addSubGroups(ldapDataTreeNode, Boolean.TRUE);
				position++;
			}

		}

		List<OrganigrammeDataTreeNode> newDataTreeNodeList = new ArrayList<OrganigrammeDataTreeNode>();
		// Suppression des noeuds qui étaient dans la liste d'affichage mais ne sont plus présent dans la liste d'enfant
		for (OrganigrammeDataTreeNode dataTreeNode : childDataTreeNodeList) {
			OrganigrammeNode originalNode = dataTreeNode.getNode();
			if (ldapChildNodeList.contains(originalNode) && (originalNode.isActive() || showDeactivedNode)) {
				newDataTreeNodeList.add(dataTreeNode);
			}
		}
		toUpdateDataTreeNode.setChildren(newDataTreeNodeList);
	}

	/**
	 * Reinitialise l'état d'ouverture/fermeture des noeuds de l'organigramme
	 * 
	 * @return
	 */
	public String collapseOrganigramme() {
		if (rootNodes != null) {
			for (OrganigrammeDataTreeNode rootNode : rootNodes) {
				rootNode.setOpened(true);
				List<OrganigrammeDataTreeNode> childList = rootNode.getChildren();
				for (OrganigrammeDataTreeNode childNode : childList) {
					childNode.setOpened(false);
				}
			}
		}

		if (filteredNodes != null) {
			for (OrganigrammeDataTreeNode rootNode : filteredNodes) {
				rootNode.setOpened(true);
				List<OrganigrammeDataTreeNode> childList = rootNode.getChildren();
				for (OrganigrammeDataTreeNode childNode : childList) {
					childNode.setOpened(false);
				}
			}
		}
		return null;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
		if (Boolean.TRUE.equals(visible)) {
			collapseOrganigramme();
		}
	}

	public Boolean isVisible() {
		return visible;
	}

}
