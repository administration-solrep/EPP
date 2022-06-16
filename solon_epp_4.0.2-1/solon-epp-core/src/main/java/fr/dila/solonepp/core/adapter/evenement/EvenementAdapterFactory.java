package fr.dila.solonepp.core.adapter.evenement;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.core.domain.evenement.EvenementImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier événement.
 *
 * @author jtremeaux
 */
public class EvenementAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, SolonEppSchemaConstant.EVENEMENT_SCHEMA);
        return new EvenementImpl(doc);
    }
}
