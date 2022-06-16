package fr.dila.solonepp.core.adapter.evenement;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.core.domain.evenement.PieceJointeImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier pièce jointe.
 *
 * @author jtremeaux
 */
public class PieceJointeAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA);
        return new PieceJointeImpl(doc);
    }
}
