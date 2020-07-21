package fr.dila.solonepp.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.ui.web.directory.VocabularyEntry;

import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.service.SolonEppVocabularyService;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.VocabularyServiceImpl;
import fr.dila.st.core.util.PropertyUtil;

public class SolonEppVocabularyServiceImpl extends VocabularyServiceImpl implements SolonEppVocabularyService {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -7106239741594247208L;

	private static final String RUBRIQUES_DIRECTORY = "rubriques";
	private static final String RUBRIQUES_VOCABULARY = "vocabularyRubrique";
	
	private static final STLogger LOGGER = STLogFactory.getLog(SolonEppVocabularyServiceImpl.class);
	
	@Override
	public List<VocabularyEntry> getRubriquesEntriesForEmetteur(final InstitutionsEnum emetteurs) {
		return getRubriquesEntriesForEmetteurs(Collections.singletonList(emetteurs));
	}
	
	@Override
	public List<VocabularyEntry> getRubriquesEntriesForEmetteurs(final Collection<InstitutionsEnum> emetteurs) {
		
		final DirectoryService directoryService = STServiceLocator.getDirectoryService();
		Session vocabularySession = null;
		final List<VocabularyEntry> entries = new ArrayList<VocabularyEntry>();
		try {
			vocabularySession = directoryService.open(RUBRIQUES_DIRECTORY);
			Map<String, String> orderBy = new HashMap<String, String>();
			orderBy.put(STVocabularyConstants.COLUMN_LABEL, "ASC");
			Set<String> filter = Collections.emptySet();
			DocumentModelList docs = vocabularySession.query(getRubriquesFiltersFromEmmetteurs(emetteurs), filter, orderBy);
			for (DocumentModel doc : docs) {
				entries.add(new VocabularyEntry(doc.getId(), PropertyUtil.getStringProperty(doc, RUBRIQUES_VOCABULARY, STVocabularyConstants.COLUMN_LABEL)));
			}
		} catch (DirectoryException dex) {
			LOGGER.error(STLogEnumImpl.FAIL_OPEN_DIRECTORY_TEC, dex);
		} catch (ClientException cex) {
			LOGGER.error(STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, cex);
		} finally {
			if (vocabularySession != null) {
				try {
					vocabularySession.close();
				} catch (DirectoryException dex) {
					LOGGER.error(STLogEnumImpl.FAIL_CLOSE_DIRECTORY_TEC, dex);
				}
			}
		}
		
		return entries;
	}
		
	private Map<String, Serializable> getRubriquesFiltersFromEmmetteurs(final Collection<InstitutionsEnum> emetteurs) {
		Map<String, Serializable> filters = new HashMap<String, Serializable>();
		for (InstitutionsEnum inst : emetteurs) {
			filters.put(inst.getColumnRubrique(), true);
		}
		return filters;
	}

	@Override
	public String getRubriqueIdForLabel(String label) throws ClientException {
		List<DocumentModel> rubriqueSuggestions = getListDocumentModelSuggestions(label,
				SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY);
		for (DocumentModel suggestion : rubriqueSuggestions) {
			String rubriqueId = suggestion.getTitle();
			if (getLabelFromId(SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY, rubriqueId,
					STVocabularyConstants.COLUMN_LABEL).equals(label)) {
				return rubriqueId;
			}
		}
		return null;
	}

}
