package fr.dila.st.core.logger;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.platform.audit.api.AuditLogger;
import org.nuxeo.ecm.platform.audit.listener.AuditEventLogger;
import org.nuxeo.ecm.platform.usermanager.UserManagerImpl;

import fr.dila.st.api.constant.STEventConstant;

public abstract class STNotificationAuditEventLogger extends AuditEventLogger {

	private static final Log			LOGGER				= LogFactory.getLog(STNotificationAuditEventLogger.class);

	/**
	 * Liste des évènements qui déclenchent une entrée dans le journal d'un dossier Ces évènements sont attachés à un
	 * DocumentEventContext qui contient le dossier
	 */
	protected static final Set<String>	SET_EVENT_DOSSIER	= new HashSet<String>();

	/**
	 * Liste des évènements qui déclenchent une entrée dans le journal technique administration. Ces évènements sont
	 * attachés à un EventContext
	 */
	protected static final Set<String>	SET_EVENT_ADMIN		= new HashSet<String>();
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

	};

	protected AuditLogger				AUDIT_LOGGER;

	@Override
	public void handleEvent(EventBundle events) throws ClientException {
		AUDIT_LOGGER = getAuditLogger();
		if (AUDIT_LOGGER == null) {
			LOGGER.error("Can not reach AuditLogger");
			return;
		}
		try {
			for (Event event : events) {
				if (event == null) {
					continue;
				}
				String eventName = event.getName();
				if (DocumentEventTypes.SESSION_SAVED.equals(eventName)) {
					continue;
				}
				loggerProcess(event);
			}
		} catch (ClientException e) {
			LOGGER.error("Unable to persist event bundle into audit log", e);
		}
	}

	protected abstract void loggerProcess(Event event) throws ClientException;

}
