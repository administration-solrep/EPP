package fr.dila.st.web.administration;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

@Name("etatApplicationActions")
@Install(precedence = Install.FRAMEWORK)
public class EtatApplicationActionsBean {

	@In(create = true, required = false)
	protected transient CoreSession					documentManager;

	@In(create = true, required = false)
	protected transient NavigationContextBean		navigationContext;

	@In(create = true)
	protected transient NavigationWebActionsBean	navigationWebActions;

	@In(create = true, required = false)
	protected transient ActionManager				actionManager;

	@In(create = true, required = false)
	protected FacesMessages							facesMessages;

	@In(create = true)
	protected ResourcesAccessor						resourcesAccessor;

	/**
	 * Retreindre l'accès à l'application
	 * 
	 * @throws ClientException
	 */
	public void restrictAccess() throws ClientException {
		EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();
		etatApplicationService.restrictAccess(documentManager, "Accès restreint aux administrateurs");
	}

	public String getBanniereInfoTitle() {
		return resourcesAccessor.getMessages().get("label.banniere.info.header");
	}

	public void restoreAccess() throws ClientException {
		EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();
		etatApplicationService.restoreAccess(documentManager);
	}

	public boolean isAccessRestricted() throws ClientException {
		EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();
		EtatApplication etatApplication = etatApplicationService.getEtatApplicationDocument(documentManager);
		return etatApplication.getRestrictionAcces();
	}

	public String editEtatApplication() throws ClientException {
		EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();
		EtatApplication etatApplication = etatApplicationService.getEtatApplicationDocument(documentManager);
		navigationContext.setCurrentDocument(etatApplication.getDocument());
		navigationContext.setChangeableDocument(etatApplication.getDocument());
		return "edit_document";
	}

	// Modification de DocumentActions.updateDocument pour changer le message.
	public String updateDocument() throws ClientException {
		try {
			DocumentModel changeableDocument = navigationContext.getChangeableDocument();
			changeableDocument = documentManager.saveDocument(changeableDocument);
			documentManager.save();
			// some changes (versioning) happened server-side, fetch new one
			navigationContext.invalidateCurrentDocument();
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("st.etat.application.modified"));
			EventManager.raiseEventsOnDocumentChange(changeableDocument);
			return STViewConstant.ETAT_APPLICATION_VIEW;
		} catch (Exception exc) {
			throw new ClientException(exc);
		}
	}

	/**
	 * Controle l'accès à la vue correspondante
	 * 
	 */
	public boolean isAccessAuthorized() {
		STPrincipal ssPrincipal = (STPrincipal) documentManager.getPrincipal();
		return (ssPrincipal.isAdministrator() || ssPrincipal.isMemberOf("AccessUnrestrictedUpdater"));
	}

}
