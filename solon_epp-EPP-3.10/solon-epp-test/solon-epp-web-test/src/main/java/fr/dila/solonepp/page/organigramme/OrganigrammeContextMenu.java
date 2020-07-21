package fr.dila.solonepp.page.organigramme;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import fr.dila.solonepp.page.EppWebPage;
import fr.dila.st.webdriver.framework.STBy;
import fr.dila.st.webdriver.utils.WebDriverWaitUtils;
import fr.sword.naiad.commons.webtest.WebPage;

/**
 * Le context menu de l'ogranigramme - accessible après un click droit sur un élément de l'organigramme
 * 
 * @author jgz
 *
 */

public class OrganigrammeContextMenu extends EppWebPage{

	private static final String CREER_UNITE_STRUCTURELLE = "Créer unité structurelle";
	
	private static final String CREER_POSTE = "Créer poste";
	
	/**
	 * Créer une unité structurelle
	 */
	public CreerUniteStructurellePage goToCreerUniteStructurelle() {
		return goToContextMenuItem(CREER_UNITE_STRUCTURELLE, CreerUniteStructurellePage.class);
	}
	
	/**
	 * Créer un poste
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
	
}
