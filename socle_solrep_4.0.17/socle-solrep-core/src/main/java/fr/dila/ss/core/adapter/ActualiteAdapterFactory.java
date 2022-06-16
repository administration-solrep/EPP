package fr.dila.ss.core.adapter;

import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.ss.core.actualite.ActualiteImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ActualiteAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, ActualiteConstant.ACTUALITE_SCHEMA);
        return new ActualiteImpl(doc);
    }
}
