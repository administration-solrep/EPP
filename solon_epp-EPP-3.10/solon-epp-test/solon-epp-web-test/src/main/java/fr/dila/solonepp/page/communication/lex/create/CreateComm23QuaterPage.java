package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm23QuaterPage;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 29 (EVT23Quater) : Demande de lecture définitive à l'Assemblée nationale
 * 
 * @author mbd
 * 
 */
public class CreateComm23QuaterPage extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-29 : Demande de lecture définitive à l'Assemblée nationale";
    public static final String DATE_DEMANDE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateInputDate";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm23QuaterPage.class;
    }
    
    public void setDate(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DEMANDE_INPUT));
        fillField("Date", elem, date);
    }

    public DetailComm23QuaterPage createComm23Quater(String commentaire) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(EMETTEUR, "Gouvernement");
        setDestinataire("Assemblée nationale");
//        checkValue(DESTINATAIRE, "Assemblée nationale");
//        checkValue(COPIE, "Sénat");
        
        // Auto remplissage du champ date de la communication - On vérifie simplement qu'il s'agit bien de la date du jour
        final String date = DateUtils.format(Calendar.getInstance());
        checkValue(DATE_DEMANDE_INPUT, date);
        
        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.LETTRE_PM, URL, PJ);
        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);

        return publier();
    }

}
