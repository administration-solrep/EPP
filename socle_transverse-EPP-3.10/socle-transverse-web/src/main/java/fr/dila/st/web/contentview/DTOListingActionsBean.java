package fr.dila.st.web.contentview;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.contentview.jsf.ContentView;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.util.StringUtil;

/**
 * Bean pour manipuler des liste de map dans les content view plutot que des document comme le fait
 * documentListingActions
 * 
 * La map est supposé contenir un champ docIdForSelection contenant l'id du document à prendre en compte lors de la
 * selection
 */
@Name("dtoListingActions")
@Scope(CONVERSATION)
public class DTOListingActionsBean implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long					serialVersionUID	= 5167277129519627971L;

	private static final Log					log					= LogFactory.getLog(DTOListingActionsBean.class);

	@In(create = true)
	protected transient NavigationContext		navigationContext;

	@In(create = true)
	protected transient ContentViewActions		contentViewActions;

	@In(required = false, create = true)
	protected transient DocumentsListsManager	documentsListsManager;

	@In(create = true, required = false)
	protected transient CoreSession				documentManager;

	// API for AJAX selection in listings of content views

	protected List<String> getCurrentPageDTO(final String contentViewName) throws ClientException {
		final List<String> docIds = new ArrayList<String>();
		final ContentView cView = contentViewActions.getContentView(contentViewName);
		if (cView != null) {
			final PageProvider<?> cProvider = cView.getCurrentPageProvider();
			if (cProvider != null) {
				final List<?> items = cProvider.getCurrentPage();
				buildIdSelectionList(docIds, items);
			}
		}
		return docIds;
	}

	private void buildIdSelectionList(final List<String> docIds, final List<?> items) throws ClientException {
		try {
			@SuppressWarnings("unchecked")
			final List<Map<String, Serializable>> maps = (List<Map<String, Serializable>>) items;
			for (final Map<String, Serializable> m : maps) {
				if (!m.containsKey(STConstant.KEY_ID_SELECTION)) {
					throw new ClientException(
							"La map de d'un objet de la page ne contient pas de clé pour la sélection "
									+ STConstant.KEY_ID_SELECTION);
				}
				docIds.add((String) m.get(STConstant.KEY_ID_SELECTION));
			}
		} catch (final Exception e) {
			throw new ClientException(e);
		}
	}

	public void processSelectPage(final String contentViewName, final String listName, final Boolean selection)
			throws ClientException {
		final List<String> docIds = getCurrentPageDTO(contentViewName);
		addDocIdToSelection(listName, selection, docIds);
	}

	private void addDocIdToSelection(final String listName, final Boolean selection, final List<String> docIds)
			throws ClientException {
		if (docIds != null && !docIds.isEmpty()) {
			final List<DocumentModel> documents = new ArrayList<DocumentModel>();
			for (final String id : docIds) {
				documents.add(documentManager.getDocument(new IdRef(id)));
			}
			final String lName = listName == null ? DocumentsListsManager.CURRENT_DOCUMENT_SELECTION : listName;
			
			StringBuilder ids = new StringBuilder();
			StringBuilder paths = new StringBuilder();
			if (log.isDebugEnabled()) {
				boolean first = true;
				for (DocumentModel doc : documents) {
					if (!first) {
						ids.append(", ");
						paths.append(", ");
					}
					ids.append(doc.getId());
					paths.append(doc.getPathAsString());
					first = false;
				}
			}

			if (Boolean.TRUE.equals(selection)) {
				if (log.isDebugEnabled()) {
					log.debug("Adding to selection documents " + ids.toString() + "(" + paths.toString() + ")");
				}
				documentsListsManager.addToWorkingList(lName, documents);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Removing from selection documents " + ids.toString() + "(" + paths.toString() + ")");
				}
				documentsListsManager.removeFromWorkingList(lName, documents);
			}
		}
	}

	public void selectAll() throws ClientException {
		final List<String> docIds = new ArrayList<String>();
		final ContentView cView = contentViewActions.getCurrentContentView();
		if (cView != null) {
			final PageProvider<?> cProvider = cView.getCurrentPageProvider();
			if (cProvider != null) {
				final Long currentPageSize = cProvider.getPageSize();
				try {
					cProvider.setPageSize(-1);
					final List<?> items = cProvider.getCurrentPage();
					buildIdSelectionList(docIds, items);
					addDocIdToSelection(cView.getSelectionListName(), Boolean.TRUE, docIds);
				} finally {
					cProvider.setPageSize(currentPageSize);
				}
			}
		}
	}

	public void deselectAll() {
		final ContentView cView = contentViewActions.getCurrentContentView();
		if (cView != null) {
			documentsListsManager.resetWorkingList(cView.getSelectionListName());
		}
	}

	public void processSelectRow(final String docRef, final String contentViewName, final String listName,
			final Boolean selection) throws ClientException {
		final List<String> docIds = getCurrentPageDTO(contentViewName);
		DocumentModel doc = null;
		if (docIds != null && !docIds.isEmpty()) {
			for (final String id : docIds) {
				if (id.equals(docRef)) {
					doc = documentManager.getDocument(new IdRef(id));
					break;
				}
			}
		}
		if (doc == null) {
			log.error(String.format("could not find doc '%s' in the current page of "
					+ "content view page provider '%s'", docRef, contentViewName));
			return;
		}
		final String lName = listName == null ? DocumentsListsManager.CURRENT_DOCUMENT_SELECTION : listName;
		if (Boolean.TRUE.equals(selection)) {
			if(log.isDebugEnabled()) {
				log.debug("Adding to selection document " + doc.getId() + "("+doc.getPathAsString()+")");
			}
			documentsListsManager.addToWorkingList(lName, doc);
		} else {
			if(log.isDebugEnabled()) {
				log.debug("Removing from selection document " + doc.getId() + "("+doc.getPathAsString()+")");
			}
			documentsListsManager.removeFromWorkingList(lName, doc);
		}
	}

	/**
	 * Handle row selection event after having ensured that the navigation context stills points to currentDocumentRef
	 * to protect against browsers' back button errors
	 * 
	 * @throws ClientException
	 *             if currentDocRef is not a valid document
	 */
	public void checkCurrentDocAndProcessSelectRow(final String docRef, final String providerName,
			final String listName, final Boolean selection, final String requestedCurrentDocRef) throws ClientException {
		final DocumentRef requestedCurrentDocumentRef = new IdRef(requestedCurrentDocRef);
		DocumentRef currentDocumentRef = null;
		final DocumentModel currentDocument = navigationContext.getCurrentDocument();
		if (currentDocument != null) {
			currentDocumentRef = currentDocument.getRef();
		}
		if (!requestedCurrentDocumentRef.equals(currentDocumentRef)) {
			navigationContext.navigateToRef(requestedCurrentDocumentRef);
		}
		processSelectRow(docRef, providerName, listName, selection);
	}

}
