package fr.dila.solonepp.webtest.webdriver030;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.RechercheAvanceePage;
import fr.dila.solonepp.page.communication.doc.detail.GeneriqueDetailComm;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

public class TestRecherche extends AbstractEppWebTest  {

	private static final String TYPE_COMMUNICATION_LEX01 = "EVT01";

	@WebTest(description = "Recherche d'une LEX01")
	@TestDocumentation(categories = {"Comm. LEX-01", "Recherche"})
	public void testRecherche_LEX01() {
		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);
		RechercheAvanceePage pageRecherche = corbeillePage.openRecherche();
		
		pageRecherche.checkIfLoaded();
		pageRecherche.selectTypeCommunication(TYPE_COMMUNICATION_LEX01);
		pageRecherche.rechercher();
		GeneriqueDetailComm detail = corbeillePage.navigateToDetailCommByPosition(1, GeneriqueDetailComm.class);
		detail.verificationChargement();
		detail.checkBordereauDataWithWS();
		
		doLogout();
	}
	
	@WebTest(description = "Recherche de deux LEX01 pour vérifier le rafraichissement")
	@TestDocumentation(categories = {"Comm. LEX-01", "Recherche"})
	public void testRecherche_LEX01_vidageCache() {
		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);
		RechercheAvanceePage pageRecherche = corbeillePage.openRecherche();
		
		pageRecherche.checkIfLoaded();
		pageRecherche.selectTypeCommunication(TYPE_COMMUNICATION_LEX01);
		pageRecherche.rechercher();
		GeneriqueDetailComm detail = corbeillePage.navigateToDetailCommByPosition(1, GeneriqueDetailComm.class);
		detail.verificationChargement();
		detail = corbeillePage.navigateToDetailCommByPosition(2, GeneriqueDetailComm.class);
		detail.verificationChargement();
		detail.checkBordereauDataWithWS();
		
		doLogout();
	}
}
