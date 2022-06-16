package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.domain.comment.STCommentImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier STComment.
 *
 */
public class STCommentAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public STCommentAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.COMMENT_SCHEMA);
        return new STCommentImpl(doc);
    }
}
