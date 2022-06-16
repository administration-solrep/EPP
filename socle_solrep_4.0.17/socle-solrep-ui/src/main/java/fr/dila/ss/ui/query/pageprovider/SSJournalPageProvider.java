package fr.dila.ss.ui.query.pageprovider;

import static fr.dila.ss.ui.services.SSUIServiceLocator.getJournalUIService;
import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.LOG_EXCEPTION_TEC;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.helper.PaginationHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.audit.api.LogEntry;

/**
 * page provider du journal affiche dans le dossier
 *
 * @author BBY, ARN
 *
 */
public class SSJournalPageProvider extends AbstractJournalPageProvider {
    public static final String CURRENT_DOCUMENT_PROPERTY = "currentDocument";

    private static final long serialVersionUID = 1L;

    private static final STLogger LOG = STLogFactory.getLog(SSJournalPageProvider.class);

    protected transient JournalService journalService;

    protected Date dateStart = null;

    /**
     * Default constructor
     */
    public SSJournalPageProvider() {
        super();
    }

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) {
        errorMessage = null;
        error = null;
        try {
            // récupération du dossier et des propriétés associées
            final Map<String, Serializable> props = getProperties();
            DocumentModel currentDocumentModel = (DocumentModel) props.get(CURRENT_DOCUMENT_PROPERTY);
            List<String> dossierIdList = new ArrayList<>();
            if (currentDocumentModel != null) {
                // récupération de l'id du dossier
                dossierIdList.add(currentDocumentModel.getId());
            }

            // ajout des filtres
            final Map<String, Object> mapFilter = new HashMap<>();
            mapFilter.put(STConstant.FILTER_CATEGORY, parameters[0]);
            mapFilter.put(STConstant.FILTER_USER, parameters[1]);
            mapFilter.put(STConstant.FILTER_DATE_DEBUT, parameters[2]);

            if (StringUtils.isEmpty((String) mapFilter.get(STConstant.FILTER_CATEGORY))) {
                // si pas de category on filtre sur les categories que
                // l'utilisateur peut voir
                mapFilter.put(STConstant.FILTER_LIST_CATEGORY, getJournalUIService().getCategoryList());
            }

            final Calendar cal = Calendar.getInstance();
            final Date date = (Date) parameters[3];
            if (date != null) {
                cal.setTime(date);
            }
            DateUtil.setDateToEndOfDay(cal);

            mapFilter.put(STConstant.FILTER_DATE_FIN, cal.getTime());

            journalService = STServiceLocator.getJournalService();
            resultsCount = journalService.getEventsCount(dossierIdList, mapFilter);
            currentItems = new ArrayList<>();

            if (resultsCount > 0) {
                // récupération page courante
                int pageNumber = PaginationHelper.calculePageNumber(offset, getPageSize(), resultsCount);

                final List<LogEntry> logEntries = journalService.queryDocumentAllLogs(
                    dossierIdList,
                    mapFilter,
                    pageNumber,
                    (int) pageSize,
                    sortInfos
                );

                if (logEntries != null) {
                    Map<String, Serializable> fieldMap = null;
                    for (LogEntry entry : logEntries) {
                        fieldMap = new HashMap<>();
                        fillMap(fieldMap, entry);
                        currentItems.add(fieldMap);
                    }
                }
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            error = e;
            LOG.error(coreSession, LOG_EXCEPTION_TEC, e);
        }
    }
}
