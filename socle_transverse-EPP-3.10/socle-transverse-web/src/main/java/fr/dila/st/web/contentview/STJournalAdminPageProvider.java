package fr.dila.st.web.contentview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.audit.api.LogEntry;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.journal.STJournalActions;

/**
 * page provider du journal affiche dans l'espace d'administration
 * 
 * @author BBY, ARN
 * 
 */
public abstract class STJournalAdminPageProvider extends AbstractDTOPageProvider {

	private static final long	serialVersionUID		= 1L;

	public static final String	JOURNAL_ACTION_PROPERTY	= "journalActions";

	protected JournalService	journalService;

	protected List<String>		dossierIdList			= null;

	@Override
	protected void fillCurrentPageMapList(CoreSession coreSession) throws ClientException {
		errorMessage = null;
		error = null;

		Map<String, Serializable> props = getProperties();

		journalService = STServiceLocator.getJournalService();

		// récupération page courante
		int pageNumber = 1 + (int) (offset / pageSize);

		// ajout des filtres
		Map<String, Object> mapFilter = new HashMap<String, Object>();
		mapFilter.put(STConstant.FILTER_CATEGORY, getParameters()[0]);
		mapFilter.put(STConstant.FILTER_USER, getParameters()[1]);
		mapFilter.put(STConstant.FILTER_DATE_DEBUT, getParameters()[2]);
		if (getParameters().length >= 6) {
			mapFilter.put(STConstant.FILTER_COMMENT, formatComment(getParameters()[5]));
		}

		if (props.get(JOURNAL_ACTION_PROPERTY) != null && (mapFilter.get(STConstant.FILTER_CATEGORY) == null
				|| mapFilter.get(STConstant.FILTER_CATEGORY) instanceof ArrayList<?>
						&& ((ArrayList<?>) mapFilter.get(STConstant.FILTER_CATEGORY)).isEmpty()
				|| mapFilter.get(STConstant.FILTER_CATEGORY) instanceof String
						&& StringUtils.isEmpty((String) mapFilter.get(STConstant.FILTER_CATEGORY)))) {
			STJournalActions bean = (STJournalActions) props.get(JOURNAL_ACTION_PROPERTY);
			// si pas de categoty on filtre sur les categories que l'utilisateur peut voir
			mapFilter.put(STConstant.FILTER_LIST_CATEGORY, bean.getCategoryList());
		}

		// pour la date de fin on se place a 23h55m59s999
		Calendar cal = Calendar.getInstance();
		Date date = (Date) getParameters()[3];
		if (date != null) {
			cal.setTime(date);
		}
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		mapFilter.put(STConstant.FILTER_DATE_FIN, cal.getTime());

		getDossierIdsList();

		currentItems = new ArrayList<Map<String, Serializable>>();

		// On vérifie si un dossier était recherché
		String dossierIdRecherche = (String) getParameters()[4];
		// Si il y avait bien un dossier recherché, mais que la liste des dossierId est vide on ne retourne rien
		// (recherche d'un dossier inexistant)
		if (dossierIdList == null && StringUtils.isNotEmpty(dossierIdRecherche)) {
			// Il n'y a pas de résultats
			resultsCount = 0;
		} else {
			// Sinon, il y a des résultats potentiels et on les recherche
			resultsCount = journalService.getEventsCount(dossierIdList, mapFilter, pageNumber, (int) pageSize);

			if (resultsCount > 0) {
				List<LogEntry> logEntries = journalService.queryDocumentAllLogs(dossierIdList, mapFilter, pageNumber,
						(int) pageSize, sortInfos);
				if (logEntries != null) {
					Calendar calendar = Calendar.getInstance();
					for (LogEntry entry : logEntries) {
						Map<String, Serializable> fieldMap = new HashMap<String, Serializable>();
						fieldMap.put("principalName", entry.getPrincipalName());
						fieldMap.put("eventId", entry.getEventId());

						calendar.setTime(entry.getEventDate());
						fieldMap.put("eventDate", calendar.getTime());

						fieldMap.put("docUUID", entry.getDocUUID());
						fieldMap.put("docType", entry.getDocType());
						// entrée contenant les informations sur les profils de l'utilisateur
						fieldMap.put("docPath", entry.getDocPath());
						fieldMap.put("category", entry.getCategory());
						fieldMap.put("comment", entry.getComment());
						fieldMap.put("docLifeCycle", entry.getDocLifeCycle());

						currentItems.add(fieldMap);
					}
				}
			}
		}

	}

	@Override
	protected void buildQuery() {
		query = "";
	}

	protected abstract void getDossierIdsList() throws ClientException;

	protected String formatComment(Object comment) {
		return comment == null ? null : comment.toString();
	}

	@Override
	public void setSearchDocumentModel(DocumentModel searchDocumentModel) {
		// remise en place du bug nuxeo pour forcer tout le temps le refresh
		this.searchDocumentModel = searchDocumentModel;
		refresh();
	}

}
