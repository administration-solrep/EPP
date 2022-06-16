package fr.dila.ss.core.operation.distribution;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

/**
 * Opération appelée à la fin de la chaine de création de CaseLink.
 *
 * @author feo
 * @author sly
 */
@Operation(
    id = STStepMailNotificationOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "Case Link created",
    description = "Fire event when CaseLink is created."
)
public class STStepMailNotificationOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "ST.Distribution.MailNotification";

    @Context
    protected OperationContext context;

    /**
     * Default constructor
     */
    public STStepMailNotificationOperation() {
        // do nothing
    }

    @OperationMethod
    public void sendMailNotification() {
        final CoreSession session = context.getCoreSession();

        // Si le CaseLink a été validé avec l'option d'envoi de mail à false, on envoi aucun mail
        if (
            context.containsKey(STEventConstant.OPERATION_SEND_MAIL_KEY) &&
            !(Boolean) context.get(STEventConstant.OPERATION_SEND_MAIL_KEY)
        ) {
            return;
        }

        final FeuilleRouteStep step = (FeuilleRouteStep) context.get(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY);
        final SSRouteStep routeStep = step.getDocument().getAdapter(SSRouteStep.class);

        final EventProducer eventProducer = STServiceLocator.getEventProducer();
        final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
        eventProperties.put(STEventConstant.NEW_DOCUMENT_EVENT_PARAM, routeStep.getDocument());
        final InlineEventContext inlineEventContext = new InlineEventContext(
            session,
            session.getPrincipal(),
            eventProperties
        );
        eventProducer.fireEvent(inlineEventContext.newEvent(STEventConstant.SEND_MAIL_AFTER_DISTRIBUTION_NOTIFICATION));
    }
}
