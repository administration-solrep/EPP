package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.jeton.JetonMaitreImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author arolin
 */
public class JetonMaitreAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public JetonMaitreAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.JETON_MAITRE_SCHEMA);
        return new JetonMaitreImpl(doc);
    }
}
