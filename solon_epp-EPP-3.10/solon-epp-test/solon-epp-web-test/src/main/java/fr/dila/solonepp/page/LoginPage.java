package fr.dila.solonepp.page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.st.webdriver.model.CommonWebPage;
import fr.sword.naiad.commons.webtest.WebPage;

public class LoginPage extends CommonWebPage {
	public static final String RENSEIGNEMENT_LINKTEXT = "Pour tout renseignement sur les conditions";
	
    @FindBy(how = How.ID, id = "username")
    private WebElement usernameElt;

    @FindBy(how = How.ID, id = "password")
    private WebElement passwordElt;

    @FindBy(how = How.XPATH, using = "//input[@class='login_button']")
    private WebElement loginButtonElt;

    /**
     * Soumet le formulaire de login
     */
    public <T extends WebPage> T submitLogin(final String login, final String password, final Class<T> pageClazz) {
        getFlog().startAction(String.format("Connexion en tant que [%s]", login));
        this.sleep(2);
        setUsername(login);
        setPassword(password);
        submit();
        getFlog().endAction();
        return getPage(pageClazz);
    }

    private void submit() {
        getFlog().actionClickButton("Connexion");
        loginButtonElt.click();
    }

    public void setUsername(final String username) {
        fillField("Identifiant", usernameElt, username);
    }

    public void setPassword(final String password) {
        fillField("Mot de passe", passwordElt, password);
    }
    

	public void checkLinkRenseignement(){
		getFlog().check("Test la présence du lien ("+RENSEIGNEMENT_LINKTEXT+"...)");
		if(!hasLinkRenseignement()){
			getFlog().checkFailed("Le lien (" + RENSEIGNEMENT_LINKTEXT+"...) n'est pas présent");
		}
	}
    
    private boolean hasLinkRenseignement() {
		final List<WebElement> webElements = getElementsBy(By.partialLinkText(RENSEIGNEMENT_LINKTEXT));
		if (webElements.isEmpty()) {
			return false;
		}
		WebElement webElement = webElements.get(0);
		String url = webElement.getAttribute("href");
		return url != null && "#".equals(url);
	}
}
