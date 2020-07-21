package fr.dila.solonepp.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import fr.dila.st.webdriver.model.CommonWebPage;
import fr.dila.st.webdriver.utils.WebDriverPageUtils;
import fr.sword.naiad.commons.webtest.WebPage;
import fr.sword.naiad.commons.webtest.logger.WebLogger;


public class EppWebPage extends CommonWebPage {

	public static Integer TWO = 2;
	//Redéfinition des champs statics à surcharger
	static {
		TIMEOUT_IN_SECONDS = 60;
	}
	
		
	/**
	 * Retourne une page en initialisant les éléments avec le cusotm page factory
	 * @param driver
	 * @param webLogger
	 * @param pageClazz
	 * @return
	 */
	public static <T extends WebPage> T getPage(final WebDriver driver, final WebLogger webLogger, final Class<T> pageClazz) {
		return WebDriverPageUtils.getPage(driver, webLogger, pageClazz);
	}
	
	protected void selectInOrganigramme(final String destinataire, final String id) {
		getFlog().action("Sélection " + destinataire + " dans organigramme");
		waitElementVisibilityById(id);
		final WebElement element = getDriver().findElement(By.id(id));
		final Organigramme organigramme = getPage(Organigramme.class);
		organigramme.openOrganigramme(element);
		organigramme.selectElement(destinataire);
	}
	
	/**
	 * clique sur un bouton par l'attribut value
	 * @param valueAction la valeur de l'attribut value du bouton 
	 * @param pageClazz la classe de la page à renvoyer
	 * @return
	 */
	protected <T extends WebPage> T clickOnButton(String valueAction, final Class<T> pageClazz ) {
		getFlog().startAction(valueAction);
		String xpathExpression = String.format("//input[@value = '%s']", valueAction);
		WebElement alerteBtn = getDriver().findElement(By.xpath(xpathExpression));
		alerteBtn.click();
		getFlog().endAction();
		return getPage(pageClazz);
	}
	
}
