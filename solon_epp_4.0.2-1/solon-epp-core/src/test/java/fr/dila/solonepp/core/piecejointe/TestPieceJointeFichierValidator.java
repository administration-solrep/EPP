package fr.dila.solonepp.core.piecejointe;

import fr.dila.solonepp.core.SolonEppFeature;
import fr.dila.solonepp.core.validator.PieceJointeFichierValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test du validateur des fichiers de pièce jointe
 *
 * @author jtremeaux
 */
@RunWith(FeaturesRunner.class)
@Features(SolonEppFeature.class)
public class TestPieceJointeFichierValidator {

    /**
     * Test du validateur de fichiers de pièces jointes.
     *
     * @throws Exception
     */
    @Test
    public void testPieceJointeFichierValidator() throws Exception {
        PieceJointeFichierValidator pieceJointeFichierValidator = new PieceJointeFichierValidator();

        pieceJointeFichierValidator.validatePieceJointeFichierFileName("test.doc");
        pieceJointeFichierValidator.validatePieceJointeFichierFileName("_-0Test9-_._1doc-");
        pieceJointeFichierValidator.validatePieceJointeFichierFileName("a.a");
        validateError(".doc");
        validateError("tes\\t");
        validateError("a*.doc");
    }

    protected void validateError(String filename) {
        PieceJointeFichierValidator pieceJointeFichierValidator = new PieceJointeFichierValidator();
        try {
            pieceJointeFichierValidator.validatePieceJointeFichierFileName(filename);
        } catch (Exception e) {
            return;
        }
        Assert.fail("Le nom de fichier doit être invalide: " + filename);
    }
}
