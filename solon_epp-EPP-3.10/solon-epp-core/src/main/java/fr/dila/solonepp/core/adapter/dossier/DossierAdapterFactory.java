package fr.dila.solonepp.core.adapter.dossier;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.core.domain.dossier.DossierImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier dossier SOLON EPP.
 * 
 * @author jtremeaux
 */
public class DossierAdapterFactory implements DocumentAdapterFactory {

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(SolonEppSchemaConstant.DOSSIER_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ SolonEppSchemaConstant.DOSSIER_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new DossierImpl(doc);
	}
}
