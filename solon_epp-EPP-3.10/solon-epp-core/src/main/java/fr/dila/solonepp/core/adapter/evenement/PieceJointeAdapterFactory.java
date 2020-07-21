package fr.dila.solonepp.core.adapter.evenement;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.core.domain.evenement.PieceJointeImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier pièce jointe.
 * 
 * @author jtremeaux
 */
public class PieceJointeAdapterFactory implements DocumentAdapterFactory {

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new PieceJointeImpl(doc);
	}
}
