package fr.dila.solonepp.page.communication.pg.create;

import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.detail.DetailCommPG_04;
import fr.dila.solonepp.utils.DateUtils;
import fr.dila.solonepp.utils.VerificationUtils;

/**
 * 
 * @author abianchi
 * @description classe de description de la creation de la page PG_04 : résultat
 *              du vote sur la déclaration de politique générale
 */
public class CreateCommPG04Page extends AbstractCreateComm {

	/* fields */
	public static final String IDENTIFIANT_DOSSIER = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_dossier";
	public static final String DATE_VOTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateVoteInputDate";
	private static final String DATE_ACCUSE_RECEPT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateAr";
	private static final String SENS_AVIS = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_sensAvis";

	/* values */
	public static final String TYPE_COMM = "PG-04 : Résultat du vote sur la déclaration de politique générale";
	public static final String EMETTEUR_VALUE = "Assemblée Nationale";
	public static final String DESTINATAIRE_VALUE = "Gouvernement";
	public static final String COPIE_VALUE = "Sénat";
	private static final String IDENTIFIANT_COMMUNICATION_PRECED_VALUE = "PG01_05122014_00000";

	private static final String URL = "http://www.url.com";
	private static final String PJ = "/attachments/piece-jointe.doc";

	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return DetailCommPG_04.class;
	}
	
    public DetailCommPG_04 createCommPG04(String sensAvis) {
        checkValue(COMMUNICATION, TYPE_COMM);
        checkValueContain(HORODATAGE, DateUtils.format(Calendar.getInstance()));
        setSensAvis(sensAvis);
        return publier();
    }

	public DetailCommPG_04 createCommPG04(String objet, String avis,
			String dateVote) {

		getFlog().startAction("Creation de la communication PG04");

		final String horodatage = DateUtils.format(Calendar.getInstance());

		checkValue(COMMUNICATION, TYPE_COMM);
		checkValue(IDENTIFIANT_COMMUNICATION, null);

		// setIdComm

		checkValue("nxw_metadonnees_evenement_destinataire", DESTINATAIRE_VALUE);
		checkValueStartWith(HORODATAGE, horodatage);
		checkValue(DATE_ACCUSE_RECEPT, null);

		setObjet(objet);
		setDateVote(dateVote);
		setSensAvis(avis);

		getFlog().endAction();
		return publier();
	}

	private void setDateVote(String dateVote) {
		final WebElement elem = getDriver().findElement(By.id(DATE_VOTE_INPUT));
		fillField("Date du vote", elem, dateVote);
	}

	private void setSensAvis(String avis) {
		selectElement(By.ById.id(SENS_AVIS), avis);
	}

	public void verifierPresenceChamps(Boolean fromSenat)
			throws ClientException {
		checkChamp("Communication");
		checkChamp("Identifiant communication");
		checkChamp("Identifiant dossier");
		checkChamp("Emetteur");
		checkChamp("Destinataire");
		checkChamp("Copie");
		checkChamp("Horodatage");
		checkChamp("Date accusé de réception");
		checkChamp("Objet");
		checkChamp("Sens avis");
		checkChamp("Nombre de suffrages exprimés");
		checkChamp("Vote(s) pour");
		checkChamp("Vote(s) contre");
		checkChamp("Abstention(s)");
		checkChamp("Commentaire");
		checkChamp("Date du vote");
		checkChamp("Autre(s)");
		// Vérification des autres éléments
		checkBoutons();
	}

	private void checkBoutons() {
		getFlog().startCheck("Vérification du nombre correct de boutons");
		String xpath = "//*[@id='evenement_metadonnees:documentViewPanel']/div[2]/input";
		List<WebElement> elements = getDriver().findElements(By.xpath(xpath));
		Assert.assertTrue(3 == elements.size());
		Assert.assertEquals("Créer brouillon",
				elements.get(0).getAttribute("value"));
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
