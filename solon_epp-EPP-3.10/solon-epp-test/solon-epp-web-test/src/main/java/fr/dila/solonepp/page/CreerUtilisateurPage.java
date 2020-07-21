package fr.dila.solonepp.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;
import fr.dila.st.webdriver.framework.STBy;
import fr.dila.st.webdriver.helper.NameShortener;

/**
 * La page de création d'un utilisateur
 * 
 * @author jgz
 * 
 */
public class CreerUtilisateurPage extends EppWebPage {

	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Identifiant")
	private WebElement	identifiant;

	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Prénom")
	private WebElement	prenom;

	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Nom")
	private WebElement	nom;

	@CustomFindBy(how = CustomHow.LABEL_TEXT, using = "Monsieur")
	private WebElement	civiliteMonsieur;

	@CustomFindBy(how = CustomHow.LABEL_TEXT, using = "Madame")
	private WebElement	civiliteMadame;

	@CustomFindBy(how = CustomHow.LABEL_TEXT, using = "Oui")
	private WebElement	utilisateurTemporaireOui;

	@CustomFindBy(how = CustomHow.LABEL_TEXT, using = "Non")
	private WebElement	utilisateurTemporaireNon;

	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Adresse")
	private WebElement	adresse;

	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Ville")
	private WebElement	ville;

	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Téléphone")
	private WebElement	telephone;

	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Mél")
	private WebElement	mel;

	@FindBy(how = How.ID, using = "createUser:button_create")
	private WebElement	enregisterBtnElt;

	@FindBy(how = How.ID, using = "createUser:nxl_userPostes:nxw_postes_findButton")
	private WebElement	linkOrga;

	public void setIdentifiant(String identifiant) {
		fillField("Identifiant", this.identifiant, identifiant);
	}

	public void setPrenom(String prenom) {
		fillField("Prénom", this.prenom, prenom);
	}

	public void setNom(String nom) {
		fillField("Nom", this.nom, nom);
	}

	public void setCiviliteMonsieur() {
		this.civiliteMonsieur.click();
	}

	public void setCiviliteMadame() {
		this.civiliteMadame.click();
	}

	public void setUtilisateurTemporaireOui() {
		this.utilisateurTemporaireOui.click();
	}

	public void setUtilisateurTemporaireNon() {
		this.utilisateurTemporaireNon.click();
	}

	public void setAdresse(String adresse) {
		this.adresse.sendKeys(adresse);
	}

	public void setVille(String ville) {
		this.ville.sendKeys(ville);
	}

	public void setTelephone(String telephone) {
		fillField("Téléphone", this.telephone, telephone);
	}

	public void setMel(String mel) {
		fillField("Mèl", this.mel, mel);
	}

	public void setProfil(NameShortener profilShortener) {
		By profilBy = STBy.labelOnNuxeoLayoutForInput("Profils");
		setAutocompleteValue(profilShortener, profilBy, 0, true);
	}

	private void setPoste(String poste) {
		Organigramme orga = getPage(Organigramme.class);
		orga.openOrganigramme(linkOrga);
		orga.addElement(poste);
	}

	/**
	 * Crée un utilisateur et l'enregistre
	 * 
	 * @param userId
	 * @param nom
	 * @param prenom
	 * @param monsieur
	 * @param temporaire
	 * @param adresse
	 * @param ville
	 * @param telephone
	 * @param mel
	 * @param profil
	 * @param poste
	 */
	public void creerUtilisateur(String userId, String nom, String prenom, Boolean monsieur, Boolean temporaire,
			String adresse, String ville, String telephone, String mel, String profil, String poste) {
		String nouvelUtilisateurIdentifiant = userId;
		setIdentifiant(nouvelUtilisateurIdentifiant);
		setPrenom(prenom);
		setNom(nom);
		if (monsieur) {
			setCiviliteMonsieur();
		} else {
			setCiviliteMadame();
		}
		if (temporaire) {
			setUtilisateurTemporaireOui();
		} else {
			setUtilisateurTemporaireNon();
		}
		setAdresse(adresse);
		setVille(ville);
		setTelephone(telephone);
		setMel(mel);
		NameShortener profilShortener = new NameShortener(profil);
		setProfil(profilShortener);
		setPoste(poste);
		enregistrer();
	}

	public void enregistrer() {
		enregisterBtnElt.click();
	}

}
