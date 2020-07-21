package fr.dila.solonepp.page.communication.doc.create.alerte;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.AlertePage;
import fr.dila.solonepp.page.communication.doc.detail.alerte.DetailCommDOC_Alerte;

public class CreateDOCAlerte extends AlertePage {


	public static final String OBJET_ID = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_objet";

	public static final String PUBLIER_BTN_ID = "evenement_metadonnees:createEvtSucc_submitButton";


	/**
	 * Défini l'objet de l'alerte
	 * 
	 * @param objet
	 */
	public void setObjet(String objet) {
		final WebElement elem = getDriver().findElement(By.id(OBJET_ID));
		fillField("Objet", elem, objet);
	}

	public void createAlerte(String objet, String destinataire) {
		setDestinataire(destinataire);
		setObjet(objet);

		publier();
	}

	protected Class<DetailCommDOC_Alerte> getCreateResultPageClass() {
		return DetailCommDOC_Alerte.class;
	}
}
