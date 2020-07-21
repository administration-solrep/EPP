package fr.dila.solonepp.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class AdministrationPage extends EppWebPage{

	
	@FindBy(how = How.LINK_TEXT, using = "Gestion de l'organigramme")
	private WebElement linkGestionOrganigramme;
	
	@FindBy(how = How.LINK_TEXT, using = "Gestion des utilisateurs")
	private WebElement linkGestionUtilisateurs;

	public Organigramme getGestionOrganigrammePage() {
		getFlog().startAction("Ouverture de la page organigramme");
		linkGestionOrganigramme.click();
		Organigramme orga = getPage(Organigramme.class);
		getFlog().endAction();
		return orga;
	}

	public GestionUtilisateurPage getGestionUtilisateurs() {
		getFlog().startAction("Ouverture de la page gestion des utilisateurs");
		linkGestionUtilisateurs.click();
		GestionUtilisateurPage utilisateurPage = getPage(GestionUtilisateurPage.class);
		getFlog().endAction();
		return utilisateurPage;
	}
	
	
	
}
