package fr.dila.solonepp.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;


/**
 * La page d'alerte
 * @author jgomez
 *
 */
public class RectificationPage extends EppWebPage {

	public static final String DESTINATAIRE = "nxw_metadonnees_evenement_destinataire_row";
	
	public static final String COMMENTAIRE_ID = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_description";
	
	public static final String PUBLIER_BTN_VALUE = "Publier";
	
	@FindBy(how = How.ID, using="evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_nor")
	private WebElement norElt;
	
	@FindBy(how = How.ID, using="evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_typeLoi")
	private WebElement selectTypeLoiElt;
	
	/**
	 * Met en place le destinataire
	 * @param destinataire le destinataire
	 */
	public void setDestinataire(String destinataire){
		selectInOrganigramme(destinataire, DESTINATAIRE);
	}

	/**
	 * Ajoute un commentaire
	 * @param commentaire
	 */
	public void addCommentaire(String commentaire) {
        final WebElement elem = getDriver().findElement(By.id(COMMENTAIRE_ID));
        fillField("Commentaire", elem, commentaire);
	}

	/**
	 * Publie l'alerte
	 */
	public void publier() {
		clickOnButton(PUBLIER_BTN_VALUE, CorbeillePage.class);
	}

	public void setNor(String nor) {
		fillField("Nor", norElt, nor);
	}

	public void setTypeLoi(String typeLoi) {
		Select selectTypeLoi = new Select(selectTypeLoiElt);
		selectTypeLoi.selectByVisibleText(typeLoi);
	}
	
	
}
