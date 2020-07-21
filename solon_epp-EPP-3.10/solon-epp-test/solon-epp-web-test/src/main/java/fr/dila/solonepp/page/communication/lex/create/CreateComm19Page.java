package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm19Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 22 (EVT19) : CMP - Demande de réunion par le Gouvernement
 * 
 * @author mbd
 * 
 */
public class CreateComm19Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-22 : CMP - Demande de réunion par le Gouvernement";
    public static final String DATE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateInputDate";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    public void setDate(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_INPUT));
        fillField("Date", elem, date);
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm19Page.class;
    }

    public DetailComm19Page createComm19(String destinataire, String commentaire) {
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValue(COMMUNICATION, TYPE_COMM);
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(EMETTEUR, "Gouvernement");
        checkValue(DESTINATAIRE, null);
        checkValue(COPIE, null);

        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);
        setDestinataire(destinataire);
//        checkValue(DESTINATAIRE, destinataire);
//        if (destinataire.equals("Sénat")) {
//            checkValue(COPIE, "Assemblée nationale");
//        } else {
//            checkValue(COPIE, "Sénat");
//        }

        // Auto remplissage du champ date de la communication - On vérifie simplement qu'il s'agit bien de la date du jour
        final String date = DateUtils.format(Calendar.getInstance());
        checkValue(DATE_INPUT, date);

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.LETTRE_PM_VERS_AN, URL, PJ);
        addPieceJointe(PieceJointeType.LETTRE_PM_VERS_SENAT, URL, PJ);

        return publier();
    }

}
