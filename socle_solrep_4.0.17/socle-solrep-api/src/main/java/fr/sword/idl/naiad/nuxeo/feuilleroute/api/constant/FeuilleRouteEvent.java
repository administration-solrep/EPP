package fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant;

public enum FeuilleRouteEvent {
    /**
     * before the route is validated, each part of the route is in "Draft"
     * state. The session used is unrestricted. The element key is the
     * route.
     */
    beforeRouteValidated,
    /**
     * after the route is validated, each part of the route is in
     * "Validated" state. The session used is unrestricted. The element key
     * is the route.
     */
    afterRouteValidated,
    /**
     * before the route is ready, each part of the route is in "Validated"
     * state.The session used is unrestricted. The element key is the route.
     */
    beforeRouteReady,
    /**
     * after the route is ready, each part of the route is in "Ready"
     * state.The session used is unrestricted. The element key is the route.
     */
    afterRouteReady,
    /**
     * before the route starts. The RouteDocument is in "Running" state,
     * other parts of the route is either in Ready, Running or Done
     * state.The session used is unrestricted. The element key is the route.
     */
    beforeRouteStart,
    /**
     * after the route is finished. The route and each part of the route is
     * in Done state.The session used is unrestricted. The element key is
     * the route.
     */
    afterRouteFinish,
    /**
     * before the operation chain for this step is called. The step is in
     * "Running" state.The session used is unrestricted. The element key is
     * the step.
     */
    beforeStepRunning,
    /**
     * After the operation chain of this step ran and if the step is not
     * done, ie: if we are in a waiting state.The session used is
     * unrestricted. The element key is the step.
     */
    stepWaiting,
    /**
     * after the operation chain for this step is called.The step is in
     * "Done" state.The session used is unrestricted. The element key is the
     * step.
     */
    afterStepRunning,
    /**
     * before a step is put back to ready state.
     */
    beforeStepBackToReady,
    /**
     * after a step was put back to ready state.
     */
    afterStepBackToReady,
    /**
     * before the undo operation is run on the step.
     */
    beforeUndoingStep,
    /**
     * after the undo operation is run on the step.
     */
    afterUndoingStep
}
