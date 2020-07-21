package fr.dila.solonepp.utils;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import fr.sword.naiad.commons.webtest.logger.WebLogger;

/**
 * Classe utilitaire d'outils de vérification 
 * @author user
 *
 */
public final class VerificationUtils {

	
	/**
	 * Vérifie qu'un champ est présent en tant que texte brut sur la page
	 * @param champ le champ à tester
	 * @throws ClientException 
	 */
	public static void checkChamp(String champ, WebLogger logger, WebDriver driver) throws ClientException {
		logger.startCheck("Vérification de la présence du champ " + champ);
		List<WebElement> eltsFound = driver.findElements(By.xpath("//*[contains(text(),'" + champ + "')]"));
		if (eltsFound.isEmpty()){
			logger.actionFailed("La vérification a échoué, le champ " + champ + " n'est pas présent");
			throw new ClientException("Le champ " + champ + " n'est pas présent");
		}
		logger.endCheck();
	}

	/**
	 * Vérifie qu'un lien est présent sur la page
	 * @param string
	 */
	public static void checkLinkPresent(String champ, WebLogger logger, WebDriver driver) throws ClientException{
		logger.startCheck("Vérification de la présence du lien " + champ);
		List<WebElement> eltsFound = driver.findElements(By.xpath("//a[contains(text(),'" + champ + "')]"));
		if (eltsFound.isEmpty()){
			logger.actionFailed("La vérification a échoué, le lien " + champ + " n'est pas présent");
			throw new ClientException("Le lien " + champ + " n'est pas présent");
		}
		logger.endCheck();
	}

}
