package fr.dila.st.web.vocabulary;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * WebBean qui permet de récupérer des information sur un vocabulaire sans utiliser le widget selectOneDirectory ou les
 * tags nxdir.
 * 
 * @author arolin
 */
@Name("vocabulary")
@Scope(ScopeType.EVENT)
public class VocabularyBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Retourne le libellé d'une entrée dans un répertoire (n'échoue jamais). Utilise l'attribut libellé par défaut
	 * ("label").
	 * 
	 * @param directoryName
	 *            Nom du répertoire
	 * @param entryId
	 *            Identifiant technique de l'entrée
	 * @return Libellé
	 */
	public String getEntryLabel(String directoryName, String entryId) {
		if (directoryName != null && !directoryName.isEmpty() && entryId != null && !entryId.isEmpty()) {
			VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
			return vocabularyService.getEntryLabel(directoryName, entryId);
		}
		return null;
	}

	/**
	 * Retourne toutes les entrées d'un vocabulaire.
	 * 
	 * @param directoryName
	 *            Nom du répertoire
	 * @return Liste de documents
	 */
	public List<DocumentModel> getAllEntry(String directoryName) {
		VocabularyService vocabularyService = STServiceLocator.getVocabularyService();

		return vocabularyService.getAllEntry(directoryName);
	}
}
