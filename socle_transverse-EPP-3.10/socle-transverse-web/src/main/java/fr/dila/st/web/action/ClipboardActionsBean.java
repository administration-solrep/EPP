package fr.dila.st.web.action;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

import fr.dila.ecm.platform.routing.web.DocumentRoutingActionsBean;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;

/**
 * Ce WebBean surcharge et étend celui de Nuxeo.
 * 
 * @author jtremeaux
 */
@Name("clipboardActions")
@Scope(SESSION)
@Install(precedence = Install.APPLICATION + 1)
public class ClipboardActionsBean extends org.nuxeo.ecm.webapp.clipboard.ClipboardActionsBean implements Serializable {
	/**
	 * Serial version UID
	 */
	private static final long				serialVersionUID	= -9042629921683218056L;

	/**
	 * Logger.
	 */
	private static final Log				LOG					= LogFactory.getLog(ClipboardActionsBean.class);

	@In(create = true, required = false)
	protected transient ContentViewActions	contentViewActions;
	
	@In(create = true, required = false)
	protected transient DocumentRoutingActionsBean routingActions;

	@Override
	public void putSelectionInClipboard() {
		// canEditSelectedDocs = null;
		if (!documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION)) {
			List<DocumentModel> docsList = documentsListsManager
					.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
			Object[] params = { docsList.size() };
			facesMessages.add(StatusMessage.Severity.INFO,
					"#0 " + resourcesAccessor.getMessages().get("n_copied_docs"), params);
			
			if (LOG.isDebugEnabled()) {
				StringBuilder ids = new StringBuilder();
				StringBuilder paths = new StringBuilder();

				boolean first = true;
				for (DocumentModel doc : docsList) {
					if (!first) {
						ids.append(", ");
						paths.append(", ");
					}
					ids.append(doc.getId());
					paths.append(doc.getPathAsString());
					first = false;
				}

				LOG.debug("Adding to clipboard documents " + ids.toString() + "(" + paths.toString() + ")");
			}

			documentsListsManager.addToWorkingList(DocumentsListsManager.CLIPBOARD, docsList);

		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No selectable Documents in context to process copy on...");
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("add to worklist processed...");
		}
	}

	/**
	 * Ajoute les documents sélectionnés de la liste CURRENT_DOCUMENT_SELECTION à la liste de travail.
	 */
	@Override
	public void putSelectionInDefaultWorkList() {
		// Vérifie si les éléments ajoutés sont du même type que les éléments de la liste
		if (!checkIfDefaultWorkingListIsSameType()) {
			displayNotSameTypeError();
			return;
		}

		// Ajoute les éléments à la liste
		super.putSelectionInDefaultWorkList();
	}

	/**
	 * Ajoute les éléments sélectionnés de la content view active dans la liste de travail.
	 */
	public void putCurrentContentViewSelectionInDefaultWorkList() {
		if (!checkIfDefaultWorkingListIsSameType()) {
			displayNotSameTypeError();
			return;
		}

		// Ajoute les éléments à la liste
		String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();
		List<DocumentModel> docsList = documentsListsManager.getWorkingList(selectionListName);
		if (docsList == null) {
			return;
		}
		documentsListsManager.addToWorkingList(getCurrentSelectedListName(), docsList);

		// Affiche un message de confirmation
		String message = MessageFormat.format(
				resourcesAccessor.getMessages().get("st.clipboard.addToWorkList.success"), docsList.size());
		facesMessages.add(StatusMessage.Severity.INFO, message);
	}

	/**
	 * Vérifie si les éléments ajoutés sont correspondent au type des éléments de la liste de sélection actuelle.
	 * 
	 * Pré-requis : - les éléments ajoutés sont tous de même type ; - les éléments de la liste de sélection sont tous de
	 * même type.
	 * 
	 * @param selectionListId
	 *            Identifiant technique de la liste des éléments ajoutés
	 * @return Éléments du même type
	 */
	public boolean checkifDefaultWorkingListIsSameType(String selectionListId) {
		// Si aucun document à ajouter, ce test est sans objet
		if (documentsListsManager.isWorkingListEmpty()) {
			return true;
		}

		// Si aucun élément n'est présent dans la liste de séléction actuelle, c'est ok
		if (documentsListsManager.isWorkingListEmpty(DocumentsListsManager.DEFAULT_WORKING_LIST)) {
			return true;
		}

		// Détermine le type des documents à ajouter à partir du premier élément
		List<DocumentModel> docToAddList = documentsListsManager.getWorkingList(selectionListId);
		DocumentModel docToAdd = docToAddList.get(0);

		// Détermine le type des documents de l'outil de sélection à partir du premier élément
		List<DocumentModel> defaultWorkingList = documentsListsManager
				.getWorkingList(DocumentsListsManager.DEFAULT_WORKING_LIST);
		DocumentModel currentDoc = defaultWorkingList.get(0);

		// les DossierLink sont repassés en dossier dans l'outil de selection
		return docToAdd.getType().equals(currentDoc.getType())
				|| docToAdd.getType().equals(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE)
				&& currentDoc.getType().equals(STConstant.DOSSIER_DOCUMENT_TYPE);
	}

	/**
	 * refresh de la liste de selection
	 */
	public void refreshClipboard() {

		List<DocumentModel> defaultWorkingList = documentsListsManager
				.getWorkingList(DocumentsListsManager.DEFAULT_WORKING_LIST);

		if (defaultWorkingList != null) {
			List<DocumentModel> newDefaultWorkingList = new ArrayList<DocumentModel>();

			for (DocumentModel documentModel : defaultWorkingList) {
				try {
					DocumentModel newDocumentModel = documentManager.getDocument(new IdRef(documentModel.getId()));
					newDefaultWorkingList.add(newDocumentModel);
				} catch (Exception e) {
					// perte de droit sur le document on l'enleve de la liste
				}
			}

			documentsListsManager.setWorkingList(DocumentsListsManager.DEFAULT_WORKING_LIST, newDefaultWorkingList);
		}

	}

	/**
	 * Vérifie si les éléments ajoutés sont du même type que les éléments de la liste
	 */
	private boolean checkIfDefaultWorkingListIsSameType() {
		if (contentViewActions == null) {
			return false;
		} else {
			// Vérifie si les éléments ajoutés sont du même type que les éléments de la liste
			String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();
			return checkifDefaultWorkingListIsSameType(selectionListName);
		}
	}

	/**
	 * Ajoute l'erreur de type dans le facesMessages
	 */
	private void displayNotSameTypeError() {
		final String errorMessage = resourcesAccessor.getMessages().get("st.clipboard.addToWorkList.notSameType.error");
		facesMessages.add(StatusMessage.Severity.INFO, errorMessage);
	}
	
	/**
	 * Renvoie true si on peut créer une note partagée sur les éléments
	 * sélectionnés. Il faut que la liste d'objets sélectionnés soit non vide,
	 * que le type des objets soit RouteStep pour tous les éléments, que la
	 * feuille de route associée ne soit pas un modèle et que toutes les
	 * RouteStep soient éditables (même condition que pour le menu contextuel
	 * d'ajout de note).
	 * 
	 * @return true si la liste de travail est non vide, que les éléments qui
	 *         sont dedans sont de type RouteStep et que les RouteStep sont
	 *         éditables.
	 * @throws ClientException
	 */
	public boolean getCanCreateSharedNote() throws ClientException {
		List<DocumentModel> dml = documentsListsManager
				.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);

		if (dml.isEmpty()) {
			// Si aucun document, on ne peut pas créer une note partagée
			return false;
		}

		for (DocumentModel docModel : dml) {
			if (!STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(docModel.getType())) {
				return false;
			}
			STRouteStep routeStep = docModel.getAdapter(STRouteStep.class);
			DocumentModel documentRoute = documentManager.getDocument(new IdRef(routeStep.getDocumentRouteId()));
			STFeuilleRoute feuilleRoute = documentRoute.getAdapter(STFeuilleRoute.class);
			if (feuilleRoute.isModel()) {
				return false;// Modèles non autorisés.
			}
		}

		return true;
	}
}
