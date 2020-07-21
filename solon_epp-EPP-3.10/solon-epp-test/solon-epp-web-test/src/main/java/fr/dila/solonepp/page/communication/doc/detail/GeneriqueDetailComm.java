package fr.dila.solonepp.page.communication.doc.detail;

import java.util.Calendar;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.doc.create.generique.CreateDOCGenerique;
import fr.dila.solonepp.page.LoginPage;
import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.utils.EppUrlHelper;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.webdriver.ws.LoginWsPage;
import fr.dila.st.webdriver.ws.WsResultPage;
import fr.sword.naiad.commons.webtest.WebPage;

public class GeneriqueDetailComm extends AbstractDetailComm {
	private static final String	ID_INTITULE			= "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_intitule";
	private static final String	ID_NOR				= "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_nor";
	private static final String	ID_OBJET			= "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_objet";
	private static final String	ID_HORODATAGE		= "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_horodatage";
	private static final String	XPATH_COPIE			= "//span[@id='evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_copie_global_region']/table/tbody/tr/td";
	private static final String	ID_DESTINATAIRE		= "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_destinataire_global_region";
	private static final String	ID_EMETTEUR			= "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_emetteur_global_region";
	private static final String	IMG_ORGANIGRAMME	= "<img src=\"/solon-epp/img/icons/base_organigramme.png\" alt=\"base\"/>";
	private static final String	ID_DOSSIER			= "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_dossier";
	private static final String	ID_TYPE_COM			= "nxw_metadonnees_evenement_libelle";
	private static final String	WS_GOUVERNEMENT		= "ws-gouvernement";
	private static final String	WS_EPG				= "/technique/xml_post.html";
	private String				idComm				= null;
	private String				idDossier			= null;
	private String				emetteur			= null;
	private String				destinataire		= null;
	private String				copie				= null;
	private Calendar			horodatage			= null;
	private String				objet				= null;
	private String				nor					= null;
	private String				intitule			= null;

	public String getIdComm () {
		getFlog().startAction("Récupération du champ identifiant de l'évènement");
		By identifiant = By.id("evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant");
		waitForPageSourcePart(identifiant, TIMEOUT_IN_SECONDS);
		WebElement identifiantComm = getDriver().findElement(identifiant);
		getFlog().endAction();

		return identifiantComm.getText();
	}

	public void checkBordereauDataWithWS () {
		getFlog().startAction("Vérification des données affichées avec celles présentes dans le WebService");

		String request = buildWsRequestToGetComm(idComm);

		// Ensuite on les compart avec celles retournées par le WebService
		// chercherDossier
		WsResultPage wsResultPage = loginWSEpg(WS_GOUVERNEMENT, WS_GOUVERNEMENT,
				((EppUrlHelper) EppUrlHelper.getInstance()).getEppUrl()
						+ "/site/solonepp/WSevenement/chercherEvenement", request);
		wsResultPage.doSubmit();

		wsResultPage.assertTextResult("<ns2:id_evenement>" + idComm.replace("\n", " ") + "</ns2:id_evenement>");
		wsResultPage.assertTextResult("<ns2:id_dossier>" + idDossier.replace("\n", " ") + "</ns2:id_dossier>");
		wsResultPage.assertTextResult("<ns2:emetteur>" + emetteur.replace("\n", " ") + "</ns2:emetteur>");
		wsResultPage.assertTextResult("<ns2:destinataire>" + destinataire.replace("\n", " ") + "</ns2:destinataire>");
		wsResultPage.assertTextResult("<ns2:copie>" + copie.replace("\n", " ") + "</ns2:copie>");
		wsResultPage.assertTextResult("<ns2:objet>" + objet.replace("\n", " ") + "</ns2:objet>");
		wsResultPage.assertTextResult("<ns2:nor>" + nor + "</ns2:nor>");
		wsResultPage.assertTextResult("<ns2:intitule>" + intitule.replace("\n", " ") + "</ns2:intitule>");

		logoutWS();
		getFlog().endAction();
	}

