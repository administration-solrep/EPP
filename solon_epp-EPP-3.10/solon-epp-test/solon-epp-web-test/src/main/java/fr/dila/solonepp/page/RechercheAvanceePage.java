package fr.dila.solonepp.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class RechercheAvanceePage extends EppWebPage{

	private static final String BOUTON_VALUE_RECHERCHER = "Rechercher";
	private static final String BOUTON_VALUE_REINITIALISER = "Réinitialiser";
	private static final String RECHERCHE_TYPE_COMMUNICATION = "rechercheDocumentaireCritereForm:nxl_recherche_documentaire_critere_commun_layout:nxw_recherche_type_evenement_par_categorie_participant_list";
	private static final String RECHERCHE_ID_DOSSIER = "rechercheDocumentaireCritereForm:nxl_recherche_documentaire_critere_commun_layout:nxw_recherche_id_dossier";
	private static final String RECHERCHE_ID_COMMUNICATION = "rechercheDocumentaireCritereForm:nxl_recherche_documentaire_critere_commun_layout:nxw_recherche_id_evenement";
	private static final String RECHERCHE_OBJET_DOSSIER = "rechercheDocumentaireCritereForm:nxl_recherche_documentaire_critere_commun_layout:nxw_recherche_objet_dossier";

	/**
	 * Mise en place du NOR dans la recherche rapide
	 * @param nor le numéro de dossier à rechercher
	 */
	public void setNor(String nor) {
        By ByIdDossier = By.id(RECHERCHE_ID_DOSSIER);
        getDriver().findElement(ByIdDossier).sendKeys(nor);
        getFlog().actionFillField("Objet dossier", nor);
	}

	
	/**
	 * Sélectionne le type de communication
	 * @param typeCommunication
	 */
	public void selectTypeCommunication(String typeCommunication) {
        getFlog().startAction("Ajout du type de communication " + typeCommunication + " dans les critères de recherche");
        By byTypeCommunication = By.id(RECHERCHE_TYPE_COMMUNICATION);
        WebElement typeComm = getDriver().findElement(byTypeCommunication);
        Select typesSelect = new Select(typeComm);
        typesSelect.selectByValue(typeCommunication);
        getFlog().endAction();
	}
	
	/**
	 * @param id_comm l'identifiant de la communication à rechercher
	 */
	public void setIdComm(String id_comm) {
        By ByIdComm = By.id(RECHERCHE_ID_COMMUNICATION);
        getDriver().findElement(ByIdComm).sendKeys(id_comm);
        getFlog().actionFillField("Identifiant communication", id_comm);
	}

	/**
	 * Cliquer sur le bouton Rechercher
	 */
	public CorbeillePage rechercher() {
		return clickOnButton(BOUTON_VALUE_RECHERCHER, CorbeillePage.class);
	}
	
	/**
	 * Cliquer sur le bouton Rechercher
	 */
	public void reinitialiser() {
		clickOnButton(BOUTON_VALUE_REINITIALISER, CorbeillePage.class);
	}

	/**
	 * 
	 * @param objet : objet de la communication
	 */
	public void setObjet(String objet) {
        By ByIdDossier = By.id(RECHERCHE_OBJET_DOSSIER);
        getDriver().findElement(ByIdDossier).sendKeys(objet);
        getFlog().actionFillField("objet dossier communication", objet);	
	}

}
