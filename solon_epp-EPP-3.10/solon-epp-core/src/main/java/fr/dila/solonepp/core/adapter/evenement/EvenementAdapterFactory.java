package fr.dila.solonepp.core.adapter.evenement;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.core.domain.evenement.EvenementImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier événement.
 * 
 * @author jtremeaux
 */
public class EvenementAdapterFactory implements DocumentAdapterFactory {

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(SolonEppSchemaConstant.EVENEMENT_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ SolonEppSchemaConstant.EVENEMENT_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new EvenementImpl(doc);
	}
}
