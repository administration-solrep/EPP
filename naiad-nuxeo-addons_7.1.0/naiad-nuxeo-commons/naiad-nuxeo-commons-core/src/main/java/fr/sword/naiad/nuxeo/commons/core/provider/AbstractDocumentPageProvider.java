package fr.sword.naiad.nuxeo.commons.core.provider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.query.api.AbstractPageProvider;

/**
 *
 * Page provider inspire de CoreQueryPageProvider où
 * ils reste a recupere les document suivant un limit, offset
 * en creant une requete (prenant en compte le tri), et ou
 * il faut eventuellement calculer le nombre total de resultat
 * 
 * @author SPL
 *
 */
public abstract class AbstractDocumentPageProvider extends AbstractPageProvider<DocumentModel> {

	public static final String PROPERTY_CORESESSION = "coreSession";
	
	private static final Log LOG = LogFactory.getLog(AbstractDocumentPageProvider.class);
	
	private static final long serialVersionUID = 1L;

	private List<DocumentModel> currentPageDocuments;

	public AbstractDocumentPageProvider() {
		super();
	}

	@Override
    public List<DocumentModel> getCurrentPage() {
		 if (currentPageDocuments == null) {
			 error = null;
	         errorMessage = null;
	         
	         try {
	        	 final CoreSession session = getSessionFromProperties();
	        	 
	        	 boolean doTotalCount = getResultsCount() < 0;
	        	 long pageSize = getPageSize();
	        	 currentPageDocuments = generateCurrentPage(session, pageSize, offset, doTotalCount);
	        	 
				// refresh may have triggered display of an empty page => go
				// back to first page or forward to last page depending on
				// results count and page size
				if (pageSize != 0) {
					if (offset != 0 && currentPageDocuments.isEmpty()) {
						if (getResultsCount() == 0) {
							// fetch first page directly
							if (LOG.isDebugEnabled()) {
								LOG.debug(String
										.format("Current page %s is not the first one but "
												+ "shows no result and there are "
												+ "no results => rewind to first page",
												Long.valueOf(getCurrentPageIndex())));
							}
							firstPage();
						} else {
							// fetch last page
							if (LOG.isDebugEnabled()) {
								LOG.debug(String
										.format("Current page %s is not the first one but "
												+ "shows no result and there are "
												+ "%s results => fetch last page",
												Long.valueOf(getCurrentPageIndex()),
												Long.valueOf(resultsCount)));
							}
							lastPage();
						}
						// fetch current page again
						getCurrentPage();
					}
				}
	        	 
	         } catch(NuxeoException e){
	        	 error = e;
	        	 errorMessage = e.getMessage();
	        	 LOG.error("Error in getCurrentPage", e);
	        	 currentPageDocuments = new ArrayList<DocumentModel>(0);
	         }
		 }
		 return currentPageDocuments;
	}
	
	@Override
	protected void pageChanged() {
		super.pageChanged();
		currentPageDocuments = null;
	}

	@Override
	public void refresh() {
		super.refresh();
		currentPageDocuments = null;
	}
	
	/**
	 * genère la liste de document pour la page courante
	 * 
	 * @return les docs de la page courante
	 */
	protected abstract List<DocumentModel> generateCurrentPage(CoreSession session, long limit, long offset, boolean doTotalCount) throws NuxeoException;
		
	protected List<DocumentModel> getCurrentPageDocuments() {
		return currentPageDocuments;
	}

	protected void setCurrentPageDocuments(
			List<DocumentModel> currentPageDocuments) {
		this.currentPageDocuments = currentPageDocuments;
	}

	protected CoreSession getSessionFromProperties() throws NuxeoException {
		Serializable session = getProperties().get(PROPERTY_CORESESSION);
		if(session instanceof CoreSession){
			return (CoreSession) session;
		} else {
			throw new NuxeoException("Cannot retrieve CoreSession from properties ["+PROPERTY_CORESESSION+"]");
		}
	}
}
