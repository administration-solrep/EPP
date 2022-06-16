package fr.dila.solonepp.core.adapter.piecejointe;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.core.domain.piecejointe.PieceJointeFichierImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier fichier de pièce jointe.
 *
 * @author jtremeaux
 */
public class PieceJointeFichierAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentType(doc, SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE);
        return new PieceJointeFichierImpl(doc);
    }
}
