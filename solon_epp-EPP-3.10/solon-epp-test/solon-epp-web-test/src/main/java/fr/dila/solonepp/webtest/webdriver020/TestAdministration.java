package fr.dila.solonepp.webtest.webdriver020;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test reprenant l'ancien test sélénium Administration :
 * - Connexion en tant qu'adminsgg et déconnexion
 * 
 * @author jgomez
 *
 */
public class TestAdministration extends AbstractEppWebTest {

	
	@WebTest(description = "Connexion en tant qu'adminsgg")
	@TestDocumentation(categories = {"Connexion"})
	public void testConnexionAdminsgg(){
        doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
        corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);        
        doLogout();
		
	}
	
}
