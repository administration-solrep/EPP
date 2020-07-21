package fr.dila.solonepp.webtest.webdriver030;

import java.util.Calendar;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.RechercheRapidePage;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.doc.create.CreateDOC01;
import fr.dila.solonepp.page.communication.doc.create.alerte.CreateDOCAlerte;
import fr.dila.solonepp.page.communication.doc.create.generique.CreateDOCGenerique;
import fr.dila.solonepp.page.communication.doc.detail.DetailCommDOC01;
import fr.dila.solonepp.page.communication.doc.detail.alerte.DetailCommDOC_Alerte;
import fr.dila.solonepp.page.communication.doc.detail.generique.DetailCommDOCGenerique;
import fr.dila.solonepp.utils.DateUtils;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * - Test de création d'une procédure de transmission de documents
 * 
 * @author abianchi
 * 
 */
public class TestProcedureTransmissionDocuments2 extends AbstractEppWebTest {

	final public static String gvt = "Gouvernement";
	final public static String an = "Assemblée nationale";
	final public static String senat = "Sénat";

	final public static String idDossierTest = "201526030001";

	final String horodatage = DateUtils.format(Calendar.getInstance());

	@WebTest(description = "Réalisation d'une procédure de transmission de documents")
	@TestDocumentation(categories = {"Comm. DOC-01", "Comm. Générique15", "Corbeille", "Comm. Alerte15", "Recherche rapide"})
	public void testTransmissionDoc() {

		getFlog().startAction("Transmission de documents");

		doGVTActions1();
		doSenatActions2();
		doGVTActions3();
		doSenatActions4();
		doGVTActions5();

		getFlog().endAction();
	}

	private void doGVTActions1() {
		getFlog().startAction("1 -Début des actions du gouvernement");

		CreateDOC01 createDOC01;
		DetailCommDOC01 detailCommDOC01;
		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);

		createDOC01 = corbeillePage.navigateToCreateComm(EvenementType.DOC_01, CreateDOC01.class);
		detailCommDOC01 = createDOC01.createCommeDOC01(idDossierTest, senat, "Transmission doc1", horodatage);

		detailCommDOC01.checkValue(AbstractDetailComm.IDENTIFIANT_DOSSIER, idDossierTest);
		detailCommDOC01.checkValue(AbstractDetailComm.DESTINATAIRE, senat);
		detailCommDOC01.checkValueStartWith(AbstractDetailComm.HORODATAGE, horodatage);
		detailCommDOC01.checkValue(AbstractDetailComm.OBJET, "Transmission doc1");

		doLogout();
		getFlog().endAction();
	}

	@SuppressWarnings("static-access")
	private void doSenatActions2() {
		getFlog().startAction("2 -Début des actions du sénat");
		CreateDOCGenerique createDOCGenerique;
		DetailCommDOC01 detailCommDOC01;
		
		doLoginAs(EppTestConstantes.SENAT_LOGIN, EppTestConstantes.SENAT_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.SENAT_LOGIN);
		
		corbeillePage.openCorbeille("Destinataire");
				
		detailCommDOC01 = corbeillePage.navigateToDetailCommByLinkNor(idDossierTest, DetailCommDOC01.class);

		detailCommDOC01.checkValue(detailCommDOC01.IDENTIFIANT_DOSSIER, idDossierTest);
		detailCommDOC01.checkValue(detailCommDOC01.DESTINATAIRE, senat);
		detailCommDOC01.checkValueStartWith(detailCommDOC01.HORODATAGE, horodatage);
		detailCommDOC01.checkValue(detailCommDOC01.OBJET, "Transmission doc1");

		createDOCGenerique = detailCommDOC01.navigateToNextComm("GENERIQUE15", CreateDOCGenerique.class);
		createDOCGenerique.createCommDOCGenerique(gvt, "réponse Transmission doc1");

		doLogout();
		getFlog().endAction();
	}

	private void doGVTActions3() {
		getFlog().startAction("3 -Début des actions du gouvernement");
		RechercheRapidePage searchPage;
		CreateDOCAlerte createDOCAlerte;
		DetailCommDOCGenerique commDOCGenerique;
		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);

		corbeillePage.openCorbeille("Autres documents transmis aux assemblées");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.reinitialiser();
		searchPage.setObjet("réponse Transmission doc1");
		searchPage.rechercher();
		commDOCGenerique = corbeillePage.navigateToDetailCommByLinkNor(idDossierTest, DetailCommDOCGenerique.class);
		createDOCAlerte = commDOCGenerique.navigateToNextComm("ALERTE15", CreateDOCAlerte.class);

		createDOCAlerte.createAlerte("Alerte Transmission doc1", senat);

		doLogout();
		getFlog().endAction();
	}

	private void doSenatActions4() {
		getFlog().startAction("4 -Début des actions du sénat");
		RechercheRapidePage searchPage;
		doLoginAs(EppTestConstantes.SENAT_LOGIN, EppTestConstantes.SENAT_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.SENAT_LOGIN);

		corbeillePage.openCorbeille("Pour info");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.reinitialiser();
		searchPage.setObjet("Alerte Transmission doc1");
		searchPage.rechercher();
		corbeillePage.navigateToDetailCommByLinkNor(idDossierTest, DetailCommDOC_Alerte.class);

		doLogout();
		getFlog().endAction();
	}

	private void doGVTActions5() {
		getFlog().startAction("5 -Début des actions du gourvenement");
		RechercheRapidePage searchPage;
		doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);
		corbeillePage.verifierIdentite(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN);

		corbeillePage.openCorbeille("Autres documents transmis aux assemblées");
		searchPage = corbeillePage.openRechercheRapide();
		searchPage.reinitialiser();
		searchPage.setObjet("Alerte Transmission doc1");
		searchPage.rechercher();
		corbeillePage.navigateToDetailCommByLinkNor(idDossierTest, DetailCommDOC_Alerte.class);

		doLogout();
		getFlog().endAction();

	}

}
