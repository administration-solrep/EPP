package fr.dila.solonepp.core.validator;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import java.util.List;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Validateur des fichiers de pièces jointes.
 *
 * @author jtremeaux
 */
public class PieceJointeFichierValidator {

    /**
     * Vérifie si le format du nom de fichier d'une pièce jointe est valide.
     *
     * @param filename
     *            Nom de fichier
     */
    public void validatePieceJointeFichierFileName(String filename) {
        Matcher m = SolonEppConstant.PIECE_JOINTE_FICHIER_FILENAME_PATTERN.matcher(filename);
        if (!m.matches()) {
            throw new NuxeoException(
                "Le nom de fichier + '" +
                filename +
                "' doit comporter uniquement les caractères suivants (accentués ou non) : [A-Z a-z 0-9], tiret (-), souligné (_) et espace"
            );
        }
    }

    /**
     * Valide les données du fichier de pièce jointe.
     *
     * @param pieceJointeDoc
     *            Document pièce jointe
     * @param pieceJointeFichierDoc
     *            Document pièce jointe de fichier
     */
    public void validatePieceJointeFichier(
        DocumentModel pieceJointeDoc,
        DocumentModel pieceJointeFichierDoc,
        List<String> mimetypesAllowed
    ) {
        PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
        PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);

        // Vérifie le nom du fichier
        String filename = pieceJointeFichier.getSafeFilename();
        if (StringUtils.isBlank(filename)) {
            throw new NuxeoException(
                "Le nom du fichier de pièce jointe est obligatoire, pièce jointe " + pieceJointe.getTypePieceJointe()
            );
        }
        validatePieceJointeFichierFileName(filename);

        // Vérifie la présence du digest SHA512
        if (StringUtils.isBlank(pieceJointeFichier.getDigestSha512())) {
            throw new NuxeoException("Le digest SHA512 du fichier " + filename + " est obligatoire");
        }

        // vérifie le mimetype des pj si restriction
        if (!mimetypesAllowed.isEmpty() && !mimetypesAllowed.contains(pieceJointeFichier.getMimeType())) {
            throw new NuxeoException(
                "La pièce jointe " +
                filename +
                " ne peut pas être ajoutée. Le format n'est pas accepté pour cette communication."
            );
        }
    }
}
