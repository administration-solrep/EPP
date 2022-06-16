package fr.dila.st.core.event;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STRechercheExportEventConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.STExcelUtil;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.core.util.StringHelper;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

public class STSendSearchUsersResultsListener implements PostCommitEventListener {
    private static final STLogger LOGGER = STLogFactory.getLog(STSendSearchUsersResultsListener.class);
    private static final String EXPORT_FILENAME = "export_resultat_recherche_utilisateurs.xls";

    @Override
    public void handleEvent(EventBundle events) {
        if (!events.containsEventName(STRechercheExportEventConstants.EXPORT_USER_SEARCH_EVENT)) {
            return;
        }
        for (Event event : events) {
            if (STRechercheExportEventConstants.EXPORT_USER_SEARCH_EVENT.equals(event.getName())) {
                handleEvent(event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleEvent(Event event) {
        EventContext eventCtx = event.getContext();

        // récupération des propriétés de l'événement
        Map<String, Serializable> eventProperties = eventCtx.getProperties();
        List<DocumentModel> usersDocs = (List<DocumentModel>) eventProperties.get(
            STRechercheExportEventConstants.PARAM_DOCUMENT_MODEL_LIST
        );

        NuxeoPrincipal principal = eventCtx.getPrincipal();
        String recipient = principal.getEmail();

        CoreSession session = eventCtx.getCoreSession();
        DataSource excelFile = STExcelUtil.exportResultUserSearch(session, usersDocs);

        if (excelFile != null) {
            STParametreService paramService = STServiceLocator.getSTParametreService();
            String object = paramService.getParametreValue(session, STParametreConstant.OBJET_MAIL_EXPORT_USERS_NAME);
            String content = StringHelper.renderFreemarker(
                paramService.getParametreValue(session, STParametreConstant.CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME),
                ImmutableMap.of("date_demande", SolonDateConverter.getClientConverter().formatNow())
            );

            try {
                STServiceLocator
                    .getSTMailService()
                    .sendMailWithAttachement(
                        Collections.singletonList(recipient),
                        object,
                        content,
                        EXPORT_FILENAME,
                        excelFile
                    );
            } catch (Exception exc) {
                LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
            }
        } else {
            LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, "L'export de la recherche utilisateurs est null");
        }
    }
}
