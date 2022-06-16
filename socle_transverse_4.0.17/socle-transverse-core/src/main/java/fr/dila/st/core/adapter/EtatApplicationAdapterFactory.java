package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.administration.EtatApplicationImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet File.
 *
 * @author feo
 */
public class EtatApplicationAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public EtatApplicationAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.ETAT_APPLICATION_SCHEMA);
        return new EtatApplicationImpl(doc);
    }
}
