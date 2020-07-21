package fr.dila.st.web.contentview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.contentview.jsf.ContentView;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageSelection;
import org.nuxeo.ecm.platform.query.api.PageSelections;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryAndFetchPageProvider;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

import fr.dila.st.api.constant.STConstant;

/**
 * Retourne des map associe a des DTO Gere la selection de document en echangeant sur un élément de la map :
 * docIdForSelection <br/>
 * Sélection un élément de la page, implique la selection du document d'id : la valeur dans la map sous la clé
 * docIdForSelection <br/>
 * Pour maintenir à jour la liste des elements selectionné, ce pageprovider peut prendre une liste de document, il
 * extrait les ids pour retrouver les map selectionnés.
 * 
 * @author spesnel
 * 
 */
public abstract class AbstractDTOPageProvider extends CoreQueryAndFetchPageProvider {

	/**
	 * Serial version UID
	 */
	private static final long							serialVersionUID			= 8712484033668441644L;

	private static final String							SORT_FIELD_NOT_IN_COLUMN	= "sortFieldNotInColumn";

	protected final Set<String>							selectedIds					= new HashSet<String>();

	protected Map<String, Map<String, Serializable>>	allItems					= new HashMap<String, Map<String, Serializable>>();

	protected DocumentsListsManager						documentsListsManager;

	private ContentView									contentView;

	protected PageSelections<Map<String, Serializable>>	currentSelection;

	/**
	 * renseigne les valeur de currentItems et resultCount
	 */
	abstract protected void fillCurrentPageMapList(CoreSession coreSession) throws ClientException;

	@Override
	public List<Map<String, Serializable>> getCurrentPage() {
		checkQueryCache();
		if (currentItems == null) {
			error = null;
			errorMessage = null;

			if (query == null) {
				buildQuery();
			}
			if (query == null) {
				throw new ClientRuntimeException(String.format("Cannot perform null query: check provider '%s'",
						getName()));
			}

			try {
				final CoreSession coreSession = getCoreSession();

				fillCurrentPageMapList(coreSession);
				if (currentItems == null) {
					throw new ClientRuntimeException("currentItems null");
				}
			} catch (final ClientException e) {
				throw new ClientRuntimeException(e);
			}

			for (final Map<String, Serializable> item : currentItems) {
				final String docIdForSelection = (String) item.get("docIdForSelection");
				allItems.put(docIdForSelection, item);
			}

		}

		return currentItems;
	}

	protected CoreSession getCoreSession() {
		final Map<String, Serializable> props = getProperties();
		final CoreSession coreSession = (CoreSession) props.get(CORE_SESSION_PROPERTY);
		if (coreSession == null) {
			throw new ClientRuntimeException("cannot find core session");
		}
		return coreSession;
	}

	@Override
	public void setSortInfos(final List<SortInfo> sortInfo) {
		this.sortInfos = sortInfo;
		final String sortFieldNotInColumn = getSortFieldNotInColumn();
		if (sortFieldNotInColumn != null) {
			if (sortInfos != null && sortInfo.size() > 1) {
				// vue que la colonne est un tri par defaut mais que la colonne est pas affiché on la supprime des tris
				if (sortInfos.get(0).getSortColumn().equals(sortFieldNotInColumn)) {
					sortInfos.remove(0);
				}
			} else if (sortInfos != null && sortInfo.isEmpty()) {
				// plus de tris, on remet le tri par defaut
				sortInfos.add(new SortInfo(sortFieldNotInColumn, true));
			}
		}
		refresh();
	}

	protected String getSortFieldNotInColumn() {
		final Map<String, Serializable> props = getProperties();
		return (String) props.get(SORT_FIELD_NOT_IN_COLUMN);
	}

