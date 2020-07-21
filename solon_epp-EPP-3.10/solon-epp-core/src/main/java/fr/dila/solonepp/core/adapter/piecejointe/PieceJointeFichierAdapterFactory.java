package fr.dila.solonepp.core.adapter.piecejointe;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.core.domain.piecejointe.PieceJointeFichierImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier fichier de pièce jointe.
 * 
 * @author jtremeaux
 */
public class PieceJointeFichierAdapterFactory implements DocumentAdapterFactory {

	protected void checkDocument(DocumentModel doc) {
		if (!doc.getType().equals(SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE)) {
			throw new CaseManagementRuntimeException("Document should be of type "
					+ SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new PieceJointeFichierImpl(doc);
	}
}
