package fr.dila.solonepp.webtest.webdriver030;

import java.util.Calendar;

import org.junit.Assert;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.RechercheRapidePage;
import fr.dila.solonepp.page.communication.pg.complete.CompletionPG01Page;
import fr.dila.solonepp.page.communication.pg.complete.CompletionPG02Page;
import fr.dila.solonepp.page.communication.pg.complete.CompletionPG04Page;
import fr.dila.solonepp.page.communication.pg.complete.generique.CompletionPGGenerique;
import fr.dila.solonepp.page.communication.pg.create.CreateCommPG01Page;
import fr.dila.solonepp.page.communication.pg.create.CreateCommPG02Page;
import fr.dila.solonepp.page.communication.pg.create.CreateCommPG03Page;
import fr.dila.solonepp.page.communication.pg.create.CreateCommPG04Page;
import fr.dila.solonepp.page.communication.pg.create.alerte.AlertePGPage;
import fr.dila.solonepp.page.communication.pg.create.fusion.CreateCommFusion;
import fr.dila.solonepp.page.communication.pg.create.generique.CreateCommPGGenerique;
import fr.dila.solonepp.page.communication.pg.detail.DetailCommPG_01;
import fr.dila.solonepp.page.communication.pg.detail.DetailCommPG_02;
import fr.dila.solonepp.page.communication.pg.detail.DetailCommPG_03;
import fr.dila.solonepp.page.communication.pg.detail.DetailCommPG_04;
import fr.dila.solonepp.page.communication.pg.detail.alerte.DetailCommPG_Alerte;
import fr.dila.solonepp.page.communication.pg.detail.fusion.DetailCommPGFusion;
import fr.dila.solonepp.page.communication.pg.detail.generique.DetailCommPGGenerique;
import fr.dila.solonepp.utils.DateUtils;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * - Test de création d'une procédure de déclaration de politique générale
 * 
 * @author abianchi
 * 
 */
public class TestProcedureDeclarationPG extends AbstractEppWebTest {

	private static final String TOUTES_LES_COMMUNICATIONS = "Toutes les communications";
	private static final String DECLARATIONS_DU_GOUVERNEMENT = "Déclarations du Gouvernement";
	final public static String idDossier = "PG01_05122014";
	final public static String idDossier2 = "PG03_15122014";
	final public static String idDossier3 = "PG01_17122014";
	final public static String idDossier4 = "PG03_17122014";

	final public static String gvt = "Gouvernement";
	final public static String an = "Assemblée nationale";
	final public static String senat = "Sénat";

	@WebTest(description = "Réalisation d'une procédure déclaration de politique générale")
	@TestDocumentation(categories = {"Comm. PG-01", "Compléter", "Comm. PG-02", "Comm. Alerte11", "Recherche rapide",
			"Générique11", "Comm. PG-04", "Comm. PG-03", "Comm. Fusion11"})
	public void testCreationPG_01() {
		doActions1();
		doANActions2();
		doSenatActions3();
		doGVTActions4();
		doANActions5();
		doSenatActions6();
		doANActions7();
		doGVTActions8();
		doGVTActions9();
		doANActions10();
		doGVTActions11();
		doSenatActions12();
		doGVTActions13();
		doANActions14();

	}

	private void doActions1() {
		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);

		final CreateCommPG01Page createCommPG01Page;
		DetailCommPG_01 detailPage;
		CompletionPG01Page completionPG01Page;
		CreateCommPG02Page createCommPG02Page;
		DetailCommPG_02 detailPage02;
		CompletionPG02Page completionPG02Page;
		RechercheRapidePage searchPage;
		CreateCommPGGenerique commGeneriquePage;
		DetailCommPGGenerique detailGeneCommPage;
		CompletionPGGenerique completionPG_GenePage;
		AlertePGPage alertePG;

		/** Creation communication PG01 **/
		getFlog().startAction("1 - Réalisation des actions du gouvernement");
		// Ouverture de la page de création de la communication PG_01
		createCommPG01Page = corbeillePage.navigateToCreateComm(EvenementType.PG_01, CreateCommPG01Page.class);
		getFlog().startCheck("Vérification des champs présents de PG01");
		try {
			createCommPG01Page.verifierPresenceChamps(true);
		} catch (ClientException e) {
			getFlog().checkFailed(e.getMessage());
			Assert.fail("Tous les champs ne sont pas présents");
		}
		getFlog().endCheck();