	public String getIntitule () {
		getFlog().startAction("Récupération du champ intitulé");
		By intitule = By.id(ID_INTITULE);
		waitForPageSourcePart(intitule, TIMEOUT_IN_SECONDS);
		WebElement cpIntitule = getDriver().findElement(intitule);
		getFlog().endAction();

		return cpIntitule.getText();
	}

	public String getNor () {
		getFlog().startAction("Récupération du champ nor");
		By nor = By.id(ID_NOR);
		waitForPageSourcePart(nor, TIMEOUT_IN_SECONDS);
		WebElement cpNor = getDriver().findElement(nor);
		getFlog().endAction();

		return cpNor.getText();
	}

	public String getObjet () {
		getFlog().startAction("Récupération du champ objet");
		By objet = By.id(ID_OBJET);
		waitForPageSourcePart(objet, TIMEOUT_IN_SECONDS);
		WebElement cpObjet = getDriver().findElement(objet);
		getFlog().endAction();

		return cpObjet.getText();
	}

	public Calendar getHorodatage () {
		getFlog().startAction("Récupération du champ horodatage");
		By horodatage = By.id(ID_HORODATAGE);
		waitForPageSourcePart(horodatage, TIMEOUT_IN_SECONDS);
		WebElement cpHorodatage = getDriver().findElement(horodatage);

		String txtHorodatage = cpHorodatage.getText();
		Calendar dtHorodatage = null;
		try {
			dtHorodatage = DateUtil.parseWithSec(txtHorodatage);
		} catch (Exception e) {
			getFlog().actionFailed(e.getMessage());
		}
		getFlog().endAction();

		return dtHorodatage;
	}

	public String getCopie () {
		getFlog().startAction("Récupération du champ copie");
		By copie = By.xpath(XPATH_COPIE);
		waitForPageSourcePart(copie, TIMEOUT_IN_SECONDS);
		WebElement cpCopie = getDriver().findElement(copie);
		getFlog().endAction();
		String convertedCopie = cpCopie.getText().replace(IMG_ORGANIGRAMME, "");

		return convertOrgaLabel(convertedCopie);
	}

	public String getDestinataire () {
		getFlog().startAction("Récupération du champ destinataire");
		By destinaire = By.id(ID_DESTINATAIRE);
		waitForPageSourcePart(destinaire, TIMEOUT_IN_SECONDS);
		WebElement cpDestinataire = getDriver().findElement(destinaire);
		getFlog().endAction();
		String convertedDest = cpDestinataire.getText().replace(IMG_ORGANIGRAMME, "");

		return convertOrgaLabel(convertedDest);
	}

	private String convertOrgaLabel (String label) {
		String code = "";

		if (label.toLowerCase().contains("gouvernement")) {
			code = "GOUVERNEMENT";
		} else if (label.toLowerCase().contains("assemblée nationale")) {
			code = "ASSEMBLEE_NATIONALE";
		} else if (label.toLowerCase().contains("sénat")) {
			code = "SENAT";
		}

		return code;
	}

	public String getEmetteur () {
		getFlog().startAction("Récupération du champ émetteur");
		By emetteur = By.id(ID_EMETTEUR);
		waitForPageSourcePart(emetteur, TIMEOUT_IN_SECONDS);
		WebElement cpEmetteur = getDriver().findElement(emetteur);
		getFlog().endAction();
		String convertedEmeteur = cpEmetteur.getText().replace(IMG_ORGANIGRAMME, "");

		return convertOrgaLabel(convertedEmeteur);
	}

	public String getIdDossier () {
		getFlog().startAction("Récupération du champ identifiant du dossier");
		By emetteur = By.id(ID_DOSSIER);
		waitForPageSourcePart(emetteur, TIMEOUT_IN_SECONDS);
		WebElement cpEmetteur = getDriver().findElement(emetteur);
		getFlog().endAction();

		return cpEmetteur.getText();
	}

