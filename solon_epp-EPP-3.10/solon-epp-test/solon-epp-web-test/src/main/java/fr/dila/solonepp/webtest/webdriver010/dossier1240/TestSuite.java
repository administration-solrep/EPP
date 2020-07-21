package fr.dila.solonepp.webtest.webdriver010.dossier1240;

import java.util.Calendar;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.dila.solonepp.page.communication.lex.create.CreateComm01Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm03Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm04BisPage;
import fr.dila.solonepp.page.communication.lex.create.CreateComm10Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm12Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm13BisPage;
import fr.dila.solonepp.page.communication.lex.create.CreateComm13Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm19Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm21Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm22Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23BisPage;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23QuaterPage;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23QuinquiesPage;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23TerPage;
import fr.dila.solonepp.page.communication.lex.create.CreateComm24Page;
import fr.dila.solonepp.page.communication.lex.create.CreateComm28Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm01Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm03Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm04BisPage;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm10Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm12Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm13BisPage;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm13Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm19Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm21Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm22Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm23BisPage;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm23Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm23QuaterPage;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm23QuinquiesPage;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm23TerPage;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm24Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm28Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.WebPage;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.NiveauLectureCode;

/**
 * Test suite EPP dossier_1240 - 
 * 
 ****************1ere lecture AN****************
 * test n°1 adminsgg créé une communication LEX-01 - nor : AN000011080 - envoi à l'AN
 * test n°2 adminsgg créé une communication LEX-14 à partir de la communication créée dans le test n° 1 - envoi à l'AN
 * test n°3 an créé une communication successive LEX-03 à partir de la communication du test n°1 - envoi au gouvernement
 * test n°4 an créé une communication successive LEX-04 à partir de la communication du test n°3 - envoi au gouvernement
 * test n°5 an créé une communication successive LEX-16 à partir de la communication du test n°3 - envoi au gouvernement
 ****************1ere lecture Sénat****************
 * test n°6 adminsgg créé une communication successive LEX-17 à partir de la communication du test n°5 - envoi au Sénat
 * test n°7 senat créé une communication successive LEX-18 à partir de la communication du test n°6 - envoi au gouvernement
 * test n°8  senat créé une communication successive LEX-04 à partir de la communication du test n°7 - envoi au gouvernement
 * test n°9 senat créé une communication successive LEX-16 à partir de la communication du test n°8 - envoi au gouvernement 
 ****************2ème lecture AN****************
 * test n°10 adminsgg créé une communication successive LEX-17 à partir de la communication du test n°9 - envoi à l'AN
 * test n°11 an créé une communication successive LEX-18 à partir de la communication du test n°10 - envoi au gouvernement
 * test n°12 an créé une communication successive LEX-04 à partir de la communication du test n°11 - envoi au gouvernement
 * test n°13 an créé une communication successive LEX-16 à partir de la communication du test n°12 - envoi au gouvernement 
 ****************2ème lecture Sénat****************
 * test n°14 adminsgg créé une communication successive LEX-17 à partir de la communication du test n°13 - envoi au sénat
 * test n°15 senat créé une communication successive LEX-18 à partir de la communication du test n°14 - envoi au gouvernement
 * test n°16  senat créé une communication successive LEX-04 à partir de la communication du test n°15 - envoi au gouvernement
 * test n°17 senat créé une communication successive LEX-16 à partir de la communication du test n°16 - envoi au gouvernement    
 ****************CMP saisie AN****************
 * test n°18 adminsgg créé une communication successive LEX-17 à partir de la communication du test n°17 - envoi à l'AN
 * test n°19 an créé une communication successive LEX-18 à partir de la communication du test n°18 - envoi au gouvernement
 * test n°20 adminsgg créé une communication successive LEX-22 à partir de la communication du test n°19 - envoi à l'AN
 * test n°21 an créé une communication successive LEX-24 à partir de la communication du test n°20 - envoi au gouvernement
 * test n°22 an créé une communication successive LEX-25 à partir de la communication du test n°21 - envoi au gouvernement
 * test n°23 adminsgg créé une communication successive LEX-26 à partir de la communication du test n°22 - envoi à l'AN
 * test n°24 an créé une communication successive LEX-16 à partir de la communication du test n°22 - envoi au gouvernement
 ****************CMP saisie Sénat****************    
 * test n°25 adminsgg créé une communication successive LEX-22 à partir de la communication du test n°24 - envoi au Sénat
 * test n°26 senat créé une communication successive LEX-24 à partir de la communication du test n°25 - envoi au gouvernement
 * test n°27 senat créé une communication successive LEX-25 à partir de la communication du test n°26 - envoi au gouvernement
 * test n°28 adminsgg créé une communication successive LEX-26 à partir de la communication du test n°27 - envoi au Sénat
 * test n°29 senat créé une communication successive LEX-16 à partir de la communication du test n°27 - envoi au gouvernement
 ****************Nouvelle lecture AN****************  
 * test n°30 adminsgg créé une communication successive LEX-27 à partir de la communication du test n°29  - envoi à l'AN
 * test n°31 an créé une communication successive LEX-28 à partir de la communication du test n°30 - envoi au gouvernement
 * test n°32 an créé une communication successive LEX-04 à partir de la communication du test n°31 - envoi au gouvernement
 * test n°33 an créé une communication successive LEX-16 à partir de la communication du test n°32 - envoi au gouvernement 
 ****************Nouvelle lecture Sénat****************
 * test n°34 adminsgg créé une communication successive LEX-17 à partir de la communication du test n°33 - envoi au Sénat
 * test n°35 senat créé une communication successive LEX-18 à partir de la communication du test n°34 - envoi au gouvernement
 * test n°36 senat créé une communication successive LEX-04 à partir de la communication du test n°35 - envoi au gouvernement
 * test n°37 senat créé une communication successive LEX-16 à partir de la communication du test n°36 - envoi au gouvernement
 ****************Lecture définitive AN****************
 * test n°38 adminsgg créé une communication successive LEX-29 à partir de la communication du test n°37 - envoi à l'AN
 * test n°39 an créé une communication successive LEX-30 à partir de la communication du test n°38 - envoi au gouvernement
 * test n°40 an créé une communication successive LEX-04 à partir de la communication du test n°39 - envoi au gouvernement
 * test n°41 an créé une communication successive LEX-31 à partir de la communication du test n°40 - envoi au gouvernement
 * test n°42 adminsgg créé une communication successive LEX-35 à partir de la communication du test n°41
 */

public class TestSuite extends AbstractEppWebTest {

    private static final String NUM_NOR = "AN000011080";
    private static final String COMMENTAIRE = "commentaire";
    private static final String OBJET = "Objet";
    private static final String INTITULE = "Projet de loi Objet";

