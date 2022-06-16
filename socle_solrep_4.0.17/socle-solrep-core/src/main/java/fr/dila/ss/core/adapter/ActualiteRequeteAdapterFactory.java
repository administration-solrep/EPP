package fr.dila.ss.core.adapter;

import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.ss.core.actualite.ActualiteRequeteImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ActualiteRequeteAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, ActualiteConstant.ACTUALITE_REQUETE_SCHEMA);
        return new ActualiteRequeteImpl(doc);
    }
}
