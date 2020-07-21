package fr.dila.solonepp.page.communication.pg.create.alerte;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.AlertePage;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.detail.alerte.DetailCommPG_Alerte;

/**
 * La page d'alerte
 * @author jgomez
 *
 */
public class AlertePGPage extends AlertePage {

	public static final String DESTINATAIRE = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_destinataire_findButton";
	
	public static final String COMMENTAIRE_ID = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_description";
	public static final String OBJET_ID = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_objet";

	public static final String PUBLIER_BTN_ID = "evenement_metadonnees:createEvtSucc_submitButton";
	
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
	 * Défini l'objet de l'alerte
	 * @param objet
	 */
	public void setObjet(String objet) {
        final WebElement elem = getDriver().findElement(By.id(OBJET_ID));
        fillField("Objet", elem, objet);
	}

	public DetailCommPG_Alerte createAlerte(String objet, String destinataire) {
		setDestinataire(destinataire);
		setObjet(objet);
		
		return publier();
	}
	
	@Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommPG_Alerte.class;
    }
}
