package fr.dila.st.webdriver.helper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import fr.dila.st.webdriver.framework.STBy;

public class LocatorHelper {

	/**
	 * Retourne un élément à partir d'un libellé sur un layout Nuxeo
	 * 
	 * @param driver
	 * @param label
	 * @return
	 */
	public static WebElement findElementByLabelOnNuxeoLayout(WebDriver driver, String label) {
		return driver.findElement(STBy.labelOnNuxeoLayoutForInput(label));
	}

	/**
	 * Retourne un élément à partir d'un texte d'un tag label
	 * 
	 * @param driver
	 * @param label
	 * @return
	 */
	public static WebElement findElementByLabelText(WebDriver driver, String label) {
		return driver.findElement(STBy.labelText(label));
	}

	/**
	 * Retourne un élément à partir d'un texte d'un tag span
	 * 
	 * @param driver
	 * @param label
	 * @return
	 */
	public static WebElement findElementBySpanText(WebDriver driver, String spanText) {
		return driver.findElement(STBy.partialSpanText(spanText));
	}
}
