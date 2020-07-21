package fr.dila.solonepp.webtest.webdriver020;

import java.util.Calendar;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.communication.lex.create.CreateComm01Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm01Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.NiveauLectureCode;

/**
 * Création d'une communication du Sénat - Reprise d'un test sélénium
 * @author jgomez
 *
 */
public class TestCreationCommGouv extends AbstractEppWebTest {

	public static final String NUM_NOR = "AGRE000001";
	public static final String OBJET = "Mon objet";
	public static final String INTITULE = "Mon intitulé";
   
	/**
     * Test suite EPP - création communication gouvernementale - reprise d'un test selenium
     * 
     * Crée une communication LEX-01 - nor : AGRE000001
     * 
     */
    @WebTest(description = "Creation Comm Gouv (LEX-01) adminsgg")
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
        
        doLoginAs(EppTestConstantes.ADMIN_FONCTIONNEL_LOGIN, EppTestConstantes.ADMIN_FONCTIONNEL_PASSWORD);

        // Ouverture de la page de création de l'évènement 01
        final CreateComm01Page createComm01Page = corbeillePage.navigateToCreateComm(EvenementType.EVT_01, CreateComm01Page.class);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        final DetailComm01Page detailPage = createComm01Page.createComm01(emetteur, destinataire, OBJET, NUM_NOR, auteur, coauteur, niveauLecture, organisme, true);

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
        detailPage.checkValue(DetailComm01Page.NIVEAU_LECTURE, "1 - Assemblée nationale");

        doLogout();
    }
}
