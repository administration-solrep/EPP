package fr.dila.solonepp.page.communication.abstracts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.EppWebPage;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.PieceJointeType;

public abstract class AbstractCreateComm extends EppWebPage {

	private static final String LAYOUT_METADONNEE_VERSION = "evenement_metadonnees:nxl_metadonnees_version:";
	private static final Set<String> EDITABLE_TAG_SET = new HashSet<String>();

	static {
		EDITABLE_TAG_SET.add("a");
		EDITABLE_TAG_SET.add("input");
		EDITABLE_TAG_SET.add("select");
	}

	public enum Mode {
		READ, EDIT;
	}

	public static final String COMMUNICATION = "nxw_metadonnees_evenement_libelle";
	public static final String IDENTIFIANT_COMMUNICATION = "nxw_metadonnees_evenement_identifiant_row";
	public static final String IDENTIFIANT_DOSSIER = "nxw_metadonnees_evenement_identifiant_dossier_row";
	public static final String EMETTEUR = "nxw_metadonnees_evenement_emetteur_row";
	public static final String DESTINATAIRE = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_destinataire_findButton";
	public static final String COPIE = "nxw_metadonnees_evenement_copie_row";
	public static final String HORODATAGE = "nxw_metadonnees_version_horodatage_row";
	public static final String IDENTIFIANT_COMMUNICATION_PRECED = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_precedent";
    public static final String BASE_LEGALE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_baseLegale";

	public static final String NIVEAU_LECTURE_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_niveauLecture:nxw_metadonnees_version_niveauLecture_select_one_menu";
	public static final String NIVEAU_LECTURE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_niveauLecture:nxw_metadonnees_version_niveauLecture_input";
	public static final String OBJET_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_objet";
	public static final String NOR_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_nor";
	public static final String COMMENTAIRE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_description";

	public static final String ADD_PIECE_JOINTE = "evenement_metadonnees:%s_addPj";
	
	public static final String PIECE_JOINTE_URL = "//span[@id='evenement_metadonnees:%s_pieceJointePanel']/table/tbody/tr[2]/td[2]/div/input";
	public static final String ADD_PJFILE = "//span[@id='evenement_metadonnees:%s_pieceJointePanel']/table/tbody/tr[1]/td[4]/span[1]/a/img";
	public static final String PIECE_JOINTE_FILE_IMG = "//span[@id='evenement_metadonnees:%s_pieceJointePanel']/table/tbody/tr[3]/td[2]/table/tbody/tr/td/div/span/img";
	public static final String ADD_FILE_HEADER = "editFilePanelHeader";
	
	public static final String BROUILLON_BTN = "//span[@id='evenement_metadonnees:documentViewPanel']/div[2]/input";
	public static final String PUBLIER_BTN = "//span[@id='evenement_metadonnees:documentViewPanel']/div[2]/input[2]";
	
	public static final String TOUT_MANDAT_AUTEUR = LAYOUT_METADONNEE_VERSION
			+ "nxw_metadonnees_version_auteur_full_table_ref";

	public static final String TOUT_MANDAT_COAUTEUR = LAYOUT_METADONNEE_VERSION
			+ "nxw_metadonnees_version_coauteur_full_table_ref";
	public static final String IDENTIFIANT_DOSSIER_INPUT = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_dossier";

    public static final String AUTEUR_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_auteur_suggest";
    public static final String AUTEUR_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_auteur_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String AUTEUR_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_auteur_delete";
    
	
    public static final String INTITULE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_intitule";


	/**
	 * Recherche d'un element par id<br/>
	 * 
	 * @param id
	 * @param mode
	 */
	public void checkElementPresent(final String id, final Mode mode) {
		getFlog().check("Recherche de l'element : " + id + " em mode : " + mode);
		final WebElement element = getDriver().findElement(By.id(id));
		final List<WebElement> childs = new ArrayList<WebElement>();
		childs.add(element);
		getAllChild(element, childs);

		Boolean editableFound = Boolean.FALSE;
		for (final WebElement webElement : childs) {
			final String tagName = webElement.getTagName();
			if (EDITABLE_TAG_SET.contains(tagName)) {
				editableFound = Boolean.TRUE;
			}
		}

		switch (mode) {
		case READ:
			if (editableFound) {
				getFlog().checkFailed(id + "found but in edit mode");
			}
			break;
		case EDIT:
			if (!editableFound) {
				getFlog().checkFailed(id + "found but in read-only mode");
			}
			break;
		}
	}


	public void checkValueStartWith(final String id, final String value) {
		getFlog().check("Verifie la valeur de l'element : " + id + ", expected start: " + value);
		checkValue(id, value, Boolean.FALSE);
	}

	public void setNiveauLecture(final Integer niveau, final NiveauLectureCode code) {
		final WebElement niveauLectureElem = getDriver().findElement(By.id(NIVEAU_LECTURE_SELECT));
		getFlog().action("Selectionne \"" + code + "\" dans le select \"Niveau de lecture\"");
		final Select select = new Select(niveauLectureElem);
		select.selectByValue(code.name());
		if (niveau != null) {
			waitForPageSourcePart(By.id(NIVEAU_LECTURE_INPUT), TIMEOUT_IN_SECONDS);
			final WebElement niveauElem = getDriver().findElement(By.id(NIVEAU_LECTURE_INPUT));
			fillField("Niveau de lecture", niveauElem, niveau.toString());
		}
	}

