package fr.dila.solonepp.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

/**
 * Classe qui représente la gestion des utilisateurs
 * @author jgomez
 *
 */
public class GestionUtilisateurPage extends EppWebPage{

	
	@FindBy(how = How.ID, using = "createUserActionsForm:createUserButton")
	private WebElement linkAjouterUnUtilisateur;

	public CreerUtilisateurPage ajouterUnUtilisateur() {
		getFlog().startAction("Ouverture de la page organigramme");
		linkAjouterUnUtilisateur.click();
		CreerUtilisateurPage ajoutUtilisateurPage = getPage(CreerUtilisateurPage.class);
		getFlog().endAction();
		return ajoutUtilisateurPage;
	}
	
	
	
}
