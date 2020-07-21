package fr.dila.solonepp.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.sword.naiad.commons.webtest.WebPage;

public class ChangePasswordPage extends WebPage{

	@FindBy(how = How.ID, id = "resetPasswordUser:firstPassword")
	private WebElement firstPassword;
	
	@FindBy(how = How.ID, id = "resetPasswordUser:secondPassword")
	private WebElement secondPassword;
	
	@FindBy(how = How.ID, id = "resetPasswordUser:save")
	private WebElement saveBtn;

	public  <T extends WebPage>  T changePasswordAndSubmit(String password, Class<T> pageClazz){
		fillField("firstPassword", firstPassword, password);
		fillField("secondPassword", secondPassword, password);
		saveBtn.click();
		return getPage(pageClazz);
	}

	public Boolean verifierRejetMotDePasse() {
		String nameAction = String.format("Regarde si le mot de passe est rejeté");
		getFlog().startAction(nameAction);
		
		boolean elementFound = false;
		WebElement elem = null;
		By errorMessage = By.xpath("//*[contains(.,'Le mot de passe doit comporter au moins 8 caractères')]");
		try {
			elem = getDriver().findElement(errorMessage);
		} catch (Exception e) {
		}
		if (elem != null) {
			elementFound = true;
		}
		
		errorMessage = By.xpath("//*[contains(.,'Le mot de passe doit contenir les caractères suivants : caractères majuscules, caractères minuscules, caractères numériques et caractères spéciaux')]");
		try {
			elem = getDriver().findElement(errorMessage);
		} catch (Exception e) {
		}
		if (elem != null) {
			elementFound = true;
		}
		getFlog().endAction();
		return elementFound;
	}
	
}
