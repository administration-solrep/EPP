package fr.dila.solonepp.core.adapter.tablereference;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.core.domain.tablereference.GouvernementImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier table de référence acteur.
 * 
 * @author jtremeaux
 */
public class GouvernementAdapterFactory implements DocumentAdapterFactory {

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new GouvernementImpl(doc);
	}
}
