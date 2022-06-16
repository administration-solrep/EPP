package fr.dila.solonepp.core.adapter.tablereference;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.core.domain.tablereference.MinistereImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier table de référence acteur.
 *
 * @author jtremeaux
 */
public class MinistereAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, SolonEppSchemaConstant.MINISTERE_SCHEMA);
        return new MinistereImpl(doc);
    }
}
