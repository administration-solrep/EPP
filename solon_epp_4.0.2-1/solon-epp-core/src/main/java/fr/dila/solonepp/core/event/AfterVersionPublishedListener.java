package fr.dila.solonepp.core.event;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener qui permet de créer la mise a jour de la fiche dossier.
 *
 * @author feo
 */
public class AfterVersionPublishedListener implements EventListener {

    public void handleEvent(Event event) {
        // Traite uniquement les évènements de transition d'état
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }

        if (
            !event.getName().equals(LifeCycleConstants.TRANSITION_EVENT) ||
            !SolonEppLifecycleConstant.VERSION_PUBLIE_STATE.equals(
                ctx.getProperty(LifeCycleConstants.TRANSTION_EVENT_OPTION_TO)
            )
        ) {
            return;
        }

        // Traite uniquement les documents de type événement
        DocumentEventContext context = (DocumentEventContext) ctx;
        DocumentModel versionDoc = context.getSourceDocument();
        if (!SolonEppConstant.VERSION_DOC_TYPE.equals(versionDoc.getType())) {
            return;
        }
        CoreSession session = context.getCoreSession();

        // Mise a jour de la fiche dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        dossierService.updateFicheDossier(session, versionDoc);
    }
}
