package fr.dila.solonepp.webtest.webdriver020;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.RechercheRapidePage;
import fr.dila.solonepp.page.TransmettreParMelPage;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm02Page;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test reprenant l'ancien test sélénium Transmettre par mail :
 * 
 * @author jgomez
 *
 */
public class TestTransmettreParMail extends AbstractEppWebTest {
	
	private static final String TYPE_COMMUNICATION = "LEX-02 : PPL - Information du Gouvernement du dépôt";
	private static final String RECUS = "Reçu";
	public static final String DOSSIER = "EFIM1100200R";
	private static final String DUMMY_DESTINATAIRE = "destinataire@gmail.com";
	
	@WebTest(description = "Test du bouton envoi de mail")
	@TestDocumentation(categories = {"Comm. LEX-02", "Recherche rapide", "Transmettre par mail"})
	public void testEnvoiMail() throws InterruptedException, ClientException{
        doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
        corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);
        // Ouvrir procédure législative et ouvrir Emis
        corbeillePage.deployFirstElementCorbeille();
        corbeillePage.waitAndOpenCorbeille(RECUS);
        
        // Recherche rapide de : LEX-02 : PPL - Information du Gouvernement du dépôt
        RechercheRapidePage rechercheRapidePage = corbeillePage.openRechercheRapide();
        rechercheRapidePage.setNor(DOSSIER);
        rechercheRapidePage.rechercher();

        // Cliquer sur EFIM1100200R
        DetailComm02Page detailPage = corbeillePage.navigateToDetailCommByLinkNor(DOSSIER, DetailComm02Page.class);
        detailPage.verifierPresenceLinks();
        TransmettreParMelPage transmettrePage = detailPage.transmettreParMel();
        transmettrePage.verifierChamps();
        transmettrePage.setObjet(TYPE_COMMUNICATION);
        transmettrePage.setDestinataires(DUMMY_DESTINATAIRE);
        transmettrePage.setMessage("message complémentaire a transmettre");
        transmettrePage.envoyer();
        doLogout();
		return;
	}

}
