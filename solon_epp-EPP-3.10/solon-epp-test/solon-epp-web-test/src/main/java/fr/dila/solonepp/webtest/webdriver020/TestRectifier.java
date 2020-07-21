package fr.dila.solonepp.webtest.webdriver020;

import junit.framework.Assert;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.RechercheRapidePage;
import fr.dila.solonepp.page.RectificationPage;
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
public class TestRectifier extends AbstractEppWebTest {
	
	private static final String TYPE_COMMUNICATION = "EVT01";
	private static final String EMIS = "Emis";
	public static final String DOSSIER = "ARTL1100002Y";
	
	@WebTest(description = "Test de la rectification")
	@TestDocumentation(categories = {"Comm. LEX-01", "Recherche rapide", "Rectification"})
	public void testBoutonAlerte() throws InterruptedException, ClientException{
        doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
        corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);
        // Ouvrir procédure législative et ouvrir Emis
        corbeillePage.deployFirstElementCorbeille();
        corbeillePage.waitAndOpenCorbeille(EMIS);
        
        // Recherche rapide de : LEX-01 : Pjl - Dépôt
        RechercheRapidePage rechercheRapidePage = corbeillePage.openRechercheRapide();
        rechercheRapidePage.selectTypeCommunication(TYPE_COMMUNICATION);
        rechercheRapidePage.setNor(DOSSIER);
        rechercheRapidePage.rechercher();

        // Cliquer sur AGRE00000
        DetailComm02Page detailPage = corbeillePage.navigateToDetailCommByLinkNor(DOSSIER, DetailComm02Page.class);

        // Vérifier les métadonnées de la page
        detailPage.verifierPresenceChamps();
        
        // Cliquer sur rectification
        RectificationPage rectificationPage = detailPage.rectifier();
        
        // Mettre les nouveaux champs de la page de rectification
        String newNor = "CCOTEST1";
        String typeLoi = "Loi cadre";
        rectificationPage.setNor(newNor);
        rectificationPage.setTypeLoi(typeLoi);
        rectificationPage.publier();
        
        Boolean hasBeenRectified = detailPage.hasRectificationMessage();
        Assert.assertTrue("La page n'a pas été rectifiée", hasBeenRectified);
        doLogout();
		return;
	}
	
}
