package fr.sword.naiad.nuxeo.ufnxql.core.operation;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;

/**
 * Une opération pour exécuter une requête FNXQL.
 * 
 * @author SPL
 */
@Operation(id = QueryFNXQLOperation.OP_ID, category = Constants.CAT_FETCH, label = "QueryFNXQL", description = "Effectue une requête FNXQL")
public class QueryFNXQLOperation {
	/**
	 * Identifiant technique de l'opération.
	 */
	public static final String OP_ID = "Fetch.QueryFNXQL";

	@Context
	protected CoreSession session;

	@Param(name = "query")
	protected String query;

	/**
	 * Default constructor
	 */
	public QueryFNXQLOperation(){
		// do nothing
	}
	
	@OperationMethod
	public DocumentModelList run() throws NuxeoException {
		DocumentRef[] refs = QueryUtils.doFNXQLQueryForIds(session, query, null);
		return session.getDocuments(refs);
	}
}
