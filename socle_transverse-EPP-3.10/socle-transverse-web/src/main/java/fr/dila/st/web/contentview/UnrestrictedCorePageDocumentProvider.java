package fr.dila.st.web.contentview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;

/**
 * Enable to populate content view as with CoreQueryDocumentPageProvider but the query are executed in unrestricted
 * session.
 * 
 * The pagination management is the same as CoreQueryDocumentPageProvider.
 * 
 * Example of usage in contrib : <genericPageProvider
 * class="fr.dila.st.web.contentview.UnrestrictedCorePageDocumentProvider"> <property
 * name="coreSession">#{documentManager}</property> <property name="checkQueryCache">true</property> <pattern
 * quoteParameters="false" escapeParameters="false"> SELECT * FROM DossierLink WHERE ecm:parentId =
 * 'd4e35b5b-68c8-44eb-bb20-5bcf34c11478' AND ecm:isProxy=0 </pattern> <pageSize>10</pageSize> </genericPageProvider>
 * 
 * @author SPL
 */
public class UnrestrictedCorePageDocumentProvider extends CoreQueryDocumentPageProvider {

	private static final long	serialVersionUID			= 1L;

	private static final Log	LOG							= LogFactory
																	.getLog(UnrestrictedCorePageDocumentProvider.class);

	private static final String	SORT_FIELD_NOT_IN_COLUMN	= "sortFieldNotInColumn";

	/**
	 * private class to execute query in unrestricted mode
	 */
	private static class DoQueryUnrestricted extends UnrestrictedSessionRunner {
		/**
		 * the query to execute
		 */
		private String				query;
		/**
		 * the number of result needed
		 */
		private long				pageSize;
		/**
		 * the offset where start the result
		 */
		private long				offset;
		/**
		 * Need of the count total of element
		 */
		private boolean				countTotal;

		/**
		 * the list of document retrieved
		 */
		private DocumentModelList	docs;

		/**
		 * the total number of document. Available if countTotal is true.
		 */
		private long				resultsCount;

		public DoQueryUnrestricted(CoreSession session, String query, long pageSize, long offset, boolean countTotal) {
			super(session);
			this.query = query;
			this.pageSize = pageSize;
			this.offset = offset;
			this.countTotal = countTotal;
		}

		@Override
		public void run() {
			try {
				docs = session.query(query, null, pageSize, offset, countTotal);
				if (countTotal) {
					resultsCount = docs.totalSize();
				} else {
					resultsCount = UNKNOWN_SIZE;
				}
			} catch (ClientException e) {

				LOG.error("DoQueryUnrestricted [" + query + "]", e);
			}
		}

		public DocumentModelList getDocs() {
			return docs;
		}

		public long getResultCount() {
			return resultsCount;
		}
	}

	/**
	 * Default constructor
	 */
	public UnrestrictedCorePageDocumentProvider() {
		super();
	}

	@Override
	public List<DocumentModel> getCurrentPage() {
		checkQueryCache();
		if (currentPageDocuments == null) {
			error = null;
			errorMessage = null;

			CoreSession coreSession = getCoreSession();
			if (query == null) {
				buildQuery(coreSession);
			}
			if (query == null) {
				throw new ClientRuntimeException(String.format("Cannot perform null query: check provider '%s'",
						getName()));
			}

			currentPageDocuments = new ArrayList<DocumentModel>();

			try {

				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Perform query for provider '%s': '%s' with pageSize=%s, offset=%s",
							getName(), query, Long.valueOf(getPageSize()), Long.valueOf(offset)));
				}

				DoQueryUnrestricted doquery = new DoQueryUnrestricted(coreSession, query, getPageSize(), offset, true);
				doquery.runUnrestricted();
				resultsCount = doquery.getResultCount();
				currentPageDocuments = doquery.getDocs();

				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Performed query for provider '%s': got %s hits", getName(),
							Long.valueOf(resultsCount)));
				}

			} catch (Exception e) {
				error = e;
				errorMessage = e.getMessage();
				LOG.warn(e.getMessage(), e);
			}
		}
		return currentPageDocuments;
	}

	public void setSortInfos(List<SortInfo> sortInfo) {
		this.sortInfos = sortInfo;
		String sortFieldNotInColumn = getSortFieldNotInColumn();
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
		Map<String, Serializable> props = getProperties();
		return (String) props.get(SORT_FIELD_NOT_IN_COLUMN);
	}

	@Override
	protected void buildQuery(CoreSession coreSession) {
		try {
			SortInfo[] sortArray = null;
			if (sortInfos != null) {
				sortArray = CorePageProviderUtil.getSortinfoForQuery(sortInfos);
			}
			String newQuery;
			PageProviderDefinition def = getDefinition();
			if (def.getWhereClause() == null) {
				newQuery = NXQLQueryBuilder.getQuery(def.getPattern(), getParameters(),
						def.getQuotePatternParameters(), def.getEscapePatternParameters(), sortArray);
			} else {
				DocumentModel searchDocumentModel = getSearchDocumentModel();
				if (searchDocumentModel == null) {
					throw new ClientException(String.format("Cannot build query of provider '%s': "
							+ "no search document model is set", getName()));
				}
				newQuery = NXQLQueryBuilder.getQuery(searchDocumentModel, def.getWhereClause(), getParameters(),
						sortArray);
			}

			if (newQuery != null && !newQuery.equals(query)) {
				// query has changed => refresh
				refresh();
				query = newQuery;
			}
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

}
