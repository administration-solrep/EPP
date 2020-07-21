package fr.dila.st.web.administration.parametre;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * 
 * Bean pour les paramêtres.
 * 
 * @author jgomez
 * 
 */
@Name("parametreActions")
@Scope(CONVERSATION)
public class ParametreActionsBean implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long				serialVersionUID	= -1937986647753748419L;

	@In(create = true, required = false)
	protected transient CoreSession			documentManager;

	@In(create = true)
	protected transient NavigationContext	navigationContext;

	@In(create = true, required = false)
	protected FacesMessages					facesMessages;

	@In(create = true)
	protected ResourcesAccessor				resourcesAccessor;

	@In(create = true)
	protected EventManager					eventManager;

	/**
	 * FEV521 : Libellé affiché dans l'en-tête des pages internes de l'application.
	 */
	private String							identificationPlateformeLibelle;
	/**
	 * FEV521 : Couleur du libellé affiché dans l'en-tête des pages internes de l'application.
	 */
	private String							identificationPlateformeCouleur;

	public ParametreActionsBean() {
		// Default constructor
	}

	/**
	 * Sauvegarde les changements d'un paramètre.
	 * 
	 */
	// Modification de DocumentActions.updateDocument pour changer le message.
	public String updateDocument() throws ClientException {
		try {
			DocumentModel changeableDocument = navigationContext.getChangeableDocument();
			changeableDocument = documentManager.saveDocument(changeableDocument);
			documentManager.save();
			// some changes (versioning) happened server-side, fetch new one
			navigationContext.invalidateCurrentDocument();
			facesMessages
					.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("st.parametre.modified"));
			EventManager.raiseEventsOnDocumentChange(changeableDocument);
			return navigationContext.navigateToDocument(changeableDocument, "after-edit");
		} catch (Exception exc) {
			throw new ClientException(exc);
		}
	}

	/**
	 * Retourne la valeur d'un paramètre s'il existe
	 * 
	 * @return
	 */
	public String getParameterValue(String parameter) {
		final STParametreService paramService = STServiceLocator.getSTParametreService();
		try {
			return paramService.getParametreValue(documentManager, parameter);
		} catch (ClientException ce) {
			facesMessages
					.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get("st.parametre.notFound"));
		}
		return null;
	}

	/**
	 * @return the identificationPlateformeLibelle
	 */
	public String getIdentificationPlateformeLibelle() {
		if (identificationPlateformeLibelle == null) {
			identificationPlateformeLibelle = STServiceLocator.getConfigService().getValue(
					STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_LIBELLE);
		}
		return identificationPlateformeLibelle;
	}

	/**
	 * @return the identificationPlateformeCouleur
	 */
	public String getIdentificationPlateformeCouleur() {
		if (identificationPlateformeCouleur == null) {
			identificationPlateformeCouleur = STServiceLocator.getConfigService().getValue(
					STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_COULEUR);
		}
		return identificationPlateformeCouleur;
	}
}
