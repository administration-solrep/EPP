package fr.dila.solonepp.webtest.webdriver020;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.AlertePage;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm02Page;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test reprenant l'ancien test sélénium Bouton Alerte :
 * - Connexion en tant qu'adminsgg et déconnexion
 * 
 * @author jgomez
 *
 */
public class TestBoutonAlerte extends AbstractEppWebTest {
	
	private static final String EMIS = "Emis";
	public static final String DOSSIER = "AGRE000001";
	
	@WebTest(description = "Test du bouton d'alerte")
	@TestDocumentation(categories = {"Comm. Alerte"})
	public void testBoutonAlerte() throws InterruptedException, ClientException{
        doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
        corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);
        // Ouvrir procédure législative et ouvrir Emis
        corbeillePage.deployFirstElementCorbeille();
        corbeillePage.waitAndOpenCorbeille(EMIS);
        
        // Cliquer sur AGRE00000
        DetailComm02Page detailPage = corbeillePage.navigateToDetailCommByLinkNor(DOSSIER, DetailComm02Page.class);

        // Vérifier les métadonnées de la page
        detailPage.verifierPresenceChamps();
        // Cliquer sur créer alerte
        AlertePage alertePage = detailPage.creerAlerte();
        
        // Vérifier texte : Création communication successive : Alerte
        // Cliquer sur destinataire
        alertePage.setDestinataire("Sénat");
        // Ajouter un commentaire
        alertePage.addCommentaire("Commentaire de l'alerte");
        // Cliquer sur créer
        alertePage.publier();
        
        doLogout();
		return;
	}
	
}
