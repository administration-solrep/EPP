package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.parametre.STParametreImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 * Fabrique d'adapteur de DocumentModel vers l'objet STParametre.
 *
 */
public class ParametreAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public ParametreAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.PARAMETRE_SCHEMA);
        return new STParametreImpl(doc);
    }
}
