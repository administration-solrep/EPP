package fr.dila.st.core.vocabulary;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.vocabulary.VocabularyConnector;

/**
 * Wrapper autour d'un vocabulaire (ou d'un directory). L'objet est censé être contruit à partir du service de
 * vocabulaire.
 * 
 * @author jgomez
 * 
 */
public class VocabularyConnectorImpl implements VocabularyConnector {

	protected String			directoryName;

	protected VocabularyService	service	= null;

	public VocabularyConnectorImpl(String directoryName, VocabularyService service) throws ClientException {
		super();
		if (service == null) {
			throw new ClientException("Le service ne peut pas être nul");
		}
		this.service = service;
		this.directoryName = directoryName;
	}

	/**
	 * @see VocabularyConnector#getSuggestion(String)
	 */
	public Boolean check(String valueToCheck) {
		return service.checkData(directoryName, STVocabularyConstants.COLUMN_LABEL, valueToCheck);
	}

	/**
	 * @see VocabularyConnector#getSuggestion(String)
	 */
	public List<String> getSuggestion(String suggestion) {
		return service.getSuggestions(suggestion, directoryName);
	}
}
