package fr.sword.idl.naiad.nuxeo.feuilleroute.api.actionable;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 * An actionable object is an object that can be validated or refused.
 *
 * see ActionableValidator
 *
 */
public interface ActionableObject {
    /**
     * The operation chain id if the action is refused.
     *
     * @return
     */
    String getRefuseOperationChainId();

    /**
     * The operation chain id if the action is validated.
     *
     * @return
     */
    String getValidateOperationChainId();

    /**
     * The step that represent the action.
     *
     * @param session
     * @return
     */
    FeuilleRouteStep getDocumentRouteStep(CoreSession session);

    /**
     * The documents processed by the action.
     */
    DocumentModelList getAttachedDocuments(CoreSession session);
}
