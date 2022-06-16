package fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import java.io.Serializable;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.lifecycle.event.BulkLifeCycleChangeListener;

/**
 * An element of a {@link FeuilleRoute}
 *
 */
public interface FeuilleRouteElement extends Serializable {
    /**
     * The lifecycle state of an element
     *
     */
    enum ElementLifeCycleState {
        draft,
        validated,
        ready,
        running,
        done,
        canceled,
        deleted
    }

    /**
     * The transition of the lifecycle state.
     *
     */
    enum ElementLifeCycleTransistion {
        toValidated,
        toReady,
        toRunning,
        toDone,
        backToReady,
        toCanceled,
        toDraft
    }

    /**
     * Return the list of documents that this route processes.
     *
     * @param session the session used to fetch the documents
     * @return
     */
    DocumentModelList getAttachedDocuments(CoreSession session);

    /**
     * Return the FeuilleRoute this element is part of.
     *
     * @param session The session use to fetch the route.
     * @return
     */
    SSFeuilleRoute getFeuilleRoute(CoreSession session);

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
     * if this route is deleted.
     *
     * @return
     */
    boolean isDeleted();

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
     * The description of this element
     *
     * @return
     */
    void setDescription(String description);

    /**
     * Execute this element. If this is a step, it will run the operation, if
     * this is a containter it will run its children.
     *
     * @param session
     */
    void run(CoreSession session);

    /**
     * Set this element to the validate state and put it in read only mode.
     *
     * @param session
     */
    void validate(CoreSession session);

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
    void setReadOnly(CoreSession session);

    /**
     * make this element follow a transition.
     *
     * @param transition the followed transition.
     * @param session the session used to follow the transition.
     * @param recursive If this element has children, do we recurse the follow
     *            transition.
     * @see BulkLifeCycleChangeListener
     */
    void followTransition(ElementLifeCycleTransistion transition, CoreSession session, boolean recursive);

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
