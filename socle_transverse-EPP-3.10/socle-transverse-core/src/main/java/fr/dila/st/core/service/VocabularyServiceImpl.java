/**
 * 
 */
package fr.dila.st.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DataModel;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.directory.BaseSession;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.ui.web.directory.DirectoryHelper;

import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.util.PropertyUtil;

/**
 * @author jgomez
 * 
 */
public class VocabularyServiceImpl implements VocabularyService {

	public static final String	UNKNOWN_ENTRY			= "**unknown entry**";

	private static final long	serialVersionUID		= 1L;
	private static final Log	LOGGER					= LogFactory.getLog(VocabularyService.class);

	private static final String	ERROR_DIR_EXCEPTION		= "Erreur lors de la fermeture du directory";
	private static final String	ERROR_CLIENT_EXCEPTION	= "Client Exception occured";

	/**
	 * Default constructor
	 */
	public VocabularyServiceImpl() {
		// do nothing
	}

	/**
	 * @see fr.dila.st.api.service.VocabularyService#checkData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean checkData(String directoryName, String columName, String valueToCheck) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("VocabularyService checkData : %s,/%s,%s", directoryName, columName,
					valueToCheck));
		}

		final DirectoryService directoryService = STServiceLocator.getDirectoryService();
		Session vocabularySession = null;
		Map<String, Serializable> filter = new HashMap<String, Serializable>();
		filter.put(columName, valueToCheck);
		try {
			vocabularySession = directoryService.open(directoryName);
			DocumentModelList listEntries = vocabularySession.query(filter);
			return listEntries != null && !listEntries.isEmpty();
		} catch (DirectoryException de) {
			LOGGER.error(ERROR_DIR_EXCEPTION, de);
		} catch (ClientException ce) {
			LOGGER.error(ERROR_CLIENT_EXCEPTION, ce);
		} finally {
			if (vocabularySession != null) {
				try {
					vocabularySession.close();
				} catch (DirectoryException e) {
					LOGGER.error(ERROR_DIR_EXCEPTION, e);
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * Méthode d'aide pour la suggestion
	 * 
	 * @param keyword
	 * @param dirSession
	 * @param schemaName
	 * @return
	 * @throws ClientException
	 */
	protected List<String> getSuggestions(String keyword, Session dirSession, String schemaName) throws ClientException {
		HashSet<String> keywordSet = new HashSet<String>();
		keywordSet.add(STVocabularyConstants.COLUMN_LABEL);
		Map<String, Serializable> filter = new HashMap<String, Serializable>();
		filter.put(STVocabularyConstants.COLUMN_LABEL, keyword);
		// récupération de la liste suggérée
		DocumentModelList docListResult = dirSession.query(filter, keywordSet);
		List<String> suggestionsList = new ArrayList<String>();
		String label = "";
		// récupération des mots-clés suggérés (label)
		for (DocumentModel doc : docListResult) {
			if (schemaName.equals(STVocabularyConstants.VOCABULARY)
					|| schemaName.equals(STVocabularyConstants.XVOCABULARY)) {
				DataModel mod = doc.getDataModel(schemaName);
				label = (String) mod.getData(STVocabularyConstants.COLUMN_LABEL);
				suggestionsList.add(label);
			}
		}
		return suggestionsList;
	}

	/**
	 * Méthode d'aide pour la suggestion
	 * 
	 * @param keyword
	 * @param dirSession
	 * @param schemaName
	 * @return DocumentModelList
	 * @throws DirectoryException
	 * @throws ClientException
	 */
	protected List<DocumentModel> getDocumentModelListSuggestions(String keyword, Session dirSession, String schemaName)
			throws ClientException {
		HashSet<String> keywordSet = new HashSet<String>();
		keywordSet.add(STVocabularyConstants.COLUMN_LABEL);
		Map<String, Serializable> filter = new HashMap<String, Serializable>();
		filter.put(STVocabularyConstants.COLUMN_LABEL, keyword);
		// récupération de la liste suggérée
		return dirSession.query(filter, keywordSet);
	}

