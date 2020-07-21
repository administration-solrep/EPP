/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     arussel
 */
package fr.dila.ecm.platform.routing.api;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import fr.dila.ecm.platform.routing.api.exception.DocumentRouteAlredayLockedException;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteNotLockedException;

/**
 * The DocumentRoutingService allows manipulate {@link DocumentRoute}.
 *
 * @author arussel
 *
 */
public interface DocumentRoutingService {
    /**
     * Create a new {@link DocumentRoute} instance from this
     * {@link DocumentRoute} model.
     *
     * @param model The model used to create the instance.
     * @param documents The list of document bound to the instance.
     * @param startInstance if the {@link DocumentRoute} is automatically
     *            started.
     * @return the created {@link DocumentRoute} instance.
     */
    DocumentRoute createNewInstance(DocumentRoute model,
            List<String> documentIds, CoreSession session, boolean startInstance);

    /**
     * @see #createNewInstance(DocumentRoute, List, CoreSession, boolean) with
     *      only one document attached.
     */
    DocumentRoute createNewInstance(DocumentRoute model, String documentId,
            CoreSession session, boolean startInstance);

    /**
     * @see #createNewInstance(DocumentRoute, List, CoreSession, boolean) with
     *      startInstance <code>true</code>
     */
    DocumentRoute createNewInstance(DocumentRoute model,
            List<String> documentIds, CoreSession session);

    /**
     * @see #createNewInstance(DocumentRoute, List, CoreSession, boolean) with
     *      startInstance <code>true</code> and only one document attached.
     */
    DocumentRoute createNewInstance(DocumentRoute model, String documentId,
            CoreSession session);

    /**
     * Save a route instance as a new model of route.
     *
     * The place in which the new instance is persisted and its name depends on
     * {@link DocumentRoutingPersister}. The route instance should be in either
     * running, done or ready state. The new route model will be in draft state
     * and won't have any attached documents.
     *
     * @param route the instance from which we create a new model.
     * @return the new model in draft state.
     */
    DocumentRoute saveRouteAsNewModel(DocumentRoute route, CoreSession session);

    /**
     * Return the list of available {@link DocumentRoute} model the user can
     * start.
     *
     * @param session The session of the user.
     * @return A list of available {@link DocumentRoute}
     */
    List<DocumentRoute> getAvailableDocumentRouteModel(CoreSession session);

    /**
     * Return the operation chain to run for a documentType. The document type
     * should extend the DocumentRouteStep. Use the <code>chainsToType</code>
     * extension point to contribute new mapping.
     *
     * @param documentType The document type
     * @return The operation chain id.
     */
    String getOperationChainId(String documentType);

    /**
     * Return the operation chain to undo a step when the step is in running
     * state. The document type should extend the DocumentRouteStep. Use the
     * <code>chainsToType</code> extension point to contribute new mapping.
     *
     * @param documentType
     * @return
     */
    String getUndoFromRunningOperationChainId(String documentType);

    /**
     * Return the operation chain to undo a step when the step is in done state.
     * The document type should extend the DocumentRouteStep. Use the
     * <code>chainsToType</code> extension point to contribute new mapping.
     *
     * @param documentType
     * @return
     */
    String getUndoFromDoneOperationChainId(String documentType);

    /**
     * Validates the given {@link DocumentRoute} model by changing its lifecycle
     * state and setting it and all its children in ReadOnly.
     *
     * @return The validated route.
     * */
    DocumentRoute validateRouteModel(DocumentRoute routeModel,
            CoreSession session) throws DocumentRouteNotLockedException,
            ClientException;

    /**
     * Computes the list of elements {@link DocumentRouteTableElement} for this
     * {@link DocumentRoute}.
     *
     * @param routeDocument {@link DocumentRoute}.
     * @param session The session used to query the {@link DocumentRoute}.
     * @param A list of {@link DocumentRouteElement}
     */
    List<DocumentRouteTableElement> getRouteElements(DocumentRoute route,
            CoreSession session);

