package fr.dila.st.core.adapter;

import static fr.dila.st.api.constant.STAlertConstant.ALERT_DOCUMENT_TYPE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.core.STRepositoryTestCase;
import fr.dila.st.core.alert.AlertImpl;
import fr.dila.st.core.schema.AlertSchemaUtils;
import fr.dila.st.core.schema.SmartFolderSchemaUtils;

public class TestAlert extends STRepositoryTestCase {

	private static final Log	LOG	= LogFactory.getLog(TestAlert.class);

	private String				requeteDocId1;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		openSession();
		final DocumentModel requete1 = createRequete1();
		requeteDocId1 = requete1.getId();
		session.save();
		closeSession();
	}

	public void testAlertProperties() throws Exception {
		openSession();

		LOG.info("test properties access");
		DocumentModel doc = createAlert1();
		assertNotNull(doc);
		Alert alert = doc.getAdapter(Alert.class);
		assertNotNull(alert);
		assertTrue(alert instanceof AlertImpl);

		assertNotNull(alert.getDateValidityBegin());
		assertNotNull(alert.getDateValidityEnd());
		assertEquals(8, alert.getPeriodicity().intValue());
		assertEquals(true, alert.isActivated().booleanValue());

		LOG.info("test isBetweenValidityRange");
		// L'alert doit être dans l'intervalle de temps.
		assertTrue(alert.isBetweenValidityRange());

		LOG.info("test getRequete");
		assertNotNull(alert.getRequeteExperte(session));
		assertEquals(requeteDocId1, alert.getRequeteExperte(session).getDocument().getId());

		LOG.info("test shouldBeRun");
		assertFalse(alert.shouldBeRun());

		closeSession();
	}

	public void testPeriodicityChecked() throws Exception {
		openSession();

		DocumentModel doc = createAlert1();
		Alert alert = doc.getAdapter(Alert.class);
		// Si l'alerte est déclenchée le jour de la date de début,
		// la méthode doit renvoyer true
		assertTrue(alert.periodicityChecked(alert.getDateValidityBegin()));
		// la méthode doit renvoyer true
		DateTime now = new DateTime();
		DateTime dateExecution1 = now.plusDays(3);
		assertTrue(alert.periodicityChecked(dateExecution1.toCalendar(null)));
		now = new DateTime();
		DateTime dateExecution2 = now.plusDays(2);
		assertFalse(alert.periodicityChecked(dateExecution2.toCalendar(null)));
		now = new DateTime();
		DateTime dateExecution3 = now.plusDays(11);
		assertTrue(alert.periodicityChecked(dateExecution3.toCalendar(null)));
		closeSession();
	}

	private DocumentModel createAlert1() throws ClientException {
		DocumentModel doc = session.createDocumentModel(ALERT_DOCUMENT_TYPE);
		doc = session.createDocument(doc);
		DateTime now = new DateTime();
		DateTime dateDebut = now.minusDays(5);
		DateTime dateFin = now.plusDays(26);
		AlertSchemaUtils.setDateValidityBegin(doc, dateDebut.toCalendar(null));
		AlertSchemaUtils.setDateValidityEnd(doc, dateFin.toCalendar(null));
		AlertSchemaUtils.setPeriodicity(doc, "8");
		AlertSchemaUtils.setIsActivated(doc, true);
		AlertSchemaUtils.setRequeteId(doc, requeteDocId1);
		session.saveDocument(doc);
		session.save();
		return doc;
	}

	private DocumentModel createRequete1() throws ClientException {
		DocumentModel doc = session.createDocumentModel(STRequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
		doc = session.createDocument(doc);
		SmartFolderSchemaUtils.setQueryPart(doc, "HELLO world");
		session.saveDocument(doc);
		session.save();
		return doc;
	}

}
