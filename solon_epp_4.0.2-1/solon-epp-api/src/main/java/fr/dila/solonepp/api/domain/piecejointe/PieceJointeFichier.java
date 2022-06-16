package fr.dila.solonepp.api.domain.piecejointe;

import fr.dila.st.api.domain.file.File;
import java.io.IOException;

/**
 * Interface des objets métiers fichier de pièce jointe.
 *
 * @author jtremeaux
 */
public interface PieceJointeFichier extends File {
    /**
     * Retourne le titre du fichier de pièce jointe.
     *
     * @return Titre du fichier de pièce jointe
     */
    String getTitle();

    /**
     * Renseigne le titre du fichier de pièce jointe.
     *
     * @param title Titre du fichier de pièce jointe
     */
    void setTitle(String title);

    /**
     * Retourne le digest du fichier.
     *
     * @return Digest du fichier
     * @throws IOException
     */
    String getDigestSha512();

    /**
     * Renseigne le digest du fichier.
     *
     * @param content
     *            Digest du fichier
     * @throws IOException
     */
    void setDigestSha512(String digest);

    /**
     * Retourne le filename du fichier de pièce jointe si le blob existe,
     * sinon retourne le title.
     *
     * @return Nom du fichier de pièce jointe
     */
    String getSafeFilename();
}
