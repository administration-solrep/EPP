package fr.dila.solonepp.web.action.admin.organigramme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.platform.ui.web.component.list.UIEditableList;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.solonepp.api.descriptor.evenementtype.DistributionElementDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.action.evenement.EvenementTypeActionsBean;
import fr.dila.solonepp.web.converter.InstitutionIdToLabelConverter;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.exception.LocalizedClientException;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.web.administration.organigramme.OrganigrammeTreeBean;
import fr.dila.st.web.converter.OrganigrammeGvtIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeMinIdConverter;
import fr.dila.st.web.converter.OrganigrammeMinIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeMultiIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeNodeIdConverter;
import fr.dila.st.web.converter.OrganigrammePosteIdConverter;
import fr.dila.st.web.converter.OrganigrammePosteIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeUSIdConverter;
import fr.dila.st.web.converter.OrganigrammeUSIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeUserIdToLabelConverter;

/**
 * ActionBean de gestion de l'organigramme
 * 
 * @author FEO
 */
@Name("organigrammeManagerActions")
@SerializedConcurrentAccess
@Scope(ScopeType.CONVERSATION)
public class OrganigrammeManagerActionsBean implements Serializable {

	/**
	 * UID
	 */
	private static final long				serialVersionUID	= 8280568166164816706L;

	@In(required = true, create = true)
	protected EppPrincipal					eppPrincipal;

	/**
	 * Logger surcouche socle de log4j
	 */
	private static final STLogger			LOGGER				= STLogFactory
																		.getLog(OrganigrammeManagerActionsBean.class);

	public static final String				ORGANIGRAMME_VIEW	= "view_organigramme";

	public static final String				ORGANIGRAMME_SCHEMA	= "organigramme";
	public static final String				MULTI				= "MULTI";
	public static final String				LIST				= "LIST";
	public static final String				SINGLE				= "SINGLE";

	public static final String				USER_TYPE			= "USER_TYPE";
	public static final String				MIN_TYPE			= "MIN_TYPE";
	public static final String				INS_TYPE			= "INS_TYPE";
	public static final String				UST_TYPE			= "UST_TYPE";
	public static final String				GVT_TYPE			= "GVT_TYPE";
	public static final String				DIR_TYPE			= "DIR_TYPE";
	public static final String				USER_MAIL_TYPE		= "USER_MAIL_TYPE";
	public static final String				POSTE_TYPE			= "POSTE_TYPE";
	public static final String				DIR_AND_UST_TYPE	= "DIR_AND_UST_TYPE";
	public static final String				MAILBOX_TYPE		= "MAILBOX_TYPE";

	protected transient OrganigrammeService	organigrammeService;

	@In(create = true, required = false)
	protected transient FacesMessages		facesMessages;

	@In(create = true)
	protected ResourcesAccessor				resourcesAccessor;

	@In(create = true, required = false)
	protected transient WebActions			webActions;

	@In(create = true, required = false)
	protected CoreSession					documentManager;

	@In(create = true)
	protected OrganigrammeTreeBean			organigrammeTree;
	
	@In(create = true)
	protected EvenementTypeActionsBean		evenementTypeActions;

	/**
	 * Id of the editable list component where selection ids are put.
	 * <p>
	 * Component must be an instance of {@link UIEditableList}
	 */
	@RequestParameter
	protected String						organigrammeSelectionListId;

	@RequestParameter
	protected String						organigrammeSelectionType;

	@RequestParameter
	protected String						organigrammeSelectionMultiple;

	protected List<Action>					organigrammeActions;

	protected OrganigrammeNode				selectedNode;

	private String							creationType;

	protected String						selectedNodeId;

	protected String						selectedNodeType;

	protected String						newGroupName;

	protected OrganigrammeNode				currentNode;

	protected boolean						nameAlreadyUsed		= false;

	protected boolean						confirmDialog		= false;

	// Gestion du changement de timbre
	/**
     * 
     */
	protected Map<String, String>			newTimbre;

	protected List<SelectItem>				newTimbreList;

	protected List<SelectItem>				gouvernementList;

	protected String						currentGouvernement;

	protected String						nextGouvernement;

