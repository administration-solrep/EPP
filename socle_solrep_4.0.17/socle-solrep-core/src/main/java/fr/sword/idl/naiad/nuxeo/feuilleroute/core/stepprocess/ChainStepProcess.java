package fr.sword.idl.naiad.nuxeo.feuilleroute.core.stepprocess;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.StepProcessInit;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Map;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

public class ChainStepProcess implements StepProcessInit {
    private String chainId;
    private String undoChainIdFromRunning;
    private String undoChainIdFromDone;

    public ChainStepProcess() {
        // do nothing
    }

    public void init(Map<String, String> parameters) {
        this.chainId = parameters.get("chainId");
        this.undoChainIdFromRunning = parameters.get("undoChainIdFromRunning");
        this.undoChainIdFromDone = parameters.get("undoChainIdFromDone");
    }

    @Override
    public void run(CoreSession session, FeuilleRouteStep element) {
        try {
            OperationContext context = new OperationContext(session);
            context.put(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY, element);
            context.setInput(element.getAttachedDocuments(session));
            getAutomationService().run(context, this.chainId);
        } catch (OperationException e) {
            throw new NuxeoException(e);
        }
    }

    @Override
    public void undo(CoreSession session, FeuilleRouteStep element) {
        String undoChainId;
        if (element.isDone()) {
            undoChainId = this.undoChainIdFromDone;
        } else if (element.isRunning()) {
            undoChainId = this.undoChainIdFromRunning;
        } else {
            throw new NuxeoException("Trying to undo a step neither in done nor running state.");
        }

        try (OperationContext context = new OperationContext(session)) {
            context.put(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY, element);
            context.setInput(element.getAttachedDocuments(session));
            getAutomationService().run(context, undoChainId);
        } catch (OperationException e) {
            throw new NuxeoException(e);
        }
    }

    private AutomationService getAutomationService() {
        return ServiceUtil.getRequiredService(AutomationService.class);
    }

    public String getChainId() {
        return chainId;
    }

    public String getUndoChainIdFromRunning() {
        return undoChainIdFromRunning;
    }

    public String getUndoChainIdFromDone() {
        return undoChainIdFromDone;
    }
}
