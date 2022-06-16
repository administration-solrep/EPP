package fr.sword.idl.naiad.nuxeo.feuilleroute.core.helper;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.actionable.ActionableObject;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;

/**
 * An actionable validator allows to run an {@link ActionableObject}.
 *
 *
 */
public class ActionableValidator {
    protected ActionableObject actionnable;

    protected CoreSession session;

    protected Map<String, Serializable> additionalProperties = new HashMap<String, Serializable>();

    public ActionableValidator(ActionableObject actionnable, CoreSession session) {
        this.actionnable = actionnable;
        this.session = session;
    }

    public ActionableValidator(
        ActionableObject actionnable,
        CoreSession session,
        Map<String, Serializable> additionalProperties
    ) {
        this.actionnable = actionnable;
        this.session = session;
        this.additionalProperties = additionalProperties;
    }

    public void validate() {
        String chainId = actionnable.getValidateOperationChainId();
        runChain(chainId);
    }

    public void refuse() {
        String chainId = actionnable.getRefuseOperationChainId();
        runChain(chainId);
    }

    protected void runChain(String chainId) {
        AutomationService automationService = getAutomationService();
        OperationContext context = new OperationContext(session);
        context.put(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY, actionnable.getDocumentRouteStep(session));
        context.setInput(actionnable.getAttachedDocuments(session));
        context.putAll(additionalProperties);
        try {
            automationService.run(context, chainId);
        } catch (OperationException e) {
            throw new NuxeoException(e);
        }
    }

    /**
     * @return
     */
    protected AutomationService getAutomationService() {
        return Framework.getService(AutomationService.class);
    }
}
