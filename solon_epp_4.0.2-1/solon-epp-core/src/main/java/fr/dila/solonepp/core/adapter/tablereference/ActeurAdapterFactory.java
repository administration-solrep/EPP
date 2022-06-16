package fr.dila.solonepp.core.adapter.tablereference;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.core.domain.tablereference.ActeurImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier table de référence acteur.
 *
 * @author jtremeaux
 */
public class ActeurAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, SolonEppSchemaConstant.ACTEUR_SCHEMA);
        return new ActeurImpl(doc);
    }
}
