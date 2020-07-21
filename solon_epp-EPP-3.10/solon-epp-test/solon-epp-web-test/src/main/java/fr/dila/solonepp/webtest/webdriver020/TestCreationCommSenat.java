package fr.dila.solonepp.webtest.webdriver020;

import java.util.Calendar;

import org.junit.Assert;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.solonepp.constantes.EppTestConstantes;
import fr.dila.solonepp.page.communication.lex.create.CreateComm02Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm01Page;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm02Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.dila.solonepp.webtest.common.AbstractEppWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.NiveauLectureCode;

/**
 * Création d'une communication du Sénat
 * @author jgomez
 *
 */
public class TestCreationCommSenat extends AbstractEppWebTest {

	public static final String ID_DOSSIER = "CCO8956KGF";
	public static final String ID_SENAT = "CCO598555SEN";
	public static final String OBJET = "Mon objet";
	public static final String INTITULE = "Mon intitulé";
   
	/**
     * Test suite EPP - création communication gouvernementale - reprise d'un test selenium
     * 
     * Crée une communication CCO598555SEN - nor : CCO598555SEN
     * 
     */
    @WebTest(description = "Creation Comm Sénat (LEX-02) senat")
    @TestDocumentation(categories = {"Comm. LEX-02"})
    public void testCommSenat() {
        final String emetteur = "Sénat";
        final String destinataire = "Gouvernement";
        final String copie = "Assemblée nationale";
        final String auteur = "Sén";
        final String coauteur = "Sén";
        final String numeroDepotTexte = "123456789";
        final Integer niveauLecture = 1;
        final NiveauLectureCode organisme = NiveauLectureCode.AN;
        
        doLoginAs(EppTestConstantes.SENAT_LOGIN, EppTestConstantes.SENAT_LOGIN);

        // Ouverture de la page de création de l'évènement 01
        final CreateComm02Page createComm02Page = corbeillePage.navigateToCreateComm(EvenementType.EVT_02, CreateComm02Page.class);
        getFlog().startCheck("Vérification des champs présent");
        try{
        	createComm02Page.verifierPresenceChamps(true);
        } catch (ClientException e){
        	getFlog().checkFailed(e.getMessage());
        	Assert.fail();
        }
    	getFlog().endCheck();
        
        final String horodatage = DateUtils.format(Calendar.getInstance());
        // Remplissage des champs de la page de création et publication de la communication
        DetailComm02Page detailPage = createComm02Page.createComm02(emetteur, destinataire, OBJET, ID_DOSSIER, auteur, coauteur, niveauLecture, organisme, true, copie, ID_SENAT, numeroDepotTexte, horodatage, "commentaire", "san", "san", true);
        detailPage.checkValue(DetailComm01Page.IDENTIFIANT_DOSSIER, ID_DOSSIER);
        detailPage.checkValue(DetailComm01Page.EMETTEUR, emetteur);
        detailPage.checkValue(DetailComm01Page.DESTINATAIRE, destinataire);
        detailPage.checkValue(DetailComm01Page.COPIE, copie);
        doLogout();
    }
}