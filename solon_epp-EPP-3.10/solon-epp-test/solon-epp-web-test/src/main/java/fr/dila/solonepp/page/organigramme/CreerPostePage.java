package fr.dila.solonepp.page.organigramme;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.solonepp.page.EppWebPage;
import fr.dila.st.webdriver.helper.LocatorHelper;

public class CreerPostePage extends EppWebPage{

	private WebElement libelleElt;
	
	@FindBy(how = How.XPATH, using = "//input[@value='Enregistrer']")
	private WebElement enregisterBtnElt;	
	
	/**
	 * Mise en place du libellé
	 * @param libelle
	 */
	public void setLibelle(String libelle){
		libelleElt = LocatorHelper.findElementByLabelOnNuxeoLayout(getDriver(), "Libellé");
		libelleElt.sendKeys(libelle);
	}
	
	public void enregistrer(){
		enregisterBtnElt.click();
	}
	
}
