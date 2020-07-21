package fr.dila.st.webdriver.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.dila.st.webdriver.framework.CustomPageFactory;
import fr.dila.st.webdriver.helper.AutoCompletionHelper;
import fr.dila.st.webdriver.helper.NameShortener;
import fr.sword.naiad.commons.webtest.WebPage;
import fr.sword.naiad.commons.webtest.logger.WebLogger;

public class CommonWebPage extends WebPage {

	protected static final Set<String>	EDITABLE_TAG_SET	= new HashSet<String>();

	static {
		EDITABLE_TAG_SET.add("a");
		EDITABLE_TAG_SET.add("input");
		EDITABLE_TAG_SET.add("select");
	}

	public enum Mode {
		READ, EDIT;
	}

	public enum By_enum {
		ID, XPATH, LINK, CSS;// ce sont les plus utilisees.
	}

	public static final String	ADD_FILE_INPUT	= "editFileForm:uploadParapheurFiles:file";
	public static final String	ADD_FILE		= "editFileForm:editFileButtonImage";

	public final WebDriver		driver			= getDriver();

	public CommonWebPage() {
		TIMEOUT_IN_SECONDS = 60;
	}

	public <T extends WebPage> T navigateToAnotherTab(final String title, final Class<T> pageClazz) {
		getFlog().action("Aller vers l'onglet " + pageClazz.toString());

		final WebElement goToLink = findElement(By.linkText(title));
		getFlog().actionClickLink(title);
		goToLink.click();
		return getPage(pageClazz);
	}

	public String addAttachment(final String attachment) {
		final File file = new File(attachment);
		getFlog().startAction("Ajout de la pièce jointe : " + file.getName());
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.presenceOfElementLocated(By
				.id(ADD_FILE_INPUT)));

		WebElement element = findElement(By.id(ADD_FILE_INPUT));
		getFlog().action("Entre " + file.getAbsolutePath() + " comme chemin");

		// Il faut d'abord cliquer sur le bouton pour que le champ 'element'
		// soit visible
		WebElement elementToClick = findElement(By.id("editFileForm:uploadParapheurFiles:add2"));
		elementToClick.click();

		element.sendKeys(file.getAbsolutePath());

		waitForPageSourcePart("Téléchargement effectué", TIMEOUT_IN_SECONDS);

		WebElement addFile = null;

		final List<WebElement> elements = getElementsBy(By.id(ADD_FILE));
		for (WebElement webElement : elements) {
			if (webElement.isDisplayed()) {
				addFile = webElement;
				break;
			}
		}

		if (addFile != null) {
			addFile.click();
		} else {
			Assert.fail("On n'a pas trouvé l'image d'ajout de fichier");
		}

		getFlog().endAction();

