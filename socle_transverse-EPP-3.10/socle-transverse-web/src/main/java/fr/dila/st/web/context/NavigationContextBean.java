package fr.dila.st.web.context;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.helpers.EventManager;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Surcharge du NavigationContextBean de Nuxeo.
 */
@Name("navigationContext")
@Scope(CONVERSATION)
@Install(precedence = FRAMEWORK + 1)
public class NavigationContextBean extends org.nuxeo.ecm.webapp.context.NavigationContextBean {
	/**
	 * Serial version UID.
	 */
	private static final long		serialVersionUID	= 1356073411805191773L;

	@In(create = true, required = false)
	protected transient WebActions	webActions;

	private String					currentView			= null;

	@Override
	public void setCurrentDocument(DocumentModel documentModel) throws ClientException {
		if (!checkIfUpdateNeeded(currentDocument, documentModel)) {
			if (checkIfChangeableResetNeeded()) {
				setChangeableDocument(null);
			}
			return;
		}

		invalidateChildrenProvider();

		// --- Lève un événement de changement de document
		raiseEvent(documentModel);
		// --- FIN AJOUT a la methode surchargee

		currentSuperSpace = null;
		currentDocument = documentModel;
		if (currentDocument == null) {
			currentDocumentParents = null;
		} else {
			DocumentRef ref = currentDocument.getRef();
			if (ref == null) {
				throw new ClientException("DocumentRef is null for currentDocument: " + currentDocument.getName());
			}
			currentDocumentParents = documentManager.getParentDocuments(ref);
		}
		// update all depending variables
		updateContextVariables();
		resetCurrentPath();
		Contexts.getEventContext().remove("currentDocument");

		EventManager.raiseEventsOnDocumentSelected(currentDocument);
	}

	protected void raiseEvent(DocumentModel documentModel) throws ClientException {
		// --- Lève un événement de changement de document
		EventProducer eventProducer = STServiceLocator.getEventProducer();
		Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
		eventProperties.put(STEventConstant.OLD_DOCUMENT_EVENT_PARAM, currentDocument);
		eventProperties.put(STEventConstant.NEW_DOCUMENT_EVENT_PARAM, documentModel);
		InlineEventContext inlineEventContext = new InlineEventContext(documentManager, documentManager.getPrincipal(),
				eventProperties);
		eventProducer.fireEvent(inlineEventContext.newEvent(STEventConstant.CURRENT_DOCUMENT_CHANGED_EVENT));
		// Lève l'événement pour les beans ayant @Observer
		Events.instance().raiseEvent(STEventConstant.CURRENT_DOCUMENT_CHANGED_EVENT);
		// --- FIN AJOUT a la methode surchargee
	}

	/**
	 * Navigue vers le document et affiche l'onglet désigné (ne modifie pas la vue)
	 * 
	 * @param document
	 *            Document
	 * @param currentTabActionId
	 *            Tab onglet feuille de route
	 */
	public void setCurrentTabAndNavigate(DocumentModel document, String currentTabActionId) throws ClientException {
		// Navigue vers le document (ignore la vue définie dans son type ECM)
		navigateToDocument(document);

		// force creation of new actions if needed
		webActions.getTabsList();

		// set current tab
		webActions.setCurrentTabId(currentTabActionId);
	}

	/**
	 * Vide le document courant
	 */
	public void resetCurrentDocument() throws ClientException {
		setCurrentDocument(null);
	}

	@Override
	public String getCurrentDocumentUrl() {
		if (null == currentDocument) {
			return null;
		}
		return super.getCurrentDocumentUrl();
	}

	@Override
	public String getCurrentDocumentFullUrl() {
		if (null == currentDocument) {
			return null;
		}
		return super.getCurrentDocumentFullUrl();
	}

	public void setCurrentView(String currentView) {
		this.currentView = currentView;
	}

	public String getCurrentView() {
		return currentView;
	}
}
