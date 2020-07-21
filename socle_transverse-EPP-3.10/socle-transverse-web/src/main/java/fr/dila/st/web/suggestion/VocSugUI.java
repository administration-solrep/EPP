package fr.dila.st.web.suggestion;

import java.util.List;

/**
 * Classe utilitaire pour chercher et récupérer les informations d'un vocabulaire suggéré dans l'UI.
 * 
 * @author JGZ & ARN
 * 
 */
public abstract class VocSugUI {

	/**
	 * Nombre maximum de suggestions affichées.
	 */
	protected static final int	MAX_RESULT	= 10;

	/**
	 * Label du mot clé du vocabulaire qui va être affiché et enregistré
	 */
	protected String			motCleLabel;

	/**
	 * Id du mot clé du vocabulaire qui va être affiché et enregistré
	 */
	protected String			motCleId;

	protected String			vocabularyLabel;

	public VocSugUI(String vocId) {
		vocabularyLabel = vocId;
	}

	public void reset() {
		motCleLabel = null;
		motCleId = null;
	}

	public String getMotCleLabel() {
		return motCleLabel;
	}

	public void setMotCleLabel(String motCleLabel) {
		this.motCleLabel = motCleLabel;
	}

	public String getMotCleId() {
		return motCleId;
	}

	public void setMotCleId(String motCleId) {
		this.motCleId = motCleId;
	}

	/**
	 * Récupère le label du vocabulaire.
	 */
	public String getVocabularyLabel() {
		return this.vocabularyLabel;
	}

	public void setVocabularyLabel(String vocabularyLabel) {
		this.vocabularyLabel = vocabularyLabel;
	}

	/**
	 * retourne les suggestions appropriées en fonctions des caractères choisi et du vocabulaire. A implémenter
	 * différemment dans chaque projet !
	 * 
	 * @param input
	 * 
	 * @return List<String>
	 */
	abstract public List<String> getSuggestions(Object input);

	// abstract public List<DocumentModel> getDocumentModelListSuggestions(Object input);
}
