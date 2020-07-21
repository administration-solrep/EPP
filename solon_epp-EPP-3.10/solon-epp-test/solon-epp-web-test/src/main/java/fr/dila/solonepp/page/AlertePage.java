package fr.dila.solonepp.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;

/**
 * La page d'alerte
 * 
 * @author jgomez
 * 
 */
public class AlertePage extends AbstractCreateComm {

	public static final String DESTINATAIRE = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_destinataire_findButton";

	public static final String COMMENTAIRE_ID = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_description";

	public static final String PUBLIER_BTN_ID = "evenement_metadonnees:createEvtSucc_submitButton";

	/**
	 * Met en place le destinataire
	 * 
	 * @param destinataire
	 *            le destinataire
	 */
	public void setDestinataire(String destinataire) {
		selectInOrganigramme(destinataire, DESTINATAIRE);
	}

	/**
	 * Ajoute un commentaire
	 * 
	 * @param commentaire
	 */
	public void addCommentaire(String commentaire) {
		final WebElement elem = getDriver().findElement(By.id(COMMENTAIRE_ID));
		fillField("Commentaire", elem, commentaire);
	}


	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return null;
	}
	
	@Override
	public <T extends AbstractDetailComm> T publier(String messageRetour) {
		final WebElement elem = getDriver().findElement(By.id("evenement_metadonnees:createEvtSucc_submitButton"));
		getFlog().actionClickButton("Publier");
		elem.click();
	
		getFlog().check("Test du message de confirmation : " + messageRetour);
		waitForPageSourcePart(messageRetour, TIMEOUT_IN_SECONDS);
		Class clazz = getCreateResultPageClass();
		if (clazz != null) {
			return (T) getPage(clazz);
		}
		return null;
	}
}
