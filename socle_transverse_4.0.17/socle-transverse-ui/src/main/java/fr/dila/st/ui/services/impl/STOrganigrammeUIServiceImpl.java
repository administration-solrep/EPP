package fr.dila.st.ui.services.impl;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Objects;
import java.util.function.Consumer;
import org.nuxeo.ecm.core.api.CoreSession;

public class STOrganigrammeUIServiceImpl<N extends OrganigrammeNode> {

    protected STOrganigrammeUIServiceImpl() {
        // Must be inherited
    }

    protected OrganigrammeService getOrganigrammeService() {
        return STServiceLocator.getOrganigrammeService();
    }

    protected void lockOrganigrammeNode(SpecificContext context, N node) {
        boolean locked = getOrganigrammeService().lockOrganigrammeNode(context.getSession(), node);
        if (locked) {
            context.getMessageQueue().addSuccessToQueue("organigramme.success.node.locked");
        } else {
            String message = ResourceHelper.getString(
                "organigramme.error.node.lock",
                SolonDateConverter.DATE_SLASH.format(node.getLockDate()),
                node.getLockUserName()
            );
            context.getMessageQueue().addErrorToQueue(message);
        }
    }

    protected void performNodeUpdate(SpecificContext context, N node, Consumer<N> updater, String successKey) {
        CoreSession session = context.getSession();

        if (Objects.equals(session.getPrincipal().getName(), node.getLockUserName())) {
            try {
                updater.accept(node);
            } finally {
                getOrganigrammeService().unlockOrganigrammeNode(node);
            }

            context.getMessageQueue().addSuccessToQueue(successKey);
            context.getMessageQueue().addSuccessToQueue("organigramme.success.node.unlocked");
        } else if (node.getLockUserName() == null) {
            context.getMessageQueue().addErrorToQueue("organigramme.error.node.noLock");
        } else {
            String message = ResourceHelper.getString(
                "organigramme.error.node.lock",
                SolonDateConverter.DATE_SLASH.format(node.getLockDate()),
                node.getLockUserName()
            );
            context.getMessageQueue().addErrorToQueue(message);
        }
    }
}
