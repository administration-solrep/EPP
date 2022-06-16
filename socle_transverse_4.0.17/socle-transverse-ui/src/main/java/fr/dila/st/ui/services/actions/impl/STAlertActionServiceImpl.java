package fr.dila.st.ui.services.actions.impl;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.services.actions.STAlertActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class STAlertActionServiceImpl implements STAlertActionService {
    public static final String ETAT_ALERT_IS_SUSPENDED = "etat_alert_isSuspended";

    public static final String ETAT_ALERT_IS_ACTIVATED = "etat_alert_isActivated";

    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOG = STLogFactory.getLog(STAlertActionServiceImpl.class);

    public String suspend(SpecificContext context) {
        return setIsActivatedAlert(context, false, "suivi.alerte.suspendue");
    }

    public Boolean isSuspended(DocumentModel doc) {
        Alert alert = getCurrentAlert(doc);
        if (alert == null || alert.isActivated() == null) {
            return true;
        }
        return !alert.isActivated();
    }

    public Boolean isActivated(DocumentModel doc) {
        Alert alert = getCurrentAlert(doc);
        if (alert == null || alert.isActivated() == null) {
            return false;
        }
        return alert.isActivated();
    }

    public String activate(SpecificContext context) {
        return setIsActivatedAlert(context, true, "suivi.alerte.activee");
    }

    public Alert getCurrentAlert(DocumentModel alertDoc) {
        return alertDoc.getAdapter(Alert.class);
    }

    public void delete(SpecificContext context, CoreSession session, DocumentModel doc) {
        try {
            LOG.info(session, STLogEnumImpl.DEL_ALERT_FONC, doc);
            session.removeDocument(doc.getRef());
            session.save();
        } catch (Exception e) {
            LOG.error(session, STLogEnumImpl.FAIL_DEL_ALERT_FONC, doc, e);
            context.getMessageQueue().addInfoToQueue("alert.error.alertDeletion");
        }
    }

    private String setIsActivatedAlert(SpecificContext context, boolean isActivated, String successMessageKey) {
        Alert alert = getCurrentAlert(context.getCurrentDocument());
        alert.setIsActivated(isActivated);
        DocumentModel savedDoc = context.getSession().saveDocument(alert.getDocument());
        context.getMessageQueue().addToastSuccess(ResourceHelper.getString(successMessageKey));
        return savedDoc.getId();
    }
}
