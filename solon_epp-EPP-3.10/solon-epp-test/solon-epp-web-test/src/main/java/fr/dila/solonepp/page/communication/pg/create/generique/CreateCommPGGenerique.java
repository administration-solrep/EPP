package fr.dila.solonepp.page.communication.pg.create.generique;

import java.util.List;

import junit.framework.Assert;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.detail.generique.DetailCommPGGenerique;
import fr.dila.solonepp.utils.VerificationUtils;

/**
 * 
 * @author abianchi
 * @description classe de description de la creation de la page PG générique :
 *              déclaration de politique générale
 */
public class CreateCommPGGenerique extends AbstractCreateComm {

	public static final String IDENTIFIANT_DOSSIER = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_dossier";
	public static final String TYPE_LOI_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_typeLoi";
	public static final String DATE_PRESENTATION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_datePresentationInputDate";
	public static final String DATE_LETTRE_PM_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateLettrePmInputDate";
	private static final String DATE_ACCUSE_RECEPT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateAr";

	private static final String URL = "http://www.url.com";
	private static final String PJ = "/attachments/piece-jointe.doc";

	public static final String TYPE_COMM = "Generique - Déclaration de politique générale";

	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return DetailCommPGGenerique.class;
	}

	public DetailCommPGGenerique createCommPG_Generique(String objet, String destinataire) {

		getFlog().startAction("Création de la communication PG générique 01");
		setDestinataire(destinataire);
		setObjet(objet);
		getFlog().endAction();

		return publier();
	}

	public void setIdentifiantDossier(String idDossier) {
		final WebElement elem = getDriver().findElement(By.id(IDENTIFIANT_DOSSIER));
		fillField("Identifiant dossier", elem, idDossier);
	}

	private void setLettrePM(String dateLettrePM) {
		final WebElement elem = getDriver().findElement(By.id(DATE_LETTRE_PM_INPUT));
		fillField("Date de la lettre du premier ministre ", elem, dateLettrePM);
	}

	private void setDatePresentation(String datePresentation) {
		final WebElement elem = getDriver().findElement(By.id(DATE_PRESENTATION_INPUT));
		fillField("Date de la présentation", elem, datePresentation);
	}

	public void verifierPresenceChamps(Boolean fromSenat) throws ClientException {
		checkChamp("Communication");
		checkChamp("Identifiant communication");
		checkChamp("Identifiant dossier");
		checkChamp("Emetteur");
		checkChamp("Destinataire");
		checkChamp("Copie");
		checkChamp("Horodatage");
		checkChamp("Date accusé de réception");
		checkChamp("Objet");
		checkChamp("Commentaire");
		checkChamp("Autre(s)");
		// Vérification des autres éléments
		checkBoutons();
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
