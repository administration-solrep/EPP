package fr.dila.solonepp.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import fr.dila.solonepp.page.organigramme.CreerPostePage;
import fr.dila.solonepp.page.organigramme.CreerUniteStructurellePage;
import fr.dila.solonepp.page.organigramme.OrganigrammeContextMenu;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;
import fr.dila.st.webdriver.framework.STBy;
import fr.dila.st.webdriver.utils.WebDriverActionUtils;
import fr.dila.st.webdriver.utils.WebDriverWaitUtils;
import fr.sword.naiad.commons.webtest.WebPage;

/**
 * Des méthodes pour pouvoir utiliser l'organigramme
 * @author user
 *
 */
public class Organigramme extends EppWebPage {

	/**
	 * Ouvre une popup organigramme
	 * @param baseElement l'élément de base de l'organigramme : le td qui suit le label
	 */
    public void openOrganigramme(final WebElement baseElement) {
    	getFlog().startAction("Ouverture de l'organigramme");
        baseElement.click();
        waitForPageSourcePart(By.xpath("//div[text()='Organigramme']"), TIMEOUT_IN_SECONDS);
        getFlog().endAction();
    }
    
    /**
     * Ferme la popup de l'organigramme
     */
    public void closeOrganigramme() {
        final WebElement closeButton = getDriver().findElement(By.xpath("//*[@class=\"rich-modalpanel\"]/div[2]/div/div[2]/div/a"));
        closeButton.click();
        waitForPageSourcePartHide(By.xpath("//*[@class=\"rich-modalpanel\"]"), TIMEOUT_IN_SECONDS);
    }

    /**
     * Sélectionne un élément dans l'organigramme
     * @param name le nom d'un poste
     */
    public void selectElement(final String name) {
        final WebElement spanElement = getDriver().findElement(By.xpath("//span[text()='" + name + "']"));
        final WebElement td = spanElement.findElement(By.xpath(".."));
        final WebElement addButton = td.findElement(By.xpath(".//a"));
        addButton.click();
        waitForPageSourcePartHide(By.xpath("//div[text()='Organigramme']"), TIMEOUT_IN_SECONDS);
        this.sleep(1);
    }
    
    /**
     * Une méthode alternative pour sélectionner un élément de l'organigramme, plus générique 
     * @param name le nom d'un poste
     */
    public void addElement(final String name) {
        final WebElement spanElement = getDriver().findElement(By.xpath("//*[text()='" + name + "']"));
        By byAddImg = By.xpath(".//img[@class = 'add_icon']");
		final WebElement addButton = spanElement.findElement(byAddImg);
        addButton.click();
        waitForPageSourcePartHide(By.xpath("//*[@class=\"rich-modalpanel\"]"), TIMEOUT_IN_SECONDS);
    }
    
	private static final String CREER_UNITE_STRUCTURELLE = "Créer unité structurelle";
	
	private static final String CREER_POSTE = "Créer poste";
	
	/**
	 * Aller vers créer une unité structurelle
	 */
	public CreerUniteStructurellePage goToCreerUniteStructurelle() {
		return goToContextMenuItem(CREER_UNITE_STRUCTURELLE, CreerUniteStructurellePage.class);
	}
	
	/**
	 * Aller vers créer un poste
	 */
	public CreerPostePage goToCreerPoste() {
		return goToContextMenuItem(CREER_POSTE, CreerPostePage.class);
	}

	/**
	 * Aller vers un item du menu de context de l'organigramme
	 * @param <T>
	 * @param menuItemSelector le sélecteur de l'item
	 */
	private <T extends WebPage> T goToContextMenuItem(String menuItemSelector, Class<T> pageClass) {
		By contextMenuItemBy = STBy.partialSpanText(menuItemSelector);
		WebElement menuItemPage = getDriver().findElement(contextMenuItemBy);
		WebDriverWaitUtils.twoSecondsWait(getDriver()).until(ExpectedConditions.visibilityOfElementLocated(contextMenuItemBy));
		menuItemPage.click();
		return getPage(pageClass);
	}
    
	
	@CustomFindBy(how = CustomHow.LABEL_TEXT, using="Eléments actifs et inactifs")
	private WebElement elementsActifsEtInactifsBtn;
	
	@CustomFindBy(how = CustomHow.LABEL_TEXT, using="Eléments actifs seulement")
	private WebElement elementsActifsBtn;
	
	public OrganigrammeContextMenu rightClickOn(String nomElementOrganigramme) {
		By elementOrganigrammeBy = STBy.partialSpanText(nomElementOrganigramme);
		WebElement elementOrganigramme = getDriver().findElement(elementOrganigrammeBy);
		return rightClickOnOrganigramme(elementOrganigramme);
	}
	
	public void waitForElement(String nomElementOrganigramme){
		By elementOrganigrammeBy = STBy.partialSpanText(nomElementOrganigramme);
		waitForPageSourcePartDisplayed(elementOrganigrammeBy, TIMEOUT_IN_SECONDS);
	}
	
	public void unfold(String nomElementOrganigramme) {
		By elementOrganigrammeBy = STBy.nodeNameForToggleBtnOrga(nomElementOrganigramme);
		WebElement elementOrganigramme = getDriver().findElement(elementOrganigrammeBy);
		unfold(elementOrganigramme);
	}

	/**
	 * Clique droit sur un élement de l'organigramme
	 * @param element un element de l'organigramme
	 */
	public OrganigrammeContextMenu rightClickOnOrganigramme(WebElement element){
		WebDriverActionUtils.rightClick(getDriver(), element);
		return getPage(OrganigrammeContextMenu.class);
	}

	/**
	 * Déplie un élement de l'organigramme
	 * @param element
	 */
	private void unfold(WebElement element) {
		element.click();
	}
	
	/**
	 * Rafraichir l'organigramme
	 * @throws InterruptedException 
	 */
	public void refresh() throws InterruptedException {
		elementsActifsEtInactifsBtn.click();
		Thread.sleep(1000);
		elementsActifsBtn.click();
		Thread.sleep(1000);
	}
    
}
