package fr.dila.st.core.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.service.STAlertService;
import fr.dila.st.core.STRepositoryTestCase;
import fr.dila.st.core.schema.AlertSchemaUtils;

public class TestAlertSchedulerService extends STRepositoryTestCase {

	private STAlertService	sTAlertService;

	public void setUp() throws Exception {
		super.setUp();

		sTAlertService = STServiceLocator.getAlertService();
		assertNotNull(sTAlertService);
	}

	public void testGetAlert() throws ClientException {
		assertNotNull(sTAlertService);
		assertTrue(sTAlertService instanceof STAlertServiceImpl);

		openSession();

		List<Alert> alerts;

		// NO ALERT
		alerts = sTAlertService.getAlertsToBeRun(session);
		assertTrue(alerts.isEmpty());

		// 4 ALERTS : ONE INACTIVE
		DocumentModel alertDoc1 = createRunnableAlert(session, true);
		/* DocumentModel alertDoc2 = */createRunnableAlert(session, false);
		DocumentModel alertDoc3 = createRunnableAlert(session, true);
		DocumentModel alertDoc4 = createRunnableAlert(session, true);
		session.save();

		final Set<String> expectedIds = new HashSet<String>();
		expectedIds.add(alertDoc1.getId());
		expectedIds.add(alertDoc3.getId());
		expectedIds.add(alertDoc4.getId());

		alerts = sTAlertService.getAlertsToBeRun(session);
		assertEquals(expectedIds.size(), alerts.size());
		final Set<String> actuals = new HashSet<String>();
		for (Alert alert : alerts) {
			actuals.add(alert.getDocument().getId());
		}

		assertEquals(expectedIds, actuals);

		closeSession();
	}

	public void testSendMail() throws ClientException {
		try {
			sTAlertService.sendMail(session, null);
			fail("Pas d'implementation dans le socle");
		} catch (UnsupportedOperationException e) {
			assertEquals("Not implemented", e.getMessage());
		}
	}

	/**
	 * Crée une alerte qui doit etre lancé en terme de date de debut, de fin et de periodicité Le status actif/inactif
	 * est donné en paramètre
	 * 
	 * @param session
	 * @param isActivated
	 * @return le document model associé à l'alert
	 * @throws ClientException
	 */
	private DocumentModel createRunnableAlert(CoreSession session, boolean isActivated) throws ClientException {

		final Calendar start = Calendar.getInstance();
		start.add(Calendar.DAY_OF_MONTH, -5);
		final Calendar end = Calendar.getInstance();
		end.add(Calendar.DAY_OF_MONTH, 5);

		DocumentModel doc = session.createDocumentModel("/", "alert", STAlertConstant.ALERT_DOCUMENT_TYPE);
		AlertSchemaUtils.setIsActivated(doc, isActivated);
		AlertSchemaUtils.setPeriodicity(doc, "1");
		AlertSchemaUtils.setDateValidityBegin(doc, start);
		AlertSchemaUtils.setDateValidityEnd(doc, end);
		doc = session.createDocument(doc);
		return doc;
	}

}
