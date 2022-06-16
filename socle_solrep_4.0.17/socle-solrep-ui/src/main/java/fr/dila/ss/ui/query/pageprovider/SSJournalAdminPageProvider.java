package fr.dila.ss.ui.query.pageprovider;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import fr.dila.ss.ui.bean.JournalDossierResultList;
import fr.dila.ss.ui.services.SSJournalUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.helper.PaginationHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.STExcelUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.audit.api.LogEntry;

/**
 * page provider du journal affiche dans l'espace d'administration
 *
 * @author BBY, ARN
 */
public abstract class SSJournalAdminPageProvider extends AbstractJournalPageProvider {
    private static final long serialVersionUID = 1L;

    private static final int HEADER_ROW_SIZE = 1;

    protected transient List<String> dossierIdList = null;

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) {
        errorMessage = null;
        error = null;

        // ajout des filtres
        Map<String, Object> mapFilter = new HashMap<>();
        mapFilter.put(STConstant.FILTER_CATEGORY, parameters[0]);
        mapFilter.put(STConstant.FILTER_USER, parameters[1]);
        mapFilter.put(STConstant.FILTER_DATE_DEBUT, parameters[2]);
        if (parameters.length >= 6) {
            mapFilter.put(STConstant.FILTER_COMMENT, formatComment(parameters[5]));
        }

        if (
            mapFilter.get(STConstant.FILTER_CATEGORY) == null ||
            mapFilter.get(STConstant.FILTER_CATEGORY) instanceof ArrayList<?> &&
            ((ArrayList<?>) mapFilter.get(STConstant.FILTER_CATEGORY)).isEmpty() ||
            mapFilter.get(STConstant.FILTER_CATEGORY) instanceof String &&
            StringUtils.isEmpty((String) mapFilter.get(STConstant.FILTER_CATEGORY))
        ) {
            // si pas de category on filtre sur les categories que l'utilisateur
            // peut voir
            Set<String> categoryList = getJournalUIService().getCategoryList();
            mapFilter.put(STConstant.FILTER_LIST_CATEGORY, categoryList);
        }

        // pour la date de fin on se place a 23h55m59s999
        Calendar cal = Calendar.getInstance();
        Date date = (Date) parameters[3];
        if (date != null) {
            cal.setTime(date);
        }
        DateUtil.setDateToEndOfDay(cal);

        mapFilter.put(STConstant.FILTER_DATE_FIN, cal.getTime());

        getDossierIdsList();

        currentItems = new ArrayList<>();

        // On vérifie si un dossier était recherché
        String dossierIdRecherche = (String) parameters[4];
        // Si il y avait bien un dossier recherché, mais que la liste des
        // dossierId est vide on ne retourne rien
        // (recherche d'un dossier inexistant)
        if (isEmpty(dossierIdList) && isNotEmpty(dossierIdRecherche)) {
            // Il n'y a pas de résultats
            resultsCount = 0;
        } else {
            // Sinon, il y a des résultats potentiels et on les recherche
            JournalService journalService = STServiceLocator.getJournalService();
            // XXX optimize events count
            resultsCount = journalService.getEventsCount(dossierIdList, mapFilter);

            if (resultsCount > 0) {
                // récupération page courante
                int pageNumber = PaginationHelper.calculePageNumber(offset, getPageSize(), resultsCount);

                List<LogEntry> logEntries = journalService.queryDocumentAllLogs(
                    dossierIdList,
                    mapFilter,
                    pageNumber,
                    (int) pageSize,
                    sortInfos
                );
                if (logEntries != null) {
                    for (LogEntry entry : logEntries) {
                        Map<String, Serializable> fieldMap = new HashMap<>();
                        fillMap(fieldMap, entry);
                        fieldMap.put("dossierRef", getDossierRef(coreSession, entry));
                        currentItems.add(fieldMap);

                    }
                }
            }
        }
    }

    protected SSJournalUIService<JournalDossierResultList> getJournalUIService() {
        return SSUIServiceLocator.getJournalUIService();
    }

    protected abstract void getDossierIdsList();

    protected String formatComment(Object comment) {
        return comment == null ? null : comment.toString();
    }

    protected abstract String getDossierRef(CoreSession session, LogEntry entry);

    public void setForceNonPaginatedForExcel() {
        this.offset = 0;
        this.pageSize = STExcelUtil.XLS_ROW_LIMIT - HEADER_ROW_SIZE;
    }
}