	// Gestion du copier / coller
	protected OrganigrammeNode				selectedNodeForCopy;

	private String							nodeActive;

	@Destroy
	public void destroy() {
		organigrammeService = null;
	}

	@Factory(value = "getOrganigrammeActions", scope = ScopeType.EVENT)
	public List<Action> getOrganigrammeActions() {
		if (organigrammeActions == null) {
			organigrammeActions = webActions.getActionsList("ORGANIGRAMME_CONTEXT_MENU");
		}
		return organigrammeActions;
	}

	/**
	 * Suppression d'un noeud dans l'organigramme
	 * 
	 * @return null
	 * @throws ClientException
	 * @author Fabio Esposito
	 */
	public String deleteNode() throws ClientException {

		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}

		if (selectedNode != null) {
			if (OrganigrammeType.getEnum(selectedNodeType).equals(OrganigrammeType.POSTE)) {
				// check all users to see if anyone is active; if so, abort
				List<STUser> allUsersInThisPost = STServiceLocator.getSTPostesService()
						.getUserFromPoste(selectedNodeId);
				for (STUser user : allUsersInThisPost) {
					if (user.isActive()) {
						facesMessages.add(StatusMessage.Severity.WARN,
								"Le poste ne peut pas être supprimé, il contient des utilisateurs actifs");
						return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
					}
				}
			}

			try {
				SolonEppServiceLocator.getOrganigrammeService().deleteFromDn(selectedNode, true);
			} catch (LocalizedClientException e) {
				String message = resourcesAccessor.getMessages().get(e.getMessage());
				facesMessages.add(StatusMessage.Severity.WARN, message);
				return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
			}
		}
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;

	}

	/**
	 * action d'édition d'un élément
	 * 
	 * @return la vue édition
	 * @throws ClientException
	 * @author Fabio Esposito
	 */
	public String editNode() throws ClientException {

		String view = null;

		if (selectedNodeType != null) {

			confirmDialog = false;

			loadSelectedNodeModel();

			if (OrganigrammeType.getEnum(selectedNodeType).equals(OrganigrammeType.DIRECTION)
					|| OrganigrammeType.getEnum(selectedNodeType).equals(OrganigrammeType.UNITE_STRUCTURELLE)) {
				view = STViewConstant.ORGANIGRAMME_EDIT_UNITE_STRUCTURELLE;
			} else if (OrganigrammeType.getEnum(selectedNodeType).equals(OrganigrammeType.POSTE)) {
				PosteNode posteNode = (PosteNode) selectedNode;
				if (posteNode.isPosteWs()) {
					view = STViewConstant.ORGANIGRAMME_EDIT_POSTE_WS;
				} else {
					view = STViewConstant.ORGANIGRAMME_EDIT_POSTE;
				}
			} else if (OrganigrammeType.getEnum(selectedNodeType).equals(OrganigrammeType.GOUVERNEMENT)) {
				view = STViewConstant.ORGANIGRAMME_EDIT_GOUVERNEMENT;
			}

			OrganigrammeNode node = selectedNode;
			if (!StringUtils.isEmpty(node.getLockUserName()) && !isCurrentUserUnlocker(node.getLockUserName())) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
				return null;
			} else {
				lockOrganigrammeNode(node);
			}

		} else {
			view = STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("organigramme.group.notfound"));
		}

		return view;
	}

	public String cancelEditNode() throws ClientException {
		unlockOrganigrammeNode(selectedNode);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * action de désactivation d'un élément
	 * 
	 * @return null, on reste sur la même vue
	 * @throws ClientException
	 * @author Fabio Esposito
	 */
	public String disableNode() throws ClientException {

		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();

		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}

		if (selectedNode != null) {
			try {
				organigrammeService.disableNodeFromDn(selectedNode.getId(), selectedNode.getType());
			} catch (LocalizedClientException e) {
				facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get(e.getMessage()));
				return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
			}
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.organigrammeManager.node.disabled"));
		}

		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return null;
	}

	/**
	 * action de réactivation d'un élément
	 * 
	 * @return null, on reste sur la même vue
	 * @throws ClientException
	 * @author Fabio Esposito
	 */
	public String enableNode() throws ClientException {

		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();

		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}

		if (selectedNode != null) {
			organigrammeService.enableNodeFromDn(selectedNode.getId(), selectedNode.getType());
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.organigrammeManager.node.enabled"));
		}

		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return null;
	}

	/**
	 * Validation du formulaire de création
	 * 
	 * @param context
	 * @param component
	 * @param value
	 * @author Fabio Esposito
	 */
	public void validateNewGroupName(FacesContext context, UIComponent component, Object value) {
		if (!(value instanceof String) || StringUtils.isEmpty(((String) value).trim())) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"organigramme.creation.missingGroupName"), null);
			// also add global message
			context.addMessage(null, message);
			throw new ValidatorException(message);
		}
	}

	/**
	 * Validation du formulaire de création
	 * 
	 * @param context
	 * @param component
	 * @param value
	 * @author Fabio Esposito
	 */
	public void validateStartDate(FacesContext context, UIComponent component, Object value) {
		if (!(value instanceof Date)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"La date ne peut être vide"), null);
			// also add global message
			throw new ValidatorException(message);
		}
		Date inputDate = (Date) value;

		if (inputDate.compareTo(new Date()) > 0) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"La date ne peut être supérieure à la date du jour"), null);
			throw new ValidatorException(message);
		}
	}

	/**
	 * Adds selection from selector as a list element
	 * <p>
	 * Must pass request parameter "organigrammeSelectionListId" holding the binding to model. Selection will be
	 * retrieved using the {@link #getSelectedValue()} method.
	 */
	public void addSelectionToList(ActionEvent event) {
		UIComponent component = event.getComponent();
		if (component == null) {
			return;
		}
		UIComponent base = ComponentUtils.getBase(component);
		UIEditableList list = ComponentUtils.getComponent(base, organigrammeSelectionListId, UIEditableList.class);

		if (list != null) {
			// add selected value to the list
			String selectedValue = getSelectedNodeId();
			Converter converter = getOrganigrammeNodeIdConverter(organigrammeSelectionMultiple,
					organigrammeSelectionType);
			if (converter != null) {
				selectedValue = converter.getAsString(null, null, selectedValue);
			}

			// check si l'élément est déjà dans la listes
			@SuppressWarnings("unchecked")
			List<Object> dataList = (List<Object>) list.getEditableModel().getWrappedData();
			if (!dataList.contains(selectedValue)) {
				list.addValue(selectedValue);
			}
		}
	}

	// /**
	// * Recupere l'id de la mailbox à partir de l'id du poste
	// * utilisé dans organigramme_select_node_widget.xhtml
	// *
	// * @param posteId id du poste
	// * @return id de la mailbox
	// */
	// public String getMailboxIdFromPosteId(String posteId) {
	//
	// if (!StringUtils.isEmpty(posteId)) {
	// MailboxPosteService mailboxPosteService = SolonEppServiceLocator.getMailboxPosteService();
	// return mailboxPosteService.getPosteMailboxId(posteId);
	// }
	// return null;
	// }

	public OrganigrammeNode getSelectedNode() {
		return selectedNode;
	}

	public String getNewGroupName() {
		return newGroupName;
	}

	public void setNewGroupName(String newGroupName) {
		this.newGroupName = newGroupName;
	}

	public String getCreationType() {
		return creationType;
	}

	public void setCreationType(String creationType) {
		this.creationType = creationType;
	}

	public String getSelectedNodeId() {
		return selectedNodeId;
	}

	public void setSelectedNodeId(String selectedNodeId) {
		organigrammeTree.setVisible(Boolean.FALSE); // remove tree from view
		this.selectedNodeId = selectedNodeId;
	}

	public OrganigrammeNode getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(OrganigrammeNode currentNode) {
		this.currentNode = currentNode;
	}

	/**
	 * Retourne le converter pour le type de node organigramme.
	 * 
	 * @param type
	 * @return
	 */
	public Converter getOrganigrammeConverter(String type) {

		if (DIR_TYPE.equals(type) || DIR_AND_UST_TYPE.equals(type)) {
			return new OrganigrammeUSIdToLabelConverter();
		} else if (GVT_TYPE.equals(type)) {
			return new OrganigrammeGvtIdToLabelConverter();
		} else if (MIN_TYPE.equals(type)) {
			return new OrganigrammeMinIdToLabelConverter();
		} else if (POSTE_TYPE.equals(type)) {
			return new OrganigrammePosteIdToLabelConverter();
		} else if (USER_TYPE.equals(type)) {
			return new OrganigrammeUserIdToLabelConverter();
		} else if (type != null && type.contains(",")) {
			return new OrganigrammeMultiIdToLabelConverter();
		} else if (INS_TYPE.equals(type)) {
			return new InstitutionIdToLabelConverter();
		} else {
			return null;
		}
	}

	/**
	 * Retourne un converter permettant de convertir l'ID du noeud de l'organigramme pour identifier le type de noeud
	 * (poste, US, ministère...) traité.
	 * 
	 * @param selectionType
	 *            Type de sélection (un seul type de noeud ou plusieurs types de noeuds)
	 * @param prefix
	 *            Préfixe de l'ID du noeud
	 * @return Convertisseur
	 */
	public Converter getOrganigrammeNodeIdConverter(String selectionType, String prefix) {
		if (!MULTI.equals(selectionType)) {
			return null;
		} else {
			if (DIR_TYPE.equals(prefix)) {
				return new OrganigrammeUSIdConverter();
			} else if (MIN_TYPE.equals(prefix)) {
				return new OrganigrammeMinIdConverter();
			} else if (POSTE_TYPE.equals(prefix)) {
				return new OrganigrammePosteIdConverter();
			} else if (UST_TYPE.equals(prefix)) {
				return new OrganigrammeUSIdConverter();
			}
			return null;
		}
	}

	public Boolean contains(String selectionType, String type) {
		if (selectionType != null && type != null) {
			return selectionType.contains(type);
		} else {
			return false;
		}
	}

	/**
	 * Retourne le label d'un node
	 * 
	 * @param selectionType
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	public String getOrganigrammeNodeLabel(String selectionType, String id) throws ClientException {

		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
		OrganigrammeNode node = null;
		if (INS_TYPE.equals(selectionType)) {
			node = organigrammeService.getInstitution(id);
		} else if (DIR_TYPE.equals(selectionType) || DIR_AND_UST_TYPE.equals(selectionType)) {
			node = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(id);
		} else if (POSTE_TYPE.equals(selectionType)) {
			node = STServiceLocator.getSTPostesService().getPoste(id);
		} else if (GVT_TYPE.equals(selectionType)) {
			node = STServiceLocator.getSTGouvernementService().getGouvernement(id);
		} else if (USER_TYPE.equals(selectionType)) {
			UserNode user = organigrammeService.getUserNode(id);
			return user.getLabel();
		}
		
		if(node == null) {
			throw new ClientException("Type de noeud non reconnu.");
		}
		return node.getLabel();

	}

	/**
	 * @return the selectedNodeType
	 */
	public String getSelectedNodeType() {
		return selectedNodeType;
	}

	/**
	 * @param selectedNodeType
	 *            the selectedNodeType to set
	 */
	public void setSelectedNodeType(String selectedNodeType) {
		this.selectedNodeType = selectedNodeType;
	}

	/**
	 * Retourne true si le node est verrouillé
	 * 
	 * @return
	 * @throws ClientException
	 */
	protected boolean isSelectedNodeLocked() throws ClientException {
		if (selectedNode != null) {
			OrganigrammeNode node = selectedNode;
			if (!StringUtils.isEmpty(node.getLockUserName()) && !isCurrentUserUnlocker(node.getLockUserName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createGouvernementView() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_CREATE_GOUVERNEMENT;
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createPosteView() throws ClientException {
		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_CREATE_POSTE;
	}

	/**
	 * 
	 * @return Vue de la création des postes webservice
	 * @throws ClientException
	 */
	public String createPosteWsView() throws ClientException {
		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_CREATE_POSTE_WS;
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createUniteStructurelleView() throws ClientException {
		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_CREATE_UNITE_STRUCTURELLE;
	}

	/**
	 * Annuler creation Gouvernement
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String cancelCreateGouvernement() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Annuler creation de poste
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String cancelCreatePoste() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Annuler creation de poste webservice
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String cancelCreatePosteWs() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Annuler creation d'une unité structurelle
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String cancelCreateUniteStructurelle() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * met a null les documents en cours de création ou d'édition
	 */
	public void resetNodeModel() {
		selectedNode = null;
		confirmDialog = false;
		nameAlreadyUsed = false;
		selectedNodeForCopy = null;
	}

	public void loadSelectedNodeModel() throws ClientException {
		loadNodeModel(getSelectedNodeId(), selectedNodeType);
	}

	private void loadNodeModel(String itemId, String type) throws ClientException {
		switch (OrganigrammeType.getEnum(type)) {
			case INSTITUTION:
				selectedNode = SolonEppServiceLocator.getOrganigrammeService().getInstitution(itemId);
				break;
			case DIRECTION:
			case UNITE_STRUCTURELLE:
				selectedNode = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(itemId);
				break;
			case POSTE:
				selectedNode = STServiceLocator.getSTPostesService().getPoste(itemId);
				break;
		default:
			throw new ClientException("Invalid node type : " + selectedNodeType);
		}
	}

	public OrganigrammeNode getSelectedNodeModel() {
		return selectedNode;
	}

	/**
	 * @return the newPoste
	 * @throws ClientException
	 */
	public OrganigrammeNode getNewPoste() throws ClientException {
		if (selectedNode == null) {
			selectedNode = STServiceLocator.getSTPostesService().getBarePosteModel();

			PosteNode posteNode = (PosteNode) selectedNode;
			// ajout du parent
			List<String> parentList = new ArrayList<String>();
			parentList.add(getSelectedNodeId());
			switch (OrganigrammeType.getEnum(selectedNodeType)) {
				case INSTITUTION:
					posteNode.setParentInstitIds(parentList);
					break;
				case DIRECTION:
				case UNITE_STRUCTURELLE:
					posteNode.setParentUnitIds(parentList);
					break;
			default:
				throw new ClientException("Invalid node type : " + selectedNodeType);
			}
		}

		return selectedNode;
	}

	/**
	 * Retourne un poste Ws
	 * 
	 * @return the newPosteWs
	 * @throws ClientException
	 */
	public OrganigrammeNode getNewPosteWs() throws ClientException {
		getNewPoste();
		PosteNode posteNode = (PosteNode) selectedNode;
		posteNode.setPosteWs(true);
		return selectedNode;
	}

	/**
	 * @return the newUniteStructurelle
	 * @throws ClientException
	 */
	public OrganigrammeNode getNewUniteStructurelle() throws ClientException {
		if (selectedNode == null) {
			final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
			selectedNode = usService.getBareUniteStructurelleModel();

			UniteStructurelleNode ustNode = (UniteStructurelleNode) selectedNode;
			// ajout du parent
			List<String> parentList = new ArrayList<String>();
			parentList.add(getSelectedNodeId());
			switch (OrganigrammeType.getEnum(selectedNodeType)) {
				case INSTITUTION:
					ustNode.setParentInstitIds(parentList);
					break;
				case DIRECTION:
				case UNITE_STRUCTURELLE:
					ustNode.setParentUnitIds(parentList);
					break;
			default:
				throw new ClientException("Invalid node type : " + selectedNodeType);
			}
		}
		return selectedNode;
	}

	/**
	 * Crée un gouvernement
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createGouvernement() throws ClientException {
		STServiceLocator.getSTGouvernementService().createGouvernement((GouvernementNode) selectedNode);
		selectedNode = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.gouvernementCreated"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createPoste() throws ClientException {

		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();

		PosteNode pstNode = (PosteNode) selectedNode;

		if (confirmDialog == false) {

			List<String> ustParentList = pstNode.getParentUnitIds();
			List<String> institutionParentList = pstNode.getParentInstitIds();

			if (ustParentList == null || ustParentList.isEmpty()) {
				// si pas de parent unite structurelle
				// vérifie les institutions parentes
				if (institutionParentList == null || institutionParentList.isEmpty()) {
					// Error
					facesMessages
							.add(StatusMessage.Severity.WARN,
									resourcesAccessor.getMessages().get(
											"warn.organigrammeManager.uniteStructurelleNeedParent"));
					return null;
				}
			}

			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNode)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return null;
			}

			if (!organigrammeService.checkUniqueLabel(selectedNode)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_CREATE_POSTE;
			}
		} else {
			confirmDialog = false;
		}
		STServiceLocator.getSTPostesService().createPoste(documentManager, (PosteNode) selectedNode);
		selectedNode = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.posteCreated"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Creation d'un poste webservice
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createPosteWs() throws ClientException {

		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();

		if (confirmDialog == false) {
			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNode)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return null;
			}

			if (!organigrammeService.checkUniqueLabel(selectedNode)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_CREATE_POSTE_WS;
			}
		} else {
			confirmDialog = false;
		}
		STServiceLocator.getSTPostesService().createPoste(documentManager, (PosteNode) selectedNode);
		selectedNode = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.posteWsCreated"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Creation d'une unité structurelle
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createUniteStructurelle() throws ClientException {

		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();

		UniteStructurelleNode ustNode = (UniteStructurelleNode) selectedNode;

		if (confirmDialog == false) {

			List<String> ustParentList = ustNode.getParentUnitIds();
			List<String> institutionParentList = ustNode.getParentInstitIds();

			if (ustParentList == null || ustParentList.isEmpty()) {
				// si pas de parent unite structurelle
				// vérifie les institutions parentes
				if (institutionParentList == null || institutionParentList.isEmpty()) {
					// Error
					facesMessages
							.add(StatusMessage.Severity.WARN,
									resourcesAccessor.getMessages().get(
											"warn.organigrammeManager.uniteStructurelleNeedParent"));
					return null;
				}
			}

			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNode)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return null;
			}

			if (!organigrammeService.checkUniqueLabel(selectedNode)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_CREATE_UNITE_STRUCTURELLE;
			}
		} else {
			confirmDialog = false;
		}
		// enregistrement de l'unite structurelle
		ustNode.setType(OrganigrammeType.UNITE_STRUCTURELLE);
		STServiceLocator.getSTUsAndDirectionService().createUniteStructurelle((UniteStructurelleNode) selectedNode);

		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.uniteStructurelleCreated"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return cancelCreateUniteStructurelle();
	}

	/**
	 * Mise à jour du poste
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	public String updatePoste() throws ClientException {

		final OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
		final STPostesService postesService = STServiceLocator.getSTPostesService();

		// vérifie qu'on a toujours le lock
		OrganigrammeNode nodeToUpdate = selectedNode;
		OrganigrammeNode node = postesService.getPoste(nodeToUpdate.getId());
		if (node == null || !isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}

		if (confirmDialog == false) {
			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNode)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return STViewConstant.ERROR_VIEW;
			}

			if (!organigrammeService.checkUniqueLabel(selectedNode)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_EDIT_POSTE;
			}
		} else {
			confirmDialog = false;
		}

		try {
			postesService.updatePoste(documentManager, (PosteNode) selectedNode);
		} finally {
			unlockOrganigrammeNode(selectedNode);
		}
		selectedNode = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.posteModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Mise à jour du poste webservice
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	public String updatePosteWs() throws ClientException {

		final STPostesService postesService = STServiceLocator.getSTPostesService();

		// vérifie qu'on a toujours le lock
		OrganigrammeNode nodeToUpdate = selectedNode;
		OrganigrammeNode node = postesService.getPoste(nodeToUpdate.getId());
		if (node == null || !isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}

		try {
			postesService.updatePoste(documentManager, (PosteNode) selectedNode);
		} finally {
			unlockOrganigrammeNode(selectedNode);
		}
		selectedNode = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.posteWsModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Mise à jour de l'unité structurelle
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	public String updateUniteStructurelle() throws ClientException {

		final OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
		final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();

		// vérifie qu'on a toujours le lock
		OrganigrammeNode nodeToUpdate = selectedNode;
		OrganigrammeNode node = usService.getUniteStructurelleNode(nodeToUpdate.getId());
		if (node == null || !isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}
		UniteStructurelleNode usNode = (UniteStructurelleNode) selectedNode;

		if (organigrammeService.checkParentListContainsChildren(usNode, usNode, Boolean.TRUE)) {
			facesMessages.add(
					StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get(
							"warn.organigrammeManager.uniteStructurelle.contains.children.in.parentList"));
			return null;
		}

		if (confirmDialog == false) {
			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNode)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return null;
			}

			if (!organigrammeService.checkUniqueLabel(selectedNode)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_EDIT_UNITE_STRUCTURELLE;
			}
		} else {
			confirmDialog = false;
		}

		try {
			usService.updateUniteStructurelle((UniteStructurelleNode) selectedNode);
		} finally {
			unlockOrganigrammeNode(selectedNode);
		}
		selectedNode = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.uniteStructurelleModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Verrouille un élément d'organigramme
	 * 
	 * @param node
	 * @throws ClientException
	 */
	public boolean lockOrganigrammeNode(OrganigrammeNode node) throws ClientException {
		final OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();

		LOGGER.debug(documentManager, STLogEnumImpl.LOCK_ORGANIGRAMME_TEC, "node : " + node.getLabel());

		Boolean result = false;

		if (organigrammeService.lockOrganigrammeNode(documentManager, node)) {
			result = true;
			// Affiche un message d'information
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.organigrammeManager.node.lock"));
		} else {
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
		}

		// throw new LocalizedClientException("warn.organigramme.node.locked");
		return result;
	}

	/**
	 * Verrouille un élément d'organigramme
	 * 
	 * @param node
	 * @throws ClientException
	 */
	public boolean unlockOrganigrammeNode(OrganigrammeNode node) throws ClientException {
		final OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();

		LOGGER.debug(documentManager, STLogEnumImpl.LOCK_ORGANIGRAMME_TEC, "node : " + node.getLabel());

		Boolean result = false;

		if (organigrammeService.unlockOrganigrammeNode(node)) {
			result = true;
			// Affiche un message d'information
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.organigrammeManager.node.unlock"));
		} else {
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.unlock.failed"));
		}

		// throw new LocalizedClientException("warn.organigramme.node.locked");
		return result;
	}

	public String getImage(String nodeId, String selectionType, String selectionMultiple) {

		if (nodeId != null && !nodeId.isEmpty()) {
			if (!MULTI.equals(selectionMultiple)) {
				if (POSTE_TYPE.equals(selectionType)) {
					return "poste.png";
				} else if (USER_TYPE.equals(selectionType) || USER_MAIL_TYPE.equals(selectionType)) {
					return "user_16.png";
				} else {
					return "unite_structurelle.png";
				}
			} else {
				if (nodeId.startsWith(OrganigrammeNodeIdConverter.PREFIX_MIN)) {
					return "unite_structurelle.png";
				} else if (nodeId.startsWith(OrganigrammeNodeIdConverter.PREFIX_POSTE)) {
					return "poste.png";
				} else if (nodeId.startsWith(OrganigrammeNodeIdConverter.PREFIX_US)) {
					return "unite_structurelle.png";
				} else {
					return "user_16.png";
				}
			}
		}
		return "unite_structurelle.png";
	}

	/**
	 * Copie un noeud de l'organigramme
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String copyNode() throws ClientException {
		loadSelectedNodeModel();
		selectedNodeForCopy = selectedNode;
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Colle un noeud de l'organigramme sans mettre les utilisateurs dans les postes
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String pasteNodeWithoutUser() throws ClientException {
		loadSelectedNodeModel();

		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
		try {
			organigrammeService.copyNodeWithoutUser(documentManager, selectedNodeForCopy, selectedNode);
		} catch (LocalizedClientException e) {
			String message = resourcesAccessor.getMessages().get(e.getMessage());
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}

		resetNodeModel();

		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Colle un noeud de l'organigramme avec les utilisateurs dans les postes
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String pasteNodeWithUsers() throws ClientException {
		loadSelectedNodeModel();

		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
		try {
			organigrammeService.copyNodeWithUsers(documentManager, selectedNodeForCopy, selectedNode);
		} catch (LocalizedClientException e) {
			String message = resourcesAccessor.getMessages().get(e.getMessage());
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}
		resetNodeModel();

		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * @return the nameAlreadyUsed
	 */
	public boolean isNameAlreadyUsed() {
		return nameAlreadyUsed;
	}

	/**
	 * @param nameAlreadyUsed
	 *            the nameAlreadyUsed to set
	 */
	public void setNameAlreadyUsed(boolean nameAlreadyUsed) {
		this.nameAlreadyUsed = nameAlreadyUsed;
	}

	/**
	 * @return the confirmDialog
	 */
	public boolean isConfirmDialog() {
		return confirmDialog;
	}

	/**
	 * @param confirmDialog
	 *            the confirmDialog to set
	 */
	public void setConfirmDialog(boolean confirmDialog) {
		this.confirmDialog = confirmDialog;
	}

	/**
	 * true si un noeud est chargé pour être copier
	 * 
	 * @return
	 */
	public boolean isNodeSelectedForCopy() {
		if (selectedNodeForCopy != null) {
			return true;
		}
		return false;
	}

	public boolean isCurrentUserUnlocker(String locker) {
		return locker != null && !locker.isEmpty() && locker.equals(eppPrincipal.getName())
				|| eppPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_ADMIN_UNLOCKER);
	}

	public String unlockNode() throws ClientException {
		loadSelectedNodeModel();
		unlockOrganigrammeNode(getSelectedNodeModel());
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return ORGANIGRAMME_VIEW;
	}

	public String enableOrDisableNode() throws ClientException {
		if (!isNodeActive()) {
			return enableNode();
		} else {
			return disableNode();
		}
	}

	private boolean isNodeActive() {
		return Boolean.valueOf(getNodeActive());
	}

	public String getNodeActive() {
		return nodeActive;
	}

	public void setNodeActive(String nodeActive) {
		this.nodeActive = nodeActive;
	}

	/**
	 * Teste si l'utilisateur peut modifier l'organigramme
	 * 
	 * @param institutionId
	 * @return
	 * @throws ClientException
	 */
	public boolean allowUpdateOrganigramme(String institutionId) throws ClientException {

		if (eppPrincipal.isMemberOf("OrganigrammeUpdater")
				&& eppPrincipal.getInstitutionIdSet().contains(institutionId)) {
			return true;
		}
		return false;
	}

	/**
	 * Test si l'utilisateur peut ajouter un poste
	 * 
	 * @param posteId
	 * @return
	 * @throws ClientException
	 */
	public boolean allowAddPoste(String posteId) throws ClientException {
		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
		String currentUserInstitutionId = eppPrincipal.getInstitutionId();
		if (currentUserInstitutionId == null) {
			return false;
		}
		List<InstitutionNode> institutionNodes = organigrammeService.getInstitutionParentFromPoste(posteId);

		for (InstitutionNode institutionNode : institutionNodes) {
			if (institutionNode.getId().equals(currentUserInstitutionId)) {
				return true;
			}
		}

		return false;
	}
	
	public boolean isInstitutionSelectable(String institutionId, String restriction) {
		if (StringUtil.isBlank(restriction)) {
			return eppPrincipal.getInstitutionIdSet().contains(institutionId);
		} else {
			EvenementTypeDescriptor currentEvtTypeDescriptor = evenementTypeActions.getCurrentEvtTypeDescriptor();
			DistributionElementDescriptor descriptor = null;
			if ("emetteur".equals(restriction)) {
				descriptor = currentEvtTypeDescriptor.getDistribution().getEmetteur();
				Set<String> userInstitutions = eppPrincipal.getInstitutionIdSet();
				return descriptor.getInstitution().containsKey(institutionId) && (userInstitutions.contains(institutionId) || InstitutionsEnum.isInstitutionAlwaysAccessible(institutionId));
			} else if ("destinataire".equals(restriction)) {
				descriptor = currentEvtTypeDescriptor.getDistribution().getDestinataire();
				return descriptor.getInstitution().containsKey(institutionId);
			}
		}
		return false;
	}

}
