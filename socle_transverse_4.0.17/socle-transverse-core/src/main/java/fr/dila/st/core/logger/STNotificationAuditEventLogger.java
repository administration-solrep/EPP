package fr.dila.st.core.logger;

import fr.dila.st.api.constant.STEventConstant;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.platform.audit.api.AuditLogger;
import org.nuxeo.ecm.platform.audit.listener.StreamAuditEventListener;
import org.nuxeo.ecm.platform.usermanager.UserManagerImpl;

public abstract class STNotificationAuditEventLogger extends StreamAuditEventListener {
    private static final Log LOGGER = LogFactory.getLog(STNotificationAuditEventLogger.class);

    /**
     * Liste des évènements qui déclenchent une entrée dans le journal d'un dossier
     * Ces évènements sont attachés à un DocumentEventContext qui contient le
     * dossier
     */
    protected static final Set<String> SET_EVENT_DOSSIER = new HashSet<>();

    /**
     * Liste des évènements qui déclenchent une entrée dans le journal technique
     * administration. Ces évènements sont attachés à un EventContext
     */
    protected static final Set<String> SET_EVENT_ADMIN = new HashSet<>();

    static {
        SET_EVENT_DOSSIER.add(STEventConstant.DOSSIER_AVIS_FAVORABLE);
        SET_EVENT_DOSSIER.add(STEventConstant.DOSSIER_VALIDER_RETOUR_MODIFICATION_EVENT);
        SET_EVENT_DOSSIER.add(STEventConstant.DOSSIER_AVIS_DEFAVORABLE);
        SET_EVENT_DOSSIER.add(STEventConstant.DOSSIER_SUBSTITUER_FEUILLE_ROUTE);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_MOVE);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_DELETE);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_UPDATE);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_COPIE_FDR_DEPUIS_DOSSIER);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_FEUILLE_ROUTE_RESTART);
        SET_EVENT_DOSSIER.add(STEventConstant.BORDEREAU_UPDATE);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_ENVOI_MAIL_DOSSIER);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_DOSSIER_CREATION);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_EXPORT_ZIP_DOSSIER);
        SET_EVENT_DOSSIER.add(STEventConstant.DOSSIER_AVIS_RECTIFICATIF);
        SET_EVENT_DOSSIER.add(STEventConstant.DOSSIER_VALIDER_NON_CONCERNE_EVENT);
        SET_EVENT_DOSSIER.add(STEventConstant.EVENT_ARCHIVAGE_DOSSIER);

        SET_EVENT_ADMIN.add(STEventConstant.EVENT_EXPORT_ZIP_DOSSIER);
        SET_EVENT_ADMIN.add(UserManagerImpl.USERCREATED_EVENT_ID);
        SET_EVENT_ADMIN.add(UserManagerImpl.USERMODIFIED_EVENT_ID);
        SET_EVENT_ADMIN.add(UserManagerImpl.USERDELETED_EVENT_ID);
        SET_EVENT_ADMIN.add(STEventConstant.NODE_CREATED_EVENT);
        SET_EVENT_ADMIN.add(STEventConstant.NODE_MODIFIED_EVENT);
        SET_EVENT_ADMIN.add(STEventConstant.NODE_DELETED_EVENT);
        SET_EVENT_ADMIN.add(STEventConstant.NODE_ACTIVATION_EVENT);
        SET_EVENT_ADMIN.add(STEventConstant.NODE_DESACTIVATION_EVENT);
        SET_EVENT_ADMIN.add(STEventConstant.EVENT_GENERAL_ENVOI_MAIL);
        SET_EVENT_ADMIN.add(STEventConstant.CHANGEMENT_GVT_EVENT);
    }

    protected AuditLogger AUDIT_LOGGER;

    @Override
    public void handleEvent(Event event) {
        AUDIT_LOGGER = ServiceUtil.getService(AuditLogger.class);
        if (AUDIT_LOGGER == null) {
            LOGGER.error("Can not reach AuditLogger");
            return;
        }
        if (!acceptEvent(event)) {
            return;
        }

        loggerProcess(event);
    }

    public boolean acceptEvent(Event event) {
        return event != null ? !DocumentEventTypes.SESSION_SAVED.equals(event.getName()) : false;
    }

    public void defaultHandleEvent(Event event) {
        super.handleEvent(event);
    }

    protected abstract void loggerProcess(Event event);
}
