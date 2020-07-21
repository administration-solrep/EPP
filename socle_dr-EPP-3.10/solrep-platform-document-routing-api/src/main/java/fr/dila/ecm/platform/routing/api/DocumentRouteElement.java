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

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.lifecycle.event.BulkLifeCycleChangeListener;

/**
 * An element of a {@link DocumentRoute}
 *
 * @author arussel
 *
 */
public interface DocumentRouteElement extends Serializable {

    /**
     * The lifecycle state of an element
     *
     */
    enum ElementLifeCycleState {
        draft, validated, ready, running, done, canceled
    }

    /**
     * The transition of the lifecycle state.
     *
     */
    enum ElementLifeCycleTransistion {
        toValidated, toReady, toRunning, toDone, backToReady, toCanceled, toDraft
    }

    /**
     * Return the list of documents that this route processes.
     *
     * @param session the session used to fetch the documents
     * @return
     */
    DocumentModelList getAttachedDocuments(CoreSession session);

    /**
     * Return the DocumentRoute this element is part of.
     *
     * @param session The session use to fetch the route.
     * @return
     */
    DocumentRoute getDocumentRoute(CoreSession session);

    /**
     * if the route this element is part of has been validated.
     *
     * @return
     */
    boolean isValidated();

    /**
     * if this element is ready.
     *
     * @return
     */
    boolean isReady();

    /**
     * if this route is done.
     *
     * @return
     */
    boolean isDone();

    /**
     * if this route is running.
     *
     * @return
     */
    boolean isRunning();

    /**
     * if this route is draft.
     *
     * @return
     */
    boolean isDraft();

    /**
     * The name of this element.
     *
     * @return
     */
    String getName();

    /**
     * the description of this element.
     *
     * @return
     */
    String getDescription();

    /**
     * Execute this element. If this is a step, it will run the operation, if
     * this is a containter it will run its children.
     *
     * @param session
     * @return true is the element is not done
     */
    void run(CoreSession session);

    /**
     * Set this element to the validate state and put it in read only mode.
     *
     * @param session
     * @throws ClientException
     */
    void validate(CoreSession session) throws ClientException;

    /**
     * Get the underlying document representing this element.
     *
     * @return
     */
    DocumentModel getDocument();

    /**
     * save the document representing this DocumentRoute.
     *
     * @param session
     */
    void save(CoreSession session);

    /**
     * set this element as validated.
     */
    void setValidated(CoreSession session);

    /**
     * set this element as ready.
     */
    void setReady(CoreSession session);

    /**
     * set this element as running.
     */
    void setRunning(CoreSession session);

    /**
     * set this element as done.
     */
    void setDone(CoreSession session);

    /**
     * remove write rights to everyone but the administrators.
     */
    void setReadOnly(CoreSession session) throws ClientException;

    /**
     * make this element follow a transition.
     *
     * @param transition the followed transition.
     * @param session the session used to follow the transition.
     * @param recursive If this element has children, do we recurse the follow
     *            transition.
     * @see BulkLifeCycleChangeListener
     */
    void followTransition(ElementLifeCycleTransistion transition,
            CoreSession session, boolean recursive);

    /**
     * If this session can validate the step.
     */
    boolean canValidateStep(CoreSession session);

    /**
     * make this user or group a validator for this step.
     */
    void setCanValidateStep(CoreSession session, String userOrGroup);

    /**
     * If this session can update this step.
     */
    boolean canUpdateStep(CoreSession session);

    /**
     * make this user or group a step updater.
     */
    void setCanUpdateStep(CoreSession session, String userOrGroup);

    /**
     * If this session can delete this step.
     */
    boolean canDeleteStep(CoreSession session);

    /**
     * If this step can be undone. Default is to allow undoing only if the
     * parent folder is running.
     */
    boolean canUndoStep(CoreSession session);

    /**
     * make this user or group step deleter.
     */
    void setCanDeleteStep(CoreSession session, String userOrGroup);

    /**
     * Set the step back to the ready state from running or done. This method
     * only modify the step state, it does not run any other action (such as
     * undoing the step action)
     */
    void backToReady(CoreSession session);

    /**
     * Set the step to a cancel step. This method only modify the state of this
     * element and does not run any other action.
     */
    void setCanceled(CoreSession session);

    /**
     * Cancel this element.
     *
     * @param session
     */
    void cancel(CoreSession session);

    /**
     * @return
     */
    boolean isCanceled();

    /**
     * @return true
     */
    boolean isModifiable();
}
