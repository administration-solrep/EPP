package fr.dila.st.core.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.AbstractDocumentReader;
import org.nuxeo.ecm.core.io.impl.ExportedDocumentImpl;

public class STDocumentReader extends AbstractDocumentReader {
    private List<DocumentModel> docList;

    public STDocumentReader(List<DocumentModel> docs) {
        docList = new ArrayList<>();
        docList.addAll(docs);
    }

    @Override
    public ExportedDocument read() throws IOException {
        if (docList == null || docList.isEmpty()) {
            return null;
        }
        return new ExportedDocumentImpl(docList.remove(0));
    }

    @Override
    public void close() {
        docList = null;
    }
}