    /**
     * Return the list of related {@link DocumentRoute} in a state for a given
     * attached document.
     *
     * @param session The session used to query the {@link DocumentRoute}.
     * @param states the list of states.
     * @return A list of available {@link DocumentRoute}
     */
    List<DocumentRoute> getDocumentRoutesForAttachedDocument(
            CoreSession session, String attachedDocId,
            List<DocumentRouteElement.ElementLifeCycleState> states);

    /**
     * @see #getDocumentRoutesForAttachedDocument(CoreSession, String, List) for
     *      route running or ready.
     * @param session
     * @param attachedDocId
     * @return
     */
    List<DocumentRoute> getDocumentRoutesForAttachedDocument(
            CoreSession session, String attachedDocId);

    /**
     * if the user can create a route.
     */
    boolean canUserCreateRoute(NuxeoPrincipal currentUser);

    /**
     * if the user can create a route.
     */
    boolean canUserModifyRoute(NuxeoPrincipal currentUser);

    /**
     * if the user can validate a route.
     */
    boolean canUserValidateRoute(NuxeoPrincipal currentUser);

    /**
     * Add a route element in another route element.
     *
     * @param parentDocumentRef The DocumentRef of the parent document.
     * @param idx The position of the document in its container.
     * @param routeElement The document to add.
     * @param session
     * @throws ClientException
     */
    void addRouteElementToRoute(DocumentRef parentDocumentRef, int idx,
            DocumentRouteElement routeElement, CoreSession session)
            throws DocumentRouteNotLockedException, ClientException;

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
     * @throws ClientException
     */
    void addRouteElementToRoute(DocumentRef parentDocumentRef,
            String sourceName, DocumentRouteElement routeElement,
            CoreSession session) throws DocumentRouteNotLockedException,
            ClientException;

    /**
     * Remove the given route element
     *
     * @param The route element document.
     * @param session
     * @throws ClientException
     */
    void removeRouteElement(DocumentRouteElement routeElement,
            CoreSession session) throws DocumentRouteNotLockedException,
            ClientException;

    /**
     * Get the children of the given stepFolder ordered by the ecm:pos metadata.
     *
     * @param stepFolderId
     * @param session
     * @return
     * @throws ClientException
     */
    DocumentModelList getOrderedRouteElement(String routeElementId,
            CoreSession session) throws ClientException;

    /**
     * Locks this {@link DocumentRoute} if not already locked by the current
     * user. If the document is already locked by another user and
     * {@link DocumentRouteAlredayLockedException} is thrown
     *
     * @param routeDocument {@link DocumentRoute}.
     * @param session The session used to lock the {@link DocumentRoute}.
     * @throws ClientException
     * @throws {@link DocumentRouteAlredayLockedException}
     */
    void lockDocumentRoute(DocumentRoute routeModel, CoreSession session)
            throws DocumentRouteAlredayLockedException, ClientException;

    /**
     * Unlocks this {@link DocumentRoute}.If the document is not locked throws a
     * {@link DocumentRouteNotLockedException}
     *
     * @param routeDocument {@link DocumentRoute}.
     * @param session The session used to lock the {@link DocumentRoute}.
     * @throws {@link ClientException}
     */
    void unlockDocumentRoute(DocumentRoute routeModel, CoreSession session)
            throws DocumentRouteNotLockedException, ClientException;

    /**
     * Update the given route element
     *
     * @param The route element document.
     * @param session
     * @throws ClientException
     */
    void updateRouteElement(DocumentRouteElement routeModel, CoreSession session)
            throws DocumentRouteNotLockedException, ClientException;

    /**
     * Verify is this {@link DocumentRoute} is already locked by the current
     * user.
     *
     * @param routeDocument {@link DocumentRoute}.
     * @param session
     * @throws ClientException
     */
    boolean isLockedByCurrentUser(DocumentRoute routeModel, CoreSession session)
            throws ClientException;

}
