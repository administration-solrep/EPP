package fr.dila.solonepp.core.evenement;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.core.SolonEppRepositoryTestCase;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;

/**
 * Test du service des événements.
 * 
 * @author jtremeaux
 */
public class TestEvenementService extends SolonEppRepositoryTestCase {

    /**
     * Test du filtre des destinataires en copie.
     * 
     * @throws Exception
     */
    public void testFilterDestinataireCopie() throws Exception {
    	openSession();
    	
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
        assertEquals(3, destinataireCopieList.size());
        assert(destinataireCopieList.contains("copie1"));
        assert(destinataireCopieList.contains("copie2"));
        assert(destinataireCopieList.contains("copie3"));
        
        closeSession();
    }
}
