package fr.dila.solonepp.core.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.MimetypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.st.core.util.CollectionUtil;

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
	 * @throws ClientException
	 */
	public void validatePieceJointeFichierFileName(String filename) throws ClientException {
		Matcher m = SolonEppConstant.PIECE_JOINTE_FICHIER_FILENAME_PATTERN.matcher(filename);
		if (!m.matches()) {
			throw new ClientException(
					"Le nom de fichier + '"
							+ filename
							+ "' doit comporter uniquement les caractères suivants (accentués ou non) : [A-Z a-z 0-9], tiret (-), souligné (_) et espace");
		}
	}

	/**
	 * Valide les données du fichier de pièce jointe.
	 * 
	 * @param pieceJointeDoc
	 *            Document pièce jointe
	 * @param pieceJointeFichierDoc
	 *            Document pièce jointe de fichier
	 * @throws ClientException
	 */
	public void validatePieceJointeFichier(DocumentModel pieceJointeDoc, DocumentModel pieceJointeFichierDoc, List<String> mimetypesAllowed)
			throws ClientException {
		PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
		PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);

		// Vérifie le nom du fichier
		String filename = pieceJointeFichier.getFilename();
		if (StringUtils.isBlank(filename)) {
			throw new ClientException("Le nom du fichier de pièce jointe est obligatoire, pièce jointe "
					+ pieceJointe.getTypePieceJointe());
		}
		validatePieceJointeFichierFileName(filename);

		// Vérifie la présence du digest SHA512
		if (StringUtils.isBlank(pieceJointeFichier.getDigestSha512())) {
			throw new ClientException("Le digest SHA512 du fichier " + filename + " est obligatoire");
		}
		
		// vérifie le mimetype des pj si restriction
        if (!mimetypesAllowed.isEmpty() && !mimetypesAllowed.contains(pieceJointeFichier.getMimeType())) {
            throw new ClientException("La pièce jointe " + filename + " ne peut pas être ajoutée. Le format n'est pas accepté pour cette communication.");
        }
        
	}
	
	public void validatePieceJointeFichierMimeType(String mimetype, Map<String, MimetypeDescriptor> mimetypesAllowed)
			throws ClientException {
		if (!CollectionUtil.isEmpty(new ArrayList<MimetypeDescriptor>(mimetypesAllowed.values()))
				&& !mimetypesAllowed.keySet().contains(mimetype)) {
			
			StringBuffer mimetypesBuffer = new StringBuffer();
			boolean first = true;
			for (MimetypeDescriptor desc : mimetypesAllowed.values()) {
				if (!first) {
					mimetypesBuffer.append(", ");
				}
				mimetypesBuffer.append(desc.getLabel());
				first = false;
			}
			
			throw new ClientException(
					"La pièce jointe ne peut pas être ajoutée. Le format n'est pas accepté pour cette communication. Les formats autorisés sont : "+mimetypesBuffer.toString());
		}
	}
}
