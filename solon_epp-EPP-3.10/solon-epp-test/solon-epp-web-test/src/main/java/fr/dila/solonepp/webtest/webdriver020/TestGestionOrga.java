package fr.dila.solonepp.webtest.webdriver020;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.AdministrationPage;
import fr.dila.solonepp.page.Organigramme;
import fr.dila.solonepp.page.organigramme.CreerPostePage;
import fr.dila.solonepp.page.organigramme.CreerUniteStructurellePage;
import fr.dila.solonepp.page.organigramme.OrganigrammeContextMenu;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test reprenant l'ancien test sélénium Création utilisateur:
 * - Connexion en tant qu'adminsgg, ajout d'une unité structurelle et d'un poste de test puis déconnexion
 * 
 * @author jgomez
 *
 */
public class TestGestionOrga extends AbstractEppWebTest {


	private static final String POSTE_DE_TEST = "Poste de test sélénium";
	
	private static final String NOM_UNITE_STRUCTURELLE_TEST = "Unité structurelle de test 2";
	
	private static final String GOUVERNEMENT_NODE = "Gouvernement";

	@WebTest(description = "Création d'une unité structurelle et d'un poste")
	@TestDocumentation(categories = {"Organigramme", "Administration"})
	public void testCreationUniteStructurelleEtPoste() throws InterruptedException{
        doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
        AdministrationPage adminPage = corbeillePage.openAdministration();
        Organigramme organigrammePage = adminPage.getGestionOrganigrammePage();
		
        getFlog().startAction("Ajout d'une unité structurelle");
		OrganigrammeContextMenu orgaContextMenu1 = organigrammePage.rightClickOn(GOUVERNEMENT_NODE);
		CreerUniteStructurellePage newUniteStructurelle = orgaContextMenu1.goToCreerUniteStructurelle();
		newUniteStructurelle.setLibelle(NOM_UNITE_STRUCTURELLE_TEST);
		newUniteStructurelle.enregistrer();
		getFlog().endAction();
		
		getFlog().startAction("Ajout d'un poste");
		organigrammePage.refresh();
		organigrammePage.waitForElement(NOM_UNITE_STRUCTURELLE_TEST);
		OrganigrammeContextMenu orgaContextMenu2 = organigrammePage.rightClickOn(NOM_UNITE_STRUCTURELLE_TEST);
		CreerPostePage newPoste = orgaContextMenu2.goToCreerPoste();
		newPoste.setLibelle(POSTE_DE_TEST);
		newPoste.enregistrer();
		getFlog().endAction();
		doLogout();
		return;
	}
	
}
