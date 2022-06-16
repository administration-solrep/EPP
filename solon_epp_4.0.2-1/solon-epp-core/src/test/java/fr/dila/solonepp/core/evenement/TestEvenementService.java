package fr.dila.solonepp.core.evenement;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.core.SolonEppFeature;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test du service des événements.
 *
 * @author jtremeaux
 */
@RunWith(FeaturesRunner.class)
@Features(SolonEppFeature.class)
public class TestEvenementService {
    @Inject
    private CoreSession session;

    /**
     * Test du filtre des destinataires en copie.
     *
     * @throws Exception
     */
    @Test
    public void testFilterDestinataireCopie() throws Exception {
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        DocumentModel evenementDoc = evenementService.createBareEvenement(session);
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        evenement.setEmetteur("emetteur");
        evenement.setDestinataire("destinataire");
        List<String> destinataireCopieList = new ArrayList<String>();
        destinataireCopieList.add("emetteur");
        destinataireCopieList.add("destinataire");
        destinataireCopieList.add("copie1");
        destinataireCopieList.add("copie1");
        destinataireCopieList.add("copie1");
        destinataireCopieList.add("copie2");
        destinataireCopieList.add("copie2");
        destinataireCopieList.add("copie3");
        evenement.setDestinataireCopie(destinataireCopieList);

        evenementService.filterDestinataireCopie(evenementDoc);
        destinataireCopieList = evenement.getDestinataireCopie();
        Assert.assertEquals(3, destinataireCopieList.size());
        assert (destinataireCopieList.contains("copie1"));
        assert (destinataireCopieList.contains("copie2"));
        assert (destinataireCopieList.contains("copie3"));
    }
}
