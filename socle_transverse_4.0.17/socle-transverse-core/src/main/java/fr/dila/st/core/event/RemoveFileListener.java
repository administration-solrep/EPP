package fr.dila.st.core.event;

import static fr.dila.st.api.constant.FilesSchemaConstant.SCHEMA_FILES;
import static fr.dila.st.api.constant.STSchemaConstant.FILE_SCHEMA;

import fr.dila.st.api.constant.FilesSchemaConstant;
import fr.dila.st.core.schema.FileSchemaUtils;
import fr.dila.st.core.schema.FilesSchemaUtils;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.blob.binary.BinaryBlob;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public class RemoveFileListener extends AbstractSyncDocumentEventListener {
    public static final String DOC_ID_PROP = "docId";
    public static final String FILE_KEYS_PROP = "fileKeys";

    @Override
    protected void handleEvent(Event event, DocumentEventContext docCtx, CoreSession session, DocumentModel doc) {
        Set<String> fileKeys = new HashSet<>();

        Optional
            .of(doc)
            .map(FileSchemaUtils::getContent)
            .filter(Objects::nonNull)
            .map(BinaryBlob.class::cast)
            .map(BinaryBlob::getKey)
            .ifPresent(fileKeys::add);

        if (doc.hasSchema(FilesSchemaConstant.SCHEMA_FILES)) {
            FilesSchemaUtils
                .getFiles(doc)
                .stream()
                .map(BinaryBlob.class::cast)
                .map(BinaryBlob::getKey)
                .forEach(fileKeys::add);
        }

        if (fileKeys.isEmpty()) {
            return;
        }

        docCtx.setProperty(DOC_ID_PROP, doc.getId());
        docCtx.setProperty(FILE_KEYS_PROP, (Serializable) fileKeys);
        ServiceUtil.getRequiredService(EventService.class).fireEvent("removeBinariesEvent", docCtx);
    }

    @Override
    protected boolean accept(Event event) {
        if (!super.accept(event)) {
            return false;
        }
        DocumentModel doc = ((DocumentEventContext) event.getContext()).getSourceDocument();
        return doc.hasSchema(FILE_SCHEMA) || doc.hasSchema(SCHEMA_FILES);
    }
}
