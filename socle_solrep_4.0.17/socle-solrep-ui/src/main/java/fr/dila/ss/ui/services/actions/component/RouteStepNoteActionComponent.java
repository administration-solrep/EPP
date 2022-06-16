package fr.dila.ss.ui.services.actions.component;

import fr.dila.ss.ui.services.actions.RouteStepNoteActionService;
import fr.dila.ss.ui.services.actions.impl.RouteStepNoteActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RouteStepNoteActionComponent
    extends ServiceEncapsulateComponent<RouteStepNoteActionService, RouteStepNoteActionServiceImpl> {

    public RouteStepNoteActionComponent() {
        super(RouteStepNoteActionService.class, new RouteStepNoteActionServiceImpl());
    }
}