		final String horodatage = DateUtils.format(Calendar.getInstance());
		// Remplissage des champs de la page de création et publication de la
		// communication
		detailPage = createCommPG01Page.createCommPG01(idDossier, "PG01", horodatage, horodatage);
		detailPage.checkValue(DetailCommPG_01.IDENTIFIANT_DOSSIER, idDossier);
		detailPage.checkValue(DetailCommPG_01.EMETTEUR, gvt); //
		detailPage.checkValue(DetailCommPG_01.DESTINATAIRE, an);
		detailPage.checkValue(DetailCommPG_01.COPIE, senat);
		detailPage.checkValue(DetailCommPG_01.OBJET, "PG01");

		// ouverture de la page de modification de PG_01
		completionPG01Page = detailPage.completer();
		detailPage = completionPG01Page.completePG01("Un commentaire somme toute assez simple.");
		detailPage.checkValue(CompletionPG01Page.COMMENTAIRE, "Un commentaire somme toute assez simple.");

		// Création de la communication successive PG02
		createCommPG02Page = detailPage.navigateToNextComm("PG02", CreateCommPG02Page.class);
		getFlog().startCheck("Vérification des champs présents de PG02");
		try {
			createCommPG02Page.verifierPresenceChamps(true);
		} catch (ClientException e) {
			getFlog().checkFailed(e.getMessage());
			Assert.fail("Tous les champs ne sont pas présents");
		}
		getFlog().endCheck();

		detailPage02 = createCommPG02Page.createCommPG02("PG02", horodatage, horodatage);
		detailPage02.checkValue(DetailCommPG_02.EMETTEUR, gvt);
		detailPage02.checkValue(DetailCommPG_02.DESTINATAIRE, senat);
		detailPage02.checkValue(DetailCommPG_02.COPIE, an);

		completionPG02Page = detailPage02.completer();
		detailPage02 = completionPG02Page.completePG02("Le même commentaire mais pour le sénat");
		detailPage02.checkValue(DetailCommPG_02.COMMENTAIRE, "Le même commentaire mais pour le sénat");

