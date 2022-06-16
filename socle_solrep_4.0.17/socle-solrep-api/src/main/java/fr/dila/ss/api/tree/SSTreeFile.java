package fr.dila.ss.api.tree;

import java.io.File;
import java.io.InputStream;
import org.nuxeo.ecm.core.api.Blob;

public interface SSTreeFile extends SSTreeNode {
    /**
     * Récupère le nom du noeud.
     */
    String getFilename();

    /**
     * définit le nom du noeud.
     *
     * @param name
     */
    void setFilename(String filename);

    /**
     * Retourne le filename du fichier de pièce jointe si le blob existe,
     * sinon retourne le title.
     *
     * @return Nom du fichier de pièce jointe
     */
    String getSafeFilename();

    /**
     * récupère le mime type du document
     *
     * @return
     */
    String getFileMimeType();

    /**
     *
     * @return
     */
    Blob getContent();

    /**
     *
     * @param content
     */
    void setContent(Blob content);

    /**
     * Récupère l'information major_version du schema uid
     *
     * @return
     */
    Long getMajorVersion();

    File getFile();

    void setFile(InputStream in, String filename);

    void setFile(File file);
}
