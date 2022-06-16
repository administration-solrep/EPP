package fr.dila.ss.core.event;

import com.google.common.collect.ImmutableMap;
import fr.dila.ss.api.constant.SSParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.CoreSessionUtil;
import fr.dila.st.core.util.STMailHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.core.util.StringHelper;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.stream.StreamSupport;
import javax.activation.DataSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

public abstract class AbstractExportDossierListener implements PostCommitEventListener {
    private static final STLogger LOGGER = STLogFactory.getLog(AbstractExportDossierListener.class);
    private static final int DEFAULT_MAX_ITEMS_TO_EXPORT = 5000;

    private final String eventName;
    private final String exportFilename;
    private int maxItemsToExport = DEFAULT_MAX_ITEMS_TO_EXPORT;

    protected AbstractExportDossierListener(String eventName, String exportFilename) {
        this.eventName = eventName;
        this.exportFilename = exportFilename;
    }

    protected int getMaxItemsToExport() {
        return maxItemsToExport;
    }

    @Override
    public void handleEvent(EventBundle events) {
        if (events.containsEventName(eventName)) {
            StreamSupport
                .stream(events.spliterator(), false)
                .filter(event -> eventName.equals(event.getName()))
                .forEach(this::handleSearchResultEvent);
        }
    }

    private void handleSearchResultEvent(Event event) {
        LOGGER.info(STLogEnumImpl.START_EVENT_TEC, "Début de l'export " + eventName);
        Instant startTime = Instant.now();

        EventContext eventCtx = event.getContext();
        CoreSession session = eventCtx.getCoreSession();
        NuxeoPrincipal principal = eventCtx.getPrincipal();
        String username = principal.getName();
        String mail = principal.getEmail();

        STParametreService paramService = STServiceLocator.getSTParametreService();
        String objet = paramService.getParametreValue(session, SSParametreConstant.OBJET_MAIL_EXPORT_NAME);
        Map<String, Object> corpsParamMap = ImmutableMap.of(
            "date_demande",
            SolonDateConverter.getClientConverter().formatNow()
        );
        String corpsErrorTemplate = paramService.getParametreValue(
            session,
            SSParametreConstant.CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME
        );

        try (CloseableCoreSession requeteUserSession = CoreSessionUtil.openSession(session, username)) {
            maxItemsToExport =
                NumberUtils.createInteger(
                    paramService.getParametreValue(
                        requeteUserSession,
                        SSParametreConstant.EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME
                    )
                );

            long nbDossiersToExport = countItemsToExport(requeteUserSession, eventCtx.getProperties());

            if (nbDossiersToExport <= maxItemsToExport) {
                DataSource fichierExcelResultat = processExport(requeteUserSession, eventCtx.getProperties());

                if (fichierExcelResultat != null) {
                    LOGGER.debug(STLogEnumImpl.CREATE_FILE_TEC, "Fichier excel généré, on envoie le mail");
                    STServiceLocator
                        .getSTMailService()
                        .sendMailWithAttachement(
                            Collections.singletonList(mail),
                            objet,
                            StringHelper.renderFreemarker(
                                paramService.getParametreValue(
                                    requeteUserSession,
                                    SSParametreConstant.CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME
                                ),
                                corpsParamMap
                            ),
                            exportFilename,
                            fichierExcelResultat
                        );
                } else {
                    String exportError = paramService.getParametreValue(
                        requeteUserSession,
                        SSParametreConstant.ERROR_MESSAGE_MAIL_EXPORT_NAME
                    );
                    corpsErrorTemplate += exportError;
                    sendErrorTemplateHtmlMail(mail, objet, corpsErrorTemplate, corpsParamMap);
                }
            } else {
                corpsErrorTemplate += "Il y a plus de " + maxItemsToExport + " dossiers à exporter";
                sendErrorTemplateHtmlMail(mail, objet, corpsErrorTemplate, corpsParamMap);
            }
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_SESSION_TEC, exc);
            corpsErrorTemplate += exc.getMessage();
            sendErrorTemplateHtmlMail(mail, objet, corpsErrorTemplate, corpsParamMap);
        }

        Instant endTime = Instant.now();
        LOGGER.info(
            STLogEnumImpl.START_EVENT_TEC,
            "Fin de l'export " + eventName + ", temps d'exécution : " + Duration.between(startTime, endTime)
        );
    }

    protected abstract long countItemsToExport(CoreSession session, Map<String, Serializable> eventProperties)
        throws Exception;

    protected abstract DataSource processExport(CoreSession session, Map<String, Serializable> eventProperties)
        throws Exception;

    private static void sendErrorTemplateHtmlMail(
        String mail,
        String objet,
        String corpsErrorTemplate,
        Map<String, Object> corpsParamMap
    ) {
        STServiceLocator
            .getSTMailService()
            .sendTemplateHtmlMail(
                STMailHelper.toMailAdresses(Collections.singletonList(mail)),
                objet,
                corpsErrorTemplate,
                corpsParamMap
            );
    }
}
