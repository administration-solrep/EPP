package fr.dila.ss.ui.query.pageprovider;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_CATEGORY;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_COMMENT;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_DOC_LIFE_CYCLE;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_DOC_PATH;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_DOC_TYPE;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_DOC_UUID;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_EVENT_DATE;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_EVENT_ID;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_PRINCIPAL_NAME;

import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.ui.contentview.AbstractDTOPageProvider;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import org.nuxeo.ecm.platform.audit.api.ExtendedInfo;
import org.nuxeo.ecm.platform.audit.api.LogEntry;

/**
 * page provider du journal affiche dans le dossier
 *
 * @author BBY, ARN
 *
 */
public abstract class AbstractJournalPageProvider extends AbstractDTOPageProvider {
    private static final long serialVersionUID = 1L;

    @Override
    protected void buildQuery() {
        query = "";
    }

    protected void fillMap(Map<String, Serializable> fieldMap, LogEntry entry) {
        fieldMap.put(LOG_PRINCIPAL_NAME, entry.getPrincipalName());
        fieldMap.put(LOG_EVENT_ID, entry.getEventId());
        fieldMap.put(LOG_EVENT_DATE, entry.getEventDate());
        fieldMap.put(LOG_DOC_UUID, entry.getDocUUID());
        fieldMap.put(LOG_DOC_TYPE, entry.getDocType());
        // entrée contenant les informations sur les profils de
        // l'utilisateur
        fieldMap.put(LOG_DOC_PATH, entry.getDocPath());
        fieldMap.put(LOG_CATEGORY, SSUIServiceLocator.getJournalUIService().getCategoryLabel(entry.getCategory()));
        fieldMap.put(LOG_COMMENT, entry.getComment());
        fieldMap.put(LOG_DOC_LIFE_CYCLE, entry.getDocLifeCycle());
    }

    protected String getUserId(LogEntry log) {
        ExtendedInfo loginInfo = log.getExtendedInfos().get("login");
        return Optional
            .ofNullable(loginInfo)
            .map(ExtendedInfo::getSerializableValue)
            .map(Serializable::toString)
            .orElseGet(() -> EMPTY);
    }
}
