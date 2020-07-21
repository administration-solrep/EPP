package fr.dila.solonepp.page.communication.pg.detail;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.AlertePage;
import fr.dila.solonepp.page.EppWebPage;
import fr.dila.solonepp.page.RechercheRapidePage;
import fr.dila.solonepp.page.TransmettreParMelPage;
import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.complete.CompletionPG02Page;
import fr.dila.solonepp.page.communication.pg.create.CreateCommPG02Page;
import fr.dila.solonepp.utils.VerificationUtils;

public class DetailCommPG_02 extends AbstractDetailComm {
    
    private static final String BOUTON_VALUE_CREATENEXTCOMM = "evenement_metadonnees:subviewCreateEvenementSucc:createEvenementSuccSubmitButton";
    
	public static final String NATURE_LOI = "nxw_metadonnees_version_natureLoi_row";
    public static final String TYPE_LOI = "nxw_metadonnees_version_typeLoi_row";
    
    public static final String SELECT_VALUE_PG02 = "PG-02 : Information sur la déclaration de politique générale du PM devant l’AN (49-1C)";

    /**
     * Vérifier la présence de certains champs sur la page
     */
    
	public void verifierPresenceChamps() throws ClientException{
		checkChamp("Communication");
		checkChamp("Identifiant communication");
		checkChamp("Identifiant dossier");
		checkChamp("Emetteur");
		checkChamp("Destinataire");
		checkChamp("Copie");
		checkChamp("Horodatage");
		checkChamp("Date accusé de réception");
		checkChamp("Objet");
		checkChamp("Commentaire");
		checkChamp("Date de la présentation");
		checkChamp("Date de la lettre du premier ministre");
		checkChamp("Lettre Premier Ministre");
		checkChamp("Autre(s)");
	}

	/**
	 * Créer une alerte
	 */
	public AlertePage creerAlerte() {
		return clickOnButton(BOUTON_VALUE_CREER_UNE_ALERTE, AlertePage.class);
	}

	/**
	 * Appuie sur le bouton rectifier
	 * @return
	 */
	public CompletionPG02Page rectifier() {
		return clickOnButton(BOUTON_VALUE_RECTIFIER, CompletionPG02Page.class);
	}
	
	public CompletionPG02Page completer() {
		return clickOnButton(BOUTON_VALUE_COMPLETER, CompletionPG02Page.class);
	}

	public TransmettreParMelPage transmettreParMel() {
		return clickOnButton(BOUTON_VALUE_TRANSMETTRE_PAR_MEL, TransmettreParMelPage.class);
	}

	/**
	 * Vrai si la page possède le message indiquant qu'elle a été rectifiée
	 * @return
	 */
	public Boolean hasRectificationMessage() {
		String messageConfirmationRectification = "La présente version a été rectifiée";
		String xpath = String.format("//*[contains(text(), '%s')]", messageConfirmationRectification);
		List<WebElement> elts = getDriver().findElements(By.xpath(xpath));
		if (elts.isEmpty()){
			return false;
		}
		return true;
	}

	/**
	 * Verifier la présence des onglets sur la page
	 * @throws ClientException
	 */
	public void verifierPresenceLinks() throws ClientException {
		VerificationUtils.checkLinkPresent("Détail communication", getFlog(), getDriver());
		VerificationUtils.checkLinkPresent("Détail dossier", getFlog(), getDriver());
		VerificationUtils.checkLinkPresent("Historique dossier", getFlog(), getDriver());
		VerificationUtils.checkChamp("Détails communication", getFlog(), getDriver());
	}
	
	public EppWebPage createNextComm(String nextComm){
		
		return clickOnButton(BOUTON_VALUE_CREATENEXTCOMM, EppWebPage.class);	
	}

	public void searchComm(String string) {
		getPage(RechercheRapidePage.class).setIdComm(string);
		getPage(RechercheRapidePage.class).rechercher();
	}

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommPG02Page.class;
    }
}
