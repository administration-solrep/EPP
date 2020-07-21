package fr.dila.st.web.action;

import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.DISABLE_REDIRECT_REQUEST_KEY;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGOUT_PAGE;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.common.utils.URIUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean Web permettant de gérer les actions communes aux applications utilisant le socle transverse. Gère entre autre la
 * navigation en trois panneaux.
 * 
 * @author jtremeaux
 */
@Name("navigationWebActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.APPLICATION)
@SerializedConcurrentAccess
public class NavigationWebActionsBean implements Serializable {

	private static final long					serialVersionUID	= 1959221536502251848L;
	private static final STLogger				LOGGER				= STLogFactory
																			.getLog(NavigationWebActionsBean.class);

	/**
	 * Action du menu de gauche sélectionné (un menu par espace de l'application).
	 */
	@Out(required = false)
	protected Action							currentLeftMenuAction;

	/**
	 * Action de l'élément du menu de gauche sélectionné.
	 */
	@Out(required = false)
	protected Action							currentLeftMenuItemAction;

	/**
	 * Action du menu principal sélectionné.
	 */
	@Out(required = false)
	protected Action							currentMainMenuAction;

	@In(create = true, required = false)
	protected transient NavigationContextBean	navigationContext;

	@In(create = true, required = false)
	protected transient CoreSession				documentManager;

	/**
	 * Comportements du panneau gauche
	 */
	protected Boolean							leftPanelIsOpened	= true;

	/**
	 * Comportements du panneau supérieur
	 */
	protected Boolean							upperPanelIsOpened	= true;

	/**
	 * Id/Etat des divs à cacher ou afficher
	 */
	protected Map<String, Boolean>				panelStates			= new HashMap<String, Boolean>();

	/**
	 * Retourne le menu principal sélectionné.
	 * 
	 * @return Menu principal sélectionné
	 */
	public Action getCurrentMainMenuAction() {
		return currentMainMenuAction;
	}

	/**
	 * Renseigne le menu principal sélectionné.
	 * 
	 * @param currentMainMenuAction
	 *            Menu principal sélectionné
	 */
	public void setCurrentMainMenuAction(Action currentMainMenuAction) {
		this.currentMainMenuAction = currentMainMenuAction;
	}

	/**
	 * Retourne le menu de gauche sélectionné.
	 * 
	 * @return Menu de gauche sélectionné
	 */
	public Action getCurrentLeftMenuAction() {
		return currentLeftMenuAction;
	}

	/**
	 * Renseigne le menu de gauche sélectionné.
	 * 
	 * @param currentLeftMenuAction
	 *            Menu de gauche sélectionné
	 */
	public void setCurrentLeftMenuAction(Action currentLeftMenuAction) {
		this.currentLeftMenuAction = currentLeftMenuAction;
	}

	/**
	 * Retourne l'élément du menu de gauche sélectionné.
	 * 
	 * @return Menu de gauche sélectionné
	 */
	public Action getCurrentLeftMenuItemAction() {
		return currentLeftMenuItemAction;
	}

	/**
	 * Renseigne l'élément du menu de gauche sélectionné.
	 * 
	 * @param currentLeftMenuAction
	 *            Menu de gauche sélectionné
	 */
	public void setCurrentLeftMenuItemAction(Action currentLeftMenuItemAction) {
		this.currentLeftMenuItemAction = currentLeftMenuItemAction;
	}

	/**
	 * Retourne l'état du panneau gauche
	 * 
	 * @return état ouvert si true
	 */
	public Boolean getLeftPanelIsOpened() {
		return leftPanelIsOpened;
	}

	/**
	 * Set l'état du panneau gauche, ouvert si true
	 * 
	 * @param leftPanelIsOpened
	 */
	public void setLeftPanelIsOpened(Boolean leftPanelIsOpened) {
		this.leftPanelIsOpened = leftPanelIsOpened;
	}

	/**
	 * Retourne l'état du panneau supérieur
	 * 
	 * @return état ouvert si true
	 */
	public Boolean getUpperPanelIsOpened() {
		return upperPanelIsOpened;
	}

	/**
	 * Set l'état du panneau supérieur, ouvert si true
	 * 
	 * @param upperPanelIsOpened
	 */
	public void setUpperPanelIsOpened(Boolean upperPanelIsOpened) {
		this.upperPanelIsOpened = upperPanelIsOpened;
	}

	/**
	 * Retourne l'identifiant de version.
	 * 
	 * @return Le tag de l'application
	 */
	public String getProductTag() {
		return Framework.getProperty(STConfigConstants.PRODUCT_TAG);
	}

	/**
	 * Retourne true si l'application est executé avec un template de dev (dev ou qa)
	 * 
	 * @return
	 */
	public boolean isDevTemplate() {
		List<String> templates = Arrays.asList(StringUtils.split(Framework.getProperty("nuxeo.templates"), ',', true));
		return templates.contains("dev") || templates.contains("qa");
	}

	/**
	 * Permet de supprimer un document unique (bizarrement, les WebBean présents dans Nuxeo permettent de supprimer
	 * uniquement une liste de documents).
	 * 
	 * @param doc
	 *            Document à supprimer
	 * @return Vue
	 * @throws ClientException
	 */
	public String deleteDocument(DocumentModel doc) throws ClientException {
		LOGGER.info(documentManager, STLogEnumImpl.DEL_DOC_FONC, doc);
		documentManager.removeDocument(doc.getRef());
		documentManager.save();
		return null;
	}

	public String closeDocument() throws ClientException {
		navigationContext.resetCurrentDocument();
		return null;
	}

	/**
	 * Logout avec le message "Utilisateur non autorisé"
	 * 
	 * @return
	 * @throws Exception
	 */
	public String logout() throws Exception {
		Map<String, String> parameters = new HashMap<String, String>();
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext eContext = context.getExternalContext();
		Object req = eContext.getRequest();
		Object resp = eContext.getResponse();
		HttpServletRequest request = null;
		if (req instanceof HttpServletRequest) {
			request = (HttpServletRequest) req;
		}
		HttpServletResponse response = null;
		if (resp instanceof HttpServletResponse) {
			response = (HttpServletResponse) resp;
		}

		parameters.put("securityError", "true");

		if (response != null && request != null && !context.getResponseComplete()) {
			String baseURL = BaseURL.getBaseURL(request) + LOGOUT_PAGE;
			request.setAttribute(DISABLE_REDIRECT_REQUEST_KEY, true);
			baseURL = URIUtils.addParametersToURIQuery(baseURL, parameters);
			response.sendRedirect(baseURL);
			context.responseComplete();
		}
		return null;
	}

	/**
	 * Renseigne sur l'ouverture de la div
	 * 
	 * @param idDiv
	 * @return vrai si le panneau est ouvert, faux s'il est fermé
	 */
	public Boolean getPanelState(String idDiv) {
		if (!panelStates.containsKey(idDiv)) {
			panelStates.put(idDiv, false);
		}
		return panelStates.get(idDiv);
	}

	/**
	 * Définit l'ouverture de la div
	 * 
	 * @param idDiv
	 *            l'id de la div à cacher / afficher
	 * @param panelStates
	 *            vrai si le panneau est ouvert, faux s'il est fermé
	 */
	public void setPanelState(String idDiv, Boolean panelStates) {
		this.panelStates.put(idDiv, panelStates);
	}
}
