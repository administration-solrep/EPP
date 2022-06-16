package fr.dila.ss.core.tree;

import static java.util.Optional.ofNullable;

import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.schema.FileSchemaUtils;
import fr.dila.st.core.util.BlobUtils;
import java.io.File;
import java.io.InputStream;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.model.BlobNotFoundException;

public class SSTreeFileImpl extends SSTreeNodeImpl implements SSTreeFile {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -8231018847656537811L;

    public SSTreeFileImpl(DocumentModel doc) {
        super(doc);
    }

    @Override
    public Blob getContent() {
        return FileSchemaUtils.getContent(document);
    }

    @Override
    public void setContent(Blob content) {
        FileSchemaUtils.setContent(document, content);
    }

    @Override
    public Long getMajorVersion() {
        return getLongProperty(STSchemaConstant.UID_SCHEMA, STSchemaConstant.UID_MAJOR_VERSION_PROPERTY);
    }

    @Override
    public String getFileMimeType() {
        return getStringProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_CONTENT_MIME_TYPE_PROPERTY);
    }

    @Override
    public String getFilename() {
        Blob blob = getContent();
        if (blob == null) {
            return null;
        } else {
            return blob.getFilename();
        }
    }

    @Override
    public void setFilename(String filename) {
        Blob blob = getContent();
        if (blob != null) {
            blob.setFilename(filename);
        }
    }

    @Override
    public String getSafeFilename() {
        try {
            return getFilename();
        } catch (BlobNotFoundException e) {
            // do nothing
        }
        return getTitle();
    }

    @Override
    public File getFile() {
        return ofNullable(document.getAdapter(BlobHolder.class))
            .map(BlobHolder::getBlob)
            .map(Blob::getFile)
            .orElse(null);
    }

    @Override
    public void setFile(InputStream in, String filename) {
        Blob blob = ofNullable(in).map(is -> BlobUtils.createSerializableBlob(is, filename)).orElse(null);
        document.getAdapter(BlobHolder.class).setBlob(blob);
    }

    @Override
    public void setFile(File file) {
        document.getAdapter(BlobHolder.class).setBlob(BlobUtils.createBlob(file));
    }
}
