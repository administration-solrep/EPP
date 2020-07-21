package fr.dila.solonepp.page.communication.doc.detail.fusion;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.AlertePage;
import fr.dila.solonepp.page.TransmettreParMelPage;
import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.doc.create.fusion.CreateCommDOCFusion;
import fr.dila.solonepp.utils.VerificationUtils;

public class DetailCommDOCFusion extends AbstractDetailComm {

	public static final String NATURE_LOI = "nxw_metadonnees_version_natureLoi_row";
	public static final String TYPE_LOI = "nxw_metadonnees_version_typeLoi_row";

	// public static final String SELECT_VALUE_PG02 =
	// "Fusion de dossiers - Déclaration de politique générale";

	/**
	 * Vérifier la présence de certains champs sur la page
	 */

	public void verifierPresenceChamps() throws ClientException {
		checkChamp("Communication");
		checkChamp("Identifiant communication");
		checkChamp("Identifiant dossier");
		checkChamp("Emetteur");
		checkChamp("Destinataire");
		checkChamp("Horodatage");
		checkChamp("Identifiant Sénat");
		checkChamp("Date accusé de réception");
		checkChamp("Objet");
		checkChamp("Dossier cible");
		checkChamp("Commentaire");
		checkChamp("Autre(s)");
	}

	/**
	 * Créer une alerte
	 */
	public AlertePage creerAlerte() {
		return clickOnButton(BOUTON_VALUE_CREER_UNE_ALERTE, AlertePage.class);
	}

	// /**
	// * Appuie sur le bouton rectifier
	// *
	// * @return
	// */
	// public CompletionPG01Page rectifier() {
	// return clickOnButton(BOUTON_VALUE_RECTIFIER, CompletionPG01Page.class);
	// }
	//
	// public CompletionPG01Page completer() {
	// return clickOnButton(BOUTON_VALUE_COMPLETER, CompletionPG01Page.class);
	// }

	public TransmettreParMelPage transmettreParMel() {
		return clickOnButton(BOUTON_VALUE_TRANSMETTRE_PAR_MEL, TransmettreParMelPage.class);
	}

	/**
	 * Vrai si la page possède le message indiquant qu'elle a été rectifiée
	 * 
	 * @return
	 */
	public Boolean hasRectificationMessage() {
		String messageConfirmationRectification = "La présente version a été rectifiée";
		String xpath = String.format("//*[contains(text(), '%s')]", messageConfirmationRectification);
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
		VerificationUtils.checkLinkPresent("Détail communication", getFlog(), getDriver());
		VerificationUtils.checkLinkPresent("Détail dossier", getFlog(), getDriver());
		VerificationUtils.checkLinkPresent("Historique dossier", getFlog(), getDriver());
		VerificationUtils.checkChamp("Détails communication", getFlog(), getDriver());
	}
	
    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommDOCFusion.class;
    }

}
