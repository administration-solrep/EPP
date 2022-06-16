package fr.dila.st.core.adapter;

import static fr.dila.st.api.constant.STAlertConstant.ALERT_DOCUMENT_TYPE;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.core.alert.AlertImpl;
import fr.dila.st.core.schema.AlertSchemaUtils;
import fr.dila.st.core.test.STFeature;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
public class TestAlert {
    private static final Log LOG = LogFactory.getLog(TestAlert.class);

    private String requeteDocId1;

    @Inject
    private CoreFeature coreFeature;

    @Before
    public void setUp() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            final DocumentModel requete1 = createRequete1(session);
            requeteDocId1 = requete1.getId();
            session.save();
        }
    }

    @Test
    public void testAlertProperties() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            LOG.info("test properties access");
            DocumentModel doc = createAlert1(session);
            Assert.assertNotNull(doc);
            Alert alert = doc.getAdapter(Alert.class);
            Assert.assertNotNull(alert);
            Assert.assertTrue(alert instanceof AlertImpl);

            Assert.assertNotNull(alert.getDateValidityBegin());
            Assert.assertNotNull(alert.getDateValidityEnd());
            Assert.assertEquals(8, alert.getPeriodicity().intValue());
            Assert.assertEquals(true, alert.isActivated().booleanValue());

            LOG.info("test isBetweenValidityRange");
            // L'alert doit être dans l'intervalle de temps.
            Assert.assertTrue(alert.isBetweenValidityRange());

            LOG.info("test getRequete");
            Assert.assertNotNull(alert.getRequeteExperte(session));
            Assert.assertEquals(requeteDocId1, alert.getRequeteExperte(session).getDocument().getId());

            LOG.info("test shouldBeRun");
            Assert.assertFalse(alert.shouldBeRun());
        }
    }

    @Test
    public void testPeriodicityChecked() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = createAlert1(session);
            Alert alert = doc.getAdapter(Alert.class);
            // Si l'alerte est déclenchée le jour de la date de début,
            // la méthode doit renvoyer true
            Assert.assertTrue(alert.periodicityChecked(alert.getDateValidityBegin()));
            // la méthode doit renvoyer true
            DateTime now = new DateTime();
            DateTime dateExecution1 = now.plusDays(3);
            Assert.assertTrue(alert.periodicityChecked(dateExecution1.toCalendar(null)));
            now = new DateTime();
            DateTime dateExecution2 = now.plusDays(2);
            Assert.assertFalse(alert.periodicityChecked(dateExecution2.toCalendar(null)));
            now = new DateTime();
            DateTime dateExecution3 = now.plusDays(11);
            Assert.assertTrue(alert.periodicityChecked(dateExecution3.toCalendar(null)));
        }
    }

    private DocumentModel createAlert1(CoreSession session) {
        DocumentModel doc = session.createDocumentModel(ALERT_DOCUMENT_TYPE);
        DateTime now = new DateTime();
        DateTime dateDebut = now.minusDays(5);
        DateTime dateFin = now.plusDays(26);
        AlertSchemaUtils.setDateValidityBegin(doc, dateDebut.toCalendar(null));
        AlertSchemaUtils.setDateValidityEnd(doc, dateFin.toCalendar(null));
        AlertSchemaUtils.setPeriodicity(doc, "8");
        AlertSchemaUtils.setIsActivated(doc, true);
        AlertSchemaUtils.setRequeteId(doc, requeteDocId1);
        doc = session.createDocument(doc);
        session.save();
        return doc;
    }

    private DocumentModel createRequete1(CoreSession session) {
        DocumentModel doc = session.createDocumentModel(STRequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc.getAdapter(RequeteExperte.class).setWhereClause("HELLO world");
        doc = session.createDocument(doc);
        session.save();
        return doc;
    }
}
