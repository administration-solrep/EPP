package fr.dila.solonepp.page.communication.abstracts;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.dila.solonepp.page.EppWebPage;
import fr.dila.solonepp.utils.VerificationUtils;
import fr.sword.naiad.commons.webtest.WebPage;
import fr.sword.xsd.solon.epp.EvenementType;

public abstract class AbstractDetailComm extends EppWebPage {

	public static final String BOUTON_VALUE_CREER_UNE_ALERTE = "Créer alerte";
	public static final String BOUTON_VALUE_TRANSMETTRE_PAR_MEL = "Transmettre par mél.";
	public static final String BOUTON_VALUE_COMPLETER = "Compléter";
	public static final String BOUTON_VALUE_RECTIFIER = "Rectifier";
	
    public static final String COMMUNICATION = "nxw_metadonnees_evenement_libelle";
    public static final String IDENTIFIANT_COMMUNICATION = "nxw_metadonnees_evenement_identifiant_row";
    public static final String IDENTIFIANT_DOSSIER = "nxw_metadonnees_evenement_identifiant_dossier_row";
    public static final String EMETTEUR = "nxw_metadonnees_evenement_emetteur_row";
    public static final String DESTINATAIRE = "nxw_metadonnees_evenement_destinataire";
    public static final String COPIE = "nxw_metadonnees_evenement_copie_row";
    public static final String HORODATAGE = "nxw_metadonnees_version_horodatage_row";
    public static final String NIVEAU_LECTURE = "nxw_metadonnees_version_niveauLecture_row";
    public static final String OBJET = "nxw_metadonnees_version_objet_row";
    public static final String NOR = "nxw_metadonnees_version_nor_row";
    public static final String COMMENTAIRE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_description";
    public static final String AUTEUR = "nxw_metadonnees_version_auteur_row";
    public static final String COAUTEUR = "nxw_metadonnees_version_coauteur_row";
    public static final String INTITULE = "nxw_metadonnees_version_intitule_row";
    public static final String ID_COMMUNICATION_PRECEDENTE = "nxw_metadonnees_evenement_identifiant_precedent_row";
    public static final String BOUTON_VALUE_CREATENEXTCOMM = "evenement_metadonnees:subviewCreateEvenementSucc:createEvenementSuccSubmitButton";

    
    public static final String MODIFIER_BTN = "//*[@value='Modifier']";
    public static final String TRAITE_BTN = "//*[@value='Traité']";
    public static final String TRAITER_BTN = "//*[@value='Traiter']";
    public static final String Completer_BTN = "//*[@value='Compléter']";
    public static final String CONFIRMER_BTN = "popup_ok";

    public void checkValueStartWith(final String id, final String value) {
        getFlog().check("Verifie la valeur de l'element : " + id + ", expected start: " + value);
        checkValue(id, value, Boolean.FALSE);
    }
    
    /**
     * Utilise le select de la page de détail pour créer une communication successive
     * @param <T>
     * @param evenementType
     * @param pageClazz
     * @return null si la communication donnée ne peut pas être créée, la page de création sinon
     */
    public <T extends AbstractCreateComm> T navigateToCreateCommSucc(final EvenementType evenementType, final Class<T> pageClazz) {        
        getFlog().action("Selectionne \"" + evenementType.value() + "\" dans le select de création de communication successive");
        final WebElement evtTypeElem = getDriver().findElement(By.id("evenement_metadonnees:subviewCreateEvenementSucc:selectEvenementSuccessifTypeId"));
        final Select select = new Select(evtTypeElem);
        List<WebElement> options = select.getOptions();
        boolean hasEvenementType = false;
        // On parcourt les options pour vérifier que la création de l'évènement souhaité est possible
        for (WebElement option : options) {
            if (option.getAttribute("value").equals(evenementType.value())) {
                hasEvenementType = true;
                break;
            }
        }
        if (!hasEvenementType) {
            return null;
        }
        select.selectByValue(evenementType.value());

        final WebElement createBtn = getDriver().findElement(By.id("evenement_metadonnees:subviewCreateEvenementSucc:createEvenementSuccSubmitButton"));
        getFlog().actionClickButton("Créer communication successive");
        createBtn.click();
        return getPage(pageClazz);
    }
    

	/**
	 * Vérifie qu'un champ est présent en tant que texte brut sur la page
	 * @param champ le champ à tester
	 * @throws ClientException 
	 */
	protected void checkChamp(String champ) throws ClientException {
		VerificationUtils.checkChamp(champ, getFlog(), getDriver());
	}
	

	/**
	 * 
	 * @param value La valeur de l'option de la communication à créer
	 * @param la classe de la page retournée
	 * @return
	 */
	public <T extends EppWebPage> T navigateToNextComm(final String value, final Class<T> pageClazz) {
        final WebElement evtTypeElem = getDriver().findElement(By.id("evenement_metadonnees:subviewCreateEvenementSucc:selectEvenementSuccessifTypeId"));
        getFlog().action("Selectionne \"" + value + "\" dans le select \"Créer communication successive\"");
        final Select select = new Select(evtTypeElem);
        select.selectByValue(value);

        final WebElement createBtn = getDriver().findElement(By.id(BOUTON_VALUE_CREATENEXTCOMM));
        getFlog().actionClickButton("Créer communication successive");
        createBtn.click();
        return getPage(pageClazz);
    }

//	@Override
//	public void checkValue(String id, String value) {
//		 waitForPageSourcePartDisplayed(By.id(id), TIMEOUT_IN_SECONDS);
//	        final WebElement element = getDriver().findElement(By.id(id));
//	        if (element == null) {
//	            getFlog().checkFailed(id + " not found");
//	        } else {
//	            if(!element.getText().equalsIgnoreCase(value)){
//	            	getFlog().checkFailed(id + " not found");
//	            }
//	        }
//	}
	
    public <T extends AbstractDetailComm> T traite(final Class<T> pageClazz) {
        By byPath  = By.xpath(TRAITE_BTN) ;
        new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(byPath));
        final WebElement elem = getDriver().findElement(byPath);
        getFlog().actionClickButton("Traité");
        elem.click();

        waitForPageSourcePart("Transition traité effectuée", TIMEOUT_IN_SECONDS);
        return getPage(pageClazz);
    }    
    
    public <T extends WebPage> T traiter(final Class<T> pageClazz) {
        final WebElement elem = getDriver().findElement(By.xpath(TRAITER_BTN));
        getFlog().actionClickButton("Traiter");
        elem.click();
        waitForPageSourcePartDisplayed(By.id("popup_message"), 15);
        final WebElement elemConfirm = getDriver().findElement(By.id(CONFIRMER_BTN));
        elemConfirm.click();
        return getPage(pageClazz);

    }
    
    public <T extends AbstractDetailComm> T completer(final Class<T> pageClazz) {
        final WebElement elem = getDriver().findElement(By.xpath(Completer_BTN));
        getFlog().actionClickButton("Compléter");
        elem.click();
        elem.click();

        waitForPageSourcePart("Modification communication :", TIMEOUT_IN_SECONDS);
        return getPage(pageClazz);

    }
	
    protected abstract Class<? extends AbstractCreateComm> getModifierResultPageClass();
	

}
