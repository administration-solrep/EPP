package fr.dila.st.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.STAlertService;
import fr.dila.st.core.factory.STLogFactory;

/**
 * Le service de gestion des alertes, chargé d'exécuter ces alertes à un moment donné.
 * 
 * @author jgomez
 */
public class STAlertServiceImpl implements STAlertService, Serializable {

	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(STAlertServiceImpl.class);
	private static final long		serialVersionUID	= 1L;

	private static final String		NXQL_ACTIVE_ALERT	= "SELECT * FROM "
																+ STAlertConstant.ALERT_DOCUMENT_TYPE
																+ " WHERE "
																+ STAlertConstant.ALERT_PROP_IS_ACTIVATED
																+ " = 1 and ecm:isProxy = 0 and ecm:currentLifeCycleState != '"
																+ STLifeCycleConstant.DELETED_STATE + "'";

	private static final String		ALERT_IS_NULL		= "Alert is null";

	/**
	 * Filtre pour les alertes qui doivent être jouées
	 * 
	 */
	public static class AlertThatShouldBeRunFilter implements Filter {

		private static final long	serialVersionUID	= 1L;

		/**
		 * Default constructor
		 */
		public AlertThatShouldBeRunFilter() {
			// do nothing
		}

		@Override
		public boolean accept(final DocumentModel alertDoc) {
			final Alert alert = alertDoc.getAdapter(Alert.class);
			return alert.shouldBeRun();
		}
	}

	/**
	 * Default constructor
	 */
	public STAlertServiceImpl() {
		// do nothing
	}

	@Override
	public List<Alert> getAlertsToBeRun(final CoreSession session) {
		List<DocumentModel> docs = new ArrayList<DocumentModel>();
		if (session == null) {
			LOGGER.error(null, STLogEnumImpl.UNEXPECTED_SESSION_TEC, "La session est nulle");
			return new ArrayList<Alert>();
		}
		try {
			docs = session.query(NXQL_ACTIVE_ALERT);
		} catch (final ClientException ce) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_ALERT_TEC, ce);
		}
		final List<Alert> alerts = new ArrayList<Alert>();
		final AlertThatShouldBeRunFilter filter = new AlertThatShouldBeRunFilter();
		for (final DocumentModel doc : docs) {
			if (filter.accept(doc)) {
				final Alert alert = doc.getAdapter(Alert.class);
				alerts.add(alert);
			}
		}
		return alerts;
	}

	@Override
	public Boolean sendMail(final CoreSession session, final Alert alert) throws ClientException {
		throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * créé un documentModel d'Alert non persisté
	 * 
	 * @param session
	 * @return le documentModel créé, ou null si la création a échouée
	 */
	protected DocumentModel createBareAlert(final CoreSession session) {
		DocumentModel alertDoc = null;
		try {
			alertDoc = session.createDocumentModel(STAlertConstant.ALERT_DOCUMENT_TYPE);
		} catch (ClientException ce) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_ALERT_TEC, ce);
		}
		return alertDoc;
	}

	@Override
	public Alert initAlert(final CoreSession session) {
		DocumentModel alertDoc = createBareAlert(session);
		return initAlert(session, alertDoc);
	}

	@Override
	public Alert initAlert(final CoreSession session, DocumentModel alertDoc) {
		if (alertDoc == null) {
			return null;
		} else {
			Alert alert = alertDoc.getAdapter(Alert.class);
			alert.setIsActivated(false);
			return alert;
		}
	}

	@Override
	public boolean deleteAlert(final CoreSession session, final Alert alert) {
		return deleteAlert(session, alert.getDocument());
	}

	@Override
	public boolean deleteAlert(final CoreSession session, final DocumentModel alertDoc) {
		Alert alert = alertDoc.getAdapter(Alert.class);
		RequeteExperte requeteExperte = alert.getRequeteExperte(session);
		// Soft delete par défaut
		if ("true".equals(Framework.getProperty("socle.transverse.alert.soft.delete", "true"))) {
			try {
				alert.setIsActivated(false);
				LOGGER.info(session, STLogEnumImpl.UPDATE_REQ_EXP_TEC, requeteExperte.getDocument());
				session.followTransition(requeteExperte.getDocument().getRef(),
						STLifeCycleConstant.TO_DELETE_TRANSITION);
				LOGGER.info(session, STLogEnumImpl.UPDATE_ALERT_TEC, alertDoc);
				session.followTransition(alertDoc.getRef(), STLifeCycleConstant.TO_DELETE_TRANSITION);
				session.save();
			} catch (ClientException ce) {
				LOGGER.error(session, STLogEnumImpl.FAIL_UPDATE_ALERT_TEC, alertDoc, ce);
				return false;
			}
		} else {
			try {
				// supprimer la requête experte associée à l'Alerte
				LOGGER.info(session, STLogEnumImpl.DEL_REQ_EXP_FONC, requeteExperte.getDocument());
				session.removeDocument(requeteExperte.getDocument().getRef());
				LOGGER.info(session, STLogEnumImpl.DEL_ALERT_TEC, alertDoc);
				session.removeDocument(alertDoc.getRef());
				session.save();
			} catch (ClientException ce) {
				LOGGER.error(session, STLogEnumImpl.FAIL_DEL_ALERT_TEC, alertDoc, ce);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean deleteAlert(final CoreSession session, final DocumentRef alertRef) {
		try {
			return deleteAlert(session, session.getDocument(alertRef));
		} catch (ClientException ce) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_ALERT_TEC, ce);
			return false;
		}
	}

	@Override
	public void activateAlert(final CoreSession session, final Alert alert) {
		if (alert == null) {
			LOGGER.warn(session, STLogEnumImpl.FAIL_UPDATE_ALERT_TEC, ALERT_IS_NULL);
		} else {
			alert.setIsActivated(true);
			try {
				session.saveDocument(alert.getDocument());
				session.save();
			} catch (ClientException ce) {
				LOGGER.error(session, STLogEnumImpl.FAIL_SAVE_ALERT_TEC, alert.getDocument(), ce);
			}
		}
	}

	@Override
	public void suspendAlert(final CoreSession session, final Alert alert) {
		if (alert == null) {
			LOGGER.warn(session, STLogEnumImpl.FAIL_UPDATE_ALERT_TEC, ALERT_IS_NULL);
		} else {
			alert.setIsActivated(false);
			try {
				session.saveDocument(alert.getDocument());
				session.save();
			} catch (ClientException ce) {
				LOGGER.error(session, STLogEnumImpl.FAIL_SAVE_ALERT_TEC, alert.getDocument(), ce);
			}
		}
	}
}
