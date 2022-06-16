package fr.dila.ss.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.ss.api.pdf.SSPdfService;
import fr.dila.ss.api.service.ActualiteService;
import fr.dila.ss.api.service.SSExcelService;
import fr.dila.ss.api.service.SSJournalService;
import fr.dila.ss.api.service.SSProfilUtilisateurService;
import fr.dila.ss.api.service.SSTreeService;
import fr.dila.ss.core.test.SolrepFeature;
import fr.dila.st.core.service.STServiceLocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolrepFeature.class)
public class SSServiceLocatorIT {

    @Test
    public void testServices() {
        assertThat(STServiceLocator.getJournalService()).isInstanceOf(SSJournalService.class);
        assertThat(SSServiceLocator.getSSJournalService()).isInstanceOf(SSJournalService.class);
        assertThat(SSServiceLocator.getSSTreeService()).isInstanceOf(SSTreeService.class);
        assertThat(SSServiceLocator.getActualiteService()).isInstanceOf(ActualiteService.class);
        assertThat(SSServiceLocator.getSsProfilUtilisateurService()).isInstanceOf(SSProfilUtilisateurService.class);
        assertThat(SSServiceLocator.getSSExcelService()).isInstanceOf(SSExcelService.class);
        assertThat(SSServiceLocator.getSSPdfService()).isInstanceOf(SSPdfService.class);
    }
}
