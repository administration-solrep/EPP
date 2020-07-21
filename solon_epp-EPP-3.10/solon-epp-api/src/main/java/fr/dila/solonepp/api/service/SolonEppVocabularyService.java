package fr.dila.solonepp.api.service;

import java.util.Collection;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.ui.web.directory.VocabularyEntry;

import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.st.api.service.VocabularyService;

public interface SolonEppVocabularyService extends VocabularyService {

	/**
	 * Retourne les entries correspondant à l'emetteur 
	 * 
	 * @param vocabularyDirectoryName
	 * @param emetteur
	 * @return
	 */
	List<VocabularyEntry> getRubriquesEntriesForEmetteur(InstitutionsEnum emetteur);
	
	/**
	 * Retourne les entries correspondant aux emetteurs 
	 * 
	 * @param vocabularyDirectoryName
	 * @param emetteurs
	 * @return
	 */
	List<VocabularyEntry> getRubriquesEntriesForEmetteurs(Collection<InstitutionsEnum> emetteurs);

	/**
	 * Retourne l'id de la rubrique corespondant au label
	 * 
	 * @param label
	 * @return
	 * @throws ClientException
	 */
	String getRubriqueIdForLabel(String label) throws ClientException;
}
