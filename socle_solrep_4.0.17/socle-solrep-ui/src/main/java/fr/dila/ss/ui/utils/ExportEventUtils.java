package fr.dila.ss.ui.utils;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

public final class ExportEventUtils {

    private ExportEventUtils() {
        // Classe non instanciable
    }

    public static void fireExportEvent(
        SpecificContext context,
        String eventName,
        Map<String, Serializable> eventProperties
    ) {
        CoreSession session = context.getSession();
        NuxeoPrincipal principal = session.getPrincipal();

        if (StringUtils.isBlank(principal.getEmail())) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("export.message.erreur.no.mail"));
        } else {
            EventProducer eventProducer = STServiceLocator.getEventProducer();
            InlineEventContext eventContext = new InlineEventContext(session, principal, eventProperties);
            eventProducer.fireEvent(eventContext.newEvent(eventName));
            context.getMessageQueue().addToastSuccess(ResourceHelper.getString("export.message.succes"));
        }
    }
}
