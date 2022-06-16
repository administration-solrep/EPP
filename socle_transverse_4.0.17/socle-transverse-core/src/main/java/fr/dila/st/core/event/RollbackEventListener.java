package fr.dila.st.core.event;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Cette classe permet de traiter les erreurs sur un événements. Si une erreur se produit, une exception est remontée à
 * la couche métier (où l'erreur pourra être traitée correctement et remontée à l'IHM) et la transaction est annulée.
 *
 * Attention à bien vérifier la déclaration du listener dans le fichier .xml : ce listener doit être appliqué uniquement
 * aux événements SYNCHRONES, non POST-COMMIT (dans la terminologie Nuxeo, "synchronous inline listeners").
 *
 * Pour utiliser ce listener, surcharger la méthode correspondant au type de contexte utilisé (une seule méthode doit
 * être surchargée).
 *
 * @author jtremeaux
 */
public abstract class RollbackEventListener extends AbstractEventListener {

    @Override
    public final void doHandleEvent(Event event) {
        try {
            EventContext ctx = event.getContext();
            if (ctx instanceof InlineEventContext) {
                handleInlineEvent(event, (InlineEventContext) ctx);
            } else if (ctx instanceof DocumentEventContext) {
                handleDocumentEvent(event, (DocumentEventContext) ctx);
            } else {
                handleGenericEvent(event, ctx);
            }
        } catch (NuxeoException e) {
            // Rollback la transaction en cas d'échec dans ce listener, et remonte l'exception à la couche métier
            TransactionHelper.setTransactionRollbackOnly();
            event.markRollBack();

            throw e;
        }
    }

    /**
     * Méthode à surcharger pour traiter les événements de contexte InlineEventContext.
     *
     * @param event
     *            Événement
     * @param ctx
     *            Context d'événement de type InlineEventContext
     * @throws ClientException
     */
    public void handleInlineEvent(Event event, InlineEventContext ctx) {
        // NOP
    }

    /**
     * Méthode à surcharger pour traiter les événements de contexte DocumentEventContext.
     *
     * @param event
     *            Événement
     * @param ctx
     *            Context d'événement de type DocumentEventContext
     * @throws ClientException
     */
    public void handleDocumentEvent(Event event, DocumentEventContext ctx) {
        // NOP
    }

    /**
     * Méthode à surcharger pour traiter les événements de contexte générique.
     *
     * @param event
     *            Événement
     * @param ctx
     *            Context d'événement de type générique
     * @throws ClientException
     */
    public void handleGenericEvent(Event event, EventContext ctx) {
        // NOP
    }
}
