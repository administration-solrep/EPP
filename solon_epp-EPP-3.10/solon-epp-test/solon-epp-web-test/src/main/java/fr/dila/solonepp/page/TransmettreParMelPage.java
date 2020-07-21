package fr.dila.solonepp.page;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.solonepp.utils.VerificationUtils;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;


/**
 * La page d'alerte
 * @author jgomez
 *
 */
public class TransmettreParMelPage extends EppWebPage {

	private static final String ENVOYER_VALUE = "Envoyer";

	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Objet" )
	private WebElement objetElt;
	
	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Destinataires" )
	private WebElement destinataireElt;
	
	@FindBy(how = How.ID, using = "evenement_metadonnees:nxl_transmettre_mail_evenement:nxw_message_mail")
	private WebElement messageElt;
	
	/**
	 * Mise en place de l'objet pour le mel
	 * @param objet l'objet
	 */
	public void setObjet(String objet){
		fillField("Objet", objetElt, objet);
	}
	
	/**
	 * Mise en place des destinataires pour le mel
	 * @param destinataires les destinataires
	 */
	public void setDestinataires(String destinataires){
		fillField("Destinataires", destinataireElt, destinataires);
	}
	
	
	/**
	 * Mise en place du message pour le mel
	 * @param message le message à envoyer
	 */
	public void setMessage(String message){
		fillField("Message", messageElt, message);
	}
	
	/**
	 * Envoyer le mel
	 */
	public void envoyer() {
		clickOnButton(ENVOYER_VALUE, CorbeillePage.class);
	}

	public void verifierChamps() throws ClientException, InterruptedException {
		VerificationUtils.checkChamp("Objet", getFlog(), getDriver());
		VerificationUtils.checkChamp("Destinataires", getFlog(), getDriver());
		VerificationUtils.checkChamp("Message", getFlog(), getDriver());
	}
	
	
}
