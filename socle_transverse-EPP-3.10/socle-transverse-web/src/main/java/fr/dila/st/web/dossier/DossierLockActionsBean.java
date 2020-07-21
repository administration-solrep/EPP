package fr.dila.st.web.dossier;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.edit.lock.LockActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.web.lock.STLockActionsBean;

/**
 * Bean Seam permettant de gérer les verrous sur les dossiers / instances de feuilles de route.
 * 
 * @author jtremeaux
 */
@Name("dossierLockActions")
@Scope(ScopeType.EVENT)
@Install(precedence = FRAMEWORK)
public class DossierLockActionsBean implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long				serialVersionUID	= -6359405896496460937L;

	@In(create = true, required = false)
	protected transient CoreSession			documentManager;

	@In(required = true, create = true)
	protected NuxeoPrincipal				currentUser;

	@In(create = true, required = false)
	protected transient NavigationContext	navigationContext;

	@In(create = true, required = false)
	protected WebActions					webActions;

	@In(create = true, required = false)
	protected transient FacesMessages		facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor	resourcesAccessor;

	@In(create = true)
	protected transient LockActions			lockActions;

	@In(create = true, required = false)
	protected transient STLockActionsBean	stLockActions;

	protected Boolean						canLockCurrentDossier;

	protected Boolean						canUnlockCurrentDossier;

	/**
	 * Verrouille le dossier et la feuille de route associée.
	 * 
	 * @param dossierDoc
	 *            Dossier
	 * @throws ClientException
	 */
	public void lockDossier(DocumentModel dossierDoc) throws ClientException {
		List<DocumentModel> documents = new ArrayList<DocumentModel>();
		STDossier dossier = dossierDoc.getAdapter(STDossier.class);

		documents.add(dossierDoc);
		String lastRouteId = dossier.getLastDocumentRoute();
		if (lastRouteId != null) {
			documents.add(documentManager.getDocument(new IdRef(lastRouteId)));
		}

		stLockActions.lockDocuments(documents, dossierDoc.getType());
	}

	/**
	 * Verrouille le dossier chargé et la feuille de route associée.
	 * 
	 * @throws ClientException
	 */
	public void lockCurrentDossier() throws ClientException {
		lockDossier(navigationContext.getCurrentDocument());

		resetEventContext();
	}

	/**
	 * Déverrouille le dossier et la feuille de route associée.
	 * 
	 * @param dossierDoc
	 *            Dossier
	 * @throws ClientException
	 */
	public void unlockDossier(DocumentModel dossierDoc) throws ClientException {
		List<DocumentModel> documents = new ArrayList<DocumentModel>();
		if (dossierDoc != null) {
			STDossier dossier = dossierDoc.getAdapter(STDossier.class);

			documents.add(dossierDoc);
			String lastRouteId = dossier.getLastDocumentRoute();
			if (lastRouteId != null) {
				documents.add(documentManager.getDocument(new IdRef(lastRouteId)));
			}

			stLockActions.unlockDocumentsUnrestricted(documents, dossierDoc.getType());
		}
	}

	/**
	 * Déverrouille le dossier en cours et la feuille de route associée.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public void unlockCurrentDossier() throws ClientException {
		unlockDossier(navigationContext.getCurrentDocument());

		resetEventContext();
	}

	/**
	 * Détermine si l'utilisateur peut verrouiller le dossier.
	 * 
	 * @param dossier
	 *            Dossier
	 * @return Condition
	 */
	public Boolean getCanLockDossier(DocumentModel dossier) {
		return lockActions.getCanLockDoc(dossier);
	}

	/**
	 * Détermine si l'utilisateur peut verrouiller le dossier chargé.
	 * 
	 * @return Condition
	 */
	public Boolean getCanLockCurrentDossier() {
		if (canLockCurrentDossier == null) {
			canLockCurrentDossier = getCanLockDossier(navigationContext.getCurrentDocument());
		}
		return canLockCurrentDossier;
	}

	/**
	 * Détermine si l'utilisateur peut déverrouiller le dossier.
	 * 
	 * @param dossier
	 *            Dossier
	 * @return Condition
	 */
	public Boolean getCanUnlockDossier(DocumentModel dossier) {
		return lockActions.getCanUnlockDoc(dossier);
	}

	/**
	 * Détermine si l'utilisateur peut déverrouiller le dossier chargé.
	 * 
	 * @return Condition
	 */
	public Boolean getCanUnlockCurrentDossier() {
		if (canUnlockCurrentDossier == null) {
			canUnlockCurrentDossier = getCanUnlockDossier(navigationContext.getCurrentDocument());
		}
		return canUnlockCurrentDossier;
	}

	protected void resetEventContext() {
		canLockCurrentDossier = null;
		canUnlockCurrentDossier = null;

		// TODO à utiliser...
		Context evtCtx = Contexts.getEventContext();
		if (evtCtx != null) {
			evtCtx.remove("currentDocumentCanBeLocked");
			evtCtx.remove("currentDocumentLockDetails");
			evtCtx.remove("currentDocumentCanBeUnlocked");
		}
	}

}
