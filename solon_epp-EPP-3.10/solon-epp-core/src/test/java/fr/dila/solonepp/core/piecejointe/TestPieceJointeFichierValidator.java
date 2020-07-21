package fr.dila.solonepp.core.piecejointe;

import fr.dila.solonepp.core.SolonEppRepositoryTestCase;
import fr.dila.solonepp.core.validator.PieceJointeFichierValidator;

/**
 * Test du validateur des fichiers de pièce jointe
 * 
 * @author jtremeaux
 */
public class TestPieceJointeFichierValidator extends SolonEppRepositoryTestCase {
    
    protected void validateError(String filename) {
        PieceJointeFichierValidator pieceJointeFichierValidator = new PieceJointeFichierValidator();
        try {
            pieceJointeFichierValidator.validatePieceJointeFichierFileName(filename);
        } catch (Exception e) {
            return;
        }
        fail("Le nom de fichier doit être invalide: " + filename);
    }

    /**
     * Test du validateur de fichiers de pièces jointes.
     * 
     * @throws Exception
     */
    public void testPieceJointeFichierValidator() throws Exception {
    	
        PieceJointeFichierValidator pieceJointeFichierValidator = new PieceJointeFichierValidator();
        
        pieceJointeFichierValidator.validatePieceJointeFichierFileName("test.doc");
        pieceJointeFichierValidator.validatePieceJointeFichierFileName("_-0Test9-_._1doc-");
        pieceJointeFichierValidator.validatePieceJointeFichierFileName("a.a");
        validateError(".doc");
        validateError("tes\\t");
        validateError("a*.doc");
    }
}