	/**
	 * @see fr.dila.st.api.service.VocabularyService#getSuggestions(java.lang.String, java.lang.String)
	 */
	public List<String> getSuggestions(String keyword, String vocName) {
		final DirectoryService directoryService = STServiceLocator.getDirectoryService();
		Session session = null;
		try {
			session = directoryService.open(vocName);
			String schemaName = directoryService.getDirectorySchema(vocName);
			return this.getSuggestions(keyword, session, schemaName);
		} catch (DirectoryException de) {
			LOGGER.error(ERROR_DIR_EXCEPTION, de);
		} catch (ClientException ce) {
			LOGGER.error(ERROR_CLIENT_EXCEPTION, ce);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (DirectoryException e) {
					LOGGER.error(ERROR_DIR_EXCEPTION, e);
				}
			}
		}
		return null;
	}

	/**
	 * @see fr.dila.st.api.service.VocabularyService#getSuggestions(java.lang.String, java.util.List)
	 */

	public List<String> getSuggestions(String keyword, List<String> vocList) {
		List<String> resultList = new ArrayList<String>();
		for (String vocName : vocList) {
			resultList.addAll(getSuggestions(keyword, vocName));
		}
		return new ArrayList<String>(new HashSet<String>(resultList));
	}

	/*
	 * (non-Javadoc)
	 * @see fr.dila.st.api.service.VocabularyService#getDocumentModelListSuggestions(java.lang.String, java.lang.String)
	 */
	@Override
	public List<DocumentModel> getListDocumentModelSuggestions(String keyword, String vocName) {
		final DirectoryService directoryService = STServiceLocator.getDirectoryService();
		Session session = null;
		try {
			session = directoryService.open(vocName);
			String schemaName = directoryService.getDirectorySchema(vocName);
			return this.getDocumentModelListSuggestions(keyword, session, schemaName);
		} catch (DirectoryException de) {
			LOGGER.warn(ERROR_DIR_EXCEPTION, de);
		} catch (ClientException ce) {
			LOGGER.warn(ERROR_CLIENT_EXCEPTION, ce);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (DirectoryException e) {
					LOGGER.warn(ERROR_DIR_EXCEPTION, e);
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.dila.st.api.service.VocabularyService#getDocumentModelListSuggestions(java.lang.String, java.util.List)
	 */
	@Override
	public List<DocumentModel> getListDocumentModelSuggestions(String keyword, List<String> vocList) {
		List<DocumentModel> suggestionsDocList = new ArrayList<DocumentModel>();
		for (String vocName : vocList) {
			suggestionsDocList.addAll(getListDocumentModelSuggestions(keyword, vocName));
		}
		return suggestionsDocList;
	}

	@Override
	public String getEntryLabel(String directoryName, String entryId, String labelProperty) {
		final DirectoryService directoryService = STServiceLocator.getDirectoryService();
		// Récupère l'entrée
		DocumentModel doc = DirectoryHelper.getEntry(directoryName, entryId);
		String label = "";
		try {
			// Récupère le libellé
			String schema = directoryService.getDirectorySchema(directoryName);
			label = (String) doc.getProperty(schema, labelProperty);
		} catch (DirectoryException de) {
			label = UNKNOWN_ENTRY;
			LOGGER.warn(ERROR_DIR_EXCEPTION, de);
		} catch (ClientException ce) {
			label = UNKNOWN_ENTRY;
			LOGGER.warn(ERROR_CLIENT_EXCEPTION, ce);
		}

		return label;
	}

	@Override
	public String getEntryLabel(String directoryName, String entryId) {
		return getEntryLabel(directoryName, entryId, STVocabularyConstants.COLUMN_LABEL);
	}

	@Override
	public String getLabelFromId(String vocabularyDirectoryName, String idValue, String labelColumName) {
		final DirectoryService directoryService = STServiceLocator.getDirectoryService();
		LOGGER.debug("VocabularyService getLabelFromId : " + vocabularyDirectoryName + " / " + idValue + " / "
				+ labelColumName);
		Session vocabularySession = null;
		try {
			vocabularySession = directoryService.open(vocabularyDirectoryName);
			String schemaName = directoryService.getDirectorySchema(vocabularyDirectoryName);
			DocumentModel documentModel = vocabularySession.getEntry(idValue);

			if (documentModel != null) {
				return (String) documentModel.getProperty(schemaName, labelColumName);
			}

		} catch (Exception e) {
			LOGGER.error("Erreur lors de la récupération du label du vocbulaire à partir de son identifiant", e);
		} finally {
			if (vocabularySession != null) {
				try {
					vocabularySession.close();
				} catch (DirectoryException e) {
					// do nothing
					LOGGER.warn(ERROR_DIR_EXCEPTION, e);
				}
			}
		}
		return null;
	}

	@Override
	public DocumentModelList getAllEntry(String vocabularyDirectoryName) {
		final DirectoryService directoryService = STServiceLocator.getDirectoryService();
		Session vocabularySession = null;
		DocumentModelList list = null;
		try {
			vocabularySession = directoryService.open(vocabularyDirectoryName);
			list = vocabularySession.getEntries();

		} catch (DirectoryException de) {
			LOGGER.error(ERROR_DIR_EXCEPTION, de);
		} catch (ClientException ce) {
			LOGGER.error("Erreur lors de la récupération du vocabulaire", ce);
		} finally {
			if (vocabularySession != null) {
				try {
					vocabularySession.close();
				} catch (DirectoryException e) {
					// do nothing
					LOGGER.warn(ERROR_DIR_EXCEPTION, e);
				}
			}
		}
		return list;

	}

	@Override
	public List<DocumentModel> getEntries(String vocabularyDirectoryName, List<String> ids) {
		final DirectoryService directoryService = STServiceLocator.getDirectoryService();
		Session vocabularySession = null;
		List<DocumentModel> list = new ArrayList<DocumentModel>();
		try {
			vocabularySession = directoryService.open(vocabularyDirectoryName);
			for (String id : ids) {
				list.add(vocabularySession.getEntry(id));
			}
		} catch (Exception e) {
			LOGGER.error("Erreur lors de la récupération du vocabulaire", e);
		} finally {
			if (vocabularySession != null) {
				try {
					vocabularySession.close();
				} catch (DirectoryException e) {
					// do nothing
					LOGGER.warn(ERROR_DIR_EXCEPTION, e);
				}
			}
		}
		return list;

	}

	@Override
	public DocumentModel getNewEntry(String vocabularyDirectoryName) throws ClientException {
		final DirectoryService directoryService = STServiceLocator.getDirectoryService();
		Session vocabularySession = null;
		DocumentModel creationDirectoryEntry = null;

		try {
			vocabularySession = directoryService.open(vocabularyDirectoryName);
			String schema = directoryService.getDirectorySchema(vocabularyDirectoryName);
			creationDirectoryEntry = BaseSession.createEntryModel(null, schema, null, null);
		} catch (DirectoryException e) {
			throw new ClientException(e);
		} finally {
			if (vocabularySession != null) {
				vocabularySession.close();
			}
		}
		return creationDirectoryEntry;
	}

	@Override
	public void createDirectoryEntry(String vocabularyDirectoryName, DocumentModel creationDirectoryEntry)
			throws ClientException {
		final DirectoryService dirService = STServiceLocator.getDirectoryService();
		Session dirSession = null;
		try {
			// check if entry already exists
			// String schema = dirService.getDirectorySchema(vocabularyDirectoryName);
			// String idField = dirService.getDirectoryIdField(vocabularyDirectoryName);
			// Object id = creationDirectoryEntry.getProperty(schema, idField);
			dirSession = dirService.open(vocabularyDirectoryName);
			// if (id instanceof String && dirSession.hasEntry((String) id)) {
			// facesMessages.add(FacesMessage.SEVERITY_ERROR,
			// resourcesAccessor.getMessages().get(
			// "vocabulary.entry.identifier.already.exists"));
			// return;
			// }
			dirSession.createEntry(creationDirectoryEntry);
		} catch (DirectoryException e) {
			throw new ClientException(e);
		} finally {
			if (dirSession != null) {
				dirSession.close();
			}
		}
	}

	@Override
	public boolean hasDirectoryEntry(String vocabularyDirectoryName, String entryId) throws ClientException {
		final DirectoryService dirService = STServiceLocator.getDirectoryService();
		Session dirSession = null;
		try {
			// check if entry already exists
			dirSession = dirService.open(vocabularyDirectoryName);
			if (dirSession.hasEntry(entryId)) {
				return true;
			}
			return false;
		} catch (DirectoryException e) {
			throw new ClientException(e);
		} finally {
			if (dirSession != null) {
				dirSession.close();
			}
		}
	}

	@Override
	public void createEntryIfNotExists(String directorySchema, String directoryName, String entryId)
			throws ClientException {

		if (directoryName == null || entryId == null) {
			return;
		}

		if (directorySchema == null) {
			directorySchema = STVocabularyConstants.VOCABULARY;
		}

		if (!hasDirectoryEntry(directoryName, entryId)) {
			DocumentModel newEntry = getNewEntry(directoryName);
			PropertyUtil.setProperty(newEntry, directorySchema, STVocabularyConstants.COLUMN_ID, entryId);
			PropertyUtil.setProperty(newEntry, directorySchema, STVocabularyConstants.COLUMN_LABEL, entryId);
			createDirectoryEntry(directoryName, newEntry);
		}
	}
}