	private String buildWsRequestToGetComm (String idComm) {
		StringBuilder xmlRequest = new StringBuilder();

		xmlRequest.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		xmlRequest.append("<wsevt:chercherEvenementRequest\n");
		xmlRequest.append("xmlns:wsevt='http://www.dila.premier-ministre.gouv.fr/solon/epp/WSevenement'\n");
		xmlRequest.append(" xmlns:commons='http://www.dila.premier-ministre.gouv.fr/solon/epp/epp-commons'\n");
		xmlRequest.append("xmlns:evt='http://www.dila.premier-ministre.gouv.fr/solon/epp/epp-evt'\n");
		xmlRequest.append(" xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n");
		xmlRequest
				.append(" xsi:schemaLocation='http://www.dila.premier-ministre.gouv.fr/solon/epp/WSevenement ../../../../../../../../xsd/solon/epp/WSevenement.xsd '\n");
		xmlRequest.append(" contenu_PJ='true'>\n");
		xmlRequest.append(" <wsevt:id_evenement>\n");
		xmlRequest.append("  <evt:id>");
		xmlRequest.append(idComm);
		xmlRequest.append("</evt:id>");
		xmlRequest.append(" </wsevt:id_evenement>\n");
		xmlRequest.append(" </wsevt:chercherEvenementRequest>");

		return xmlRequest.toString();
	}

	private WsResultPage loginWSEpg (String username, String password, String url, String xml) {
		getFlog().action("Ouverture de la page de login de WS");
		LoginWsPage loginWsPage = WebPage.goToPage(getDriver(), getFlog(), getLoginEpgWSUrl(), LoginWsPage.class);

		return loginWsPage.submitLogin(username, password, url, xml, WsResultPage.class);
	}

	private LoginPage logoutWS () {
		String logoutUrl = getLogoutEpgUrl();
		getFlog().action("Acces au logout [" + logoutUrl + "]");
		return WebPage.goToPage(getDriver(), getFlog(), logoutUrl, LoginPage.class);
	}

	public static String getLoginEpgWSUrl () {
		String appUrl = ((EppUrlHelper) EppUrlHelper.getInstance()).getEppUrl();
		String loginUrl = appUrl;
		if (!loginUrl.endsWith("/")) {
			loginUrl += "/";
		}
		loginUrl += WS_EPG;
		return loginUrl;
	}

	public static String getLogoutEpgUrl () {
		String appUrl = ((EppUrlHelper) EppUrlHelper.getInstance()).getEppUrl();
		String loginUrl = appUrl;
		if (!loginUrl.endsWith("/")) {
			loginUrl += "/";
		}
		loginUrl += "logout";
		return loginUrl;
	}

	public void verificationChargement () {
		getFlog().startCheck("Vérification que la page de détail est bien chargée");
		waitElementVisibilityById(ID_TYPE_COM);

		// D'abord on récupère les valeurs de l'évènement
		idComm = getIdComm();
		idDossier = getIdDossier();
		emetteur = getEmetteur();
		destinataire = getDestinataire();
		copie = getCopie();
		horodatage = getHorodatage();
		objet = getObjet();
		nor = getNor();
		intitule = getIntitule();

		Assert.assertNotNull(objet);
		Assert.assertNotNull(copie);
		Assert.assertNotNull(idComm);
		Assert.assertNotNull(idDossier);
		Assert.assertNotNull(emetteur);
		Assert.assertNotNull(destinataire);
		Assert.assertNotNull(horodatage);
		Assert.assertNotNull(nor);
		Assert.assertNotNull(intitule);

		getFlog().endCheck();
	}

	@Override
	protected Class<? extends AbstractCreateComm> getModifierResultPageClass () {
		return CreateDOCGenerique.class;
	}
}
