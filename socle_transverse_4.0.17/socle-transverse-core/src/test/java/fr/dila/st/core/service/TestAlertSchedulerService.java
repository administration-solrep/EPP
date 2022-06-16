package fr.dila.st.core.service;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.service.STAlertService;
import fr.dila.st.core.schema.AlertSchemaUtils;
import fr.dila.st.core.test.STFeature;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.junit.Assert;
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
public class TestAlertSchedulerService {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private STAlertService sTAlertService;

    @Test
    public void testGetAlert() {
        Assert.assertNotNull(sTAlertService);

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            List<Alert> alerts;

            // NO ALERT
            alerts = sTAlertService.getAlertsToBeRun(session);
            Assert.assertTrue(alerts.isEmpty());

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
            Assert.assertEquals(expectedIds.size(), alerts.size());
            final Set<String> actuals = new HashSet<String>();
            for (Alert alert : alerts) {
                actuals.add(alert.getDocument().getId());
            }

            Assert.assertEquals(expectedIds, actuals);
        }
    }

    @Test
    public void testSendMail() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            sTAlertService.sendMail(session, null);
            Assert.fail("Pas d'implementation dans le socle");
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("Not implemented", e.getMessage());
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
    private DocumentModel createRunnableAlert(CoreSession session, boolean isActivated) {
        final Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -5);
        final Calendar end = Calendar.getInstance();
        end.add(Calendar.DAY_OF_MONTH, 5);

        DocumentModel doc = session.createDocumentModel("/", "alert", STAlertConstant.ALERT_DOCUMENT_TYPE);
        AlertSchemaUtils.setIsActivated(doc, isActivated);
        AlertSchemaUtils.setPeriodicity(doc, "1");
        AlertSchemaUtils.setDateValidityBegin(doc, start);
        AlertSchemaUtils.setDateValidityEnd(doc, end);
        return session.createDocument(doc);
    }
}
