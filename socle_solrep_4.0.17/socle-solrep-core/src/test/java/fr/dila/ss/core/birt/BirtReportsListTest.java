package fr.dila.ss.core.birt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.birt.BirtReports;
import fr.dila.ss.api.birt.ReportProperty;
import fr.dila.ss.api.constant.SSBirtConstants;
import fr.dila.st.api.service.ConfigService;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
public class BirtReportsListTest {
    @Mock
    @RuntimeService
    private ConfigService mockConfigService;

    private BirtGenerationService service;

    @Before
    public void setUp() {
        service = new BirtGenerationServiceImpl();
    }

    @Test
    public void testBirtReportsFileReading() throws Exception {
        Mockito
            .when(mockConfigService.getValue(Matchers.eq(SSBirtConstants.BIRT_REPORTS_LIST_FILE_PROP)))
            .thenReturn("src/test/resources/birtReports/list_reports.xml");

        BirtReports reportsObj = service.getReports();
        assertNotNull(reportsObj);

        Map<String, BirtReport> reports = reportsObj.getBirtReportMap();
        assertNotNull(reports);
        assertEquals(3, reports.size());

        String id = "stat01";
        BirtReport report = reports.get(id);
        assertNotNull(report);
        assertEquals(id, report.getId());
        assertEquals(
            "MIN11. Taux de réponse du Gouvernement aux questions écrites, depuis le début de la législature",
            report.getTitle()
        );
        assertEquals("MIN11_txGlobal.rptdesign", report.getFile());
        assertTrue(report.getProperties().isEmpty());

        id = "stat07";
        report = reports.get(id);
        assertNotNull(report);
        assertEquals(id, report.getId());
        assertEquals(
            "SGG03. Variations mensuelles du nombre de réponses publiées et du taux de réponse par ministère",
            report.getTitle()
        );
        assertEquals("SGG03_variationNbReponsePublie.rptdesign", report.getFile());
        Map<String, ReportProperty> props = report.getProperties();
        assertEquals(1, props.size());
        assertEquals("true", props.get("birtreportmodel:droitVisibiliteRestraintSGG"));

        id = "stat17";
        report = reports.get(id);
        assertNotNull(report);
        assertEquals(id, report.getId());
        assertEquals("SGG02. Questions écrites en cours de traitement (suivi par date)", report.getTitle());
        assertEquals("SGG02_QEEnCoursSuiviDates.rptdesign", report.getFile());
        props = report.getProperties();
        assertEquals(2, props.size());
        assertEquals("MINISTERE", props.get("birtreportmodel:parametreOrganigramme"));
        assertEquals("true", props.get("birtreportmodel:droitVisibiliteRestraintSGG"));
    }
}
