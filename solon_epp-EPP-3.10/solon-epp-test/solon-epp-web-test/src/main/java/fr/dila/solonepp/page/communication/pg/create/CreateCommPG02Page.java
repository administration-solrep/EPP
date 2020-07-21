package fr.dila.solonepp.page.communication.pg.create;

import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.detail.DetailCommPG_02;
import fr.dila.solonepp.utils.DateUtils;
import fr.dila.solonepp.utils.VerificationUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * 
 * @author abianchi
 * @description classe de description de la creation de la page PG_02 : déclaration de politique générale 
 */
public class CreateCommPG02Page extends AbstractCreateComm {

    /* fields */
	public static final String IDENTIFIANT_DOSSIER = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_dossier";
	public static final String TYPE_LOI_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_typeLoi";
    public static final String DATE_PRESENTATION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_datePresentationInputDate";
    public static final String DATE_LETTRE_PM_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateLettrePmInputDate";
	private static final String DATE_ACCUSE_RECEPT ="evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateAr";
	
    /*values*/
    public static final String TYPE_COMM = "PG-02 : Information sur la déclaration de politique générale du PM devant l’AN (49-1C)";
	public static final String EMETTEUR_VALUE = "Gouvernement";
	public static final String DESTINATAIRE_VALUE = "Sénat";
	public static final String COPIE_VALUE = "Assemblée Nationale";
	private static final String IDENTIFIANT_COMMUNICATION_PRECED_VALUE ="PG01_05122014_00000";
	
    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommPG_02.class;
    }

    public DetailCommPG_02 createCommPG02(String objet, String datePresentation, String dateLettrePM) {
    	
    	getFlog().startAction("Creation de la communication PG02");
    	
        final String horodatage = DateUtils.format(Calendar.getInstance());
    	
        checkValue(COMMUNICATION, TYPE_COMM);
        checkValue(IDENTIFIANT_COMMUNICATION, null);
        checkValue(IDENTIFIANT_COMMUNICATION_PRECED, IDENTIFIANT_COMMUNICATION_PRECED_VALUE);
        
        // setIdComm
        
        checkValue(EMETTEUR, EMETTEUR_VALUE);
        checkValue("nxw_metadonnees_evenement_destinataire", DESTINATAIRE_VALUE);
        checkValue(COPIE, COPIE_VALUE);
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(DATE_ACCUSE_RECEPT, null);

        setObjet(objet);
        setDatePresentation(datePresentation);
        setLettrePM(dateLettrePM);

        addPieceJointe(PieceJointeType.LETTRE_PM, URL, PJ);

        getFlog().endAction();
        return publier();
    }

//	public void setIdentifiantDossier(String idDossier) {
//		final WebElement elem = getDriver().findElement(By.id(IDENTIFIANT_DOSSIER));
//		fillField("Identifiant dossier", elem, idDossier);
//	}

	private void setLettrePM(String dateLettrePM) {
		final WebElement elem = getDriver().findElement(By.id(DATE_LETTRE_PM_INPUT));
		fillField("Date de la lettre du premier ministre ", elem, dateLettrePM);
	}

	private void setDatePresentation(String datePresentation) {
		final WebElement elem = getDriver().findElement(By.id(DATE_PRESENTATION_INPUT));
		fillField("Date de la présentation", elem, datePresentation);
	}


	public void verifierPresenceChamps(Boolean fromSenat) throws ClientException{
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
		checkChamp("Date de la présentation");
		checkChamp("Date de la lettre du premier ministre");
		checkChamp("Lettre Premier Ministre");
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
	 * @param champ le champ à tester
	 * @throws ClientException 
	 */
	private void checkChamp(String champ) throws ClientException {
		VerificationUtils.checkChamp(champ, getFlog(), getDriver());
	}
	
}