		// on re sélectionne PG01
		corbeillePage.openCorbeille("Déclaration de politique générale");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.setIdComm(idDossier + "_00000");
		searchPage.rechercher();
		detailPage = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_01.class);

		commGeneriquePage = detailPage.navigateToNextComm("GENERIQUE11", CreateCommPGGenerique.class);
		detailGeneCommPage = commGeneriquePage.createCommPG_Generique("GEN01", an);
		detailGeneCommPage.checkValue(DetailCommPG_01.DESTINATAIRE, an);
		detailGeneCommPage.checkValue(DetailCommPG_01.COPIE, senat);
		detailGeneCommPage.checkValue(DetailCommPG_01.IDENTIFIANT_DOSSIER, idDossier);
		detailGeneCommPage.checkValue(DetailCommPG_01.OBJET, "GEN01");

		completionPG_GenePage = detailGeneCommPage.completer();
		detailGeneCommPage = completionPG_GenePage.completePG_GEN01("Un commentaire générique");
		detailGeneCommPage.checkValue(DetailCommPG_01.COMMENTAIRE, "Un commentaire générique");

		searchPage.rechercher();
		detailPage = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_01.class);

		/** création de l'alerte **/

		alertePG = detailPage.navigateToNextComm("ALERTE11", AlertePGPage.class);
		DetailCommPG_Alerte detailAlertePG = alertePG.createAlerte("ALE01", an);
		detailAlertePG.checkValue(DetailCommPG_Alerte.OBJET, "ALE01");
		detailAlertePG.checkValue(DetailCommPG_Alerte.DESTINATAIRE, an);
		
		getFlog().endAction();

		doLogout();
	}

	/**
	 * Actions effectuées par l'assemblée nationale lors de la procédure: -
	 * création communication générique de déclaration de PG - création alerte
	 */
	public void doANActions2() {
		// déclarations
		RechercheRapidePage searchPage;
		DetailCommPGGenerique detailGeneCommPage;
		DetailCommPG_01 detailPG_01;
		CreateCommPG04Page createCommPG04;
		CompletionPG04Page completePG04;
		DetailCommPG_04 detailPG04;
		DetailCommPG_Alerte detailAlerte;
		AlertePGPage createAlertePage;
		CreateCommPGGenerique detailPG_Gen;
		final String horodatage = DateUtils.format(Calendar.getInstance());

		// start
		getFlog().startAction("2 - Réalisation des actions de l'assemblée nationale");
		doLoginAs(EppTestConstantes.AN_LOGIN, EppTestConstantes.AN_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.AN_LOGIN);
		corbeillePage.openCorbeille(TOUTES_LES_COMMUNICATIONS);
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.reinitialiser();
		searchPage.setObjet("GEN01");
		searchPage.rechercher();

		// creation communication générique
		detailGeneCommPage = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPGGenerique.class);
		detailPG_Gen = detailGeneCommPage.navigateToNextComm("GENERIQUE11", CreateCommPGGenerique.class);
		detailGeneCommPage = detailPG_Gen.createCommPG_Generique("GEN02", senat);
		
		detailGeneCommPage.checkValue(DetailCommPGGenerique.OBJET, "GEN02");
		detailGeneCommPage.checkValue(DetailCommPGGenerique.DESTINATAIRE, senat);
		detailGeneCommPage.checkValue(DetailCommPGGenerique.COPIE, gvt);
		
		corbeillePage.openCorbeille(TOUTES_LES_COMMUNICATIONS);
		searchPage.reinitialiser();
		searchPage.setIdComm(idDossier + "_00003");
		searchPage.rechercher();

		// creation alerte
		detailAlerte = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_Alerte.class);
		createAlertePage = detailAlerte.navigateToNextComm("ALERTE11", AlertePGPage.class);
		detailAlerte = createAlertePage.createAlerte("ALE02", senat);
		
		detailAlerte.checkValue(DetailCommPG_Alerte.OBJET, "ALE02");
		detailAlerte.checkValue(DetailCommPG_Alerte.DESTINATAIRE, senat);
		

		// verification PG02
		corbeillePage.openCorbeille(DECLARATIONS_DU_GOUVERNEMENT);
		searchPage.reinitialiser();
		searchPage.setIdComm(idDossier + "_00001");
		searchPage.rechercher();
		DetailCommPG_02 pagePg02Searched = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_02.class);
		
		pagePg02Searched.checkValue(DetailCommPG_02.EMETTEUR, gvt);
		pagePg02Searched.checkValue(DetailCommPG_02.DESTINATAIRE, senat);
		pagePg02Searched.checkValue(DetailCommPG_02.COPIE, an);
		pagePg02Searched.checkValue(DetailCommPG_02.COMMENTAIRE, "Le même commentaire mais pour le sénat");

		// verification PG_01
		corbeillePage.openCorbeille(DECLARATIONS_DU_GOUVERNEMENT);
		searchPage.reinitialiser();
		searchPage.setIdComm(idDossier + "_00000");
		searchPage.rechercher();
		detailPG_01 = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_01.class);
		
		detailPG_01.checkValue(DetailCommPG_02.EMETTEUR, gvt);
		detailPG_01.checkValue(DetailCommPG_02.DESTINATAIRE, an);
		detailPG_01.checkValue(DetailCommPG_02.COPIE, senat);
		detailPG_01.checkValue(DetailCommPG_02.COMMENTAIRE, "Un commentaire somme toute assez simple.");

		// creation PG_04
		createCommPG04 = detailPG_01.navigateToNextComm("PG04", CreateCommPG04Page.class);
		detailPG04 = createCommPG04.createCommPG04("PG04", "Favorable", horodatage);
		completePG04 = detailPG04.completer();
		detailPG04 = completePG04.completePG04("commentaire PG04", "100", "50", "49", "1");
		
		detailPG04.checkValue(DetailCommPG_04.EMETTEUR, an);
		detailPG04.checkValue(DetailCommPG_04.DESTINATAIRE, gvt);
		detailPG04.checkValue(DetailCommPG_04.COPIE, senat);
		detailPG04.checkValue(DetailCommPG_04.OBJET, "PG04");
		detailPG04.checkValue(DetailCommPG_04.COMMENTAIRE, "commentaire PG04");

		getFlog().endAction();
		doLogout();
		return;
	}

	private void doSenatActions3() {
		// declarations
		RechercheRapidePage recherchePage;
		DetailCommPGGenerique detailCommPGGenerique;
		CreateCommPGGenerique createCommPGGenerique;
		DetailCommPG_Alerte detailAlerte;
		
		// start
		doLoginAs(EppTestConstantes.SENAT_LOGIN, EppTestConstantes.SENAT_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.SENAT_LOGIN);

		getFlog().startAction("3 - Début des actions du Sénat");

		// verification communications reçues
		corbeillePage.openCorbeille("Pour info");
		recherchePage = corbeillePage.openRechercheRapide();

		recherchePage.setObjet("GEN01");
		recherchePage.rechercher();
		detailCommPGGenerique = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPGGenerique.class);

		detailCommPGGenerique.checkValue(DetailCommPGGenerique.EMETTEUR, gvt);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.DESTINATAIRE, an);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.COPIE, senat);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.OBJET, "GEN01");
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.COMMENTAIRE, "Un commentaire générique");

		corbeillePage.openCorbeille("Destinataire");
		recherchePage.reinitialiser();
		recherchePage.setObjet("GEN02");
		recherchePage.rechercher();
		detailCommPGGenerique = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPGGenerique.class);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.EMETTEUR, an);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.DESTINATAIRE, senat);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.COPIE, gvt);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.OBJET, "GEN02");
		
		createCommPGGenerique = detailCommPGGenerique.navigateToNextComm("GENERIQUE11", CreateCommPGGenerique.class);
		detailCommPGGenerique = createCommPGGenerique.createCommPG_Generique("GEN03", gvt);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.EMETTEUR, senat);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.DESTINATAIRE, gvt);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.COPIE, an);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.OBJET, "GEN03");

		corbeillePage.openCorbeille("Pour info");
		recherchePage.reinitialiser();
		recherchePage.setObjet("ALE01");
		recherchePage.rechercher();
		detailAlerte = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_Alerte.class);
		
		detailAlerte.checkValue(DetailCommPG_Alerte.EMETTEUR, gvt);
		detailAlerte.checkValue(DetailCommPG_Alerte.DESTINATAIRE, an);
		detailAlerte.checkValue(DetailCommPG_Alerte.COPIE, senat);
		detailAlerte.checkValue(DetailCommPG_Alerte.OBJET, "ALE01");

		corbeillePage.openCorbeille("Destinataire");
		recherchePage.reinitialiser();
		recherchePage.setObjet("GEN02");
		recherchePage.rechercher();
		detailAlerte = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_Alerte.class);
		
		detailAlerte.checkValue(DetailCommPG_Alerte.EMETTEUR, an);
		detailAlerte.checkValue(DetailCommPG_Alerte.DESTINATAIRE, senat);
		detailAlerte.checkValue(DetailCommPG_Alerte.COPIE, gvt);
		detailAlerte.checkValue(DetailCommPG_Alerte.OBJET, "GEN02");
		
		
		AlertePGPage ap = detailAlerte.navigateToNextComm("ALERTE11", AlertePGPage.class);
		detailAlerte = ap.createAlerte("ALE03", gvt);
		
		detailAlerte.checkValue(DetailCommPG_Alerte.EMETTEUR, senat);
		detailAlerte.checkValue(DetailCommPG_Alerte.DESTINATAIRE, gvt);
		detailAlerte.checkValue(DetailCommPG_Alerte.COPIE, an);
		detailAlerte.checkValue(DetailCommPG_Alerte.OBJET, "ALE03");

		corbeillePage.openCorbeille("Pour info");
		recherchePage.reinitialiser();
		recherchePage.setObjet("PG01");
		recherchePage.rechercher();
		DetailCommPG_01 detailPG01 = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_01.class);
		detailPG01.checkValue(DetailCommPG_01.EMETTEUR, gvt);
		detailPG01.checkValue(DetailCommPG_01.DESTINATAIRE, an);
		detailPG01.checkValue(DetailCommPG_01.COPIE, senat);
		detailPG01.checkValue(DetailCommPG_01.OBJET, "PG01");
		detailPG01.checkValue(DetailCommPG_01.COMMENTAIRE, "Un commentaire somme toute assez simple.");

		corbeillePage.openCorbeille("Pour info");
		recherchePage.reinitialiser();
		recherchePage.setObjet("PG04");
		recherchePage.rechercher();
		DetailCommPG_04 detailPG04 = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_04.class);
		detailPG04.checkValue(DetailCommPG_04.EMETTEUR, an);
		detailPG04.checkValue(DetailCommPG_04.DESTINATAIRE, gvt);
		detailPG04.checkValue(DetailCommPG_04.COPIE, senat);
		detailPG04.checkValue(DetailCommPG_04.OBJET, "PG04");
		detailPG04.checkValue(DetailCommPG_04.COMMENTAIRE, "commentaire PG04");

		corbeillePage.openCorbeille("Destinataire");
		recherchePage.reinitialiser();
		recherchePage.setObjet("PG02");
		recherchePage.rechercher();
		DetailCommPG_02 detailPG02 = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_02.class);
		detailPG02.checkValue(DetailCommPG_02.EMETTEUR, gvt);
		detailPG02.checkValue(DetailCommPG_02.DESTINATAIRE, senat);
		detailPG02.checkValue(DetailCommPG_02.COPIE, an);
		detailPG02.checkValue(DetailCommPG_02.OBJET, "PG02");
		detailPG02.checkValue(DetailCommPG_02.COMMENTAIRE, "Le même commentaire mais pour le sénat");

		getFlog().endAction();
		doLogout();
	}

	public void doGVTActions4() {

		// declarations
		RechercheRapidePage searchPage;
		CreateCommPG03Page createPG03;
		final String horodatage = DateUtils.format(Calendar.getInstance());

		// start
		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);

		getFlog().startAction("4 - Seconde série d'actions du gouvernement");

		// Verifications des communications
		corbeillePage.openCorbeille("Déclaration de politique générale");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.setObjet("GEN02");
		searchPage.rechercher();
		DetailCommPGGenerique detailCommPGGenerique = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPGGenerique.class);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.EMETTEUR, an);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.DESTINATAIRE, senat);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.COPIE, gvt);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.OBJET, "GEN02");

		searchPage.reinitialiser();
		searchPage.setObjet("GEN03");
		searchPage.rechercher();
		
		detailCommPGGenerique = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPGGenerique.class);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.EMETTEUR, senat);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.DESTINATAIRE, gvt);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.COPIE, an);
		detailCommPGGenerique.checkValue(DetailCommPGGenerique.OBJET, "GEN03");

		searchPage.reinitialiser();
		searchPage.setObjet("ALE02");
		searchPage.rechercher();
		
		DetailCommPG_Alerte detailAlerte = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_Alerte.class);
		detailAlerte.checkValue(DetailCommPG_Alerte.EMETTEUR, an);
		detailAlerte.checkValue(DetailCommPG_Alerte.DESTINATAIRE, senat);
		detailAlerte.checkValue(DetailCommPG_Alerte.COPIE, gvt);
		detailAlerte.checkValue(DetailCommPG_Alerte.OBJET, "ALE02");

		searchPage.reinitialiser();
		searchPage.setObjet("ALE03");
		searchPage.rechercher();
		
		detailAlerte = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_Alerte.class);
		detailAlerte.checkValue(DetailCommPG_Alerte.EMETTEUR, senat);
		detailAlerte.checkValue(DetailCommPG_Alerte.DESTINATAIRE, gvt);
		detailAlerte.checkValue(DetailCommPG_Alerte.COPIE, an);
		detailAlerte.checkValue(DetailCommPG_Alerte.OBJET, "ALE03");
		
		searchPage.reinitialiser();
		searchPage.setObjet("PG04");
		searchPage.rechercher();
		DetailCommPG_04 detailPG04 = corbeillePage.navigateToDetailCommByLinkNor(idDossier, DetailCommPG_04.class);
		
		detailPG04.checkValue(DetailCommPG_04.EMETTEUR, an);
		detailPG04.checkValue(DetailCommPG_04.DESTINATAIRE, gvt);
		detailPG04.checkValue(DetailCommPG_04.COPIE, senat);
		detailPG04.checkValue(DetailCommPG_04.OBJET, "PG04");
		detailPG04.checkValue(DetailCommPG_04.COMMENTAIRE, "commentaire PG04");

		createPG03 = corbeillePage.navigateToCreateComm(EvenementType.PG_03, CreateCommPG03Page.class);
		DetailCommPG_03 detailPG03 = createPG03.createCommPG03(idDossier2, "PG03", horodatage, horodatage);
		
		detailPG03.checkValue(DetailCommPG_03.EMETTEUR, gvt);
		detailPG03.checkValue(DetailCommPG_03.DESTINATAIRE, senat);
		detailPG03.checkValue(DetailCommPG_03.COPIE, an);
		detailPG03.checkValue(DetailCommPG_03.OBJET, "PG03");

		getFlog().endAction();
		doLogout();
	}

	private void doANActions5() {
		RechercheRapidePage searchPage;

		doLoginAs(EppTestConstantes.AN_LOGIN, EppTestConstantes.AN_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.AN_LOGIN);

		getFlog().startAction("5 - vérifications assemblée nationale");

		corbeillePage.openCorbeille(DECLARATIONS_DU_GOUVERNEMENT);
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.reinitialiser();
		searchPage.setObjet("PG03");
		searchPage.rechercher();
		DetailCommPG_03 detailPG03 = corbeillePage.navigateToDetailCommByLinkNor(idDossier2, DetailCommPG_03.class);
		detailPG03.checkValue(DetailCommPG_03.EMETTEUR, gvt);
		detailPG03.checkValue(DetailCommPG_03.DESTINATAIRE, senat);
		detailPG03.checkValue(DetailCommPG_03.COPIE, an);
		detailPG03.checkValue(DetailCommPG_03.OBJET, "PG03");
		
		getFlog().endAction();
		doLogout();
	}

	private void doSenatActions6() {
		// declarations
		RechercheRapidePage searchPage;
		DetailCommPG_03 detailPG03;
		CreateCommPG04Page commPG04;
		final String horodatage = DateUtils.format(Calendar.getInstance());

		// start
		doLoginAs(EppTestConstantes.SENAT_LOGIN, EppTestConstantes.SENAT_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.SENAT_LOGIN);

		getFlog().startAction("6 - Verifications du senat + actions");

		corbeillePage.openCorbeille("Destinataire");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.reinitialiser();
		searchPage.setObjet("PG03");
		searchPage.rechercher();
		detailPG03 = corbeillePage.navigateToDetailCommByLinkNor(idDossier2, DetailCommPG_03.class);
		detailPG03.checkValue(DetailCommPG_03.EMETTEUR, gvt);
		detailPG03.checkValue(DetailCommPG_03.DESTINATAIRE, senat);
		detailPG03.checkValue(DetailCommPG_03.COPIE, an);
		detailPG03.checkValue(DetailCommPG_03.OBJET, "PG03");
		
		commPG04 = detailPG03.navigateToNextComm("PG04", CreateCommPG04Page.class);
		DetailCommPG_04 detailPG04 = commPG04.createCommPG04("PG04-senat", "favorable", horodatage);

		detailPG04.checkValue(DetailCommPG_04.EMETTEUR, senat);
		detailPG04.checkValue(DetailCommPG_04.DESTINATAIRE, gvt);
		detailPG04.checkValue(DetailCommPG_04.COPIE, an);
		detailPG04.checkValue(DetailCommPG_04.OBJET, "PG04-senat");
		detailPG04.checkValue(DetailCommPG_04.ID_COMMUNICATION_PRECEDENTE, "PG03_15122014_00000");
		
		getFlog().endAction();
		doLogout();

	}

	private void doANActions7() {
		RechercheRapidePage searchPage;

		doLoginAs(EppTestConstantes.AN_LOGIN, EppTestConstantes.AN_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.AN_LOGIN);

		getFlog().startAction("7 - Verifications de l'AN");

		corbeillePage.openCorbeille(DECLARATIONS_DU_GOUVERNEMENT);
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.reinitialiser();
		searchPage.setObjet("PG04-senat");
		searchPage.rechercher();
		DetailCommPG_04 detailPG04 = corbeillePage.navigateToDetailCommByLinkNor(idDossier2, DetailCommPG_04.class);
		
		detailPG04.checkValue(DetailCommPG_04.EMETTEUR, senat);
		detailPG04.checkValue(DetailCommPG_04.DESTINATAIRE, gvt);
		detailPG04.checkValue(DetailCommPG_04.COPIE, an);
		detailPG04.checkValue(DetailCommPG_04.OBJET, "PG04-senat");

		getFlog().endAction();
		doLogout();
	}

	private void doGVTActions8() {
		RechercheRapidePage searchPage;
		DetailCommPG_04 detailPG04;
		CreateCommFusion createCommFusion;

		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);

		getFlog().startAction("8 - Verifications du Gouvernement + actions");

		corbeillePage.openCorbeille("Destinataire");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.reinitialiser();
		searchPage.setObjet("PG04-senat");
		searchPage.rechercher();
		detailPG04 = corbeillePage.navigateToDetailCommByLinkNor(idDossier2, DetailCommPG_04.class);
		
		detailPG04.checkValue(DetailCommPG_04.EMETTEUR, senat);
		detailPG04.checkValue(DetailCommPG_04.DESTINATAIRE, gvt);
		detailPG04.checkValue(DetailCommPG_04.COPIE, an);
		detailPG04.checkValue(DetailCommPG_04.OBJET, "PG04-senat");
		
		createCommFusion = detailPG04.navigateToNextComm("FUSION11", CreateCommFusion.class);
		DetailCommPGFusion detailPGFusion = createCommFusion.createCommFusion("FUS01", an, idDossier);
		
		detailPGFusion.checkValue(DetailCommPGFusion.EMETTEUR, gvt);
		detailPGFusion.checkValue(DetailCommPGFusion.DESTINATAIRE, an);
		detailPGFusion.checkValue(DetailCommPGFusion.COPIE, senat);
		detailPGFusion.checkValue(DetailCommPGFusion.OBJET, "FUS01");

		getFlog().endAction();
		doLogout();
	}

	private void doGVTActions9() {
		CreateCommPG01Page createCommPG01Page;
		final String horodatage = DateUtils.format(Calendar.getInstance());

		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);

		getFlog().startAction("9 - Verifications GVT");

		createCommPG01Page = corbeillePage.navigateToCreateComm(EvenementType.PG_01, CreateCommPG01Page.class);
		DetailCommPG_01 detailPG01 = createCommPG01Page.createCommPG01(idDossier3, "PG01-bis", horodatage, horodatage);

		detailPG01.checkValue(DetailCommPG_01.EMETTEUR, gvt);
		detailPG01.checkValue(DetailCommPG_01.DESTINATAIRE, an);
		detailPG01.checkValue(DetailCommPG_01.COPIE, senat);
		detailPG01.checkValue(DetailCommPG_01.OBJET, "PG01-bis");
		
		getFlog().endAction();
		doLogout();
	}

	private void doANActions10() {
		RechercheRapidePage searchPage;
		DetailCommPG_01 detailPG01;
		CreateCommFusion createCommFusion;

		doLoginAs(EppTestConstantes.AN_LOGIN, EppTestConstantes.AN_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.AN_LOGIN);

		getFlog().startAction("10 - vérifications + actions Assemblée");

		corbeillePage.openCorbeille(TOUTES_LES_COMMUNICATIONS);
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.setObjet("FUS01");
		searchPage.rechercher();
		DetailCommPGFusion detailPGFusion = corbeillePage.navigateToDetailCommByLinkNor(idDossier2, DetailCommPGFusion.class);
		
		detailPGFusion.checkValue(DetailCommPGFusion.EMETTEUR, gvt);
		detailPGFusion.checkValue(DetailCommPGFusion.DESTINATAIRE, an);
		detailPGFusion.checkValue(DetailCommPGFusion.COPIE, senat);
		detailPGFusion.checkValue(DetailCommPGFusion.OBJET, "FUS01");

		corbeillePage.openCorbeille(DECLARATIONS_DU_GOUVERNEMENT);
		searchPage.reinitialiser();
		searchPage.setObjet("PG01-bis");
		searchPage.rechercher();
		detailPG01 = corbeillePage.navigateToDetailCommByLinkNor(idDossier3, DetailCommPG_01.class);
		
		detailPG01.checkValue(DetailCommPG_01.EMETTEUR, gvt);
		detailPG01.checkValue(DetailCommPG_01.DESTINATAIRE, an);
		detailPG01.checkValue(DetailCommPG_01.COPIE, senat);
		detailPG01.checkValue(DetailCommPG_01.OBJET, "PG01-bis");

		createCommFusion = detailPG01.navigateToNextComm("FUSION11", CreateCommFusion.class);
		detailPGFusion = createCommFusion.createCommFusion("FUS02", gvt, idDossier);
		
		detailPGFusion.checkValue(DetailCommPGFusion.EMETTEUR, an);
		detailPGFusion.checkValue(DetailCommPGFusion.DESTINATAIRE, gvt);
		detailPGFusion.checkValue(DetailCommPGFusion.COPIE, senat);
		detailPGFusion.checkValue(DetailCommPGFusion.OBJET, "FUS02");

		getFlog().endAction();
		doLogout();
	}

	private void doGVTActions11() {
		RechercheRapidePage searchPage;
		CreateCommPG03Page createCommPG_03;
		final String horodatage = DateUtils.format(Calendar.getInstance());

		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);

		getFlog().startAction("11 - Verifications GVT + actions");

		createCommPG_03 = corbeillePage.navigateToCreateComm(EvenementType.PG_03, CreateCommPG03Page.class);
		DetailCommPG_03 detailPG03 = createCommPG_03.createCommPG03(idDossier4, "PG03-bis", horodatage, horodatage);
		
		detailPG03.checkValue(DetailCommPG_03.EMETTEUR, gvt);
		detailPG03.checkValue(DetailCommPG_03.DESTINATAIRE, senat);
		detailPG03.checkValue(DetailCommPG_03.COPIE, an);
		detailPG03.checkValue(DetailCommPG_03.OBJET, "PG03-bis");

		corbeillePage.openCorbeille("Déclaration de politique générale");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.setObjet("FUS02");
		searchPage.rechercher();
		DetailCommPGFusion detailPGFusion = corbeillePage.navigateToDetailCommByLinkNor(idDossier3, DetailCommPGFusion.class);
		
		detailPGFusion.checkValue(DetailCommPGFusion.EMETTEUR, an);
		detailPGFusion.checkValue(DetailCommPGFusion.DESTINATAIRE, gvt);
		detailPGFusion.checkValue(DetailCommPGFusion.COPIE, senat);
		detailPGFusion.checkValue(DetailCommPGFusion.OBJET, "FUS02");

		getFlog().endAction();
		doLogout();
	}

	private void doSenatActions12() {
		RechercheRapidePage searchPage;
		CreateCommFusion createCommFusion;
		DetailCommPG_03 detailPG03;

		doLoginAs(EppTestConstantes.SENAT_LOGIN, EppTestConstantes.SENAT_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.SENAT_PASSWORD);

		getFlog().startAction("12 - Verifications Senat + actions");

		corbeillePage.openCorbeille("Pour info");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.setObjet("FUS02");
		searchPage.rechercher();
		DetailCommPGFusion detailPGFusion = corbeillePage.navigateToDetailCommByLinkNor(idDossier3, DetailCommPGFusion.class);
		
		detailPGFusion.checkValue(DetailCommPGFusion.EMETTEUR, an);
		detailPGFusion.checkValue(DetailCommPGFusion.DESTINATAIRE, gvt);
		detailPGFusion.checkValue(DetailCommPGFusion.COPIE, senat);
		detailPGFusion.checkValue(DetailCommPGFusion.OBJET, "FUS02");

		corbeillePage.openCorbeille("Destinataire");
		searchPage.reinitialiser();
		searchPage.setObjet("PG03-bis");
		searchPage.rechercher();
		detailPG03 = corbeillePage.navigateToDetailCommByLinkNor(idDossier4, DetailCommPG_03.class);
		
		detailPG03.checkValue(DetailCommPG_03.EMETTEUR, gvt);
		detailPG03.checkValue(DetailCommPG_03.DESTINATAIRE, senat);
		detailPG03.checkValue(DetailCommPG_03.COPIE, an);
		detailPG03.checkValue(DetailCommPG_03.OBJET, "PG03-bis");

		createCommFusion = detailPG03.navigateToNextComm("FUSION11", CreateCommFusion.class);
		detailPGFusion = createCommFusion.createCommFusion("FUS03", gvt, idDossier3);
		
		detailPGFusion.checkValue(DetailCommPGFusion.EMETTEUR, senat);
		detailPGFusion.checkValue(DetailCommPGFusion.DESTINATAIRE, gvt);
		detailPGFusion.checkValue(DetailCommPGFusion.COPIE, an);
		detailPGFusion.checkValue(DetailCommPGFusion.OBJET, "FUS03");
		
		getFlog().endAction();
		doLogout();
	}

	private void doGVTActions13() {
		RechercheRapidePage searchPage;

		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);

		getFlog().startAction("13 - Verifications gvt");

		corbeillePage.openCorbeille("Déclaration de politique générale");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.setObjet("FUS03");
		searchPage.rechercher();
		DetailCommPGFusion detailPGFusion = corbeillePage.navigateToDetailCommByLinkNor(idDossier4, DetailCommPGFusion.class);
		
		detailPGFusion.checkValue(DetailCommPGFusion.EMETTEUR, senat);
		detailPGFusion.checkValue(DetailCommPGFusion.DESTINATAIRE, gvt);
		detailPGFusion.checkValue(DetailCommPGFusion.COPIE, an);
		detailPGFusion.checkValue(DetailCommPGFusion.OBJET, "FUS03");

		getFlog().endAction();
		doLogout();
	}

	private void doANActions14() {
		RechercheRapidePage searchPage;

		doLoginAs(EppTestConstantes.AN_LOGIN, EppTestConstantes.AN_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.AN_LOGIN);

		getFlog().startAction("14 - Verifications an");

		corbeillePage.openCorbeille(TOUTES_LES_COMMUNICATIONS);
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.setObjet("FUS03");
		searchPage.rechercher();
		DetailCommPGFusion detailPGFusion = corbeillePage.navigateToDetailCommByLinkNor(idDossier4, DetailCommPGFusion.class);
		
		detailPGFusion.checkValue(DetailCommPGFusion.EMETTEUR, senat);
		detailPGFusion.checkValue(DetailCommPGFusion.DESTINATAIRE, gvt);
		detailPGFusion.checkValue(DetailCommPGFusion.COPIE, an);
		detailPGFusion.checkValue(DetailCommPGFusion.OBJET, "FUS03");

		getFlog().endAction();
		doLogout();
	}
}
