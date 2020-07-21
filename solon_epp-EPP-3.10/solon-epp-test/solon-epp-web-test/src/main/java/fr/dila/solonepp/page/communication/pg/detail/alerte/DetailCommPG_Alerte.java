package fr.dila.solonepp.page.communication.pg.detail.alerte;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.AlertePage;
import fr.dila.solonepp.page.TransmettreParMelPage;
import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.complete.generique.CompletionPGGenerique;
import fr.dila.solonepp.utils.VerificationUtils;

public class DetailCommPG_Alerte extends AbstractDetailComm {

	/**
	 * Vérifier la présence de certains champs sur la page
	 */

	public void verifierPresenceChamps() throws ClientException {
		checkChamp("Communication");
		checkChamp("Identifiant communication");
		checkChamp("Identifiant dossier");
		checkChamp("Emetteur");
		checkChamp("Destinataire");
		checkChamp("Copie");
		checkChamp("Horodatage");
		checkChamp("Objet");
		checkChamp("Commentaire");

	}

	/**
	 * Créer une alerte
	 */
	public AlertePage creerAlerte() {
		return clickOnButton(BOUTON_VALUE_CREER_UNE_ALERTE, AlertePage.class);
	}


	public CompletionPGGenerique completer() {
		return clickOnButton(BOUTON_VALUE_COMPLETER, CompletionPGGenerique.class);
	}

	public TransmettreParMelPage transmettreParMel() {
		return clickOnButton(BOUTON_VALUE_TRANSMETTRE_PAR_MEL,
				TransmettreParMelPage.class);
	}

	/**
	 * Vrai si la page possède le message indiquant qu'elle a été rectifiée
	 * 
	 * @return
	 */
	public Boolean hasRectificationMessage() {
		String messageConfirmationRectification = "La présente version a été rectifiée";
		String xpath = String.format("//*[contains(text(), '%s')]",
				messageConfirmationRectification);
		List<WebElement> elts = getDriver().findElements(By.xpath(xpath));
		if (elts.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Verifier la présence des onglets sur la page
	 * 
	 * @throws ClientException
	 */
	public void verifierPresenceLinks() throws ClientException {
		VerificationUtils.checkLinkPresent("Détail communication", getFlog(),
				getDriver());
		VerificationUtils.checkLinkPresent("Détail dossier", getFlog(),
				getDriver());
		VerificationUtils.checkLinkPresent("Historique dossier", getFlog(),
				getDriver());
		VerificationUtils.checkChamp("Détails communication", getFlog(),
				getDriver());
	}

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return null;
    }
}