		return file.getName();
	}

	public void assertTrue(final String text) {
		getFlog().check("assertTrue " + text);
		Assert.assertTrue(hasElement(By.xpath("//*[contains(text(), \"" + text + "\")]")));
	}

	public void assertFalse(final String text) {
		getFlog().check("assertFalse " + text);
		Assert.assertFalse(hasElement(By.xpath("//*[contains(text(), \"" + text + "\")]")));
	}

	public void assertTrueExactMatch(final String text) {
		getFlog().check("assertTrueExactMatch " + text);
		Assert.assertTrue(hasElement(By.xpath("//*[text()= \"" + text + "\"]")));
	}

	public void assertFalseExactMatch(final String text) {
		getFlog().check("assertFalseExactMatch " + text);
		Assert.assertFalse(hasElement(By.xpath("//*[text()= \"" + text + "\"]")));
	}

	/**
	 * Recherche d'un element par id/xpath/link<br/>
	 * 
	 * @param valeur
	 *            de l'element a trouver
	 * @param mode
	 */
	public void checkElementPresent(final By_enum by, final String value, final Mode mode) {
		getFlog().check("Recherche de l'element : " + value + " en mode : " + mode);

		WebElement element = null;

		switch (by) {
			case XPATH:
				element = getElementByXpath(value);
				break;
			case ID:
				element = getElementById(value);
				break;
			case LINK:
				element = getElementByLinkText(value);
				break;
			case CSS:
				element = getElementByCssSelector(value);
				break;
		}

		final List<WebElement> childs = new ArrayList<WebElement>();
		childs.add(element);
		getAllChild(element, childs);

		Boolean editableFound = Boolean.FALSE;
		for (final WebElement webElement : childs) {
			final String tagName = webElement.getTagName();
			if (EDITABLE_TAG_SET.contains(tagName)) {
				editableFound = Boolean.TRUE;
			}
		}

		switch (mode) {
			case READ:
				if (editableFound) {
					getFlog().checkFailed(value + "found but in edit mode");
				}
				break;
			case EDIT:
				if (!editableFound) {
					getFlog().checkFailed(value + "found but in read-only mode");
				}
				break;
		}
	}

	public void checkElementNotPresent(final String id) {
		getFlog().check("Recherche de l'element : " + id);
		final List<WebElement> elements = getElementsBy(By.id(id));
		Assert.assertTrue("L'élément " + id + " a été trouvé", elements.isEmpty());
	}

	public void checkValueContain(final String id, final String value) {
		getFlog().check("Verifie la valeur de l'element : " + id + ", expected contains: " + value);
		checkValue(id, value, Boolean.FALSE);
	}

	public void checkValue(final String id, final String value, final Boolean fullText) {
		waitForPageSourcePartDisplayed(By.id(id), TIMEOUT_IN_SECONDS);
		WebElement element = getElementById(id);
		if (element == null) {
			getFlog().checkFailed(id + " not found");
		} else {
			checkValue(element, value, fullText);
		}
	}

	public void checkValue(final String id, final String value) {
		getFlog().check("Verifie la valeur de l'element : " + id + ", expected : " + value);
		checkValue(id, value, Boolean.TRUE);
	}

	public void checkValue(final By_enum by, final String selector, final String value, final Boolean fullText) {
		getFlog().check(
				"Verifie la valeur de l'element : " + selector + ", expected : " + value
						+ (fullText ? " (as full text)" : " (as partial text)"));
		WebElement element = null;

		switch (by) {
			case XPATH:
				element = getElementByXpath(selector);
				break;
			case ID:
				element = getElementById(selector);
				break;
			case LINK:
				element = getElementByLinkText(selector);
				break;
			case CSS:
				element = getElementByCssSelector(selector);
				break;
		}

		if (element == null) {
			getFlog().checkFailed("element \"" + value + "\" not found");
		} else {
			switch (by) {
				case ID:
					checkValue(selector, value, fullText);
					break;
				case XPATH:
				case CSS:
				case LINK:
					checkValue(element, value, fullText);
					break;
			}
		}
	}

	public void checkValue(WebElement element, final String value, final Boolean fullText) {
		boolean found = false;
		// On cherche déjà directement dans le contenu de la balise, sinon on ira vérifier dans les enfants
		if (fullText == null || fullText) {
			if (element.getText().equalsIgnoreCase(value)) {
				found = true;
			}
		} else {
			if (element.getText().contains(value)) {
				found = true;
			}
		}
		if (!found) {
			try {

				String textAreaValue = "";
				String textValue = "";
				final List<WebElement> childs = new ArrayList<WebElement>();
				childs.add(element);
				getAllChild(element, childs);
				Boolean textFound = Boolean.FALSE;
				for (final WebElement webElement : childs) {
					textValue = webElement.getText();
					if (fullText) {
						if (webElement.getText().equals(value)) {
							found = Boolean.TRUE;
						}

						textAreaValue = webElement.getAttribute("value");
						if (StringUtils.isNotBlank(textAreaValue) && textAreaValue.equals(value)) {
							found = Boolean.TRUE;
						}
					} else {
						if (webElement.getText().contains(value)) {
							found = Boolean.TRUE;
						}
						textAreaValue = webElement.getAttribute("value");
						if (StringUtils.isNotBlank(textAreaValue) && textAreaValue.contains(value)) {
							found = Boolean.TRUE;
						}
					}
					if (StringUtils.isNotBlank(webElement.getText())) {
						textFound = Boolean.TRUE;
					}

					textAreaValue = webElement.getAttribute("value");
					if (StringUtils.isNotBlank(textAreaValue)) {
						textFound = Boolean.TRUE;
					}

				}
				if (!textFound && StringUtils.isBlank(value)) {
					return;
				}

				if (!found) {
					if (textAreaValue != null) {
						getFlog().checkFailed("Valeur non trouvée, valeur trouvée est '" + textAreaValue + "'");
					} else {
						getFlog().checkFailed("Valeur non trouvée, valeur trouvée est '" + textValue + "'");
					}
					Assert.fail("L'élément " + value + " n'a pas pu être trouvé");
					return;
				}

				if (textFound && StringUtils.isBlank(value)) {
					getFlog().checkFailed("Une valeur a été trouvée");
					Assert.fail("L'élément " + value + " n'a pas pu être trouvé");
					return;
				}
			} catch (final Exception e) {
				getFlog().checkFailed(element.toString() + " not found " + e.getMessage());
				Assert.fail("L'élément " + value + " n'a pas pu être trouvé");
				return;
			}
		}
	}

	protected List<WebElement> getAllChild(final WebElement element, final List<WebElement> childs) {
		if (element == null) {
			throw new UnsupportedOperationException("Recherche de fils sur un element null...");
		}

		final List<WebElement> directChild = element.findElements(By.xpath("*"));
		if (directChild != null) {
			for (final WebElement webElement : directChild) {
				final List<WebElement> children = new ArrayList<WebElement>();
				getAllChild(webElement, children);
				childs.add(webElement);
				childs.addAll(directChild);
			}
		}
		return childs;
	}

	public void waitForPageLoaded(WebDriver driver) {

		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};

		Wait<WebDriver> wait = new WebDriverWait(driver, TIMEOUT_IN_SECONDS);
		try {
			wait.until(expectation);
		} catch (Throwable error) {
			assertFalse("Timeout waiting for Page Load Request to complete.");
		}
	}

	public void openRecordFromList(final String dossierTitre) {
		getFlog().actionClickLink("essaie d'ouvrir le dossier " + dossierTitre + " de la liste");
		WebElement dossierLink = null;

		try {
			dossierLink = getDriver()
					.findElement(
							By.id("corbeille_message_list:nxl_corbeille_message_listing_layout_1:nxw_idDossier_widget_1:msgCmd"));
			// dossierLink = getDriver().findElement(By.xpath("//a/span[contains(text(),'" + dossierTitre + "')]"));
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
		Assert.assertNotNull(dossierLink);
		dossierLink.click();
		waitForPageLoaded(getDriver());
	}

	public <T extends CommonWebPage> T navigateToPageFromButton(final String ButtonValue, final Class<T> pageClazz) {

		getFlog().actionClickButton(ButtonValue);
		getDriver().findElement(By.xpath("//*[@value='" + ButtonValue + "']")).click();

		return getPage(pageClazz);
	}

	public void assertElementValue(By_enum by, String identifier, String value) {
		WebElement webElement = null;
		switch (by) {
			case XPATH:
				webElement = getElementByXpath(identifier);
				break;
			case ID:
				webElement = getElementById(identifier);
				break;
			case LINK:
				webElement = getElementByLinkText(identifier);
				break;
			case CSS:
				webElement = getElementByCssSelector(identifier);
				break;
		}

		// D'abord on vérifie que l'élément qu'on recherche contient exactement ce text
		if (webElement != null) {
			String realValue = webElement.getText();
			Assert.assertEquals(value, realValue);
		} else {
			// Sinon si on n'a pas trouvé l'élément on le cherche partout dans la page
			findElement(By.partialLinkText(value));
		}
	}

	public WebElement getElementBy(By_enum by, String identifier) {

		switch (by) {
			case XPATH:
				return getElementByXpath(identifier);
			case ID:
				return getElementById(identifier);
			case LINK:
				return getElementByLinkText(identifier);
			case CSS:
				return getElementByCssSelector(identifier);
		}

		return null;
	}

	public String getElementValue(By_enum by, String identifier) {

		WebElement element = getElementBy(by, identifier);
		if (element != null) {
			return element.getText();
		}
		return null;
	}

	public void assertElementPresent(By_enum by, String value) {
		switch (by) {
			case XPATH:
				Assert.assertFalse(getElementsBy(By.xpath(value)).isEmpty());
				break;
			case ID:
				Assert.assertFalse(getElementsBy(By.id(value)).isEmpty());
				break;
			case LINK:
				Assert.assertFalse(getElementsBy(By.linkText(value)).isEmpty());
				break;
			case CSS:
				Assert.assertFalse(getElementsBy(By.cssSelector(value)).isEmpty());
				break;
		}
	}

	/**
	 * @param by
	 * @param value
	 *            make sure alements is not present in the dom
	 */
	public void assertElementNotPresent(By_enum by, String value) {
		switch (by) {
			case XPATH:
				Assert.assertTrue(getElementsBy(By.xpath(value)).isEmpty());
				break;
			case ID:
				Assert.assertTrue(getElementsBy(By.id(value)).isEmpty());
				break;
			case LINK:
				Assert.assertTrue(getElementsBy(By.linkText(value)).isEmpty());
				break;
			case CSS:
				Assert.assertTrue(getElementsBy(By.cssSelector(value)).isEmpty());
				break;
		}

	}

	public <T extends WebPage> T openRecordByIdFromList(final String id, final Class<T> pageClazz) {
		getFlog().check("Cliquer sur le lien " + id);
		getElementById(id).click();
		return getPage(pageClazz);
	}

	public <T extends WebPage> T openRecordByPathFromList(final String path, final Class<T> pageClazz) {
		getElementByXpath(path).click();
		return getPage(pageClazz);
	}

	public WebElement getElementByXpath(String xPath) {
		return findElement(By.xpath(xPath));
	}

	public WebElement getElementByName(String name) {
		return findElement(By.name(name));
	}

	public WebElement getElementById(String id) {
		return findElement(By.id(id));
	}

	public List<WebElement> getElementsBy(By by) {
		return getDriver().findElements(by);
	}

	public WebElement getElementByLinkText(String linkText) {
		return findElement(By.linkText(linkText));
	}

	public WebElement getElementByCssSelector(String selector) {
		return findElement(By.cssSelector(selector));
	}

	public void waitElementVisibilityById(String elementId) {
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(By
				.id(elementId)));
	}

	public void waitElementVisibilityByXpath(String elementXpath) {
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(By
				.xpath(elementXpath)));
	}

	public void waitElementVisibilityByLinkText(String elementLinkText) {
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(By
				.linkText(elementLinkText)));
	}

	public void waitElementSelectedById(String elementId) {
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.elementSelectionStateToBe(
				By.id(elementId), true));
	}

	public void waitElementNotSelectedById(String elementId) {
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.elementSelectionStateToBe(
				By.id(elementId), false));
	}

	public boolean isCheckBoxSelected(String id) {
		return getElementById(id).isSelected();
	}

	public String getInputValueById(String id) {
		WebElement elementById = getElementById(id);
		return elementById.getAttribute("value");
	}

	public String getInputValueByXpath(String xPath) {
		WebElement elementByXpath = getElementByXpath(xPath);
		return elementByXpath.getAttribute("value");
	}

	/**
	 * execute javascript using javascriptExecutor
	 * 
	 * @param script
	 *            : a javaScript to be executed
	 */
	public void executeScript(String script) {
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		jse.executeScript(script);
	}

	public void makeMouseOverAction(By_enum by, String onElement, String waitFor) {
		WebElement webElement = getElementBy(by, onElement);
		if (webElement != null) {
			Actions actions = new Actions(getDriver());
			actions.moveToElement(webElement).click().build().perform();
			if (waitFor != null) {
				this.waitForPageSourcePart(waitFor, TIMEOUT_IN_SECONDS);
			}
		}
	}

	/**
	 * Retourne une attente de 30 secondes
	 * 
	 * @return
	 */
	public WebDriverWait thirtySecondsWait() {
		return getWait(30);
	}

	public WebDriverWait getWait(Integer secCount) {
		WebDriverWait wait = new WebDriverWait(getDriver(), secCount);
		return wait;
	}

	/**
	 * Retourne une attente de 2 secondes
	 * 
	 * @return
	 */
	public WebDriverWait twoSecondsWait() {
		return getWait(2);
	}

	/**
	 * Retourne une attente de 1 seconde
	 * 
	 * @return
	 */
	public WebDriverWait oneSecondsWait() {
		return getWait(1);
	}

	/**
	 * Clique droit sur un élement
	 * 
	 * @param element
	 */
	public void rightClick(WebElement element) {
		Actions builderq = new Actions(super.getDriver());
		Action rClick = builderq.contextClick(element).build();
		rClick.perform();
	}

	/**
	 * Bouge vers l'élement
	 * 
	 * @param element
	 */
	public void moveToward(WebElement element) {
		Actions builderq = new Actions(super.getDriver());
		Action mClick = builderq.moveToElement(element).build();
		mClick.perform();
	}

	/**
	 * Clique droit sur un élement et va à la page
	 * 
	 * @param element
	 */
	public <T extends WebPage> T rightClick(WebElement element, Class<T> pageClass) {
		rightClick(element);
		return getPage(pageClass);
	}

	/**
	 * Raccourci pour findElement
	 * 
	 * @param by
	 *            le by de paramêtre
	 * @return
	 */
	public WebElement findElement(By by) {
		return getDriver().findElement(by);
	}

	/**
	 * clique sur l'élément passée en paramètre et renvoie une page du type donné (class1).
	 **/
	public <T extends WebPage> T clickToPage(WebElement element, Class<T> class1) {
		getFlog().startAction("Clique sur un lien " + element.getText());
		WebElement body = getElementByCssSelector("body");
		element.click();
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.stalenessOf(body));
		waitForPageLoaded(getDriver());
		T page = getPage(class1);
		getFlog().endAction();
		return page;
	}

	/**
	 * clique sur l'élément passé en paramètre et attend le rafraichissement de refreshedBy pour retourner une page du type donné (class1).
	 **/
	public <T extends WebPage> T clickAndRefreshElement(By clickBy, By refreshedBy, Class<T> class1) {
		WebElement clickElement = findElement(clickBy);
		getFlog().startAction("Clique sur un lien " + clickElement.getText());
		WebElement refreshedElement = findElement(refreshedBy);
		clickElement.click();
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.stalenessOf(refreshedElement));
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(refreshedBy));
		T page = getPage(class1);
		getFlog().endAction();
		return page;
	}

	/**
	 * Ouvre le menu contextuel lié à <code>element</code>, et clique sur l'item <code>libelle</code>. Retourne
	 * la nouvelle page <code>class1<code> ouverte.
	 **/
	public <T extends WebPage> T clickContextMenuToPage(WebElement element, String libelle, Class<T> class1) {
		getFlog().startAction("Ouvre le menu contextuel du lien " + element.getText());
		element.click();
		String xpath = String.format("//div[@id='jqContextMenu']/ul/li/a[contains(text(),'%s')]", libelle);
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		WebElement contextMenuItem = findElement(By.xpath(xpath));
		T page = clickToPage(contextMenuItem, class1);
		getFlog().endAction();
		return page;
	}

	/**
	 * Ouvre le menu contextuel lié à <code>element</code>, et clique sur l'item <code>libelle</code>.
	 * ATTENTION, n'attends pas d'événement après le clic sur le menu !
	 **/
	public <T extends WebPage> T clickContextMenu(WebElement element, String libelle, Class<T> class1) {
		getFlog().startAction("Ouvre le menu contextuel du lien " + element.getText());
		element.click();
		String xpath = String.format("//div[@id='jqContextMenu']/ul/li/a[contains(text(),'%s')]", libelle);
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		WebElement contextMenuItem = findElement(By.xpath(xpath));
		contextMenuItem.click();
		T page = getPage(class1);
		getFlog().endAction();
		return page;
	}

	public static <T extends WebPage> T getPage(final WebDriver driver, final WebLogger webLogger,
			final Class<T> pageClazz) {
		final T page = CustomPageFactory.initElements(driver, pageClazz);
		page.setWebLogger(webLogger);
		page.setDriver(driver);
		page.checkIfLoaded();
		return page;
	}

	@Override
	public <T extends WebPage> T getPage(final Class<T> pageClazz) {
		return getPage(getDriver(), getFlog(), pageClazz);
	}

	public void setAutocompleteValue(NameShortener shortener, By valeurBy) {
		setAutocompleteValue(shortener, valeurBy, 0, false);
	}

	public void setAutocompleteValue(NameShortener shortener, By valeurBy, int index, Boolean nospan) {
		AutoCompletionHelper.setBoxValue(getFlog(), getDriver(), valeurBy, shortener, index, nospan);
	}

	/**
	 * Retourne vrai si le tableau de résultat contie
	 * 
	 * @param numeroQuestionComplet
	 * @return
	 */
	public Boolean containsQuestion(String numeroQuestionComplet) {
		By tableResultsBy = new ByChained(By.className("dataOutput"), By.partialLinkText(numeroQuestionComplet));
		Boolean found = false;
		try {
			findElement(tableResultsBy);
			found = true;
		} catch (NoSuchElementException e) {
			found = false;
		}
		return found;
	}

	/**
	 * Cliquer sur une image
	 * 
	 * @param imgTitle
	 */
	public void clickOnImg(String imgTitle) {
		By imgBy = getImgBy(imgTitle);
		waitForPageSourcePartDisplayed(imgBy, 15);
		WebElement imgElt = findElement(imgBy);
		imgElt.click();
	}

	public By getImgBy(String imgTitle) {
		String imgExpr = String.format("//img[@title = \"%s\"]", imgTitle);
		By imgBy = By.xpath(imgExpr);
		return imgBy;
	}

	public boolean containsEltLocatedBy(By by) {
		return !getElementsBy(by).isEmpty();
	}

	public void clickBtn(WebElement btn) {
		getFlog().startAction("Clique sur le bouton " + btn.getAttribute("value"));
		btn.click();
		getFlog().endAction();
	}

	public void clickLien(WebElement lien) {
		getFlog().startAction("Clique sur le lien " + lien.getText());
		lien.click();
		getFlog().endAction();
	}

	protected void fillElement(String id, String title, String value) {
		WebElement el = getElementById(id);
		fillField(title, el, value);
	}

	/**
	 * Vérifie plusieurs contenus possible sur la page courante selon les classes CSS fournies, et renvoie l'index (+1)
	 * du contenu trouvé. 0 veut dire qu'on n'a rien trouvé
	 **/
	public int waitForPageSourceByClasses(int timeout, final String... cssClasses) {
		int result = new WebDriverWait(getDriver(), timeout).until(new ExpectedCondition<Integer>() {
			@Override
			public Integer apply(final WebDriver wdriver) {
				for (int n = 0; n < cssClasses.length; n++) {
					WebElement elem = wdriver.findElement(By.className(cssClasses[n]));
					if (elem != null) {
						return n + 1;
					}
				}
				return 0;
			}
		});
		return result;
	}

	/**
	 * Idem que findElements, mais filtre sur les éléments visibles. Car Selenium refuse de cliquer sur les éléments
	 * invisibles.
	 **/
	public List<WebElement> findDisplayedElements(By by) {
		List<WebElement> list = getElementsBy(by);
		for (Iterator<WebElement> it = list.iterator(); it.hasNext();) {
			if (!it.next().isDisplayed()) {
				it.remove();
			}
		}
		return list;
	}

	/** Attend qu'un champ reperé par son ID contienne une valeur donnée **/
	public void waitForFieldValue(final String id, final String value) {
		new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				try {
					WebElement element = d.findElement(By.id(id));
					return element != null && value.equals(element.getAttribute("value"));
				} catch (StaleElementReferenceException e) {
					return false;
				}
			}
		});
	}

	// Cette méthode est déjà définie dans naiad, mais elle n'utilise pas la
	// méthode 'isDisplayed' d'un WebElement.
	// En effet, Selenium considère qu'un élément est affiché sur plusieurs
	// critères: 'display != none, visibility != hidden et taille > (0,0)'
	@Override
	public void waitForPageSourcePartHide(final By byelt, final int timeoutInSec) {
		new WebDriverWait(getDriver(), timeoutInSec).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(final WebDriver wdriver) {
				try {
					final List<WebElement> elt = wdriver.findElements(byelt);
					Boolean hidden = Boolean.TRUE;
					for (final WebElement webElement : elt) {
						hidden &= !webElement.isDisplayed();
					}
					return hidden;
				} catch (final StaleElementReferenceException e) {
					return true;
				}

			}
		});
	}

	/**
	 * Workaround focus problem ; focus can be stolen during text input ; we check and retry multiple times
	 * before failing.
	 */
	@Override
	protected void fillField(String fieldTitle, final WebElement fieldElt, final String value) {
		boolean isFile = false;
		if (fieldElt.getAttribute("type").equals("file")) {
			isFile = true;
		}
		for (int i = 0; i < 10; i++) {
			super.fillField(fieldTitle, fieldElt, value);
			if (!isFile) {
				// pour les champs textuels, on vérifie la valeur sur le champ
				try {
					new WebDriverWait(getDriver(), 1).until(new ExpectedCondition<String>() {
						@Override
						public String apply(WebDriver driver) {
							return fieldElt.getAttribute("value").equals(value) ? value : null;
						}
					});
					break;
				} catch (Exception e) {
					getFlog().action(String.format("Echec lors de la saisie de %s dans %s (%d tentative); nouvel essai",
							value, fieldElt.toString(), i + 1));
				}
			} else {
				// pour un champ d'upload, le champ est supprimé à la fin de la saisie
				// pas de retry
				new WebDriverWait(getDriver(), 5).until(ExpectedConditions.stalenessOf(fieldElt));
				break;
			}
		}
	}

}