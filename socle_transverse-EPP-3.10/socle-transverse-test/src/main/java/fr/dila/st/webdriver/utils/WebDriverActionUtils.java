package fr.dila.st.webdriver.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

/**
 * Constructeur privé
 * 
 * @author jgomez
 * 
 */
public class WebDriverActionUtils {

	private WebDriverActionUtils() {
		super();
	}

	/**
	 * Clique droit sur un élement
	 * 
	 * @param element
	 */
	public static void rightClick(WebDriver driver, WebElement element) {
		Actions builderq = new Actions(driver);
		Action rClick = builderq.contextClick(element).build();
		rClick.perform();
	}

	/**
	 * Bouge vers l'élement
	 * 
	 * @param element
	 */
	public static void moveToward(WebDriver driver, WebElement element) {
		Actions builderq = new Actions(driver);
		Action mClick = builderq.moveToElement(element).build();
		mClick.perform();
	}

}
