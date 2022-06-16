package fr.dila.solonepp.core.event;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener qui permet de créer une notification lors du passage d'un événement à l'état en instance.
 *
 * @author jtremeaux
 */
public class AfterEvenementEnInstanceListener implements PostCommitEventListener {

    public void handleEvent(Event event) {
        // Traite uniquement les évènements de transition d'état
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }

        if (
            !event.getName().equals(LifeCycleConstants.TRANSITION_EVENT) ||
            !SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE.equals(
                ctx.getProperty(LifeCycleConstants.TRANSTION_EVENT_OPTION_TO)
            )
        ) {
            return;
        }

        // Traite uniquement les documents de type événement
        DocumentEventContext context = (DocumentEventContext) ctx;
        DocumentModel evenementDoc = context.getSourceDocument();
        if (!SolonEppConstant.EVENEMENT_DOC_TYPE.equals(evenementDoc.getType())) {
            return;
        }
        CoreSession session = context.getCoreSession();

        // Crée les notifications
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.createNotificationEvenement(
            session,
            evenementDoc,
            SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_EVENEMENT_EN_INSTANCE_VALUE
        );
    }

    @Override
    public void handleEvent(EventBundle events) {
        for (Event event : events) {
            handleEvent(event);
        }
    }
}
