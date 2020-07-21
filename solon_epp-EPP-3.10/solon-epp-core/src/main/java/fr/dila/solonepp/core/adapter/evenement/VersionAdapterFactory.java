package fr.dila.solonepp.core.adapter.evenement;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.core.domain.evenement.VersionImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier dossier version.
 * 
 * @author jtremeaux
 */
public class VersionAdapterFactory implements DocumentAdapterFactory {

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(SolonEppSchemaConstant.VERSION_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ SolonEppSchemaConstant.VERSION_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new VersionImpl(doc);
	}
}
