package fr.dila.solonepp.page.communication.doc.create.fusion;

import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.doc.detail.fusion.DetailCommDOCFusion;
import fr.dila.solonepp.utils.DateUtils;
import fr.dila.solonepp.utils.VerificationUtils;

/**
 * 
 * @author abianchi
 * @description classe de description de la creation de la page Fusion de
 *              dossiers : déclaration de politique générale
 */
public class CreateCommDOCFusion extends AbstractCreateComm {

	public static final String IDENTIFIANT_DOSSIER = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_dossier";
	public static final String DATE_PRESENTATION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_datePresentationInputDate";
	public static final String DATE_LETTRE_PM_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateLettrePmInputDate";
	private static final String DATE_ACCUSE_RECEPT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateAr";
	private static final String DOSSIER_CIBLE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dossierCible";

	private static final String URL = "http://www.url.com";
	private static final String PJ = "/attachments/piece-jointe.doc";

	public static final String TYPE_COMM = null;

	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return DetailCommDOCFusion.class;
	}

	public DetailCommDOCFusion createCommDOCFusion(String objet, String destinataire) {
		getFlog().startAction("Fusion de dossiers");

		final String horodatage = DateUtils.format(Calendar.getInstance());

		checkValue(COMMUNICATION, TYPE_COMM);
		checkValue(IDENTIFIANT_COMMUNICATION, null);
		checkValueStartWith(HORODATAGE, horodatage);

		setDestinataire(destinataire);
		setObjet(objet);

		getFlog().endAction();
		return publier();
	}

	private void setDossierCible(String dossierCible) {
		final WebElement elem = getDriver().findElement(By.id(DOSSIER_CIBLE_INPUT));
		fillField("Dossier cible", elem, dossierCible);
	}

	public void setIdentifiantDossier(String idDossier) {
		final WebElement elem = getDriver().findElement(By.id(IDENTIFIANT_DOSSIER));
		fillField("Identifiant dossier", elem, idDossier);
	}

	/**
	 * Vérifier la présence de certains champs sur la page
	 */

	public void verifierPresenceChamps() throws ClientException {
		// checkChamp("Communication");
		checkChamp("Identifiant communication");
		checkChamp("Identifiant dossier");
		checkChamp("Emetteur");
		checkChamp("Destinataire");
		checkChamp("Horodatage");
		// checkChamp("Identifiant Sénat");
		// checkChamp("Date accusé de réception");
		checkChamp("Objet");
		// checkChamp("Dossier cible");
		// checkChamp("Commentaire");
		// checkChamp("Autre(s)");
	}

	private void checkBoutons() {
		getFlog().startCheck("Vérification du nombre correct de boutons");
		String xpath = "//*[@id='evenement_metadonnees:documentViewPanel']/div[2]/input";
		List<WebElement> elements = getDriver().findElements(By.xpath(xpath));
		Assert.assertTrue(3 == elements.size());
		Assert.assertEquals("Créer brouillon", elements.get(0).getAttribute("value"));
		Assert.assertEquals("Publier", elements.get(1).getAttribute("value"));
		Assert.assertEquals("Annuler", elements.get(2).getAttribute("value"));
		getFlog().endCheck();
	}

	/**
	 * Vérifie qu'un champ est présent en tant que texte brut sur la page
	 * 
	 * @param champ
	 *            le champ à tester
	 * @throws ClientException
	 */
	private void checkChamp(String champ) throws ClientException {
		VerificationUtils.checkChamp(champ, getFlog(), getDriver());
	}

}