	@Override
	protected void buildQuery() {
		try {
			SortInfo[] sortArray = null;
			if (sortInfos != null) {
				sortArray = CorePageProviderUtil.getSortinfoForQuery(sortInfos);
			}
			String newQuery;
			final PageProviderDefinition def = getDefinition();
			if (def.getWhereClause() == null) {
				newQuery = NXQLQueryBuilder.getQuery(def.getPattern(), getParameters(),
						def.getQuotePatternParameters(), def.getEscapePatternParameters(), sortArray);
			} else {
				final DocumentModel searchDocumentModel = getSearchDocumentModel();
				if (searchDocumentModel == null) {
					throw new ClientException(String.format("Cannot build query of provider '%s': "
							+ "no search document model is set", getName()));
				}
				newQuery = NXQLQueryBuilder.getQuery(searchDocumentModel, def.getWhereClause(), getParameters(),
						sortArray);
			}

			newQuery = OrderByDistinctQueryCorrector.correct(newQuery, sortArray);

			if (newQuery != null && !newQuery.equals(query)) {
				// query has changed => refresh
				if (query != null) {
					refresh();
				}
				query = newQuery;
			}
		} catch (final ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	public void setSelectedEntriesFromDocumentList(final List<?> entries) {
		selectedIds.clear();

		if (entries != null) {
			for (final Object o : (List<?>) entries) {
				if (o instanceof DocumentModel) {
					final String docId = ((DocumentModel) o).getId();
					selectedIds.add(docId);
				} else if (o instanceof Map<?, ?>) {
					final String docId = (String) ((Map<?, ?>) o).get(STConstant.KEY_ID_SELECTION);
					selectedIds.add(docId);
				}
			}
		}
	}

	@Override
	public void setSelectedEntries(final List<Map<String, Serializable>> entries) {
		setSelectedEntriesFromDocumentList(entries);
		// reset current select page so that it's rebuilt
		currentSelectPage = null;
	}

	public PageSelections<Map<String, Serializable>> getCurrentSelection() {

		if (currentSelection == null) {

			currentSelection = new PageSelections<Map<String, Serializable>>();

			final List<PageSelection<Map<String, Serializable>>> entries = new ArrayList<PageSelection<Map<String, Serializable>>>();

			if (documentsListsManager != null && contentView != null) {
				final List<DocumentModel> selectedDoc = documentsListsManager.getWorkingList(contentView
						.getSelectionListName());
				if (selectedDoc != null) {
					for (final DocumentModel doc : selectedDoc) {
						final Map<String, Serializable> item = allItems.get(doc.getId());
						if (item != null) {
							entries.add(new PageSelection<Map<String, Serializable>>(item, false));
						}
					}
				}
			}

			currentSelection.setEntries(entries);
		}

		return currentSelection;
	}

	@Override
	public PageSelections<Map<String, Serializable>> getCurrentSelectPage() {
		if (currentSelectPage == null) {
			final List<PageSelection<Map<String, Serializable>>> entries = new ArrayList<PageSelection<Map<String, Serializable>>>();
			final List<Map<String, Serializable>> currentPage = getCurrentPage();
			currentSelectPage = new PageSelections<Map<String, Serializable>>();
			currentSelectPage.setName(name);
			if (currentPage != null && !currentPage.isEmpty()) {
				if (selectedIds.isEmpty()) {
					// no selection at all
					for (int i = 0; i < currentPage.size(); i++) {
						entries.add(new PageSelection<Map<String, Serializable>>(currentPage.get(i), false));
					}
				} else {
					boolean allSelected = true;
					for (int i = 0; i < currentPage.size(); i++) {
						final Map<String, Serializable> entry = currentPage.get(i);
						final Boolean selected = Boolean.valueOf(selectedIds.contains(entry
								.get(STConstant.KEY_ID_SELECTION)));
						if (!Boolean.TRUE.equals(selected)) {
							allSelected = false;
						}
						entries.add(new PageSelection<Map<String, Serializable>>(entry, selected.booleanValue()));
					}
					if (allSelected) {
						currentSelectPage.setSelected(true);
					}
				}
			}
			currentSelectPage.setEntries(entries);
		}
		return currentSelectPage;
	}

	@Override
	public void setSearchDocumentModel(final DocumentModel searchDocumentModel) {
		if (this.searchDocumentModel != searchDocumentModel) {
			this.searchDocumentModel = searchDocumentModel;
			refresh();
		}
	}

	public void setDocumentsListsManager(final DocumentsListsManager documentsListsManager) {
		this.documentsListsManager = documentsListsManager;
	}

	public DocumentsListsManager getDocumentsListsManager() {
		return documentsListsManager;
	}

	public void setContentView(final ContentView contentView) {
		this.contentView = contentView;
		// reset currentSelection
		currentSelection = null;
	}

	public ContentView getContentView() {
		return contentView;
	}
}
