package fr.dila.solonepp.core.event;

import fr.dila.solonepp.core.operation.DataInjectionOperation;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.HashMap;
import java.util.Map;
import org.h2.tools.Console;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.content.template.service.PostContentCreationHandler;

public class DevInitListener implements PostContentCreationHandler {

    @Override
    public void execute(CoreSession session) {
        OperationContext ctx = new OperationContext(session);
        AutomationService automationService = ServiceUtil.getRequiredService(AutomationService.class);

        // Operation1 parameter setting
        Map<String, Object> params = new HashMap<>();
        try {
            automationService.run(ctx, DataInjectionOperation.ID, params);
        } catch (OperationException e) {
            throw new NuxeoException(e);
        }

        try {
            Console.main("-webAllowOthers", "-web");
        } catch (Exception e) {
            throw new NuxeoException(e);
        }
    }
}