    private void doRapidSearch(String corbeilleName, String valueOptionSelected) {
        corbeillePage.deployFirstElementCorbeille();
        WebDriverWait wait = new WebDriverWait(getDriver(), WebPage.TIMEOUT_IN_SECONDS);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("corbeilleForm:corbeilleRegion:status.start")));
        corbeillePage.openCorbeille(corbeilleName);

        getFlog().check("Vérification de la présence du NOR " + NUM_NOR);
        Assert.assertTrue(corbeillePage.hasElement(By.xpath("//*[contains(text(), '" + NUM_NOR + "')]")));

        corbeillePage.openRechercheRapide();

        getFlog().action("Ajout du type de communication " + valueOptionSelected + " dans les critères de recherche");
        WebElement typeComm = getDriver().findElement(By.id("quickSearchForm:nxl_recherche_rapide_layout:nxw_recherche_type_evenement_participant_list"));
        Select typesSelect = new Select(typeComm);
        typesSelect.selectByValue(valueOptionSelected);

        getDriver().findElement(By.id("quickSearchForm:nxl_recherche_rapide_layout:nxw_recherche_id_dossier")).sendKeys(NUM_NOR);

        corbeillePage.doRechercheRapide();
    }

    /**
     * Test suite EPP dossier_1240 - test n°1
     * 
     * Créé une communication LEX-01 - nor : AN000011080
     * 
     */
    @WebTest(description = "Creation Comm 01 (LEX-01) adminsgg - 1ère lecture an")
    @TestDocumentation(categories = {"Comm. LEX-01"})
    public void testCase001() {
        final String typeComm = "LEX-01 : Pjl - Dépôt";
        final String emetteur = "Gouvernement";
        final String destinataire = "Assemblée nationale";
        final String copie = "Sénat";
        final String auteur = "fil";
        final String coauteur = "fil";
        final Integer niveauLecture = 1;
        final NiveauLectureCode organisme = NiveauLectureCode.AN;
        
        doLoginAs("adminsgg", "adminsgg");

        // Ouverture de la page de création de l'évènement 01
        final CreateComm01Page createComm01Page = corbeillePage.navigateToCreateComm(EvenementType.EVT_01, CreateComm01Page.class);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm01Page detailPage = createComm01Page.createComm01(emetteur, destinataire, OBJET, NUM_NOR, auteur, coauteur, niveauLecture, organisme, false);

        // Vérification sur la page de détail de l'évènement créé des champs
        detailPage.checkValue(DetailComm01Page.COMMUNICATION, typeComm);
        detailPage.checkValue(DetailComm01Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00000");
        detailPage.checkValue(DetailComm01Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage.checkValue(DetailComm01Page.EMETTEUR, emetteur);
        detailPage.checkValue(DetailComm01Page.DESTINATAIRE, destinataire);
        detailPage.checkValue(DetailComm01Page.COPIE, copie);
        detailPage.checkValueStartWith(DetailComm01Page.HORODATAGE, horodatage);
        detailPage.checkValue(DetailComm01Page.OBJET, OBJET);
        detailPage.checkValue(DetailComm01Page.NOR, NUM_NOR);
        detailPage.checkValue(DetailComm01Page.NATURE_LOI, "Projet");
        detailPage.checkValue(DetailComm01Page.TYPE_LOI, "Loi");
        detailPage.checkValue(DetailComm01Page.AUTEUR, "M. Fillon François");
        detailPage.checkValue(DetailComm01Page.COAUTEUR, "M. Fillon François");
        detailPage.checkValue(DetailComm01Page.INTITULE, INTITULE);
        detailPage.checkValue(DetailComm01Page.NIVEAU_LECTURE, "1 - Assemblée nationale");

        doLogout();
    }

    /**
     * Test suite EPP dossier_1240 - test n°2 
     * Créé une communication LEX-14 à partir de la communication créée dans le test n° 1 de la suite
     * 
     */
    @WebTest(description = "Creation Comm S_10 (LEX-14) adminsgg - 1ère lecture an")
    @TestDocumentation(categories = {"Comm. LEX-01", "Recherche rapide", "Comm. LEX-14"})
    public void testCase002() {
        final String typeComm = "LEX-14 : Engagement de la procédure accélérée";
        final String emetteur = "Gouvernement";
        final String destinataire = "Assemblée nationale";
        final String copie = "Sénat";

        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Emis", "EVT01");

        // Ouverture de la page de détail de l'évènement créé dans le test 01
        final DetailComm01Page detailPage01 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm01Page.class);
        // Initialisation page de création évènement successif 10
        CreateComm10Page createPage10 = detailPage01.navigateToCreateCommSucc(EvenementType.EVT_10, CreateComm10Page.class);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de l'évènement puis publication
        DetailComm10Page detailPage10 = createPage10.createComm10(COMMENTAIRE, destinataire);
        // Vérification sur la page de détail de l'évènement 10 du bon remplissage des champs
        detailPage10.checkValue(DetailComm10Page.COMMUNICATION, typeComm);
        detailPage10.checkValue(DetailComm10Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00001");
        detailPage10.checkValue(DetailComm10Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00000");
        detailPage10.checkValue(DetailComm10Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage10.checkValue(DetailComm10Page.EMETTEUR, emetteur);
        detailPage10.checkValue(DetailComm10Page.DESTINATAIRE, destinataire);
        detailPage10.checkValue(DetailComm10Page.COPIE, copie);
        detailPage10.checkValueStartWith(DetailComm10Page.HORODATAGE, horodatage);
        detailPage10.checkValue(DetailComm10Page.OBJET, OBJET);
        detailPage10.checkValue(DetailComm10Page.INTITULE, INTITULE);
        detailPage10.checkValue(DetailComm10Page.COMMENTAIRE, COMMENTAIRE);
        detailPage10.checkValue(DetailComm10Page.NIVEAU_LECTURE, "1 - Assemblée nationale");

        doLogout();
    }

    /**
     * Test suite EPP dossier_1240 - test n°3 
     * Créé une communication successive LEX-03 à partir de la communication du test n°1
     * 
     */
    @WebTest(description = "Creation Comm S_03 (LEX-03) an - 1ère lecture an")
    @TestDocumentation(categories = {"Comm. LEX-03", "Comm. LEX-01", "Recherche rapide", "1ère lecture AN"})
    public void testCase003() {
        final String typeComm = "LEX-03 : Pjl - Enregistrement du dépôt";
        final String urlDossier = "urlDossier";
        final String numeroDepot = "4546-7647";
        final String dateDepot = "14/03/2012";
        final String commFond = "org";
        final String commAvis = "org";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";

        doLoginAs("an", "an");
        doRapidSearch("Séance réception", "EVT01");

        // Ouverture de la page de détail de l'évènement créé dans le test 02 envoyé par le gouvernement
        final DetailComm10Page detailPage10 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm10Page.class);
        // Initialisation de la page de création de l'évènement successif 03
        CreateComm03Page createPage03 = detailPage10.navigateToCreateCommSucc(EvenementType.EVT_03, CreateComm03Page.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs puis publication évènement
        DetailComm03Page detailPage03 = createPage03.createComm03(urlDossier, numeroDepot, COMMENTAIRE, dateDepot, commFond, commAvis);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage03.checkValue(DetailComm03Page.COMMUNICATION, typeComm);
        detailPage03.checkValue(DetailComm03Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00002");
        detailPage03.checkValue(DetailComm03Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00000");
        detailPage03.checkValue(DetailComm03Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage03.checkValue(DetailComm03Page.EMETTEUR, emetteur);
        detailPage03.checkValue(DetailComm03Page.DESTINATAIRE, destinataire);
        detailPage03.checkValue(DetailComm03Page.COPIE, copie);
        detailPage03.checkValueStartWith(DetailComm03Page.HORODATAGE, horodatage);
        detailPage03.checkValue(DetailComm03Page.OBJET, OBJET);
        detailPage03.checkValue(DetailComm03Page.INTITULE, INTITULE);
        detailPage03.checkValue(DetailComm03Page.COMMENTAIRE, COMMENTAIRE);
        detailPage03.checkValue(DetailComm03Page.COMM_SAISIE_FOND, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage03.checkValue(DetailComm03Page.COMM_SAISIE_AVIS, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage03.checkValue(DetailComm03Page.NIVEAU_LECTURE, "1 - Assemblée nationale");

        doLogout();
    }

    /**
     * Test suite EPP dossier_1240 - test n°4 
     * Créé une communication successive LEX-04 à partir de la communication du test n°3
     * 
     */
    @WebTest(description = "Creation Comm S_04Bis (LEX-04) an - 1ère lecture an")
    @TestDocumentation(categories = {"Comm. LEX-04", "Comm. LEX-03", "Recherche rapide", "1ère lecture AN"})
    public void testCase004() {
        final String typeComm = "LEX-04 : Dépôt de rapport, avis et texte de commission";
        final String dateDistribution = "14/03/2012";
        final String natureRapport = "Avis";
        final String versionTitre = "titre";
        final String numeroRapport = "4546-1234";
        final String dateDepotTexte = "14/03/2012";
        final String numeroTexte = "1234-0000";
        final String rapporteur = "ass";
        final String commission = "org";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";

        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT03");

        // Ouverture de la page de détail de l'évènement envoyé dans le test 03 envoyé par l'an
        final DetailComm03Page detailPage03 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm03Page.class);
        // Initialisation de la page de création de l'évènement successif 04 bis
        CreateComm04BisPage createPage04Bis = detailPage03.navigateToCreateCommSucc(EvenementType.EVT_04_BIS, CreateComm04BisPage.class);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs puis publication évènement
        DetailComm04BisPage detailPage04Bis = createPage04Bis.createComm04Bis(dateDistribution, natureRapport, versionTitre, numeroRapport, 
                dateDepotTexte, numeroTexte, rapporteur, commission, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMUNICATION, typeComm);
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00003");
        detailPage04Bis.checkValue(DetailComm04BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00002");
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage04Bis.checkValue(DetailComm04BisPage.EMETTEUR, emetteur);
        detailPage04Bis.checkValue(DetailComm04BisPage.DESTINATAIRE, destinataire);
        detailPage04Bis.checkValue(DetailComm04BisPage.COPIE, copie);
        detailPage04Bis.checkValueStartWith(DetailComm04BisPage.HORODATAGE, horodatage);
        detailPage04Bis.checkValue(DetailComm04BisPage.OBJET, OBJET);
        detailPage04Bis.checkValue(DetailComm04BisPage.INTITULE, INTITULE);
        detailPage04Bis.checkValue(DetailComm04BisPage.VERSION_TITRE, versionTitre);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage04Bis.checkValue(DetailComm04BisPage.NIVEAU_LECTURE, "1 - Assemblée nationale");
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DISTRIBUTION_ELECTRONIQUE, dateDistribution);
        detailPage04Bis.checkValue(DetailComm04BisPage.NATURE_RAPPORT, natureRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DEPOT_TEXTE, dateDepotTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_RAPPORT, numeroRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_TEXTE, numeroTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMISSION_SAISIE, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage04Bis.checkValue(DetailComm04BisPage.RAPPORTEURS_LIST, "M. Assemblée Nationale");

        doLogout();
    }

    /**
     * Test suite EPP dossier_1240 - test n°5 
     * Créé une communication successive LEX-16 à partir de la communication du test n°3
     * 
     */
    @WebTest(description = "Creation Comm S_12 (LEX-16) an - 1ère lecture an")
    @TestDocumentation(categories = {"Comm. LEX-16", "Comm. LEX-03", "Recherche rapide", "1ère lecture AN"})
    public void testCase005() {
        final String typeComm = "LEX-16 : Pjl - Transmission ou notification du rejet";
        final String numeroTexte = "1258-6398";
        final String dateAdoption = "14/03/2012";
        final String sortAdoption = "Adopté";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";

        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT03");

        // Chargement de la page de détail de l'évènement envoyé dans le test 4 par l'assemblée nationale
        final DetailComm03Page detailPage03 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm03Page.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm12Page createPage12 = detailPage03.navigateToCreateCommSucc(EvenementType.EVT_12, CreateComm12Page.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm12Page detailPage12 = createPage12.createComm12(numeroTexte, dateAdoption, sortAdoption, COMMENTAIRE);

        // Vérification des champs sur la page de détail de la communication publiée
        detailPage12.checkValue(DetailComm12Page.COMMUNICATION, typeComm);
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00004");
        detailPage12.checkValue(DetailComm12Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00002");
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage12.checkValue(DetailComm12Page.EMETTEUR, emetteur);
        detailPage12.checkValue(DetailComm12Page.DESTINATAIRE, destinataire);
        detailPage12.checkValue(DetailComm12Page.COPIE, copie);
        detailPage12.checkValueStartWith(DetailComm12Page.HORODATAGE, horodatage);
        detailPage12.checkValue(DetailComm12Page.OBJET, OBJET);
        detailPage12.checkValue(DetailComm12Page.INTITULE, INTITULE);
        detailPage12.checkValue(DetailComm12Page.COMMENTAIRE, COMMENTAIRE);
        detailPage12.checkValue(DetailComm12Page.NIVEAU_LECTURE, "1 - Assemblée nationale");
        detailPage12.checkValue(DetailComm12Page.DATE_ADOPTION, dateAdoption);
        detailPage12.checkValue(DetailComm12Page.SORT_ADOPTION, sortAdoption);
        detailPage12.checkValue(DetailComm12Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }

    /**
     * Test suite EPP dossier_1240 - test n°6 
     * Créé une communication successive LEX-17 à partir de la communication du test n°5
     * 
     */
    @WebTest(description = "Creation Comm S_13 (LEX-17) adminsgg - 1ère lecture senat")
    @TestDocumentation(categories = {"Comm. LEX-17", "Recherche rapide", "Comm. LEX-16", "1ère lecture Sénat"})
    public void testCase006() {
        final String typeComm = "LEX-17 : Pjl - Navettes diverses";        
        final String numeroTexte = "1234-0000";
        final String dateAdoption = "14/03/2012";
        final String sortAdoption = "Adopté";
        final Integer niveauLecture = 1;
        final NiveauLectureCode organisme = NiveauLectureCode.SENAT;
        final String emetteur = "Gouvernement";
        final String destinataire = "Sénat";
        final String copie = "Assemblée nationale";

        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT12");

        // Chargement de la page de détail de l'évènement envoyé dans le test 5 par l'assemblée nationale
        final DetailComm12Page detailPage12 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm12Page.class);
        // Initialisation de la page de création de l'évènement successif 13
        CreateComm13Page createComm13 = detailPage12.navigateToCreateCommSucc(EvenementType.EVT_13, CreateComm13Page.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm13Page detailPage13 = createComm13.createComm13(destinataire, COMMENTAIRE, numeroTexte, dateAdoption, 
                sortAdoption, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13.checkValue(DetailComm13Page.COMMUNICATION, typeComm);
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00005");
        detailPage13.checkValue(DetailComm13Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00004");
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13.checkValue(DetailComm13Page.EMETTEUR, emetteur);
        detailPage13.checkValue(DetailComm13Page.DESTINATAIRE, destinataire);
        detailPage13.checkValue(DetailComm13Page.COPIE, copie);
        detailPage13.checkValueStartWith(DetailComm13Page.HORODATAGE, horodatage);
        detailPage13.checkValue(DetailComm13Page.OBJET, OBJET);
        detailPage13.checkValue(DetailComm13Page.INTITULE, INTITULE);
        detailPage13.checkValue(DetailComm13Page.COMMENTAIRE, COMMENTAIRE);
        detailPage13.checkValue(DetailComm13Page.NIVEAU_LECTURE, "1 - Sénat");
        detailPage13.checkValue(DetailComm13Page.DATE_ADOPTION, dateAdoption);
        detailPage13.checkValue(DetailComm13Page.SORT_ADOPTION, sortAdoption);
        detailPage13.checkValue(DetailComm13Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }

    /**
     * Test suite EPP dossier_1240 - test n° 7 
     * Créé une communication successive LEX-18 à partir de la communication du test n°6
     * 
     */
    @WebTest(description = "Creation Comm S_13Bis (LEX-18) senat - 1ère lecture senat")
    @TestDocumentation(categories = {"Comm. LEX-18", "Recherche rapide", "Comm. LEX-17", "1ère lecture Sénat"})
    public void testCase007() {
        final String typeComm = "LEX-18 : Pjl - Enregistrement du dépôt en navette";        
        final Integer niveauLecture = 1;
        final NiveauLectureCode organisme = NiveauLectureCode.SENAT;
        final String numeroTexteAdopte = "1234-0000";
        final String dateAdoption = "14/02/2012";
        final String sortAdoption = "Adopté";
        final String dateDepotTexte = "14/02/2012";
        final String numeroDepot = "8965-8974";
        final String commFond = "org";
        final String commAvis = "org";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";

        doLoginAs("senat", "senat");
        doRapidSearch("Reçu", "EVT13");

        // Chargement de la page de détail de l'évènement envoyé dans le test 6 par le gouvernement
        final DetailComm10Page detailPage10 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm10Page.class);
        // Initialisation de la page de création de l'évènement successif 13Bis
        CreateComm13BisPage createPage13Bis = detailPage10.navigateToCreateCommSucc(EvenementType.EVT_13_BIS, CreateComm13BisPage.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm13BisPage detailPage13Bis = createPage13Bis.createComm13Bis(emetteur, COMMENTAIRE, numeroTexteAdopte, 
                dateAdoption, sortAdoption, dateDepotTexte, numeroDepot, commFond, commAvis, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMUNICATION, typeComm);
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00006");
        detailPage13Bis.checkValue(DetailComm13BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00005");
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13Bis.checkValue(DetailComm13BisPage.EMETTEUR, emetteur);
        detailPage13Bis.checkValue(DetailComm13BisPage.DESTINATAIRE, destinataire);
        detailPage13Bis.checkValue(DetailComm13BisPage.COPIE, copie);
        detailPage13Bis.checkValueStartWith(DetailComm13BisPage.HORODATAGE, horodatage);
        detailPage13Bis.checkValue(DetailComm13BisPage.OBJET, OBJET);
        detailPage13Bis.checkValue(DetailComm13BisPage.INTITULE, INTITULE);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage13Bis.checkValue(DetailComm13BisPage.NIVEAU_LECTURE, "1 - Sénat");
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_ADOPTION, dateAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.SORT_ADOPTION, sortAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_TEXTE_ADOPTE, numeroTexteAdopte);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_AVIS, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_FOND, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_DEPOT, numeroDepot);
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_DEPOT, dateDepotTexte);

        doLogout();
    }

    /**
     * Test suite EPP dossier_1240 - test n°8 
     * Créé une communication successive LEX-04 à partir de la communication du test n°7
     * 
     */
    @WebTest(description = "Creation Comm S_04bis sénat (LEX-04) senat - 1ère lecture senat")
    @TestDocumentation(categories = {"Comm. LEX-04", "Recherche rapide", "Comm. LEX-18", "1ère lecture Sénat"})
    public void testCase008() {
        final String typeComm = "LEX-04 : Dépôt de rapport, avis et texte de commission";
        final String dateDistribution = "14/03/2012";
        final String natureRapport = "Avis";
        final String versionTitre = "titre";
        final String numeroRapport = "4546-1234";
        final String numeroTexte = "4546-7647";
        final String rapporteur = "sén";
        final String commission = "org";
        final String dateDepotTexte = "14/03/2012";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";

        doLoginAs("senat", "senat");
        doRapidSearch("Émis", "EVT13BIS");

        // Chargement de la page de détail de l'évènement envoyé dans le test 7 par le sénat
        final DetailComm03Page detailPage03 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm03Page.class);
        // Initialisation de la page de création de l'évènement successif 04Bis
        CreateComm04BisPage createPage04Bis = detailPage03.navigateToCreateCommSucc(EvenementType.EVT_04_BIS, CreateComm04BisPage.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm04BisPage detailPage04Bis = createPage04Bis.createComm04Bis(dateDistribution, natureRapport, versionTitre, numeroRapport, 
                dateDepotTexte, numeroTexte, rapporteur, commission, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMUNICATION, typeComm);
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00007");
        detailPage04Bis.checkValue(DetailComm04BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00006");
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage04Bis.checkValue(DetailComm04BisPage.EMETTEUR, emetteur);
        detailPage04Bis.checkValue(DetailComm04BisPage.DESTINATAIRE, destinataire);
        detailPage04Bis.checkValue(DetailComm04BisPage.COPIE, copie);
        detailPage04Bis.checkValueStartWith(DetailComm04BisPage.HORODATAGE, horodatage);
        detailPage04Bis.checkValue(DetailComm04BisPage.OBJET, OBJET);
        detailPage04Bis.checkValue(DetailComm04BisPage.INTITULE, INTITULE);
        detailPage04Bis.checkValue(DetailComm04BisPage.VERSION_TITRE, versionTitre);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage04Bis.checkValue(DetailComm04BisPage.NIVEAU_LECTURE, "1 - Sénat");
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DISTRIBUTION_ELECTRONIQUE, dateDistribution);
        detailPage04Bis.checkValue(DetailComm04BisPage.NATURE_RAPPORT, natureRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DEPOT_TEXTE, dateDepotTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_RAPPORT, numeroRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_TEXTE, numeroTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMISSION_SAISIE, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage04Bis.checkValue(DetailComm04BisPage.RAPPORTEURS_LIST, "M. Sénat Sénat");

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°9 
     * Créé une communication successive LEX-16 à partir de la communication du test n°8 
     *
     */
    @WebTest(description = "Creation Comm S_12 (LEX-16) senat - 1ère lecture senat")
    @TestDocumentation(categories = {"Comm. LEX-16", "Recherche rapide", "Comm. LEX-04", "1ère lecture Sénat"})
    public void testCase009() {
        final String typeComm = "LEX-16 : Pjl - Transmission ou notification du rejet";
        final String numeroTexte = "1258-6398";
        final String dateAdoption = "14/03/2013";
        final String sortAdoption = "Adopté";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";
        
        doLoginAs("senat", "senat");
        doRapidSearch("Émis", "EVT04BIS");
        // Chargement de la page de détail de l'évènement envoyé dans le test 8 par le sénat
        final DetailComm04BisPage detailPage04Bis = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm04BisPage.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm12Page createPage12 = detailPage04Bis.navigateToCreateCommSucc(EvenementType.EVT_12, CreateComm12Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());        
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm12Page detailPage12 = createPage12.createComm12(numeroTexte, dateAdoption, sortAdoption, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage12.checkValue(DetailComm12Page.COMMUNICATION, typeComm);
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00008");
        detailPage12.checkValue(DetailComm12Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00007");
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage12.checkValue(DetailComm12Page.EMETTEUR, emetteur);
        detailPage12.checkValue(DetailComm12Page.DESTINATAIRE, destinataire);
        detailPage12.checkValue(DetailComm12Page.COPIE, copie);
        detailPage12.checkValueStartWith(DetailComm12Page.HORODATAGE, horodatage);
        detailPage12.checkValue(DetailComm12Page.OBJET, OBJET);
        detailPage12.checkValue(DetailComm12Page.INTITULE, INTITULE);
        detailPage12.checkValue(DetailComm12Page.COMMENTAIRE, COMMENTAIRE);
        detailPage12.checkValue(DetailComm12Page.NIVEAU_LECTURE, "1 - Sénat");
        detailPage12.checkValue(DetailComm12Page.DATE_ADOPTION, dateAdoption);
        detailPage12.checkValue(DetailComm12Page.SORT_ADOPTION, sortAdoption);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°10 
     * Créé une communication successive LEX-17 à partir de la communication du test n°9 
     *
     */
    @WebTest(description = "Creation Comm S_13 (LEX-17) adminsgg - 2ème lecture an")
    @TestDocumentation(categories = {"Comm. LEX-17", "Recherche rapide", "Comm. LEX-16", "2ème lecture AN"})
    public void testCase010() {
        final String typeComm = "LEX-17 : Pjl - Navettes diverses";
        final String dateAdoption = "14/03/2012";
        final String numeroTexte = "1234-0000";
        final String sortAdoption = "Adopté";
        final Integer niveauLecture = 2;
        final NiveauLectureCode organisme = NiveauLectureCode.AN;
        final String emetteur = "Gouvernement";
        final String destinataire = "Assemblée nationale";
        final String copie = "Sénat";
        
        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT12");        
        // Chargement de la page de détail de l'évènement envoyé dans le test 9 par le sénat
        final DetailComm12Page detailPage12 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm12Page.class);
        // Initialisation de la page de création de l'évènement successif 13
        CreateComm13Page createPage13 = detailPage12.navigateToCreateCommSucc(EvenementType.EVT_13, CreateComm13Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());        
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm13Page detailPage13 = createPage13.createComm13(destinataire, COMMENTAIRE, numeroTexte, dateAdoption, 
                sortAdoption, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13.checkValue(DetailComm13Page.COMMUNICATION, typeComm);
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00009");
        detailPage13.checkValue(DetailComm13Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00008");
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13.checkValue(DetailComm13Page.EMETTEUR, emetteur);
        detailPage13.checkValue(DetailComm13Page.DESTINATAIRE, destinataire);
        detailPage13.checkValue(DetailComm13Page.COPIE, copie);
        detailPage13.checkValueStartWith(DetailComm13Page.HORODATAGE, horodatage);
        detailPage13.checkValue(DetailComm13Page.OBJET, OBJET);
        detailPage13.checkValue(DetailComm13Page.INTITULE, INTITULE);
        detailPage13.checkValue(DetailComm13Page.COMMENTAIRE, COMMENTAIRE);
        detailPage13.checkValue(DetailComm13Page.NIVEAU_LECTURE, "2 - Assemblée nationale");
        detailPage13.checkValue(DetailComm13Page.DATE_ADOPTION, dateAdoption);
        detailPage13.checkValue(DetailComm13Page.SORT_ADOPTION, sortAdoption);
        detailPage13.checkValue(DetailComm13Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 11
     * Créé une communication successive LEX-18 à partir de la communication du test n°10 
     *
     */
    @WebTest(description = "Creation Comm S_13Bis (LEX-18) an - 2ème lecture an")
    @TestDocumentation(categories = {"Comm. LEX-18", "Recherche rapide", "Comm. LEX-17", "2ème lecture AN"})
    public void testCase011() {
        final String typeComm = "LEX-18 : Pjl - Enregistrement du dépôt en navette";
        final String dateDepotTexte = "14/03/2012";
        final String dateAdoption = "14/03/2012";
        final String numeroTexteAdopte = "1234-0000";
        final String numeroDepot = "8965-8974";
        final String sortAdoption = "Adopté";
        final String commFond = "org";
        final String commAvis = "org";
        final Integer niveauLecture = 2;
        final NiveauLectureCode organisme = NiveauLectureCode.AN;
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";
        
        doLoginAs("an", "an");
        doRapidSearch("Séance réception", "EVT13");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 10 par le gouvernement
        final DetailComm13Page detailPage13 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm13Page.class);
        // Initialisation de la page de création de l'évènement successif 13Bis
        CreateComm13BisPage createPage13Bis = detailPage13.navigateToCreateCommSucc(EvenementType.EVT_13_BIS, CreateComm13BisPage.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm13BisPage detailPage13Bis = createPage13Bis.createComm13Bis(emetteur, COMMENTAIRE, numeroTexteAdopte, 
                dateAdoption, sortAdoption, dateDepotTexte, numeroDepot, commFond, commAvis, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMUNICATION, typeComm);
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00010");
        detailPage13Bis.checkValue(DetailComm13BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00009");
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13Bis.checkValue(DetailComm13BisPage.EMETTEUR, emetteur);
        detailPage13Bis.checkValue(DetailComm13BisPage.DESTINATAIRE, destinataire);
        detailPage13Bis.checkValue(DetailComm13BisPage.COPIE, copie);
        detailPage13Bis.checkValueStartWith(DetailComm13BisPage.HORODATAGE, horodatage);
        detailPage13Bis.checkValue(DetailComm13BisPage.OBJET, OBJET);
        detailPage13Bis.checkValue(DetailComm13BisPage.INTITULE, INTITULE);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage13Bis.checkValue(DetailComm13BisPage.NIVEAU_LECTURE, "2 - Assemblée nationale");
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_ADOPTION, dateAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.SORT_ADOPTION, sortAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_TEXTE_ADOPTE, numeroTexteAdopte);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_AVIS, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_FOND, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_DEPOT, numeroDepot);
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_DEPOT, dateDepotTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°12 
     * Créé une communication successive LEX-04 à partir de la communication du test n°11 
     *
     */
    @WebTest(description = "Creation Comm S_04bis (LEX-04) an - 2ème lecture an")
    @TestDocumentation(categories = {"Comm. LEX-04", "Recherche rapide", "Comm. LEX-18", "2ème lecture AN"})
    public void testCase012() {
        final String typeComm = "LEX-04 : Dépôt de rapport, avis et texte de commission";
        final String versionTitre = "titre";
        final String dateDepotTexte = "14/03/2012";
        final String natureRapport = "Avis";
        final String numeroRapport = "4546-1234";
        final String numeroTexte = "4546-7647";
        final String dateDistribution = "14/03/2012";
        final String rapporteur = "ass";
        final String commission = "org";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";
        
        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT13BIS");        
        // Chargement de la page de détail de l'évènement envoyé dans le test 11 par l'assemblée nationale
        final DetailComm03Page detailPage03 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm03Page.class);
        // Initialisation de la page de création de l'évènement successif 04Bis
        CreateComm04BisPage createPage04Bis = detailPage03.navigateToCreateCommSucc(EvenementType.EVT_04_BIS, CreateComm04BisPage.class);
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm04BisPage detailPage04Bis = createPage04Bis.createComm04Bis(dateDistribution, natureRapport, versionTitre, 
                numeroRapport, dateDepotTexte, numeroTexte, rapporteur, commission, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMUNICATION, typeComm);
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00011");
        detailPage04Bis.checkValue(DetailComm04BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00010");
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage04Bis.checkValue(DetailComm04BisPage.EMETTEUR, emetteur);
        detailPage04Bis.checkValue(DetailComm04BisPage.DESTINATAIRE, destinataire);
        detailPage04Bis.checkValue(DetailComm04BisPage.COPIE, copie);
        detailPage04Bis.checkValueStartWith(DetailComm04BisPage.HORODATAGE, horodatage);
        detailPage04Bis.checkValue(DetailComm04BisPage.OBJET, OBJET);
        detailPage04Bis.checkValue(DetailComm04BisPage.INTITULE, INTITULE);
        detailPage04Bis.checkValue(DetailComm04BisPage.VERSION_TITRE, versionTitre);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage04Bis.checkValue(DetailComm04BisPage.NIVEAU_LECTURE, "2 - Assemblée nationale");
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DISTRIBUTION_ELECTRONIQUE, dateDistribution);
        detailPage04Bis.checkValue(DetailComm04BisPage.NATURE_RAPPORT, natureRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DEPOT_TEXTE, dateDepotTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_RAPPORT, numeroRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_TEXTE, numeroTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMISSION_SAISIE, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage04Bis.checkValue(DetailComm04BisPage.RAPPORTEURS_LIST, "M. Assemblée Nationale");        

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°13 
     * Créé une communication successive LEX-16 à partir de la communication du test n°12 
     *
     */
    @WebTest(description = "Creation Comm S_12 (LEX-16) an - 2ème lecture an")
    @TestDocumentation(categories = {"Comm. LEX-16", "Recherche rapide", "Comm. LEX-04", "2ème lecture AN" })
    public void testCase013() {
        final String typeComm = "LEX-16 : Pjl - Transmission ou notification du rejet";
        final String numeroTexte = "1258-6398";
        final String dateAdoption = "14/03/2012";
        final String sortAdoption = "Adopté";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";

        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT04BIS");

        // Chargement de la page de détail de l'évènement envoyé dans le test 12 par l'assemblée nationale
        final DetailComm04BisPage detailPage04Bis = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm04BisPage.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm12Page createPage12 = detailPage04Bis.navigateToCreateCommSucc(EvenementType.EVT_12, CreateComm12Page.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm12Page detailPage12 = createPage12.createComm12(numeroTexte, dateAdoption, sortAdoption, COMMENTAIRE);

        // Vérification des champs sur la page de détail de la communication publiée
        detailPage12.checkValue(DetailComm12Page.COMMUNICATION, typeComm);
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00012");
        detailPage12.checkValue(DetailComm12Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00011");
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage12.checkValue(DetailComm12Page.EMETTEUR, emetteur);
        detailPage12.checkValue(DetailComm12Page.DESTINATAIRE, destinataire);
        detailPage12.checkValue(DetailComm12Page.COPIE, copie);
        detailPage12.checkValueStartWith(DetailComm12Page.HORODATAGE, horodatage);
        detailPage12.checkValue(DetailComm12Page.OBJET, OBJET);
        detailPage12.checkValue(DetailComm12Page.INTITULE, INTITULE);
        detailPage12.checkValue(DetailComm12Page.COMMENTAIRE, COMMENTAIRE);
        detailPage12.checkValue(DetailComm12Page.NIVEAU_LECTURE, "2 - Assemblée nationale");
        detailPage12.checkValue(DetailComm12Page.DATE_ADOPTION, dateAdoption);
        detailPage12.checkValue(DetailComm12Page.SORT_ADOPTION, sortAdoption);
        detailPage12.checkValue(DetailComm12Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°14 
     * Créé une communication successive LEX-17 à partir de la communication du test n°13 
     *
     */
    @WebTest(description = "Creation Comm S_13 (LEX-17) adminsgg - 2ème lecture sénat")
    @TestDocumentation(categories = {"Comm. LEX-17", "Recherche rapide", "Comm. LEX-16", "2ème lecture Sénat"})
    public void testCase014() {
        final String typeComm = "LEX-17 : Pjl - Navettes diverses";
        final String dateAdoption = "14/03/2012";
        final String numeroTexte = "1234-0000";
        final String sortAdoption = "Adopté";
        final Integer niveauLecture = 2;
        final NiveauLectureCode organisme = NiveauLectureCode.SENAT;
        final String emetteur = "Gouvernement";
        final String destinataire = "Sénat";
        final String copie = "Assemblée nationale";
        
        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT12");        
        // Chargement de la page de détail de l'évènement envoyé dans le test 13 par l'assemblée nationale
        final DetailComm12Page detailPage12 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm12Page.class);
        // Initialisation de la page de création de l'évènement successif 13
        CreateComm13Page createPage13 = detailPage12.navigateToCreateCommSucc(EvenementType.EVT_13, CreateComm13Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());        
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm13Page detailPage13 = createPage13.createComm13(destinataire, COMMENTAIRE, numeroTexte, dateAdoption, 
                sortAdoption, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13.checkValue(DetailComm13Page.COMMUNICATION, typeComm);
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00013");
        detailPage13.checkValue(DetailComm13Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00012");
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13.checkValue(DetailComm13Page.EMETTEUR, emetteur);
        detailPage13.checkValue(DetailComm13Page.DESTINATAIRE, destinataire);
        detailPage13.checkValue(DetailComm13Page.COPIE, copie);
        detailPage13.checkValueStartWith(DetailComm13Page.HORODATAGE, horodatage);
        detailPage13.checkValue(DetailComm13Page.OBJET, OBJET);
        detailPage13.checkValue(DetailComm13Page.INTITULE, INTITULE);
        detailPage13.checkValue(DetailComm13Page.COMMENTAIRE, COMMENTAIRE);
        detailPage13.checkValue(DetailComm13Page.NIVEAU_LECTURE, "2 - Sénat");
        detailPage13.checkValue(DetailComm13Page.DATE_ADOPTION, dateAdoption);
        detailPage13.checkValue(DetailComm13Page.SORT_ADOPTION, sortAdoption);
        detailPage13.checkValue(DetailComm13Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 15
     * Créé une communication successive LEX-18 à partir de la communication du test n°14 
     *
     */
    @WebTest(description = "Creation Comm S_13Bis (LEX-18) senat - 2ème lecture sénat")
    @TestDocumentation(categories = {"Comm. LEX-18", "Recherche rapide", "Comm. LEX-17", "2ème lecture Sénat"})
    public void testCase015() {
        final String typeComm = "LEX-18 : Pjl - Enregistrement du dépôt en navette";
        final String dateDepotTexte = "14/03/2012";
        final String dateAdoption = "14/03/2012";
        final String numeroTexteAdopte = "1234-0000";
        final String numeroDepot = "8965-8974";
        final String sortAdoption = "Adopté";
        final String commFond = "org";
        final String commAvis = "org";
        final Integer niveauLecture = 2;
        final NiveauLectureCode organisme = NiveauLectureCode.SENAT;
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";        
        
        doLoginAs("senat", "senat");
        doRapidSearch("Reçu", "EVT13");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 14 par le gouvernement
        final DetailComm13Page detailPage13 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm13Page.class);
        // Initialisation de la page de création de l'évènement successif 13Bis
        CreateComm13BisPage createPage13Bis = detailPage13.navigateToCreateCommSucc(EvenementType.EVT_13_BIS, CreateComm13BisPage.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm13BisPage detailPage13Bis = createPage13Bis.createComm13Bis(emetteur, COMMENTAIRE, numeroTexteAdopte, 
                dateAdoption, sortAdoption, dateDepotTexte, numeroDepot, commFond, commAvis, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMUNICATION, typeComm);
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00014");
        detailPage13Bis.checkValue(DetailComm13BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00013");
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13Bis.checkValue(DetailComm13BisPage.EMETTEUR, emetteur);
        detailPage13Bis.checkValue(DetailComm13BisPage.DESTINATAIRE, destinataire);
        detailPage13Bis.checkValue(DetailComm13BisPage.COPIE, copie);
        detailPage13Bis.checkValueStartWith(DetailComm13BisPage.HORODATAGE, horodatage);
        detailPage13Bis.checkValue(DetailComm13BisPage.OBJET, OBJET);
        detailPage13Bis.checkValue(DetailComm13BisPage.INTITULE, INTITULE);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage13Bis.checkValue(DetailComm13BisPage.NIVEAU_LECTURE, "2 - Sénat");
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_ADOPTION, dateAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.SORT_ADOPTION, sortAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_TEXTE_ADOPTE, numeroTexteAdopte);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_AVIS, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_FOND, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_DEPOT, numeroDepot);
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_DEPOT, dateDepotTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°16 
     * Créé une communication successive LEX-04 à partir de la communication du test n°15 
     *
     */
    @WebTest(description = "Creation Comm S_04bis (LEX-04) senat - 2ème lecture sénat")
    @TestDocumentation(categories = {"Comm. LEX-04", "Recherche rapide", "Comm. LEX-18", "2ème lecture Sénat"})
    public void testCase016() {
        final String typeComm = "LEX-04 : Dépôt de rapport, avis et texte de commission";
        final String versionTitre = "titre";
        final String dateDepotTexte = "14/03/2012";
        final String natureRapport = "Avis";
        final String numeroRapport = "4546-1234";
        final String numeroTexte = "4546-7647";
        final String dateDistribution = "14/03/2012";
        final String rapporteur = "sén";
        final String commission = "org";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";
        
        doLoginAs("senat", "senat");
        doRapidSearch("Émis", "EVT13BIS");        
        // Chargement de la page de détail de l'évènement envoyé dans le test 15 par le sénat
        final DetailComm03Page detailPage03 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm03Page.class);
        // Initialisation de la page de création de l'évènement successif 04Bis
        CreateComm04BisPage createPage04Bis = detailPage03.navigateToCreateCommSucc(EvenementType.EVT_04_BIS, CreateComm04BisPage.class);
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm04BisPage detailPage04Bis = createPage04Bis.createComm04Bis(dateDistribution, natureRapport, versionTitre, 
                numeroRapport, dateDepotTexte, numeroTexte, rapporteur, commission, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMUNICATION, typeComm);
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00015");
        detailPage04Bis.checkValue(DetailComm04BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00014");
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage04Bis.checkValue(DetailComm04BisPage.EMETTEUR, emetteur);
        detailPage04Bis.checkValue(DetailComm04BisPage.DESTINATAIRE, destinataire);
        detailPage04Bis.checkValue(DetailComm04BisPage.COPIE, copie);
        detailPage04Bis.checkValueStartWith(DetailComm04BisPage.HORODATAGE, horodatage);
        detailPage04Bis.checkValue(DetailComm04BisPage.OBJET, OBJET);
        detailPage04Bis.checkValue(DetailComm04BisPage.INTITULE, INTITULE);
        detailPage04Bis.checkValue(DetailComm04BisPage.VERSION_TITRE, versionTitre);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage04Bis.checkValue(DetailComm04BisPage.NIVEAU_LECTURE, "2 - Sénat");
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DISTRIBUTION_ELECTRONIQUE, dateDistribution);
        detailPage04Bis.checkValue(DetailComm04BisPage.NATURE_RAPPORT, natureRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DEPOT_TEXTE, dateDepotTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_RAPPORT, numeroRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_TEXTE, numeroTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMISSION_SAISIE, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage04Bis.checkValue(DetailComm04BisPage.RAPPORTEURS_LIST, "M. Sénat Sénat");        

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°17 
     * Créé une communication successive LEX-16 à partir de la communication du test n°16 
     *
     */
    @WebTest(description = "Creation Comm S_12 (LEX-16) senat - 2ème lecture senat")
    @TestDocumentation(categories = {"Comm. LEX-16", "Recherche rapide", "Comm. LEX-04", "2ème lecture Sénat"})
    public void testCase017() {
        final String typeComm = "LEX-16 : Pjl - Transmission ou notification du rejet";
        final String numeroTexte = "1258-6398";
        final String dateAdoption = "14/03/2012";
        final String sortAdoption = "Adopté";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";

        doLoginAs("senat", "senat");
        doRapidSearch("Émis", "EVT04BIS");

        // Chargement de la page de détail de l'évènement envoyé dans le test 16 par le sénat
        final DetailComm04BisPage detailPage04Bis = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm04BisPage.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm12Page createPage12 = detailPage04Bis.navigateToCreateCommSucc(EvenementType.EVT_12, CreateComm12Page.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm12Page detailPage12 = createPage12.createComm12(numeroTexte, dateAdoption, sortAdoption, COMMENTAIRE);

        // Vérification des champs sur la page de détail de la communication publiée
        detailPage12.checkValue(DetailComm12Page.COMMUNICATION, typeComm);
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00016");
        detailPage12.checkValue(DetailComm12Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00015");
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage12.checkValue(DetailComm12Page.EMETTEUR, emetteur);
        detailPage12.checkValue(DetailComm12Page.DESTINATAIRE, destinataire);
        detailPage12.checkValue(DetailComm12Page.COPIE, copie);
        detailPage12.checkValueStartWith(DetailComm12Page.HORODATAGE, horodatage);
        detailPage12.checkValue(DetailComm12Page.OBJET, OBJET);
        detailPage12.checkValue(DetailComm12Page.INTITULE, INTITULE);
        detailPage12.checkValue(DetailComm12Page.COMMENTAIRE, COMMENTAIRE);
        detailPage12.checkValue(DetailComm12Page.NIVEAU_LECTURE, "2 - Sénat");
        detailPage12.checkValue(DetailComm12Page.DATE_ADOPTION, dateAdoption);
        detailPage12.checkValue(DetailComm12Page.SORT_ADOPTION, sortAdoption);
        detailPage12.checkValue(DetailComm12Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°18 
     * Créé une communication successive LEX-17 à partir de la communication du test n°17 
     *
     */
    @WebTest(description = "Creation Comm S_13 (LEX-17) adminsgg - CMP saisie an")
    @TestDocumentation(categories = {"Comm. LEX-17", "Recherche rapide", "Comm. LEX-16", "CMP"})
    public void testCase018() {
        final String typeComm = "LEX-17 : Pjl - Navettes diverses";
        final String dateAdoption = "14/03/2012";
        final String numeroTexte = "1234-0000";
        final String sortAdoption = "Adopté";
        final Integer niveauLecture = null;
        final NiveauLectureCode organisme = NiveauLectureCode.CMP;
        final String emetteur = "Gouvernement";
        final String destinataire = "Assemblée nationale";
        final String copie = "Sénat";
        
        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT12");
        // Chargement de la page de détail de l'évènement envoyé dans le test 17 par le sénat
        final DetailComm12Page detailPage12 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm12Page.class);
        // Initialisation de la page de création de l'évènement successif 13
        CreateComm13Page createPage13 = detailPage12.navigateToCreateCommSucc(EvenementType.EVT_13, CreateComm13Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());        
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm13Page detailPage13 = createPage13.createComm13(destinataire, COMMENTAIRE, numeroTexte, dateAdoption, 
                sortAdoption, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13.checkValue(DetailComm13Page.COMMUNICATION, typeComm);
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00017");
        detailPage13.checkValue(DetailComm13Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00016");
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13.checkValue(DetailComm13Page.EMETTEUR, emetteur);
        detailPage13.checkValue(DetailComm13Page.DESTINATAIRE, destinataire);
        detailPage13.checkValue(DetailComm13Page.COPIE, copie);
        detailPage13.checkValueStartWith(DetailComm13Page.HORODATAGE, horodatage);
        detailPage13.checkValue(DetailComm13Page.OBJET, OBJET);
        detailPage13.checkValue(DetailComm13Page.INTITULE, INTITULE);
        detailPage13.checkValue(DetailComm13Page.COMMENTAIRE, COMMENTAIRE);
        detailPage13.checkValue(DetailComm13Page.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage13.checkValue(DetailComm13Page.DATE_ADOPTION, dateAdoption);
        detailPage13.checkValue(DetailComm13Page.SORT_ADOPTION, sortAdoption);
        detailPage13.checkValue(DetailComm13Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 19
     * Créé une communication successive LEX-18 à partir de la communication du test n°18 
     *
     */
    @WebTest(description = "Creation Comm S_13Bis (LEX-18) an - CMP saisie an")
    @TestDocumentation(categories = {"Comm. LEX-18", "Recherche rapide", "Comm. LEX-17", "CMP"})
    public void testCase019() {
        final String typeComm = "LEX-18 : Pjl - Enregistrement du dépôt en navette";
        final String dateDepotTexte = "14/03/2012";
        final String dateAdoption = "14/03/2012";
        final String numeroTexteAdopte = "1234-0000";
        final String numeroDepot = "8965-8974";
        final String sortAdoption = "Adopté";
        final String commFond = "org";
        final String commAvis = "org";
        final Integer niveauLecture = null;
        final NiveauLectureCode organisme = NiveauLectureCode.CMP;
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";
        
        doLoginAs("an", "an");
        doRapidSearch("Séance réception", "EVT13");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 18 par le gouvernement
        final DetailComm13Page detailPage13 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm13Page.class);
        // Initialisation de la page de création de l'évènement successif 13Bis
        CreateComm13BisPage createPage13Bis = detailPage13.navigateToCreateCommSucc(EvenementType.EVT_13_BIS, CreateComm13BisPage.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm13BisPage detailPage13Bis = createPage13Bis.createComm13Bis(emetteur, COMMENTAIRE, numeroTexteAdopte, 
                dateAdoption, sortAdoption, dateDepotTexte, numeroDepot, commFond, commAvis, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMUNICATION, typeComm);
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00018");
        detailPage13Bis.checkValue(DetailComm13BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00017");
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13Bis.checkValue(DetailComm13BisPage.EMETTEUR, emetteur);
        detailPage13Bis.checkValue(DetailComm13BisPage.DESTINATAIRE, destinataire);
        detailPage13Bis.checkValue(DetailComm13BisPage.COPIE, copie);
        detailPage13Bis.checkValueStartWith(DetailComm13BisPage.HORODATAGE, horodatage);
        detailPage13Bis.checkValue(DetailComm13BisPage.OBJET, OBJET);
        detailPage13Bis.checkValue(DetailComm13BisPage.INTITULE, INTITULE);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage13Bis.checkValue(DetailComm13BisPage.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_ADOPTION, dateAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.SORT_ADOPTION, sortAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_TEXTE_ADOPTE, numeroTexteAdopte);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_AVIS, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_FOND, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_DEPOT, numeroDepot);
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_DEPOT, dateDepotTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 20
     * Créé une communication successive LEX-22 à partir de la communication du test n°19 
     *
     */
    @WebTest(description = "Creation Comm S_19 (LEX-22) adminsgg - CMP saisie an")
    @TestDocumentation(categories = {"Comm. LEX-22", "Recherche rapide", "Comm. LEX-18", "CMP"})
    public void testCase020() {
        final String typeComm = "LEX-22 : CMP - Demande de réunion par le Gouvernement";
        final String emetteur = "Gouvernement";
        final String destinataire = "Assemblée nationale";
        final String copie = "Sénat";
        
        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT13BIS");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 19 par l'assemblée nationale
        final DetailComm13BisPage detailPage13Bis = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm13BisPage.class);
        // Initialisation de la page de création de l'évènement successif 19
        CreateComm19Page createPage19 = detailPage13Bis.navigateToCreateCommSucc(EvenementType.EVT_19, CreateComm19Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm19Page detailPage19 = createPage19.createComm19(destinataire, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage19.checkValue(DetailComm19Page.COMMUNICATION, typeComm);
        detailPage19.checkValue(DetailComm19Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00019");
        detailPage19.checkValue(DetailComm19Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00018");
        detailPage19.checkValue(DetailComm19Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage19.checkValue(DetailComm19Page.EMETTEUR, emetteur);
        detailPage19.checkValue(DetailComm19Page.DESTINATAIRE, destinataire);
        detailPage19.checkValue(DetailComm19Page.COPIE, copie);
        detailPage19.checkValueStartWith(DetailComm19Page.HORODATAGE, horodatage);
        detailPage19.checkValue(DetailComm19Page.OBJET, OBJET);
        detailPage19.checkValue(DetailComm19Page.INTITULE, INTITULE);
        detailPage19.checkValue(DetailComm19Page.COMMENTAIRE, COMMENTAIRE);
        detailPage19.checkValue(DetailComm19Page.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage19.checkValue(DetailComm19Page.DATE_COMM, horodatage);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 21
     * Créé une communication successive LEX-24 à partir de la communication du test n°20 
     *
     */
    @WebTest(description = "Creation Comm S_21 (LEX-24) an - CMP saisie an")
    @TestDocumentation(categories = {"Comm. LEX-24", "Recherche rapide", "Comm. LEX-22", "CMP"})
    public void testCase021() {
        final String typeComm = "LEX-24 : CMP - Convocation";
        final String dateConvocation = "14/03/2012";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";
        
        doLoginAs("an", "an");
        doRapidSearch("Séance réception", "EVT19");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 20 par le gouvernement
        final DetailComm19Page detailPage19 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm19Page.class);
        // Initialisation de la page de création de l'évènement successif 21
        CreateComm21Page createPage21 = detailPage19.navigateToCreateCommSucc(EvenementType.EVT_21, CreateComm21Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm21Page detailPage21 = createPage21.createComm21(emetteur, dateConvocation, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage21.checkValue(DetailComm21Page.COMMUNICATION, typeComm);
        detailPage21.checkValue(DetailComm21Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00020");
        detailPage21.checkValue(DetailComm21Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00019");
        detailPage21.checkValue(DetailComm21Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage21.checkValue(DetailComm21Page.EMETTEUR, emetteur);
        detailPage21.checkValue(DetailComm21Page.DESTINATAIRE, destinataire);
        detailPage21.checkValue(DetailComm21Page.COPIE, copie);
        detailPage21.checkValueStartWith(DetailComm21Page.HORODATAGE, horodatage);
        detailPage21.checkValue(DetailComm21Page.OBJET, OBJET);
        detailPage21.checkValue(DetailComm21Page.INTITULE, INTITULE);
        detailPage21.checkValue(DetailComm21Page.COMMENTAIRE, COMMENTAIRE);
        detailPage21.checkValue(DetailComm21Page.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage21.checkValue(DetailComm21Page.DATE_CONVOCATION, dateConvocation);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 22
     * Créé une communication successive LEX-25 à partir de la communication du test n°21 
     *
     */
    @WebTest(description = "Creation Comm S_22 (LEX-25) an - CMP saisie an")
    @TestDocumentation(categories = {"Comm. LEX-25", "Recherche rapide", "Comm. LEX-24", "CMP"})
    public void testCase022() {
        final String typeComm = "LEX-25 : CMP - Notification du résultat et dépôt du rapport";
        final String numeroDepotRapport = "2011-5652";
        final String dateDepot = "14/03/2012";
        final String numeroDepotTexte = "2011-8965";
        final String libelleResCmp = "Accord";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";
        
        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT21");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 21 par l'assemblée nationale
        final DetailComm21Page detailPage21 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm21Page.class);
        // Initialisation de la page de création de l'évènement successif 22
        CreateComm22Page createPage22 = detailPage21.navigateToCreateCommSucc(EvenementType.EVT_22, CreateComm22Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm22Page detailPage22 = createPage22.createComm22(emetteur, numeroDepotRapport, dateDepot, numeroDepotTexte,
                libelleResCmp, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage22.checkValue(DetailComm22Page.COMMUNICATION, typeComm);
        detailPage22.checkValue(DetailComm22Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00021");
        detailPage22.checkValue(DetailComm22Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00020");
        detailPage22.checkValue(DetailComm22Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage22.checkValue(DetailComm22Page.EMETTEUR, emetteur);
        detailPage22.checkValue(DetailComm22Page.DESTINATAIRE, destinataire);
        detailPage22.checkValue(DetailComm22Page.COPIE, copie);
        detailPage22.checkValueStartWith(DetailComm22Page.HORODATAGE, horodatage);
        detailPage22.checkValue(DetailComm22Page.OBJET, OBJET);
        detailPage22.checkValue(DetailComm22Page.INTITULE, INTITULE);
        detailPage22.checkValue(DetailComm22Page.COMMENTAIRE, COMMENTAIRE);
        detailPage22.checkValue(DetailComm22Page.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage22.checkValue(DetailComm22Page.DATE_DEPOT, dateDepot);
        detailPage22.checkValue(DetailComm22Page.NUMERO_DEPOT_RAPPORT, numeroDepotRapport);
        detailPage22.checkValue(DetailComm22Page.NUMERO_DEPOT_TEXTE, numeroDepotTexte);
        detailPage22.checkValue(DetailComm22Page.RESULTAT_CMP, libelleResCmp);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 23
     * Créé une communication successive LEX-26 à partir de la communication du test n°22 
     *
     */
    @WebTest(description = "Creation Comm S_23 (LEX-26) adminsgg - CMP saisie an")
    @TestDocumentation(categories = {"Comm. LEX-26", "Recherche rapide", "Comm. LEX-25", "CMP"})
    public void testCase023() {
        final String typeComm = "LEX-26 : CMP - Demande de lecture des conclusions";        
        final String emetteur = "Gouvernement";
        final String destinataire = "Assemblée nationale";
        final String copie = "Sénat";
        
        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT22");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 22 par l'assemblée nationale
        final DetailComm22Page detailPage22 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm22Page.class);
        // Initialisation de la page de création de l'évènement successif 23
        CreateComm23Page createPage23 = detailPage22.navigateToCreateCommSucc(EvenementType.EVT_23, CreateComm23Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm23Page detailPage23 = createPage23.createComm23(destinataire, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage23.checkValue(DetailComm23Page.COMMUNICATION, typeComm);
        detailPage23.checkValue(DetailComm23Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00022");
        detailPage23.checkValue(DetailComm23Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00021");
        detailPage23.checkValue(DetailComm23Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage23.checkValue(DetailComm23Page.EMETTEUR, emetteur);
        detailPage23.checkValue(DetailComm23Page.DESTINATAIRE, destinataire);
        detailPage23.checkValue(DetailComm23Page.COPIE, copie);
        detailPage23.checkValueStartWith(DetailComm23Page.HORODATAGE, horodatage);
        detailPage23.checkValue(DetailComm23Page.OBJET, OBJET);
        detailPage23.checkValue(DetailComm23Page.INTITULE, INTITULE);
        detailPage23.checkValue(DetailComm23Page.COMMENTAIRE, COMMENTAIRE);
        detailPage23.checkValue(DetailComm23Page.NIVEAU_LECTURE, "Commission mixte paritaire");

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°24 
     * Créé une communication successive LEX-16 à partir de la communication du test n°22 
     *
     */
    @WebTest(description = "Creation Comm S_12 (LEX-16) an - CMP saisie an")
    @TestDocumentation(categories = {"Comm. LEX-16", "Recherche rapide", "Comm. LEX-25", "CMP"})
    public void testCase024() {
        final String typeComm = "LEX-16 : Pjl - Transmission ou notification du rejet";
        final String numeroTexte = "1258-6398";
        final String dateAdoption = "14/03/2012";
        final String sortAdoption = "Adopté";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";

        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT22");

        // Chargement de la page de détail de l'évènement envoyé dans le test 22 par l'assemblée nationale
        final DetailComm22Page detailPage22 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm22Page.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm12Page createPage12 = detailPage22.navigateToCreateCommSucc(EvenementType.EVT_12, CreateComm12Page.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm12Page detailPage12 = createPage12.createComm12(numeroTexte, dateAdoption, sortAdoption, COMMENTAIRE);

        // Vérification des champs sur la page de détail de la communication publiée
        detailPage12.checkValue(DetailComm12Page.COMMUNICATION, typeComm);
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00023");
        detailPage12.checkValue(DetailComm12Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00021");
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage12.checkValue(DetailComm12Page.EMETTEUR, emetteur);
        detailPage12.checkValue(DetailComm12Page.DESTINATAIRE, destinataire);
        detailPage12.checkValue(DetailComm12Page.COPIE, copie);
        detailPage12.checkValueStartWith(DetailComm12Page.HORODATAGE, horodatage);
        detailPage12.checkValue(DetailComm12Page.OBJET, OBJET);
        detailPage12.checkValue(DetailComm12Page.INTITULE, INTITULE);
        detailPage12.checkValue(DetailComm12Page.COMMENTAIRE, COMMENTAIRE);
        detailPage12.checkValue(DetailComm12Page.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage12.checkValue(DetailComm12Page.DATE_ADOPTION, dateAdoption);
        detailPage12.checkValue(DetailComm12Page.SORT_ADOPTION, sortAdoption);
        detailPage12.checkValue(DetailComm12Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 25
     * Créé une communication successive LEX-22 à partir de la communication du test n°24 
     *
     */
    @WebTest(description = "Creation Comm S_19 (LEX-22) adminsgg - CMP saisie sénat")
    @TestDocumentation(categories = {"Comm. LEX-22", "Recherche rapide", "Comm. LEX-16", "CMP"})
    public void testCase025() {
        final String typeComm = "LEX-22 : CMP - Demande de réunion par le Gouvernement";
        final String emetteur = "Gouvernement";
        final String destinataire = "Sénat";
        final String copie = "Assemblée nationale";
        
        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT12");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 24 par l'assemblée nationale
        final DetailComm12Page detailPage12 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm12Page.class);
        // Initialisation de la page de création de l'évènement successif 19
        CreateComm19Page createPage19 = detailPage12.navigateToCreateCommSucc(EvenementType.EVT_19, CreateComm19Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm19Page detailPage19 = createPage19.createComm19(destinataire, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage19.checkValue(DetailComm19Page.COMMUNICATION, typeComm);
        detailPage19.checkValue(DetailComm19Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00024");
        detailPage19.checkValue(DetailComm19Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00023");
        detailPage19.checkValue(DetailComm19Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage19.checkValue(DetailComm19Page.EMETTEUR, emetteur);
        detailPage19.checkValue(DetailComm19Page.DESTINATAIRE, destinataire);
        detailPage19.checkValue(DetailComm19Page.COPIE, copie);
        detailPage19.checkValueStartWith(DetailComm19Page.HORODATAGE, horodatage);
        detailPage19.checkValue(DetailComm19Page.OBJET, OBJET);
        detailPage19.checkValue(DetailComm19Page.INTITULE, INTITULE);
        detailPage19.checkValue(DetailComm19Page.COMMENTAIRE, COMMENTAIRE);
        detailPage19.checkValue(DetailComm19Page.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage19.checkValue(DetailComm19Page.DATE_COMM, horodatage);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 26
     * Créé une communication successive LEX-24 à partir de la communication du test n°25 
     *
     */
    @WebTest(description = "Creation Comm S_21 (LEX-24) senat - CMP saisie sénat")
    @TestDocumentation(categories = {"Comm. LEX-24", "Recherche rapide", "Comm. LEX-22", "CMP"})
    public void testCase026() {
        final String typeComm = "LEX-24 : CMP - Convocation";
        final String dateConvocation = "14/03/2012";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";
        
        doLoginAs("senat", "senat");
        doRapidSearch("Reçu", "EVT19");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 25 par le gouvernement
        final DetailComm19Page detailPage19 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm19Page.class);
        // Initialisation de la page de création de l'évènement successif 21
        CreateComm21Page createPage21 = detailPage19.navigateToCreateCommSucc(EvenementType.EVT_21, CreateComm21Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm21Page detailPage21 = createPage21.createComm21(emetteur, dateConvocation, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage21.checkValue(DetailComm21Page.COMMUNICATION, typeComm);
        detailPage21.checkValue(DetailComm21Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00025");
        detailPage21.checkValue(DetailComm21Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00024");
        detailPage21.checkValue(DetailComm21Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage21.checkValue(DetailComm21Page.EMETTEUR, emetteur);
        detailPage21.checkValue(DetailComm21Page.DESTINATAIRE, destinataire);
        detailPage21.checkValue(DetailComm21Page.COPIE, copie);
        detailPage21.checkValueStartWith(DetailComm21Page.HORODATAGE, horodatage);
        detailPage21.checkValue(DetailComm21Page.OBJET, OBJET);
        detailPage21.checkValue(DetailComm21Page.INTITULE, INTITULE);
        detailPage21.checkValue(DetailComm21Page.COMMENTAIRE, COMMENTAIRE);
        detailPage21.checkValue(DetailComm21Page.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage21.checkValue(DetailComm21Page.DATE_CONVOCATION, dateConvocation);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 27
     * Créé une communication successive LEX-25 à partir de la communication du test n°26 
     *
     */
    @WebTest(description = "Creation Comm S_22 (LEX-25) senat - CMP saisie senat")
    @TestDocumentation(categories = {"Comm. LEX-25", "Recherche rapide", "Comm. LEX-24", "CMP"})
    public void testCase027() {
        final String typeComm = "LEX-25 : CMP - Notification du résultat et dépôt du rapport";
        final String numeroDepotRapport = "2011-5652";
        final String dateDepot = "14/03/2012";
        final String numeroDepotTexte = "2011-8965";
        final String libelleResCmp = "Accord";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";
        
        doLoginAs("senat", "senat");
        doRapidSearch("Émis", "EVT21");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 21 par le sénat
        final DetailComm21Page detailPage21 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm21Page.class);
        // Initialisation de la page de création de l'évènement successif 22
        CreateComm22Page createPage22 = detailPage21.navigateToCreateCommSucc(EvenementType.EVT_22, CreateComm22Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm22Page detailPage22 = createPage22.createComm22(emetteur, numeroDepotRapport, dateDepot, numeroDepotTexte,
                libelleResCmp, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage22.checkValue(DetailComm22Page.COMMUNICATION, typeComm);
        detailPage22.checkValue(DetailComm22Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00026");
        detailPage22.checkValue(DetailComm22Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00025");
        detailPage22.checkValue(DetailComm22Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage22.checkValue(DetailComm22Page.EMETTEUR, emetteur);
        detailPage22.checkValue(DetailComm22Page.DESTINATAIRE, destinataire);
        detailPage22.checkValue(DetailComm22Page.COPIE, copie);
        detailPage22.checkValueStartWith(DetailComm22Page.HORODATAGE, horodatage);
        detailPage22.checkValue(DetailComm22Page.OBJET, OBJET);
        detailPage22.checkValue(DetailComm22Page.INTITULE, INTITULE);
        detailPage22.checkValue(DetailComm22Page.COMMENTAIRE, COMMENTAIRE);
        detailPage22.checkValue(DetailComm22Page.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage22.checkValue(DetailComm22Page.DATE_DEPOT, dateDepot);
        detailPage22.checkValue(DetailComm22Page.NUMERO_DEPOT_RAPPORT, numeroDepotRapport);
        detailPage22.checkValue(DetailComm22Page.NUMERO_DEPOT_TEXTE, numeroDepotTexte);
        detailPage22.checkValue(DetailComm22Page.RESULTAT_CMP, libelleResCmp);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n° 28
     * Créé une communication successive LEX-26 à partir de la communication du test n°27 
     *
     */
    @WebTest(description = "Creation Comm S_23 (LEX-26) adminsgg - CMP saisie senat")
    @TestDocumentation(categories = {"Comm. LEX-26", "Recherche rapide", "Comm. LEX-25", "CMP"})
    public void testCase028() {
        final String typeComm = "LEX-26 : CMP - Demande de lecture des conclusions";        
        final String emetteur = "Gouvernement";
        final String destinataire = "Sénat";
        final String copie = "Assemblée nationale";
        
        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT22");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 22 par le sénat
        final DetailComm22Page detailPage22 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm22Page.class);
        // Initialisation de la page de création de l'évènement successif 23
        CreateComm23Page createPage23 = detailPage22.navigateToCreateCommSucc(EvenementType.EVT_23, CreateComm23Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm23Page detailPage23 = createPage23.createComm23(destinataire, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage23.checkValue(DetailComm23Page.COMMUNICATION, typeComm);
        detailPage23.checkValue(DetailComm23Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00027");
        detailPage23.checkValue(DetailComm23Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00026");
        detailPage23.checkValue(DetailComm23Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage23.checkValue(DetailComm23Page.EMETTEUR, emetteur);
        detailPage23.checkValue(DetailComm23Page.DESTINATAIRE, destinataire);
        detailPage23.checkValue(DetailComm23Page.COPIE, copie);
        detailPage23.checkValueStartWith(DetailComm23Page.HORODATAGE, horodatage);
        detailPage23.checkValue(DetailComm23Page.OBJET, OBJET);
        detailPage23.checkValue(DetailComm23Page.INTITULE, INTITULE);
        detailPage23.checkValue(DetailComm23Page.COMMENTAIRE, COMMENTAIRE);
        detailPage23.checkValue(DetailComm23Page.NIVEAU_LECTURE, "Commission mixte paritaire");

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°29 
     * Créé une communication successive LEX-16 à partir de la communication du test n°27 
     *
     */
    @WebTest(description = "Creation Comm S_12 (LEX-16) senat - CMP saisie sénat")
    @TestDocumentation(categories = {"Comm. LEX-16", "Recherche rapide", "Comm. LEX-26", "CMP"})
    public void testCase029() {
        final String typeComm = "LEX-16 : Pjl - Transmission ou notification du rejet";
        final String numeroTexte = "1258-6398";
        final String dateAdoption = "14/03/2012";
        final String sortAdoption = "Adopté";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";

        doLoginAs("senat", "senat");
        doRapidSearch("Émis", "EVT22");

        // Chargement de la page de détail de l'évènement envoyé dans le test 27 par le sénat
        final DetailComm22Page detailPage22 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm22Page.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm12Page createPage12 = detailPage22.navigateToCreateCommSucc(EvenementType.EVT_12, CreateComm12Page.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm12Page detailPage12 = createPage12.createComm12(numeroTexte, dateAdoption, sortAdoption, COMMENTAIRE);

        // Vérification des champs sur la page de détail de la communication publiée
        detailPage12.checkValue(DetailComm12Page.COMMUNICATION, typeComm);
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00028");
        detailPage12.checkValue(DetailComm12Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00026");
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage12.checkValue(DetailComm12Page.EMETTEUR, emetteur);
        detailPage12.checkValue(DetailComm12Page.DESTINATAIRE, destinataire);
        detailPage12.checkValue(DetailComm12Page.COPIE, copie);
        detailPage12.checkValueStartWith(DetailComm12Page.HORODATAGE, horodatage);
        detailPage12.checkValue(DetailComm12Page.OBJET, OBJET);
        detailPage12.checkValue(DetailComm12Page.INTITULE, INTITULE);
        detailPage12.checkValue(DetailComm12Page.COMMENTAIRE, COMMENTAIRE);
        detailPage12.checkValue(DetailComm12Page.NIVEAU_LECTURE, "Commission mixte paritaire");
        detailPage12.checkValue(DetailComm12Page.DATE_ADOPTION, dateAdoption);
        detailPage12.checkValue(DetailComm12Page.SORT_ADOPTION, sortAdoption);
        detailPage12.checkValue(DetailComm12Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°30 
     * Créé une communication successive LEX-27 à partir de la communication du test n°29 
     *
     */
    @WebTest(description = "Creation Comm S_23Bis (LEX-27) adminsgg - Nouvelle lecture AN")
    @TestDocumentation(categories = {"Comm. LEX-27", "Recherche rapide", "Comm. LEX-16", "Nouvelle lecture"})
    public void testCase030() {
        final String typeComm = "LEX-27 : Demande de nouvelle lecture";
        final Integer niveauLecture = null;
        final NiveauLectureCode organisme = NiveauLectureCode.NOUVELLE_LECTURE_AN;
        final String emetteur = "Gouvernement";
        final String destinataire = "Assemblée nationale";
        final String copie = "Sénat";

        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT12");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 29 par le sénat
        final DetailComm12Page detailPage12 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm12Page.class);
        // Initialisation de la page de création de l'évènement successif 23Bis
        CreateComm23BisPage createPage23Bis = detailPage12.navigateToCreateCommSucc(EvenementType.EVT_23_BIS, CreateComm23BisPage.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm23BisPage detailPage23Bis = createPage23Bis.createComm23Bis(destinataire, niveauLecture, organisme, COMMENTAIRE);
        
        // Vérification des champs sur la page de détail de la communication publiée
        detailPage23Bis.checkValue(DetailComm23BisPage.COMMUNICATION, typeComm);
        detailPage23Bis.checkValue(DetailComm23BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00029");
        detailPage23Bis.checkValue(DetailComm23BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00028");
        detailPage23Bis.checkValue(DetailComm23BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage23Bis.checkValue(DetailComm23BisPage.EMETTEUR, emetteur);
        detailPage23Bis.checkValue(DetailComm23BisPage.DESTINATAIRE, destinataire);
        detailPage23Bis.checkValue(DetailComm23BisPage.COPIE, copie);
        detailPage23Bis.checkValueStartWith(DetailComm23BisPage.HORODATAGE, horodatage);
        detailPage23Bis.checkValue(DetailComm23BisPage.OBJET, OBJET);
        detailPage23Bis.checkValue(DetailComm23BisPage.INTITULE, INTITULE);
        detailPage23Bis.checkValue(DetailComm23BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage23Bis.checkValue(DetailComm23BisPage.NIVEAU_LECTURE, "Nouvelle lecture AN");
        detailPage23Bis.checkValue(DetailComm23BisPage.DATE_DEMANDE, horodatage);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°31 
     * Créé une communication successive LEX-28 à partir de la communication du test n°30 
     *
     */
    @WebTest(description = "Creation Comm S_23Ter (LEX-28) an - Nouvelle lecture AN")
    @TestDocumentation(categories = {"Comm. LEX-28", "Recherche rapide", "Comm. LEX-27", "Nouvelle lecture"})
    public void testCase031() {
        final String typeComm = "LEX-28 : Enregistrement de la demande de nouvelle lecture";
        final String dateDepot = "14/03/2012";
        final String numeroDepot = "2012-8945";
        final String commFond = "org";
        final String commAvis = "org";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";

        doLoginAs("an", "an");
        doRapidSearch("Séance réception", "EVT23BIS");
        
        // Chargement de la page de détail de l'évènement envoyé dans le test 30 par le gouvernement
        final DetailComm23BisPage detailPage23Bis = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm23BisPage.class);
        // Initialisation de la page de création de l'évènement successif 23Ter
        CreateComm23TerPage createPage23Ter = detailPage23Bis.navigateToCreateCommSucc(EvenementType.EVT_23_TER, CreateComm23TerPage.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm23TerPage detailPage23Ter = createPage23Ter.createComm23Ter(dateDepot, numeroDepot, commFond, commAvis, COMMENTAIRE);
        
        // Vérification des champs sur la page de détail de la communication publiée
        detailPage23Ter.checkValue(DetailComm23TerPage.COMMUNICATION, typeComm);
        detailPage23Ter.checkValue(DetailComm23TerPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00030");
        detailPage23Ter.checkValue(DetailComm23TerPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00029");
        detailPage23Ter.checkValue(DetailComm23TerPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage23Ter.checkValue(DetailComm23TerPage.EMETTEUR, emetteur);
        detailPage23Ter.checkValue(DetailComm23TerPage.DESTINATAIRE, destinataire);
        detailPage23Ter.checkValue(DetailComm23TerPage.COPIE, copie);
        detailPage23Ter.checkValueStartWith(DetailComm23TerPage.HORODATAGE, horodatage);
        detailPage23Ter.checkValue(DetailComm23TerPage.OBJET, OBJET);
        detailPage23Ter.checkValue(DetailComm23TerPage.INTITULE, INTITULE);
        detailPage23Ter.checkValue(DetailComm23TerPage.COMMENTAIRE, COMMENTAIRE);
        detailPage23Ter.checkValue(DetailComm23TerPage.NIVEAU_LECTURE, "Nouvelle lecture AN");
        detailPage23Ter.checkValue(DetailComm23TerPage.DATE_DEPOT, dateDepot);
        detailPage23Ter.checkValue(DetailComm23TerPage.NUMERO_DEPOT, numeroDepot);
        detailPage23Ter.checkValue(DetailComm23TerPage.COMMISSION_SAISIE_FOND, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage23Ter.checkValue(DetailComm23TerPage.COMMISSION_SAISIE_AVIS, "Organisme Name (du 13/11/2010 au 13/11/2020)");

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°32 
     * Créé une communication successive LEX-04 à partir de la communication du test n°31
     *
     */
    @WebTest(description = "Creation Comm S_04bis (LEX-04) an - Nouvelle lecture AN")
    @TestDocumentation(categories = {"Comm. LEX-04", "Recherche rapide", "Comm. LEX-28", "Nouvelle lecture"})
    public void testCase032() {
        final String typeComm = "LEX-04 : Dépôt de rapport, avis et texte de commission";
        final String versionTitre = "titre";
        final String dateDepotTexte = "14/03/2012";
        final String natureRapport = "Avis";
        final String numeroRapport = "4546-1234";
        final String numeroTexte = "4546-7647";
        final String dateDistribution = "14/03/2012";
        final String rapporteur = "ass";
        final String commission = "org";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";
        
        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT23TER");        
        // Chargement de la page de détail de l'évènement envoyé dans le test 31 par l'assemblée nationale
        final DetailComm23TerPage detailPage23Ter = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm23TerPage.class);
        // Initialisation de la page de création de l'évènement successif 04Bis
        CreateComm04BisPage createPage04Bis = detailPage23Ter.navigateToCreateCommSucc(EvenementType.EVT_04_BIS, CreateComm04BisPage.class);
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm04BisPage detailPage04Bis = createPage04Bis.createComm04Bis(dateDistribution, natureRapport, versionTitre, 
                numeroRapport, dateDepotTexte, numeroTexte, rapporteur, commission, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMUNICATION, typeComm);
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00031");
        detailPage04Bis.checkValue(DetailComm04BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00030");
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage04Bis.checkValue(DetailComm04BisPage.EMETTEUR, emetteur);
        detailPage04Bis.checkValue(DetailComm04BisPage.DESTINATAIRE, destinataire);
        detailPage04Bis.checkValue(DetailComm04BisPage.COPIE, copie);
        detailPage04Bis.checkValueStartWith(DetailComm04BisPage.HORODATAGE, horodatage);
        detailPage04Bis.checkValue(DetailComm04BisPage.OBJET, OBJET);
        detailPage04Bis.checkValue(DetailComm04BisPage.INTITULE, INTITULE);
        detailPage04Bis.checkValue(DetailComm04BisPage.VERSION_TITRE, versionTitre);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage04Bis.checkValue(DetailComm04BisPage.NIVEAU_LECTURE, "Nouvelle lecture AN");
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DISTRIBUTION_ELECTRONIQUE, dateDistribution);
        detailPage04Bis.checkValue(DetailComm04BisPage.NATURE_RAPPORT, natureRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DEPOT_TEXTE, dateDepotTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_RAPPORT, numeroRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_TEXTE, numeroTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMISSION_SAISIE, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage04Bis.checkValue(DetailComm04BisPage.RAPPORTEURS_LIST, "M. Assemblée Nationale");        

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°33 
     * Créé une communication successive LEX-16 à partir de la communication du test n°32 
     *
     */
    @WebTest(description = "Creation Comm S_12 (LEX-16) an - Nouvelle lecture AN")
    @TestDocumentation(categories = {"Comm. LEX-16", "Recherche rapide", "Comm. LEX-04", "Nouvelle lecture"})
    public void testCase033() {
        final String typeComm = "LEX-16 : Pjl - Transmission ou notification du rejet";
        final String numeroTexte = "1258-6398";
        final String dateAdoption = "14/03/2012";
        final String sortAdoption = "Adopté";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";

        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT04BIS");

        // Chargement de la page de détail de l'évènement envoyé dans le test 32 par l'assemblée nationale
        final DetailComm04BisPage detailPage04Bis = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm04BisPage.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm12Page createPage12 = detailPage04Bis.navigateToCreateCommSucc(EvenementType.EVT_12, CreateComm12Page.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm12Page detailPage12 = createPage12.createComm12(numeroTexte, dateAdoption, sortAdoption, COMMENTAIRE);

        // Vérification des champs sur la page de détail de la communication publiée
        detailPage12.checkValue(DetailComm12Page.COMMUNICATION, typeComm);
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00032");
        detailPage12.checkValue(DetailComm12Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00031");
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage12.checkValue(DetailComm12Page.EMETTEUR, emetteur);
        detailPage12.checkValue(DetailComm12Page.DESTINATAIRE, destinataire);
        detailPage12.checkValue(DetailComm12Page.COPIE, copie);
        detailPage12.checkValueStartWith(DetailComm12Page.HORODATAGE, horodatage);
        detailPage12.checkValue(DetailComm12Page.OBJET, OBJET);
        detailPage12.checkValue(DetailComm12Page.INTITULE, INTITULE);
        detailPage12.checkValue(DetailComm12Page.COMMENTAIRE, COMMENTAIRE);
        detailPage12.checkValue(DetailComm12Page.NIVEAU_LECTURE, "Nouvelle lecture AN");
        detailPage12.checkValue(DetailComm12Page.DATE_ADOPTION, dateAdoption);
        detailPage12.checkValue(DetailComm12Page.SORT_ADOPTION, sortAdoption);
        detailPage12.checkValue(DetailComm12Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }
    

    /**
     * Test suite EPP dossier_1240 - test n°34 
     * Créé une communication successive LEX-17 à partir de la communication du test n°33
     * 
     */
    @WebTest(description = "Creation Comm S_13 (LEX-17) adminsgg - Nouvelle lecture Sénat")
    @TestDocumentation(categories = {"Comm. LEX-17", "Recherche rapide", "Comm. LEX-16", "Nouvelle lecture"})
    public void testCase034() {
        final String typeComm = "LEX-17 : Pjl - Navettes diverses";        
        final String numeroTexte = "1234-0000";
        final String dateAdoption = "14/03/2012";
        final String sortAdoption = "Adopté";
        final Integer niveauLecture = null;
        final NiveauLectureCode organisme = NiveauLectureCode.NOUVELLE_LECTURE_SENAT; 
        final String emetteur = "Gouvernement";
        final String destinataire = "Sénat";
        final String copie = "Assemblée nationale";

        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT12");

        // Chargement de la page de détail de l'évènement envoyé dans le test 33 par l'assemblée nationale
        final DetailComm12Page detailPage12 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm12Page.class);
        // Initialisation de la page de création de l'évènement successif 13
        CreateComm13Page createComm13 = detailPage12.navigateToCreateCommSucc(EvenementType.EVT_13, CreateComm13Page.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm13Page detailPage13 = createComm13.createComm13(destinataire, COMMENTAIRE, numeroTexte, dateAdoption, 
                sortAdoption, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13.checkValue(DetailComm13Page.COMMUNICATION, typeComm);
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00033");
        detailPage13.checkValue(DetailComm13Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00032");
        detailPage13.checkValue(DetailComm13Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13.checkValue(DetailComm13Page.EMETTEUR, emetteur);
        detailPage13.checkValue(DetailComm13Page.DESTINATAIRE, destinataire);
        detailPage13.checkValue(DetailComm13Page.COPIE, copie);
        detailPage13.checkValueStartWith(DetailComm13Page.HORODATAGE, horodatage);
        detailPage13.checkValue(DetailComm13Page.OBJET, OBJET);
        detailPage13.checkValue(DetailComm13Page.INTITULE, INTITULE);
        detailPage13.checkValue(DetailComm13Page.COMMENTAIRE, COMMENTAIRE);
        detailPage13.checkValue(DetailComm13Page.NIVEAU_LECTURE, "Nouvelle lecture Sénat");
        detailPage13.checkValue(DetailComm13Page.DATE_ADOPTION, dateAdoption);
        detailPage13.checkValue(DetailComm13Page.SORT_ADOPTION, sortAdoption);
        detailPage13.checkValue(DetailComm13Page.NUMERO_TEXTE_ADOPTE, numeroTexte);

        doLogout();
    }

    /**
     * Test suite EPP dossier_1240 - test n° 35 
     * Créé une communication successive LEX-18 à partir de la communication du test n°34
     * 
     */
    @WebTest(description = "Creation Comm S_13Bis (LEX-18) senat - Nouvelle lecture Sénat")
    @TestDocumentation(categories = {"Comm. LEX-18", "Recherche rapide", "Comm. LEX-17", "Nouvelle lecture"})
    public void testCase035() {
        final String typeComm = "LEX-18 : Pjl - Enregistrement du dépôt en navette";        
        final Integer niveauLecture = null;
        final NiveauLectureCode organisme = NiveauLectureCode.NOUVELLE_LECTURE_SENAT;
        final String numeroTexteAdopte = "1234-0000";
        final String dateAdoption = "14/02/2012";
        final String sortAdoption = "Adopté";
        final String dateDepotTexte = "14/02/2012";
        final String numeroDepot = "8965-8974";
        final String commFond = "org";
        final String commAvis = "org";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";

        doLoginAs("senat", "senat");
        doRapidSearch("Reçu", "EVT13");

        // Chargement de la page de détail de l'évènement envoyé dans le test 34 par le gouvernement
        final DetailComm10Page detailPage10 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm10Page.class);
        // Initialisation de la page de création de l'évènement successif 13Bis
        CreateComm13BisPage createPage13Bis = detailPage10.navigateToCreateCommSucc(EvenementType.EVT_13_BIS, CreateComm13BisPage.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm13BisPage detailPage13Bis = createPage13Bis.createComm13Bis(emetteur, COMMENTAIRE, numeroTexteAdopte, 
                dateAdoption, sortAdoption, dateDepotTexte, numeroDepot, commFond, commAvis, niveauLecture, organisme);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMUNICATION, typeComm);
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00034");
        detailPage13Bis.checkValue(DetailComm13BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00033");
        detailPage13Bis.checkValue(DetailComm13BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage13Bis.checkValue(DetailComm13BisPage.EMETTEUR, emetteur);
        detailPage13Bis.checkValue(DetailComm13BisPage.DESTINATAIRE, destinataire);
        detailPage13Bis.checkValue(DetailComm13BisPage.COPIE, copie);
        detailPage13Bis.checkValueStartWith(DetailComm13BisPage.HORODATAGE, horodatage);
        detailPage13Bis.checkValue(DetailComm13BisPage.OBJET, OBJET);
        detailPage13Bis.checkValue(DetailComm13BisPage.INTITULE, INTITULE);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage13Bis.checkValue(DetailComm13BisPage.NIVEAU_LECTURE, "Nouvelle lecture Sénat");
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_ADOPTION, dateAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.SORT_ADOPTION, sortAdoption);
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_TEXTE_ADOPTE, numeroTexteAdopte);
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_AVIS, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.COMM_SAISIE_FOND, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage13Bis.checkValue(DetailComm13BisPage.NUMERO_DEPOT, numeroDepot);
        detailPage13Bis.checkValue(DetailComm13BisPage.DATE_DEPOT, dateDepotTexte);

        doLogout();
    }

    /**
     * Test suite EPP dossier_1240 - test n°36 
     * Créé une communication successive LEX-04 à partir de la communication du test n°35
     * 
     */
    @WebTest(description = "Creation Comm S_04bis sénat (LEX-04) senat - Nouvelle lecture senat")
    @TestDocumentation(categories = {"Comm. LEX-04", "Recherche rapide", "Comm. LEX-17", "Nouvelle lecture"})
    public void testCase036() {
        final String typeComm = "LEX-04 : Dépôt de rapport, avis et texte de commission";
        final String dateDistribution = "14/03/2012";
        final String natureRapport = "Avis";
        final String versionTitre = "titre";
        final String numeroRapport = "4546-1234";
        final String numeroTexte = "4546-7647";
        final String rapporteur = "sén";
        final String commission = "org";
        final String dateDepotTexte = "14/03/2012";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";

        doLoginAs("senat", "senat");
        doRapidSearch("Émis", "EVT13BIS");

        // Chargement de la page de détail de l'évènement envoyé dans le test 35 par le sénat
        final DetailComm03Page detailPage03 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm03Page.class);
        // Initialisation de la page de création de l'évènement successif 04Bis
        CreateComm04BisPage createPage04Bis = detailPage03.navigateToCreateCommSucc(EvenementType.EVT_04_BIS, CreateComm04BisPage.class);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm04BisPage detailPage04Bis = createPage04Bis.createComm04Bis(dateDistribution, natureRapport, versionTitre, numeroRapport, 
                dateDepotTexte, numeroTexte, rapporteur, commission, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMUNICATION, typeComm);
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00035");
        detailPage04Bis.checkValue(DetailComm04BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00034");
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage04Bis.checkValue(DetailComm04BisPage.EMETTEUR, emetteur);
        detailPage04Bis.checkValue(DetailComm04BisPage.DESTINATAIRE, destinataire);
        detailPage04Bis.checkValue(DetailComm04BisPage.COPIE, copie);
        detailPage04Bis.checkValueStartWith(DetailComm04BisPage.HORODATAGE, horodatage);
        detailPage04Bis.checkValue(DetailComm04BisPage.OBJET, OBJET);
        detailPage04Bis.checkValue(DetailComm04BisPage.INTITULE, INTITULE);
        detailPage04Bis.checkValue(DetailComm04BisPage.VERSION_TITRE, versionTitre);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage04Bis.checkValue(DetailComm04BisPage.NIVEAU_LECTURE, "Nouvelle lecture Sénat");
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DISTRIBUTION_ELECTRONIQUE, dateDistribution);
        detailPage04Bis.checkValue(DetailComm04BisPage.NATURE_RAPPORT, natureRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DEPOT_TEXTE, dateDepotTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_RAPPORT, numeroRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_TEXTE, numeroTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMISSION_SAISIE, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage04Bis.checkValue(DetailComm04BisPage.RAPPORTEURS_LIST, "M. Sénat Sénat");

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°37 
     * Créé une communication successive LEX-16 à partir de la communication du test n°36 
     *
     */
    @WebTest(description = "Creation Comm S_12 (LEX-16) senat - Nouvelle lecture senat")
    @TestDocumentation(categories = {"Comm. LEX-16", "Recherche rapide", "Comm. LEX-04", "Nouvelle lecture"})
    public void testCase037() {
        final String typeComm = "LEX-16 : Pjl - Transmission ou notification du rejet";
        final String numeroTexte = "1258-6398";
        final String dateAdoption = "14/03/2013";
        final String sortAdoption = "Adopté";
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";
        
        doLoginAs("senat", "senat");
        doRapidSearch("Émis", "EVT04BIS");
        // Chargement de la page de détail de l'évènement envoyé dans le test 36 par le sénat
        final DetailComm04BisPage detailPage04Bis = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm04BisPage.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm12Page createPage12 = detailPage04Bis.navigateToCreateCommSucc(EvenementType.EVT_12, CreateComm12Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());        
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm12Page detailPage12 = createPage12.createComm12(numeroTexte, dateAdoption, sortAdoption, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage12.checkValue(DetailComm12Page.COMMUNICATION, typeComm);
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00036");
        detailPage12.checkValue(DetailComm12Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00035");
        detailPage12.checkValue(DetailComm12Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage12.checkValue(DetailComm12Page.EMETTEUR, emetteur);
        detailPage12.checkValue(DetailComm12Page.DESTINATAIRE, destinataire);
        detailPage12.checkValue(DetailComm12Page.COPIE, copie);
        detailPage12.checkValueStartWith(DetailComm12Page.HORODATAGE, horodatage);
        detailPage12.checkValue(DetailComm12Page.OBJET, OBJET);
        detailPage12.checkValue(DetailComm12Page.INTITULE, INTITULE);
        detailPage12.checkValue(DetailComm12Page.COMMENTAIRE, COMMENTAIRE);
        detailPage12.checkValue(DetailComm12Page.NIVEAU_LECTURE, "Nouvelle lecture Sénat");
        detailPage12.checkValue(DetailComm12Page.DATE_ADOPTION, dateAdoption);
        detailPage12.checkValue(DetailComm12Page.SORT_ADOPTION, sortAdoption);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°38 
     * Créé une communication successive LEX-29 à partir de la communication du test n°37 
     *
     */
    @WebTest(description = "Creation Comm S_23Quater (LEX-29) adminsgg - Lecture définitive AN")
    @TestDocumentation(categories = {"Comm. LEX-29", "Recherche rapide", "Comm. LEX-16", "Lecture définitive"})
    public void testCase038() {
        final String typeComm = "LEX-29 : Demande de lecture définitive à l'Assemblée nationale";
        final String emetteur = "Gouvernement";
        final String destinataire = "Assemblée nationale";
        final String copie = "Sénat";
        
        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT12");
        // Chargement de la page de détail de l'évènement envoyé dans le test 37 par le sénat
        final DetailComm12Page detailPage12 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm12Page.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm23QuaterPage createPage23Quater = detailPage12.navigateToCreateCommSucc(EvenementType.EVT_23_QUATER, CreateComm23QuaterPage.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());        
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm23QuaterPage detailPage23Quater = createPage23Quater.createComm23Quater(COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage23Quater.checkValue(DetailComm23QuaterPage.COMMUNICATION, typeComm);
        detailPage23Quater.checkValue(DetailComm23QuaterPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00037");
        detailPage23Quater.checkValue(DetailComm23QuaterPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00036");
        detailPage23Quater.checkValue(DetailComm23QuaterPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage23Quater.checkValue(DetailComm23QuaterPage.EMETTEUR, emetteur);
        detailPage23Quater.checkValue(DetailComm23QuaterPage.DESTINATAIRE, destinataire);
        detailPage23Quater.checkValue(DetailComm23QuaterPage.COPIE, copie);
        detailPage23Quater.checkValueStartWith(DetailComm23QuaterPage.HORODATAGE, horodatage);
        detailPage23Quater.checkValue(DetailComm23QuaterPage.OBJET, OBJET);
        detailPage23Quater.checkValue(DetailComm23QuaterPage.INTITULE, INTITULE);
        detailPage23Quater.checkValue(DetailComm23QuaterPage.COMMENTAIRE, COMMENTAIRE);
        detailPage23Quater.checkValue(DetailComm23QuaterPage.NIVEAU_LECTURE, "Lecture définitive");
        detailPage23Quater.checkValue(DetailComm23QuaterPage.DATE_DEMANDE, horodatage);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°39 
     * Créé une communication successive LEX-30 à partir de la communication du test n°38 
     *
     */
    @WebTest(description = "Creation Comm S_23Quinquies (LEX-30) an - Lecture définitive AN")
    @TestDocumentation(categories = {"Comm. LEX-30", "Recherche rapide", "Comm. LEX-29", "Lecture définitive"})
    public void testCase039() {
        final String typeComm = "LEX-30 : Enregistrement de la demande de lecture définitive";
        final String dateDepot = "14/03/2012";
        final String numeroDepot = "4546-7647";
        final String commFond = "org";
        final String commAvis = "org";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";
        
        doLoginAs("an", "an");
        doRapidSearch("Séance réception", "EVT23QUATER");
        // Chargement de la page de détail de l'évènement envoyé dans le test 38 par le gouvernement
        final DetailComm23QuaterPage detailPage23Quater = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm23QuaterPage.class);
        // Initialisation de la page de création de l'évènement successif 12
        CreateComm23QuinquiesPage createPage23Quinquies = detailPage23Quater.navigateToCreateCommSucc(EvenementType.EVT_23_QUINQUIES, CreateComm23QuinquiesPage.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());        
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm23QuinquiesPage detailPage23Quinquies = createPage23Quinquies.createComm23Quinquies(dateDepot, numeroDepot,
                commFond, commAvis, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.COMMUNICATION, typeComm);
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00038");
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00037");
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.EMETTEUR, emetteur);
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.DESTINATAIRE, destinataire);
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.COPIE, copie);
        detailPage23Quinquies.checkValueStartWith(DetailComm23QuinquiesPage.HORODATAGE, horodatage);
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.OBJET, OBJET);
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.INTITULE, INTITULE);
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.COMMENTAIRE, COMMENTAIRE);
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.NIVEAU_LECTURE, "Lecture définitive");
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.COMMISSION_SAISIE_AVIS, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.COMMISSION_SAISIE_FOND, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.DATE_DEPOT_TEXTE, dateDepot);
        detailPage23Quinquies.checkValue(DetailComm23QuinquiesPage.NUMERO_DEPOT_TEXTE, numeroDepot);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°40 
     * Créé une communication successive LEX-04 à partir de la communication du test n°39
     *
     */
    @WebTest(description = "Creation Comm S_04bis (LEX-04) an - Lecture définitive AN")
    @TestDocumentation(categories = {"Comm. LEX-04", "Recherche rapide", "Comm. LEX-30", "Lecture définitive"})
    public void testCase040() {
        final String typeComm = "LEX-04 : Dépôt de rapport, avis et texte de commission";
        final String versionTitre = "titre";
        final String dateDepotTexte = "14/03/2012";
        final String natureRapport = "Avis";
        final String numeroRapport = "4546-1234";
        final String numeroTexte = "4546-7647";
        final String dateDistribution = "14/03/2012";
        final String rapporteur = "ass";
        final String commission = "org";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";
        
        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT23QUINQUIES");        
        // Chargement de la page de détail de l'évènement envoyé dans le test 39 par l'assemblée nationale
        final DetailComm23QuinquiesPage detailPage23Quinquies = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm23QuinquiesPage.class);
        // Initialisation de la page de création de l'évènement successif 04Bis
        CreateComm04BisPage createPage04Bis = detailPage23Quinquies.navigateToCreateCommSucc(EvenementType.EVT_04_BIS, CreateComm04BisPage.class);
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm04BisPage detailPage04Bis = createPage04Bis.createComm04Bis(dateDistribution, natureRapport, versionTitre, 
                numeroRapport, dateDepotTexte, numeroTexte, rapporteur, commission, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMUNICATION, typeComm);
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00039");
        detailPage04Bis.checkValue(DetailComm04BisPage.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00038");
        detailPage04Bis.checkValue(DetailComm04BisPage.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage04Bis.checkValue(DetailComm04BisPage.EMETTEUR, emetteur);
        detailPage04Bis.checkValue(DetailComm04BisPage.DESTINATAIRE, destinataire);
        detailPage04Bis.checkValue(DetailComm04BisPage.COPIE, copie);
        detailPage04Bis.checkValueStartWith(DetailComm04BisPage.HORODATAGE, horodatage);
        detailPage04Bis.checkValue(DetailComm04BisPage.OBJET, OBJET);
        detailPage04Bis.checkValue(DetailComm04BisPage.INTITULE, INTITULE);
        detailPage04Bis.checkValue(DetailComm04BisPage.VERSION_TITRE, versionTitre);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMENTAIRE, COMMENTAIRE);
        detailPage04Bis.checkValue(DetailComm04BisPage.NIVEAU_LECTURE, "Lecture définitive");
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DISTRIBUTION_ELECTRONIQUE, dateDistribution);
        detailPage04Bis.checkValue(DetailComm04BisPage.NATURE_RAPPORT, natureRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.DATE_DEPOT_TEXTE, dateDepotTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_RAPPORT, numeroRapport);
        detailPage04Bis.checkValue(DetailComm04BisPage.NUMERO_DEPOT_TEXTE, numeroTexte);
        detailPage04Bis.checkValue(DetailComm04BisPage.COMMISSION_SAISIE, "Organisme Name (du 13/11/2010 au 13/11/2020)");
        detailPage04Bis.checkValue(DetailComm04BisPage.RAPPORTEURS_LIST, "M. Assemblée Nationale");        

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°41 
     * Créé une communication successive LEX-31 à partir de la communication du test n°40
     *
     */
    @WebTest(description = "Creation Comm S_24 (LEX-31) an - Lecture définitive AN")
    @TestDocumentation(categories = {"Comm. LEX-31", "Recherche rapide", "Comm. LEX-30", "Lecture définitive"})
    public void testCase041() {
        final String typeComm = "LEX-31 : Adoption définitive / Adoption ou rejet en Congrès";
        final String dateAdoption = "14/03/2012";
        final String sortAdoption = "Adopté";
        final String numeroTexte = "2012-8945";
        final String emetteur = "Assemblée nationale";
        final String destinataire = "Gouvernement";
        final String copie = "Sénat";
        
        doLoginAs("an", "an");
        doRapidSearch("Séance envoi", "EVT23QUINQUIES");        
        // Chargement de la page de détail de l'évènement envoyé dans le test 40 par l'assemblée nationale
        final DetailComm23QuinquiesPage detailPage23Quinquies = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm23QuinquiesPage.class);
        // Initialisation de la page de création de l'évènement successif 24
        CreateComm24Page createPage24 = detailPage23Quinquies.navigateToCreateCommSucc(EvenementType.EVT_24, CreateComm24Page.class);
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm24Page detailPage24 = createPage24.createComm24(emetteur, numeroTexte, dateAdoption, sortAdoption, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage24.checkValue(DetailComm24Page.COMMUNICATION, typeComm);
        detailPage24.checkValue(DetailComm24Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00040");
        detailPage24.checkValue(DetailComm24Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00038");
        detailPage24.checkValue(DetailComm24Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage24.checkValue(DetailComm24Page.EMETTEUR, emetteur);
        detailPage24.checkValue(DetailComm24Page.DESTINATAIRE, destinataire);
        detailPage24.checkValue(DetailComm24Page.COPIE, copie);
        detailPage24.checkValueStartWith(DetailComm24Page.HORODATAGE, horodatage);
        detailPage24.checkValue(DetailComm24Page.OBJET, OBJET);
        detailPage24.checkValue(DetailComm24Page.INTITULE, INTITULE);
        detailPage24.checkValue(DetailComm24Page.COMMENTAIRE, COMMENTAIRE);
        detailPage24.checkValue(DetailComm24Page.NIVEAU_LECTURE, "Lecture définitive");
        detailPage24.checkValue(DetailComm24Page.DATE_ADOPTION, dateAdoption);
        detailPage24.checkValue(DetailComm24Page.NUMERO_TEXTE_ADOPTE, numeroTexte);
        detailPage24.checkValue(DetailComm24Page.SORT_ADOPTION, sortAdoption);

        doLogout();
    }
    
    /**
     * Test suite EPP dossier_1240 - test n°42 
     * Créé une communication successive LEX-35 à partir de la communication du test n°41 
     *
     */
    @WebTest(description = "Creation Comm S_28 (LEX-35) adminsgg - Lecture définitive AN")
    @TestDocumentation(categories = {"Comm. LEX-35", "Recherche rapide", "Comm. LEX-31", "Publication"})
    public void testCase042() {
        final String typeComm = "LEX-35 : Publication";
        final String nor = "NOR00256";
        final String titre = "titre";
        final String datePromulgation = "14/03/2012";
        final String datePublication = "14/03/2012";
        final String numeroLoi = "56987456";
        final String numeroJO = "96 587 412";
        final String pageJO = "659";
        final String emetteur = "Gouvernement";
        final String destinataire = "Assemblée nationale";
        
        doLoginAs("adminsgg", "adminsgg");
        doRapidSearch("Reçu", "EVT24");
        // Chargement de la page de détail de l'évènement envoyé dans le test 41 par l'assemblée nationale
        final DetailComm24Page detailPage24 = corbeillePage.navigateToDetailCommByLinkNor(NUM_NOR, DetailComm24Page.class);
        // Initialisation de la page de création de l'évènement successif 28
        CreateComm28Page createPage28 = detailPage24.navigateToCreateCommSucc(EvenementType.EVT_28, CreateComm28Page.class);        
        
        final String horodatage = DateUtils.format(Calendar.getInstance());        
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm28Page detailPage28 = createPage28.createComm28(destinataire, nor, titre, datePromulgation, datePublication, 
                numeroLoi, numeroJO.replace(" ", ""), pageJO, COMMENTAIRE);

        // Vérification sur la page de détail de l'évènement du bon remplissage des champs
        detailPage28.checkValue(DetailComm28Page.COMMUNICATION, typeComm);
        detailPage28.checkValue(DetailComm28Page.IDENTIFIANT_COMMUNICATION, NUM_NOR + "_00041");
        detailPage28.checkValue(DetailComm28Page.ID_COMMUNICATION_PRECEDENTE, NUM_NOR + "_00040");
        detailPage28.checkValue(DetailComm28Page.IDENTIFIANT_DOSSIER, NUM_NOR);
        detailPage28.checkValue(DetailComm28Page.EMETTEUR, emetteur);
        detailPage28.checkValue(DetailComm28Page.DESTINATAIRE, destinataire);
        detailPage28.checkValueStartWith(DetailComm28Page.HORODATAGE, horodatage);
        detailPage28.checkValue(DetailComm28Page.OBJET, OBJET);
        detailPage28.checkValue(DetailComm28Page.NOR, nor);
        detailPage28.checkValue(DetailComm28Page.TITRE, titre);
        detailPage28.checkValue(DetailComm28Page.DATE_PROMULGATION, datePromulgation);
        detailPage28.checkValue(DetailComm28Page.DATE_PUBLICATION, datePublication);
        detailPage28.checkValue(DetailComm28Page.NUMERO_LOI, numeroLoi);
        detailPage28.checkValue(DetailComm28Page.NUMERO_JO, numeroJO);
        detailPage28.checkValue(DetailComm28Page.PAGE_JO, pageJO);
        detailPage28.checkValue(DetailComm28Page.COMMENTAIRE, COMMENTAIRE);

        doLogout();
    }
}
