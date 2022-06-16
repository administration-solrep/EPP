package fr.sword.naiad.nuxeo.commons.core.operation;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

@Operation(id = UnrestrictedUnlockOperation.OPID, category = Constants.CAT_DOCUMENT, label = "Unrestricted unlock", description = "Unrestricted unlock of document")
public class UnrestrictedUnlockOperation {

	private static final Log LOG = LogFactory.getLog(UnrestrictedUnlockOperation.class);
	public static final String OPID = "Naiad.Document.UnrestrictedUnlock";
	
	private static class UnrestrictedUnlock extends UnrestrictedSessionRunner{
		private final List<DocumentModel> docs;
		public UnrestrictedUnlock(CoreSession session, List<DocumentModel> docs){
			super(session);
			this.docs = docs;
		}
		
		public void run() throws NuxeoException {
			for(DocumentModel doc : docs){				
				DocumentRef docRef = doc.getRef();
				Lock lock = session.getLockInfo(docRef);
				if(lock != null){
					LOG.info("Remove lock on document [" + doc + "]");
					session.removeLock(docRef);
				}
			}
		}
	}
	
	@Context
    protected CoreSession session;

	@OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws NuxeoException {
    	unlockIfNeeded(session, Collections.singletonList(doc));
    	return doc;
    }
    
    @OperationMethod
	public DocumentModelList run(DocumentModelList docs) throws NuxeoException {
    	unlockIfNeeded(session, docs);
    	return docs;
	}
    
    private void unlockIfNeeded(CoreSession session, List<DocumentModel> docs) throws NuxeoException {
    	UnrestrictedUnlock unlock = new UnrestrictedUnlock(session, docs);
    	unlock.runUnrestricted();
    }
	
}
