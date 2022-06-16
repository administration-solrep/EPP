package fr.dila.st.api.domain.file;

import java.io.Serializable;
import org.nuxeo.ecm.core.api.Blob;

/**
 * Interface des objets métiers fichier.
 *
 * @author jtremeaux
 */
public interface File extends Serializable {
    /**
     * Retourne le nom du fichier.
     *
     * @return Nom du fichier
     */
    String getFilename();

    /**
     * Renseigne le nom du fichier.
     *
     * @param filename
     *            Nom du fichier
     */
    void setFilename(String filename);

    /**
     * Retourne le contenu du fichier.
     *
     * @return Contenu du fichier
     */
    Blob getContent();

    /**
     * Renseigne le contenu du fichier.
     *
     * @param blob
     *            Contenu du fichier
     */
    void setContent(Blob blob);

    /**
     * Retourne le type MIME du fichier.
     *
     * @return Type MIME du fichier
     */
    String getMimeType();

    /**
     * Renseigne le type MIME du fichier.
     *
     * @param mimeType
     *            Type MIME du fichier
     */
    void setMimeType(String mimeType);
}
