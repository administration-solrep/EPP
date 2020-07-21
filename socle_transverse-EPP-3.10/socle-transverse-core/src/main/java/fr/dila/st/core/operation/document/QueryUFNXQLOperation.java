package fr.dila.st.core.operation.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.core.query.QueryUtils;

/**
 * Une opération pour exécuter une requête UFNXL.
 * 
 * @author jgomez
 */
@Operation(id = QueryUFNXQLOperation.ID, category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY, label = "QueryUFNXQL", description = "Effectue une requête UFNXQL")
public class QueryUFNXQLOperation {
	/**
	 * Identifiant technique de l'opération.
	 */
	public static final String	ID		= "ST.Document.QueryUFNXQL";

	@Context
	protected CoreSession		session;

	@Param(name = "query")
	protected String			query;

	@Param(name = "language", required = false, widget = Constants.W_OPTION, values = { "NXQL" })
	protected String			lang	= "UFNXQL";

	/**
	 * Default constructor
	 */
	public QueryUFNXQLOperation() {
		// do nothing
	}

	@OperationMethod
	public DocumentModelList run() throws Exception {
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doUFNXQLQuery(session, query, null);
			Iterator<Map<String, Serializable>> it = res.iterator();
			List<DocumentRef> ids = new ArrayList<DocumentRef>();
			while (it.hasNext()) {
				Map<String, Serializable> m = it.next();
				ids.add(new IdRef((String) m.get("id")));
			}
			DocumentRef[] refs = new DocumentRef[ids.size()];
			int i = 0;
			for (DocumentRef r : ids) {
				refs[i] = r;
				i++;
			}
			return session.getDocuments(refs);
		} finally {
			if (res != null) {
				res.close();
			}
		}
	}

}
