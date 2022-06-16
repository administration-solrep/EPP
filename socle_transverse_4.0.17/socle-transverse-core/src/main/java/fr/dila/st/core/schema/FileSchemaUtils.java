package fr.dila.st.core.schema;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 * @author SPL
 *
 */
public final class FileSchemaUtils {

    private FileSchemaUtils() {
        // do nothing
    }

    public static final Blob getContent(DocumentModel doc) {
        return PropertyUtil.getBlobProperty(doc, STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_CONTENT_PROPERTY);
    }

    public static final void setContent(DocumentModel doc, Blob value) {
        PropertyUtil.setProperty(doc, STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_CONTENT_PROPERTY, value);
    }
}
