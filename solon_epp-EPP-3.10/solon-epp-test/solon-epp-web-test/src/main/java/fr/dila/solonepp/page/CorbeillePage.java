package fr.dila.solonepp.page;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.st.webdriver.model.CommonWebPage;
import fr.sword.xsd.solon.epp.EvenementType;

public class CorbeillePage extends CommonWebPage {

    private static final int TIMEOUT = 300;


	public <T extends AbstractCreateComm> T navigateToCreateComm(final EvenementType evenementType, final Class<T> pageClazz) {
        final WebElement evtTypeElem = getDriver().findElement(By.xpath("//*[@class=\"userMailboxesContent\"]/div/form/select"));
        getFlog().action("Selectionne \"" + evenementType.value() + "\" dans le select \"Créer communication créatrice\"");
        final Select select = new Select(evtTypeElem);
        select.selectByValue(evenementType.value());

        final WebElement createBtn = getDriver().findElement(By.xpath("//*[@class=\"userMailboxesContent\"]/div/form/input"));
        getFlog().actionClickButton("Créer communication créatrice");
        createBtn.click();
        return getPage(pageClazz);
    }

    public void deployFirstElementCorbeille() {
        getFlog().action("Deploiement premier élément corbeille");
        By corbeilleElem = By.xpath("//div[@id='corbeilleForm:corbeilleTree:childs']/table/tbody/tr/td/div/a");
        waitForPageSourcePart(corbeilleElem, TIMEOUT_IN_SECONDS);
        getDriver().findElement(corbeilleElem).click();
        
    }
    
    /**
     * Ouvre une corbeille donnée
     * @param corbeilleName le nom de la corbeille à ouvrir
     */    
    public void openCorbeille(String corbeilleName) {
        getFlog().actionClickLink(corbeilleName);
        getDriver().findElement(By.xpath("//*[contains(text(), '" + corbeilleName + "')]")).click();
    }
    
    /**
     * Attends qu'une corbeille donnée apparaisse
     * @param corbeilleName le nom de la corbeille à ouvrir
     */    
    public void waitCorbeille(String corbeilleName) {
        getFlog().actionClickLink(corbeilleName);
        waitForPageSourcePartDisplayed(By.xpath("//*[contains(text(), '" + corbeilleName + "')]"), TIMEOUT);
    }
    
    /**
     * Attends que la corbeille apparaisse, puis l'ouvre
     * @param corbeille corbeille
     */
	public void waitAndOpenCorbeille(String corbeille) {
		waitCorbeille(corbeille);
		openCorbeille(corbeille);
	}
    
    /**
     * Ouvre le panneau de recherche rapide
     */
    public RechercheRapidePage openRechercheRapide() {
        getFlog().actionClickLink("Recherche Rapide");
        getDriver().findElement(By.id("toggleRechercheRapideForm:openRechercheRapideButtonId")).click();
        return getPage(RechercheRapidePage.class);
    }
    
    
    /**
     * Ouvre le panneau de recherche rapide
     */
    public RechercheAvanceePage openRecherche () {
        getFlog().actionClickLink("Recherche");
        getDriver().findElement(By.id("userServicesForm:userServicesActionsTable:1:userServicesActionCommandLink")).click();
        return getPage(RechercheAvanceePage.class);
    }
    
    /**
     * Lance l'action de recherche rapide
     */
    public void doRechercheRapide() {
        getFlog().actionClickButton("Rechercher");
        getDriver().findElement(By.id("quickSearchForm:submitQuickSearch")).click();
    }
    
    /**
     * Clique sur un lien nor d'une corbeille pour ouvrir les détails d'une communication
     * @param <T>
     * @param numeroNor le numero nor link à cliquer
     * @param pageClazz la page de détails qu'on souhaite obtenir
     * @return la page de détail de la communication
     */
    public <T extends AbstractDetailComm> T navigateToDetailCommByLinkNor(String numeroNor, final Class<T> pageClazz) {
        getFlog().actionClickLink("Ouverture communication : dossier nor " + numeroNor);
        By norLink = By.linkText(numeroNor);
        getDriver().findElement(norLink).click();
        
        return getPage(pageClazz);
    }

    /**
     * Vérifier que l'identité de l'utilisateur est bien présent sur la page d'accueil
     * @param aDMIN_FONCTIONNEL_LOGIN
     */
	public void verifierIdentite(String login) {
		getFlog().startCheck("Vérifier l'identité de : " + login);
		String xpathExpr = String.format("//div[@class='userMetaActions' and contains(., 'Votre identité : %s')]", login);
		waitForPageSourcePart(By.xpath(xpathExpr), TIMEOUT_IN_SECONDS);
        getDriver().findElement(By.xpath(xpathExpr)).click();
		getFlog().endCheck();
	}

	
	/**
	 * Ouvre la page administration
	 * @return
	 */
	public AdministrationPage openAdministration() {
		getFlog().startAction("Clique sur l'onglet Administration");
		WebElement webElement = getDriver().findElement(By.linkText("Administration"));
		webElement.click();
		getFlog().startAction("Fin d'ouverture de l'onglet Administration");
		return getPage(AdministrationPage.class);
	}

	public <T extends AbstractDetailComm> T navigateToDetailCommByPosition(int position, Class<T> pageClazz) {
		By idObjetDossierColonne = By.id("recherche_message_list:nxl_corbeille_message_list_listing_layout:objet_dossier_column_header_sort");
		waitForPageSourcePart(idObjetDossierColonne, TIMEOUT_IN_SECONDS);
		
		getFlog().startAction("Ouverture de la communication en position : " + position);
		getFlog().actionClickLink("Ouverture de la communication en position : " + position);
        By norLink = By.xpath("//table[@class='dataOutput']/tbody/tr["+position+"]/td[5]/div/a");
        getDriver().findElement(norLink).click();
        getFlog().endAction();
        
        return getPage(pageClazz);		
	}
	  
	public <T extends AbstractDetailComm> T openRecordFromList(final String dossierTitre, final Class<T> pageClazz) {
		getFlog().actionClickLink("essaie d'ouvrir le dossier " + dossierTitre + " de la liste");
		WebElement dossierLink = null;

		try {
			dossierLink = getDriver().findElement(By.xpath("//a/span[contains(text(),'" + dossierTitre + "')]"));
		} catch (NoSuchElementException nsee) {// org.openqa.selenium.
			try {
				dossierLink = getDriver().findElement(By.linkText(dossierTitre));
			} catch (NoSuchElementException nsee2) {// org.openqa.selenium.
				try {
					dossierLink = getDriver().findElement(By.partialLinkText(dossierTitre));
				} catch (NoSuchElementException nsee3) {// org.openqa.selenium.
					getFlog().checkFailed("le record avec le lien " + dossierTitre + " ne peut pas etre trouvé");
					dossierLink = null;
				}
			}
		}

		dossierLink.click();
		waitForPageLoaded(getDriver());
		return getPage(pageClazz);
	}

}
