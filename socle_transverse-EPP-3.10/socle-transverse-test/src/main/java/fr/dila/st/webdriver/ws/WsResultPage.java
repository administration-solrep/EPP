package fr.dila.st.webdriver.ws;

import java.util.regex.Pattern;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

public class WsResultPage extends LoginWsPage {
	protected String	resultFromWS;

	public void doSubmit() {
		// On vide le résultat précédent avant d'envoyer la requête
		((JavascriptExecutor) getDriver()).executeScript("window.document.getElementById('result').innerHTML = '';");
		getDriver().findElement(By.xpath("//*[@value='OK']")).click();
		waitForPageLoaded(getDriver());
		resultFromWS = this.getRealContent();
	}

	public void assertTextResult(String result) {
		if (resultFromWS != null) {
			// On attend que le champ 'result' soit valorisé
			waitForPageSourcePart(By.xpath("//div[@id='result'][string()!='']"), 300);

			result = result.replace("&lt;", "<");
			result = result.replace("&gt;", ">");
			result = result.replace("&quot;", "\"");

			getFlog().check("On check que la chaîne " + result + " est contenue dans " + resultFromWS);

			Assert.assertTrue("La chaîne " + result + " n'a pas été trouvée dans " + resultFromWS,
					Pattern.compile(Pattern.quote(result), Pattern.CASE_INSENSITIVE).matcher(resultFromWS).find());
		}
	}

	public String getRealContent() {
		waitForPageSourcePart(By.xpath("//div[@id='result'][string()!='']"), TIMEOUT_IN_SECONDS);
		return (String) ((JavascriptExecutor) getDriver())
				.executeScript("return window.document.getElementById('result').innerHTML;");
	}
}
