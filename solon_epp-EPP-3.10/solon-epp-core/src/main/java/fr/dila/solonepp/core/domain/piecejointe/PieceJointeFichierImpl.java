package fr.dila.solonepp.core.domain.piecejointe;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.st.core.domain.file.FileImpl;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de l'objet métier fichier de pièce jointe.
 * 
 * @author jtremeaux
 */
public class PieceJointeFichierImpl extends FileImpl implements PieceJointeFichier {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur de PieceJointeFichierImpl.
     * 
     * @param document Modèle de document
     */
    public PieceJointeFichierImpl(DocumentModel document) {
        super(document);
    }

    @Override
    public String getTitle() {
    	return DublincoreSchemaUtils.getTitle(document);
    }
    
    @Override
    public void setTitle(String title) {
    	DublincoreSchemaUtils.setTitle(document, title);
    }

	/**
	 * @see FEV541 utilise désormais un SHA512
	 */
	@Override
	public String getDigestSha512() {
		return PropertyUtil.getStringProperty(document, "stfile", "digestSha512");
	}

	/**
	 * @see FEV541 utilise désormais un SHA512
	 */
	@Override
	public void setDigestSha512(String digest) {
		PropertyUtil.setProperty(document, "stfile", "digestSha512", digest);
	}
}
