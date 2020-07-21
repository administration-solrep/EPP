package fr.dila.st.web.alert;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.contentbrowser.DocumentActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * 
 * Les actions relative aux alertes qui sont communes aux projets EPG et Reponses
 * 
 * @author admin
 * 
 */
@Name("alertActions")
@Scope(ScopeType.CONVERSATION)
public class STAlertActionsBean implements Serializable {

	public static final String							ETAT_ALERT_IS_SUSPENDED		= "etat_alert_isSuspended";

	public static final String							ETAT_ALERT_IS_ACTIVATED		= "etat_alert_isActivated";

	/**
     * 
     */
	private static final long							serialVersionUID			= 1L;

	protected static final String						ACTION_ALERT_ACTIVATED		= "alert_activated";

	protected static final String						ACTION_ALERT_DESACTIVATED	= "alert_desactivated";

	@In(create = true, required = true)
	protected DocumentActions							documentActions;

	@In(create = true, required = true)
	protected transient NavigationContextBean			navigationContext;

	@In(create = true, required = false)
	protected transient FacesMessages					facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor				resourcesAccessor;

	@In(create = true, required = false)
	protected transient CoreSession						documentManager;

	@In(create = true, required = true)
	protected ContentViewActions						contentViewActions;

	@In(create = true, required = true)
	protected transient ActionManager					actionManager;

	@In(create = true, required = false)
	protected transient WebActions						webActions;


	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger						LOG							= STLogFactory
																							.getLog(STAlertActionsBean.class);

	/**
	 * Met l'alerte en pause
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String suspend() throws ClientException {
		Alert alert = getCurrentAlert();
		alert.setIsActivated(false);
		documentManager.saveDocument(alert.getDocument());
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("alert.messages.info.alertSuspended"));
		return ACTION_ALERT_DESACTIVATED;
	}

	/**
	 * renvoie vrai si l'alerte est suspendue
	 * 
	 * @return
	 */
	public Boolean isSuspended() {
		Alert alert = getCurrentAlert();
		if (alert == null || alert.isActivated() == null) {
			return true;
		}
		return !alert.isActivated();
	}

	/**
	 * renvoie vrai si l'alerte est activée
	 * 
	 * @return
	 */
	public Boolean isActivated() {
		Alert alert = getCurrentAlert();
		if (alert == null || alert.isActivated() == null) {
			return false;
		}
		return alert.isActivated();
	}

	/**
	 * Rend l'alerte active.
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String activate() throws ClientException {
		Alert alert = getCurrentAlert();
		alert.setIsActivated(true);
		documentManager.saveDocument(alert.getDocument());
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("alert.messages.info.alertActivated"));
		return ACTION_ALERT_ACTIVATED;
	}

	/**
	 * Retourne la requête courante.
	 * 
	 * @return
	 */
	public Alert getCurrentAlert() {
		DocumentModel alertDoc = navigationContext.getCurrentDocument();
		Alert alert = alertDoc.getAdapter(Alert.class);
		return alert;
	}

	/**
	 * Supprime le document alert
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String delete(DocumentModel doc) {
		try {
			LOG.info(documentManager, STLogEnumImpl.DEL_ALERT_FONC, doc);
			documentManager.removeDocument(doc.getRef());
			documentManager.save();
		} catch (Exception e) {
			LOG.error(documentManager, STLogEnumImpl.FAIL_DEL_ALERT_FONC, doc, e);
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("alert.error.alertDeletion"));

		}
		return null;
	}

}
