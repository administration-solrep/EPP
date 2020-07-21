/**
 * 
 */
package fr.dila.st.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 * Service de vocabulaire du socle transverse, contient des fonctions pour faciliter les recherches et les tests sur des
 * vocabulaires ou directory.
 * 
 * @author jgomez
 */
public interface VocabularyService extends Serializable {

	/**
	 * Renvoie vraie si la donnée valueToCheck est présente dans le vocabulaire à la colonne donnée.
	 * 
	 * @param vocabularyDirectoryName
	 *            Le nom du vocabulaire
	 * @param columName
	 *            La valeur de la colonne
	 * @param valueToCheck
	 *            La valeur à tester
	 * @return vraie si la donnée est présente
	 */
	boolean checkData(String vocabularyDirectoryName, String columName, String valueToCheck);

	/**
	 * Ramene la liste des index commencant par keyword ( sur la colonne 'label')
	 * 
	 * @param keyword
	 *            : la chaine de caractere de recherche.
	 * @param vocList
	 *            : la liste des vocabulaires sur lesquels on veut effectuer la recherche.
	 * @return la liste des résultats commencant par keyword, sur le vocabulaire demande
	 * 
	 */
	List<String> getSuggestions(String keyword, List<String> vocList);

	/**
	 * Ramene la liste des mots-clés commencant par keyword ( sur la colonne 'label')
	 * 
	 * @param keyword
	 *            : la chaine de caractere de recherche.
	 * @param vocName
	 *            : le nom du vocabulaire sur lequel on veut effectuer la recherche.
	 * @return la liste des résultats commencant par keyword, sur le vocabulaire demande
	 */
	List<String> getSuggestions(String keyword, String vocName);

	/**
	 * Ramene la liste de DocumentModel contenant les mots-clés commencant par keyword ( sur la colonne 'label')
	 * 
	 * @param keyword
	 *            : la chaine de caractere de recherche.
	 * @param vocName
	 *            : le nom du vocabulaire sur lequel on veut effectuer la recherche.
	 * @return la liste des résultats commencant par keyword, sur le vocabulaire demande
	 * 
	 */
	List<DocumentModel> getListDocumentModelSuggestions(String keyword, String vocName);

	/**
	 * Ramene la Liste de DocumentModel contenant les mots-clés commencant par keyword ( sur la colonne 'label')
	 * 
	 * @param keyword
	 *            : la chaine de caractere de recherche.
	 * @param vocList
	 *            : la liste des vocabulaires sur lesquels on veut effectuer la recherche.
	 * @return la liste des résultats commencant par keyword, sur le vocabulaire demande
	 */
	List<DocumentModel> getListDocumentModelSuggestions(String keyword, List<String> vocList);

	/**
	 * Retourne le libellé d'une entrée dans un répertoire (n'échoue jamais).
	 * 
	 * @param directoryName
	 *            Nom du répertoire
	 * @param entryId
	 *            Identifiant technique de l'entrée
	 * @param labelProperty
	 *            Nom le l'attribut comportant le libellé
	 * @return Libellé
	 */
	String getEntryLabel(String directoryName, String entryId, String labelProperty);

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
	String getEntryLabel(String directoryName, String entryId);

	/**
	 * Récupère le label du vocabulaire à partir de son identifiant, du nom de la colone label et du nom du directory.
	 * 
	 * @param vocabularyDirectoryName
	 * @param idValue
	 * @param labelColumName
	 * @return
	 */
	String getLabelFromId(String vocabularyDirectoryName, String idValue, String labelColumName);

	/**
	 * Retourne tou le vocabulaire
	 * 
	 * @param string
	 */
	DocumentModelList getAllEntry(String vocabularyDirectoryName);

	/**
	 * Retourne un document model pour créer une entrée
	 * 
	 * @param vocabularyDirectoryName
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getNewEntry(String vocabularyDirectoryName) throws ClientException;

	/**
	 * Créer une nouvelle entrée dans le vocabulaire
	 * 
	 * @param vocabularyDirectoryName
	 * @param creationDirectoryEntry
	 * @throws ClientException
	 */
	void createDirectoryEntry(String vocabularyDirectoryName, DocumentModel creationDirectoryEntry)
			throws ClientException;

	/**
	 * Verifie si l'entrée existe
	 * 
	 * @param vocabularyDirectoryName
	 * @param anid
	 * @return
	 * @throws ClientException
	 */
	boolean hasDirectoryEntry(String vocabularyDirectoryName, String anid) throws ClientException;

	/**
	 * Retourne les entry correspondant aux ids
	 * 
	 * @param vocabularyDirectoryName
	 * @param ids
	 * @return
	 */
	List<DocumentModel> getEntries(String vocabularyDirectoryName, List<String> ids);

	/**
	 * Ajoute une entrée dans un vocabulaire si elle n'existe pas déjà, en lui spécifiant un schéma. utile pour les
	 * vocabulaires hiérarchiques qui utilisent le schéma xvocabulary au lieu de vocabulary
	 * 
	 * @param directoryName
	 * @param directorySchema
	 * @param anid
	 * @throws ClientException
	 */
	void createEntryIfNotExists(String directorySchema, String directoryName, String entryId) throws ClientException;
}
