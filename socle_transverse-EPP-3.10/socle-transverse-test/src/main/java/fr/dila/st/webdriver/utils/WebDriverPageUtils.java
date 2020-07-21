package fr.dila.st.webdriver.utils;

import org.openqa.selenium.WebDriver;

import fr.dila.st.webdriver.framework.CustomPageFactory;
import fr.sword.naiad.commons.webtest.WebPage;
import fr.sword.naiad.commons.webtest.logger.WebLogger;

/**
 * Classe utilitaire pour gérer les pages.
 * 
 * @author user
 * 
 */
public class WebDriverPageUtils {

	/**
	 * Constrcuteur privé
	 */
	private WebDriverPageUtils() {
		super();
	}

	/**
	 * Retourne une page en initialisant les éléments avec le custom page factory. Permet ensuite d'utiliser les
	 * annotations cusstom dans les pages
	 * 
	 * @param driver
	 *            le driver
	 * @param webLogger
	 *            le logger
	 * @param pageClazz
	 *            la classe de la page
	 * @return
	 */
	public static <T extends WebPage> T getPage(final WebDriver driver, final WebLogger webLogger,
			final Class<T> pageClazz) {
		final T page = CustomPageFactory.initElements(driver, pageClazz);
		page.setWebLogger(webLogger);
		page.setDriver(driver);
		page.checkIfLoaded();
		return page;
	}
}
