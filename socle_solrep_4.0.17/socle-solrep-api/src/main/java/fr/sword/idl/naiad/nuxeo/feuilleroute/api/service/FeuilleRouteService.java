package fr.sword.idl.naiad.nuxeo.feuilleroute.api.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * The FeuilleRouteService allows manipulate {@link FeuilleRoute}.
 *
 *
 */
public interface FeuilleRouteService {
    /**
     * Create a new {@link FeuilleRoute} instance from this
     * {@link FeuilleRoute} model.
     *
     * @param model The model used to create the instance.
     * @param documentIds The list of document bound to the instance.
     * @param session
     * @param startInstance if the {@link FeuilleRoute} is automatically
     *            started.
     * @return the created {@link FeuilleRoute} instance.
     */
    FeuilleRoute createNewInstance(
        FeuilleRoute model,
        List<String> documentIds,
        CoreSession session,
        boolean startInstance
    );

    /**
     * @see #createNewInstance(FeuilleRoute, List, CoreSession, boolean) with
     *      only one document attached.
     */
    FeuilleRoute createNewInstance(FeuilleRoute model, String documentId, CoreSession session, boolean startInstance);

    /**
     * @see #createNewInstance(FeuilleRoute, List, CoreSession, boolean) with
     *      startInstance <code>true</code>
     */
    FeuilleRoute createNewInstance(FeuilleRoute model, List<String> documentIds, CoreSession session);

    /**
     * @see #createNewInstance(FeuilleRoute, List, CoreSession, boolean) with
     *      startInstance <code>true</code> and only one document attached.
     */
    FeuilleRoute createNewInstance(FeuilleRoute model, String documentId, CoreSession session);

    /**
     * Save a route instance as a new model of route.
     *
     * The place in which the new instance is persisted and its name depends on
     * {@link FeuilleRoutePersister}. The route instance should be in either
     * running, done or ready state. The new route model will be in draft state
     * and won't have any attached documents.
     *
     * @param route the instance from which we create a new model.
     * @return the new model in draft state.
     */
    FeuilleRoute saveRouteAsNewModel(FeuilleRoute route, CoreSession session);

    /**
     * Return the list of available {@link FeuilleRoute} model the user can
     * start.
     *
     * @param session The session of the user.
     * @return A list of available {@link FeuilleRoute}
     */
    List<FeuilleRoute> getAvailableDocumentRouteModel(CoreSession session);

    //    /**
    //     * Return the operation chain to run for a documentType. The document type
    //     * should extend the DocumentRouteStep. Use the <code>chainsToType</code>
    //     * extension point to contribute new mapping.
    //     *
    //     * @param documentType The document type
    //     * @return The operation chain id.
    //     */
    //    String getOperationChainId(String documentType);
    //
    //    /**
    //     * Return the operation chain to undo a step when the step is in running
    //     * state. The document type should extend the DocumentRouteStep. Use the
    //     * <code>chainsToType</code> extension point to contribute new mapping.
    //     *
    //     * @param documentType
    //     * @return
    //     */
    //    String getUndoFromRunningOperationChainId(String documentType);
    //
    //    /**
    //     * Return the operation chain to undo a step when the step is in done state.
    //     * The document type should extend the DocumentRouteStep. Use the
    //     * <code>chainsToType</code> extension point to contribute new mapping.
    //     *
    //     * @param documentType
    //     * @return
    //     */
    //    String getUndoFromDoneOperationChainId(String documentType);

    /**
     * Validates the given {@link FeuilleRoute} model by changing its lifecycle
     * state and setting it and all its children in ReadOnly.
     *
     * @return The validated route.
     * */
    FeuilleRoute validateRouteModel(FeuilleRoute routeModel, CoreSession session);

    /**
     * Return the list of related {@link FeuilleRoute} in a state for a given
     * attached document.
     *
     * @param session The session used to query the {@link FeuilleRoute}.
     * @param states the list of states.
     * @return A list of available {@link FeuilleRoute}
     */
    List<FeuilleRoute> getDocumentRoutesForAttachedDocument(
        CoreSession session,
        String attachedDocId,
        List<FeuilleRouteElement.ElementLifeCycleState> states
    );

    /**
     * @see #getDocumentRoutesForAttachedDocument(CoreSession, String, List) for
     *      route running or ready.
     * @param session
     * @param attachedDocId
     * @return
     */
    List<FeuilleRoute> getDocumentRoutesForAttachedDocument(CoreSession session, String attachedDocId);

    //    /**
    //     * if the user can create a route.
    //     */
    //    boolean canUserCreateRoute(NuxeoPrincipal currentUser);
    //
    //    /**
    //     * if the user can create a route.
    //     */
    //    boolean canUserModifyRoute(NuxeoPrincipal currentUser);
    //
    //    /**
    //     * if the user can validate a route.
    //     */
    //    boolean canUserValidateRoute(NuxeoPrincipal currentUser);

    /**
     * Add a route element in another route element.
     *
     * @param parentDocumentRef The DocumentRef of the parent document.
     * @param idx The position of the document in its container.
     * @param routeElement The document to add.
     * @param session
     */
    void addRouteElementToRoute(
        DocumentRef parentDocumentRef,
        int idx,
        FeuilleRouteElement routeElement,
        CoreSession session
    );

    /**
     * Add a route element in another route element.
     *
     * If the parent element is in draft state, the routeElement is kept in
     * draft state. Otherwise, the element is set to 'ready' state.
     *
     * @param parentDocumentRef The DocumentRef of the parent document.
     * @param sourceName the name of the previous document in the container.
     * @param routeElement the document to add.
     * @param session
     */
    void addRouteElementToRoute(
        DocumentRef parentDocumentRef,
        String sourceName,
        FeuilleRouteElement routeElement,
        CoreSession session
    );

    /**
     * Remove the given route element
     *
     * @param routeElement The route element document.
     * @param session
     */
    void removeRouteElement(FeuilleRouteElement routeElement, CoreSession session);

    /**
     * Get the children of the given stepFolder ordered by the ecm:pos metadata.
     *
     * @param routeElementId
     * @param session
     * @return
     */
    DocumentModelList getOrderedRouteElement(String routeElementId, CoreSession session);

    /**
     * Locks this {@link FeuilleRoute} if not already locked by the current
     * user. If the document is already locked by another user and
     * {@link FeuilleRouteAlreadyLockedException} is thrown
     *
     * @param routeModel {@link FeuilleRoute}.
     * @param session The session used to lock the {@link FeuilleRoute}.
     */
    void lockDocumentRoute(FeuilleRoute routeModel, CoreSession session);

    /**
     * Unlocks this {@link FeuilleRoute}.If the document is not locked throws a
     * {@link FeuilleRouteNotLockedException}
     *
     * @param routeModel {@link FeuilleRoute}.
     * @param session The session used to lock the {@link FeuilleRoute}.
     */
    void unlockDocumentRoute(FeuilleRoute routeModel, CoreSession session);

    /**
     * Update the given route element
     *
     * @param routeModel The route element document.
     * @param session
     */
    void updateRouteElement(FeuilleRouteElement routeModel, CoreSession session);

    String getOrCreateRootOfRouteInstancePath(CoreSession session);
}
