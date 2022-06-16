package fr.dila.st.ui.contentview;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryAndFetchPageProvider;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;

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
    private static final long serialVersionUID = 8712484033668441644L;

    private static final String SORT_FIELD_NOT_IN_COLUMN = "sortFieldNotInColumn";

    protected Set<String> selectedIds = new HashSet<>();

    protected Map<String, Map<String, Serializable>> allItems = new HashMap<>();

    /**
     * renseigne les valeur de currentItems et resultCount
     */
    protected abstract void fillCurrentPageMapList(CoreSession coreSession);

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
                throw new NuxeoException(String.format("Cannot perform null query: check provider '%s'", getName()));
            }

            final CoreSession coreSession = getCoreSession();

            fillCurrentPageMapList(coreSession);
            if (currentItems == null) {
                throw new NuxeoException("currentItems null");
            }

            for (final Map<String, Serializable> item : currentItems) {
                final String docIdForSelection = (String) item.get("docIdForSelection");
                allItems.put(docIdForSelection, item);
            }
        }

        return currentItems;
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
        SortInfo[] sortArray = null;
        if (sortInfos != null) {
            sortArray = CorePageProviderUtil.getSortinfoForQuery(sortInfos);
        }
        String newQuery;
        final PageProviderDefinition def = getDefinition();
        if (def.getWhereClause() == null) {
            newQuery =
                NXQLQueryBuilder.getQuery(
                    def.getPattern(),
                    getParameters(),
                    def.getQuotePatternParameters(),
                    def.getEscapePatternParameters(),
                    null,
                    sortArray
                );
        } else {
            final DocumentModel searchDocumentModel = getSearchDocumentModel();
            if (searchDocumentModel == null) {
                throw new NuxeoException(
                    String.format(
                        "Cannot build query of provider '%s': " + "no search document model is set",
                        getName()
                    )
                );
            }
            newQuery = NXQLQueryBuilder.getQuery(searchDocumentModel, def.getWhereClause(), getParameters(), sortArray);
        }

        newQuery = OrderByDistinctQueryCorrector.correct(newQuery, sortArray);

        if (newQuery != null && !newQuery.equals(query)) {
            // query has changed => refresh
            if (query != null) {
                refresh();
            }
            query = newQuery;
        }
    }
}
