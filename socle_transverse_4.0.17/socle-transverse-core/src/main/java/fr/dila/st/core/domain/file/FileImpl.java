package fr.dila.st.core.domain.file;

import fr.dila.st.api.domain.file.File;
import fr.dila.st.core.schema.FileSchemaUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier fichier.
 *
 * @author jtremeaux
 */
public class FileImpl implements File {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de PieceJointeFichierImpl.
     *
     * @param document
     *            Modèle de document
     */
    public FileImpl(DocumentModel document) {
        this.document = document;
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
            setContent(blob);
        }
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
    public String getMimeType() {
        Blob blob = getContent();
        if (blob == null) {
            return null;
        } else {
            return blob.getMimeType();
        }
    }

    @Override
    public void setMimeType(String mimeType) {
        Blob blob = getContent();
        if (blob != null) {
            blob.setMimeType(mimeType);
        }
    }
}
