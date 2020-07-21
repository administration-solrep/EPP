package fr.dila.st.webdriver.helper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

import fr.dila.st.webdriver.framework.STBy;
import fr.sword.naiad.commons.webtest.logger.WebLogger;

/**
 * Une classe pour gérer facilement les boîtes d'autocomplétion (organigramme, profil, etc ...)
 * 
 * @author jgz
 * 
 */
public class AutoCompletionHelper {

	private static final int	AUTO_COMPLETION_WAIT_IN_SECS	= 18;

	/**
	 * Trouve des éléments en attendant un peu
	 * 
	 * @param driver
	 * @param locator
	 * @param timeoutSeconds
	 * @return
	 */
	private static List<WebElement> findElement(final WebDriver driver, final By locator, final int timeoutSeconds) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeoutSeconds, TimeUnit.SECONDS)
				.pollingEvery(500, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);
		return wait.until(new Function<WebDriver, List<WebElement>>() {
			public List<WebElement> apply(WebDriver webDriver) {
				return driver.findElements(locator);
			}
		});
	}

	/**
	 * Remplit une boîte de complétion automatique avec la valeur qui est suggéré par shortValue, si elle corresponds à
	 * fullValue
	 * 
	 * @param flog
	 * @param shortValue
	 * @param fullValue
	 * @throws InterruptedException
	 */
	public static void setBoxValue(WebLogger flog, WebDriver driver, By by, NameShortener shortener, Integer index,
			Boolean nospan) {
		flog.action("Remplit le champ d'autocomplétion avec " + shortener.getShortName());
		WebDriverWait wait = new WebDriverWait(driver, AUTO_COMPLETION_WAIT_IN_SECS);
		WebElement elementBox = driver.findElement(by);
		elementBox.click();
		slowSendKeys(elementBox, shortener.getShortName());
		By fullValueBy = By.xpath("//*[contains(text(), \"" + shortener.getFullName() + "\")]");
		wait.until(ExpectedConditions.elementToBeClickable(fullValueBy));
		try {
			List<WebElement> elements = findElement(driver, fullValueBy, AUTO_COMPLETION_WAIT_IN_SECS);
			if (elements.isEmpty()) {
				flog.actionFailed("Pas d'élément trouvé");
				return;
			}
			if (index > elements.size()) {
				flog.actionFailed(elements.size() + " éléments trouvés, pas possible de récupérer l'élément à l'index "
						+ index);
			}
			elements.get(index).click();
		} catch (TimeoutException ex) {
			flog.actionFailed("Le délai de " + AUTO_COMPLETION_WAIT_IN_SECS + " a été dépassé, "
					+ shortener.getFullName() + " n'est pas visible");
			throw ex;
		}
		if (nospan) {
			// L'élément ajouté n'est pas contenu dans un span
			flog.action("En attente de " + shortener.getFullName());
			By noSpanBy = By.xpath("//*[text()[contains(.,\"" + shortener.getFullName() + "\")]]");
			wait.until(ExpectedConditions.presenceOfElementLocated(noSpanBy));
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			// L'élément ajouté est contenu dans un span
			wait.until(ExpectedConditions.visibilityOfElementLocated(STBy.partialSpanText(shortener.getFullName())));
		}
	}

	// FIXME: Méthode non générique, attente non contrôlé ...
	public static void setUserValue(WebLogger flog, WebDriver driver, By by, NameShortener shortener) {
		flog.action("Remplit le champ d'autocomplétion utilisateur avec " + shortener.getShortName());
		WebDriverWait wait = new WebDriverWait(driver, AUTO_COMPLETION_WAIT_IN_SECS);
		WebElement elementBox = driver.findElement(by);
		elementBox.click();
		slowSendKeys(elementBox, shortener.getFullName());

		wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.xpath("//table[@id = \"requeteur:alertForm:nxl_alert:nxw_recipients_suggestionBox:suggest\"]")));

		By idSuggest = By.id("requeteur:alertForm:nxl_alert:nxw_recipients_suggest");
		WebElement idSuggestElt = driver.findElement(idSuggest);
		idSuggestElt.sendKeys(Keys.RETURN);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void setRubriqueValue(WebLogger flog, WebDriver driver, By by, NameShortener shortener) {
		flog.action("Remplit le champ d'autocomplétion avec " + shortener.getShortName());
		WebElement elementBox = driver.findElement(by);
		elementBox.click();
		slowSendKeys(elementBox, shortener.getFullName());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		WebElement idSuggestElt = driver.findElement(by);
		idSuggestElt.sendKeys(Keys.RETURN);
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void setBoxValue(WebLogger flog, WebDriver driver, By by, NameShortener shortener) {
		setBoxValue(flog, driver, by, shortener, 0, false);
	}

	/**
	 * Tape la valeur en plusieurs fois, augmente la fiabilité de la saisie
	 * 
	 * @param elementBox
	 *            l'élément
	 * @param valeur
	 *            la valeur à entrer
	 */
	private static void slowSendKeys(WebElement elementBox, String valeur) {
		elementBox.clear();
		int length = valeur.length();
		int middle = Math.round(valeur.length() / 2);
		elementBox.sendKeys(valeur.subSequence(0, middle));
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		elementBox.sendKeys(valeur.subSequence(middle, length));
	}

}
