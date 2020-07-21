package fr.dila.st.webdriver.ws;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.TimeoutException;

import fr.dila.st.webdriver.model.CommonWebPage;
import fr.sword.naiad.commons.webtest.WebPage;

public class LoginWsPage extends CommonWebPage {

	@FindBy(how = How.ID, id = "username")
	private WebElement	usernameElt;

	@FindBy(how = How.ID, id = "password")
	private WebElement	passwordElt;

	@FindBy(how = How.ID, id = "url")
	private WebElement	urlElt;

	@FindBy(how = How.ID, id = "request")
	private WebElement	requestElt;

	@FindBy(how = How.ID, id = "submit")
	private WebElement	okButtonElt;

	/**
	 * Soumet le formulaire de login
	 */
	public <T extends WebPage> T submitLogin(final String login, final String password, final String url,
			final String request, final Class<T> pageClazz) {
		getFlog().startAction(String.format("Connexion Web Service en tant que [%s]", login));
		LoginWsPage.sleep(2);
		waitForPageSourcePart(By.id("username"), TIMEOUT_IN_SECONDS);
		setUsername(login);
		setPassword(password);

		// Parfois, l'un des champs n'est pas renseigné, alors on attend que les infos soient bien présentes sur la page
		try {
			waitForFieldValue("username", login);
			waitForFieldValue("password", password);
		} catch (TimeoutException e) {
			// Il est déjà arrivé qu'un des champs ne soit pas ajouté dans la page (étrange, mais oui !)
			// dans ce cas, on tente de remettre les champs et on retente
			getFlog().action(
					"#SAUVETAGE#: le mot de passe (" + password
							+ ") n'est pas valorisé sur la page, alors on recommence");
			setUsername(login);
			setPassword(password);
			waitForFieldValue("password", password);
		}

		setUrl(url);
		setRequest(request);
		submit();

		getFlog().endAction();
		return getPage(pageClazz);
	}

	private void submit() {
		getFlog().actionClickButton("Connexion");
		okButtonElt.click();
	}

	public void setUsername(final String username) {
		fillField("Identifiant", usernameElt, username);
	}

	public void setPassword(final String password) {
		fillField("Mot de passe", passwordElt, password);
	}

	public void setUrl(final String url) {
		fillField("Url", urlElt, url);
	}

	public void setRequest(final String request) {
		fillField("Xml", requestElt, request);
	}

}
