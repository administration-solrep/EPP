package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm23BisPage;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 27 (EVT23Bis) : Demande de nouvelle lecture
 * 
 * @author mbd
 * 
 */
public class CreateComm23BisPage extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-27 : Demande de nouvelle lecture";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm23BisPage.class;
    }

    public DetailComm23BisPage createComm23Bis(String destinataire, Integer niveauLecture, NiveauLectureCode organisme, String commentaire) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(EMETTEUR, "Gouvernement");
        
        setDestinataire(destinataire);
//        checkValue(DESTINATAIRE, destinataire);
//
//        if (destinataire.equals("Sénat")) {
//            checkValue(COPIE, "Assemblée nationale");
//        } else {
//            checkValue(COPIE, "Sénat");
//        }
        
        setNiveauLecture(niveauLecture, organisme);
        if (niveauLecture != null) {
            checkValue(NIVEAU_LECTURE_INPUT, niveauLecture.toString());
        }
        
        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);
        addPieceJointe(PieceJointeType.LETTRE_PM, URL, PJ);

        return publier();
    }

}