	public void setDestinataire(final String destinataire) {
		this.selectInOrganigramme(destinataire, DESTINATAIRE);
	}
	
    public void setbaseLegale(final String baseLegal) {
        final WebElement elem = getDriver().findElement(By.id(BASE_LEGALE));
        fillField("Base légale", elem, baseLegal);
    }

	public void setEmetteur(final String emetteur) {
		selectInOrganigramme(emetteur, EMETTEUR);
	}

    public void setIdentifiantDossier(final String idDossier) {
        final WebElement elem = getDriver().findElement(By.id(IDENTIFIANT_DOSSIER_INPUT));
        fillField("Identifiant dossier", elem, idDossier);
    }
	
	public void setCopie(final String copie) {
		selectInOrganigramme(copie, COPIE);
	}

	public void setObjet(final String objet) {
		final WebElement elem = getDriver().findElement(By.id(OBJET_INPUT));
		fillField("Objet", elem, objet);

	}

	public void setNor(final String nor) {
		final WebElement elem = getDriver().findElement(By.id(NOR_INPUT));
		fillField("Nor", elem, nor);
	}

	public void setCommentaire(final String commentaire) {
		final WebElement elem = getDriver().findElement(By.id(COMMENTAIRE));
		fillField("Commentaire", elem, commentaire);
	}

	public void addPieceJointe(final PieceJointeType pieceJointeType, final String url, final String file) {
		getFlog().startAction("Ajout d'une pièce jointe", "Ajout de la pièce jointe " + url + " | " + file);

		final WebElement elem = getDriver().findElement(By.id(String.format(ADD_PIECE_JOINTE, pieceJointeType.name())));
		elem.click();
		waitForPageSourcePartDisplayed(By.xpath(String.format(PIECE_JOINTE_URL, pieceJointeType.name())),
				TIMEOUT_IN_SECONDS);

		final WebElement urlElem = getDriver().findElement(
				By.xpath(String.format(PIECE_JOINTE_URL, pieceJointeType.name())));
		fillField("url", urlElem, url);

		final WebElement pjfBtn = getDriver().findElement(By.xpath(String.format(ADD_PJFILE, pieceJointeType.name())));
		pjfBtn.click();

		waitForPageSourcePartDisplayed(By.id(ADD_FILE_HEADER), TIMEOUT_IN_SECONDS);

		final String fileName = addAttachment("src/main"+file);

		waitForPageSourcePartDisplayed(By.xpath(String.format(PIECE_JOINTE_FILE_IMG, pieceJointeType.name())),
				TIMEOUT_IN_SECONDS);

		getFlog().check("Vérifie de l'ajout");
		final WebElement fileElem = getDriver().findElement(
				By.xpath(String.format(PIECE_JOINTE_FILE_IMG, pieceJointeType.name())));
		if (!fileName.equals(fileElem.getAttribute("title"))) {
			getFlog().checkFailed("Erreur d'ajout de la piece jointe [" + pieceJointeType + ", " + fileName + "]");
		}

		getFlog().endAction();
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractDetailComm> T creerBrouillon() {
		final WebElement elem = getDriver().findElement(By.xpath(BROUILLON_BTN));
		getFlog().actionClickButton("Créer Brouillon");
		elem.click();

		waitForPageSourcePart("La communication a été enregistrée", TIMEOUT_IN_SECONDS);
		return (T) getPage(getCreateResultPageClass());

	}

	public <T extends AbstractDetailComm> T publier() {
		return publier("La communication a été publiée");
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractDetailComm> T publier(String messageRetour) {
		final WebElement elem = getDriver().findElement(By.xpath(PUBLIER_BTN));
		getFlog().actionClickButton("Publier");
		elem.click();

		getFlog().check("Test du message de confirmation : " + messageRetour);
		waitForPageSourcePart(messageRetour, TIMEOUT_IN_SECONDS);
		return (T) getPage(getCreateResultPageClass());
	}

	public void tickTousMandats() {
		getFlog().startAction("Sélectionne inclure les mandats inactifs");
		final WebElement box1 = getDriver().findElement(By.id(TOUT_MANDAT_AUTEUR));
		box1.click();
		final WebElement box2 = getDriver().findElement(By.id(TOUT_MANDAT_COAUTEUR));
		box2.click();
		getFlog().endAction();
	}

    public void addAuteur(final String auteur) {
        final WebElement elem = getDriver().findElement(By.id(AUTEUR_INPUT));
        fillField("Auteur", elem, auteur);
        waitForPageSourcePart(By.xpath(AUTEUR_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(AUTEUR_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(AUTEUR_DELETE), TIMEOUT_IN_SECONDS);
    }
	
	protected abstract Class<? extends AbstractDetailComm> getCreateResultPageClass();

}
