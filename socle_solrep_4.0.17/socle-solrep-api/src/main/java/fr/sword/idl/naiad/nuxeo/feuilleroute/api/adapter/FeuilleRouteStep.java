package fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * A Step. The element of the route that will process the documents.
 *
 */
public interface FeuilleRouteStep extends FeuilleRouteElement {
    /**
     * Undo this step. This operation run the undo operation on this element.
     */
    FeuilleRouteStep undo(CoreSession session);
}
