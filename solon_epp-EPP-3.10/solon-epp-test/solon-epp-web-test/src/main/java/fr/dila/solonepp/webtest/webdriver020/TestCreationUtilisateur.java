package fr.dila.solonepp.webtest.webdriver020;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.AdministrationPage;
import fr.dila.solonepp.page.ChangePasswordPage;
import fr.dila.solonepp.page.CreerUtilisateurPage;
import fr.dila.solonepp.page.GestionUtilisateurPage;
import fr.dila.solonepp.page.LoginPage;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test reprenant l'ancien test sélénium Création utilisateur:
 * - Connexion en tant qu'adminsgg, ajout d'un utilisateur puis déconnexion
 * 
 * @author jgomez
 *
 */
public class TestCreationUtilisateur extends AbstractEppWebTest {


	private static final String PASSWORD_USER_TEST_2 = "wQc5ldwj";
	private static final String LOGIN_USERTEST_2 = "usertest2";
	private static final String OK_PASSWORD = "Ajdhft45!";

	@WebTest(description = "Création d'un utilisateur")
	@TestDocumentation(categories = {"Utilisateur", "Administration"})
	public void testCreationUtilisateur(){
        doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
        AdministrationPage adminPage = corbeillePage.openAdministration();
        GestionUtilisateurPage orgaPage = adminPage.getGestionUtilisateurs();
        CreerUtilisateurPage creerUtilisateur = orgaPage.ajouterUnUtilisateur();
        creerUtilisateur.creerUtilisateur("usertest1", "Ly", "Sanh", true, false, "15 rue Jean", "Lyon", "000000000", "sanh.ly@sword-group.com", "Administrateur fonctionnel", "Poste technique gouvernement");
        doLogout();
		return;
	}
	
	@WebTest(description = "Test de reset de password pour un nouvel utilisateur")
	@TestDocumentation(categories = {"Utilisateur", "Gestion mot de passe"})
	public void testResetPassword(){
	    ChangePasswordPage changePasswordPage = doLoginAsNewUser(LOGIN_USERTEST_2, PASSWORD_USER_TEST_2);
	    changePasswordPage.changePasswordAndSubmit(OK_PASSWORD, LoginPage.class);
	    doLoginAs(LOGIN_USERTEST_2, OK_PASSWORD);
	    corbeillePage.verifierIdentite(LOGIN_USERTEST_2);
	    doLogout();
	}
}
