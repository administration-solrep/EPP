package fr.dila.solonepp.core.event.batch;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

public class LanceurGeneralBatchListener extends AbstractBatchEventListener {
    private static final String BATCH_HEBDO_PREFIX = "batch-hebdo-";

    private enum DailyBatch {
        BATCH_EVENT_REMINDER_OUTDATED_PASSWORD(STEventConstant.SEND_DAILY_REMIND_CHANGE_PASS_EVENT),
        BATCH_EVENT_UNLOCK_ORGANIGRAMME(STEventConstant.UNLOCK_ORGANIGRAMME_BATCH_EVENT);

        private String eventName;

        DailyBatch(String eventName) {
            this.eventName = eventName;
        }
    }

    /**
     * Les batchs hebdomadaires
     */
    private enum WeeklyBatch {
        BATCH_EVENT_PURGE_TENTATIVES_CONNEXION(STEventConstant.BATCH_EVENT_PURGE_TENTATIVES_CONNEXION);

        private String eventName;

        WeeklyBatch(String eventName) {
            this.eventName = eventName;
        }
    }

    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(LanceurGeneralBatchListener.class);

    public LanceurGeneralBatchListener() {
        super(LOGGER, STEventConstant.LANCEUR_BATCH_EVENT);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        LOGGER.info(session, STLogEnumImpl.INIT_B_LANCEUR_GENERAL_TEC);
        final long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            EventProducer eventProducer = STServiceLocator.getEventProducer();
            Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
            eventProperties.put(STEventConstant.BATCH_EVENT_PROPERTY_PARENT_ID, batchLoggerId);

            Calendar calendar = GregorianCalendar.getInstance();
            Integer weekDay = calendar.get(Calendar.DAY_OF_WEEK);

            for (DailyBatch dailyBatch : DailyBatch.values()) {
                InlineEventContext inlineEventContext = new InlineEventContext(
                    session,
                    session.getPrincipal(),
                    eventProperties
                );
                eventProducer.fireEvent(inlineEventContext.newEvent(dailyBatch.eventName));
            }

            STParametreService parametreService = STServiceLocator.getSTParametreService();

            for (WeeklyBatch weeklyBatch : WeeklyBatch.values()) {
                String paramValue = parametreService.getParametreValue(
                    session,
                    BATCH_HEBDO_PREFIX + weeklyBatch.eventName
                );
                if (paramValue == null) {
                    LOGGER.error(
                        session,
                        STLogEnumImpl.FAIL_PROCESS_B_LANCEUR_GENERAL_TEC,
                        weeklyBatch.eventName +
                        " est déclaré comme étant un batch hebdomadaire, mais n'a pas de paramêtre pour fixer son jour d'éxécution"
                    );
                } else {
                    JoursSemaine jourSemaine = JoursSemaine.valueOf(paramValue.trim().toUpperCase());
                    if (jourSemaine != null) {
                        Integer batchDay = jourSemaine.getCalValue();
                        if (batchDay.equals(weekDay)) {
                            InlineEventContext inlineEventContext = new InlineEventContext(
                                session,
                                session.getPrincipal(),
                                eventProperties
                            );
                            eventProducer.fireEvent(inlineEventContext.newEvent(weeklyBatch.eventName));
                        }
                    }
                }
            }
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_PROCESS_B_LANCEUR_GENERAL_TEC, exc);
            errorCount++;
        }
        final long endTime = Calendar.getInstance().getTimeInMillis();
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Exécution du lanceur général",
                endTime - startTime
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, STLogEnumImpl.END_B_LANCEUR_GENERAL_TEC);
    }
}
