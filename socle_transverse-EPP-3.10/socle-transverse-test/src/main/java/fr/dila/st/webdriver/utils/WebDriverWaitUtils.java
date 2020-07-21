package fr.dila.st.webdriver.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Une classe utilitaire pour gérer les diverses attentes
 * 
 * @author jgomez
 * 
 */
public final class WebDriverWaitUtils {

	/**
	 * Un constructeur privé pour une classe utilitaire
	 */
	private WebDriverWaitUtils() {
		super();
	}

	/**
	 * Retourne une attente de 2 secondes
	 * 
	 * @return
	 */
	public static WebDriverWait twoSecondsWait(WebDriver driver) {
		return getWait(driver, 2);
	}

	/**
	 * Retourne une attente de secCount secondes
	 * 
	 * @param driver
	 *            le driver
	 * @param secCount
	 *            le nombre de secondes de l'attente
	 * @return
	 */
	public static WebDriverWait getWait(WebDriver driver, Integer secCount) {
		WebDriverWait wait = new WebDriverWait(driver, secCount);
		return wait;
	}

}
