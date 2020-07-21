package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.nuxeo.ecm.core.api.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm02Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.dila.solonepp.utils.VerificationUtils;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.PieceJointeType;
import fr.sword.xsd.solon.epp.TypeLoi;

public class CreateComm02Page extends CreateComm03Page {

    public static final String TYPE_COMM = "LEX-02 : PPL - Information du Gouvernement du dépôt";
    public static final String TYPE_LOI_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_typeLoi";
    public static final String NATURE_LOI = "nxw_metadonnees_version_natureLoi_row";
    public static final String AUTEUR_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_auteur_suggest";
    public static final String AUTEUR_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_auteur_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String AUTEUR_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_auteur_delete";
    public static final String COAUTEUR_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_coauteur_suggest";
    public static final String COAUTEUR_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_coauteur_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COAUTEUR_RESET = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_coauteur_list:0:nxw_metadonnees_version_coauteur_delete";
    public static final String INTITULE = "nxw_metadonnees_version_intitule_row";
    public static final String ID_DOSSIER = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_dossier";
    public static final String ID_SENAT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_senat";
    public static final String URL_DOSSIER_SENAT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_urlDossierSenat_url";
    public static final String URL_DOSSIER_AN = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_urlDossierAn_url";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    public void setTypeLoi(final TypeLoi loi) {
        final WebElement elem = getDriver().findElement(By.id(TYPE_LOI_SELECT));
        getFlog().action("Selectionne \"" + loi + "\" dans le select \"Type loi\"");
        final Select select = new Select(elem);
        select.selectByValue(loi.name());
    }

    public void addAuteur(final String auteur) {
        final WebElement elem = getDriver().findElement(By.id(AUTEUR_INPUT));
        fillField("Auteur", elem, auteur);
        waitForPageSourcePart(By.xpath(AUTEUR_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(AUTEUR_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(AUTEUR_DELETE), TIMEOUT_IN_SECONDS);
    }

    public void addCoAuteur(final String coAuteur) {
        final WebElement elem = getDriver().findElement(By.id(COAUTEUR_INPUT));
        fillField("Auteur", elem, coAuteur);
        waitForPageSourcePart(By.xpath(COAUTEUR_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(COAUTEUR_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(COAUTEUR_RESET), TIMEOUT_IN_SECONDS);
    }
    
    private void setIdSenat(String idSenat) {
		final WebElement elem = getDriver().findElement(By.id(ID_SENAT));
		fillField("Id Sénat", elem, idSenat);
	}

	private void setIdDossier(String idDossier) {
		final WebElement elem = getDriver().findElement(By.id(ID_DOSSIER));
		fillField("Id Dossier", elem, idDossier);
	}
	
	private void setUrlDossierSenat(String urlDossier) {
		final WebElement elem = getDriver().findElement(By.id(URL_DOSSIER_SENAT));
		fillField("URL Dossier (Sénat)", elem, urlDossier);
	}
	
	private void setUrlDossierAN(String urlDossier) {
		final WebElement elem = getDriver().findElement(By.id(URL_DOSSIER_AN));
		fillField("URL Dossier (AN)", elem, urlDossier);
	}
	
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm02Page.class;
    }

    public DetailComm02Page createComm02(String emetteur, String destinataire,String objet, String idDossier, String auteur, 
            String coauteur, Integer niveauLecture, NiveauLectureCode organisme, Boolean inclureMandatInactif, String copie, String idSenat, String numeroDepotTexte, String dateDepot, String commentaire, String commFond, String commAvis, boolean fromSenat) {
    	if (inclureMandatInactif){
    		tickTousMandats();
    	}
        checkValue(COMMUNICATION, TYPE_COMM);
        checkValue(IDENTIFIANT_COMMUNICATION, null);
        checkValue(IDENTIFIANT_DOSSIER, null);
        checkValue(EMETTEUR, emetteur);
        checkValue("nxw_metadonnees_evenement_destinataire", destinataire);
        checkValue(COPIE, copie);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        setNiveauLecture(niveauLecture, organisme);

        checkValue(INTITULE, null);

        setObjet(objet);
        setIdDossier(idDossier);
        if (fromSenat){
        	setIdSenat(idSenat);
        }
        if (fromSenat){
        	setUrlDossierSenat("dossier@url.com");
        } else{
        	setUrlDossierAN("dossier@url.com");
        }
        checkValue(NATURE_LOI, "Proposition");
        setTypeLoi(TypeLoi.LOI);

        setNumeroDepotTexte(numeroDepotTexte);
        checkValue(NUM_DEPOT_TXT_INPUT, numeroDepotTexte);

        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);

        setDateDepotTexte(dateDepot);
        checkValue(DATE_DEPOT_INPUT, dateDepot);

        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);
        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addAuteur(auteur);

        return publier();
    }

	/**
     * Vérifier la présence de certains champs sur la page
     */
	public void verifierPresenceChamps(Boolean fromSenat) throws ClientException{
		checkChamp("Identifiant communication");
		checkChamp("Identifiant dossier");
		checkChamp("Emetteur");
		checkChamp("Copie");
		checkChamp("Horodatage");
		if (fromSenat){
			checkChamp("Identifiant Sénat");
		}
		checkChamp("Niveau de lecture");
		checkChamp("Date accusé de réception");
		checkChamp("Type loi");
		checkChamp("Nature loi");
		checkChamp("Auteur");
		checkChamp("Coauteur(s)");
		checkChamp("URL Dossier");
		checkChamp("Cosignataire(s) collectifs(s)");
		checkChamp("Date de dépôt du texte");
		checkChamp("N° de dépôt du texte");
		checkChamp("Commission saisie au fond");
		checkChamp("Commission saisie pour avis");
		checkChamp("Commentaire");
		checkChamp("Texte");
		checkChamp("Autre");
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
	
    public DetailComm02Page createCommEVT02(String idDossier,String objet, TypeLoi typeLoi, String dateDepot, String numeroDepotTexte, String auteur) {
        checkValue(COMMUNICATION, TYPE_COMM);
        setIdentifiantDossier(idDossier);
        setObjet(objet);
        setTypeLoi(typeLoi);
        setDateDepotTexte(dateDepot);
        setNumeroDepotTexte(numeroDepotTexte);
        addAuteur(auteur);
        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);
      
        return publier();
        
    }

}
